package fj.data;

import fj.*;

import static fj.Bottom.error;
import static fj.Monoid.intAdditionMonoid;
import static fj.data.fingertrees.FingerTree.measured;

import fj.data.List.Buffer;
import fj.data.fingertrees.FingerTree;
import fj.data.fingertrees.MakeTree;
import fj.data.fingertrees.Measured;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides an immutable finite sequence, implemented as a finger tree. This structure gives O(1) access to
 * the head and tail, as well as O(log n) random access and concatenation of sequences.
 */
public final class Seq<A> implements Iterable<A> {
  private static final Measured<Integer, Object> ELEM_MEASURED = measured(intAdditionMonoid, Function.constant(1));
  private static final MakeTree<Integer, Object> MK_TREE = FingerTree.mkTree(ELEM_MEASURED);
  private static final Seq<Object> EMPTY = new Seq<>(MK_TREE.empty());

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
    return Equal.equals0(Seq.class, this, other, () -> Equal.seqEqual(Equal.anyEqual()));
  }

  /**
   * A singleton sequence.
   *
   * @param a The single element in the sequence.
   * @return A new sequence with the given element in it.
   */
  public static <A> Seq<A> single(final A a) {
    return new Seq<>(Seq.<A>mkTree().single(a));
  }

  /**
   * Constructs a sequence from the given elements.
   * @param as The elements to create the sequence from.
   * @return A sequence with the given elements.
     */
  @SafeVarargs public static <A> Seq<A> seq(final A... as) {
    return arraySeq(as);
  }

  /**
   * Constructs a sequence from the given list.
   *
   * @deprecated As of release 4.5, use {@link #listSeq(List)}
   *
   * @param list The list to create the sequence from.
   * @return A sequence with the given elements in the list.
   */
  @Deprecated
  public static <A>Seq<A> seq(final List<A> list) {
    return iterableSeq(list);
  }

  /**
   * Constructs a sequence from the given list.
   *
   * @deprecated As of release 4.5, use {@link #iterableSeq}
   *
   * @param list The list to create the sequence from.
   * @return A sequence with the elements of the list.
   */
  @Deprecated
  public static <A>Seq<A> listSeq(final List<A> list) {
    return iterableSeq(list);
  }

  /**
   * Constructs a sequence from the iterable.
   * @param i The iterable to create the sequence from.
   * @return A sequence with the elements of the iterable.
   */
  public static <A>Seq<A> iterableSeq(final Iterable<A> i) {
    Seq<A> s = empty();
    for (final A a: i) {
      s = s.snoc(a);
    }
    return s;
  }

  /**
   * Constructs a sequence from the iterator.
   * @param i The iterator to create the sequence from.
   * @return A sequence with the elements of the iterator.
   */
  public static <A>Seq<A> iteratorSeq(final Iterator<A> i) {
    return iterableSeq(() -> i);
  }

  /**
   * Constructs a sequence from the array.
   */
  @SafeVarargs
  public static <A>Seq<A> arraySeq(A... as) {
    return iterableSeq(Array.array(as));
  }

  /**
   * Constructs a sequence from the given list.
   * @param list The list to create the sequence from.
   * @return A sequence with the elements of the list.
   */
  public static <A>Seq<A> fromJavaList(final java.util.List<A> list) {
    return iterableSeq(list);
  }

  /**
   * Inserts the given element at the front of this sequence.
   *
   * @param a An element to insert at the front of this sequence.
   * @return A new sequence with the given element at the front.
   */
  public Seq<A> cons(final A a) {
    return new Seq<>(ftree.cons(a));
  }

  /**
   * Inserts the given element at the end of this sequence.
   *
   * @param a An element to insert at the end of this sequence.
   * @return A new sequence with the given element at the end.
   */
  public Seq<A> snoc(final A a) {
    return new Seq<>(ftree.snoc(a));
  }

  /**
   * The first element of this sequence. This is an O(1) operation.
   *
   * @return The first element if this sequence is nonempty, otherwise throws an error.
   */
  public A head() { return ftree.head(); }

  public Option<A> headOption() {
      return ftree.headOption();
  }

  /**
   * The last element of this sequence. This is an O(1) operation.
   *
   * @return The last element if this sequence is nonempty, otherwise throws an error.
   */
  public A last() { return ftree.last(); }

  /**
   * The sequence without the first element. This is an O(1) operation.
   *
   * @return The sequence without the first element if this sequence is nonempty, otherwise throws an error.
   */
  public Seq<A> tail() {
    return (length() == 1) ? empty() : new Seq<>(ftree.tail());
  }

  /**
   * The sequence without the last element. This is an O(1) operation.
   *
   * @return The sequence without the last element if this sequence is nonempty, otherwise throws an error.
   */
  public Seq<A> init() {
    return (length() == 1) ? empty() : new Seq<>(ftree.init());
  }

  /**
   * Converts this sequence to a Stream
   */
  public Stream<A> toStream() {
    return ftree.foldLeft((b, a) -> b.cons(a), Stream.<A>nil()).reverse();
  }

  /**
   * Converts this sequence to a List
   */
  public List<A> toList() {
    final Buffer<A> buf = Buffer.empty();
    for (final A a : this) { buf.snoc(a); }
    return buf.toList();
  }

  /**
   * Converts the sequence to a java.util.List
   */
  public java.util.List<A> toJavaList() {
    return new AbstractList<A>() {
      @Override public A get(int i) { return index(i); }
      @Override public Iterator<A> iterator() { return Seq.this.iterator(); }
      @Override public int size() { return length(); }
    };
  }

  /**
   * Returns an iterator for this seq. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this seq.
   */
  public Iterator<A> iterator() {
    return new Iterator<A>() {
      private FingerTree<Integer, A> ftree = Seq.this.ftree;

      public boolean hasNext() {
        return !ftree.isEmpty();
      }

      public A next() {
        if (ftree.isEmpty())
          throw new NoSuchElementException();
        else {
          final A a = ftree.head();
          ftree = ftree.tail();
          return a;
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
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
    return new Seq<>(ftree.append(as.ftree));
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
   * Inserts the element at the given index. This is an O(log(n)) operation.
   *
   * @param index The index of the element to return.
   * @return The sequence with the element inserted at the given index,
   * or throws an error if the index is out of bounds.
   */
  public Seq<A> insert(int index, A a) {
    final P2<Seq<A>, Seq<A>> p = split(index);
    return p._1().append(single(a)).append(p._2());
  }

  /**
   * Checks if this sequence is not empty.
   *
   * @return True if this sequence is not empty, otherwise false.
   */
  public boolean isNotEmpty() {
    return !ftree.isEmpty();
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
   * Splits this sequence into a pair of sequences at the given position. This is a O(log(n)) operation.
   *
   * @return Pair: the subsequence containing elements with indices less than <code>i</code>
   *   and the subsequence containing elements with indices greater than or equal to <code>i</code>.
   */
  public P2<Seq<A>, Seq<A>> split(final int i) {
    final P2<FingerTree<Integer, A>, FingerTree<Integer, A>> lr = ftree.split(index -> index > i);
    return P.p(new Seq<>(lr._1()), new Seq<>(lr._2()));
  }

  /**
   * Returns the element at the given index. This is an O(log(n)) operation.
   *
   * @param i The index of the element to return.
   * @return The element at the given index, or throws an error if the index is out of bounds.
   */
  public A index(final int i) {
    checkBounds(i);
    return ftree.lookup(Function.identity(), i)._2();
  }

  /**
   * Replace the element at the given index with the supplied value. This is an O(log(n)) operation.
   *
   * @param i The index of the element to update.
   * @param a The new value.
   *
   * @return The updated sequence, or throws an error if the index is out of bounds.
   */
  public Seq<A> update(final int i, final A a) {
    checkBounds(i);
    final P3<FingerTree<Integer, A>, A, FingerTree<Integer, A>> lxr = ftree.split1(index -> index > i);
    return new Seq<>(lxr._1().append(lxr._3().cons(a)));
  }

  /**
   * Delete the element at the given index. This is an O(log(n)) operation.
   *
   * @param i The index of the element to update.
   *
   * @return The updated sequence, or throws an error if the index is out of bounds.
   */
  public Seq<A> delete(final int i) {
    checkBounds(i);
    final P3<FingerTree<Integer, A>, A, FingerTree<Integer, A>> lxr = ftree.split1(index -> index > i);
    return new Seq<>(lxr._1().append(lxr._3()));
  }

  /**
   * Takes the given number of elements from the head of this sequence if they are available.
   *
   * @param n The maximum number of elements to take from this sequence.
   * @return A sequence consisting only of the first n elements of this sequence, or else the whole sequence,
   *   if it has less than n elements.
   */
  public Seq<A> take(final int n) { return split(n)._1(); }

  /**
   * Drops the given number of elements from the head of this sequence if they are available.
   *
   * @param n The number of elements to drop from this sequence.
   * @return A sequence consisting of all elements of this sequence except the first n ones, or else the empty sequence,
   *   if this sequence has less than n elements.
   */
  public Seq<A> drop(final int n) { return split(n)._2(); }

  private void checkBounds(final int i) { if (i < 0 || i >= length()) throw error("Index " + i + " is out of bounds."); }

    public <B> B foldLeft(final F2<B, A, B> f, final B z) {
        return ftree.foldLeft(f, z);
    }

    public <B> B foldRight(final F2<A, B, B> f, final B z) {
        return ftree.foldRight(f, z);
    }


  public Seq<A> filter(F<A, Boolean> f) {
    return foldLeft((acc, a) -> f.f(a) ? acc.snoc(a) : acc, empty());
  }

    @Override
    public int hashCode() {
      return Hash.seqHash(Hash.<A>anyHash()).hash(this);
    }

    public <B> Seq<B> map(F<A, B> f) {
        return new Seq<>(ftree.map(f, Seq.elemMeasured()));
    }

}
