package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Semigroup;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.Either;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/**
 * A {@link POptional} can be seen as a pair of functions: - `getOrModify: S => T \/ A` - `set : (B, S) => T`
 *
 * A {@link POptional} could also be defined as a weaker {@link PLens} and weaker {@link PPrism}
 *
 * {@link POptional} stands for Polymorphic Optional as it set and modify methods change a type `A` to `B` and `S` to `T`.
 * {@link Optional} is a {@link POptional} restricted to monomoprhic updates: {{{ type Optional[S, A] = POptional[S, S, A, A]
 * }}}
 *
 * @param <S> the source of a {@link POptional}
 * @param <T> the modified source of a {@link POptional}
 * @param <A> the target of a {@link POptional}
 * @param <B> the modified target of a {@link POptional}
 */
public abstract class POptional<S, T, A, B> {

  POptional() {
    super();
  }

  /** get the target of a {@link POptional} or modify the source in case there is no target */
  public abstract Either<T, A> getOrModify(S s);

  /** get the modified source of a {@link POptional} */
  public abstract F<S, T> set(final B b);

  /** get the target of a {@link POptional} or nothing if there is no target */
  public abstract Option<A> getOption(final S s);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, IO<T>> modifyIOF(F<A, IO<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, Trampoline<T>> modifyTrampolineF(F<A, Trampoline<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, Promise<T>> modifyPromiseF(F<A, Promise<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, List<T>> modifyListF(F<A, List<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, Option<T>> modifyOptionF(F<A, Option<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, Stream<T>> modifyStreamF(F<A, Stream<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, P1<T>> modifyP1F(F<A, P1<B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract <E> F<S, Validation<E, T>> modifyValidationF(F<A, Validation<E, B>> f);

  /**
   * modify polymorphically the target of a {@link POptional} with an Applicative function
   */
  public abstract F<S, V2<T>> modifyV2F(F<A, V2<B>> f);

  /** modify polymorphically the target of a {@link POptional} with a function */
  public abstract F<S, T> modify(final F<A, B> f);

  /**
   * modify polymorphically the target of a {@link POptional} with a function. return empty if the {@link POptional} is not
   * matching
   */
  public final F<S, Option<T>> modifyOption(final F<A, B> f) {
    return s -> getOption(s).map(Function.constant(modify(f).f(s)));
  }

  /** set polymorphically the target of a {@link POptional} with a value. return empty if the {@link POptional} is not matching */
  public final F<S, Option<T>> setOption(final B b) {
    return modifyOption(Function.constant(b));
  }

  /** check if a {@link POptional} has a target */
  public final boolean isMatching(final S s) {
    return getOption(s).isSome();

  }

  /** join two {@link POptional} with the same target */
  public final <S1, T1> POptional<Either<S, S1>, Either<T, T1>, A, B> sum(final POptional<S1, T1, A, B> other) {
    return pOptional(
        e -> e.either(s -> getOrModify(s).left().map(Either.left_()), s1 -> other.getOrModify(s1).left().map(Either.right_())),
        b -> e -> e.bimap(set(b), other.set(b)));
  }

  public <C> POptional<P2<S, C>, P2<T, C>, P2<A, C>, P2<B, C>> first() {
    return pOptional(
        sc -> getOrModify(sc._1()).bimap(t -> P.p(t, sc._2()), a -> P.p(a, sc._2())),
        bc -> s_ -> P.p(set(bc._1()).f(s_._1()), bc._2()));
  }

  public <C> POptional<P2<C, S>, P2<C, T>, P2<C, A>, P2<C, B>> second() {
    return pOptional(
        cs -> getOrModify(cs._2()).bimap(t -> P.p(cs._1(), t), a -> P.p(cs._1(), a)),
        cb -> _s -> P.p(cb._1(), set(cb._2()).f(_s._2())));
  }

  /***************************************************************/
  /** Compose methods between a {@link POptional} and another Optics */
  /***************************************************************/

  /** compose a {@link POptional} with a {@link Fold} */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /** compose a {@link POptional} with a {@link Getter} */
  public final <C> Fold<S, C> composeGetter(final Getter<A, C> other) {
    return asFold().composeGetter(other);
  }

  /** compose a {@link POptional} with a {@link PSetter} */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link POptional} with a {@link PTraversal} */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link POptional} with a {@link POptional} */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    final POptional<S, T, A, B> self = this;
    return new POptional<S, T, C, D>() {

      @Override
      public Either<T, C> getOrModify(final S s) {
        return self.getOrModify(s).right()
            .bind(a -> other.getOrModify(a).bimap(b -> POptional.this.set(b).f(s), Function.identity()));
      }

      @Override
      public F<S, T> set(final D d) {
        return self.modify(other.set(d));
      }

      @Override
      public Option<C> getOption(final S s) {
        return self.getOption(s).bind(other::getOption);
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

  /** compose a {@link POptional} with a {@link PPrism} */
  public final <C, D> POptional<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /** compose a {@link POptional} with a {@link PLens} */
  public final <C, D> POptional<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /** compose a {@link POptional} with a {@link PIso} */
  public final <C, D> POptional<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composeOptional(other.asOptional());
  }

  /*********************************************************************/
  /** Transformation methods to view a {@link POptional} as another Optics */
  /*********************************************************************/

  /** view a {@link POptional} as a {@link Fold} */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override
      public <M> F<S, M> foldMap(final Monoid<M> m, final F<A, M> f) {
        return s -> POptional.this.getOption(s).map(f).orSome(m.zero());
      }
    };
  }

  /** view a {@link POptional} as a {@link PSetter} */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override
      public F<S, T> modify(final F<A, B> f) {
        return POptional.this.modify(f);
      }

      @Override
      public F<S, T> set(final B b) {
        return POptional.this.set(b);
      }
    };
  }

  /** view a {@link POptional} as a {@link PTraversal} */
  public PTraversal<S, T, A, B> asTraversal() {
    final POptional<S, T, A, B> self = this;
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
        return s -> self.getOption(s).map(f).orSome(monoid.zero());
      }
    };
  }

