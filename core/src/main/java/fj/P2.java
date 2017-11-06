package fj;

import static fj.Function.*;
import static fj.P.weakMemo;
import static fj.data.optic.PLens.pLens;
import fj.data.*;
import fj.data.optic.Lens;
import fj.data.optic.PLens;

/**
 * A product-2.
 *
 * @version %build.number%
 */
public abstract class P2<A, B> {
  /**
   * Access the first element of the product.
   *
   * @return The first element of the product.
   */
  public abstract A _1();

  /**
   * Access the second element of the product.
   *
   * @return The second element of the product.
   */
  public abstract B _2();

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(P2.class, this, other, () -> Equal.p2Equal(Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.p2Hash(Hash.<A>anyHash(), Hash.<B>anyHash()).hash(this);
  }

  /**
   * Swaps the elements around in this product.
   *
   * @return A new product-2 with the elements swapped.
   */
  public final P2<B, A> swap() {
      return P.lazy(P2.this::_2, P2.this::_1);
  }

  /**
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P2<X, B> map1(final F<A, X> f) {
      P2<A, B> self = this;
      return P.lazy(() -> f.f(self._1()), self::_2);
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P2<A, X> map2(final F<B, X> f) {
      return P.lazy(P2.this::_1, () -> f.f(P2.this._2()));
  }


  /**
   * Split this product between two argument functions and combine their output.
   *
   * @param f A function that will map the first element of this product.
   * @param g A function that will map the second element of this product.
   * @return A new product with the first function applied to the second element
   *         and the second function applied to the second element.
   */
  public final <C, D> P2<C, D> split(final F<A, C> f, final F<B, D> g) {
    final F<P2<A, D>, P2<C, D>> ff = map1_(f);
    final F<P2<A, B>, P2<A, D>> gg = map2_(g);
    return compose(ff, gg).f(this);
  }

  /**
   * Duplicates this product on the first element, and maps the given function across the duplicate (Comonad pattern).
   *
   * @param k A function to map over the duplicated product.
   * @return A new product with the result of the given function applied to this product as the first element,
   *         and with the second element intact.
   */
  public final <C> P2<C, B> cobind(final F<P2<A, B>, C> k) {
      P2<A, B> self = this;
      return P.lazy(() -> k.f(self), self::_2);
  }

  /**
   * Duplicates this product into the first element (Comonad pattern).
   *
   * @return A new product with this product in its first element and with the second element intact.
   */
  public final P2<P2<A, B>, B> duplicate() {
    final F<P2<A, B>, P2<A, B>> id = identity();
    return cobind(id);
  }

  /**
   * Replaces the first element of this product with the given value.
   *
   * @param c The value with which to replace the first element of this product.
   * @return A new product with the first element replaced with the given value.
   */
  public final <C> P2<C, B> inject(final C c) {
    final F<P2<A, B>, C> co = constant(c);
    return cobind(co);
  }

  /**
   * Applies a list of comonadic functions to this product, returning a list of values.
   *
   * @param fs A list of functions to apply to this product.
   * @return A list of the results of applying the given list of functions to this product.
   */
  public final <C> List<C> sequenceW(final List<F<P2<A, B>, C>> fs) {
    List.Buffer<C> cs = List.Buffer.empty();
    for (final F<P2<A, B>, C> f : fs)
      cs = cs.snoc(f.f(this));
    return cs.toList();
  }

  public final <C> List<P2<A, C>> traverseList(final F<B, List<C>> f) {
        return f.f(_2()).map(x -> P.p(_1(), x));
  }

  public final <C> Stream<P2<A, C>> traverseStream(final F<B, Stream<C>> f) {
        return f.f(_2()).map(x -> P.p(_1(), x));
  }

  public final <C> IO<P2<A, C>> traverseIO(final F<B, IO<C>> f) {
        return IOFunctions.map(f.f(_2()), x -> P.p(_1(), x));
  }

  public final <C> Option<P2<A, C>> traverseOption(final F<B, Option<C>> f) {
        return f.f(_2()).map(x -> P.p(_1(), x));
  }

  public final <C, X> Either<X, P2<A, C>> traverseEither(final F<B, Either<X, C>> f) {
        return f.f(_2()).right().map(x -> P.p(_1(), x));
  }

  /**
   * Applies a stream of comonadic functions to this product, returning a stream of values.
   *
   * @param fs A stream of functions to apply to this product.
   * @return A stream of the results of applying the given stream of functions to this product.
   */
  public final <C> Stream<C> sequenceW(final Stream<F<P2<A, B>, C>> fs) {
    return fs.isEmpty()
           ? Stream.nil()
           : Stream.cons(fs.head().f(this), () -> sequenceW(fs.tail()._1()));
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return F1Functions.lazy(P2.<A, B>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P2.<A, B>__2()).f(this);
  }

  /**
   * Creates a {@link P3} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P3} containing the original {@link P2} with the extra element added at the end
   */
  public final <C> P3<A, B, C> append(C el) {
    return P.p(_1(), _2(), el);
  }

  /**
   * Creates a {@link P4} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P4} containing the original {@link P2} with the extra element added at the end
   */
  public final <C, D> P4<A, B, C, D> append(P2<C, D> el) {
    return P.p(_1(), _2(), el._1(), el._2());
  }

  /**
   * Creates a {@link P5} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P5} containing the original {@link P2} with the extra element added at the end
   */
  public final <C, D, E> P5<A, B, C, D, E> append(P3<C, D, E> el) {
    return P.p(_1(), _2(), el._1(), el._2(), el._3());
  }

  /**
   * Creates a {@link P6} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P6} containing the original {@link P2} with the extra element added at the end
   */
  public final <C, D, E, F> P6<A, B, C, D, E, F> append(P4<C, D, E, F> el) {
    return P.p(_1(), _2(), el._1(), el._2(), el._3(), el._4());
  }

  /**
   * Creates a {@link P7} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P7} containing the original {@link P2} with the extra element added at the end
   */
  public final <C, D, E, F, G> P7<A, B, C, D, E, F, G> append(P5<C, D, E, F, G> el) {
    return P.p(_1(), _2(), el._1(), el._2(), el._3(), el._4(), el._5());
  }

  /**
   * Creates a {@link P8} by adding the given element to the current {@link P2}
   *
   * @param el the element to append
   * @return A {@link P8} containing the original {@link P2} with the extra element added at the end
   */
  public final <C, D, E, F, G, H> P8<A, B, C, D, E, F, G, H> append(P6<C, D, E, F, G, H> el) {
    return P.p(_1(), _2(), el._1(), el._2(), el._3(), el._4(), el._5(), el._6());
  }


  /**
     * Provides a memoising P2 that remembers its values.
     *
     * @return A P2 that calls this P2 once for any given element and remembers the value for subsequent calls.
     */
    public final P2<A, B> memo() {
        P2<A, B> self = this;
        return new P2<A, B>() {
            private final P1<A> a = weakMemo(self::_1);
            private final P1<B> b = weakMemo(self::_2);

            public A _1() {
                return a._1();
            }

            public B _2() {
                return b._1();
            }
        };
    }

  /**
   * A first-class version of the split function.
   *
   * @param f A function that will map the first element of the given product.
   * @param g A function that will map the second element of the given product.
   * @return A function that splits a given product between the two given functions and combines their output.
   */
  public static <A, B, C, D> F<P2<A, B>, P2<C, D>> split_(final F<A, C> f, final F<B, D> g) {
    return p -> p.split(f, g);
  }

  /**
   * Promotes a function so that it maps the first element of a product.
   *
   * @param f The function to promote.
   * @return The given function, promoted to map the first element of products.
   */
  public static <A, B, X> F<P2<A, B>, P2<X, B>> map1_(final F<A, X> f) {
    return p -> p.map1(f);
  }

  /**
   * Promotes a function so that it maps the second element of a product.
   *
   * @param f The function to promote.
   * @return The given function, promoted to map the second element of products.
   */
  public static <A, B, X> F<P2<A, B>, P2<A, X>> map2_(final F<B, X> f) {
    return p -> p.map2(f);
  }

  /**
   * Sends the given input value to both argument functions and combines their output.
   *
   * @param f A function to receive an input value.
   * @param g A function to receive an input value.
   * @param b An input value to send to both functions.
   * @return The product of the two functions applied to the input value.
   */
  public static <B, C, D> P2<C, D> fanout(final F<B, C> f, final F<B, D> g, final B b) {
    return join(P.<B, B>p2()).f(b).split(f, g);
  }

  /**
   * Maps the given function across both the elements of the given product.
   *
   * @param f A function to map over a product.
   * @param p A product over which to map.
   * @return A new product with the given function applied to both elements.
   */
  public static <A, B> P2<B, B> map(final F<A, B> f, final P2<A, A> p) {
    return p.split(f, f);
  }

  /**
   * Returns a curried form of {@link #swap()}.
   *
   * @return A curried form of {@link #swap()}.
   */
  public static <A, B> F<P2<A, B>, P2<B, A>> swap_() {
    return P2::swap;
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B> F<P2<A, B>, A> __1() {
    return P2::_1;
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B> F<P2<A, B>, B> __2() {
    return P2::_2;
  }

  /**
   * Transforms a curried function of arity-2 to a function of a product-2
   *
   * @param f a curried function of arity-2 to transform into a function of a product-2
   * @return The function, transformed to operate on on a product-2
   */
  public static <A, B, C> F<P2<A, B>, C> tuple(final F<A, F<B, C>> f) {
    return p -> f.f(p._1()).f(p._2());
  }

  /**
   * Transforms an uncurried function of arity-2 to a function of a product-2
   *
   * @param f an uncurried function of arity-2 to transform into a function of a product-2
   * @return The function, transformed to operate on on a product-2
   */
  public static <A, B, C> F<P2<A, B>, C> tuple(final F2<A, B, C> f) {
    return tuple(curry(f));
  }

  /**
   * Transforms a function of a product-2 to an uncurried function or arity-2.
   *
   * @param f A function of a product-2 to transform into an uncurried function.
   * @return The function, transformed to an uncurried function of arity-2.
   */
  public static <A, B, C> F2<A, B, C> untuple(final F<P2<A, B>, C> f) {
    return (a, b) -> f.f(P.p(a, b));
  }


    @Override
	public final String toString() {
		return Show.p2Show(Show.<A>anyShow(), Show.<B>anyShow()).showS(this);
	}

  /**
   * Optic factory methods for a P2

   */
  public static final class Optic {

    private Optic() {
      throw new UnsupportedOperationException();
    }

    /**
     * Polyomorphic lens targeted on _1.
     */
    public static <A, B, C> PLens<P2<A, B>, P2<C, B>, A, C> _1p() {
      return pLens(__1(), a -> p2 -> P.p(a, p2._2()));
    }

    /**
     * Monomorphic lens targeted on _1.
     */
    public static <A, B> Lens<P2<A, B>, A> _1() {
      return new Lens<>(_1p());
    }

    /**
     * Polyomorphic lens targeted on _2.
     */
    public static <A, B, C> PLens<P2<A, B>, P2<A, C>, B, C> _2p() {
      return pLens(__2(), b -> p2 -> P.p(p2._1(), b));
    }

    /**
     * Monomorphic lens targeted on _1.
     */
    public static <A, B> Lens<P2<A, B>, B> _2() {
      return new Lens<>(_2p());
    }

  }

}
