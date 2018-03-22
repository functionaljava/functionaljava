package fj.data;

import fj.Equal;
import fj.F;
import fj.F0;
import fj.F2;
import fj.F3;
import fj.Function;
import fj.Hash;
import fj.Monoid;
import fj.Ord;
import fj.Ordering;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Show;
import fj.Unit;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.function.Effect1;

import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static fj.Bottom.error;
import static fj.Function.*;
import static fj.Ordering.EQ;
import static fj.Ordering.GT;
import static fj.Ordering.LT;
import static fj.P.p;
import static fj.P.p2;
import static fj.Unit.unit;
import static fj.control.parallel.Promise.promise;
import static fj.data.Array.mkArray;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.function.Booleans.not;

/**
 * A lazy (not yet evaluated), immutable, singly linked list.
 *
 * @version %build.number%
 */
public abstract class Stream<A> implements Iterable<A> {
  Stream() {}

  /**
   * Performs a reduction on this stream using the given arguments.
   *
   * @param nil  The value to return if this stream is empty.
   * @param cons The function to apply to the head and tail of this stream if it is not empty.
   * @return A reduction on this stream.
   */
  public abstract <B> B uncons(B nil, F2<A, Stream<A>, B> cons);

  /**
   * Evaluate this stream to its weak head normal form (WHNF).
   * On an {@link EvaluatedStream}, the {@link #isEmpty()}, {@link EvaluatedStream#unsafeHead()}
   * and {@link EvaluatedStream#unsafeTail()} ()} methods can be call repeatably without further evaluation.
   * The tail of the stream, however, may still be a "by-name" or lazy "thunk" (not evaluated, potentially infinite).
   *
   * @implNote Do not call this methods multiple times on a same instance as it would cause repeated evaluation,
   * in the (default) case of a "by-name" stream. Instead cache the result of this method in a local variable of reuse.
   */
  public abstract EvaluatedStream<A> eval();

