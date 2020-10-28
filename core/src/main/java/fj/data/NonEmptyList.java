package fj.data;

import fj.*;
import fj.control.Trampoline;

import java.util.Collection;
import java.util.Iterator;

import static fj.Function.*;
import static fj.data.Option.some;
import static fj.data.Option.somes;

/**
 * Provides an in-memory, immutable, singly linked list with total <code>head</code> and <code>tail</code>.
 *
 * @version %build.number%
 */
public final class NonEmptyList<A> implements Iterable<A> {
  /**
   * Returns an iterator for this non-empty list. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this non-empty list.
   */

  public Iterator<A> iterator() {
    return toCollection().iterator();
  }

  private final A head;

  private final List<A> tail;

  /**
   * The first element of this linked list.
   */
  public A head() {
    return head;
  }

  /**
   * This list without the first element.
   */
  public List<A> tail() {
    return tail;
  }

  public List<A> init()
  {
    return toList().init();
  }

  public A last()
  {
    return toList().last();
  }

  private NonEmptyList(final A head, final List<A> tail) {
    this.head = head;
    this.tail = tail;
  }

  /**
   * Prepend the given value to this list.
   *
   * @param a The value to prepend.
   * @return A non-empty list with an extra element.
   */
  public NonEmptyList<A> cons(final A a) {
    return nel(a, tail.cons(head));
  }

  /**
   * Appends (snoc) the given element to this non empty list to produce a new non empty list. O(n).
   *
   * @param a The element to append to this non empty list.
   * @return A new non empty list with the given element appended.
   */
  public NonEmptyList<A> snoc(final A a) {
    return nel(head, tail.snoc(a));
  }

  /**
   * The length of this list.
   *
   * @return The length of this list.
   */
  public int length() {
    return 1 + tail.length();
  }

  /**
   * Appends the given list to this list.
   *
   * @param as The list to append.
   * @return A new list with the given list appended.
   */
  public NonEmptyList<A> append(final List<A> as) {
    return nel(head, tail.append(as));
  }

  /**
   * Appends the given list to this list.
   *
   * @param as The list to append.
   * @return A new list with the given list appended.
   */
  public NonEmptyList<A> append(final NonEmptyList<A> as) {
    final List.Buffer<A> b = new List.Buffer<>();
    b.append(tail);
    b.snoc(as.head);
    b.append(as.tail);
    final List<A> bb = b.toList();
    return nel(head, bb);
  }

  /**
   * Maps the given function across this list.
   *
   * @param f The function to map across this list.
   * @return A new list after the given function has been applied to each element.
   */
  public <B> NonEmptyList<B> map(final F<A, B> f) {
    return nel(f.f(head), tail.map(f));
  }

  /**
   * Binds the given function across each element of this list with a final join.
   *
   * @param f The function to apply to each element of this list.
   * @return A new list after performing the map, then final join.
   */
  public <B> NonEmptyList<B> bind(final F<A, NonEmptyList<B>> f) {
    final List.Buffer<B> b = new List.Buffer<>();
    final NonEmptyList<B> p = f.f(head);
    b.snoc(p.head);
    b.append(p.tail);
    tail.foreachDoEffect(a -> {
      final NonEmptyList<B> p1 = f.f(a);
      b.snoc(p1.head);
      b.append(p1.tail);
    });
    final List<B> bb = b.toList();
    return nel(bb.head(), bb.tail());
  }

  /**
   * Returns a NonEmptyList of the sublists of this list.
   *
   * @return a NonEmptyList of the sublists of this list.
   */
  public NonEmptyList<NonEmptyList<A>> sublists() {
    return fromList(
            somes(toList().toStream().substreams()
                    .map(F1Functions.o(NonEmptyList::fromList, Conversions.Stream_List())).toList())).some();
  }

  /**
   * Returns a NonEmptyList of the tails of this list. A list is considered a tail of itself for the purpose of this
   * function (Comonad pattern).
   *
   * @return A NonEmptyList of the tails of this list.
   */
  public NonEmptyList<NonEmptyList<A>> tails() {
    return fromList(somes(toList().tails().map(NonEmptyList::fromList))).some();
  }

