package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P1;
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
 * {@link PLens} with a monomorphic set function
 */
public abstract class Lens<S, A> extends PLens<S, S, A, A> {

  Lens() {
    super();
  }

  /** join two {@link Lens} with the same target */
  public final <S1> Lens<Either<S, S1>, A> sum(final Lens<S1, A> other) {
    return lens(
        e -> e.either(this::get, other::get),
        b -> e -> e.bimap(Lens.this.set(b), other.set(b)));
  }

  /**********************************************************/
  /** Compose methods between a {@link Lens} and another Optics */
  /**********************************************************/

  /**
   * compose a {@link Lens} with a {@link Setter}
   */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return asSetter().composeSetter(other);
  }

  /**
   * compose a {@link Lens} with a {@link Traversal}
   */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link Lens} with an {@link Optional} */
  public final <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return asOptional().composeOptional(other);
  }

  /** compose a {@link Lens} with a {@link Prism} */
  public final <C> Optional<S, C> composePrism(final Prism<A, C> other) {
    return asOptional().composeOptional(other.asOptional());
  }

  /** compose a {@link Lens} with a {@link Lens} */
  public final <C> Lens<S, C> composeLens(final Lens<A, C> other) {
    final Lens<S, A> self = this;
    return new Lens<S, C>() {
      @Override
      public C get(final S s) {
        return other.get(self.get(s));
      }

      @Override
      public F<S, S> set(final C d) {
        return self.modify(other.set(d));
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

  /** compose a {@link Lens} with an {@link Iso} */
  public final <C> Lens<S, C> composeIso(final Iso<A, C> other) {
    return composeLens(other.asLens());
  }

  /****************************************************************/
  /** Transformation methods to view a {@link Lens} as another Optics */
  /****************************************************************/

  /** view a {@link Lens} as a {@link Setter} */
  @Override
  public Setter<S, A> asSetter() {
    return new Setter<S, A>() {
      @Override
      public F<S, S> modify(final F<A, A> f) {
        return Lens.this.modify(f);
      }

      @Override
      public F<S, S> set(final A b) {
        return Lens.this.set(b);
      }
    };
  }

  /** view a {@link Lens} as a {@link Traversal} */
  @Override
  public final Traversal<S, A> asTraversal() {
    final Lens<S, A> self = this;
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
        return s -> f.f(get(s));
      }

    };
  }

  /** view a {@link Lens} as an {@link Optional} */
  @Override
  public final Optional<S, A> asOptional() {
    final Lens<S, A> self = this;
    return new Optional<S, A>() {
      @Override
      public Either<S, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public F<S, S> set(final A b) {
        return self.set(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }

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
      public F<S, S> modify(final F<A, A> f) {
        return self.modify(f);
      }
    };
  }

  public static final <S> Lens<S, S> id() {
    return Iso.<S> id().asLens();
  }

  /**
   * create a {@link Lens} using a pair of functions: one to get the target, one to set the target.
   */
  public static <S, A> Lens<S, A> lens(final F<S, A> get, final F<A, F<S, S>> set) {
    return new Lens<S, A>() {

      @Override
      public A get(final S s) {
        return get.f(s);
      }

      @Override
      public F<S, S> set(final A b) {
        return set.f(b);
      }

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return s -> Function.compose(b -> set.f(b).f(s), f.f(get(s)));
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return s -> f.f(get.f(s)).right().map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return s -> IOFunctions.map(f.f(get.f(s)), a -> set.f(a).f(s));
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return s -> f.f(get.f(s)).fmap(a -> set.f(a).f(s));
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return s -> f.f(get.f(s)).map(a -> set.f(a).f(s));
      }

      @Override
      public F<S, S> modify(final F<A, A> f) {
        return s -> set(f.f(get.f(s))).f(s);
      }
    };
  }
}