  public static <S, T> POptional<S, T, S, T> pId() {
    return PIso.<S, T> pId().asOptional();
  }

  /** create a {@link POptional} using the canonical functions: getOrModify and set */
  public static <S, T, A, B> POptional<S, T, A, B> pOptional(final F<S, Either<T, A>> getOrModify, final F<B, F<S, T>> set) {
    return new POptional<S, T, A, B>() {
      @Override
      public Either<T, A> getOrModify(final S s) {
        return getOrModify.f(s);
      }

      @Override
      public F<S, T> set(final B b) {
        return set.f(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return getOrModify.f(s).right().toOption();
      }

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return s -> getOrModify.f(s).either(
            Function.constant(),
            a -> Function.compose(b -> set.f(b).f(s), f.f(a))
            );
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return s -> getOrModify.f(s).either(
            Either.right_(),
            t -> f.f(t).right().map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return s -> getOrModify.f(s).either(
            IOFunctions::unit,
            t -> IOFunctions.map(f.f(t), b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return s -> getOrModify.f(s).either(
            Trampoline.pure(),
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return s -> getOrModify.f(s).either(
            t -> Promise.promise(Strategy.idStrategy(), P.p(t)),
            t -> f.f(t).fmap(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return s -> getOrModify.f(s).either(
            List::single,
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return s -> getOrModify.f(s).either(
            Option.some_(),
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return s -> getOrModify.f(s).either(
            Stream.single(),
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return s -> getOrModify.f(s).either(
            P.p1(),
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
        return s -> getOrModify.f(s).either(
            Validation::<E, T>success,
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return s -> getOrModify.f(s).either(
            t -> V2.p(P.p(t, t)),
            t -> f.f(t).map(b -> set.f(b).f(s))
            );
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return s -> getOrModify.f(s).either(Function.identity(), a -> set.f(f.f(a)).f(s));
      }
    };
  }

}