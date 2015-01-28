package fj.data;

import fj.Equal;
import fj.Hash;
import fj.Show;
import fj.F;
import fj.F2;
import fj.Function;
import fj.Monoid;
import fj.Ord;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.Ordering;
import fj.function.Effect1;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static fj.Bottom.error;
import static fj.Function.compose;
import static fj.Function.constant;
import static fj.Function.curry;
import static fj.Function.flip;
import static fj.Function.identity;
import static fj.P.p;
import static fj.P.p2;
import static fj.Unit.unit;
import static fj.control.parallel.Promise.promise;
import static fj.data.Array.mkArray;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.function.Booleans.not;
import static fj.Ordering.EQ;
import static fj.Ordering.GT;
import static fj.Ordering.LT;

/**
 * A lazy (not yet evaluated), immutable, singly linked list.
 *
 * @version %build.number%
 */
public abstract class Stream<A> implements Iterable<A> {
  private Stream() {

  }

  /**
   * Returns an iterator for this stream. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this stream.
   */
  public final Iterator<A> iterator() {
    return toCollection().iterator();
  }

  /**
   * The first element of the stream or fails for the empty stream.
   *
   * @return The first element of the stream or fails for the empty stream.
   */
  public abstract A head();

  /**
   * The stream without the first element or fails for the empty stream.
   *
   * @return The stream without the first element or fails for the empty stream.
   */
  public abstract P1<Stream<A>> tail();

  /**
   * Returns <code>true</code> if this stream is empty, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this stream is empty, <code>false</code> otherwise.
   */
  public final boolean isEmpty() {
    return this instanceof Nil;
  }

  /**
   * Returns <code>false</code> if this stream is empty, <code>true</code> otherwise.
   *
   * @return <code>false</code> if this stream is empty, <code>true</code> otherwise.
   */
  public final boolean isNotEmpty() {
    return this instanceof Cons;
  }