  /**
   * Returns an iterator for this stream. This method exists to permit the use in a <code>for/code>-each loop.
   *
   * @return A iterator for this stream.
   */
  public final Iterator<A> iterator() {
    return new Iterator<A>() {
      private EvaluatedStream<A> xs = Stream.this.eval();

      public boolean hasNext() {
        return !xs.isEmpty();
      }

      public A next() {
        if (xs.isEmpty())
          throw new NoSuchElementException();
        else {
          final A a = xs.unsafeHead();
          xs = xs.unsafeTail().eval();
          return a;
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * The first element of the stream.
   *
   * @return The first element of the stream.
   */
  public final Option<A> headOption() {
    return uncons(Option.none(), (a, as) -> Option.some(a));
  }

  /**
   * The first element of the stream or fails for the empty stream.
   * Prefer safe alternative {@link #headOption()}.
   *
   * @return The first element of the stream or fails for the empty stream.
   *
   * @@deprecated use {@link EvaluatedStream#unsafeHead()} on a WKNF of this stream (on the result of {@link #eval()}) (since 4.8).
   */
  @Deprecated
  public final A head() {
    return eval().unsafeHead();
  }

  /**
   * The stream without the first element or fails for the empty stream.
   *
   * @return The stream without the first element or fails for the empty stream.
   */
  public final Option<Stream<A>> tailOption() {
    return uncons(Option.none(), (a, as) -> Option.some(as));
  }

  /**
   * The stream without the first element.
   * Prefer safe alternative {@link #tailOption()}).
   *
   * @return The stream without the first element.
   *
   * @@deprecated use {@link EvaluatedStream#unsafeTail()} on a WKNF of this stream (on the result of {@link #eval()})  (since 4.8).
   */
  @Deprecated
  public final P1<Stream<A>> tail() {
    return P.p(eval().unsafeTail());
  }

  /**
   * Weakly memoize this Stream: once evaluated, heads and tails will be wrap in {@link WeakReference}s,
   * avoiding re-evaluation as long as they are not garbage collected.
   */
  public final Stream<A> weakMemo() {
    F<Stream<A>, Stream<A>> memoized = Stream.cata(Stream.nil(), (a, as) -> cons(a, memo(as)));
    return memo(() -> memoized.f(this));
  }

  /**
   * Memoize this Stream: elements will be evaluated at most once.
   */
  public final Stream<A> hardMemo() {
    F<Stream<A>, Stream<A>> memoized = Stream.cata(Stream.nil(), (a, as) -> cons(a, lazy(as)));
    return lazy(() -> memoized.f(this));
  }

  /**
   * Returns <code>true</code> if this stream is empty, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this stream is empty, <code>false</code> otherwise.
   *
   * @implNote For better performance, when using this method in conjunction with head and/or tail retrieval,
   * preferably use methods on the WKNF of this stream (on the result of {@link #eval()}).
   */
  public boolean isEmpty() {
    return uncons(true, (a, as) -> false);
  }

  /**
   * Returns <code>false</code> if this stream is empty, <code>true</code> otherwise.
   *
   * @return <code>false</code> if this stream is empty, <code>true</code> otherwise.
   *
   * @implNote For better performance, when using this method in conjunction with head and/or tail retrieval,
   * preferably use methods on the WKNF of this stream (on the result of {@link #eval()}).
   */
  public final boolean isNotEmpty() {
    return !isEmpty();
  }

  /**
   * Performs a reduction on this stream using the given arguments.  Equivalent to {@link #uncons}.
   *
   * @deprecated As of release 4.5, use {@link #uncons}
   *
   * @param nil  The value to return if this stream is empty.
   * @param cons The function to apply to the head and tail of this stream if it is not empty.
   * @return A reduction on this stream.
   * @deprecated Use {@link #uncons(Object, F2)} (since 4.8)
   */
  @Deprecated
  public final <B> B stream(final B nil, final F<A, F<P1<Stream<A>>, B>> cons) {
    return uncons(nil, cons);
  }

  /**
   * Performs a reduction on this stream using the given arguments.
   *
   * @param nil  The value to return if this stream is empty.
   * @param cons The function to apply to the head and tail of this stream if it is not empty.
   * @return A reduction on this stream.
   * @deprecated use {@link #uncons(Object, F2)} (since 4.8)
   */
  @Deprecated
  public final <B> B uncons(final B nil, final F<A, F<P1<Stream<A>>, B>> cons) {
    return uncons(nil, (a, as) -> cons.f(a).f(P.p(as)));
  }

  /**
   * Performs a right-fold reduction across this stream.
   * If f consume eagerly, this function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight(final F<A, F<F0<B>, B>> f, final B b) {
    return foldRight(uncurryF2(f), b);
  }

  /**
   * Performs a right-fold reduction across this stream.
   * If f consume eagerly, this function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight(final F2<A, F0<B>, B> f, final B b) {
    return Stream.cata(b, f).f(this);
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight1(final F<A, F<B, B>> f, final B b) {
    return foldRight1(uncurryF2(f), b);
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight1(final F2<A, B, B> f, final B b) {
    return Stream.<A, B>cata(b, (a, as) -> f.f(a, as.f())).f(this);
  }

  /**
   * Performs a left-fold reduction across this stream. This function runs in constant space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the left-fold reduction.
   */
  public final <B> B foldLeft(final F<B, F<A, B>> f, final B b) {
    return foldLeft(uncurryF2(f), b);
  }

  /**
   * Performs a left-fold reduction across this stream. This function runs in constant space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the left-fold reduction.
   */
  public final <B> B foldLeft(final F2<B, A, B> f, final B b) {
    class Acc implements Effect1<A> {
      B acc = b;
      @Override
      public void f(A a) {
        acc = f.f(acc, a);
      }
    }
    Acc acc = new Acc();
    foreachDoEffect(acc);
    return acc.acc;
  }

  /**
   * Takes the first 2 elements of the stream and applies the function to them,
   * then applies the function to the result and the third element and so on.
   *
   * @param f The function to apply on each element of the stream.
   * @return The final result after the left-fold reduction.
   */
  public final A foldLeft1(final F2<A, A, A> f) {
    EvaluatedStream<A> eval = eval();
    if (eval.isEmpty())
      throw error("Undefined: foldLeft1 on empty list");
    return eval.unsafeTail().foldLeft(f, eval.unsafeHead());
  }

  /**
   * Takes the first 2 elements of the stream and applies the function to them,
   * then applies the function to the result and the third element and so on.
   *
   * @param f The function to apply on each element of the stream.
   * @return The final result after the left-fold reduction.
   */
  public final A foldLeft1(final F<A, F<A, A>> f) {
    return foldLeft1(uncurryF2(f));
  }

  /**
   * Returns the head of this stream if there is one or the given argument if this stream is empty.
   *
   * @param a The argument to return if this stream is empty.
   * @return The head of this stream if there is one or the given argument if this stream is empty.
   */
  public final A orHead(final F0<A> a) {
    return uncons(a, (h, tail) -> () -> h).f();
  }

  /**
   * Returns the tail of this stream if there is one or the given argument if this stream is empty.
   *
   * @param as The argument to return if this stream is empty.
   * @return The tail of this stream if there is one or the given argument if this stream is empty.
   */
  public final P1<Stream<A>> orTail(final F0<Stream<A>> as) {
    return P.lazy(uncons(as, (h, tail) -> () -> tail));
  }

  /**
   * Intersperses the given value between each two elements of the stream.
   *
   * @param a The value to intersperse between values of the stream.
   * @return A new stream with the given value between each two elements of the stream.
   */
  public final Stream<A> intersperse(final A a) { ;
    return byName(() -> uncons(nil(), (head, tail)-> cons(head, prefix(a, tail))));
  }

  private static <A> Stream<A> prefix(A a, Stream<A> as) {
    return byName(() -> as.uncons(nil(), (h, tail) -> cons(a, cons(h, prefix(a, tail)))));
  }

  /**
   * Maps the given function across this stream.
   *
   * @param f The function to map across this stream.
   * @return A new stream after the given function has been applied to each element.
   */
  public final <B> Stream<B> map(final F<A, B> f) {
    F<Stream<A>, Stream<B>> map = cata(nil(), (a, as) -> cons(f.f(a), byName(as)));
    return byName(() -> map.f(this));
  }

  /**
   * Provides a first-class version of the map function.
   *
   * @return A function that maps a given function across a given stream.
   */
  public static <A, B> F<Stream<A>, Stream<B>> map_(F<A, B> f) {
    F<Stream<A>, Stream<B>> map = cata(nil(), (a, as) -> cons(f.f(a), byName(as)));
    return s -> byName(() -> map.f(s));
  }

  /**
   * Provides a first-class version of the map function.
   *
   * @return A function that maps a given function across a given stream.
   */
  public static <A, B> F<F<A, B>, F<Stream<A>, Stream<B>>> map_() {
    return Stream::map_;
  }

  /**
   * Performs a side-effect for each element of this stream.
   *
   * @param f The side-effect to perform for the given element.
   * @return The unit value.
   */
  public final Unit foreach(final F<A, Unit> f) {
    foreachDoEffect(f::f);
    return unit();
  }

  /**
   * Performs a side-effect for each element of this stream.
   *
   * @param f The side-effect to perform for the given element.
   */
  public final void foreachDoEffect(final Effect1<A> f) {
    // a bit ugly due to lack of TCO
    class ConsVisitor implements F2<A, Stream<A>, Boolean> {
      Stream<A> s = Stream.this;

      @Override
      public Boolean f(A head, Stream<A> tail) {
        f.f(head);
        s = tail;
        return true;
      }
    }
    ConsVisitor consVisitor = new ConsVisitor();
    while (consVisitor.s.uncons(false, consVisitor)) {
    }
  }

  /**
   * Filters elements from this stream by returning only elements which produce <code>true</code>
   * when the given function is applied to them.
   *
   * @param f The predicate function to filter on.
   * @return A new stream whose elements all match the given predicate.
   */
  public final Stream<A> filter(final F<A, Boolean> f) {
    F<Stream<A>, Stream<A>> filter = Stream.cata(nil(), (a, tail) -> f.f(a) ? cons(a, byName(tail)) : byName(tail));
    return byName(() -> filter.f(this));
  }

  /**
   * Appends the given stream to this stream.
   *
   * @param as The stream to append to this one.
   * @return A new stream that has appended the given stream.
   */
  public final Stream<A> append(final Stream<A> as) {
    F<Stream<A>, Stream<A>> append = Stream.cata(as, (head, tail) -> cons(head, byName(tail)));
    return byName(() -> append.f(this));
  }

  /**
   * Appends the given stream to this stream.
   *
   * @param as The stream to append to this one.
   * @return A new stream that has appended the given stream.
   */
  public final Stream<A> append(final F0<Stream<A>> as) {
    return append(byName(as));
  }

  /**
   * Returns a new stream of all the items in this stream that do not appear in the given stream.
   *
   * @param eq an equality for the items of the streams.
   * @param xs a list to subtract from this stream.
   * @return a stream of all the items in this stream that do not appear in the given stream.
   */
  public final Stream<A> minus(final Equal<A> eq, final Stream<A> xs) {
    return removeAll(compose(Monoid.disjunctionMonoid.sumLeftS(), xs.mapM(eq::eq)));
  }

  /**
   * Filters elements from this stream by returning only elements which produce <code>false</code> when
   * the given function is applied to them.
   *
   * @param f The predicate function to filter on.
   * @return A new stream whose elements do not match the given predicate.
   */
  public final Stream<A> removeAll(final F<A, Boolean> f) {
    return filter(compose(not, f));
  }

  /**
   * Turn a stream of functions into a function returning a stream.
   *
   * @param fs The stream of functions to sequence into a single function that returns a stream.
   * @return A function that, when given an argument, applies all the functions in the given stream to it
   *         and returns a stream of the results.
   */
  public static <A, B> F<B, Stream<A>> sequence_(final Stream<F<B, A>> fs) {
    F<Stream<F<B, A>>, F<B, Stream<A>>> sequence = Stream.cata(Function
        .constant(Stream.nil()), (baf, bafs) -> b -> cons(baf.f(b), byName(() -> bafs.f().f(b))));
    return b -> byName(() -> sequence.f(fs).f(b));
  }

  /**
   * Maps the given function of arity-2 across this stream and returns a function that applies all the resulting
   * functions to a given argument.
   *
   * @param f A function of arity-2
   * @return A function that, when given an argument, applies the given function to that argument and every element
   *         in this list.
   */
  public final <B, C> F<B, Stream<C>> mapM(final F<A, F<B, C>> f) {
    return sequence_(map(f));
  }

  /**
   * Binds the given function across each element of this stream with a final join.
   *
   * @param f The function to apply to each element of this stream.
   * @return A new stream after performing the map, then final join.
   */
  public final <B> Stream<B> bind(final F<A, Stream<B>> f) {
    F<Stream<A>, Stream<B>> bind = Stream.cata(nil(), (a, tail) -> f.f(a).append(tail));
    return byName(() -> bind.f(this));
  }

  /**
   * Binds the given function across each element of this stream and the given stream with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given stream.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C> Stream<C> bind(final Stream<B> sb, final F<A, F<B, C>> f) {
    return sb.apply(map(f));
  }

  /**
   * Binds the given function across each element of this stream and the given stream with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given stream.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C> Stream<C> bind(final Stream<B> sb, final F2<A, B, C> f) {
    return bind(sb, curry(f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D> Stream<D> bind(final Stream<B> sb, final Stream<C> sc, final F<A, F<B, F<C, D>>> f) {
    return sc.apply(bind(sb, f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param sd A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D, E> Stream<E> bind(final Stream<B> sb, final Stream<C> sc, final Stream<D> sd,
                                     final F<A, F<B, F<C, F<D, E>>>> f) {
    return sd.apply(bind(sb, sc, f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param sd A given stream to bind the given function with.
   * @param se A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D, E, F$> Stream<F$> bind(final Stream<B> sb, final Stream<C> sc, final Stream<D> sd,
                                          final Stream<E> se, final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
    return se.apply(bind(sb, sc, sd, f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param sd A given stream to bind the given function with.
   * @param se A given stream to bind the given function with.
   * @param sf A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G> Stream<G> bind(final Stream<B> sb, final Stream<C> sc, final Stream<D> sd,
                                            final Stream<E> se, final Stream<F$> sf,
                                            final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
    return sf.apply(bind(sb, sc, sd, se, f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param sd A given stream to bind the given function with.
   * @param se A given stream to bind the given function with.
   * @param sf A given stream to bind the given function with.
   * @param sg A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G, H> Stream<H> bind(final Stream<B> sb, final Stream<C> sc, final Stream<D> sd,
                                               final Stream<E> se, final Stream<F$> sf, final Stream<G> sg,
                                               final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
    return sg.apply(bind(sb, sc, sd, se, sf, f));
  }

  /**
   * Binds the given function across each element of this stream and the given streams with a final
   * join.
   *
   * @param sb A given stream to bind the given function with.
   * @param sc A given stream to bind the given function with.
   * @param sd A given stream to bind the given function with.
   * @param se A given stream to bind the given function with.
   * @param sf A given stream to bind the given function with.
   * @param sg A given stream to bind the given function with.
   * @param sh A given stream to bind the given function with.
   * @param f  The function to apply to each element of this stream and the given streams.
   * @return A new stream after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G, H, I> Stream<I> bind(final Stream<B> sb, final Stream<C> sc, final Stream<D> sd,
                                                  final Stream<E> se, final Stream<F$> sf, final Stream<G> sg,
                                                  final Stream<H> sh,
                                                  final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
    return sh.apply(bind(sb, sc, sd, se, sf, sg, f));
  }

  /**
   * Performs a bind across each stream element, but ignores the element value each time.
   *
   * @param bs The stream to apply in the final join.
   * @return A new stream after the final join.
   */
  public final <B> Stream<B> sequence(final Stream<B> bs) {
    final F<A, Stream<B>> c = constant(bs);
    return bind(c);
  }

  /**
   * Sequence through the Stream monad.
   *
   * @param io The IO stream to sequence.
   * @return The stream of IOs after sequencing.
   */
  public static <A> Stream<IO<A>> sequence(final IO<Stream<A>> io) {
    return IOFunctions.runSafe(io).map(IOFunctions::unit);
  }

  /**
   * Sequence through the Stream monad.
   *
   * @param p The lazy stream to sequence.
   * @return The stream of (pre-calculated) lazy values after sequencing.
   */
  public static <A> Stream<P1<A>> sequence(final F0<Stream<A>> p) {
    return byName(p).map(P::p);
  }

  /**
   * Sequence through the Stream monad.
   *
   * @param o The optional stream to sequence.
   * @return The stream of options after sequencing.
   */
  public static <A> Stream<Option<A>> sequence(final Option<Stream<A>> o) {
    return o.isNone() ? nil() : o.some().map(Option::some);
  }

  /**
   * Performs function application within a stream (applicative functor pattern).
   *
   * @param sf The stream of functions to apply.
   * @return A new stream after applying the given stream of functions through this stream.
   */
  public final <B> Stream<B> apply(final Stream<F<A, B>> sf) {
    return sf.bind(this::map);
  }

  /**
   * Interleaves the given stream with this stream to produce a new stream.
   *
   * @param as The stream to interleave this stream with.
   * @return A new stream with elements interleaved from this stream and the given stream.
   */
  public final Stream<A> interleave(final Stream<A> as) {
    return byName(() -> as.uncons(this, (h, tail) -> cons(h, as.interleave(tail))));
  }

  public static <A> Stream<A> enumerationStream(Enumeration<A> e) {
    return byName(() -> e.hasMoreElements() ? cons(e.nextElement(), enumerationStream(e)): nil());
  }

  /**
   * Sort this stream according to the given ordering.
   *
   * @param o An ordering for the elements of this stream.
   * @return A new stream with the elements of this stream sorted according to the given ordering.
   */
  public final Stream<A> sort(final Ord<A> o) {
    return mergesort(o, map(Stream.single()));
  }

  // Merges a stream of individually sorted streams into a single sorted stream.
  private static <A> Stream<A> mergesort(final Ord<A> o, final Stream<Stream<A>> s) {
    EvaluatedStream<Stream<A>> eval = s.eval();
    if (eval.isEmpty())
      return nil();
    EvaluatedStream<Stream<A>> tail = eval.unsafeTail().eval();
    while (!tail.isEmpty()){
      eval = mergePairs(o, cons(eval.unsafeHead(), tail)).eval();
      tail = eval.unsafeTail().eval();
    }
    return eval.unsafeHead();
  }

  // Merges individually sorted streams two at a time.
  private static <A> Stream<Stream<A>> mergePairs(final Ord<A> o, final Stream<Stream<A>> s) {
    return memo(() -> s.uncons(nil(), (h1, t) -> t.uncons(cons(h1, nil()), (h2, t2) ->
      cons(merge(o, h1, h2), mergePairs(o, t2))
    )));
  }

  // Merges two individually sorted streams.
  private static <A> Stream<A> merge(final Ord<A> o, final Stream<A> xs, final Stream<A> ys) {
    return memo(() -> xs.uncons(ys, (x, xtail) ->
      ys.uncons(xs, (y, ytail) -> o.isGreaterThan(x, y) ? cons(y, merge(o, xs, ytail)) : cons(x, merge(o, xtail, ys)))
    ));
  }

  /**
   * Sort this stream according to the given ordering, using a parallel Quick Sort algorithm that uses the given
   * parallelisation strategy.
   *
   * @param o An ordering for the elements of this stream.
   * @param s A strategy for parallelising the algorithm.
   * @return A new stream with the elements of this stream sorted according to the given ordering.
   */
  public final Stream<A> sort(final Ord<A> o, final Strategy<Unit> s) {
    return qs(o, s).claim();
  }

  private Promise<Stream<A>> qs(final Ord<A> o, final Strategy<Unit> s) {
    EvaluatedStream<A> eval = eval();
    if (eval.isEmpty())
      return promise(s, p(this));
    else {
      final F<Boolean, Boolean> id = identity();
      final A x = eval.unsafeHead();
      final Stream<A> xs = eval.unsafeTail().weakMemo();
      final Promise<Stream<A>> left = Promise.join(s, P.lazy(() -> flt(o, s, x, id).f(xs)));
      final Promise<Stream<A>> right = flt(o, s, x, not).f(xs);
      final Monoid<Stream<A>> m = Monoid.streamMonoid();
      return right.fmap(m.sum(single(x))).apply(left.fmap(m.sum()));
    }
  }

  private static <A> F<Stream<A>, Promise<Stream<A>>> qs_(final Ord<A> o, final Strategy<Unit> s) {
    return xs -> xs.qs(o, s);
  }

  private static <A> F<Stream<A>, Promise<Stream<A>>> flt(final Ord<A> o,
                                                          final Strategy<Unit> s,
                                                          final A x,
                                                          final F<Boolean, Boolean> f) {
    final F<F<A, Boolean>, F<Stream<A>, Stream<A>>> filter = filter();
    final F<A, Boolean> lt = o.isLessThan(x);
    return compose(qs_(o, s), filter.f(compose(f, lt)));
  }

  /**
   * Projects an immutable collection of this stream.
   *
   * @return An immutable collection of this stream.
   */
  public final Collection<A> toCollection() {
    return new AbstractCollection<A>() {
      public Iterator<A> iterator() {
        return Stream.this.iterator();
      }

      public int size() {
        return length();
      }
    };
  }

  /**
   * Returns a stream of integers from the given <code>from</code> value (inclusive) to the given
   * <code>to</code> value (exclusive).
   *
   * @param from The minimum value for the stream (inclusive).
   * @param to   The maximum value for the stream (exclusive).
   * @return A stream of integers from the given <code>from</code> value (inclusive) to the given
   *         <code>to</code> value (exclusive).
   */
  public static Stream<Integer> range(final int from, final long to) {
    return from >= to ? Stream.nil() : cons(from, () -> range(from + 1, to));
  }

  /**
   * Constructs a stream with the given elements.
   *
   * @param as The elements which which to construct a stream.
   * @return a new stream with the given elements.
   */
  @SafeVarargs public static <A> Stream<A> stream(final A... as) {
    return arrayStream(as);
  }

  /**
   * Constructs a stream with the given elements in the Iterable.  Equivalent to {@link #iterableStream(Iterable)} .
   *
   * @deprecated As of release 4.5, use {@link #iterableStream(Iterable)}
   */
  @Deprecated
  public static <A> Stream<A> stream(Iterable<A> it) {
    return iterableStream(it);
  }

  /**
   * Constructs a stream with the given elements in the Iterator.  Equivalent to {@link #iteratorStream(Iterator)} .
   *
   * @deprecated As of release 4.5, use {@link #iteratorStream(Iterator)}
   */
  @Deprecated
  public static <A> Stream<A> stream(final Iterator<A> it) {
    return iteratorStream(it);
  }

  /**
   * Constructs a stream with the given elements in the Iterator.
   */
  public static <A> Stream<A> iteratorStream(final Iterator<A> it) {
    return lazy(() -> it.hasNext() ? cons(it.next(), iteratorStream(it)) : nil());
  }

  /**
   * Constructs a stream with the given elements in the Iterator.
   */
  public static <A> Stream<A> nonReusableIteratorStream(final Iterator<A> it) {
    return byName(() -> it.hasNext() ? cons(it.next(), nonReusableIteratorStream(it)) : nil());
  }

  /**
   * Returns a stream that is either infinite or bounded up to the maximum value of the given iterator starting at the
   * given value and stepping at increments of <code>1</code>.
   *
   * @param e    The enumerator to compute successors from.
   * @param from The value to begin computing successors from.
   * @return A stream that is either infinite or bounded up to the maximum value of the given iterator starting at the
   *         given value and stepping at increments of <code>1</code>.
   */
  public static <A> Stream<A> forever(final Enumerator<A> e, final A from) {
    return forever(e, from, 1L);
  }

  /**
   * Returns a stream that is either infinite or bounded up to the maximum value of the given iterator starting at the
   * given value and stepping at the given increment.
   *
   * @param e    The enumerator to compute successors from.
   * @param from The value to begin computing successors from.
   * @param step The increment to step.
   * @return A stream that is either infinite or bounded up to the maximum value of the given iterator starting at the
   *         given value and stepping at the given increment.
   */
  public static <A> Stream<A> forever(final Enumerator<A> e, final A from, final long step) {
    return cons(from, () -> e.plus(from, step).map(a -> forever(e, a, step)).orSome(Stream.nil()));
  }

  /**
   * Returns a stream using the given enumerator from the given value to the other given value stepping at increments of
   * <code>1</code>.
   *
   * @param e    The enumerator to compute successors from.
   * @param from The value to begin computing successors from.
   * @param to   The value to stop computing successors from.
   * @return A stream using the given enumerator from the given value to the other given value stepping at increments of
   *         <code>1</code>.
   */
  public static <A> Stream<A> range(final Enumerator<A> e, final A from, final A to) {
    return range(e, from, to, 1L);
  }

  /**
   * Returns a stream using the given enumerator from the given value to the other given value stepping at the given
   * increment.
   *
   * @param e    The enumerator to compute successors from.
   * @param from The value to begin computing successors from.
   * @param to   The value to stop computing successors from.
   * @param step The increment to step.
   * @return A stream using the given enumerator from the given value to the other given value stepping at the given
   *         increment.
   */
  public static <A> Stream<A> range(final Enumerator<A> e, final A from, final A to, final long step) {
    final Ordering o = e.order().compare(from, to);
    return o == EQ || step > 0L && o == GT || step < 0L && o == LT ? single(from) : cons(from, () -> join(e.plus(from, step).filter(a -> !(o == LT
                ? e.order().isLessThan(to, a)
                : e.order().isGreaterThan(to, a))).map(a1 -> range(e, a1, to, step)).toStream()));
  }
  /**
   * Returns an infinite stream of integers from the given <code>from</code> value (inclusive).
   *
   * @param from The minimum value for the stream (inclusive).
   * @return A stream of integers from the given <code>from</code> value (inclusive).
   */
  public static Stream<Integer> range(final int from) {
    return cons(from, () -> range(from + 1));
  }

  /**
   * Returns a first-class version of the filter function.
   *
   * @return a function that filters a given stream using a given predicate.
   */
  public static <A> F<F<A, Boolean>, F<Stream<A>, Stream<A>>> filter() {
    return curry((f, as) -> as.filter(f));
  }

  /**
   * Zips this stream with the given stream of functions, applying each function in turn to the
   * corresponding element in this stream to produce a new stream. If this stream and the given stream
   * have different lengths, then the longer stream is normalised so this function never fails.
   *
   * @param fs The stream of functions to apply to this stream.
   * @return A new stream with a length the same as the shortest of this stream and the given stream.
   */
  public final <B> Stream<B> zapp(final Stream<F<A, B>> fs) {
    return byName(() -> {
      EvaluatedStream<F<A, B>> fsEval = fs.eval();
      EvaluatedStream<A> eval = eval();
      return fsEval.isEmpty() || eval.isEmpty() ? Stream.nil() :
          cons(fsEval.unsafeHead().f(eval.unsafeHead()), eval.unsafeTail().zapp(fsEval.unsafeTail()));
    });
  }

  /**
   * Zips this stream with the given stream using the given function to produce a new stream. If
   * this stream and the given stream have different lengths, then the longer stream is normalised
   * so this function never fails.
   *
   * @param bs The stream to zip this stream with.
   * @param f  The function to zip this stream and the given stream with.
   * @return A new stream with a length the same as the shortest of this stream and the given
   *         stream.
   */
  public final <B, C> Stream<C> zipWith(final Stream<B> bs, final F<A, F<B, C>> f) {
    return bs.zapp(zapp(repeat(f)));
  }

  /**
   * Zips this stream with the given stream using the given function to produce a new stream. If
   * this stream and the given stream have different lengths, then the longer stream is normalised
   * so this function never fails.
   *
   * @param bs The stream to zip this stream with.
   * @param f  The function to zip this stream and the given stream with.
   * @return A new stream with a length the same as the shortest of this stream and the given
   *         stream.
   */
  public final <B, C> Stream<C> zipWith(final Stream<B> bs, final F2<A, B, C> f) {
    return zipWith(bs, curry(f));
  }

  /**
   * Partially-applied version of zipWith.
   * Returns a function that zips a given stream with this stream using the given function.
   *
   * @param f The function to zip this stream and a given stream with.
   * @return A function that zips a given stream with this stream using the given function.
   */
  public final <B, C> F<Stream<B>, Stream<C>> zipWith(final F<A, F<B, C>> f) {
    return stream -> zipWith(stream, f);
  }

  /**
   * Zips this stream with the given stream to produce a stream of pairs. If this stream and the
   * given stream have different lengths, then the longer stream is normalised so this function
   * never fails.
   *
   * @param bs The stream to zip this stream with.
   * @return A new stream with a length the same as the shortest of this stream and the given
   *         stream.
   */
  public final <B> Stream<P2<A, B>> zip(final Stream<B> bs) {
    final F<A, F<B, P2<A, B>>> __2 = p2();
    return zipWith(bs, __2);
  }

  /**
   * Zips this stream with the index of its element as a pair.
   *
   * @return A new stream with the same length as this stream.
   */
  public final Stream<P2<A, Integer>> zipIndex() {
    return zipWith(range(0), (F2<A, Integer, P2<A, Integer>>) P::p);
  }

  /**
   * Returns an either projection of this stream; the given argument in <code>Left</code> if empty,
   * or the first element in <code>Right</code>.
   *
   * @param x The value to return in left if this stream is empty.
   * @return An either projection of this stream.
   */
  public final <X> Either<X, A> toEither(final F0<X> x) {
    EvaluatedStream<A> eval = eval();
    return eval.isEmpty() ? Either.left(x.f()) : Either.right(eval.unsafeHead());
  }

  /**
   * Returns an option projection of this stream; <code>None</code> if empty, or the first element
   * in <code>Some</code>.
   *
   * @return An option projection of this stream.
   */
  public final Option<A> toOption() {
    return headOption();
  }

  /**
   * To be removed in future release:
   * affectation of the result of this method to a non generic array
   * will result in runtime error (ClassCastException).
   *
   * @deprecated As of release 4.6, use {@link #array(Class)}.
   */
  @Deprecated
  public final A[] toJavaArray() {
    @SuppressWarnings("unchecked")
    final A[] array = (A[]) new Object[length()];
    int i = 0;
    for (A a: this) {
      array[i] = a;
      i++;
    }
    return array;
  }

  /**
   * Returns a list projection of this stream.
   *
   * @return A list projection of this stream.
   */
    public final List<A> toList() {
        List.Buffer<A> buf = List.Buffer.empty();
        foreachDoEffect(buf::snoc);
        return buf.toList();
    }

  /**
   * Returns a java.util.List projection of this stream.
   */
  public final java.util.List<A> toJavaList() {
    return new java.util.LinkedList<>(toCollection());
  }

  /**
   * Returns a array projection of this stream.
   *
   * @return A array projection of this stream.
   */
  @SuppressWarnings("unchecked")
  public final Array<A> toArray() {
    EvaluatedStream<A> x = this.eval();
    final int l = x.length();
    final Object[] a = new Object[l];
    for (int i = 0; i < l; i++) {
      a[i] = x.unsafeHead();
      x = x.unsafeTail().eval();
    }

    return mkArray(a);
  }

  /**
   * Returns a array projection of this stream.
   *
   * @param c The class type of the array to return.
   * @return A array projection of this stream.
   */
  @SuppressWarnings({"unchecked", "UnnecessaryFullyQualifiedName"})
  public final Array<A> toArray(final Class<A[]> c) {
    final A[] a = (A[]) java.lang.reflect.Array.newInstance(c.getComponentType(), length());

    int i = 0;
    for (final A x : this) {
      a[i] = x;
      i++;
    }

    return Array.array(a);
  }

  /**
   * Returns an array from this stream.
   *
   * @param c The class type of the array to return.
   * @return An array from this stream.
   */
  public final A[] array(final Class<A[]> c) {
    return toArray(c).array(c);
  }

  /**
   * Prepends (cons) the given element to this stream to product a new stream.
   *
   * @param a The element to prepend.
   * @return A new stream with the given element at the head.
   */
  public final Stream<A> cons(final A a) {
    return new Cons<>(a, Stream.this);
  }

  /**
   * Returns a string from the given stream of characters. The inverse of this function is {@link
   * #fromString(String)}.
   *
   * @param cs The stream of characters to produce the string from.
   * @return A string from the given stream of characters.
   */
  public static String asString(final Stream<Character> cs) {
    StringBuilder sb = new StringBuilder();
    cs.foreachDoEffect(sb::append);
    return sb.toString();
  }

  /**
   * Returns a stream of characters from the given string. The inverse of this function is {@link
   * #asString(Stream)}.
   *
   * @param s The string to produce the stream of characters from.
   * @return A stream of characters from the given string.
   */
  public static Stream<Character> fromString(final String s) {
    return LazyString.str(s).toStream();
  }

  /**
   * Append the given element to this stream to product a new stream.
   *
   * @param a The element to append.
   * @return A new stream with the given element at the end.
   */
  public final Stream<A> snoc(final A a) {
    return append(single(a));
  }

  /**
   * Append the given element to this stream to produce a new stream.
   *
   * @param a The element to append.
   * @return A new stream with the given element at the end.
   */
  public final Stream<A> snoc(final F0<A> a) {
    return append(() -> single(a.f()));
  }

  /**
   * Returns the first <code>n</code> elements from the head of this stream.
   *
   * @param n The number of elements to take from this stream.
   * @return The first <code>n</code> elements from the head of this stream.
   */
  public final Stream<A> take(final int n) {
    return n <= 0 ? Stream.nil() : byName(() -> uncons(Stream.nil(), (head, tail) ->
           cons(head, tail.take(n - 1))));
  }

  /**
   * Drops the given number of elements from the head of this stream if they are available.
   *
   * @param i The number of elements to drop from the head of this stream.
   * @return A stream with a length the same, or less than, this stream.
   */
  public final Stream<A> drop(final int i) {
    return lazy(() -> {
      EvaluatedStream<A> xs = this.eval();

      for (int c = 0; !xs.isEmpty() && c < i; xs = xs.unsafeTail().eval())
        c++;

      return xs;
    });
  }

  /**
   * Returns the first elements of the head of this stream that match the given predicate function.
   *
   * @param f The predicate function to apply on this stream until it finds an element that does not
   *          hold, or the stream is exhausted.
   * @return The first elements of the head of this stream that match the given predicate function.
   */
  public final Stream<A> takeWhile(final F<A, Boolean> f) {
    return byName(() -> uncons(this, (head, tail) -> f.f(head) ?
        cons(head, tail.takeWhile(f)) :
        Stream.nil()));
  }

  /**
   * Traversable instance of Stream for IO.
   *
   * @return traversed value
   */
  public final <B> IO<Stream<B>> traverseIO(F<A, IO<B>> f) {
    // TODO: add trampolined IO and use lazy foldRight:
    return this.foldRight1((a, acc) ->
            IOFunctions.bind(acc, (Stream<B> bs) ->
                    IOFunctions.map(f.f(a), bs::cons)), IOFunctions.unit(Stream.nil()));

  }

  /**
   * Traversable instance of Stream for Option.
   *
   * @return traversed value
   */
  public final <B> Option<Stream<B>> traverseOption(F<A, Option<B>> f) {
    // TODO: add trampolined Option and use lazy foldRight:
    return this.foldRight1((a, acc) -> acc.bind(bs -> f.f(a).map(bs::cons)), some(Stream.nil()));
  }

  /**
   * Removes elements from the head of this stream that do not match the given predicate function
   * until an element is found that does match or the stream is exhausted.
   *
   * @param f The predicate function to apply through this stream.
   * @return The stream whose first element does not match the given predicate function.
   */
  public final Stream<A> dropWhile(final F<A, Boolean> f) {
    return lazy(() -> {
      EvaluatedStream<A> as;
      //noinspection StatementWithEmptyBody
      for (as = this.eval(); !as.isEmpty() && f.f(as.unsafeHead()); as = as.unsafeTail().eval()) ;

      return as;
    });
  }

  /**
   * Returns a tuple where the first element is the longest prefix of this stream that satisfies
   * the given predicate and the second element is the remainder of the stream.
   *
   * @param p A predicate to be satisfied by a prefix of this stream.
   * @return A tuple where the first element is the longest prefix of this stream that satisfies
   *         the given predicate and the second element is the remainder of the stream.
   */
  public final P2<Stream<A>, Stream<A>> span(final F<A, Boolean> p) {
    EvaluatedStream<A> eval = eval();
    if (eval.isEmpty())
      return p(eval, eval);
    else if (p.f(eval.unsafeHead())) {
      final P1<P2<Stream<A>, Stream<A>>> yszs = P.hardMemo(() -> eval.unsafeTail().span(p));
      return P.p(
            cons(eval.unsafeHead(), byName(() -> yszs._1()._1())),
            byName(() -> yszs._1()._2())
        );
    } else
      return p(Stream.nil(), this);
  }

  /**
   * Returns a new stream resulting from replacing all elements that match the given predicate with the given element.
   *
   * @param p The predicate to match replaced elements.
   * @param a The element with which to replace elements.
   * @return A new stream resulting from replacing all elements that match the given predicate with the given element.
   */
  public final Stream<A> replace(final F<A, Boolean> p, final A a) {
    return byName(() -> uncons(nil(), (head, tail) -> {
      final P2<Stream<A>, Stream<A>> s = span(p);
      return s._1().append(cons(a, s._2().eval().unsafeTail().replace(p, a)));
    }));
  }

  /**
   * Returns a tuple where the first element is the longest prefix of this stream that does not satisfy
   * the given predicate and the second element is the remainder of the stream.
   *
   * @param p A predicate not to be satisfied by a prefix of this stream.
   * @return A tuple where the first element is the longest prefix of this stream that does not satisfy
   *         the given predicate and the second element is the remainder of the stream.
   */
  public final P2<Stream<A>, Stream<A>> split(final F<A, Boolean> p) {
    return span(compose(not, p));
  }

  /**
   * Reverse this stream in constant stack space.
   *
   * @return A new stream that is the reverse of this one.
   */
  public final Stream<A> reverse() {
    return foldLeft((as, a) -> cons(a, as), Stream.nil());
  }

  /**
   * Get the last element of this stream. Undefined for infinite streams.
   *
   * @return The last element in this stream, if there is one.
   */
  public final A last() {
    return reverse().eval().unsafeHead();
  }

  /**
   * The length of this stream. This function will not terminate for an infinite stream.
   *
   * @return The length of this stream.
   */
  public final int length() {
    // we're using an iterative approach here as the previous implementation (toList().length()) took
    // very long even for some 10000 elements.
    EvaluatedStream<A> xs = this.eval();
    int i = 0;
    while (!xs.isEmpty()) {
      xs = xs.unsafeTail().eval();
      i += 1;
    }
    return i;
  }

  /**
   * Returns the element at the given index if it exists, fails otherwise.
   *
   * @param i The index at which to get the element to return.
   * @return The element at the given index if it exists, fails otherwise.
   */
  public final A index(final int i) {
    if (i < 0)
      throw error("index " + i + " out of range on stream");
    else {
      EvaluatedStream<A> xs = this.eval();

      for (int c = 0; c < i; c++) {
        if (xs.isEmpty())
          throw error("index " + i + " out of range on stream");

        xs = xs.unsafeTail().eval();
      }

      if (xs.isEmpty())
        throw error("index " + i + " out of range on stream");

      return xs.unsafeHead();
    }
  }

  /**
   * Returns <code>true</code> if the predicate holds for all of the elements of this stream,
   * <code>false</code> otherwise (<code>true</code> for the empty stream).
   *
   * @param f the predicate function to test on each element of this stream.
   * @return <code>true</code> if the predicate holds for all of the elements of this stream,
   *         <code>false</code> otherwise.
   */
  public final boolean forall(final F<A, Boolean> f) {
    for (final A a : this) {
      if (!f.f(a)) return false;
    }
    return true;
  }

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(Stream.class, this, other, () -> Equal.streamEqual(Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.streamHash(Hash.<A>anyHash()).hash(this);
  }

  @Override
  public final String toString() {
    return toStringLazy();
  }

  public final String toStringLazy() {
    return uncons("Nil()", (head, tail) -> "Cons(" + Show.<A>anyShow().showS(head) + ", ?)");
  }

  public final String toStringEager() {
    return Show.streamShow(Show.<A>anyShow()).showS(this);
  }

  /**
   * Returns <code>true</code> if the predicate holds for at least one of the elements of this
   * stream, <code>false</code> otherwise (<code>false</code> for the empty stream).
   *
   * @param f The predicate function to test on the elements of this stream.
   * @return <code>true</code> if the predicate holds for at least one of the elements of this
   *         stream.
   */
  public final boolean exists(final F<A, Boolean> f) {
    return !dropWhile(not(f)).isEmpty();
  }

  /**
   * Finds the first occurrence of an element that matches the given predicate or no value if no
   * elements match.
   *
   * @param f The predicate function to test on elements of this stream.
   * @return The first occurrence of an element that matches the given predicate or no value if no
   *         elements match.
   */
  public final Option<A> find(final F<A, Boolean> f) {
    for (EvaluatedStream<A> as = this.eval(); !as.isEmpty(); as = as.unsafeTail().eval()) {
      if (f.f(as.unsafeHead()))
        return some(as.unsafeHead());
    }

    return none();
  }

  /**
   * Binds the given function across the stream of substreams of this stream.
   *
   * @param k A function to bind across this stream and its substreams.
   * @return a new stream of the results of applying the given function to this stream and its substreams.
   */
  public final <B> Stream<B> cobind(final F<Stream<A>, B> k) {
    return substreams().map(k);
  }

  /**
   * Returns a stream of the suffixes of this stream. A stream is considered to be a suffix of itself in this context.
   *
   * @return a stream of the suffixes of this stream, starting with the stream itself.
   */
  public final Stream<Stream<A>> tails() {
    return byName(() -> uncons(Stream.nil(), (head, tail) -> cons(this, tail.tails())));
  }

  /**
   * Returns a stream of all prefixes of this stream. A stream is considered a prefix of itself in tnis context.
   *
   * @return a stream of the prefixes of this stream, starting with the stream itself.
   */
  public final Stream<Stream<A>> inits() {
    final Stream<Stream<A>> nil = cons(Stream.nil(), Stream::nil);
    return byName(() -> uncons(nil, (head, tail) -> nil.append(tail.inits().map(t -> cons(head, t)))));
  }

  /**
   * Returns a stream of all infixes of this stream. A stream is considered to contain itself.
   *
   * @return a stream of the infixes of this stream.
   */
  public final Stream<Stream<A>> substreams() {
    return tails().bind(Stream::inits);
  }

  /**
   * Returns the position of the first element matching the given predicate, if any.
   *
   * @param p A predicate to match.
   * @return the position of the first element matching the given predicate, if any.
   */
  public final Option<Integer> indexOf(final F<A, Boolean> p) {
    return zipIndex().find(p2 -> p.f(p2._1())).map(P2.__2());
  }

  /**
   * Applies a stream of comonadic functions to this stream, returning a stream of values.
   *
   * @param fs A stream of comonadic functions to apply to this stream.
   * @return A new stream of the results of applying the stream of functions to this stream.
   */
  public final <B> Stream<B> sequenceW(final Stream<F<Stream<A>, B>> fs) {
    return byName(() -> fs.uncons(Stream.nil(), (head, tail) ->
           cons(head.f(this), sequenceW(tail))));
  }

  /**
   * Converts this stream to a function of natural numbers.
   *
   * @return A function from natural numbers to values with the corresponding position in this stream.
   */
  public final F<Integer, A> toFunction() {
    return this::index;
  }

  /**
   * Converts a function of natural numbers to a stream.
   *
   * @param f The function to convert to a stream.
   * @return A new stream of the results of the given function applied to the natural numbers, starting at 0.
   */
  public static <A> Stream<A> fromFunction(final F<Natural, A> f) {
    return fromFunction(Enumerator.naturalEnumerator, f, Natural.ZERO);
  }

  /**
   * Converts a function of an enumerable type to a stream of the results of that function,
   * starting at the given index.
   *
   * @param e An enumerator for the domain of the function.
   * @param f The function to convert to a stream.
   * @param i The index into the function at which to begin the stream.
   * @return A new stream of the results of the given function applied to the values of the given enumerator,
   *         starting at the given value.
   */
  public static <A, B> Stream<A> fromFunction(final Enumerator<B> e, final F<B, A> f, final B i) {
    return cons(f.f(i), () -> {
        final Option<B> s = e.successor(i);
        return s.isSome()
               ? fromFunction(e, f, s.some())
               : Stream.nil();
      });
  }

  /**
   * Transforms a stream of pairs into a stream of first components and a stream of second components.
   *
   * @param xs The stream of pairs to transform.
   * @return A stream of first components and a stream of second components.
   */
  public static <A, B> P2<Stream<A>, Stream<B>> unzip(final Stream<P2<A, B>> xs) {
    return xs.foldRight((p, ps) -> {
      final P1<P2<Stream<A>, Stream<B>>> pp = P.weakMemo(ps);
      return p(cons(p._1(), byName(() -> pp._1()._1())), cons(p._2(), byName(() -> pp._1()._2())));
    }, p(Stream.nil(), Stream.nil()));
  }

  /**
   * A first-class version of the zipWith function.
   *
   * @return a function that zips two given streams with a given function.
   */
  public static <A, B, C> F<Stream<A>, F<Stream<B>, F<F<A, F<B, C>>, Stream<C>>>> zipWith() {
    return curry((F3<Stream<A>, Stream<B>, F<A, F<B, C>>, Stream<C>>) Stream::zipWith);
  }

  /**
   * A stream evaluated to weak head normal form (WHNF): constructor and parameters are available,
   * but the tail of the stream may be a "by-name" or lazy "thunk" (not evaluated, potentially infinite).
   */
  public static abstract class EvaluatedStream<A> extends Stream<A> {
    EvaluatedStream() {
    }

    @Override
    public final EvaluatedStream<A> eval() {
      return this;
    }

    /**
     * Returns <code>true</code> if this stream is empty, <code>false</code> otherwise.
     *
     * @return <code>true</code> if this stream is empty, <code>false</code> otherwise.
     */
    public final boolean isEmpty() {
      return this instanceof Nil<?>;
    };

    public abstract A unsafeHead();
    public abstract Stream<A> unsafeTail();
  }

  private static final class Nil<A> extends EvaluatedStream<A> {

    static final Stream<?> NIL = new Nil<>();

    @Override
    public <B> B uncons(B nil, F2<A, Stream<A>, B> cons) {
      return nil;
    }

    @Override
    public A unsafeHead() {
      throw error("head on empty stream");
    }

    @Override
    public Stream<A> unsafeTail() {
      throw error("tail on empty stream");
    }
  }

  private static final class Cons<A> extends EvaluatedStream<A> {
    final A head;
    final Stream<A> tail;

    Cons(final A head, final Stream<A> tail) {
      this.head = head;
      this.tail = tail;
    }

    @Override
    public <B> B uncons(B nil, F2<A, Stream<A>, B> cons) {
      return cons.f(head, tail);
    }

    @Override
    public A unsafeHead() {
      return head;
    }

    @Override
    public Stream<A> unsafeTail() {
      return tail;
    }
  }

  static <A> EvaluatedStream<A> eval(F0<Stream<A>> expr) {
    F0<Stream<A>> next = expr;
    do {
      Stream<A> value = next.f();
      if (value instanceof Stream.EvaluatedStream<?>)
        return (EvaluatedStream<A>) value;
      if (value instanceof Lazy<?>) {
        next = ((Lazy<A>) value).expression;
        if (next == null)
          return ((Lazy<A>) value).evaluation;
      } else if (value instanceof ByName<?>)
        next = ((ByName<A>) value).expression;
      else if (value instanceof WeakMemo<?>) {
        next = ((WeakMemo<A>) value).expression;
        if (next == null)
          return (EvaluatedStream<A>) Nil.NIL;
        EvaluatedStream<A> weakValue = ((WeakMemo<A>) value).value();
        if (weakValue != null) {
          return weakValue;
        }
      }
    } while (true);
  }



  private static final class Lazy<A> extends Stream<A> {

    private volatile F0<Stream<A>> expression;

    private EvaluatedStream<A> evaluation;

    Lazy(F0<Stream<A>> expression) {
      this.expression = expression;
    }

    @Override
    public <B> B uncons(B nil, F2<A, Stream<A>, B> cons) {
      return eval().uncons(nil, cons);
    }

    @Override
    public EvaluatedStream<A> eval() {
      return (this.expression == null ? this.evaluation : evaluate());
    }

    private synchronized EvaluatedStream<A> evaluate() {
      F0<Stream<A>> e = expression;
      if (e != null) {
        evaluation = eval(e);
        expression = null;
      }
      return evaluation;
    }
  }


  private static final class ByName<A> extends Stream<A> {

    final F0<Stream<A>> expression;

    ByName(F0<Stream<A>> expression) {
      this.expression = expression;
    }

    @Override
    public <B> B uncons(B nil, F2<A, Stream<A>, B> cons) {
      return eval(expression).uncons(nil, cons);
    }

    @Override
    public EvaluatedStream<A> eval() {
      return eval(expression);
    }
  }


  private static final class WeakMemo<A> extends Stream<A> {

    private static <V> V memo(V v, WeakReference<V> wref, AtomicReference<WeakReference<V>> ref) {
      if (ref.compareAndSet(wref, new WeakReference<>(v)))
        return v;
      V crt = ref.get().get();
      return (crt != null) ? crt : v;
    }

    private volatile F0<Stream<A>> expression;
    private final AtomicReference<WeakReference<A>> head = new AtomicReference<>();
    private final AtomicReference<WeakReference<Stream<A>>> tail = new AtomicReference<>();

    WeakMemo(F0<Stream<A>> expression) {
      this.expression = expression;
    }

    @Override
    public <B> B uncons(B nil, F2<A, Stream<A>, B> cons) {
      F0<Stream<A>> expr = expression;
      if (expr == null)
        return nil;
      WeakReference<A> href = head.get();
      if (href == null) {
        B res = eval(expr).uncons((B) UNCONS_NIL, (a, as) -> cons.f(memo(a, null, this.head), memo(as, null, this.tail)));
        if (res == UNCONS_NIL) {
          expression = null;
          return nil;
        }
        return res;
      }
      A weakHead = href.get();
      WeakReference<Stream<A>> tref = tail.get();
      Stream<A> weakTail = tref == null ? null : tref.get();
      if (weakHead == null | weakTail == null) {
        B res = eval(expr).uncons((B) UNCONS_NIL, (a, as) -> cons.f(memo(weakHead == null ? a : weakHead, href, this.head), memo(weakTail == null ? as : weakTail, tref, this.tail)));
        if (res == UNCONS_NIL) {
          expression = null;
          return nil;
        }
        return res;
      }
      return cons.f(weakHead, weakTail);
    }

    @Override
    public EvaluatedStream<A> eval() {
      F0<Stream<A>> expr = expression;
      if (expr == null)
        return (EvaluatedStream<A>) Nil.NIL;
      WeakReference<A> href = head.get();
      if (href == null) {
        EvaluatedStream<A> eval = eval(expr);
        if (eval.uncons(UNCONS_NIL, (a, as) -> {
          memo(a, null, this.head);
          memo(as, null, this.tail);
          return unit();
        }) == UNCONS_NIL) {
          expression = null;
        }
        return eval;
      }
      A weakHead = href.get();
      WeakReference<Stream<A>> tref = tail.get();
      Stream<A> weakTail = tref == null ? null : tref.get();
      if (weakHead == null | weakTail == null) {
        EvaluatedStream<A> eval = eval(expr);
        if (eval.uncons(UNCONS_NIL, (a, as) -> {
          memo(weakHead == null ? a : weakHead, href, this.head);
          memo(weakTail == null ? as : weakTail, tref, this.tail);
          return unit();
        }) == UNCONS_NIL) {
          expression = null;
        }
        return eval;
      }
      return new Cons<>(weakHead, weakTail);
    }

    EvaluatedStream<A> value() {
      WeakReference<Stream<A>> tref = tail.get();
      if (tref == null)
        return null;
      Stream<A> as = tref.get();
      A a = head.get().get();
      if (a == null | as == null)
        return null;
      return new Cons<>(a, as);
    }

    @Override
    public boolean isEmpty() {
      if (head.get() != null){
        return false;
      }
      return uncons(true, (t, h) -> false);
    }
  }

  /**
   * Returns a function that prepends (cons) an element to a stream to produce a new stream.
   *
   * @return A function that prepends (cons) an element to a stream to produce a new stream.
   */
  public static <A> F<A, F<F0<Stream<A>>, Stream<A>>> cons() {
    return a -> list -> cons(a, list);
  }

  /**
   * Returns a function that prepends (cons) an element to a stream to produce a new stream.
   *
   * @return A function that prepends (cons) an element to a stream to produce a new stream.
   */
  public static <A> F<A, F<Stream<A>, Stream<A>>> cons_() {
    return curry((a, as) -> as.cons(a));
  }

  /**
   * Returns an empty stream.
   *
   * @return An empty stream.
   */
  public static <A> Stream<A> nil() {
    return (Stream<A>) Nil.NIL;
  }

  /**
   * Returns an empty stream.
   *
   * @return An empty stream.
   */
  public static <A> P1<Stream<A>> nil_() {
    return P.lazy(Nil::new);
  }

  /**
   * Returns a function that determines whether a given stream is empty.
   *
   * @return A function that determines whether a given stream is empty.
   */
  public static <A> F<Stream<A>, Boolean> isEmpty_() {
    return Stream::isEmpty;
  }

  /**
   * Returns a function that determines whether a given stream is not empty.
   *
   * @return A function that determines whether a given stream is not empty.
   */
  public static <A> F<Stream<A>, Boolean> isNotEmpty_() {
    return Stream::isNotEmpty;
  }

  /**
   * Returns a stream of one element containing the given value.
   *
   * @param a The value for the head of the returned stream.
   * @return A stream of one element containing the given value.
   */
  public static <A> Stream<A> single(final A a) {
    return cons(a, nil());
  }

  /**
   * Returns a function that yields a stream containing its argument.
   *
   * @return a function that yields a stream containing its argument.
   */
  public static <A> F<A, Stream<A>> single() {
    return Stream::single;
  }

  /**
   * Prepends the given head element to the given tail element to produce a new stream.
   *
   * @param head The element to prepend.
   * @param tail The stream to prepend to.
   * @return The stream with the given element prepended.
   */
  public static <A> Stream<A> cons(final A head, final F0<Stream<A>> tail) {
    return new Cons<>(head, new ByName<>(tail));
  }

  public static <A> Stream<A> cons(final A head, final Stream<A> tail) {
    return new Cons<>(head, tail);
  }

  public static <A> Stream<A> memo(final F0<Stream<A>> s) {
    return new WeakMemo<>(s);
  }

  public static <A> Stream<A> lazy(final F0<Stream<A>> s) {
    return new Lazy<>(s);
  }

  public static <A> Stream<A> byName(F0<Stream<A>> s) {
    return new ByName<>(s);
  }

  /**
   * Joins the given stream of streams by concatenation.
   *
   * @param o The stream of streams to join.
   * @return A new stream that is the join of the given streams.
   */
  public static <A> Stream<A> join(final Stream<Stream<A>> o) { return o.bind(identity()); }

  /**
   * A first-class version of join
   *
   * @return A function that joins a stream of streams using a bind operation.
   */
  public static <A> F<Stream<Stream<A>>, Stream<A>> join() {
    return Stream::join;
  }

  /**
   * Unfolds across the given function starting at the given value to produce a stream.
   *
   * @param f The function to unfold across.
   * @param b The start value to begin the unfold.
   * @return A new stream that is a result of unfolding until the function does not produce a
   *         value.
   */
  public static <A, B> Stream<A> unfold(final F<B, Option<P2<A, B>>> f, final B b) {
    return byName(() -> f.f(b).option(nil(), p -> cons(p._1(), unfold(f, p._2()))));
  }

  /**
   * Creates a stream where the first item is calculated by applying the function on the third argument,
   * the second item by applying the function on the previous result and so on.
   *
   * @param f The function to iterate with.
   * @param p The predicate which must be true for the next item in order to continue the iteration.
   * @param a The input to the first iteration.
   * @return A stream where the first item is calculated by applying the function on the third argument,
   *         the second item by applying the function on the previous result and so on.
   */
  public static <A> Stream<A> iterateWhile(final F<A, A> f, final F<A, Boolean> p, final A a) {
    return unfold(
            o -> Option.iif(p2 -> p.f(o), p(o, f.f(o)))
            , a);
  }

  /**
   * Takes the given iterable to a stream.
   *
   * @param i The iterable to take to a stream.
   * @return A stream from the given iterable.
   */
  public static <A> Stream<A> iterableStream(final Iterable<A> i) {
    return lazy(() -> iteratorStream(i.iterator()));
  }

  /**
   * Takes the given iterable to a stream.
   *
   * @param i The iterable to take to a stream.
   * @return A stream from the given iterable.
   */
  public static <A> Stream<A> nonReusableIterableStream(final Iterable<A> i) {
    return byName(() -> nonReusableIteratorStream(i.iterator()));
  }

  @SafeVarargs
  public static <A> Stream<A> arrayStream(final A...as) {
    return as.length == 0 ? Stream.nil()
            : unfold(P2.tuple((as1, i) -> i >= as.length ? Option.none()
            : some(p(as[i], p(as, i + 1)))), p(as, 0));
  }

  /**
   * Returns an infinite-length stream of the given element.
   *
   * @param a The element to repeat infinitely.
   * @return An infinite-length stream of the given element.
   */
  public static <A> Stream<A> repeat(final A a) {
    return new Object() {
      final Stream<A> repeat = cons(a, () -> this.repeat);
    }.repeat;
  }

  /**
   * Returns an infinite-length stream of the given elements cycling. Fails on the empty stream.
   *
   * @param as The elements to cycle infinitely. This must not be empty.
   * @return An infinite-length stream of the given elements cycling.
   */
  public static <A> Stream<A> cycle(final Stream<A> as) {
    Stream<A> eval = as.eval();
    if (eval.isEmpty())
      throw error("cycle on empty list");
    else
      return new Object() {
        final Stream<A> cycle = eval.append(() -> this.cycle).eval();
      }.cycle;
  }

  /**
   * Returns a stream constructed by applying the given iteration function starting at the given value.
   *
   * @param f The iteration function.
   * @param a The value to begin iterating from.
   * @return A stream constructed by applying the given iteration function starting at the given value.
   */
  public static <A> Stream<A> iterate(final F<A, A> f, final A a) {
    return cons(a, () -> iterate(f, f.f(a)));
  }

  /**
   * A first-class version of the iterate function.
   *
   * @return A function that returns a stream constructed by applying a given iteration function
   *         starting at a given value.
   */
  public static <A> F<F<A, A>, F<A, Stream<A>>> iterate() {
    return curry(Stream::iterate);
  }

  /**
   * A first-class version of the bind function.
   *
   * @return A function that binds a given function across a given stream, joining the resulting streams.
   */
  public static <A, B> F<F<A, Stream<B>>, F<Stream<A>, Stream<B>>> bind_() {
    return curry((f, as) -> as.bind(f));
  }

  /**
   * A first-class version of the foldRight function.
   *
   * @return A function that folds a given stream with a given function.
   */
  public static <A, B> F<Stream<A>, B> cata(B nil, F2<A, F0<B>, B> cons) {
    return new F<Stream<A>, B>() {
      @Override
      public B f(Stream<A> as) {
        return as.uncons(nil, (head, tail) -> cons.f(head, () -> f(tail)));
      }
    };
  }

  /**
   * A first-class version of the foldRight function.
   *
   * @return A function that folds a given stream with a given function.
   */
  public static <A, B> F<F<A, F<F0<B>, B>>, F<B, F<Stream<A>, B>>> foldRight() {
    return (f -> (z -> cata(z, (a, b) -> f.f(a).f(b))));
  }

  private static final Object UNCONS_NIL = new Object();
}
