package fj.data.optic;

import fj.F;
import fj.P1;
import fj.P2;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/** {@link POptional} restricted to monomorphic update */
public final class Optional<S, A> extends POptional<S, S, A, A> {

  final POptional<S, S, A, A> pOptional;

  public Optional(final POptional<S, S, A, A> pOptional) {
    this.pOptional = pOptional;
  }

  @Override
  public F<S, S> set(final A a) {
    return pOptional.set(a);
  }

  @Override
  public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
    return pOptional.modifyValidationF(f);
  }

  @Override
  public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
    return pOptional.modifyV2F(f);
  }

  @Override
  public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
    return pOptional.modifyTrampolineF(f);
  }

  @Override
  public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
    return pOptional.modifyStreamF(f);
  }

  @Override
  public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
    return pOptional.modifyPromiseF(f);
  }

  @Override
  public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
    return pOptional.modifyP1F(f);
  }

  @Override
  public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
    return pOptional.modifyOptionF(f);
  }

  @Override
  public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
    return pOptional.modifyListF(f);
  }

  @Override
  public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
    return pOptional.modifyIOF(f);
  }

  @Override
  public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
    return pOptional.modifyFunctionF(f);
  }

  @Override
  public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
    return pOptional.modifyEitherF(f);
  }

  @Override
  public F<S, S> modify(final F<A, A> f) {
    return pOptional.modify(f);
  }

  @Override
  public Either<S, A> getOrModify(final S s) {
    return pOptional.getOrModify(s);
  }

  @Override
  public Option<A> getOption(final S s) {
    return pOptional.getOption(s);
  }

  /** join two {@link Optional} with the same target */
  public final <S1> Optional<Either<S, S1>, A> sum(final Optional<S1, A> other) {
    return new Optional<>(pOptional.sum(other.pOptional));
  }

  @Override
  public final <C> Optional<P2<S, C>, P2<A, C>> first() {
    return new Optional<>(pOptional.first());
  }

  @Override
  public final <C> Optional<P2<C, S>, P2<C, A>> second() {
    return new Optional<>(pOptional.second());
  }

  /**************************************************************/
  /** Compose methods between a {@link Optional} and another Optics */
  /**************************************************************/

  /** compose a {@link Optional} with a {@link Setter} */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pOptional.composeSetter(other.pSetter));
  }

  /** compose a {@link Optional} with a {@link Traversal} */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pOptional.composeTraversal(other.pTraversal));
  }

  /** compose a {@link Optional} with a {@link Optional} */
  public final <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return new Optional<>(pOptional.composeOptional(other.pOptional));
  }

  /** compose a {@link Optional} with a {@link Prism} */
  public final <C> Optional<S, C> composePrism(final Prism<A, C> other) {
    return new Optional<>(pOptional.composePrism(other.pPrism));
  }

  /** compose a {@link Optional} with a {@link Lens} */
  public final <C> Optional<S, C> composeLens(final Lens<A, C> other) {
    return new Optional<>(pOptional.composeLens(other.pLens));
  }

  /** compose a {@link Optional} with an {@link Iso} */
  public final <C> Optional<S, C> composeIso(final Iso<A, C> other) {
    return new Optional<>(pOptional.composeIso(other.pIso));
  }

  /********************************************************************/
  /** Transformation methods to view a {@link Optional} as another Optics */
  /********************************************************************/

  /** view a {@link Optional} as a {@link Setter} */
  @Override
  public final Setter<S, A> asSetter() {
    return new Setter<>(pOptional.asSetter());
  }

  /** view a {@link Optional} as a {@link Traversal} */
  @Override
  public final Traversal<S, A> asTraversal() {
    return new Traversal<>(pOptional.asTraversal());
  }

  public static <S> Optional<S, S> id() {
    return new Optional<>(POptional.pId());
  }

  /** create a {@link Optional} using the canonical functions: getOrModify and set */
  public static final <S, A> Optional<S, A> optional(final F<S, Either<S, A>> getOrModify, final F<A, F<S, S>> set) {
    return new Optional<>(POptional.pOptional(getOrModify, set));
  }

}
