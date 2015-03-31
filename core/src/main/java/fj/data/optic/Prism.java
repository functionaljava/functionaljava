package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P1;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/**
 * {@link PPrism} restricted to monomorphic update
 */
public abstract class Prism<S, A> extends PPrism<S, S, A, A> {

  Prism() {
    super();
  }

  /***********************************************************/
  /** Compose methods between a {@link Prism} and another Optics */
  /***********************************************************/

  /** compose a {@link Prism} with a {@link Setter} */
  public final <C, D> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link Prism} with a {@link Traversal} */
  public final <C, D> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link Prism} with a {@link Optional} */
  public final <C, D> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return asOptional().composeOptional(other);
  }

  /** compose a {@link Prism} with a {@link Lens} */
  public final <C, D> Optional<S, C> composeLens(final Lens<A, C> other) {
    return asOptional().composeOptional(other.asOptional());
  }

  /** compose a {@link Prism} with a {@link Prism} */
  public final <C> Prism<S, C> composePrism(final Prism<A, C> other) {
    return new Prism<S, C>() {

      @Override
      public Either<S, C> getOrModify(final S s) {
        return Prism.this.getOrModify(s).right()
            .bind(a -> other.getOrModify(a).bimap(b -> Prism.this.set(b).f(s), Function.identity()));
      }

      @Override
      public S reverseGet(final C d) {
        return Prism.this.reverseGet(other.reverseGet(d));
      }

      @Override
      public Option<C> getOption(final S s) {
        return Prism.this.getOption(s).bind(other::getOption);
      }
    };
  }

  /** compose a {@link Prism} with an {@link Iso} */
  public final <C, D> Prism<S, C> composeIso(final Iso<A, C> other) {
    return composePrism(other.asPrism());
  }

  /*****************************************************************/
  /** Transformation methods to view a {@link Prism} as another Optics */
  /*****************************************************************/

  /** view a {@link Prism} as a {@link Setter} */
  @Override
  public final Setter<S, A> asSetter() {
    return new Setter<S, A>() {
      @Override
      public F<S, S> modify(final F<A, A> f) {
        return Prism.this.modify(f);
      }

      @Override
      public F<S, S> set(final A b) {
        return Prism.this.set(b);
      }
    };
  }

  /** view a {@link Prism} as a {@link Traversal} */
  @Override
  public final Traversal<S, A> asTraversal() {
    final Prism<S, A> self = this;
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
        return s -> getOption(s).map(f).orSome(monoid.zero());
      }

    };
  }

  /** view a {@link Prism} as a {@link Optional} */
  @Override
  public final Optional<S, A> asOptional() {
    final Prism<S, A> self = this;
    return new Optional<S, A>() {

      @Override
      public Either<S, A> getOrModify(final S s) {
        return self.getOrModify(s);
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
      public F<S, S> set(final A b) {
        return self.set(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return self.getOption(s);
      }

      @Override
      public F<S, S> modify(final F<A, A> f) {
        return self.modify(f);
      }

    };
  }

  public static <S> Prism<S, S> id() {
    return Iso.<S> id().asPrism();
  }

  /** create a {@link Prism} using the canonical functions: getOrModify and reverseGet */
  public static <S, A> Prism<S, A> prism(final F<S, Either<S, A>> getOrModify, final F<A, S> reverseGet) {
    return new Prism<S, A>() {

      @Override
      public Either<S, A> getOrModify(final S s) {
        return getOrModify.f(s);
      }

      @Override
      public S reverseGet(final A b) {
        return reverseGet.f(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return getOrModify.f(s).right().toOption();
      }
    };
  }

}