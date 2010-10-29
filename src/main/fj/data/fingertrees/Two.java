package fj.data.fingertrees;

import fj.data.vector.V2;
import fj.F;

/**
 * A two-element prefix or suffix of a finger tree.
 */
public final class Two<V, A> extends Digit<V, A> {
  private final V2<A> as;

  Two(final Measured<V, A> m, final V2<A> as) {
    super(m);
    this.as = as;
  }

  public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(as._1()).f(aff.f(as._2()).f(z));
  }

  public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return as.toStream().foldLeft(bff, z);
  }

  @Override public <B> B match(
      final F<One<V, A>, B> one, final F<Two<V, A>, B> two, final F<Three<V, A>, B> three,
      final F<Four<V, A>, B> four) {
    return two.f(this);
  }

  /**
   * Returns the elements of this digit as a vector.
   *
   * @return the elements of this digit as a vector.
   */
  public V2<A> values() {
    return as;
  }
}
