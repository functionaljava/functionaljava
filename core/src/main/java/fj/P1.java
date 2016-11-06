package fj;

import fj.data.Array;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import static fj.P.p;
import static fj.Unit.unit;
//import fj.data.*;


public abstract class P1<A> implements F0<A> {

    @Override
    public final A f() {
        return _1();
    }

    /**
     * Access the first element of the product.
     *
     * @return The first element of the product.
     */
    public abstract A _1();

    /**
     * Returns a function that returns the first element of a product.
     *
     * @return A function that returns the first element of a product.
     */
    public static <A> F<P1<A>, A> __1() {
        return P1::_1;
    }

    /**
     * Promote any function to a transformation between P1s.
     *
	 * @deprecated As of release 4.5, use {@link #map_}
     * @param f A function to promote to a transformation between P1s.
     * @return A function promoted to operate on P1s.
     */
    public static <A, B> F<P1<A>, P1<B>> fmap(final F<A, B> f) {
        return map_(f);
    }

	/**
	 * Promote any function to a transformation between P1s.
	 *
	 * @param f A function to promote to a transformation between P1s.
	 * @return A function promoted to operate on P1s.
	 */
	public static <A, B> F<P1<A>, P1<B>> map_(final F<A, B> f) {
		return a -> a.map(f);
	}

	/**
     * Binds the given function to the value in a product-1 with a final join.
     *
     * @param f A function to apply to the value in a product-1.
     * @return The result of applying the given function to the value of given product-1.
     */
    public final <B> P1<B> bind(final F<A, P1<B>> f) {
        P1<A> self = this;
        return P.lazy(() -> f.f(self._1())._1());
    }

    /**
     * Promotes the given function so that it returns its value in a P1.
     *
     * @param f A function to have its result wrapped in a P1.
     * @return A function whose result is wrapped in a P1.
     */
    public static <A, B> F<A, P1<B>> curry(final F<A, B> f) {
        return a -> P.lazy(() -> f.f(a));
    }

    /**
     * Performs function application within a P1 (applicative functor pattern).
     *
     * @param cf The P1 function to apply.
     * @return A new P1 after applying the given P1 function to the first argument.
     */
    public final <B> P1<B> apply(final P1<F<A, B>> cf) {
        P1<A> self = this;
        return cf.bind(f -> map_(f).f(self));
    }

    /**
     * Binds the given function to the values in the given P1s with a final join.
     *
     * @param cb A given P1 to bind the given function with.
     * @param f  The function to apply to the values in the given P1s.
     * @return A new P1 after performing the map, then final join.
     */
    public final <B, C> P1<C> bind(final P1<B> cb, final F<A, F<B, C>> f) {
        return cb.apply(map_(f).f(this));
    }

	/**
	 * Binds the given function to the values in the given P1s with a final join.
	 */
	public final <B, C> P1<C> bind(final P1<B> cb, final F2<A, B, C> f) {
		return bind(cb, F2W.lift(f).curry());
	}

    /**
     * Joins a P1 of a P1 with a bind operation.
     *
     * @param a The P1 of a P1 to join.
     * @return A new P1 that is the join of the given P1.
     */
    public static <A> P1<A> join(final P1<P1<A>> a) {
        return a.bind(Function.identity());
    }

    /**
     * Promotes a function of arity-2 to a function on P1s.
     *
     * @param f The function to promote.
     * @return A function of arity-2 promoted to map over P1s.
     */
    public static <A, B, C> F<P1<A>, F<P1<B>, P1<C>>> liftM2(final F<A, F<B, C>> f) {
        return Function.curry((pa, pb) -> pa.bind(pb, f));
    }

	public final <B, C> P1<C> liftM2(P1<B> pb, F2<A, B, C> f) {
		return P.lazy(() -> f.f(_1(), pb._1()));
	}

    /**
     * Turns a List of P1s into a single P1 of a List.
     *
     * @param as The list of P1s to transform.
     * @return A single P1 for the given List.
     */
    public static <A> P1<List<A>> sequence(final List<P1<A>> as) {
        return as.foldRight(liftM2(List.cons()), p(List.nil()));
    }

    /**
     * A first-class version of the sequence method for lists of P1s.
     *
     * @return A function from a List of P1s to a single P1 of a List.
     */
    public static <A> F<List<P1<A>>, P1<List<A>>> sequenceList() {
        return P1::sequence;
    }

    /**
     * Turns a stream of P1s into a single P1 of a stream.
     *
     * @param as The stream of P1s to transform.
     * @return A single P1 for the given stream.
     */
    public static <A> P1<Stream<A>> sequence(final Stream<P1<A>> as) {
        return as.foldRight(liftM2(Stream.cons()), p(Stream.nil()));
    }

	/**
	 * Turns an optional P1 into a lazy option.
	 */
	public static <A> P1<Option<A>> sequence(final Option<P1<A>> o) {
		return P.lazy(() -> o.map(P1::_1));
	}

    /**
     * Turns an array of P1s into a single P1 of an array.
     *
     * @param as The array of P1s to transform.
     * @return A single P1 for the given array.
     */
    public static <A> P1<Array<A>> sequence(final Array<P1<A>> as) {
        return P.lazy(() -> as.map(P1.__1()));
    }

    /**
     * Traversable instance of P1 for List
     *
     * @param f The function that takes A and produces a List<B> (non-deterministic result)
     * @return A List of P1<B>
     */
    public final <B> List<P1<B>> traverseList(final F<A, List<B>> f){
        return f.f(_1()).map(P::p);
    }

