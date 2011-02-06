package fj.data;

import fj.Function;
import static fj.Bottom.error;
import static fj.Monoid.intAdditionMonoid;
import static fj.data.fingertrees.FingerTree.measured;

import fj.data.fingertrees.FingerTree;
import fj.data.fingertrees.MakeTree;
import fj.data.fingertrees.Measured;

/**
 * Provides an immutable finite sequence, implemented as a finger tree. This structure gives O(1) access to
 * the head and tail, as well as O(log n) random access and concatenation of sequences.
 */
public final class Seq<A> {
  private static <A> MakeTree<Integer, A> mkTree() {
    return FingerTree.mkTree(Seq.<A>elemMeasured());
  }

  private final FingerTree<Integer, A> ftree;

  private Seq(final FingerTree<Integer, A> ftree) {
    this.ftree = ftree;
  }

  private static <A> Measured<Integer, A> elemMeasured() {
    return measured(intAdditionMonoid, Function.<A, Integer>constant(1));
  }

  /**
   * The empty sequence.
   *
   * @return A sequence with no elements.
   */
  public static <A> Seq<A> empty() {
    return new Seq<A>(Seq.<A>mkTree().empty());
  }

  /**
   * A singleton sequence.
   *
   * @param a The single element in the sequence.
   * @return A new sequence with the given element in it.
   */
  public static <A> Seq<A> single(final A a) {
    return new Seq<A>(Seq.<A>mkTree().single(a));
  }

  /**
   * Inserts the given element at the front of this sequence.
   *
   * @param a An element to insert at the front of this sequence.
   * @return A new sequence with the given element at the front.
   */
  public Seq<A> cons(final A a) {
    return new Seq<A>(ftree.cons(a));
  }

  /**
   * Inserts the given element at the end of this sequence.
   *
   * @param a An element to insert at the end of this sequence.
   * @return A new sequence with the given element at the end.
   */
  public Seq<A> snoc(final A a) {
    return new Seq<A>(ftree.snoc(a));
  }

  /**
   * Appends the given sequence to this sequence.
   *
   * @param as A sequence to append to this one.
   * @return A new sequence with the given sequence appended to this one.
   */
  public Seq<A> append(final Seq<A> as) {
    return new Seq<A>(ftree.append(as.ftree));
  }

  /**
   * Checks if this is the empty sequence.
   *
   * @return True if this sequence is empty, otherwise false.
   */
  public boolean isEmpty() {
    return ftree.isEmpty();
  }

  /**
   * Returns the number of elements in this sequence.
   *
   * @return the number of elements in this sequence.
   */
  public int length() {
    return ftree.measure();
  }

  /**
   * Returns the element at the given index.
   *
   * @param i The index of the element to return.
   * @return The element at the given index, or throws an error if the index is out of bounds.
   */
  public A index(final int i) {
    if (i < 0 || i >= length())
      throw error("Index " + i + "out of bounds.");
    return ftree.lookup(Function.<Integer>identity(), i)._2();
  }
}