  /**
   * Maps the given function across the tails of this list (comonad pattern).
   *
   * @param f The function to map across the tails of this list.
   * @return The results of applying the given function to the tails of this list, as a NonEmptyList.
   */
  public <B> NonEmptyList<B> mapTails(final F<NonEmptyList<A>, B> f) {
    return tails().map(f);
  }

  /**
   * Intersperses the given argument between each element of this non empty list.
   *
   * @param a The separator to intersperse in this non empty list.
   * @return A non empty list with the given separator interspersed.
   */
  public NonEmptyList<A> intersperse(final A a) {
    final List<A> list = toList().intersperse(a);
    return nel(list.head(), list.tail());
  }

  /**
   * Reverse this non empty list in constant stack space.
   *
   * @return A new non empty list with the elements in reverse order.
   */
  public NonEmptyList<A> reverse() {
    final List<A> list = toList().reverse();
    return nel(list.head(), list.tail());
  }

  /**
   * Sorts this non empty list using the given order over elements using a <em>merge sort</em> algorithm.
   *
   * @param o The order over the elements of this non empty list.
   * @return A sorted non empty list according to the given order.
   */
  public NonEmptyList<A> sort(final Ord<A> o) {
    final List<A> list = toList().sort(o);
    return nel(list.head(), list.tail());
  }


  /**
   * Returns the minimum element in this non empty list according to the given ordering.
   *
   * @param o An ordering for the elements of this non empty list.
   * @return The minimum element in this list according to the given ordering.
   */
  public final A minimum(final Ord<A> o) {
    return foldLeft1(o::min);
  }

  /**
   * Returns the maximum element in this non empty list according to the given ordering.
   *
   * @param o An ordering for the elements of this non empty list.
   * @return The maximum element in this list according to the given ordering.
   */
  public final A maximum(final Ord<A> o) {
    return foldLeft1(o::max);
  }


  /**
   * Zips this non empty list with the given non empty list to produce a list of pairs. If this list and the given list
   * have different lengths, then the longer list is normalised so this function never fails.
   *
   * @param bs The non empty list to zip this non empty list with.
   * @return A new non empty list with a length the same as the shortest of this list and the given list.
   */
  public <B> NonEmptyList<P2<A, B>> zip(final NonEmptyList<B> bs) {
    final List<P2<A, B>> list = toList().zip(bs.toList());
    return nel(list.head(), list.tail());
  }

  /**
   * Zips this non empty list with the index of its element as a pair.
   *
   * @return A new non empty list with the same length as this list.
   */
  public NonEmptyList<P2<A, Integer>> zipIndex() {
    final List<P2<A, Integer>> list = toList().zipIndex();
    return nel(list.head(), list.tail());
  }

  /**
   * Zips this non empty list with the given non empty list using the given function to produce a new list. If this list
   * and the given list have different lengths, then the longer list is normalised so this function
   * never fails.
   *
   * @param bs The non empty list to zip this non empty list with.
   * @param f  The function to zip this non empty list and the given non empty list with.
   * @return A new non empty list with a length the same as the shortest of this list and the given list.
   */
  public <B, C> NonEmptyList<C> zipWith(final List<B> bs, final F<A, F<B, C>> f) {
    final List<C> list = toList().zipWith(bs, f);
    return nel(list.head(), list.tail());
  }

  /**
   * Zips this non empty list with the given non empty list using the given function to produce a new list. If this list
   * and the given list have different lengths, then the longer list is normalised so this function
   * never fails.
   *
   * @param bs The non empty list to zip this non empty list with.
   * @param f  The function to zip this non empty list and the given non empty list with.
   * @return A new non empty list with a length the same as the shortest of this list and the given list.
   */
  public <B, C> NonEmptyList<C> zipWith(final List<B> bs, final F2<A, B, C> f) {
    final List<C> list = toList().zipWith(bs, f);
    return nel(list.head(), list.tail());
  }

  /**
   * Transforms a non empty list of pairs into a non empty list of first components and
   * a non empty list of second components.
   *
   * @param xs The non empty list of pairs to transform.
   * @return A non empty list of first components and a non empty list of second components.
   */
  public static <A, B> P2<NonEmptyList<A>, NonEmptyList<B>> unzip(final NonEmptyList<P2<A, B>> xs) {
    final P2<List<A>, List<B>> p = List.unzip(xs.toList());
    return P.p(nel(p._1().head(), p._1().tail()), nel(p._2().head(), p._2().tail()));
  }

