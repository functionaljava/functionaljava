package fj.data;

import fj.*;

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
  private static final Measured<Integer, Object> ELEM_MEASURED = measured(intAdditionMonoid, Function.constant(1));
  private static final MakeTree<Integer, Object> MK_TREE = FingerTree.mkTree(ELEM_MEASURED);
  private static final Seq<Object> EMPTY = new Seq<Object>(MK_TREE.empty());

  @SuppressWarnings("unchecked")
  private static <A> MakeTree<Integer, A> mkTree() {
    return (MakeTree<Integer, A>) MK_TREE;
  }

  private final FingerTree<Integer, A> ftree;

  private Seq(final FingerTree<Integer, A> ftree) {
    this.ftree = ftree;
  }

  @SuppressWarnings("unchecked")
  private static <A> Measured<Integer, A> elemMeasured() {
    return (Measured<Integer, A>) ELEM_MEASURED;
  }

  /**
   * The empty sequence.
   *
   * @return A sequence with no elements.
   */
  @SuppressWarnings("unchecked")
  public static <A> Seq<A> empty() {
    return (Seq<A>) EMPTY;
  }

  @Override
  public boolean equals(Object other) {

    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.seqEqual(Equal.<A>anyEqual()).eq(this, (Seq<A>) other)));
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

  public static <A>Seq<A> seq(final A... as) {
    return seq(List.list(as));
  }

  public static <A>Seq<A> seq(final List<A> list) {
    return list.foldLeft((b, a) -> b.snoc(a), Seq.<A>empty());
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

  public Stream<A> toStream() {
    return ftree.foldLeft((b, a) -> b.cons(a), Stream.<A>nil()).reverse();
  }

  @Override
  public String toString() {
    return Show.seqShow(Show.<A>anyShow()).showS(this);
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

    public <B> B foldLeft(final F2<B, A, B> f, final B z) {
        return ftree.foldLeft(f, z);
    }

    public <B> B foldRight(final F2<A, B, B> f, final B z) {
        return ftree.foldRight(f, z);
    }

    @Override
    public int hashCode() {
      return Hash.seqHash(Hash.<A>anyHash()).hash(this);
    }

    public <B> Seq<B> map(F<A, B> f) {
        return new Seq<B>(ftree.map(f, Seq.<B>elemMeasured()));
    }

}
