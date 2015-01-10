package fj.data;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.F2Functions;
import fj.F3;
import fj.Function;
import fj.Ord;
import fj.P;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.Show;
import fj.function.Integers;

import java.util.Iterator;

import static fj.Function.compose;
import static fj.Function.curry;
import static fj.Function.flip;
import static fj.Function.join;
import static fj.Function.uncurryF2;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Stream.nil;
import static fj.data.Stream.repeat;
import static fj.F2Functions.*;

/**
 * Provides a pointed stream, which is a non-empty zipper-like stream structure that tracks an index (focus)
 * position in a stream. Focus can be moved forward and backwards through the stream, elements can be inserted
 * before or after the focused position, and the focused item can be deleted.
 * <p/>
 * Based on the pointedlist library by Jeff Wheeler.
 */
public final class Zipper<A> implements Iterable<Zipper<A>> {
  private final Stream<A> left;
  private final A focus;
  private final Stream<A> right;

  private Zipper(final Stream<A> left, final A focus, final Stream<A> right) {
    this.left = left;
    this.focus = focus;
    this.right = right;
  }


  /**
   * Creates a new Zipper with the given streams before and after the focus, and the given focused item.
   *
   * @param left  The stream of elements before the focus.
   * @param focus The element under focus.
   * @param right The stream of elements after the focus.
   * @return a new Zipper with the given streams before and after the focus, and the given focused item.
   */
  public static <A> Zipper<A> zipper(final Stream<A> left, final A focus, final Stream<A> right) {
    return new Zipper<A>(left, focus, right);
  }

  /**
   * Creates a new Zipper from the given triple.
   *
   * @param p A triple of the elements before the focus, the focus element, and the elements after the focus,
   *          respectively.
   * @return a new Zipper created from the given triple.
   */
  public static <A> Zipper<A> zipper(final P3<Stream<A>, A, Stream<A>> p) {
    return new Zipper<A>(p._1(), p._2(), p._3());
  }

  /**
   * First-class constructor of zippers.
   *
   * @return A function that yields a new zipper given streams on the left and right and a focus element.
   */
  public static <A> F3<Stream<A>, A, Stream<A>, Zipper<A>> zipper() {
    return (l, a, r) -> zipper(l, a, r);
  }

  /**
   * Returns the product-3 representation of this Zipper.
   *
   * @return the product-3 representation of this Zipper.
   */
  public P3<Stream<A>, A, Stream<A>> p() {
    return P.p(left, focus, right);
  }

  /**
   * A first-class function that yields the product-3 representation of a given Zipper.
   *
   * @return A first-class function that yields the product-3 representation of a given Zipper.
   */
  public static <A> F<Zipper<A>, P3<Stream<A>, A, Stream<A>>> p_() {
    return a -> a.p();
  }

  /**
   * An Ord instance for Zippers.
   *
   * @param o An Ord instance for the element type.
   * @return An Ord instance for Zippers.
   */
  public static <A> Ord<Zipper<A>> ord(final Ord<A> o) {
    final Ord<Stream<A>> so = Ord.streamOrd(o);
    return Ord.p3Ord(so, o, so).comap(Zipper.<A>p_());
  }

  /**
   * An Equal instance for Zippers.
   *
   * @param e An Equal instance for the element type.
   * @return An Equal instance for Zippers.
   */
  public static <A> Equal<Zipper<A>> eq(final Equal<A> e) {
    final Equal<Stream<A>> se = Equal.streamEqual(e);
    return Equal.p3Equal(se, e, se).comap(Zipper.<A>p_());
  }

  /**
   * A Show instance for Zippers.
   *
   * @param s A Show instance for the element type.
   * @return A Show instance for Zippers.
   */
  public static <A> Show<Zipper<A>> show(final Show<A> s) {
    final Show<Stream<A>> ss = Show.streamShow(s);
    return Show.p3Show(ss, s, ss).comap(Zipper.<A>p_());
  }

  /**
   * Maps the given function across the elements of this zipper (covariant functor pattern).
   *
   * @param f A function to map across this zipper.
   * @return A new zipper with the given function applied to all elements.
   */
  public <B> Zipper<B> map(final F<A, B> f) {
    return zipper(left.map(f), f.f(focus), right.map(f));
  }

  /**
   * Performs a right-fold reduction across this zipper.
   *
   * @param f The function to apply on each element of this zipper.
   * @param z The beginning value to start the application from.
   * @return the final result after the right-fold reduction.
   */
  public <B> B foldRight(final F<A, F<B, B>> f, final B z) {
    return left.foldLeft(flip(f),
                         right.cons(focus).foldRight(compose(
                             Function.<P1<B>, B, B>andThen().f(P1.<B>__1()), f), z));
  }

