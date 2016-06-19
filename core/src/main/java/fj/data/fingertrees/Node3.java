package fj.data.fingertrees;

import fj.P;
import fj.P3;
import fj.Show;
import fj.data.Option;
import fj.data.Stream;
import fj.data.vector.V3;
import fj.F;
import fj.P2;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.fingertrees.FingerTree.mkTree;

/**
 * A three-element inner tree node.
 */
public final class Node3<V, A> extends Node<V, A> {
  private final V3<A> as;

  Node3(final Measured<V, A> m, final V3<A> as) {
    super(m, m.sum(m.measure(as._1()), m.sum(m.measure(as._2()), m.measure(as._3()))));
    this.as = as;
  }

  public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(z)));
  }

  public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return bff.f(bff.f(bff.f(z).f(as._1())).f(as._2())).f(as._3());
  }

  public <B> B match(final F<Node2<V, A>, B> n2, final F<Node3<V, A>, B> n3) {
    return n3.f(this);
  }

    @Override
    public int length() {
        return 3;
    }

    public Digit<V, A> toDigit() {
    return new Three<>(measured(), as);
  }

  P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> split1(final F<V, Boolean> predicate, final V acc) {
    final Measured<V, A> m = measured();
    final MakeTree<V, A> mk = mkTree(m);
    final V acc1 = m.sum(acc, m.measure().f(as._1()));
    if (predicate.f(acc1)) {
      return P.p(none(), as._1(), some(mk.two(as._2(), as._3())));
    } else if (predicate.f(m.sum(acc1, m.measure().f(as._2())))) {
      return P.p(some(mk.one(as._1())), as._2(), some(mk.one(as._3())));
    } else {
      return P.p(some(mk.two(as._1(), as._2())), as._3(), none());
    }
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    final F<A, V> m = measured().measure();
    final int s1 = o.f(m.f(as._1()));
    if (i < s1) {
      return P.p(i, as._1());
    } else {
      final int s2 = s1 + o.f(m.f(as._2()));
      if (i < s2) {
        return P.p(i - s1, as._2());
      } else {
        return P.p(i - s2, as._3());
      }
    }
  }

  public V3<A> toVector() {
    return as;
  }

  public String toString() {
    return Show.nodeShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
  }

  public Stream<A> toStream() {
    return as.toStream();
  }

}
