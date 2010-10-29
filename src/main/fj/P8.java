package fj;

/**
 * A product-8.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
@SuppressWarnings({"UnnecessaryFullyQualifiedName"})
public abstract class P8<A, B, C, D, E, F, G, H> {
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
   * Access the sixth element of the product.
   *
   * @return The sixth element of the product.
   */
  public abstract F _6();

  /**
   * Access the seventh element of the product.
   *
   * @return The seventh element of the product.
   */
  public abstract G _7();

  /**
   * Access the eighth element of the product.
   *
   * @return The eighth element of the product.
   */
  public abstract H _8();

  /**
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<X, B, C, D, E, F, G, H> map1(final fj.F<A, X> f) {
    return new P8<X, B, C, D, E, F, G, H>() {
      public X _1() {
        return f.f(P8.this._1());
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, X, C, D, E, F, G, H> map2(final fj.F<B, X> f) {
    return new P8<A, X, C, D, E, F, G, H>() {
      public A _1() {
        return P8.this._1();
      }

      public X _2() {
        return f.f(P8.this._2());
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, X, D, E, F, G, H> map3(final fj.F<C, X> f) {
    return new P8<A, B, X, D, E, F, G, H>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public X _3() {
        return f.f(P8.this._3());
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the fourth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, C, X, E, F, G, H> map4(final fj.F<D, X> f) {
    return new P8<A, B, C, X, E, F, G, H>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public X _4() {
        return f.f(P8.this._4());
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the fifth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, C, D, X, F, G, H> map5(final fj.F<E, X> f) {
    return new P8<A, B, C, D, X, F, G, H>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public X _5() {
        return f.f(P8.this._5());
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the sixth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, C, D, E, X, G, H> map6(final fj.F<F, X> f) {
    return new P8<A, B, C, D, E, X, G, H>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public X _6() {
        return f.f(P8.this._6());
      }

      public G _7() {
        return P8.this._7();
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the seventh element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, C, D, E, F, X, H> map7(final fj.F<G, X> f) {
    return new P8<A, B, C, D, E, F, X, H>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public X _7() {
        return f.f(P8.this._7());
      }

      public H _8() {
        return P8.this._8();
      }
    };
  }

  /**
   * Map the eighth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P8<A, B, C, D, E, F, G, X> map8(final fj.F<H, X> f) {
    return new P8<A, B, C, D, E, F, G, X>() {
      public A _1() {
        return P8.this._1();
      }

      public B _2() {
        return P8.this._2();
      }

      public C _3() {
        return P8.this._3();
      }

      public D _4() {
        return P8.this._4();
      }

      public E _5() {
        return P8.this._5();
      }

      public F _6() {
        return P8.this._6();
      }

      public G _7() {
        return P8.this._7();
      }

      public X _8() {
        return f.f(P8.this._8());
      }
    };
  }


  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return P8.<A, B, C, D, E, F, G, H>__1().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return P8.<A, B, C, D, E, F, G, H>__2().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return P8.<A, B, C, D, E, F, G, H>__3().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return P8.<A, B, C, D, E, F, G, H>__4().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return P8.<A, B, C, D, E, F, G, H>__5().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the sixth element.
   *
   * @return the 1-product projection over the sixth element.
   */
  public final P1<F> _6_() {
    return P8.<A, B, C, D, E, F, G, H>__6().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the seventh element.
   *
   * @return the 1-product projection over the seventh element.
   */
  public final P1<G> _7_() {
    return P8.<A, B, C, D, E, F, G, H>__7().lazy().f(this);
  }

  /**
   * Returns the 1-product projection over the eighth element.
   *
   * @return the 1-product projection over the eighth element.
   */
  public final P1<H> _8_() {
    return P8.<A, B, C, D, E, F, G, H>__8().lazy().f(this);
  }

  /**
   * Provides a memoising P8 that remembers its values.
   *
   * @return A P8 that calls this P8 once for any given element and remembers the value for subsequent calls.
   */
  public final P8<A, B, C, D, E, F, G, H> memo() {
    return new P8<A, B, C, D, E, F, G, H>() {
      private final P1<A> a = _1_().memo();
      private final P1<B> b = _2_().memo();
      private final P1<C> c = _3_().memo();
      private final P1<D> d = _4_().memo();
      private final P1<E> e = _5_().memo();
      private final P1<F> f = _6_().memo();
      private final P1<G> g = _7_().memo();
      private final P1<H> h = _8_().memo();

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

      public F _6() {
        return f._1();
      }

      public G _7() {
        return g._1();
      }

      public H _8() {
        return h._1();
      }
    };
  }


  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, A> __1() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, A>() {
      public A f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, B> __2() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, B>() {
      public B f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._2();
      }
    };
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, C> __3() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, C>() {
      public C f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._3();
      }
    };
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, D> __4() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, D>() {
      public D f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._4();
      }
    };
  }

  /**
   * Returns a function that returns the fifth element of a product.
   *
   * @return A function that returns the fifth element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, E> __5() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, E>() {
      public E f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._5();
      }
    };
  }

  /**
   * Returns a function that returns the sixth element of a product.
   *
   * @return A function that returns the sixth element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, F$> __6() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, F$>() {
      public F$ f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._6();
      }
    };
  }

  /**
   * Returns a function that returns the seventh element of a product.
   *
   * @return A function that returns the seventh element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, G> __7() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, G>() {
      public G f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._7();
      }
    };
  }

  /**
   * Returns a function that returns the eighth element of a product.
   *
   * @return A function that returns the eighth element of a product.
   */
  public static <A, B, C, D, E, F$, G, H> fj.F<P8<A, B, C, D, E, F$, G, H>, H> __8() {
    return new fj.F<P8<A, B, C, D, E, F$, G, H>, H>() {
      public H f(final P8<A, B, C, D, E, F$, G, H> p) {
        return p._8();
      }
    };
  }
}
