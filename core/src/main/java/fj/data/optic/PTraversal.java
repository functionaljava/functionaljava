package fj.data.optic;

import fj.F;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.Function;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.Semigroup;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/**
 * A {@link PTraversal} can be seen as a {@link POptional} generalised to 0 to n targets where n can be infinite.
 *
 * {@link PTraversal} stands for Polymorphic Traversal as it set and modify methods change a type `A` to `B` and `S` to `T`.
 * {@link Traversal} is a {@link PTraversal} restricted to monomoprhic updates.
 *
 * @param <S> the source of a {@link PTraversal}
 * @param <T> the modified source of a {@link PTraversal}
 * @param <A> the target of a {@link PTraversal}
 * @param <B> the modified target of a {@link PTraversal}
 */
public abstract class PTraversal<S, T, A, B> {

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract <C> F<S, F<C, T>> modifyFunctionF(F<A, F<C, B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract <L> F<S, Either<L, T>> modifyEitherF(F<A, Either<L, B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, IO<T>> modifyIOF(F<A, IO<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, Trampoline<T>> modifyTrampolineF(F<A, Trampoline<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, Promise<T>> modifyPromiseF(F<A, Promise<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, List<T>> modifyListF(F<A, List<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, Option<T>> modifyOptionF(F<A, Option<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, Stream<T>> modifyStreamF(F<A, Stream<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, P1<T>> modifyP1F(F<A, P1<B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> s, F<A, Validation<E, B>> f);

  /**
   * modify polymorphically the target of a {@link PTraversal} with an Applicative function
   */
  public abstract F<S, V2<T>> modifyV2F(F<A, V2<B>> f);

  /** map each target to a {@link Monoid} and combine the results */
  public abstract <M> F<S, M> foldMap(Monoid<M> monoid, F<A, M> f);

  /** combine all targets using a target's {@link Monoid} */
  public final F<S, A> fold(final Monoid<A> m) {
    return foldMap(m, Function.identity());
  }

  /** get all the targets of a {@link PTraversal} */
  public final List<A> getAll(final S s) {
    return foldMap(Monoid.listMonoid(), List::single).f(s);
  }

  /** find the first target of a {@link PTraversal} matching the predicate */
  public final F<S, Option<A>> find(final F<A, Boolean> p) {
    return foldMap(Monoid.firstOptionMonoid(), a -> p.f(a) ? Option.some(a) : Option.none());
  }

  /** get the first target of a {@link PTraversal} */
  public final Option<A> headOption(final S s) {
    return find(Function.constant(Boolean.TRUE)).f(s);
  }

  /** check if at least one target satisfies the predicate */
  public final F<S, Boolean> exist(final F<A, Boolean> p) {
    return foldMap(Monoid.disjunctionMonoid, p);
  }

  /** check if all targets satisfy the predicate */
  public final F<S, Boolean> all(final F<A, Boolean> p) {
    return foldMap(Monoid.conjunctionMonoid, p);
  }

  /** modify polymorphically the target of a {@link PTraversal} with a function */
  public final F<S, T> modify(final F<A, B> f) {
    return s -> this.modifyP1F(a -> P.p(f.f(a))).f(s)._1();
  }

  /** set polymorphically the target of a {@link PTraversal} with a value */
  public final F<S, T> set(final B b) {
    return modify(Function.constant(b));
  }

  /** join two {@link PTraversal} with the same target */
  public final <S1, T1> PTraversal<Either<S, S1>, Either<T, T1>, A, B> sum(final PTraversal<S1, T1, A, B> other) {
    final PTraversal<S, T, A, B> self = this;
    return new PTraversal<Either<S, S1>, Either<T, T1>, A, B>() {

      @Override
      public <C> F<Either<S, S1>, F<C, Either<T, T1>>> modifyFunctionF(final F<A, F<C, B>> f) {
        return ss1 -> ss1.either(
            s -> Function.compose(Either.left_(), self.modifyFunctionF(f).f(s)),
            s1 -> Function.compose(Either.right_(), other.modifyFunctionF(f).f(s1))
            );
      }

      @Override
      public <L> F<Either<S, S1>, Either<L, Either<T, T1>>> modifyEitherF(final F<A, Either<L, B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyEitherF(f).f(s).right().map(Either.left_()),
            s1 -> other.modifyEitherF(f).f(s1).right().map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, IO<Either<T, T1>>> modifyIOF(final F<A, IO<B>> f) {
        return ss1 -> ss1.either(
            s -> IOFunctions.map(self.modifyIOF(f).f(s), Either.left_()),
            s1 -> IOFunctions.map(other.modifyIOF(f).f(s1), Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Trampoline<Either<T, T1>>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyTrampolineF(f).f(s).map(Either.left_()),
            s1 -> other.modifyTrampolineF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Promise<Either<T, T1>>> modifyPromiseF(final F<A, Promise<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyPromiseF(f).f(s).fmap(Either.left_()),
            s1 -> other.modifyPromiseF(f).f(s1).fmap(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, List<Either<T, T1>>> modifyListF(final F<A, List<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyListF(f).f(s).map(Either.left_()),
            s1 -> other.modifyListF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Option<Either<T, T1>>> modifyOptionF(final F<A, Option<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyOptionF(f).f(s).map(Either.left_()),
            s1 -> other.modifyOptionF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Stream<Either<T, T1>>> modifyStreamF(final F<A, Stream<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyStreamF(f).f(s).map(Either.left_()),
            s1 -> other.modifyStreamF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, P1<Either<T, T1>>> modifyP1F(final F<A, P1<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyP1F(f).f(s).map(Either.left_()),
            s1 -> other.modifyP1F(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, V2<Either<T, T1>>> modifyV2F(final F<A, V2<B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyV2F(f).f(s).map(Either.left_()),
            s1 -> other.modifyV2F(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public <E> F<Either<S, S1>, Validation<E, Either<T, T1>>> modifyValidationF(Semigroup<E> se, final F<A, Validation<E, B>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyValidationF(se, f).f(s).map(Either.left_()),
            s1 -> other.modifyValidationF(se, f).f(s1).map(Either.right_())
            );
      }

      @Override
      public <M> F<Either<S, S1>, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return ss1 -> ss1.either(
            self.foldMap(monoid, f),
            other.foldMap(monoid, f)
            );
      }

    };
  }

  /****************************************************************/
  /** Compose methods between a {@link PTraversal} and another Optics */
  /****************************************************************/

  /** compose a {@link PTraversal} with a {@link Fold} */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  //
  /** compose a {@link PTraversal} with a {@link Getter} */
  public final <C> Fold<S, C> composeFold(final Getter<A, C> other) {
    return asFold().composeGetter(other);
  }

  /** compose a {@link PTraversal} with a {@link PSetter} */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link PTraversal} with a {@link PTraversal} */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    final PTraversal<S, T, A, B> self = this;
    return new PTraversal<S, T, C, D>() {

      @Override
      public <G> F<S, F<G, T>> modifyFunctionF(final F<C, F<G, D>> f) {
        return self.modifyFunctionF(other.modifyFunctionF(f));
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<C, Either<L, D>> f) {
        return self.modifyEitherF(other.modifyEitherF(f));
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<C, IO<D>> f) {
        return self.modifyIOF(other.modifyIOF(f));
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<C, Trampoline<D>> f) {
        return self.modifyTrampolineF(other.modifyTrampolineF(f));
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<C, Promise<D>> f) {
        return self.modifyPromiseF(other.modifyPromiseF(f));
      }

      @Override
      public F<S, List<T>> modifyListF(final F<C, List<D>> f) {
        return self.modifyListF(other.modifyListF(f));
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<C, Option<D>> f) {
        return self.modifyOptionF(other.modifyOptionF(f));
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<C, Stream<D>> f) {
        return self.modifyStreamF(other.modifyStreamF(f));
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<C, P1<D>> f) {
        return self.modifyP1F(other.modifyP1F(f));
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> s, final F<C, Validation<E, D>> f) {
        return self.modifyValidationF(s, other.modifyValidationF(s, f));
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<C, V2<D>> f) {
        return self.modifyV2F(other.modifyV2F(f));
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<C, M> f) {
        return self.foldMap(monoid, other.foldMap(monoid, f));
      }
    };
  }

  /** compose a {@link PTraversal} with a {@link POptional} */
  public final <C, D> PTraversal<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /** compose a {@link PTraversal} with a {@link PPrism} */
  public final <C, D> PTraversal<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /** compose a {@link PTraversal} with a {@link PLens} */
  public final <C, D> PTraversal<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /** compose a {@link PTraversal} with a {@link PIso} */
  public final <C, D> PTraversal<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composeTraversal(other.asTraversal());
  }

  /**********************************************************************/
  /** Transformation methods to view a {@link PTraversal} as another Optics */
  /**********************************************************************/

  /** view a {@link PTraversal} as a {@link Fold} */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return PTraversal.this.foldMap(monoid, f);
      }
    };
  }

  /** view a {@link PTraversal} as a {@link PSetter} */
  public PSetter<S, T, A, B> asSetter() {
    return PSetter.pSetter(this::modify);
  }

  public static <S, T> PTraversal<S, T, S, T> pId() {
    return PIso.<S, T> pId().asTraversal();
  }

  public static <S, T> PTraversal<Either<S, S>, Either<T, T>, S, T> pCodiagonal() {
    return new PTraversal<Either<S, S>, Either<T, T>, S, T>() {

      @Override
      public <C> F<Either<S, S>, F<C, Either<T, T>>> modifyFunctionF(final F<S, F<C, T>> f) {
        return s -> s.bimap(f, f).either(
            f1 -> Function.compose(Either.left_(), f1),
            f1 -> Function.compose(Either.right_(), f1)
            );
      }

      @Override
      public <L> F<Either<S, S>, Either<L, Either<T, T>>> modifyEitherF(final F<S, Either<L, T>> f) {
        return s -> s.bimap(f, f).either(
            e -> e.right().map(Either.left_()),
            e -> e.right().map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, IO<Either<T, T>>> modifyIOF(final F<S, IO<T>> f) {
        return s -> s.bimap(f, f).either(
            io -> IOFunctions.map(io, Either.left_()),
            io -> IOFunctions.map(io, Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Trampoline<Either<T, T>>> modifyTrampolineF(final F<S, Trampoline<T>> f) {
        return s -> s.bimap(f, f).either(
            t -> t.map(Either.left_()),
            t -> t.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Promise<Either<T, T>>> modifyPromiseF(final F<S, Promise<T>> f) {
        return s -> s.bimap(f, f).either(
            p -> p.fmap(Either.left_()),
            p -> p.fmap(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, List<Either<T, T>>> modifyListF(final F<S, List<T>> f) {
        return s -> s.bimap(f, f).either(
            l -> l.map(Either.left_()),
            l -> l.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Option<Either<T, T>>> modifyOptionF(final F<S, Option<T>> f) {
        return s -> s.bimap(f, f).either(
            o -> o.map(Either.left_()),
            o -> o.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Stream<Either<T, T>>> modifyStreamF(final F<S, Stream<T>> f) {
        return s -> s.bimap(f, f).either(
            stream -> stream.map(Either.left_()),
            stream -> stream.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, P1<Either<T, T>>> modifyP1F(final F<S, P1<T>> f) {
        return s -> s.bimap(f, f).either(
            p1 -> p1.map(Either.left_()),
            p1 -> p1.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, V2<Either<T, T>>> modifyV2F(final F<S, V2<T>> f) {
        return s -> s.bimap(f, f).either(
            v2 -> v2.map(Either.left_()),
            v2 -> v2.map(Either.right_())
            );
      }

      @Override
      public <E> F<Either<S, S>, Validation<E, Either<T, T>>> modifyValidationF(Semigroup<E> se, final F<S, Validation<E, T>> f) {
        return s -> s.bimap(f, f).either(
            v -> v.map(Either.left_()),
            v -> v.map(Either.right_())
            );
      }

      @Override
      public <M> F<Either<S, S>, M> foldMap(final Monoid<M> monoid, final F<S, M> f) {
        return s -> s.either(f, f);
      }
    };
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final F<S, A> get1, final F<S, A> get2,
      final F3<B, B, S, T> set) {
    return new PTraversal<S, T, A, B>() {

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return s -> Function.apply(Function.compose(b1 -> b2 -> set.f(b1, b2, s), f.f(get1.f(s))), f.f(get2.f(s)));
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return s -> f.f(get2.f(s)).right().apply(f.f(get1.f(s)).right().<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return s -> IOFunctions.apply(f.f(get2.f(s)),
            IOFunctions.<B, F<B, T>> map(f.f(get1.f(s)), b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> fmap(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return s -> f.f(get2.f(s)).apply(f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> se, final F<A, Validation<E, B>> f) {
        return s -> f.f(get2.f(s)).accumapply(se, f.f(get1.f(s)).<F<B, T>> map(b1 -> b2 -> set.f(b1, b2, s)));
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> monoid.sum(f.f(get1.f(s)), f.f(get2.f(s)));
      }
    };
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F4<B, B, B, S, T> set) {
    return fromCurried(pTraversal(get1, get2, (b1, b2, s) -> b3 -> set.f(b1, b2, b3, s)), get3);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4,
      final F5<B, B, B, B, S, T> set) {
    return fromCurried(pTraversal(get1, get2, get3, (b1, b2, b3, s) -> b4 -> set.f(b1, b2, b3, b4, s)), get4);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5,
      final F6<B, B, B, B, B, S, T> set) {
    return fromCurried(pTraversal(get1, get2, get3, get4, (b1, b2, b3, b4, s) -> b5 -> set.f(b1, b2, b3, b4, b5, s)), get5);
  }

  public static <S, T, A, B> PTraversal<S, T, A, B> pTraversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5, final F<S, A> get6,
      final F7<B, B, B, B, B, B, S, T> set) {
    return fromCurried(
        pTraversal(get1, get2, get3, get4, get5, (b1, b2, b3, b4, b5, s) -> b6 -> set.f(b1, b2, b3, b4, b5, b6, s)),
        get6);
  }

  private static <S, T, A, B> PTraversal<S, T, A, B> fromCurried(final PTraversal<S, F<B, T>, A, B> curriedTraversal,
      final F<S, A> lastGet) {
    return new PTraversal<S, T, A, B>() {

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return s -> Function.apply(curriedTraversal.modifyFunctionF(f).f(s), f.f(lastGet.f(s)));
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return s -> f.f(lastGet.f(s)).right().apply(curriedTraversal.modifyEitherF(f).f(s));
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return s -> IOFunctions.apply(f.f(lastGet.f(s)), curriedTraversal.modifyIOF(f).f(s));
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyTrampolineF(f).f(s));
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyPromiseF(f).f(s));
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyListF(f).f(s));
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyOptionF(f).f(s));
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyStreamF(f).f(s));
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyP1F(f).f(s));
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return s -> f.f(lastGet.f(s)).apply(curriedTraversal.modifyV2F(f).f(s));
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> se, final F<A, Validation<E, B>> f) {
        return s -> f.f(lastGet.f(s)).accumapply(se, curriedTraversal.modifyValidationF(se, f).f(s));
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> monoid.sum(curriedTraversal.foldMap(monoid, f).f(s), f.f(lastGet.f(s)));
      }
    };
  }
}