  /**
   * Creates a new zipper with a single element.
   *
   * @param a The focus element of the new zipper.
   * @return a new zipper with a single element which is in focus.
   */
  public static <A> Zipper<A> single(final A a) {
    return zipper(Stream.<A>nil(), a, Stream.<A>nil());
  }

  /**
   * Possibly create a zipper if the provided stream has at least one element, otherwise None.
   * The provided stream's head will be the focus of the zipper, and the rest of the stream will follow
   * on the right side.
   *
   * @param a The stream from which to create a zipper.
   * @return a new zipper if the provided stream has at least one element, otherwise None.
   */
  @SuppressWarnings({"IfMayBeConditional"})
  public static <A> Option<Zipper<A>> fromStream(final Stream<A> a) {
    if (a.isEmpty())
      return none();
    else
      return some(zipper(Stream.<A>nil(), a.head(), a.tail()._1()));
  }

  /**
   * Possibly create a zipper if the provided stream has at least one element, otherwise None.
   * The provided stream's last element will be the focus of the zipper, following the rest of the stream in order,
   * to the left.
   *
   * @param a The stream from which to create a zipper.
   * @return a new zipper if the provided stream has at least one element, otherwise None.
   */
  public static <A> Option<Zipper<A>> fromStreamEnd(final Stream<A> a) {
    if (a.isEmpty())
      return none();
    else {
      final Stream<A> xs = a.reverse();
      return some(zipper(xs.tail()._1(), xs.head(), Stream.<A>nil()));
    }
  }

  /**
   * Returns the focus element of this zipper.
   *
   * @return the focus element of this zipper.
   */
  public A focus() {
    return focus;
  }

  /**
   * Possibly moves the focus to the next element in the list.
   *
   * @return An optional zipper with the focus moved one element to the right, if there are elements to the right of
   *         focus, otherwise None.
   */
  public Option<Zipper<A>> next() {
    return right.isEmpty() ? Option.<Zipper<A>>none() : some(tryNext());
  }

  /**
   * Attempts to move the focus to the next element, or throws an error if there are no more elements.
   *
   * @return A zipper with the focus moved one element to the right, if there are elements to the right of
   *         focus, otherwise throws an error.
   */
  public Zipper<A> tryNext() {
    if (right.isEmpty())
      throw new Error("Tried next at the end of a zipper.");
    else
      return zipper(left.cons(focus), right.head(), right.tail()._1());
  }

  /**
   * Possibly moves the focus to the previous element in the list.
   *
   * @return An optional zipper with the focus moved one element to the left, if there are elements to the left of
   *         focus, otherwise None.
   */
  public Option<Zipper<A>> previous() {
    return left.isEmpty() ? Option.<Zipper<A>>none() : some(tryPrevious());
  }

  /**
   * Attempts to move the focus to the previous element, or throws an error if there are no more elements.
   *
   * @return A zipper with the focus moved one element to the left, if there are elements to the left of
   *         focus, otherwise throws an error.
   */
  public Zipper<A> tryPrevious() {
    if (left.isEmpty())
      throw new Error("Tried previous at the beginning of a zipper.");
    else
      return zipper(left.tail()._1(), left.head(), right.cons(focus));
  }

  /**
   * First-class version of the next() function.
   *
   * @return A function that moves the given zipper's focus to the next element.
   */
  public static <A> F<Zipper<A>, Option<Zipper<A>>> next_() {
    return as -> as.next();
  }

  /**
   * First-class version of the previous() function.
   *
   * @return A function that moves the given zipper's focus to the previous element.
   */
  public static <A> F<Zipper<A>, Option<Zipper<A>>> previous_() {
    return as -> as.previous();
  }

  /**
   * Inserts an element to the left of the focus, then moves the focus to the new element.
   *
   * @param a A new element to insert into this zipper.
   * @return A new zipper with the given element in focus, and the current focus element on its right.
   */
  public Zipper<A> insertLeft(final A a) {
    return zipper(left, a, right.cons(focus));
  }

  /**
   * Inserts an element to the right of the focus, then moves the focus to the new element.
   *
   * @param a A new element to insert into this zipper.
   * @return A new zipper with the given element in focus, and the current focus element on its left.
   */
  public Zipper<A> insertRight(final A a) {
    return zipper(left.cons(focus), a, right);
  }

  /**
   * Possibly deletes the element at the focus, then moves the element on the left into focus.
   * If no element is on the left, focus on the element to the right.
   * Returns None if the focus element is the only element in this zipper.
   *
   * @return A new zipper with this zipper's focus element removed, or None if deleting the focus element
   *         would cause the zipper to be empty.
   */
  public Option<Zipper<A>> deleteLeft() {
    return left.isEmpty() && right.isEmpty()
           ? Option.<Zipper<A>>none()
           : some(zipper(left.isEmpty() ? left : left.tail()._1(),
                         left.isEmpty() ? right.head() : left.head(),
                         left.isEmpty() ? right.tail()._1() : right));
  }

