package fj.data.optic;

import fj.F;
import fj.data.Either;

/** {@link PSetter} with a monomorphic modify function */
public abstract class Setter<S, A> extends PSetter<S, S, A, A> {

  Setter() {
    super();
  }

  /** join two {@link Setter} with the same target */
  public final <S1> Setter<Either<S, S1>, A> sum(final Setter<S1, A> other) {
    return setter(f -> e -> e.bimap(modify(f), other.modify(f)));
  }

  /************************************************************/
  /** Compose methods between a {@link Setter} and another Optics */
  /************************************************************/

  /** compose a {@link Setter} with a {@link Setter} */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    final Setter<S, A> self = this;
    return new Setter<S, C>() {

      @Override
      public F<S, S> modify(final F<C, C> f) {
        return self.modify(other.modify(f));
      }

      @Override
      public F<S, S> set(final C c) {
        return self.modify(other.set(c));
      }
    };
  }

  /** compose a {@link Setter} with a {@link Traversal} */
  public final <C> Setter<S, C> composeTraversal(final Traversal<A, C> other) {
    return composeSetter(other.asSetter());
  }

  /** compose a {@link Setter} with an {@link Iso} */
  public final <C> Setter<S, C> composeIso(final Iso<A, C> other) {
    return composeSetter(other.asSetter());
  }

  public static <S> Setter<S, S> id() {
    return Iso.<S> id().asSetter();
  }

  public static final <S> Setter<Either<S, S>, S> codiagonal() {
    return setter(f -> e -> e.bimap(f, f));
  }

  /** alias for {@link PSetter} constructor with a monomorphic modify function */
  public static final <S, A> Setter<S, A> setter(final F<F<A, A>, F<S, S>> modify) {
    return new Setter<S, A>() {
      @Override
      public F<S, S> modify(final F<A, A> f) {
        return modify.f(f);
      }

      @Override
      public F<S, S> set(final A a) {
        return modify(__ -> a);
      }
    };
  }
}
