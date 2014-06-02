package fj.data;

import static fj.Bottom.error;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.P;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;
import fj.Unit;
import fj.Show;

import static fj.Function.*;
import static fj.P.p;
import static fj.Unit.unit;
import static fj.data.List.cons;
import static fj.data.List.cons_;
import static fj.data.Validation.parseByte;
import static fj.data.Validation.parseDouble;
import static fj.data.Validation.parseFloat;
import static fj.data.Validation.parseInt;
import static fj.data.Validation.parseLong;
import static fj.data.Validation.parseShort;
import static fj.Show.optionShow;
import static fj.Show.anyShow;

import java.util.Collection;
import java.util.Iterator;

/**
 * An optional value that may be none (no value) or some (a value). This type is a replacement for
 * the use of <code>null</code> with better type checks.
 *
 * @version %build.number%
 */
public abstract class Option<A> implements Iterable<A> {
  private Option() {

  }

  public String toString() {
    final Show<A> s = anyShow();
    return optionShow(s).showS(this);
  }

  /**
   * Returns an iterator for this optional value. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this optional value.
   */
  public final Iterator<A> iterator() {
    return toCollection().iterator();
  }

  /**
   * Returns the value from this optional value, or fails if there is no value.
   *
   * @return The value from this optional value, or fails if there is no value.
   */
  public abstract A some();

  /**
   * Returns <code>true</code> if this optional value has a value, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this optional value has a value, <code>false</code> otherwise.
   */
  public final boolean isSome() {
    return this instanceof Some;
  }

  /**
   * Returns <code>false</code> if this optional value has a value, <code>true</code> otherwise.
   *
   * @return <code>false</code> if this optional value has a value, <code>true</code> otherwise.
   */
  public final boolean isNone() {
    return this instanceof None;
  }

  /**
   * A first-class version of the isSome method.
   *
   * @return A function that returns true if a given optional value has a value, otherwise false.
   */
  public static <A> F<Option<A>, Boolean> isSome_() {
    return new F<Option<A>, Boolean>() {
      public Boolean f(final Option<A> a) {
        return a.isSome();
      }
    };
  }

  /**
   * A first-class version of the isNone method.
   *
   * @return A function that returns false if a given optional value has a value, otherwise true.
   */
  public static <A> F<Option<A>, Boolean> isNone_() {
    return new F<Option<A>, Boolean>() {
      public Boolean f(final Option<A> a) {
        return a.isNone();
      }
    };
  }

  /**
   * Performs a reduction on this optional value using the given arguments.
   *
   * @param b The value to return if this optional value has no value.
   * @param f The function to apply to the value of this optional value.
   * @return A reduction on this optional value.
   */
  public final <B> B option(final B b, final F<A, B> f) {
    return isSome() ? f.f(some()) : b;
  }

  /**
   * Performs a reduction on this optional value using the given arguments.
   *
   * @param b The value to return if this optional value has no value.
   * @param f The function to apply to the value of this optional value.
   * @return A reduction on this optional value.
   */
  public final <B> B option(final P1<B> b, final F<A, B> f) {
    return isSome() ? f.f(some()) : b._1();
  }

  /**
   * Returns the length of this optional value; 1 if there is a value, 0 otherwise.
   *
   * @return The length of this optional value; 1 if there is a value, 0 otherwise.
   */
  public final int length() {
    return isSome() ? 1 : 0;
  }

  /**
   * Returns the value of this optional value or the given argument.
   *
   * @param a The argument to return if this optiona value has no value.
   * @return The value of this optional value or the given argument.
   */
  public final A orSome(final P1<A> a) {
    return isSome() ? some() : a._1();
  }

  /**
   * Returns the value of this optional value or the given argument.
   *
   * @param a The argument to return if this optiona value has no value.
   * @return The value of this optional value or the given argument.
   */
  public final A orSome(final A a) {
    return isSome() ? some() : a;
  }

  /**
   * Returns the value of this optional value or fails with the given message.
   *
   * @param message The message to fail with if this optional value has no value.
   * @return The value of this optional value if there there is one.
   */
  public final A valueE(final P1<String> message) {
    if(isSome())
      return some();
    else
      throw error(message._1());
  }

