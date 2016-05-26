package fj.data.optic;

import fj.F;
import fj.data.Either;
import fj.data.Option;

/**
 * {@link PPrism} restricted to monomorphic update
 */
public final class Prism<S, A> extends PPrism<S, S, A, A> {

  final PPrism<S, S, A, A> pPrism;

  public Prism(final PPrism<S, S, A, A> pPrism) {
    this.pPrism = pPrism;
  }

  @Override
  public Either<S, A> getOrModify(final S s) {
    return pPrism.getOrModify(s);
  }

  @Override
  public S reverseGet(final A a) {
    return pPrism.reverseGet(a);
  }

  @Override
  public Option<A> getOption(final S s) {
    return pPrism.getOption(s);
  }

  /***********************************************************/
  /** Compose methods between a {@link Prism} and another Optics */
  /***********************************************************/

  /** compose a {@link Prism} with a {@link Setter} */
  public <C, D> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pPrism.composeSetter(other.pSetter));
  }

  /** compose a {@link Prism} with a {@link Traversal} */
  public <C, D> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pPrism.composeTraversal(other.pTraversal));
  }

  /** compose a {@link Prism} with a {@link Optional} */
  public <C, D> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return new Optional<>(pPrism.composeOptional(other.pOptional));
  }

  /** compose a {@link Prism} with a {@link Lens} */
  public <C, D> Optional<S, C> composeLens(final Lens<A, C> other) {
    return new Optional<>(pPrism.composeLens(other.pLens));
  }

  /** compose a {@link Prism} with a {@link Prism} */
  public <C> Prism<S, C> composePrism(final Prism<A, C> other) {
    return new Prism<>(pPrism.composePrism(other.pPrism));
  }

  /** compose a {@link Prism} with an {@link Iso} */
  public <C, D> Prism<S, C> composeIso(final Iso<A, C> other) {
    return new Prism<>(pPrism.composeIso(other.pIso));
  }

  /*****************************************************************/
  /** Transformation methods to view a {@link Prism} as another Optics */
  /*****************************************************************/

  /** view a {@link Prism} as a {@link Setter} */
  @Override
  public Setter<S, A> asSetter() {
    return new Setter<>(pPrism.asSetter());
  }

  /** view a {@link Prism} as a {@link Traversal} */
  @Override
  public Traversal<S, A> asTraversal() {
    return new Traversal<>(pPrism.asTraversal());
  }

  /** view a {@link Prism} as a {@link Optional} */
  @Override
  public Optional<S, A> asOptional() {
    return new Optional<>(pPrism.asOptional());
  }

  public static <S> Prism<S, S> id() {
    return new Prism<>(PPrism.pId());
  }

  public static <S, A> Prism<S, A> prism(final F<S, Option<A>> getOption, final F<A, S> reverseGet) {
    return new Prism<>(new PPrism<S, S, A, A>() {

      @Override
      public Either<S, A> getOrModify(final S s) {
        return getOption.f(s).option(Either.left(s), Either.right_());
      }

      @Override
      public S reverseGet(final A a) {
        return reverseGet.f(a);
      }

      @Override
      public Option<A> getOption(final S s) {
        return getOption.f(s);
      }
    });
  }

}