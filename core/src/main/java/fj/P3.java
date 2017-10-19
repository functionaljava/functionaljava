package fj;

import static fj.P.weakMemo;

/**
 * A product-3.
 *
 * @version %build.number%
 */
public abstract class P3<A, B, C> {
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

  /**
   * Access the third element of the product.
   *
   * @return The third element of the product.
   */
  public abstract C _3();

  /**
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P3<X, B, C> map1(final F<A, X> f) {
    return new P3<X, B, C>() {
      public X _1() {
        return f.f(P3.this._1());
      }

      public B _2() {
        return P3.this._2();
      }

      public C _3() {
        return P3.this._3();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P3<A, X, C> map2(final F<B, X> f) {
    return new P3<A, X, C>() {
      public A _1() {
        return P3.this._1();
      }

      public X _2() {
        return f.f(P3.this._2());
      }

      public C _3() {
        return P3.this._3();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P3<A, B, X> map3(final F<C, X> f) {
    return new P3<A, B, X>() {
      public A _1() {
        return P3.this._1();
      }

      public B _2() {
        return P3.this._2();
      }

      public X _3() {
        return f.f(P3.this._3());
      }
    };
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return F1Functions.lazy(P3.<A, B, C>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P3.<A, B, C>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P3.<A, B, C>__3()).f(this);
  }


  /**
   * Creates a {@link P4} by adding the given element to the current {@link P3}
   *
   * @param el the element to append
   * @return A {@link P4} containing the original {@link P3} with the extra element added at the end
   */
  public final <D> P4<A, B, C, D> append(D el) {
    return P.p(_1(), _2(), _3(), el);
  }

  /**
   * Creates a {@link P5} by adding the given element to the current {@link P3}
   *
   * @param el the element to append
   * @return A {@link P5} containing the original {@link P3} with the extra element added at the end
   */
  public final <D, E> P5<A, B, C, D, E> append(P2<D, E> el) {
    return P.p(_1(), _2(), _3(), el._1(), el._2());
  }

  /**
   * Creates a {@link P6} by adding the given element to the current {@link P3}
   *
   * @param el the element to append
   * @return A {@link P6} containing the original {@link P3} with the extra element added at the end
   */
  public final <D, E, F> P6<A, B, C, D, E, F> append(P3<D, E, F> el) {
    return P.p(_1(), _2(), _3(), el._1(), el._2(), el._3());
  }

  /**
   * Creates a {@link P7} by adding the given element to the current {@link P3}
   *
   * @param el the element to append
   * @return A {@link P7} containing the original {@link P3} with the extra element added at the end
   */
  public final <D, E, F, G> P7<A, B, C, D, E, F, G> append(P4<D, E, F, G> el) {
    return P.p(_1(), _2(), _3(), el._1(), el._2(), el._3(), el._4());
  }

  /**
   * Creates a {@link P8} by adding the given element to the current {@link P3}
   *
   * @param el the element to append
   * @return A {@link P8} containing the original {@link P3} with the extra element added at the end
   */
  public final <D, E, F, G, H> P8<A, B, C, D, E, F, G, H> append(P5<D, E, F, G, H> el) {
    return P.p(_1(), _2(), _3(), el._1(), el._2(), el._3(), el._4(), el._5());
  }


  /**
   * Provides a memoising P3 that remembers its values.
   *
   * @return A P3 that calls this P3 once for any given element and remembers the value for subsequent calls.
   */
  public final P3<A, B, C> memo() {
      P3<A, B, C> self = this;
    return new P3<A, B, C>() {
      private final P1<A> a = weakMemo(self::_1);
      private final P1<B> b = weakMemo(self::_2);
      private final P1<C> c = weakMemo(self::_3);

      public A _1() {
        return a._1();
      }

      public B _2() {
        return b._1();
      }

      public C _3() {
        return c._1();
      }
    };
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C> F<P3<A, B, C>, A> __1() {
    return P3::_1;
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C> F<P3<A, B, C>, B> __2() {
    return P3::_2;
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C> F<P3<A, B, C>, C> __3() {
    return P3::_3;
  }

    @Override
	public final String toString() {
		return Show.p3Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow()).showS(this);
	}

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(P3.class, this, other, 
        () -> Equal.p3Equal(Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.p3Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash()).hash(this);
  }

}