  /**
   * Returns the value of this optional value or fails with the given message.
   *
   * @param message The message to fail with if this optional value has no value.
   * @return The value of this optional value if there there is one.
   */
  public final A valueE(final String message) {
    if(isSome())
      return some();
    else
      throw error(message);
  }

  /**
   * Maps the given function across this optional value.
   *
   * @param f The function to map across this optional value.
   * @return A new optional value after the given function has been applied to its element.
   */
  public final <B> Option<B> map(final F<A, B> f) {
    return isSome() ? some(f.f(some())) : Option.<B>none();
  }

  /**
   * A first-class map function.
   *
   * @return A function that maps a given function across a given optional value.
   */
  public static <A, B> F<F<A, B>, F<Option<A>, Option<B>>> map() {
    return curry(new F2<F<A, B>, Option<A>, Option<B>>() {
      public Option<B> f(final F<A, B> abf, final Option<A> option) {
        return option.map(abf);
      }
    });
  }

  /**
   * Performs a side-effect for the value of this optional value.
   *
   * @param f The side-effect to perform for the given element.
   * @return The unit value.
   */
  public final Unit foreach(final F<A, Unit> f) {
    return isSome() ? f.f(some()) : unit();
  }

  /**
   * Performs a side-effect for the value of this optional value.
   *
   * @param f The side-effect to perform for the given element.
   */
  public final void foreach(final Effect<A> f) {
    if (isSome())
      f.e(some());
  }

  /**
   * Filters elements from this optional value by returning only elements which produce
   * <code>true</code> when the given function is applied to them.
   *
   * @param f The predicate function to filter on.
   * @return A new optional value whose value matches the given predicate if it has one.
   */
  public final Option<A> filter(final F<A, Boolean> f) {
    return isSome() ? f.f(some()) ? this : Option.<A>none() : Option.<A>none();
  }

  /**
   * Binds the given function across the element of this optional value with a final join.
   *
   * @param f The function to apply to the element of this optional value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B> Option<B> bind(final F<A, Option<B>> f) {
    return isSome() ? f.f(some()) : Option.<B>none();
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C> Option<C> bind(final Option<B> ob, final F<A, F<B, C>> f) {
    return ob.apply(map(f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D> Option<D> bind(final Option<B> ob, final Option<C> oc, final F<A, F<B, F<C, D>>> f) {
    return oc.apply(bind(ob, f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param od A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D, E> Option<E> bind(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                     final F<A, F<B, F<C, F<D, E>>>> f) {
    return od.apply(bind(ob, oc, f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param od A given optional value to bind the given function with.
   * @param oe A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D, E, F$> Option<F$> bind(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                          final Option<E> oe, final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
    return oe.apply(bind(ob, oc, od, f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param od A given optional value to bind the given function with.
   * @param oe A given optional value to bind the given function with.
   * @param of A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G> Option<G> bind(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                            final Option<E> oe, final Option<F$> of,
                                            final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
    return of.apply(bind(ob, oc, od, oe, f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param od A given optional value to bind the given function with.
   * @param oe A given optional value to bind the given function with.
   * @param of A given optional value to bind the given function with.
   * @param og A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G, H> Option<H> bind(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                               final Option<E> oe, final Option<F$> of, final Option<G> og,
                                               final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
    return og.apply(bind(ob, oc, od, oe, of, f));
  }

  /**
   * Binds the given function across the element of this optional value and the given optional value
   * with a final join.
   *
   * @param ob A given optional value to bind the given function with.
   * @param oc A given optional value to bind the given function with.
   * @param od A given optional value to bind the given function with.
   * @param oe A given optional value to bind the given function with.
   * @param of A given optional value to bind the given function with.
   * @param og A given optional value to bind the given function with.
   * @param oh A given optional value to bind the given function with.
   * @param f  The function to apply to the element of this optional value and the given optional
   *           value.
   * @return A new optional value after performing the map, then final join.
   */
  public final <B, C, D, E, F$, G, H, I> Option<I> bind(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                                  final Option<E> oe, final Option<F$> of, final Option<G> og,
                                                  final Option<H> oh,
                                                  final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
    return oh.apply(bind(ob, oc, od, oe, of, og, f));
  }

  public final <B> Option<P2<A,B>> bindProduct(final Option<B> ob) {
    return bind(ob, P.<A,B>p2()); 
  }