  /**
   * Returns a <code>List</code> projection of this list.
   *
   * @return A <code>List</code> projection of this list.
   */
  public List<A> toList() {
    return tail.cons(head);
  }

  /**
   * Projects an immutable collection of this non-empty list.
   *
   * @return An immutable collection of this non-empty list.
   */
  public Collection<A> toCollection() {
    return toList().toCollection();
  }

  /**
   * Returns a function that takes a non-empty list to a list.
   *
   * @return A function that takes a non-empty list to a list.
   */
  public static <A> F<NonEmptyList<A>, List<A>> toList_() {
    return NonEmptyList::toList;
  }

  /**
   * Return a non-empty list with the given head and tail.
   *
   * @param head The first element of the new list.
   * @param tail The remaining elements of the new list.
   * @return A non-empty list with the given head and tail.
   */
  public static <A> NonEmptyList<A> nel(final A head, final List<A> tail) {
    return new NonEmptyList<>(head, tail);
  }

  /**
   * Constructs a non empty list from the given elements.
   *
   * @param head The first in the non-empty list.
   * @param tail The elements to construct a list's tail with.
   * @return A non-empty list with the given elements.
   */
  @SafeVarargs
  public static <A> NonEmptyList<A> nel(final A head, final A... tail) {
    return nel(head, List.list(tail));
  }

  /**
   * Returns a function that puts an element into a non-empty list.
   *
   * @return A function that puts an element into a non-empty list.
   */
  public static <A> F<A, NonEmptyList<A>> nel() {
    return a -> nel(a);
  }

  /**
   * Returns a potential non-empty list from the given list. A non-value is returned if the given list is empty.
   *
   * @param as The list to construct a potential non-empty list with.
   * @return A potential non-empty list from the given list.
   */
  public static <A> Option<NonEmptyList<A>> fromList(final List<A> as) {
    return as.isEmpty() ?
            Option.none() :
            some(nel(as.head(), as.tail()));
  }

  /**
   * Concatenate (join) a non empty list of non empty lists.
   *
   * @param o The non empty list of non empty lists to join.
   * @return A new non empty list that is the concatenation of the given lists.
   */
  public static <A> NonEmptyList<A> join(final NonEmptyList<NonEmptyList<A>> o) {
    return o.bind(identity());
  }

  /**
   * Perform an equality test on this list which delegates to the .equals() method of the member instances.
   * This is implemented with Equal.nonEmptyListEqual using the anyEqual rule.
   *
   * @param obj the other object to check for equality against.
   * @return true if this list is equal to the provided argument
   */
  @Override
  public boolean equals(final Object obj) {
    return Equal.equals0(NonEmptyList.class, this, obj, () -> Equal.nonEmptyListEqual(Equal.anyEqual()));
  }

  @Override
  public int hashCode() {
    return Hash.nonEmptyListHash(Hash.<A>anyHash()).hash(this);
  }

  @Override
  public String toString() {
    return Show.nonEmptyListShow(Show.<A>anyShow()).showS(this);
  }

  /**
   * Fold the list, from right to left.
   *
   * @param combine the combine function
   * @return the output
   */
  public final A foldRight1(
          final F<A, F<A, A>> combine) {
    return foldRightN(combine, identity());
  }

  /**
   * Fold the list, from right to left.
   *
   * @param combine the combine function
   * @return the output
   */
  public final A foldRight1(
          final F2<A, A, A> combine) {
    return foldRightN(combine, identity());
  }

  /**
   * Fold the list, from left to right.
   *
   * @param combine the combine function
   * @return the output
   */
  public final A foldLeft1(
          final F<A, F<A, A>> combine) {
    return foldLeftN(combine, identity());
  }

  /**
   * Fold the list, from left to right.
   *
   * @param combine   the combine function
   * @return the output
   */
  public final A foldLeft1(
          final F2<A, A, A> combine) {
    return foldLeftN(combine, identity());
  }