    /**
     * Traversable instance of P1 for Either
     *
     * @param f The function produces Either
     * @return An Either of  P1<B>
     */
    public final <B, X> Either<X, P1<B>> traverseEither(final F<A, Either<X, B>> f){
        return f.f(_1()).right().map(P::p);
    }

    /**
     * Traversable instance of P1 for Option
     *
     * @param f The function that produces Option
     * @return An Option of  P1<B>
     */
    public final <B> Option<P1<B>> traverseOption(final F<A, Option<B>> f){
        return f.f(_1()).map(P::p);
    }

    /**
     * Traversable instance of P1 for Validation
     *
     * @param f The function might produces Validation
     * @return An Validation  of P1<B>
     */
    public final <B, E> Validation<E, P1<B>> traverseValidation(final F<A, Validation<E, B>> f){
        return f.f(_1()).map(P::p);
    }

    /**
     * Traversable instance of P1 for Stream
     *
     * @param f The function that produces Stream
     * @return An Stream of  P1<B>
     */
    public final <B> Stream<P1<B>> traverseStream(final F<A, Stream<B>> f){
        return f.f(_1()).map(P::p);
    }

    /**
       * Map the element of the product.
       *
       * @param f The function to map with.
       * @return A product with the given function applied.
       */
      public final <B> P1<B> map(final F<A, B> f) {
          final P1<A> self = this;
        return P.lazy(() -> f.f(self._1()));
      }

    /**
     * @deprecated since 4.7. Use {@link P1#weakMemo()} instead.
     */
    @Deprecated
    public final P1<A> memo() {
        return weakMemo();
    }

    /**
     * Returns a P1 that remembers its value.
     *
     * @return A P1 that calls this P1 once and remembers the value for subsequent calls.
     */
    public P1<A> hardMemo() { return new Memo<>(this); }

    /**
     * Like <code>memo</code>, but the memoized value is wrapped into a <code>WeakReference</code>
     */
    public P1<A> weakMemo() { return new WeakReferenceMemo<>(this); }

    /**
     * Like <code>memo</code>, but the memoized value is wrapped into a <code>SoftReference</code>
     */
    public P1<A> softMemo() { return new SoftReferenceMemo<>(this); }

    /**
     * @deprecated since 4.7. Use {@link P#weakMemo(F0)} instead.
     */
    @Deprecated
    public static <A> P1<A> memo(F<Unit, A> f) {
        return P.weakMemo(() -> f.f(unit()));
    }

  /**
   * @deprecated since 4.7. Use {@link P#weakMemo(F0)} instead.
   */
  @Deprecated
  public static <A> P1<A> memo(F0<A> f) {
		return P.weakMemo(f);
	}

    static final class Memo<A> extends P1<A> {
      private volatile F0<A> fa;
      private A value;

      Memo(F0<A> fa) { this.fa = fa; }

      @Override public final A _1() {
        return (fa == null) ? value : computeValue();
      }

      private synchronized A computeValue() {
        F0<A> fa = this.fa;
        if (fa != null) {
          value = fa.f();
          this.fa = null;
        }
        return value;
      }

      @Override public P1<A> hardMemo() { return this; }
      @Override public P1<A> softMemo() { return this; }
      @Override public P1<A> weakMemo() { return this; }
    }

    abstract static class ReferenceMemo<A> extends P1<A> {
      private final F0<A> fa;
      private volatile Reference<P1<A>> v = null;

      ReferenceMemo(final F0<A> fa) { this.fa = fa; }

      @Override public final A _1() {
        Reference<P1<A>> v = this.v;
        P1<A> p1 = v != null ? v.get() : null;
        return p1 != null ? p1._1() : computeValue();
      }

      private synchronized A computeValue() {
        Reference<P1<A>> v = this.v;
        P1<A> p1 = v != null ? v.get() : null;
        if (p1 == null) {
          A a = fa.f();
          this.v = newReference(p(a));
          return a;
        }
        return p1._1();
      }

      abstract <B> Reference<B> newReference(B ref);
    }

    static final class WeakReferenceMemo<A> extends ReferenceMemo<A> {
      WeakReferenceMemo(F0<A> fa) { super(fa); }
      @Override
      <B> Reference<B> newReference(final B ref) { return new WeakReference<>(ref); }
      @Override public P1<A> weakMemo() { return this; }
    }

    static final class SoftReferenceMemo<A> extends ReferenceMemo<A> {
      SoftReferenceMemo(F0<A> self) { super(self); }
      @Override
      <B> Reference<B> newReference(final B ref) { return new SoftReference<>(ref); }
      @Override public P1<A> softMemo() { return this; }
      @Override public P1<A> weakMemo() { return this; }
    }

    /**
     * Returns a constant function that always uses this value.
     *
     * @return A constant function that always uses this value.
     */
    public final <B> F<B, A> constant() { return Function.constant(_1()); }

    @Override
    public final String toString() {
		return Show.p1Show(Show.<A>anyShow()).showS(this);
	}

    @Override
    public final boolean equals(Object other) {
        return Equal.equals0(P1.class, this, other, () -> Equal.p1Equal(Equal.anyEqual()));
    }

    @Override
    public final int hashCode() {
        return Hash.p1Hash(Hash.<A>anyHash()).hash(this);
    }

}