  public final <B, C> Option<P3<A,B,C>> bindProduct(final Option<B> ob, final Option<C> oc) {
    return bind(ob, oc, P.<A,B,C>p3());
  }
  
  public final <B, C, D> Option<P4<A,B,C,D>> bindProduct(final Option<B> ob, final Option<C> oc, final Option<D> od) {
    return bind(ob, oc, od, P.<A, B, C, D>p4());
  }
  
  public final <B,C,D,E> Option<P5<A,B,C,D,E>> bindProduct(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                                     final Option<E> oe) {
    return bind(ob, oc, od, oe, P.<A, B, C, D, E>p5());
  }

  public final <B,C,D,E,F$> Option<P6<A,B,C,D,E,F$>> bindProduct(final Option<B> ob, final Option<C> oc, final Option<D> od,
                                                           final Option<E> oe, final Option<F$> of) {
    return bind(ob, oc, od, oe, of, P.<A, B, C, D, E, F$>p6());
  }

  public final <B,C,D,E,F$,G> Option<P7<A,B,C,D,E,F$,G>> bindProduct(final Option<B> ob, final Option<C> oc,
                                                               final Option<D> od, final Option<E> oe,
                                                               final Option<F$> of, final Option<G> og) {
    return bind(ob, oc, od, oe, of, og, P.<A, B, C, D, E, F$, G>p7());
  }

  public final <B,C,D,E,F$,G,H> Option<P8<A,B,C,D,E,F$,G,H>> bindProduct(final Option<B> ob, final Option<C> oc,
                                                                   final Option<D> od, final Option<E> oe,
                                                                   final Option<F$> of, final Option<G> og,
                                                                   final Option<H> oh) {
    return bind(ob, oc, od, oe, of, og, oh, P.<A,B,C,D,E,F$,G,H>p8());
  }

  /**
   * Performs a bind across the optional value, but ignores the element value in the function.
   *
   * @param o The optional value to apply in the final join.
   * @return A new optional value after the final join.
   */
  public final <B> Option<B> sequence(final Option<B> o) {
    final F<A, Option<B>> c = constant(o);
    return bind(c);
  }

  /**
   * Performs function application within an optional value (applicative functor pattern).
   *
   * @param of The optional value of functions to apply.
   * @return A new optional value after applying the given optional value of functions through this
   *         optional value.
   */
  public final <B> Option<B> apply(final Option<F<A, B>> of) {
    return of.bind(new F<F<A, B>, Option<B>>() {
      public Option<B> f(final F<A, B> f) {
        return map(new F<A, B>() {
          public B f(final A a) {
            return f.f(a);
          }
        });
      }
    });
  }

  /**
   * Returns this optional value if there is one, otherwise, returns the argument optional value.
   *
   * @param o The optional value to return if this optional value has no value.
   * @return This optional value if there is one, otherwise, returns the argument optional value.
   */
  public final Option<A> orElse(final P1<Option<A>> o) {
    return isSome() ? this : o._1();
  }

  /**
   * Returns this optional value if there is one, otherwise, returns the argument optional value.
   *
   * @param o The optional value to return if this optional value has no value.
   * @return This optional value if there is one, otherwise, returns the argument optional value.
   */
  public final Option<A> orElse(final Option<A> o) {
    return isSome() ? this : o;
  }

  /**
   * Returns an either projection of this optional value; the given argument in <code>Left</code> if
   * no value, or the value in <code>Right</code>.
   *
   * @param x The value to return in left if this optional value has no value.
   * @return An either projection of this optional value.
   */
  public final <X> Either<X, A> toEither(final P1<X> x) {
    return isSome() ? Either.<X, A>right(some()) : Either.<X, A>left(x._1());
  }

  /**
   * Returns an either projection of this optional value; the given argument in <code>Left</code> if
   * no value, or the value in <code>Right</code>.
   *
   * @param x The value to return in left if this optional value has no value.
   * @return An either projection of this optional value.
   */
  public final <X> Either<X, A> toEither(final X x) {
    return isSome() ? Either.<X, A>right(some()) : Either.<X, A>left(x);
  }

