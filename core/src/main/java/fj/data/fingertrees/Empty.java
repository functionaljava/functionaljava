package fj.data.fingertrees;

import fj.F;
import fj.P2;
import fj.P3;
import fj.Show;
import fj.data.Stream;

import static fj.Bottom.error;

/**
 * The empty tree.
 */
public final class Empty<V, A> extends FingerTree<V, A> {
  Empty(final Measured<V, A> m) {
    super(m);
  }

  @Override public FingerTree<V, A> cons(final A a) {
    return new Single<>(measured(), a);
  }

  @Override public FingerTree<V, A> snoc(final A a) {
    return cons(a);
  }

  @Override public A head() { throw error("Selection of head in empty tree"); }

  @Override public A last() { throw error("Selection of last in empty tree"); }

  @Override public FingerTree<V, A> tail() { throw error("Selection of tail in empty tree"); }

  @Override public FingerTree<V, A> init() { throw error("Selection of init in empty tree"); }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    return t;
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) { throw error("Lookup of empty tree."); }

    @Override
    public int length() {
        return 0;
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
    return new Empty<>(m);
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

  @Override P3<FingerTree<V, A>, A, FingerTree<V, A>> split1(final F<V, Boolean> predicate, final V acc) {
    throw error("Splitting an empty tree");
  }

  public String toString() {
    return Show.fingerTreeShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
  }

  public Stream<A> toStream() {
    return Stream.nil();
  }

}