  /**
   * Possibly deletes the element at the focus, then moves the element on the right into focus.
   * If no element is on the right, focus on the element to the left.
   * Returns None if the focus element is the only element in this zipper.
   *
   * @return A new zipper with this zipper's focus element removed, or None if deleting the focus element
   *         would cause the zipper to be empty.
   */
  public Option<Zipper<A>> deleteRight() {
    return left.isEmpty() && right.isEmpty()
           ? Option.<Zipper<A>>none()
           : some(zipper(right.isEmpty() ? left.tail()._1() : left,
                         right.isEmpty() ? left.head() : right.head(),
                         right.isEmpty() ? right : right.tail()._1()));
  }

  /**
   * Deletes all elements in the zipper except the focus.
   *
   * @return A new zipper with the focus element as the only element.
   */
  public Zipper<A> deleteOthers() {
    final Stream<A> nil = nil();
    return zipper(nil, focus, nil);
  }

  /**
   * Returns the length of this zipper.
   *
   * @return the length of this zipper.
   */
  public int length() {
    return foldRight(Function.<A, F<Integer, Integer>>constant(Integers.add.f(1)), 0);
  }

  /**
   * Returns whether the focus is on the first element.
   *
   * @return true if the focus is on the first element, otherwise false.
   */
  public boolean atStart() {
    return left.isEmpty();
  }

  /**
   * Returns whether the focus is on the last element.
   *
   * @return true if the focus is on the last element, otherwise false.
   */
  public boolean atEnd() {
    return right.isEmpty();
  }

  /**
   * Creates a zipper of variations of this zipper, in which each element is focused,
   * with this zipper as the focus of the zipper of zippers (comonad pattern).
   *
   * @return a zipper of variations of the provided zipper, in which each element is focused,
   *         with this zipper as the focus of the zipper of zippers.
   */
  public Zipper<Zipper<A>> positions() {
    final Stream<Zipper<A>> left = Stream.unfold(
            p -> p.previous().map(join(P.<Zipper<A>, Zipper<A>>p2())), this);
    final Stream<Zipper<A>> right = Stream.unfold(
            p -> p.next().map(join(P.<Zipper<A>, Zipper<A>>p2())), this);

    return zipper(left, this, right);
  }

  /**
   * Maps over variations of this zipper, such that the given function is applied to each variation (comonad pattern).
   *
   * @param f The comonadic function to apply for each variation of this zipper.
   * @return A new zipper, with the given function applied for each variation of this zipper.
   */
  public <B> Zipper<B> cobind(final F<Zipper<A>, B> f) {
    return positions().map(f);
  }

  /**
   * Zips the elements of this zipper with a boolean that indicates whether that element has focus.
   * All of the booleans will be false, except the focused element.
   *
   * @return A new zipper of pairs, with each element of this zipper paired with a boolean that is true if that
   *         element has focus, and false otherwise.
   */
  public Zipper<P2<A, Boolean>> zipWithFocus() {
    return zipper(left.zip(repeat(false)), P.p(focus, true), right.zip(repeat(false)));
  }

  /**
   * Move the focus to the specified index.
   *
   * @param n The index to which to move the focus.
   * @return A new zipper with the focus moved to the specified index, or none if there is no such index.
   */
  public Option<Zipper<A>> move(final int n) {
    final int ll = left.length();
    final int rl = right.length();
    Option<Zipper<A>> p = some(this);
    if (n < 0 || n >= length())
      return none();
    else if (ll >= n)
      for (int i = ll - n; i > 0; i--)
        p = p.bind(Zipper.<A>previous_());
    else if (rl >= n)
      for (int i = rl - n; i > 0; i--)
        p = p.bind(Zipper.<A>next_());
    return p;
  }

  /**
   * A first-class version of the move function.
   *
   * @return A function that moves the focus of the given zipper to the given index.
   */
  public static <A> F<Integer, F<Zipper<A>, Option<Zipper<A>>>> move() {
    return curry((i, a) -> a.move(i));
  }

  /**
   * Moves the focus to the element matching the given predicate, if present.
   *
   * @param p A predicate to match.
   * @return A new zipper with the nearest matching element focused if it is present in this zipper.
   */
  public Option<Zipper<A>> find(final F<A, Boolean> p) {
    if (p.f(focus()))
      return some(this);
    else {
      final Zipper<Zipper<A>> ps = positions();
      return ps.lefts().interleave(ps.rights()).find(zipper -> p.f(zipper.focus()));
    }
  }

  /**
   * Returns the index of the focus.
   *
   * @return the index of the focus.
   */
  public int index() {
    return left.length();
  }

