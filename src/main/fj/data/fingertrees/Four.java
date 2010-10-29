package fj.data.fingertrees;

import fj.data.vector.V4;
import fj.F;

/**
 * A four-element prefix or suffix of a finger tree.
 */
public final class Four<V, A> extends Digit<V, A> {
  private final V4<A> as;

  Four(final Measured<V, A> m, final V4<A> as) {
    super(m);
    this.as = as;
  }

  public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(aff.f(as._4()).f(z))));
  }

  public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return as.toStream().foldLeft(bff, z);
  }

  @Override public <B> B match(
      final F<One<V, A>, B> one, final F<Two<V, A>, B> two, final F<Three<V, A>, B> three,
      final F<Four<V, A>, B> four) {
    return four.f(this);
  }

  /**
   * Returns the elements of this digit as a vector.
   *
   * @return the elements of this digit as a vector.
   */
  public V4<A> values() {
    return as;
  }
}
