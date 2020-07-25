package fj.data;

import fj.*;
import fj.control.Trampoline;
import fj.data.List.Buffer;
import fj.data.fingertrees.*;

import java.util.*;

import static fj.Bottom.error;
import static fj.Function.*;
import static fj.Monoid.intAdditionMonoid;
import static fj.P.p;
import static fj.data.Either.*;
import static fj.data.Option.some;
import static fj.data.Validation.success;
import static fj.data.fingertrees.FingerTree.measured;

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

  /**
   * Bind the given function across this seq.
   *
   * @param f   the given function
   * @param <B> the type of the seq value
   * @return the seq
   */
  public <B> Seq<B> bind(final F<A, Seq<B>> f) {
    return foldRight(
        (element, accumulator) -> f.f(element).append(accumulator),
        empty());
  }

  /**
   * Sequence the given seq and collect the output on the right side of an either.
   *
   * @param seq the given seq
   * @param <B> the type of the right value
   * @param <L> the type of the left value
   * @return the either
   */
  public static <L, B> Either<L, Seq<B>> sequenceEither(final Seq<Either<L, B>> seq) {
    return seq.traverseEither(identity());
  }

  /**
   * Sequence the given seq and collect the output on the left side of an either.
   *
   * @param seq the given seq
   * @param <R> the type of the right value
   * @param <B> the type of the left value
   * @return the either
   */
  public static <R, B> Either<Seq<B>, R> sequenceEitherLeft(final Seq<Either<B, R>> seq) {
    return seq.traverseEitherLeft(identity());
  }

  /**
   * Sequence the given seq and collect the output on the right side of an either.
   *
   * @param seq the given seq
   * @param <B> the type of the right value
   * @param <L> the type of the left value
   * @return the either
   */
  public static <L, B> Either<L, Seq<B>> sequenceEitherRight(final Seq<Either<L, B>> seq) {
    return seq.traverseEitherRight(identity());
  }

  /**
   * Sequence the given seq and collect the output as a function.
   *
   * @param seq the given seq
   * @param <C> the type of the input value
   * @param <B> the type of the output value
   * @return the either
   */
  public static <C, B> F<C, Seq<B>> sequenceF(final Seq<F<C, B>> seq) {
    return seq.traverseF(identity());
  }

  /**
   * Sequence the given seq and collect the output as an IO.
   *
   * @param seq the given seq
   * @param <B> the type of the IO value
   * @return the IO
   */
  public static <B> IO<Seq<B>> sequenceIO(final Seq<IO<B>> seq) {
    return seq.traverseIO(identity());
  }

  /**
   * Sequence the given seq and collect the output as a list.
   *
   * @param seq the given seq
   * @param <B> the type of the seq value
   * @return the list
   */
  public static <B> List<Seq<B>> sequenceList(final Seq<List<B>> seq) {
    return seq.traverseList(identity());
  }

  /**
   * Sequence the given seq and collect the output as an seq.
   *
   * @param seq the given seq
   * @param <B> the type of the seq value
   * @return the seq
   */
  public static <B> Option<Seq<B>> sequenceOption(final Seq<Option<B>> seq) {
    return seq.traverseOption(identity());
  }

  /**
   * Sequence the given seq and collect the output as a P1.
   *
   * @param seq the given seq
   * @param <B> the type of the P1 value
   * @return the P1
   */
  public static <B> P1<Seq<B>> sequenceP1(final Seq<P1<B>> seq) {
    return seq.traverseP1(identity());
  }

  /**
   * Sequence the given seq and collect the output as a seq.
   *
   * @param seq the given seq
   * @param <B> the type of the seq value
   * @return the seq
   */
  public static <B> Seq<Seq<B>> sequenceSeq(final Seq<Seq<B>> seq) {
    return seq.traverseSeq(identity());
  }

  /**
   * Sequence the given seq and collect the output as a set; use the given ord to order the set.
   *
   * @param ord the given ord
   * @param seq the given seq
   * @param <B> the type of the set value
   * @return the either
   */
  public static <B> Set<Seq<B>> sequenceSet(final Ord<B> ord, final Seq<Set<B>> seq) {
    return seq.traverseSet(ord, identity());
  }

  /**
   * Sequence the given seq and collect the output as a stream.
   *
   * @param seq the given seq
   * @param <B> the type of the stream value
   * @return the stream
   */
  public static <B> Stream<Seq<B>> sequenceStream(final Seq<Stream<B>> seq) {
    return seq.traverseStream(identity());
  }

  /**
   * Sequence the given seq and collect the output as a trampoline.
   *
   * @param seq the given trampoline
   * @param <B> the type of the stream value
   * @return the stream
   */
  public static <B> Trampoline<Seq<B>> sequenceTrampoline(final Seq<Trampoline<B>> seq) {
    return seq.traverseTrampoline(identity());
  }

  /**
   * Sequence the given seq and collect the output as a validation.
   *
   * @param seq the given seq
   * @param <E> the type of the failure value
   * @param <B> the type of the success value
   * @return the validation
   */
  public static <E, B> Validation<E, Seq<B>> sequenceValidation(final Seq<Validation<E, B>> seq) {
    return seq.traverseValidation(identity());
  }

  /**
   * Sequence the given seq and collect the output as a validation; use the given semigroup to reduce the errors.
   *
   * @param semigroup the given semigroup
   * @param seq       the given seq
   * @param <E>       the type of the failure value
   * @param <B>       the type of the success value
   * @return the validation
   */
  public static <E, B> Validation<E, Seq<B>> sequenceValidation(final Semigroup<E> semigroup, final Seq<Validation<E, B>> seq) {
    return seq.traverseValidation(semigroup, identity());
  }

  /**
   * Traverse this seq with the given function and collect the output on the right side of an either.
   *
   * @param f   the given function
   * @param <L> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <B, L> Either<L, Seq<B>> traverseEither(final F<A, Either<L, B>> f) {
    return traverseEitherRight(f);
  }

  /**
   * Traverse this seq with the given function and collect the output on the left side of an either.
   *
   * @param f   the given function
   * @param <R> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <R, B> Either<Seq<B>, R> traverseEitherLeft(final F<A, Either<B, R>> f) {
    return foldRight(
        (element, either) -> f.f(element).left().bind(elementInner -> either.left().map(seq -> seq.cons(elementInner))),
        left(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output on the right side of an either.
   *
   * @param f   the given function
   * @param <L> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <L, B> Either<L, Seq<B>> traverseEitherRight(final F<A, Either<L, B>> f) {
    return foldRight(
        (element, either) -> f.f(element).right().bind(elementInner -> either.right().map(seq -> seq.cons(elementInner))),
        right(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as a function.
   *
   * @param f   the given function
   * @param <C> the type of the input value
   * @param <B> the type of the output value
   * @return the function
   */
  public <C, B> F<C, Seq<B>> traverseF(final F<A, F<C, B>> f) {
    return foldRight(
        (element, fInner) -> Function.bind(f.f(element), elementInner -> andThen(fInner, seq -> seq.cons(elementInner))),
        constant(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as an IO.
   *
   * @param f   the given function
   * @param <B> the type of the IO value
   * @return the IO
   */
  public <B> IO<Seq<B>> traverseIO(final F<A, IO<B>> f) {
    return foldRight(
        (element, io) -> IOFunctions.bind(f.f(element), elementInner -> IOFunctions.map(io, seq -> seq.cons(elementInner))),
        IOFunctions.unit(empty())
    );
  }

  /**
   * Traverse this seq with the given function and collect the output as a list.
   *
   * @param f   the given function
   * @param <B> the type of the list value
   * @return the list
   */
  public <B> List<Seq<B>> traverseList(final F<A, List<B>> f) {
    return foldRight(
        (element, list) -> f.f(element).bind(elementInner -> list.map(seq -> seq.cons(elementInner))),
        List.single(empty()));
  }

  /**
   * Traverses through the Seq with the given function
   *
   * @param f The function that produces Option value
   * @return none if applying f returns none to any element of the seq or f mapped seq in some .
   */
  public <B> Option<Seq<B>> traverseOption(final F<A, Option<B>> f) {
    return foldRight(
        (element, option) -> f.f(element).bind(elementInner -> option.map(seq -> seq.cons(elementInner))),
        some(empty())
    );
  }

  /**
   * Traverse this seq with the given function and collect the output as a p1.
   *
   * @param f   the given function
   * @param <B> the type of the p1 value
   * @return the p1
   */
  public <B> P1<Seq<B>> traverseP1(final F<A, P1<B>> f) {
    return foldRight(
        (element, p1) -> f.f(element).bind(elementInner -> p1.map(seq -> seq.cons(elementInner))),
        p(empty())
    );
  }

  /**
   * Traverse this seq with the given function and collect the output as a seq.
   *
   * @param f   the given function
   * @param <B> the type of the seq value
   * @return the seq
   */
  public <B> Seq<Seq<B>> traverseSeq(final F<A, Seq<B>> f) {
    return foldRight(
        (element, seq) -> f.f(element).bind(elementInner -> seq.map(seqInner -> seqInner.cons(elementInner))),
        single(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as a set; use the given ord to order the set.
   *
   * @param ord the given ord
   * @param f   the given function
   * @param <B> the type of the set value
   * @return the set
   */
  public <B> Set<Seq<B>> traverseSet(final Ord<B> ord, final F<A, Set<B>> f) {
    final Ord<Seq<B>> seqOrd = Ord.seqOrd(ord);
    return foldRight(
        (element, set) -> f.f(element).bind(seqOrd, elementInner -> set.map(seqOrd, seq -> seq.cons(elementInner))),
        Set.single(seqOrd, empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as a stream.
   *
   * @param f   the given function
   * @param <B> the type of the stream value
   * @return the stream
   */
  public <B> Stream<Seq<B>> traverseStream(final F<A, Stream<B>> f) {
    return foldRight(
        (element, stream) -> f.f(element).bind(elementInner -> stream.map(seq -> seq.cons(elementInner))),
        Stream.single(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as a trampoline.
   *
   * @param f   the given function
   * @param <B> the type of the trampoline value
   * @return the trampoline
   */
  public <B> Trampoline<Seq<B>> traverseTrampoline(final F<A, Trampoline<B>> f) {
    return foldRight(
        (element, trampoline) -> f.f(element).bind(elementInner -> trampoline.map(seq -> seq.cons(elementInner))),
        Trampoline.pure(empty()));
  }

  /**
   * Traverse this seq with the given function and collect the output as a validation.
   *
   * @param f   the given function
   * @param <E> the type of the failure value
   * @param <B> the type of the success value
   * @return the validation
   */
  public <E, B> Validation<E, Seq<B>> traverseValidation(final F<A, Validation<E, B>> f) {
    return foldRight(
        (element, validation) -> f.f(element).bind(elementInner -> validation.map(seq -> seq.cons(elementInner))),
        success(empty())
    );
  }

  /**
   * Traverse this seq with the given function and collect the output as a validation; use the given semigroup to reduce the errors.
   *
   * @param semigroup the given semigroup
   * @param f         the given function
   * @param <E>       the type of the failure value
   * @param <B>       the type of the success value
   * @return the validation
   */
  public <E, B> Validation<E, Seq<B>> traverseValidation(final Semigroup<E> semigroup, final F<A, Validation<E, B>> f) {
    return foldRight(
        (element, validation) -> f.f(element).map(Seq::single).accumulate(semigroup, validation, Seq::append),
        success(empty())
    );
  }
}