  /**
   * Move the focus to the next element. If the last element is focused, loop to the first element.
   *
   * @return A new zipper with the next element focused, unless the last element is currently focused, in which case
   *         the first element becomes focused.
   */
  public Zipper<A> cycleNext() {
    if (left.isEmpty() && right.isEmpty())
      return this;
    else if (right.isEmpty()) {
      final Stream<A> xs = left.reverse();
      return zipper(Stream.<A>nil(), xs.head(), xs.tail()._1().snoc(P.p(focus)));
    } else
      return tryNext();
  }

  /**
   * Move the focus to the previous element. If the first element is focused, loop to the last element.
   *
   * @return A new zipper with the previous element focused, unless the first element is currently focused,
   *         in which case the last element becomes focused.
   */
  public Zipper<A> cyclePrevious() {
    if (left.isEmpty() && right.isEmpty())
      return this;
    else if (left.isEmpty()) {
      final Stream<A> xs = right.reverse();
      return zipper(xs.tail()._1().snoc(P.p(focus)), xs.head(), Stream.<A>nil());
    } else
      return tryPrevious();
  }

  /**
   * Possibly deletes the element at the focus, then move the element on the left into focus. If no element is on the
   * left, focus on the last element. If the deletion will cause the list to be empty, return None.
   *
   * @return A new zipper with the focused element removed, and focus on the previous element to the left, or the last
   *         element if there is no element to the left.
   */
  public Option<Zipper<A>> deleteLeftCycle() {
    if (left.isEmpty() && right.isEmpty())
      return none();
    else if (left.isNotEmpty())
      return some(zipper(left.tail()._1(), left.head(), right));
    else {
      final Stream<A> xs = right.reverse();
      return some(zipper(xs.tail()._1(), xs.head(), Stream.<A>nil()));
    }
  }

  /**
   * Possibly deletes the element at the focus, then move the element on the right into focus. If no element is on the
   * right, focus on the first element. If the deletion will cause the list to be empty, return None.
   *
   * @return A new zipper with the focused element removed, and focus on the next element to the right, or the first
   *         element if there is no element to the right.
   */
  public Option<Zipper<A>> deleteRightCycle() {
    if (left.isEmpty() && right.isEmpty())
      return none();
    else if (right.isNotEmpty())
      return some(zipper(left, right.head(), right.tail()._1()));
    else {
      final Stream<A> xs = left.reverse();
      return some(zipper(Stream.<A>nil(), xs.head(), xs.tail()._1()));
    }
  }

  /**
   * Replaces the element in focus with the given element.
   *
   * @param a An element to replace the focused element with.
   * @return A new zipper with the given element in focus.
   */
  public Zipper<A> replace(final A a) {
    return zipper(left, a, right);
  }

  /**
   * Returns the Stream representation of this zipper.
   *
   * @return A stream that contains all the elements of this zipper.
   */
  public Stream<A> toStream() {
    return left.reverse().snoc(P.p(focus)).append(right);
  }

  /**
   * Returns a Stream of the elements to the left of focus.
   *
   * @return a Stream of the elements to the left of focus.
   */
  public Stream<A> lefts() {
    return left;
  }

  /**
   * Returns a Stream of the elements to the right of focus.
   *
   * @return a Stream of the elements to the right of focus.
   */
  public Stream<A> rights() {
    return right;
  }

  /**
   * Zips this Zipper with another, applying the given function lock-step over both zippers in both directions.
   * The structure of the resulting Zipper is the structural intersection of the two Zippers.
   *
   * @param bs A Zipper to zip this one with.
   * @param f  A function with which to zip together the two Zippers.
   * @return The result of applying the given function over this Zipper and the given Zipper, location-wise.
   */
  public <B, C> Zipper<C> zipWith(final Zipper<B> bs, final F2<A, B, C> f) {
    return F2Functions.zipZipperM(f).f(this, bs);
  }


  /**
   * Zips this Zipper with another, applying the given function lock-step over both zippers in both directions.
   * The structure of the resulting Zipper is the structural intersection of the two Zippers.
   *
   * @param bs A Zipper to zip this one with.
   * @param f  A function with which to zip together the two Zippers.
   * @return The result of applying the given function over this Zipper and the given Zipper, location-wise.
   */
  public <B, C> Zipper<C> zipWith(final Zipper<B> bs, final F<A, F<B, C>> f) {
    return zipWith(bs, uncurryF2(f));
  }

  /**
   * Returns an iterator of all the positions of this Zipper, starting from the leftmost position.
   *
   * @return An iterator of all the positions of this Zipper, starting from the leftmost position.
   */
  public Iterator<Zipper<A>> iterator() { return positions().toStream().iterator(); }
}
