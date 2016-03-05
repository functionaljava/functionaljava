package fj.data.optic;

import fj.F;
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
 * {@link PLens} with a monomorphic set function
 */
public final class Lens<S, A> extends PLens<S, S, A, A> {

  final PLens<S, S, A, A> pLens;

  public Lens(final PLens<S, S, A, A> pLens) {
    this.pLens = pLens;
  }

  @Override
  public A get(final S s) {
    return pLens.get(s);
  }

  @Override
  public F<S, S> set(final A a) {
    return pLens.set(a);
  }

  @Override
  public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
    return pLens.modifyFunctionF(f);
  }

  @Override
  public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
    return pLens.modifyEitherF(f);
  }

  @Override
  public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
    return pLens.modifyIOF(f);
  }

  @Override
  public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
    return pLens.modifyTrampolineF(f);
  }

  @Override
  public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
    return pLens.modifyPromiseF(f);
  }

  @Override
  public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
    return pLens.modifyListF(f);
  }

  @Override
  public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
    return pLens.modifyOptionF(f);
  }

  @Override
  public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
    return pLens.modifyStreamF(f);
  }

  @Override
  public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
    return pLens.modifyP1F(f);
  }

  @Override
  public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
    return pLens.modifyValidationF(f);
  }

  @Override
  public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
    return pLens.modifyV2F(f);
  }

  @Override
  public F<S, S> modify(final F<A, A> f) {
    return pLens.modify(f);
  }

  /** join two {@link Lens} with the same target */
  public <S1> Lens<Either<S, S1>, A> sum(final Lens<S1, A> other) {
    return new Lens<>(pLens.sum(other.pLens));
  }

  /**********************************************************/
  /** Compose methods between a {@link Lens} and another Optics */
  /**********************************************************/

  /**
   * compose a {@link Lens} with a {@link Setter}
   */
  public <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pLens.composeSetter(other.pSetter));
  }

  /**
   * compose a {@link Lens} with a {@link Traversal}
   */
  public <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pLens.composeTraversal(other.pTraversal));
  }

  /** compose a {@link Lens} with an {@link Optional} */
  public <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return new Optional<>(pLens.composeOptional(other.pOptional));
  }

  /** compose a {@link Lens} with a {@link Prism} */
  public <C> Optional<S, C> composePrism(final Prism<A, C> other) {
    return new Optional<>(pLens.composePrism(other.pPrism));
  }

  /** compose a {@link Lens} with a {@link Lens} */
  public <C> Lens<S, C> composeLens(final Lens<A, C> other) {
    return new Lens<>(pLens.composeLens(other.pLens));
  }

  /** compose a {@link Lens} with an {@link Iso} */
  public <C> Lens<S, C> composeIso(final Iso<A, C> other) {
    return new Lens<>(pLens.composeIso(other.pIso));
  }

  /****************************************************************/
  /** Transformation methods to view a {@link Lens} as another Optics */
  /****************************************************************/

  /** view a {@link Lens} as a {@link Setter} */
  @Override
  public Setter<S, A> asSetter() {
    return new Setter<>(pLens.asSetter());
  }

  /** view a {@link Lens} as a {@link Traversal} */
  @Override
  public Traversal<S, A> asTraversal() {
    return new Traversal<>(pLens.asTraversal());
  }

  /** view a {@link Lens} as an {@link Optional} */
  @Override
  public Optional<S, A> asOptional() {
    return new Optional<>(pLens.asOptional());
  }

  public static <S> Lens<S, S> id() {
    return new Lens<>(PLens.pId());
  }

  /**
   * create a {@link Lens} using a pair of functions: one to get the target, one to set the target.
   */
  public static <S, A> Lens<S, A> lens(final F<S, A> get, final F<A, F<S, S>> set) {
    return new Lens<>(PLens.pLens(get, set));
  }

}