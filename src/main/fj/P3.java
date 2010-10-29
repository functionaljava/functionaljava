package fj;

/**
 * A product-3.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
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
    return P3.<A, B, C>__1().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return P3.<A, B, C>__2().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return P3.<A, B, C>__3().lazy().f(this);
  }

  /**
   * Provides a memoising P3 that remembers its values.
   *
   * @return A P3 that calls this P3 once for any given element and remembers the value for subsequent calls.
   */
  public final P3<A, B, C> memo() {
    return new P3<A, B, C>() {
      private final P1<A> a = _1_().memo();
      private final P1<B> b = _2_().memo();
      private final P1<C> c = _3_().memo();

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
    return new F<P3<A, B, C>, A>() {
      public A f(final P3<A, B, C> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C> F<P3<A, B, C>, B> __2() {
    return new F<P3<A, B, C>, B>() {
      public B f(final P3<A, B, C> p) {
        return p._2();
      }
    };
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C> F<P3<A, B, C>, C> __3() {
    return new F<P3<A, B, C>, C>() {
      public C f(final P3<A, B, C> p) {
        return p._3();
      }
    };
  }
}