  /**
   * Performs a reduction on this stream using the given arguments.
   *
   * @param nil  The value to return if this stream is empty.
   * @param cons The function to apply to the head and tail of this stream if it is not empty.
   * @return A reduction on this stream.
   */
  public final <B> B stream(final B nil, final F<A, F<P1<Stream<A>>, B>> cons) {
    return isEmpty() ? nil : cons.f(head()).f(tail());
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight(final F<A, F<P1<B>, B>> f, final B b) {
    return isEmpty() ? b : f.f(head()).f(new P1<B>() {
      public B _1() {
        return tail()._1().foldRight(f, b);
      }
    });
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight(final F2<A, P1<B>, B> f, final B b) {
    return foldRight(curry(f), b);
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight1(final F<A, F<B, B>> f, final B b) {
    return foldRight(compose(Function.<P1<B>, B, B>andThen().f(P1.<B>__1()), f), b);
  }

  /**
   * Performs a right-fold reduction across this stream. This function uses O(length) stack space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the right-fold reduction.
   */
  public final <B> B foldRight1(final F2<A, B, B> f, final B b) {
    return foldRight1(curry(f), b);
  }

  /**
   * Performs a left-fold reduction across this stream. This function runs in constant space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the left-fold reduction.
   */
  public final <B> B foldLeft(final F<B, F<A, B>> f, final B b) {
    B x = b;

    for (Stream<A> xs = this; !xs.isEmpty(); xs = xs.tail()._1())
      x = f.f(x).f(xs.head());

    return x;
  }

  /**
   * Performs a left-fold reduction across this stream. This function runs in constant space.
   *
   * @param f The function to apply on each element of the stream.
   * @param b The beginning value to start the application from.
   * @return The final result after the left-fold reduction.
   */
  public final <B> B foldLeft(final F2<B, A, B> f, final B b) {
    return foldLeft(curry(f), b);
  }

  /**
   * Takes the first 2 elements of the stream and applies the function to them,
   * then applies the function to the result and the third element and so on.
   *
   * @param f The function to apply on each element of the stream.
   * @return The final result after the left-fold reduction.
   */
  public final A foldLeft1(final F2<A, A, A> f) {
    return foldLeft1(curry(f));
  }

  /**
   * Takes the first 2 elements of the stream and applies the function to them,
   * then applies the function to the result and the third element and so on.
   *
   * @param f The function to apply on each element of the stream.
   * @return The final result after the left-fold reduction.
   */
  public final A foldLeft1(final F<A, F<A, A>> f) {
    if (isEmpty())
      throw error("Undefined: foldLeft1 on empty list");
    return tail()._1().foldLeft(f, head());
  }

  /**
   * Returns the head of this stream if there is one or the given argument if this stream is empty.
   *
   * @param a The argument to return if this stream is empty.
   * @return The head of this stream if there is one or the given argument if this stream is empty.
   */
  public final A orHead(final P1<A> a) {
    return isEmpty() ? a._1() : head();
  }

  /**
   * Returns the tail of this stream if there is one or the given argument if this stream is empty.
   *
   * @param as The argument to return if this stream is empty.
   * @return The tail of this stream if there is one or the given argument if this stream is empty.
   */
  public final P1<Stream<A>> orTail(final P1<Stream<A>> as) {
    return isEmpty() ? as : tail();
  }

  /**
   * Intersperses the given value between each two elements of the stream.
   *
   * @param a The value to intersperse between values of the stream.
   * @return A new stream with the given value between each two elements of the stream.
   */
  public final Stream<A> intersperse(final A a) {
    return isEmpty() ? this : cons(head(), new P1<Stream<A>>() {
      public Stream<A> _1() {
        return prefix(a, tail()._1());
      }

      public Stream<A> prefix(final A x, final Stream<A> xs) {
        return xs.isEmpty() ? xs : cons(x, p(cons(xs.head(), new P1<Stream<A>>() {
          public Stream<A> _1() {
            return prefix(a, xs.tail()._1());
          }
        })));
      }
    });
  }

  /**
   * Maps the given function across this stream.
   *
   * @param f The function to map across this stream.
   * @return A new stream after the given function has been applied to each element.
   */
  public final <B> Stream<B> map(final F<A, B> f) {
    return isEmpty() ? Stream.<B>nil() : cons(f.f(head()), new P1<Stream<B>>() {
      public Stream<B> _1() {
        return tail()._1().map(f);
      }
    });
  }

  /**
   * Provides a first-class version of the map function.
   *
   * @return A function that maps a given function across a given stream.
   */
  public static <A, B> F<F<A, B>, F<Stream<A>, Stream<B>>> map_() {
    return f -> as -> as.map(f);
  }

  /**
   * Performs a side-effect for each element of this stream.
   *
   * @param f The side-effect to perform for the given element.
   * @return The unit value.
   */
  public final Unit foreach(final F<A, Unit> f) {
    for (Stream<A> xs = this; xs.isNotEmpty(); xs = xs.tail()._1())
      f.f(xs.head());

    return unit();
  }

  /**
   * Performs a side-effect for each element of this stream.
   *
   * @param f The side-effect to perform for the given element.
   */
  public final void foreachDoEffect(final Effect1<A> f) {
    for (Stream<A> xs = this; xs.isNotEmpty(); xs = xs.tail()._1())
      f.f(xs.head());
  }

  /**
   * Filters elements from this stream by returning only elements which produce <code>true</code>
   * when the given function is applied to them.
   *
   * @param f The predicate function to filter on.
   * @return A new stream whose elements all match the given predicate.
   */
  public final Stream<A> filter(final F<A, Boolean> f) {
    final Stream<A> as = dropWhile(not(f));
    return as.isNotEmpty() ? cons(as.head(), new P1<Stream<A>>() {
      public Stream<A> _1() {
        return as.tail()._1().filter(f);
      }
    }) : as;
  }

  /**
   * Appends the given stream to this stream.
   *
   * @param as The stream to append to this one.
   * @return A new stream that has appended the given stream.
   */
  public final Stream<A> append(final Stream<A> as) {
    return isEmpty() ? as : cons(head(), new P1<Stream<A>>() {
      public Stream<A> _1() {
        return tail()._1().append(as);
      }
    });
  }

  /**
   * Appends the given stream to this stream.
   *
   * @param as The stream to append to this one.
   * @return A new stream that has appended the given stream.
   */
  public final Stream<A> append(final P1<Stream<A>> as) {
    return isEmpty() ? as._1() : cons(head(), new P1<Stream<A>>() {
      public Stream<A> _1() {
        return tail()._1().append(as);
      }
    });
  }

  /**
   * Returns a new stream of all the items in this stream that do not appear in the given stream.
   *
   * @param eq an equality for the items of the streams.
   * @param xs a list to subtract from this stream.
   * @return a stream of all the items in this stream that do not appear in the given stream.
   */
  public final Stream<A> minus(final Equal<A> eq, final Stream<A> xs) {
    return removeAll(compose(Monoid.disjunctionMonoid.sumLeftS(), xs.mapM(curry(eq.eq()))));
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
    return fs.foldRight((baf, p1) -> Function.bind(baf, p1._1(), Function.curry((a, stream) -> cons(a, p(stream)))), Function
        .<B, Stream<A>>constant(Stream.<A>nil()));
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
    return map(f).foldLeft((accumulator, element) -> {
        Stream<B> result = accumulator;
        for (B single : element) {
            result = result.cons(single);
        }
        return result;
    }, Stream.<B>nil()).reverse();
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
   * Performs function application within a stream (applicative functor pattern).
   *
   * @param sf The stream of functions to apply.
   * @return A new stream after applying the given stream of functions through this stream.
   */
  public final <B> Stream<B> apply(final Stream<F<A, B>> sf) {
    return sf.bind(f -> map(a -> f.f(a)));
  }

  /**
   * Interleaves the given stream with this stream to produce a new stream.
   *
   * @param as The stream to interleave this stream with.
   * @return A new stream with elements interleaved from this stream and the given stream.
   */
  public final Stream<A> interleave(final Stream<A> as) {
    return isEmpty() ? as : as.isEmpty() ? this : cons(head(), new P1<Stream<A>>() {
      @Override public Stream<A> _1() {
        return as.interleave(tail()._1());
      }
    });
  }

  /**
   * Sort this stream according to the given ordering.
   *
   * @param o An ordering for the elements of this stream.
   * @return A new stream with the elements of this stream sorted according to the given ordering.
   */
  public final Stream<A> sort(final Ord<A> o) {
    return mergesort(o, map(flip(Stream.<A>cons()).f(p(Stream.<A>nil()))));
  }

  // Merges a stream of individually sorted streams into a single sorted stream.
  private static <A> Stream<A> mergesort(final Ord<A> o, final Stream<Stream<A>> s) {
    if (s.isEmpty())
      return nil();
    Stream<Stream<A>> xss = s;
    while (xss.tail()._1().isNotEmpty())
      xss = mergePairs(o, xss);
    return xss.head();
  }

  // Merges individually sorted streams two at a time.
  private static <A> Stream<Stream<A>> mergePairs(final Ord<A> o, final Stream<Stream<A>> s) {
    if (s.isEmpty() || s.tail()._1().isEmpty())
      return s;
    final Stream<Stream<A>> t = s.tail()._1();
    return cons(merge(o, s.head(), t.head()), new P1<Stream<Stream<A>>>() {
      public Stream<Stream<A>> _1() {
        return mergePairs(o, t.tail()._1());
      }
    });
  }

  // Merges two individually sorted streams.
  private static <A> Stream<A> merge(final Ord<A> o, final Stream<A> xs, final Stream<A> ys) {
    if (xs.isEmpty())
      return ys;
    if (ys.isEmpty())
      return xs;
    final A x = xs.head();
    final A y = ys.head();
    if (o.isGreaterThan(x, y))
      return cons(y, new P1<Stream<A>>() {
        public Stream<A> _1() {
          return merge(o, xs, ys.tail()._1());
        }
      });
    return cons(x, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return merge(o, xs.tail()._1(), ys);
      }
    });
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
    if (isEmpty())
      return promise(s, P.p(this));
    else {
      final F<Boolean, Boolean> id = identity();
      final A x = head();
      final P1<Stream<A>> xs = tail();
      final Promise<Stream<A>> left = Promise.join(s, xs.map(flt(o, s, x, id)));
      final Promise<Stream<A>> right = xs.map(flt(o, s, x, not))._1();
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
        return new Iterator<A>() {
          private Stream<A> xs = Stream.this;

          public boolean hasNext() {
            return xs.isNotEmpty();
          }

          public A next() {
            if (xs.isEmpty())
              throw new NoSuchElementException();
            else {
              final A a = xs.head();
              xs = xs.tail()._1();
              return a;
            }
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
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
    return from >= to ? Stream.<Integer>nil() : cons(from, new P1<Stream<Integer>>() {
      public Stream<Integer> _1() {
        return range(from + 1, to);
      }
    });
  }

  /**
   * Constructs a stream with the given elements.
   *
   * @param as The elements which which to construct a stream.
   * @return a new stream with the given elements.
   */
  public static <A> Stream<A> stream(final A... as) {
    return as.length == 0 ? Stream.<A>nil()
                          : unfold(P2.tuple((as1, i) -> i >= as.length ? Option.<P2<A, P2<A[], Integer>>>none()
                                                : some(P.p(as[i], P.p(as, i + 1)))), P.p(as, 0));
  }


  public static <A> Stream<A> stream(Iterable<A> it) {
    return iterableStream(it);
  }

  public static <A> Stream<A> stream(Iterator<A> it) {
    return iteratorStream(it);
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
    return cons(from, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return e.plus(from, step).map(a -> forever(e, a, step)).orSome(Stream.<A>nil());
      }
    });
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
    return o == EQ || step > 0L && o == GT || step < 0L && o == LT ? single(from) : cons(from, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return Stream.join(e.plus(from, step).filter(a -> !(o == LT
                ? e.order().isLessThan(to, a)
                : e.order().isGreaterThan(to, a))).map(a1 -> range(e, a1, to, step)).toStream());
      }
    });
  }

  /**
   * Returns an infinite stream of integers from the given <code>from</code> value (inclusive).
   *
   * @param from The minimum value for the stream (inclusive).
   * @return A stream of integers from the given <code>from</code> value (inclusive).
   */
  public static Stream<Integer> range(final int from) {
    return cons(from, new P1<Stream<Integer>>() {
      public Stream<Integer> _1() {
        return range(from + 1);
      }
    });
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
    return fs.isEmpty() || isEmpty() ? Stream.<B>nil() :
           cons(fs.head().f(head()), new P1<Stream<B>>() {
             public Stream<B> _1() {
               return tail()._1().zapp(fs.tail()._1());
             }
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
    return zipWith(range(0), (a, i) -> p(a, i));
  }

  /**
   * Returns an either projection of this stream; the given argument in <code>Left</code> if empty,
   * or the first element in <code>Right</code>.
   *
   * @param x The value to return in left if this stream is empty.
   * @return An either projection of this stream.
   */
  public final <X> Either<X, A> toEither(final P1<X> x) {
    return isEmpty() ? Either.<X, A>left(x._1()) : Either.<X, A>right(head());
  }

  /**
   * Returns an option projection of this stream; <code>None</code> if empty, or the first element
   * in <code>Some</code>.
   *
   * @return An option projection of this stream.
   */
  public final Option<A> toOption() {
    return isEmpty() ? Option.<A>none() : some(head());
  }

  /**
   * Returns a list projection of this stream.
   *
   * @return A list projection of this stream.
   */
  public final List<A> toList() {
    List<A> as = List.nil();

    for (Stream<A> x = this; !x.isEmpty(); x = x.tail()._1()) {
      as = as.snoc(x.head());
    }

    return as;
  }


  /**
   * Returns a array projection of this stream.
   *
   * @return A array projection of this stream.
   */
  @SuppressWarnings({"unchecked"})
  public final Array<A> toArray() {
    final int l = length();
    final Object[] a = new Object[l];
    Stream<A> x = this;
    for (int i = 0; i < l; i++) {
      a[i] = x.head();
      x = x.tail()._1();
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
    return new Cons<A>(a, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return Stream.this;
      }
    });
  }

  /**
   * Returns a string from the given stream of characters. The inverse of this function is {@link
   * #fromString(String)}.
   *
   * @param cs The stream of characters to produce the string from.
   * @return A string from the given stream of characters.
   */
  public static String asString(final Stream<Character> cs) {
    return LazyString.fromStream(cs).toString();
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
    return snoc(p(a));
  }

  /**
   * Append the given element to this stream to produce a new stream.
   *
   * @param a The element to append.
   * @return A new stream with the given element at the end.
   */
  public final Stream<A> snoc(final P1<A> a) {
    return append(new P1<Stream<A>>() {
      public Stream<A> _1() {
        return single(a._1());
      }
    });
  }

  /**
   * Returns the first <code>n</code> elements from the head of this stream.
   *
   * @param n The number of elements to take from this stream.
   * @return The first <code>n</code> elements from the head of this stream.
   */
  public final Stream<A> take(final int n) {
    return n <= 0 || isEmpty() ?
           Stream.<A>nil() :
           cons(head(), new P1<Stream<A>>() {
             public Stream<A> _1() {
               return tail()._1().take(n - 1);
             }
           });
  }

  /**
   * Drops the given number of elements from the head of this stream if they are available.
   *
   * @param i The number of elements to drop from the head of this stream.
   * @return A stream with a length the same, or less than, this stream.
   */
  public final Stream<A> drop(final int i) {
    int c = 0;

    Stream<A> xs = this;

    for (; xs.isNotEmpty() && c < i; xs = xs.tail()._1())
      c++;

    return xs;
  }

  /**
   * Returns the first elements of the head of this stream that match the given predicate function.
   *
   * @param f The predicate function to apply on this stream until it finds an element that does not
   *          hold, or the stream is exhausted.
   * @return The first elements of the head of this stream that match the given predicate function.
   */
  public final Stream<A> takeWhile(final F<A, Boolean> f) {
    return isEmpty() ?
           this :
           f.f(head()) ?
           cons(head(), new P1<Stream<A>>() {
             public Stream<A> _1() {
               return tail()._1().takeWhile(f);
             }
           }) :
           Stream.<A>nil();
  }

  /**
   * Removes elements from the head of this stream that do not match the given predicate function
   * until an element is found that does match or the stream is exhausted.
   *
   * @param f The predicate function to apply through this stream.
   * @return The stream whose first element does not match the given predicate function.
   */
  public final Stream<A> dropWhile(final F<A, Boolean> f) {
    Stream<A> as;
    //noinspection StatementWithEmptyBody
    for (as = this; !as.isEmpty() && f.f(as.head()); as = as.tail()._1()) ;

    return as;
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
    if (isEmpty())
      return p(this, this);
    else if (p.f(head())) {
      final P1<P2<Stream<A>, Stream<A>>> yszs = new P1<P2<Stream<A>, Stream<A>>>() {
        @Override public P2<Stream<A>, Stream<A>> _1() {
          return tail()._1().span(p);
        }
      };
      return new P2<Stream<A>, Stream<A>>() {
        @Override public Stream<A> _1() {
          return cons(head(), yszs.map(P2.<Stream<A>, Stream<A>>__1()));
        }

        @Override public Stream<A> _2() {
          return yszs._1()._2();
        }
      };
    } else
      return p(Stream.<A>nil(), this);
  }

  /**
   * Returns a new stream resulting from replacing all elements that match the given predicate with the given element.
   *
   * @param p The predicate to match replaced elements.
   * @param a The element with which to replace elements.
   * @return A new stream resulting from replacing all elements that match the given predicate with the given element.
   */
  public final Stream<A> replace(final F<A, Boolean> p, final A a) {
    if (isEmpty())
      return nil();
    else {
      final P2<Stream<A>, Stream<A>> s = span(p);
      return s._1().append(cons(a, new P1<Stream<A>>() {
        @Override public Stream<A> _1() {
          return s._2().tail()._1().replace(p, a);
        }
      }));
    }
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
    return foldLeft(as -> {
        return a -> cons(a, new P1<Stream<A>>() {
            public Stream<A> _1() {
                return as;
            }
        });
    }, Stream.<A>nil());
  }

  /**
   * Get the last element of this stream. Undefined for infinite streams.
   *
   * @return The last element in this stream, if there is one.
   */
  public final A last() {
    return reverse().head();
  }

  /**
   * The length of this stream. This function will not terminate for an infinite stream.
   *
   * @return The length of this stream.
   */
  public final int length() {
    // we're using an iterative approach here as the previous implementation (toList().length()) took
    // very long even for some 10000 elements.
    Stream<A> xs = this;
    int i = 0;
    while (!xs.isEmpty()) {
      xs = xs.tail()._1();
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
      Stream<A> xs = this;

      for (int c = 0; c < i; c++) {
        if (xs.isEmpty())
          throw error("index " + i + " out of range on stream");

        xs = xs.tail()._1();
      }

      if (xs.isEmpty())
        throw error("index " + i + " out of range on stream");

      return xs.head();
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
    return isEmpty() || f.f(head()) && tail()._1().forall(f);
  }

  @Override
  public boolean equals(Object other) {
    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.streamEqual(Equal.<A>anyEqual()).eq(this, (Stream<A>) other)));
  }

  @Override
  public int hashCode() {
    return Hash.streamHash(Hash.<A>anyHash()).hash(this);
  }

  @Override
  public String toString() {
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
    return dropWhile(not(f)).isNotEmpty();
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
    for (Stream<A> as = this; as.isNotEmpty(); as = as.tail()._1()) {
      if (f.f(as.head()))
        return some(as.head());
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
    return isEmpty() ? Stream.<Stream<A>>nil() : cons(this, new P1<Stream<Stream<A>>>() {
      public Stream<Stream<A>> _1() {
        return tail()._1().tails();
      }
    });
  }

  /**
   * Returns a stream of all prefixes of this stream. A stream is considered a prefix of itself in tnis context.
   *
   * @return a stream of the prefixes of this stream, starting with the stream itself.
   */
  public final Stream<Stream<A>> inits() {
    final Stream<Stream<A>> nil = Stream.cons(Stream.<A>nil(), new P1<Stream<Stream<A>>>() {
      public Stream<Stream<A>> _1() {
        return nil();
      }
    });
    return isEmpty() ? nil : nil.append(new P1<Stream<Stream<A>>>() {
      public Stream<Stream<A>> _1() {
        return tail()._1().inits().map(Stream.<A>cons_().f(head()));
      }
    });
  }

  /**
   * Returns a stream of all infixes of this stream. A stream is considered to contain itself.
   *
   * @return a stream of the infixes of this stream.
   */
  public final Stream<Stream<A>> substreams() {
    return tails().bind(stream -> stream.inits());
  }

  /**
   * Returns the position of the first element matching the given predicate, if any.
   *
   * @param p A predicate to match.
   * @return the position of the first element matching the given predicate, if any.
   */
  public final Option<Integer> indexOf(final F<A, Boolean> p) {
    return zipIndex().find(p2 -> p.f(p2._1())).map(P2.<A, Integer>__2());
  }

  /**
   * Applies a stream of comonadic functions to this stream, returning a stream of values.
   *
   * @param fs A stream of comonadic functions to apply to this stream.
   * @return A new stream of the results of applying the stream of functions to this stream.
   */
  public final <B> Stream<B> sequenceW(final Stream<F<Stream<A>, B>> fs) {
    return fs.isEmpty()
           ? Stream.<B>nil()
           : cons(fs.head().f(this), new P1<Stream<B>>() {
             public Stream<B> _1() {
               return sequenceW(fs.tail()._1());
             }
           });
  }

  /**
   * Converts this stream to a function of natural numbers.
   *
   * @return A function from natural numbers to values with the corresponding position in this stream.
   */
  public final F<Integer, A> toFunction() {
    return i -> index(i);
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
    return cons(f.f(i), new P1<Stream<A>>() {
      public Stream<A> _1() {
        final Option<B> s = e.successor(i);
        return s.isSome()
               ? fromFunction(e, f, s.some())
               : Stream.<A>nil();
      }
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
      final P2<Stream<A>, Stream<B>> pp = ps._1();
      return P.p(cons(p._1(), P.p(pp._1())), cons(p._2(), P.p(pp._2())));
    }, P.p(Stream.<A>nil(), Stream.<B>nil()));
  }

  /**
   * A first-class version of the zipWith function.
   *
   * @return a function that zips two given streams with a given function.
   */
  public static <A, B, C> F<Stream<A>, F<Stream<B>, F<F<A, F<B, C>>, Stream<C>>>> zipWith() {
    return curry((as, bs, f) -> as.zipWith(bs, f));
  }

  private static final class Nil<A> extends Stream<A> {
    public A head() {
      throw error("head on empty stream");
    }

    public P1<Stream<A>> tail() {
      throw error("tail on empty stream");
    }
  }

  private static final class Cons<A> extends Stream<A> {
    private final A head;
    private final P1<Stream<A>> tail;

    Cons(final A head, final P1<Stream<A>> tail) {
      this.head = head;
      this.tail = tail.memo();
    }

    public A head() {
      return head;
    }

    public P1<Stream<A>> tail() {
      return tail;
    }

  }

  /**
   * Returns a function that prepends (cons) an element to a stream to produce a new stream.
   *
   * @return A function that prepends (cons) an element to a stream to produce a new stream.
   */
  public static <A> F<A, F<P1<Stream<A>>, Stream<A>>> cons() {
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
    return new Nil<A>();
  }

  /**
   * Returns an empty stream.
   *
   * @return An empty stream.
   */
  public static <A> P1<Stream<A>> nil_() {
    return new P1<Stream<A>>() {
      public Stream<A> _1() {
        return new Nil<A>();
      }
    };
  }

  /**
   * Returns a function that determines whether a given stream is empty.
   *
   * @return A function that determines whether a given stream is empty.
   */
  public static <A> F<Stream<A>, Boolean> isEmpty_() {
    return as -> as.isEmpty();
  }

  /**
   * Returns a function that determines whether a given stream is not empty.
   *
   * @return A function that determines whether a given stream is not empty.
   */
  public static <A> F<Stream<A>, Boolean> isNotEmpty_() {
    return as -> as.isNotEmpty();
  }

  /**
   * Returns a stream of one element containing the given value.
   *
   * @param a The value for the head of the returned stream.
   * @return A stream of one element containing the given value.
   */
  public static <A> Stream<A> single(final A a) {
    return cons(a, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return nil();
      }
    });
  }