  /**
   * Fold the list, from right to left, using one function to transform the last and another function to combine the
   * output.
   *
   * @param combine   the combine function
   * @param transform the transform function
   * @param <B>       the type of the output
   * @return the output
   */
  public final <B> B foldRightN(
          final F<A, F<B, B>> combine,
          final F<A, B> transform) {
    return tail().isEmpty() ?
            transform.f(head()) :
            init().foldRight(combine, transform.f(last()));
  }

  /**
   * Fold the list, from right to left, using one function to transform the last and another function to combine the
   * output.
   *
   * @param combine   the combine function
   * @param transform the transform function
   * @param <B>       the type of the output
   * @return the output
   */
  public final <B> B foldRightN(
          final F2<A, B, B> combine,
          final F<A, B> transform) {
    return tail().isEmpty() ?
            transform.f(head()) :
            init().foldRight(combine, transform.f(last()));
  }

  /**
   * Fold the list, from left to right, using one function to transform the head and another function to combine the
   * output.
   *
   * @param combine   the combine function
   * @param transform the transform function
   * @param <B>       the type of the output
   * @return the output
   */
  public final <B> B foldLeftN(
          final F<B, F<A, B>> combine,
          final F<A, B> transform) {
    return tail().isEmpty() ?
            transform.f(head()) :
            tail().foldLeft(combine, transform.f(head()));
  }

  /**
   * Fold the list, from left to right, using one function to transform the head and another function to combine the
   * output.
   *
   * @param combine   the combine function
   * @param transform the transform function
   * @param <B>       the type of the output
   * @return the output
   */
  public final <B> B foldLeftN(
          final F2<B, A, B> combine,
          final F<A, B> transform) {
    return tail().isEmpty() ?
            transform.f(head()) :
            tail().foldLeft(combine, transform.f(head()));
  }