  /**
   * A first-class version of the toEither method.
   *
   * @return A function that returns an either projection of a given optional value, given a value to
   *         return in left.
   */
  public static <A, X> F<Option<A>, F<X, Either<X, A>>> toEither() {
    return curry(new F2<Option<A>, X, Either<X, A>>() {
      public Either<X, A> f(final Option<A> a, final X x) {
        return a.toEither(x);
      }
    });
  }

  /**
   * Returns a list projection of this optional value.
   *
   * @return A list projection of this optional value.
   */
  public final List<A> toList() {
    return isSome() ? cons(some(), List.<A>nil()) : List.<A>nil();
  }

  /**
   * Returns a stream projection of this optional value.
   *
   * @return A stream projection of this optional value.
   */
  public final Stream<A> toStream() {
    return isSome() ? Stream.<A>nil().cons(some()) : Stream.<A>nil();
  }

  /**
   * Returns an array projection of this optional value.
   *
   * @return An array projection of this optional value.
   */
  @SuppressWarnings({"unchecked"})
  public final Array<A> toArray() {
    return isSome() ? Array.array(some()) : Array.<A>empty();
  }

  /**
   * Returns an array projection of this optional value.
   *
   * @param c The class type of the array to return.
   * @return An array projection of this optional value.
   */
  @SuppressWarnings({"unchecked"})
  public final Array<A> toArray(final Class<A[]> c) {
    if (isSome()) {
      final A[] a = (A[]) java.lang.reflect.Array.newInstance(c.getComponentType(), 1);
      a[0] = some();
      return Array.array(a);
    } else
      return Array.array((A[]) java.lang.reflect.Array.newInstance(c.getComponentType(), 0));
  }

  /**
   * Returns an array from this optional value.
   *
   * @param c The class type of the array to return.
   * @return An array from this optional value.
   */
  public final A[] array(final Class<A[]> c) {
    return toArray(c).array(c);
  }

  /**
   * Returns the value from this optional value, or if there is no value, returns <code>null</code>.
   * This is intended for interfacing with APIs that expect a <code>null</code> for non-existence.
   *
   * @return This optional value or <code>null</code> if there is no value.
   */
  public final A toNull() {
    return orSome((A) null);
  }

  /**
   * Returns <code>true</code> if this optional value has no value, or the predicate holds for the
   * given predicate function, <code>false</code> otherwise.
   *
   * @param f the predicate function to test on the value of this optional value.
   * @return <code>true</code> if this optional value has no value, or the predicate holds for the
   *         given predicate function, <code>false</code> otherwise.
   */
  public final boolean forall(final F<A, Boolean> f) {
    return isNone() || f.f(some());
  }

  /**
   * Returns <code>true</code> is this optional value has a value and the given predicate function
   * holds on that value, <code>false</code> otherwise.
   *
   * @param f the predicate function to test on the value of this optional value.
   * @return <code>true</code> is this optional value has a value and the given predicate function
   *         holds on that value, <code>false</code> otherwise.
   */
  public final boolean exists(final F<A, Boolean> f) {
    return isSome() && f.f(some());
  }

  /**
   * Projects an immutable collection of this optional value.
   *
   * @return An immutable collection of this optional value.
   */
  public final Collection<A> toCollection() {
    return toList().toCollection();
  }

  private static final class None<A> extends Option<A> {
    public A some() {
      throw error("some on None");
    }

    @Override
    public int hashCode() {
       return 31;
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       return true;
    }
  }

  private static final class Some<A> extends Option<A> {
    private final A a;

    Some(final A a) {
      this.a = a;
    }

    public A some() {
      return a;
    }

    @Override
    public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result + ((a == null) ? 0 : a.hashCode());
       return result;
    }

