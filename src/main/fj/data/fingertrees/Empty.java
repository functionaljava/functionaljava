package fj.data.fingertrees;

import fj.F;
import fj.P2;
import static fj.Bottom.error;

/**
 * The empty tree.
 */
public final class Empty<V, A> extends FingerTree<V, A> {
  Empty(final Measured<V, A> m) {
    super(m);
  }

  @Override public FingerTree<V, A> cons(final A a) {
    return new Single<V, A>(measured(), a);
  }

  @Override public FingerTree<V, A> snoc(final A a) {
    return cons(a);
  }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    return t;
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    throw error("Lookup of empty tree.");
  }

  @Override public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return z;
  }

  public A reduceRight(final F<A, F<A, A>> aff) {
    throw error("Reduction of empty tree");
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return z;
  }

  @Override public A reduceLeft(final F<A, F<A, A>> aff) {
    throw error("Reduction of empty tree");
  }

  @Override public <B> FingerTree<V, B> map(final F<A, B> abf, final Measured<V, B> m) {
    return new Empty<V, B>(m);
  }

  /**
   * Returns zero.
   *
   * @return Zero.
   */
  public V measure() {
    return measured().zero();
  }

  /**
   * Pattern matching on the structure of this tree. Matches the empty tree.
   */
  @Override public <B> B match(
      final F<Empty<V, A>, B> empty, final F<Single<V, A>, B> single, final F<Deep<V, A>, B> deep) {
    return empty.f(this);
  }


}