  /**
   * Sequence the given nonEmptyList and collect the output on the right side of an either.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the right value
   * @param <L>          the type of the left value
   * @return the either
   */
  public static <L, B> Either<L, NonEmptyList<B>> sequenceEither(
          final NonEmptyList<Either<L, B>> nonEmptyList) {
    return nonEmptyList.traverseEither(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output on the left side of an either.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <R>          the type of the right value
   * @param <B>          the type of the left value
   * @return the either
   */
  public static <R, B> Either<NonEmptyList<B>, R> sequenceEitherLeft(
          final NonEmptyList<Either<B, R>> nonEmptyList) {
    return nonEmptyList.traverseEitherLeft(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output on the right side of an either.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the right value
   * @param <L>          the type of the left value
   * @return the either
   */
  public static <L, B> Either<L, NonEmptyList<B>> sequenceEitherRight(
          final NonEmptyList<Either<L, B>> nonEmptyList) {
    return nonEmptyList.traverseEitherRight(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a function.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <C>          the type of the input value
   * @param <B>          the type of the output value
   * @return the either
   */
  public static <C, B> F<C, NonEmptyList<B>> sequenceF(
          final NonEmptyList<F<C, B>> nonEmptyList) {
    return nonEmptyList.traverseF(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as an IO.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the IO value
   * @return the IO
   */
  public static <B> IO<NonEmptyList<B>> sequenceIO(
          final NonEmptyList<IO<B>> nonEmptyList) {
    return nonEmptyList.traverseIO(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a list.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the list value
   * @return the list
   */
  public static <B> List<NonEmptyList<B>> sequenceList(
          final NonEmptyList<List<B>> nonEmptyList) {
    return nonEmptyList.traverseList(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a nonEmptyList.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the nonEmptyList value
   * @return the nonEmptyList
   */
  public static <B> NonEmptyList<NonEmptyList<B>> sequenceNonEmptyList(
          final NonEmptyList<NonEmptyList<B>> nonEmptyList) {
    return nonEmptyList.traverseNonEmptyList(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as an nonEmptyList.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the option value
   * @return the nonEmptyList
   */
  public static <B> Option<NonEmptyList<B>> sequenceOption(
          final NonEmptyList<Option<B>> nonEmptyList) {
    return nonEmptyList.traverseOption(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a P1.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the P1 value
   * @return the P1
   */
  public static <B> P1<NonEmptyList<B>> sequenceP1(
          final NonEmptyList<P1<B>> nonEmptyList) {
    return nonEmptyList.traverseP1(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a seq.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the nonEmptyList value
   * @return the seq
   */
  public static <B> Seq<NonEmptyList<B>> sequenceSeq(
          final NonEmptyList<Seq<B>> nonEmptyList) {
    return nonEmptyList.traverseSeq(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a set; use the given ord to order the set.
   *
   * @param ord          the given ord
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the set value
   * @return the either
   */
  public static <B> Set<NonEmptyList<B>> sequenceSet(
          final Ord<B> ord,
          final NonEmptyList<Set<B>> nonEmptyList) {
    return nonEmptyList.traverseSet(ord, identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a stream.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <B>          the type of the nonEmptyList value
   * @return the stream
   */
  public static <B> Stream<NonEmptyList<B>> sequenceStream(
          final NonEmptyList<Stream<B>> nonEmptyList) {
    return nonEmptyList.traverseStream(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a trampoline.
   *
   * @param nonEmptyList the given trampoline
   * @param <B>          the type of the nonEmptyList value
   * @return the nonEmptyList
   */
  public static <B> Trampoline<NonEmptyList<B>> sequenceTrampoline(
          final NonEmptyList<Trampoline<B>> nonEmptyList) {
    return nonEmptyList.traverseTrampoline(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a validation.
   *
   * @param nonEmptyList the given nonEmptyList
   * @param <E>          the type of the failure value
   * @param <B>          the type of the success value
   * @return the validation
   */
  public static <E, B> Validation<E, NonEmptyList<B>> sequenceValidation(
          final NonEmptyList<Validation<E, B>> nonEmptyList) {
    return nonEmptyList.traverseValidation(identity());
  }

  /**
   * Sequence the given nonEmptyList and collect the output as a validation; use the given semigroup to reduce the errors.
   *
   * @param semigroup    the given semigroup
   * @param nonEmptyList the given nonEmptyList
   * @param <E>          the type of the failure value
   * @param <B>          the type of the success value
   * @return the validation
   */
  public static <E, B> Validation<E, NonEmptyList<B>> sequenceValidation(
          final Semigroup<E> semigroup,
          final NonEmptyList<Validation<E, B>> nonEmptyList) {
    return nonEmptyList.traverseValidation(semigroup, identity());
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output on the right side of an either.
   *
   * @param f   the given function
   * @param <L> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <B, L> Either<L, NonEmptyList<B>> traverseEither(
          final F<A, Either<L, B>> f) {
    return traverseEitherRight(f);
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output on the left side of an either.
   *
   * @param f   the given function
   * @param <R> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <R, B> Either<NonEmptyList<B>, R> traverseEitherLeft(
          final F<A, Either<B, R>> f) {
    return foldRightN(
            element -> either -> f.f(element).left().bind(elementInner -> either.left().map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).left().map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output on the right side of an either.
   *
   * @param f   the given function
   * @param <L> the type of the left value
   * @param <B> the type of the right value
   * @return the either
   */
  public <L, B> Either<L, NonEmptyList<B>> traverseEitherRight(
          final F<A, Either<L, B>> f) {
    return foldRightN(
            element -> either -> f.f(element).right().bind(elementInner -> either.right().map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).right().map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a function.
   *
   * @param f   the given function
   * @param <C> the type of the input value
   * @param <B> the type of the output value
   * @return the function
   */
  public <C, B> F<C, NonEmptyList<B>> traverseF(
          final F<A, F<C, B>> f) {
    return foldRightN(
            element -> fInner -> Function.bind(f.f(element), elementInner -> andThen(fInner, nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> F1Functions.map(f.f(element), elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as an IO.
   *
   * @param f   the given function
   * @param <B> the type of the IO value
   * @return the IO
   */
  public <B> IO<NonEmptyList<B>> traverseIO(
          final F<A, IO<B>> f) {
    return foldRightN(
            element -> io -> IOFunctions.bind(f.f(element), elementInner -> IOFunctions.map(io, nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> IOFunctions.map(f.f(element), elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a list.
   *
   * @param f   the given function
   * @param <B> the type of the list value
   * @return the list
   */
  public <B> List<NonEmptyList<B>> traverseList(
          final F<A, List<B>> f) {
    return foldRightN(
            element -> list -> f.f(element).bind(elementInner -> list.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a nonEmptyList.
   *
   * @param f   the given function
   * @param <B> the type of the nonEmptyList value
   * @return the nonEmptyList
   */
  public <B> NonEmptyList<NonEmptyList<B>> traverseNonEmptyList(
          final F<A, NonEmptyList<B>> f) {
    return foldRightN(
            element -> nonEmptyList -> f.f(element).bind(elementInner -> nonEmptyList.map(nonEmptyListInner -> nonEmptyListInner.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverses through the Seq with the given function
   *
   * @param f The function that produces Option value
   * @return none if applying f returns none to any element of the seq or f mapped seq in some .
   */
  public <B> Option<NonEmptyList<B>> traverseOption(
          final F<A, Option<B>> f) {
    return foldRightN(
            element -> option -> f.f(element).bind(elementInner -> option.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a p1.
   *
   * @param f   the given function
   * @param <B> the type of the p1 value
   * @return the p1
   */
  public <B> P1<NonEmptyList<B>> traverseP1(
          final F<A, P1<B>> f) {
    return foldRightN(
            element -> p1 -> f.f(element).bind(elementInner -> p1.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a seq.
   *
   * @param f   the given function
   * @param <B> the type of the seq value
   * @return the seq
   */
  public <B> Seq<NonEmptyList<B>> traverseSeq(
          final F<A, Seq<B>> f) {
    return foldRightN(
            element -> seq -> f.f(element).bind(elementInner -> seq.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a set; use the given ord to order the
   * set.
   *
   * @param ord the given ord
   * @param f   the given function
   * @param <B> the type of the set value
   * @return the set
   */
  public <B> Set<NonEmptyList<B>> traverseSet(
          final Ord<B> ord,
          final F<A, Set<B>> f) {
    final Ord<NonEmptyList<B>> seqOrd = Ord.nonEmptyListOrd(ord);
    return foldRightN(
            element -> set -> f.f(element).bind(seqOrd, elementInner -> set.map(seqOrd, seq -> seq.cons(elementInner))),
            element -> f.f(element).map(seqOrd, elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a stream.
   *
   * @param f   the given function
   * @param <B> the type of the nonEmptyList value
   * @return the stream
   */
  public <B> Stream<NonEmptyList<B>> traverseStream(
          final F<A, Stream<B>> f) {
    return foldRightN(
            element -> stream -> f.f(element).bind(elementInner -> stream.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a trampoline.
   *
   * @param f   the given function
   * @param <B> the type of the trampoline value
   * @return the trampoline
   */
  public <B> Trampoline<NonEmptyList<B>> traverseTrampoline(
          final F<A, Trampoline<B>> f) {
    return foldRightN(
            element -> trampoline -> f.f(element).bind(elementInner -> trampoline.map(seq -> seq.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a validation.
   *
   * @param f   the given function
   * @param <E> the type of the failure value
   * @param <B> the type of the success value
   * @return the validation
   */
  public final <E, B> Validation<E, NonEmptyList<B>> traverseValidation(
          final F<A, Validation<E, B>> f) {
    return foldRightN(
            element -> validation -> f.f(element).bind(elementInner -> validation.map(nonEmptyList -> nonEmptyList.cons(elementInner))),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }

  /**
   * Traverse this nonEmptyList with the given function and collect the output as a validation; use the given semigroup to
   * reduce the errors.
   *
   * @param semigroup the given semigroup
   * @param f         the given function
   * @param <E>       the type of the failure value
   * @param <B>       the type of the success value
   * @return the validation
   */
  public final <E, B> Validation<E, NonEmptyList<B>> traverseValidation(
          final Semigroup<E> semigroup,
          final F<A, Validation<E, B>> f) {
    return foldRightN(
            element -> validation -> f.f(element).map(NonEmptyList::nel).accumulate(semigroup, validation, (nel1, nel2) -> nel1.append(nel2)),
            element -> f.f(element).map(elementInner -> nel(elementInner)));
  }
}
