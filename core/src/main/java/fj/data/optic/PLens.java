package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
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
 * A {@link PLens} can be seen as a pair of functions: - `get: S => A` i.e. from an `S`, we can extract an `A` - `set: (B, S) =>
 * T` i.e. if we replace an `A` by a `B` in an `S`, we obtain a `T`
 *
 * A {@link PLens} could also be defined as a weaker {@link PIso} where set requires an additional parameter than reverseGet.
 *
 * {@link PLens} stands for Polymorphic Lens as it set and modify methods change a type `A` to `B` and `S` to `T`. {@link Lens}
 * is a {@link PLens} restricted to monomoprhic updates.
 *
 * A {@link PLens} is also a valid {@link Getter}, {@link Fold}, {@link POptional}, {@link PTraversal} and {@link PSetter}
 *
 * Typically a {@link PLens} or {@link Lens} can be defined between a Product (e.g. case class, tuple, HList) and one of it is
 * component.
 *
 * @param <S> the source of a {@link PLens}
 * @param <T> the modified source of a {@link PLens}
 * @param <A> the target of a {@link PLens}
 * @param <B> the modified target of a {@link PLens}
 */
public abstract class PLens<S, T, A, B> {

  PLens() {
    super();
  }

  /** get the target of a {@link PLens} */
  public abstract A get(S s);

  /** set polymorphically the target of a {@link PLens} using a function */
  public abstract F<S, T> set(B b);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract <C> F<S, F<C, T>> modifyFunctionF(F<A, F<C, B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract <L> F<S, Either<L, T>> modifyEitherF(F<A, Either<L, B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, IO<T>> modifyIOF(F<A, IO<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, Trampoline<T>> modifyTrampolineF(F<A, Trampoline<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, Promise<T>> modifyPromiseF(F<A, Promise<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, List<T>> modifyListF(F<A, List<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, Option<T>> modifyOptionF(F<A, Option<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, Stream<T>> modifyStreamF(F<A, Stream<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, P1<T>> modifyP1F(F<A, P1<B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract <E> F<S, Validation<E, T>> modifyValidationF(F<A, Validation<E, B>> f);

  /**
   * modify polymorphically the target of a {@link PLens} with an Applicative function
   */
  public abstract F<S, V2<T>> modifyV2F(F<A, V2<B>> f);

  /** modify polymorphically the target of a {@link PLens} using a function */
  public abstract F<S, T> modify(final F<A, B> f);

  /** join two {@link PLens} with the same target */
  public final <S1, T1> PLens<Either<S, S1>, Either<T, T1>, A, B> sum(final PLens<S1, T1, A, B> other) {
    return pLens(
        e -> e.either(this::get, other::get),
        b -> e -> e.bimap(PLens.this.set(b), other.set(b)));
  }

  /***********************************************************/
  /** Compose methods between a {@link PLens} and another Optics */
  /***********************************************************/

  /** compose a {@link PLens} with a {@link Fold} */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /** compose a {@link PLens} with a {@link Getter} */
  public final <C> Getter<S, C> composeGetter(final Getter<A, C> other) {
    return asGetter().composeGetter(other);
  }

  /**
   * compose a {@link PLens} with a {@link PSetter}
   */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /**
   * compose a {@link PLens} with a {@link PTraversal}
   */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link PLens} with an {@link POptional} */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return asOptional().composeOptional(other);
  }

  /** compose a {@link PLens} with a {@link PPrism} */
  public final <C, D> POptional<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return asOptional().composeOptional(other.asOptional());
  }

  /** compose a {@link PLens} with a {@link PLens} */
  public final <C, D> PLens<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    final PLens<S, T, A, B> self = this;
    return new PLens<S, T, C, D>() {
      @Override
      public C get(final S s) {
        return other.get(self.get(s));
      }

      @Override
      public F<S, T> set(final D d) {
        return self.modify(other.set(d));
      }

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
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<C, Validation<E, D>> f) {
        return self.modifyValidationF(other.modifyValidationF(f));
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<C, V2<D>> f) {
        return self.modifyV2F(other.modifyV2F(f));
      }

      @Override
      public F<S, T> modify(final F<C, D> f) {
        return self.modify(other.modify(f));
      }
    };
  }

  /** compose a {@link PLens} with an {@link PIso} */
  public final <C, D> PLens<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composeLens(other.asLens());
  }

  /************************************************************************************************/
  /** Transformation methods to view a {@link PLens} as another Optics */
  /************************************************************************************************/

  /** view a {@link PLens} as a {@link Fold} */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override
      public <M> F<S, M> foldMap(final Monoid<M> m, final F<A, M> f) {
        return s -> f.f(get(s));
      }
    };
  }

  /** view a {@link PLens} as a {@link Getter} */
  public final Getter<S, A> asGetter() {
    return new Getter<S, A>() {
      @Override
      public A get(final S s) {
        return PLens.this.get(s);
      }
    };
  }

  /** view a {@link PLens} as a {@link PSetter} */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override
      public F<S, T> modify(final F<A, B> f) {
        return PLens.this.modify(f);
      }

      @Override
      public F<S, T> set(final B b) {
        return PLens.this.set(b);
      }
    };
  }

  /** view a {@link PLens} as a {@link PTraversal} */
  public PTraversal<S, T, A, B> asTraversal() {
    final PLens<S, T, A, B> self = this;
    return new PTraversal<S, T, A, B>() {

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> s, final F<A, Validation<E, B>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> f.f(get(s));
      }

    };
  }

  /** view a {@link PLens} as an {@link POptional} */
  public POptional<S, T, A, B> asOptional() {
    final PLens<S, T, A, B> self = this;
    return new POptional<S, T, A, B>() {
      @Override
      public Either<T, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public F<S, T> set(final B b) {
        return self.set(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return self.modify(f);
      }
    };
  }

  public static <S, T> PLens<S, T, S, T> pId() {
    return PIso.<S, T> pId().asLens();
  }

  /**
   * create a {@link PLens} using a pair of functions: one to get the target, one to set the target.
   */
  public static <S, T, A, B> PLens<S, T, A, B> pLens(final F<S, A> get, final F<B, F<S, T>> set) {
    return new PLens<S, T, A, B>() {

      @Override
      public A get(final S s) {
        return get.f(s);
      }

      @Override
      public F<S, T> set(final B b) {
        return set.f(b);
      }

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return s -> Function.compose(b -> set.f(b).f(s), f.f(get.f(s)));
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return s -> f.f(get.f(s)).right().map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return s -> IOFunctions.map(f.f(get.f(s)), a -> set.f(a).f(s));
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return s -> f.f(get.f(s)).fmap(a -> set.f(a).f(s));
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return s -> set.f(f.f(get.f(s))).f(s);
      }
    };
  }
}