  /**
   * Returns a function that yields a stream containing its argument.
   *
   * @return a function that yields a stream containing its argument.
   */
  public static <A> F<A, Stream<A>> single() {
    return a -> single(a);
  }

  /**
   * Prepends the given head element to the given tail element to produce a new stream.
   *
   * @param head The element to prepend.
   * @param tail The stream to prepend to.
   * @return The stream with the given element prepended.
   */
  public static <A> Stream<A> cons(final A head, final P1<Stream<A>> tail) {
    return new Cons<A>(head, tail);
  }

  /**
   * Joins the given stream of streams by concatenation.
   *
   * @param o The stream of streams to join.
   * @return A new stream that is the join of the given streams.
   */
  public static <A> Stream<A> join(final Stream<Stream<A>> o) {
    return Monoid.<A>streamMonoid().sumRight(o);
  }

  /**
   * A first-class version of join
   *
   * @return A function that joins a stream of streams using a bind operation.
   */
  public static <A> F<Stream<Stream<A>>, Stream<A>> join() {
    return as -> join(as);
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
    final Option<P2<A, B>> o = f.f(b);
    if (o.isNone())
      return nil();
    else {
      final P2<A, B> p = o.some();
      return cons(p._1(), new P1<Stream<A>>() {
        public Stream<A> _1() {
          return unfold(f, p._2());
        }
      });
    }
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
            o -> Option.iif(p2 -> p.f(o), P.p(o, f.f(o)))
            , a);
  }

  /**
   * Takes the given iterable to a stream.
   *
   * @param i The iterable to take to a stream.
   * @return A stream from the given iterable.
   */
  public static <A> Stream<A> iterableStream(final Iterable<A> i) {
    return iteratorStream(i.iterator());
  }

  /**
   * Takes the given iterator to a stream.
   *
   * @param i The iterator to take to a stream.
   * @return A stream from the given iterator.
   */
  public static <A> Stream<A> iteratorStream(final Iterator<A> i) {
    if (i.hasNext()) {
      final A a = i.next();
      return cons(a, P.lazy(u -> iteratorStream(i)));
    } else
      return nil();
  }

  /**
   * Returns an infinite-length stream of the given element.
   *
   * @param a The element to repeat infinitely.
   * @return An infinite-length stream of the given element.
   */
  public static <A> Stream<A> repeat(final A a) {
    return cons(a, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return repeat(a);
      }
    });
  }

  /**
   * Returns an infinite-length stream of the given elements cycling. Fails on the empty stream.
   *
   * @param as The elements to cycle infinitely. This must not be empty.
   * @return An infinite-length stream of the given elements cycling.
   */
  public static <A> Stream<A> cycle(final Stream<A> as) {
    if (as.isEmpty())
      throw error("cycle on empty list");
    else
      return as.append(new P1<Stream<A>>() {
        public Stream<A> _1() {
          return cycle(as);
        }
      });
  }

  /**
   * Returns a stream constructed by applying the given iteration function starting at the given value.
   *
   * @param f The iteration function.
   * @param a The value to begin iterating from.
   * @return A stream constructed by applying the given iteration function starting at the given value.
   */
  public static <A> Stream<A> iterate(final F<A, A> f, final A a) {
    return cons(a, new P1<Stream<A>>() {
      public Stream<A> _1() {
        return iterate(f, f.f(a));
      }
    });
  }

  /**
   * A first-class version of the iterate function.
   *
   * @return A function that returns a stream constructed by applying a given iteration function
   *         starting at a given value.
   */
  public static <A> F<F<A, A>, F<A, Stream<A>>> iterate() {
    return curry((f, a) -> iterate(f, a));
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
  public static <A, B> F<F<A, F<P1<B>, B>>, F<B, F<Stream<A>, B>>> foldRight() {
    return curry((f, b, as) -> as.foldRight(f, b));
  }
}
