package fj.data.fingertrees;

import fj.data.vector.V2;
import fj.data.vector.V3;

import static fj.data.vector.V.v;

/**
 * A builder of trees and tree components, supplied with a particular monoid and measuring function.
 */
public final class MakeTree<V, A> {
  private final Measured<V, A> m;
  private final Empty<V, A> empty;

  MakeTree(final Measured<V, A> m) {
    this.m = m;
    this.empty = new Empty<V, A>(m);
  }

  // Tree constructors

  /**
   * Constructs an empty tree.
   *
   * @return The empty tree.
   */
  public FingerTree<V, A> empty() {
    return empty;
  }

  /**
   * Constructs a singleton tree.
   *
   * @param a A single element for the tree.
   * @return A tree with the given value as the single element.
   */
  public FingerTree<V, A> single(final A a) {
    return new Single<V, A>(m, a);
  }

  /**
   * Constructs a deep tree. This structure consists of two digits, of 1 to 4 elements each, on the left and right,
   * with the rest of the tree in the middle.
   *
   * @param prefix The leftmost elements of the tree.
   * @param middle The subtree, which is a Finger Tree of 2-3 nodes.
   * @param suffix The rightmost elements of the tree.
   * @return A new finger tree with the given prefix, suffix, and middle.
   */
  public FingerTree<V, A> deep(final Digit<V, A> prefix, final FingerTree<V, Node<V, A>> middle,
                               final Digit<V, A> suffix) {
    return deep(m.sum(prefix.measure(), m.sum(middle.measure(), suffix.measure())), prefix, middle, suffix);
  }

  /**
   * Constructs a deep tree with the given annotation value.
   *
   * @param v      The value with which to annotate this tree.
   * @param prefix The leftmost elements of the tree.
   * @param middle The subtree, which is a Finger Tree of 2-3 nodes.
   * @param suffix The rightmost elements of the tree.
   * @return A new finger tree with the given prefix, suffix, and middle, and annotated with the given value.
   */
  public FingerTree<V, A> deep(final V v, final Digit<V, A> prefix, final FingerTree<V, Node<V, A>> middle,
                               final Digit<V, A> suffix) {
    return new Deep<V, A>(m, v, prefix, middle, suffix);
  }

  // Digit constructors

  /**
   * A digit of one element.
   *
   * @param a The element of the digit.
   * @return A digit of the given element.
   */
  public One<V, A> one(final A a) {
    return new One<V, A>(m, a);
  }

  /**
   * A digit of two elements.
   *
   * @param a The first element of the digit.
   * @param b The second element of the digit.
   * @return A digit of the given elements.
   */
  public Two<V, A> two(final A a, final A b) {
    return new Two<V, A>(m, v(a, b));
  }

  /**
   * A digit of three elements.
   *
   * @param a The first element of the digit.
   * @param b The second element of the digit.
   * @param c The third element of the digit.
   * @return A digit of the given elements.
   */
  public Three<V, A> three(final A a, final A b, final A c) {
    return new Three<V, A>(m, v(a, b, c));
  }

  /**
   * A digit of four elements.
   *
   * @param a The first element of the digit.
   * @param b The second element of the digit.
   * @param c The third element of the digit.
   * @param d The fifth element of the digit.
   * @return A digit of the given elements.
   */
  public Four<V, A> four(final A a, final A b, final A c, final A d) {
    return new Four<V, A>(m, v(a, b, c, d));
  }

  // Node constructors

  /**
   * A binary tree node.
   *
   * @param a The left child of the node.
   * @param b The right child of the node.
   * @return A new binary tree node.
   */
  public Node2<V, A> node2(final A a, final A b) {
    return new Node2<V, A>(m, v(a, b));
  }

  /**
   * A trinary tree node.
   *
   * @param a The left child of the node.
   * @param b The middle child of the node.
   * @param c The right child of the node.
   * @return A new trinary tree node.
   */
  public Node3<V, A> node3(final A a, final A b, final A c) {
    return new Node3<V, A>(m, v(a, b, c));
  }

  /**
   * A binary tree node
   *
   * @param v A vector of the node's elements.
   * @return A new binary tree node.
   */
  public Node2<V, A> node2(final V2<A> v) {
    return new Node2<V, A>(m, v);
  }

  /**
   * A trinary tree node
   *
   * @param v A vector of the node's elements.
   * @return A new trinary tree node.
   */
  public Node3<V, A> node3(final V3<A> v) {
    return new Node3<V, A>(m, v);
  }

}
