package fj.data.fingertrees;

import fj.F;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.Option;
import fj.data.Stream;

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
    return curry((b, node) -> node.foldLeft(bff, b));
  }

  public static <V, A, B> F<B, F<Node<V, A>, B>> foldRight_(final F<A, F<B, B>> aff) {
    return curry((b, node) -> node.foldRight(aff, b));
  }

  public final <B> Node<V, B> map(final F<A, B> f, final Measured<V, B> m) {
    return match(
        node2 -> new Node2<>(m, node2.toVector().map(f)),
        node3 -> new Node3<>(m, node3.toVector().map(f))
    );
  }

  public static <V, A, B> F<Node<V, A>, Node<V, B>> liftM(final F<A, B> f, final Measured<V, B> m) {
    return node -> node.map(f, m);
  }

  public abstract Digit<V, A> toDigit();

  Node(final Measured<V, A> m, final V measure) {
    this.m = m;
    this.measure = measure;
  }

  public final V measure() {
    return measure;
  }

  final Measured<V, A> measured() {
    return m;
  }

  abstract P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> split1(final F<V, Boolean> predicate, final V acc);

  public abstract P2<Integer, A> lookup(final F<V, Integer> o, final int i);

  public abstract <B> B match(final F<Node2<V, A>, B> n2, final F<Node3<V, A>, B> n3);

  public abstract int length();

  public abstract Stream<A> toStream();

}
