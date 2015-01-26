package fj;

/**
 * A product-8.
 *
 * @version %build.number%
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
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__3()).f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__4()).f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__5()).f(this);
  }

  /**
   * Returns the 1-product projection over the sixth element.
   *
   * @return the 1-product projection over the sixth element.
   */
  public final P1<F> _6_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__6()).f(this);
  }

  /**
   * Returns the 1-product projection over the seventh element.
   *
   * @return the 1-product projection over the seventh element.
   */
  public final P1<G> _7_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__7()).f(this);
  }

  /**
   * Returns the 1-product projection over the eighth element.
   *
   * @return the 1-product projection over the eighth element.
   */
  public final P1<H> _8_() {
    return F1Functions.lazy(P8.<A, B, C, D, E, F, G, H>__8()).f(this);
  }

  /**
   * Provides a memoising P8 that remembers its values.
   *
   * @return A P8 that calls this P8 once for any given element and remembers the value for subsequent calls.
   */
  public final P8<A, B, C, D, E, F, G, H> memo() {
      P8<A, B, C, D, E, F, G, H> self = this;
    return new P8<A, B, C, D, E, F, G, H>() {
      private final P1<A> a = P1.memo(u -> self._1());
      private final P1<B> b = P1.memo(u -> self._2());
      private final P1<C> c = P1.memo(u -> self._3());
      private final P1<D> d = P1.memo(u -> self._4());
      private final P1<E> e = P1.memo(u -> self._5());
      private final P1<F> f = P1.memo(u -> self._6());
      private final P1<G> g = P1.memo(u -> self._7());
      private final P1<H> h = P1.memo(u -> self._8());

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

  @Override
	public String toString() {
		return Show.p8Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow(), Show.<E>anyShow(), Show.<F>anyShow(), Show.<G>anyShow(), Show.<H>anyShow()).showS(this);
	}

  @Override
  public boolean equals(Object other) {
    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.p8Equal(Equal.<A>anyEqual(), Equal.<B>anyEqual(), Equal.<C>anyEqual(), Equal.<D>anyEqual(), Equal.<E>anyEqual(), Equal.<F>anyEqual(), Equal.<G>anyEqual(), Equal.<H>anyEqual()).eq(this, (P8<A, B, C, D, E, F, G, H>) other)));
  }

  @Override
  public int hashCode() {
    return Hash.p8Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash(), Hash.<E>anyHash(), Hash.<F>anyHash(), Hash.<G>anyHash(), Hash.<H>anyHash()).hash(this);
  }

}
