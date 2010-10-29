package fj.data.fingertrees;

import fj.F;
import fj.P2;
import static fj.P.p;

/**
 * A tree with a single element.
 */
public final class Single<V, A> extends FingerTree<V, A> {
  private final A a;
  private final V v;

  Single(final Measured<V, A> m, final A a) {
    super(m);
    this.a = a;
    v = m.measure(a);
  }

  @Override public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(a).f(z);
  }

  @Override public A reduceRight(final F<A, F<A, A>> aff) {
    return a;
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return bff.f(z).f(a);
  }

  @Override public A reduceLeft(final F<A, F<A, A>> aff) {
    return a;
  }

  @Override public <B> FingerTree<V, B> map(final F<A, B> abf, final Measured<V, B> m) {
    return new Single<V, B>(m, abf.f(a));
  }

  /**
   * Returns the annotation of this tree's single element.
   *
   * @return the annotation of this tree's single element.
   */
  public V measure() {
    return v;
  }

  /**
   * Pattern matching on the structure of this tree. Matches the singleton tree.
   */
  @Override public <B> B match(final F<Empty<V, A>, B> empty, final F<Single<V, A>, B> single,
                               final F<Deep<V, A>, B> deep) {
    return single.f(this);
  }

  @Override public FingerTree<V, A> cons(final A b) {
    final MakeTree<V, A> mk = mkTree(measured());
    return mk.deep(mk.one(b), new Empty<V, Node<V, A>>(measured().nodeMeasured()), mk.one(a));
  }

  @Override public FingerTree<V, A> snoc(final A b) {
    final MakeTree<V, A> mk = mkTree(measured());
    return mk.deep(mk.one(a), new Empty<V, Node<V, A>>(measured().nodeMeasured()), mk.one(b));
  }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    return t.cons(a);
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    return p(i, a);
  }

  /**
   * Returns the single element of this tree.
   *
   * @return the single element of this tree.
   */
  public A value() {
    return a;
  }
}
