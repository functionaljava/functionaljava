package fj;

/**
 * A product-5.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
public abstract class P5<A, B, C, D, E> {
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
   * Access the fourth element of the product.
   *
   * @return The fourth element of the product.
   */
  public abstract D _4();

  /**
   * Access the fifth element of the product.
   *
   * @return The fifth element of the product.
   */
  public abstract E _5();

  /**
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P5<X, B, C, D, E> map1(final F<A, X> f) {
    return new P5<X, B, C, D, E>() {
      public X _1() {
        return f.f(P5.this._1());
      }

      public B _2() {
        return P5.this._2();
      }

      public C _3() {
        return P5.this._3();
      }

      public D _4() {
        return P5.this._4();
      }

      public E _5() {
        return P5.this._5();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P5<A, X, C, D, E> map2(final F<B, X> f) {
    return new P5<A, X, C, D, E>() {
      public A _1() {
        return P5.this._1();
      }

      public X _2() {
        return f.f(P5.this._2());
      }

      public C _3() {
        return P5.this._3();
      }

      public D _4() {
        return P5.this._4();
      }

      public E _5() {
        return P5.this._5();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P5<A, B, X, D, E> map3(final F<C, X> f) {
    return new P5<A, B, X, D, E>() {
      public A _1() {
        return P5.this._1();
      }

      public B _2() {
        return P5.this._2();
      }

      public X _3() {
        return f.f(P5.this._3());
      }

      public D _4() {
        return P5.this._4();
      }

      public E _5() {
        return P5.this._5();
      }
    };
  }

  /**
   * Map the fourth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P5<A, B, C, X, E> map4(final F<D, X> f) {
    return new P5<A, B, C, X, E>() {
      public A _1() {
        return P5.this._1();
      }

      public B _2() {
        return P5.this._2();
      }

      public C _3() {
        return P5.this._3();
      }

      public X _4() {
        return f.f(P5.this._4());
      }

      public E _5() {
        return P5.this._5();
      }
    };
  }

  /**
   * Map the fifth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P5<A, B, C, D, X> map5(final F<E, X> f) {
    return new P5<A, B, C, D, X>() {
      public A _1() {
        return P5.this._1();
      }

      public B _2() {
        return P5.this._2();
      }

      public C _3() {
        return P5.this._3();
      }

      public D _4() {
        return P5.this._4();
      }

      public X _5() {
        return f.f(P5.this._5());
      }
    };
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return P5.<A, B, C, D, E>__1().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return P5.<A, B, C, D, E>__2().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return P5.<A, B, C, D, E>__3().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return P5.<A, B, C, D, E>__4().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return P5.<A, B, C, D, E>__5().lazy().f(this);
  }

  /**
   * Provides a memoising P5 that remembers its values.
   *
   * @return A P5 that calls this P5 once for any given element and remembers the value for subsequent calls.
   */
  public final P5<A, B, C, D, E> memo() {
    return new P5<A, B, C, D, E>() {
      private final P1<A> a = _1_().memo();
      private final P1<B> b = _2_().memo();
      private final P1<C> c = _3_().memo();
      private final P1<D> d = _4_().memo();
      private final P1<E> e = _5_().memo();

      public A _1() {
        return a._1();
      }

      public B _2() {
        return b._1();
      }

      public C _3() {
        return c._1();
      }

      public D _4() {
        return d._1();
      }

      public E _5() {
        return e._1();
      }
    };
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C, D, E> F<P5<A, B, C, D, E>, A> __1() {
    return new F<P5<A, B, C, D, E>, A>() {
      public A f(final P5<A, B, C, D, E> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D, E> F<P5<A, B, C, D, E>, B> __2() {
    return new F<P5<A, B, C, D, E>, B>() {
      public B f(final P5<A, B, C, D, E> p) {
        return p._2();
      }
    };
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D, E> F<P5<A, B, C, D, E>, C> __3() {
    return new F<P5<A, B, C, D, E>, C>() {
      public C f(final P5<A, B, C, D, E> p) {
        return p._3();
      }
    };
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D, E> F<P5<A, B, C, D, E>, D> __4() {
    return new F<P5<A, B, C, D, E>, D>() {
      public D f(final P5<A, B, C, D, E> p) {
        return p._4();
      }
    };
  }

  /**
   * Returns a function that returns the fifth element of a product.
   *
   * @return A function that returns the fifth element of a product.
   */
  public static <A, B, C, D, E> F<P5<A, B, C, D, E>, E> __5() {
    return new F<P5<A, B, C, D, E>, E>() {
      public E f(final P5<A, B, C, D, E> p) {
        return p._5();
      }
    };
  }
}
