package fj.data.optic;

import fj.F;
import fj.P2;

/** {@link PIso} when S = T and A = B */
public final class Iso<S, A> extends PIso<S, S, A, A> {

  final PIso<S, S, A, A> pIso;

  public Iso(final PIso<S, S, A, A> pIso) {
    this.pIso = pIso;
  }

  @Override
  public A get(final S s) {
    return pIso.get(s);
  }

  @Override
  public S reverseGet(final A a) {
    return pIso.reverseGet(a);
  }

  @Override
  public Iso<A, S> reverse() {
    return new Iso<>(pIso.reverse());
  }

  /** pair two disjoint {@link Iso} */
  public <S1, A1> Iso<P2<S, S1>, P2<A, A1>> product(final Iso<S1, A1> other) {
    return new Iso<>(pIso.product(other.pIso));
  }

  @Override
  public <C> Iso<P2<S, C>, P2<A, C>> first() {
    return new Iso<>(pIso.first());
  }

  @Override
  public <C> Iso<P2<C, S>, P2<C, A>> second() {
    return new Iso<>(pIso.second());
  }

  /**********************************************************/
  /** Compose methods between an {@link Iso} and another Optics */
  /**********************************************************/

  /** compose an {@link Iso} with a {@link Setter} */
  public <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pIso.composeSetter(other.pSetter));
  }

  /** compose an {@link Iso} with a {@link Traversal} */
  public <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pIso.composeTraversal(other.pTraversal));
  }

  /** compose an {@link Iso} with a {@link Optional} */
  public <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return new Optional<>(pIso.composeOptional(other.pOptional));
  }

  /** compose an {@link Iso} with a {@link Prism} */
  public <C> Prism<S, C> composePrism(final Prism<A, C> other) {
    return new Prism<>(pIso.composePrism(other.pPrism));
  }

  /** compose an {@link Iso} with a {@link Lens} */
  public <C> Lens<S, C> composeLens(final Lens<A, C> other) {
    return asLens().composeLens(other);
  }

  /** compose an {@link Iso} with an {@link Iso} */
  public <C> Iso<S, C> composeIso(final Iso<A, C> other) {
    return new Iso<>(pIso.composeIso(other.pIso));
  }

  /****************************************************************/
  /** Transformation methods to view an {@link Iso} as another Optics */
  /****************************************************************/

  /** view an {@link Iso} as a {@link Setter} */
  @Override
  public Setter<S, A> asSetter() {
    return new Setter<>(pIso.asSetter());
  }

  /** view an {@link Iso} as a {@link Traversal} */
  @Override
  public Traversal<S, A> asTraversal() {
    return new Traversal<>(pIso.asTraversal());
  }

  /** view an {@link Iso} as a {@link Optional} */
  @Override
  public Optional<S, A> asOptional() {
    return new Optional<>(pIso.asOptional());
  }

  /** view an {@link Iso} as a {@link Prism} */
  @Override
  public Prism<S, A> asPrism() {
    return new Prism<>(pIso.asPrism());
  }

  /** view an {@link Iso} as a {@link Lens} */
  @Override
  public Lens<S, A> asLens() {
    return new Lens<>(pIso.asLens());
  }

  /** create an {@link Iso} using a pair of functions: one to get the target and one to get the source. */
  public static <S, A> Iso<S, A> iso(final F<S, A> get, final F<A, S> reverseGet) {
    return new Iso<>(PIso.pIso(get, reverseGet));
  }

  /**
   * create an {@link Iso} between any type and itself. id is the zero element of optics composition, for all optics o of type O
   * (e.g. Lens, Iso, Prism, ...):
   *
   * <pre>
   *  o composeIso Iso.id == o
   *  Iso.id composeO o == o
   * </pre>
   *
   * (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  public static <S> Iso<S, S> id() {
    return new Iso<>(PIso.pId());
  }

}
