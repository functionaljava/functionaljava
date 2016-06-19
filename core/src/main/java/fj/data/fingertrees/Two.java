package fj.data.fingertrees;

import fj.P;
import fj.P2;
import fj.P3;
import fj.Show;
import fj.data.Option;
import fj.data.Stream;
import fj.data.vector.V2;
import fj.F;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.fingertrees.FingerTree.mkTree;

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

  @Override P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> split1(final F<V, Boolean> predicate, final V acc) {
    final Measured<V, A> m = measured();
    final MakeTree<V, A> mk = mkTree(m);
    if (predicate.f(m.sum(acc, m.measure().f(as._1())))) {
      return P.p(none(), as._1(), some(mk.one(as._2())));
    } else {
      return P.p(some(mk.one(as._1())), as._2(), none());
    }
  }

  @Override public P2<Integer, A> lookup(F<V, Integer> o, int i) {
    final F<A, V> m = measured().measure();
    final int s1 = o.f(m.f(as._1()));
    if (i < s1) {
      return P.p(i, as._1());
    } else {
      return P.p(i - s1, as._2());
    }
  }

    @Override
    public int length() {
        return 2;
    }

  public String toString() {
    return Show.digitShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
  }

  public Stream<A> toStream() {
    return values().toStream();
  }

}
