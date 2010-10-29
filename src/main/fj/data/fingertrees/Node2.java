package fj.data.fingertrees;

import fj.data.vector.V2;
import fj.F;
import fj.P2;

/**
 * A two-element inner tree node.
 */
public final class Node2<V, A> extends Node<V, A> {
  private final V2<A> as;

  Node2(final Measured<V, A> m, final V2<A> as) {
    super(m, m.sum(m.measure(as._1()), m.measure(as._2())));
    this.as = as;
  }

  @Override public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(as._1()).f(aff.f(as._2()).f(z));
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return bff.f(bff.f(z).f(as._1())).f(as._2());
  }

  public Digit<V, A> toDigit() {
    return new Two<V, A>(measured(), as);
  }

  @SuppressWarnings({"ReturnOfNull"})
  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    return null; // TODO
  }

  public <B> B match(final F<Node2<V, A>, B> n2, final F<Node3<V, A>, B> n3) {
    return n2.f(this);
  }

  public V2<A> toVector() {
    return as;
  }
}
