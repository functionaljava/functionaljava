package fj.data.fingertrees;

import fj.*;
import fj.data.Seq;

/**
 * Provides 2-3 finger trees, a functional representation of persistent sequences supporting access to the ends in
 * amortized O(1) time. Concatenation and splitting time is O(log n) in the size of the smaller piece.
 * A general purpose data structure that can serve as a sequence, priority queue, search tree, priority search queue
 * and more.
 * <p/>
 * This class serves as a datastructure construction kit, rather than a datastructure in its own right. By supplying
 * a monoid, a measurement function, insertion, deletion, and so forth, any purely functional datastructure can be
 * emulated. See {@link Seq} for an example.
 * <p/>
 * Based on "Finger trees: a simple general-purpose data structure", by Ralf Hinze and Ross Paterson.
 *
 * @param <V> The monoidal type with which to annotate nodes.
 * @param <A> The type of the tree's elements.
 */
public abstract class FingerTree<V, A> {
  private final Measured<V, A> m;

  /**
   * Folds the tree to the right with the given function and the given initial element.
   *
   * @param f A function with which to fold the tree.
   * @param z An initial element to apply to the fold.
   * @return A reduction of this tree by applying the given function, associating to the right.
   */
  public abstract <B> B foldRight(final F<A, F<B, B>> f, final B z);

    public <B> B foldRight(final F2<A, B, B> f, final B z) {
        return foldRight(F2Functions.curry(f), z);
    }

  /**
   * Folds the tree to the right with the given function.
   *
   * @param f A function with which to fold the tree.
   * @return A reduction of this tree by applying the given function, associating to the right.
   */
  public abstract A reduceRight(final F<A, F<A, A>> f);

  /**
   * Folds the tree to the left with the given function and the given initial element.
   *
   * @param f A function with which to fold the tree.
   * @param z An initial element to apply to the fold.
   * @return A reduction of this tree by applying the given function, associating to the left.
   */
  public abstract <B> B foldLeft(final F<B, F<A, B>> f, final B z);

    public <B> B foldLeft(final F2<B, A, B> f, final B z) {
        return foldLeft(F2Functions.curry(f), z);
    }

  /**
   * Folds the tree to the left with the given function.
   *
   * @param f A function with which to fold the tree.
   * @return A reduction of this tree by applying the given function, associating to the right.
   */
  public abstract A reduceLeft(final F<A, F<A, A>> f);

  /**
   * Maps the given function across this tree, measuring with the given Measured instance.
   *
   * @param f A function to map across the values of this tree.
   * @param m A measuring with which to annotate the tree.
   * @return A new tree with the same structure as this tree, with each element transformed by the given function,
   *         and nodes annotated according to the given measuring.
   */
  public abstract <B> FingerTree<V, B> map(final F<A, B> f, final Measured<V, B> m);

    public <B> FingerTree<V, A> filter(final F<A, Boolean> f) {
        FingerTree<V, A> tree = new Empty<V, A>(m);
        return foldLeft((acc, a) -> f.f(a) ? acc.snoc(a) : acc, tree);
    }

  /**
   * Returns the sum of this tree's annotations.
   *
   * @return the sum of this tree's annotations.
   */
  public abstract V measure();

  /**
   * Indicates whether this tree is empty.
   *
   * @return true if this tree is the empty tree, otherwise false.
   */
  public final boolean isEmpty() {
    return this instanceof Empty;
  }

  Measured<V, A> measured() {
    return m;
  }

  /**
   * Provides pattern matching on trees. This is the Church encoding of the FingerTree datatype.
   *
   * @param empty  The function to apply to this empty tree.
   * @param single A function to apply if this tree contains a single element.
   * @param deep   A function to apply if this tree contains more than one element.
   * @return The result of the function that matches this tree structurally, applied to this tree.
   */
  public abstract <B> B match(final F<Empty<V, A>, B> empty, final F<Single<V, A>, B> single,
                              final F<Deep<V, A>, B> deep);

  FingerTree(final Measured<V, A> m) {
    this.m = m;
  }

  /**
   * Constructs a Measured instance for the element type, given a monoid and a measuring function.
   *
   * @param monoid  A monoid for the measures.
   * @param measure A function with which to measure element values.
   * @return A Measured instance for the given element type, that uses the given monoid and measuring function.
   */
  public static <V, A> Measured<V, A> measured(final Monoid<V> monoid, final F<A, V> measure) {
    return Measured.measured(monoid, measure);
  }

  /**
   * Returns a builder of trees and tree components that annotates them using the given Measured instance.
   *
   * @param m A Measured instance with which to annotate trees, digits, and nodes.
   * @return A builder of trees and tree components that annotates them using the given Measured instance.
   */
  public static <V, A> MakeTree<V, A> mkTree(final Measured<V, A> m) {
    return new MakeTree<V, A>(m);
  }

  /**
   * Adds the given element to this tree as the first element.
   *
   * @param a The element to add to the front of this tree.
   * @return A new tree with the given element at the front.
   */
  public abstract FingerTree<V, A> cons(final A a);

  /**
   * Adds the given element to this tree as the last element.
   *
   * @param a The element to add to the end of this tree.
   * @return A new tree with the given element at the end.
   */
  public abstract FingerTree<V, A> snoc(final A a);

  /**
   * Appends one finger tree to another.
   *
   * @param t A finger tree to append to this one.
   * @return A new finger tree which is a concatenation of this tree and the given tree.
   */
  public abstract FingerTree<V, A> append(final FingerTree<V, A> t);

  public abstract P2<Integer, A> lookup(final F<V, Integer> o, final int i);
}
