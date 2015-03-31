package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
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

/** {@link POptional} restricted to monomorphic update */
public abstract class Optional<S, A> extends POptional<S, S, A, A> {

  Optional() {
    super();
  }

  /** join two {@link Optional} with the same target */
  public final <S1> Optional<Either<S, S1>, A> sum(final Optional<S1, A> other) {
    return optional(
        e -> e.either(s -> getOrModify(s).left().map(Either.left_()), s1 -> other.getOrModify(s1).left().map(Either.right_())),
        b -> e -> e.bimap(set(b), other.set(b)));
  }

  @Override
  public final <C> Optional<P2<S, C>, P2<A, C>> first() {
    return optional(
        sc -> getOrModify(sc._1()).bimap(t -> P.p(t, sc._2()), a -> P.p(a, sc._2())),
        bc -> s_ -> P.p(set(bc._1()).f(s_._1()), bc._2()));
  }

  @Override
  public final <C> Optional<P2<C, S>, P2<C, A>> second() {
    return optional(
        cs -> getOrModify(cs._2()).bimap(t -> P.p(cs._1(), t), a -> P.p(cs._1(), a)),
        cb -> _s -> P.p(cb._1(), set(cb._2()).f(_s._2())));
  }

  /**************************************************************/
  /** Compose methods between a {@link Optional} and another Optics */
  /**************************************************************/

  /** compose a {@link Optional} with a {@link Setter} */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link Optional} with a {@link Traversal} */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link Optional} with a {@link Optional} */
  public final <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    final Optional<S, A> self = this;
    return new Optional<S, C>() {

      @Override
      public Either<S, C> getOrModify(final S s) {
        return self.getOrModify(s).right()
            .bind(a -> other.getOrModify(a).bimap(b -> Optional.this.set(b).f(s), Function.identity()));
      }

      @Override
      public F<S, S> set(final C d) {
        return self.modify(other.set(d));
      }

      @Override
      public Option<C> getOption(final S s) {
        return self.getOption(s).bind(other::getOption);
      }

      @Override
      public <G> F<S, F<G, S>> modifyFunctionF(final F<C, F<G, C>> f) {
        return self.modifyFunctionF(other.modifyFunctionF(f));
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<C, Either<L, C>> f) {
        return self.modifyEitherF(other.modifyEitherF(f));
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<C, IO<C>> f) {
        return self.modifyIOF(other.modifyIOF(f));
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<C, Trampoline<C>> f) {
        return self.modifyTrampolineF(other.modifyTrampolineF(f));
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<C, Promise<C>> f) {
        return self.modifyPromiseF(other.modifyPromiseF(f));
      }

      @Override
      public F<S, List<S>> modifyListF(final F<C, List<C>> f) {
        return self.modifyListF(other.modifyListF(f));
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<C, Option<C>> f) {
        return self.modifyOptionF(other.modifyOptionF(f));
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<C, Stream<C>> f) {
        return self.modifyStreamF(other.modifyStreamF(f));
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<C, P1<C>> f) {
        return self.modifyP1F(other.modifyP1F(f));
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<C, Validation<E, C>> f) {
        return self.modifyValidationF(other.modifyValidationF(f));
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<C, V2<C>> f) {
        return self.modifyV2F(other.modifyV2F(f));
      }

      @Override
      public F<S, S> modify(final F<C, C> f) {
        return self.modify(other.modify(f));
      }
    };
  }

  /** compose a {@link Optional} with a {@link Prism} */
  public final <C> Optional<S, C> composePrism(final Prism<A, C> other) {
    return composeOptional(other.asOptional());
  }

  /** compose a {@link Optional} with a {@link Lens} */
  public final <C> Optional<S, C> composeLens(final Lens<A, C> other) {
    return composeOptional(other.asOptional());
  }

  /** compose a {@link Optional} with an {@link Iso} */
  public final <C> Optional<S, C> composeIso(final Iso<A, C> other) {
    return composeOptional(other.asOptional());
  }

  /********************************************************************/
  /** Transformation methods to view a {@link Optional} as another Optics */
  /********************************************************************/

  /** view a {@link Optional} as a {@link Setter} */
  @Override
  public final Setter<S, A> asSetter() {
    return new Setter<S, A>() {
      @Override
      public F<S, S> modify(final F<A, A> f) {
        return Optional.this.modify(f);
      }

      @Override
      public F<S, S> set(final A b) {
        return Optional.this.set(b);
      }
    };
  }

  /** view a {@link Optional} as a {@link Traversal} */
  @Override
  public final Traversal<S, A> asTraversal() {
    final Optional<S, A> self = this;
    return new Traversal<S, A>() {

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> self.getOption(s).map(f).orSome(monoid.zero());
      }
    };
  }

  public static <S> Optional<S, S> id() {
    return Iso.<S> id().asOptional();
  }

  /** create a {@link Optional} using the canonical functions: getOrModify and set */
  public static final <S, A> Optional<S, A> optional(final F<S, Either<S, A>> getOrModify, final F<A, F<S, S>> set) {
    return new Optional<S, A>() {
      @Override
      public Either<S, A> getOrModify(final S s) {
        return getOrModify.f(s);
      }

      @Override
      public F<S, S> set(final A b) {
        return set.f(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return getOrModify.f(s).right().toOption();
      }

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return s -> getOrModify(s).either(
            Function.constant(),
            a -> Function.compose(b -> set(b).f(s), f.f(a))
            );
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return s -> getOrModify(s).either(
            Either.right_(),
            t -> f.f(t).right().map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return s -> getOrModify(s).either(
            IOFunctions::unit,
            t -> IOFunctions.<A, S> map(f.f(t), b -> set(b).f(s))
            );
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return s -> getOrModify(s).either(
            Trampoline.pure(),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return s -> getOrModify(s).either(
            t -> Promise.promise(Strategy.idStrategy(), P.p(t)),
            t -> f.f(t).fmap(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return s -> getOrModify(s).either(
            List::single,
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return s -> getOrModify(s).either(
            Option.some_(),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return s -> getOrModify(s).either(
            Stream.single(),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return s -> getOrModify(s).either(
            P.p1(),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return s -> getOrModify(s).either(
            t -> Validation.<E, S> success(t),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return s -> getOrModify(s).either(
            t -> V2.p(P.p(t, t)),
            t -> f.f(t).map(b -> set(b).f(s))
            );
      }

      @Override
      public F<S, S> modify(final F<A, A> f) {
        return s -> getOrModify.f(s).either(Function.identity(), a -> set.f(f.f(a)).f(s));
      }
    };
  }

}
