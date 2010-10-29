package fj.data.fingertrees;

import fj.F;
import fj.F2;
import fj.P2;
import static fj.Function.curry;

/**
 * An inner node of the 2-3 tree.
 */
public abstract class Node<V, A> {
  private final Measured<V, A> m;
  private final V measure;

  public abstract <B> B foldRight(final F<A, F<B, B>> f, final B z);

  public abstract <B> B foldLeft(final F<B, F<A, B>> f, final B z);

  public static <V, A, B> F<B, F<Node<V, A>, B>> foldLeft_(final F<B, F<A, B>> bff) {
    return curry(new F2<B, Node<V, A>, B>() {
      public B f(final B b, final Node<V, A> node) { return node.foldLeft(bff, b); }
    });
  }

  public static <V, A, B> F<B, F<Node<V, A>, B>> foldRight_(final F<A, F<B, B>> aff) {
    return curry(new F2<B, Node<V, A>, B>() {
      public B f(final B b, final Node<V, A> node) { return node.foldRight(aff, b); }
    });
  }

  public final <B> Node<V, B> map(final F<A, B> f, final Measured<V, B> m) {
    return match(new F<Node2<V, A>, Node<V, B>>() {
      public Node<V, B> f(final Node2<V, A> node2) {
        return new Node2<V, B>(m, node2.toVector().map(f));
      }
    }, new F<Node3<V, A>, Node<V, B>>() {
      public Node<V, B> f(final Node3<V, A> node3) {
        return new Node3<V, B>(m, node3.toVector().map(f));
      }
    });
  }

  public static <V, A, B> F<Node<V, A>, Node<V, B>> liftM(final F<A, B> f, final Measured<V, B> m) {
    return new F<Node<V, A>, Node<V, B>>() {
      public Node<V, B> f(final Node<V, A> node) {
        return node.map(f, m);
      }
    };
  }

  public abstract Digit<V, A> toDigit();

  Node(final Measured<V, A> m, final V measure) {
    this.m = m;
    this.measure = measure;
  }

  public final V measure() {
    return measure;
  }

  Measured<V, A> measured() {
    return m;
  }

  public abstract P2<Integer, A> lookup(final F<V, Integer> o, final int i);

  public abstract <B> B match(final F<Node2<V, A>, B> n2, final F<Node3<V, A>, B> n3);
}