    @Override
    public boolean equals(Object obj) {
       if (this == obj)
          return true;
       if (obj == null)
          return false;
       if (getClass() != obj.getClass())
          return false;
       Some<?> other = (Some<?>) obj;
       if (a == null) {
          if (other.a != null)
             return false;
       } else if (!a.equals(other.a))
          return false;
       return true;
    }

  }

  public static <T> F<T, Option<T>> some_() {
    return new F<T, Option<T>>() {
      public Option<T> f(final T t) {
        return some(t);
      }
    };
  }

  /**
   * Constructs an optional value that has a value of the given argument.
   *
   * @param t The value for the returned optional value.
   * @return An optional value that has a value of the given argument.
   */
  public static <T> Option<T> some(final T t) {
    return new Some<T>(t);
  }

  public static <T> F<T, Option<T>> none_() {
    return new F<T, Option<T>>() {
      public Option<T> f(final T t) {
        return none();
      }
    };
  }

  /**
   * Constructs an optional value that has no value.
   *
   * @return An optional value that has no value.
   */
  public static <T> Option<T> none() {
    return new None<T>();
  }

  /**
   * Turns an unsafe nullable value into a safe optional value. If <code>t == null</code> then
   * return none, otherwise, return the given value in some.
   *
   * @param t The unsafe nullable value.
   * @return If <code>t == null</code> then return it in some, otherwise, return none.
   */
  public static <T> Option<T> fromNull(final T t) {
    return t == null ? Option.<T>none() : some(t);
  }

  /**
   * Turns an unsafe nullable value into a safe optional value. If <code>t == null</code> then
   * return none, otherwise, return the given value in some.
   *
   * @return If <code>t == null</code> then return it in some, otherwise, return none.
   */
  public static <T> F<T, Option<T>> fromNull() {
    return new F<T, Option<T>>() {
      public Option<T> f(final T t) {
        return fromNull(t);
      }
    };
  }

  /**
   * Joins the given optional value of optional value using a bind operation.
   *
   * @param o The optional value of optional value to join.
   * @return A new optional value that is the join of the given optional value.
   */
  public static <A> Option<A> join(final Option<Option<A>> o) {
    final F<Option<A>, Option<A>> id = identity();
    return o.bind(id);
  }

  /**
   * Sequence through the option monad.
   *
   * @param a The list of option to sequence.
   * @return The option of list after sequencing.
   */
  public static <A> Option<List<A>> sequence(final List<Option<A>> a) {
    return a.isEmpty() ?
           some(List.<A>nil()) :
           a.head().bind(new F<A, Option<List<A>>>() {
             public Option<List<A>> f(final A aa) {
               return sequence(a.tail()).map(cons_(aa));
             }
           });
  }

  /**
   * Returns an optional value that has a value of the given argument, if the given predicate holds
   * on that argument, otherwise, returns no value.
   *
   * @param f The predicate to test on the given argument.
   * @param a The argument to test the predicate on and potentially use as the value of the returned
   *          optional value.
   * @return an optional value that has a value of the given argument, if the given predicate holds
   *         on that argument, otherwise, returns no value.
   */
  public static <A> Option<A> iif(final F<A, Boolean> f, final A a) {
    return f.f(a) ? some(a) : Option.<A>none();
  }

  /**
   * Returns an optional value that has a value of the given argument if the given boolean is true, otherwise, returns
   * no value.
   *
   * @param p The value to be true to return the given value.
   * @param a the value to return in an optional value if the given boolean is true.
   * @return An optional value that has a value of the given argument if the given boolean is true, otherwise, returns
   *         no value.
   */
  public static <A> Option<A> iif(final boolean p, final P1<A> a) {
    return p ? some(a._1()) : Option.<A>none();
  }

  /**
   * Returns an optional value that has a value of the given argument if the given boolean is true, otherwise, returns
   * no value.
   *
   * @param p The value to be true to return the given value.
   * @param a the value to return in an optional value if the given boolean is true.
   * @return An optional value that has a value of the given argument if the given boolean is true, otherwise, returns
   *         no value.
   */
  public static <A> Option<A> iif(final boolean p, final A a) {
    return iif(p, p(a));
  }

  /**
   * First-class version of the iif function.
   *
   * @return a function that returns an optional value that has a value of the given argument, if the given predicate
   *         holds on that argument, or no value otherwise.
   */
  public static <A> F2<F<A, Boolean>, A, Option<A>> iif() {
    return new F2<F<A, Boolean>, A, Option<A>>() {
      public Option<A> f(final F<A, Boolean> p, final A a) {
        return iif(p, a);
      }
    };
  }

  /**
   * Returns all the values in the given list.
   *
   * @param as The list of potential values to get actual values from.
   * @return All the values in the given list.
   */
  public static <A> List<A> somes(final List<Option<A>> as) {
    return as.filter(Option.<A>isSome_()).map(new F<Option<A>, A>() {
      public A f(final Option<A> o) {
        return o.some();
      }
    });
  }


  /**
   * Returns all the values in the given stream.
   *
   * @param as The stream of potential values to get actual values from.
   * @return All the values in the given stream.
   */
  public static <A> Stream<A> somes(final Stream<Option<A>> as) {
    return as.filter(Option.<A>isSome_()).map(new F<Option<A>, A>() {
      public A f(final Option<A> o) {
        return o.some();
      }
    });
  }

  /**
   * Returns an optional non-empty string, or no value if the given string is empty.
   *
   * @param s A string to turn into an optional non-empty string.
   * @return an optional non-empty string, or no value if the given string is empty.
   */
  public static Option<String> fromString(final String s) {
    return fromNull(s).bind(new F<String, Option<String>>() {
      public Option<String> f(final String s) {
        final Option<String> none = none();
        return s.length() == 0 ? none : some(s);
      }
    });
  }

  /**
   * Returns a function that transforms a string to an optional non-empty string,
   * or no value if the string is empty.
   *
   * @return a function that transforms a string to an optional non-empty string,
   *         or no value if the string is empty.
   */
  public static F<String, Option<String>> fromString() {
    return new F<String, Option<String>>() {
      public Option<String> f(final String s) {
        return fromString(s);
      }
    };
  }

  /**
   * Returns a function that takes an optional value to a value or errors if there is no value.
   *
   * @return A function that takes an optional value to a value or errors if there is no value.
   */
  public static <A> F<Option<A>, A> fromSome() {
    return new F<Option<A>, A>() {
      public A f(final Option<A> option) {
        return option.some();
      }
    };
  }

  /**
   * Promotes a function of arity-2 so that it operates over options.
   *
   * @param f A function to promote.
   * @return The given function promoted to operate on options.
   */
  public static <A, B, C> F<Option<A>, F<Option<B>, Option<C>>> liftM2(final F<A, F<B, C>> f) {
    return curry(new F2<Option<A>, Option<B>, Option<C>>() {
      public Option<C> f(final Option<A> a, final Option<B> b) {
        return a.bind(b, f);
      }
    });
  }

  /**
   * First-class bind function.
   *
   * @return A function that binds a given function across an option with a final join.
   */
  public static <A, B> F<F<A, Option<B>>, F<Option<A>, Option<B>>> bind() {
    return curry(new F2<F<A, Option<B>>, Option<A>, Option<B>>() {
      public Option<B> f(final F<A, Option<B>> f, final Option<A> a) {
        return a.bind(f);
      }
    });
  }

  /**
   * First-class join function.
   *
   * @return A function that joins an Option of an Option to make a single Option.
   */
  public static <A> F<Option<Option<A>>, Option<A>> join() {
    return new F<Option<Option<A>>, Option<A>>() {
      public Option<A> f(final Option<Option<A>> option) {
        return join(option);
      }
    };
  }

  /**
   * A function that parses a string to a byte.
   */
  public static final F<String, Option<Byte>> parseByte = new F<String, Option<Byte>>() {
      public Option<Byte> f(final String s) {
          return parseByte(s).toOption();
      }
  };

  /**
   * A function that parses a string to a double.
   */
  public static final F<String, Option<Double>> parseDouble = new F<String, Option<Double>>() {
      public Option<Double> f(final String s) {
          return parseDouble(s).toOption();
      }
  };

  /**
   * A function that parses a string to a float.
   */
  public static final F<String, Option<Float>> parseFloat = new F<String, Option<Float>>() {
      public Option<Float> f(final String s) {
          return parseFloat(s).toOption();
      }
  };

  /**
   * A function that parses a string to an integer.
   */
  public static final F<String, Option<Integer>> parseInt = new F<String, Option<Integer>>() {
      public Option<Integer> f(final String s) {
          return parseInt(s).toOption();
      }
  };

  /**
   * A function that parses a string to a long.
   */
  public static final F<String, Option<Long>> parseLong = new F<String, Option<Long>>() {
      public Option<Long> f(final String s) {
          return parseLong(s).toOption();
      }
  };

  /**
   * A function that parses a string to a short.
   */
  public static final F<String, Option<Short>> parseShort = new F<String, Option<Short>>() {
      public Option<Short> f(final String s) {
          return parseShort(s).toOption();
      }
  };
}
