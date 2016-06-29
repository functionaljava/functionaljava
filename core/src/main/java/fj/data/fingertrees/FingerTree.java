package fj.data.fingertrees;

import fj.*;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

import static fj.Monoid.intAdditionMonoid;
import static fj.Monoid.intMaxMonoid;
import static fj.data.Stream.nil;

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

    public final <B> B foldRight(final F2<A, B, B> f, final B z) {
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

    public final <B> B foldLeft(final F2<B, A, B> f, final B z) {
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

    public final <B> FingerTree<V, A> filter(final F<A, Boolean> f) {
        FingerTree<V, A> tree = new Empty<>(m);
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
  
  public final Measured<V, A> measured() {
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
    return new MakeTree<>(m);
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
   * The first element of this tree. This is an O(1) operation.
   *
   * @return The first element if this tree is nonempty, otherwise throws an error.
   */
  public abstract A head();

  public final Option<A> headOption() {
      return isEmpty() ? Option.none() : Option.some(head());
  }

  /**
   * Performs a reduction on this finger tree using the given arguments.
   *
   * @param nil  The value to return if this finger tree is empty.
   * @param cons The function to apply to the head and tail of this finger tree  if it is not empty.
   * @return A reduction on this finger tree.
   */
  public final <B> B uncons(B nil, F2<A, FingerTree<V, A>, B> cons) {
    return isEmpty() ? nil : cons.f(head(), tail());
  }


  /**
   * The last element of this tree. This is an O(1) operation.
   *
   * @return The last element if this tree is nonempty, otherwise throws an error.
   */
  public abstract A last();

  /**
   * The tree without the first element. This is an O(1) operation.
   *
   * @return The tree without the first element if this tree is nonempty, otherwise throws an error.
   */
  public abstract FingerTree<V, A> tail();

  /**
   * The tree without the last element. This is an O(1) operation.
   *
   * @return The tree without the last element if this tree is nonempty, otherwise throws an error.
   */
  public abstract FingerTree<V, A> init();

  /**
   * Appends one finger tree to another.
   *
   * @param t A finger tree to append to this one.
   * @return A new finger tree which is a concatenation of this tree and the given tree.
   */
  public abstract FingerTree<V, A> append(final FingerTree<V, A> t);

  /**
   * Splits this tree into a pair of subtrees at the point where the given predicate, based on the measure,
   * changes from <code>false</code> to <code>true</code>. This is a O(log(n)) operation.
   *
   * @return Pair: the subtree containing elements before the point where <code>pred</code> first holds and the subtree
   *   containing element at and after the point where <code>pred</code> first holds. Empty if <code>pred</code> never holds.
   */
  public final P2<FingerTree<V, A>, FingerTree<V, A>> split(final F<V, Boolean> predicate) {
    if (!isEmpty() && predicate.f(measure())) {
      final P3<FingerTree<V, A>, A, FingerTree<V, A>> lxr = split1(predicate);
      return P.p(lxr._1(), lxr._3().cons(lxr._2()));
    } else {
      return P.p(this, mkTree(m).empty());
    }
  }

  /**
   * Like <code>split</code>, but returns the element where <code>pred</code> first holds separately.
   *
   * Throws an error if the tree is empty.
   */
  public final P3<FingerTree<V, A>, A, FingerTree<V, A>> split1(final F<V, Boolean> predicate) {
    return split1(predicate, measured().zero());
  }

  abstract P3<FingerTree<V, A>, A, FingerTree<V, A>> split1(final F<V, Boolean> predicate, final V acc);

  public abstract P2<Integer, A> lookup(final F<V, Integer> o, final int i);

    public abstract int length();

    public static <A> FingerTree<Integer, A> emptyIntAddition() {
      return empty(intAdditionMonoid, Function.constant(1));
    }

  /**
   * Creates an empty finger tree with elements of type A and node annotations
   * of type V.
   *
   * @param m A monoid to combine node annotations
   * @param f Function to convert node element to annotation.
   * @return An empty finger tree.
   */
  public static <V, A> FingerTree<V, A> empty(Monoid<V> m, F<A, V> f) {
    return FingerTree.mkTree(measured(m, f)).empty();
  }

  /**
   * Returns a finger tree which combines the integer node annotations with the
   * maximum function.  A priority queue with integer priorities.
   */
  public static <A> FingerTree<Integer, P2<Integer, A>> emptyIntMax() {
    return empty(intMaxMonoid, (P2<Integer, A> p) -> p._1());
  }

  public abstract Stream<A> toStream();

}
