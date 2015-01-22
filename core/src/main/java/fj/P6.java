package fj;

/**
 * A product-6.
 *
 * @version %build.number%
 */
@SuppressWarnings({"UnnecessaryFullyQualifiedName"})
public abstract class P6<A, B, C, D, E, F> {
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
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<X, B, C, D, E, F> map1(final fj.F<A, X> f) {
    return new P6<X, B, C, D, E, F>() {
      public X _1() {
        return f.f(P6.this._1());
      }

      public B _2() {
        return P6.this._2();
      }

      public C _3() {
        return P6.this._3();
      }

      public D _4() {
        return P6.this._4();
      }

      public E _5() {
        return P6.this._5();
      }

      public F _6() {
        return P6.this._6();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<A, X, C, D, E, F> map2(final fj.F<B, X> f) {
    return new P6<A, X, C, D, E, F>() {
      public A _1() {
        return P6.this._1();
      }

      public X _2() {
        return f.f(P6.this._2());
      }

      public C _3() {
        return P6.this._3();
      }

      public D _4() {
        return P6.this._4();
      }

      public E _5() {
        return P6.this._5();
      }

      public F _6() {
        return P6.this._6();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<A, B, X, D, E, F> map3(final fj.F<C, X> f) {
    return new P6<A, B, X, D, E, F>() {
      public A _1() {
        return P6.this._1();
      }

      public B _2() {
        return P6.this._2();
      }

      public X _3() {
        return f.f(P6.this._3());
      }

      public D _4() {
        return P6.this._4();
      }

      public E _5() {
        return P6.this._5();
      }

      public F _6() {
        return P6.this._6();
      }
    };
  }

  /**
   * Map the fourth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<A, B, C, X, E, F> map4(final fj.F<D, X> f) {
    return new P6<A, B, C, X, E, F>() {
      public A _1() {
        return P6.this._1();
      }

      public B _2() {
        return P6.this._2();
      }

      public C _3() {
        return P6.this._3();
      }

      public X _4() {
        return f.f(P6.this._4());
      }

      public E _5() {
        return P6.this._5();
      }

      public F _6() {
        return P6.this._6();
      }
    };
  }

  /**
   * Map the fifth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<A, B, C, D, X, F> map5(final fj.F<E, X> f) {
    return new P6<A, B, C, D, X, F>() {
      public A _1() {
        return P6.this._1();
      }

      public B _2() {
        return P6.this._2();
      }

      public C _3() {
        return P6.this._3();
      }

      public D _4() {
        return P6.this._4();
      }

      public X _5() {
        return f.f(P6.this._5());
      }

      public F _6() {
        return P6.this._6();
      }
    };
  }

  /**
   * Map the sixth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P6<A, B, C, D, E, X> map6(final fj.F<F, X> f) {
    return new P6<A, B, C, D, E, X>() {
      public A _1() {
        return P6.this._1();
      }

      public B _2() {
        return P6.this._2();
      }

      public C _3() {
        return P6.this._3();
      }

      public D _4() {
        return P6.this._4();
      }

      public E _5() {
        return P6.this._5();
      }

      public X _6() {
        return f.f(P6.this._6());
      }
    };
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__3()).f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__4()).f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__5()).f(this);
  }

  /**
   * Returns the 1-product projection over the sixth element.
   *
   * @return the 1-product projection over the sixth element.
   */
  public final P1<F> _6_() {
    return F1Functions.lazy(P6.<A, B, C, D, E, F>__6()).f(this);
  }

  /**
   * Provides a memoising P6 that remembers its values.
   *
   * @return A P6 that calls this P6 once for any given element and remembers the value for subsequent calls.
   */
  public final P6<A, B, C, D, E, F> memo() {
      P6<A, B, C, D, E, F> self = this;
    return new P6<A, B, C, D, E, F>() {
      private final P1<A> a = P1.memo(u -> self._1());
      private final P1<B> b = P1.memo(u -> self._2());
      private final P1<C> c = P1.memo(u -> self._3());
      private final P1<D> d = P1.memo(u -> self._4());
      private final P1<E> e = P1.memo(u -> self._5());
      private final P1<F> f = P1.memo(u -> self._6());

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
    };
  }


  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, A> __1() {
    return new fj.F<P6<A, B, C, D, E, F$>, A>() {
      public A f(final P6<A, B, C, D, E, F$> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, B> __2() {
    return new fj.F<P6<A, B, C, D, E, F$>, B>() {
      public B f(final P6<A, B, C, D, E, F$> p) {
        return p._2();
      }
    };
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, C> __3() {
    return new fj.F<P6<A, B, C, D, E, F$>, C>() {
      public C f(final P6<A, B, C, D, E, F$> p) {
        return p._3();
      }
    };
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, D> __4() {
    return new fj.F<P6<A, B, C, D, E, F$>, D>() {
      public D f(final P6<A, B, C, D, E, F$> p) {
        return p._4();
      }
    };
  }

  /**
   * Returns a function that returns the fifth element of a product.
   *
   * @return A function that returns the fifth element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, E> __5() {
    return new fj.F<P6<A, B, C, D, E, F$>, E>() {
      public E f(final P6<A, B, C, D, E, F$> p) {
        return p._5();
      }
    };
  }

  /**
   * Returns a function that returns the sixth element of a product.
   *
   * @return A function that returns the sixth element of a product.
   */
  public static <A, B, C, D, E, F$> fj.F<P6<A, B, C, D, E, F$>, F$> __6() {
    return new fj.F<P6<A, B, C, D, E, F$>, F$>() {
      public F$ f(final P6<A, B, C, D, E, F$> p) {
        return p._6();
      }
    };
  }

    @Override
	public String toString() {
		return Show.p6Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow(), Show.<E>anyShow(), Show.<F>anyShow()).showS(this);
	}


  @Override
  public boolean equals(Object other) {
    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.p6Equal(Equal.<A>anyEqual(), Equal.<B>anyEqual(), Equal.<C>anyEqual(), Equal.<D>anyEqual(), Equal.<E>anyEqual(), Equal.<F>anyEqual()).eq(this, (P6<A, B, C, D, E, F>) other)));
  }

  @Override
  public int hashCode() {
    return Hash.p6Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash(), Hash.<E>anyHash(), Hash.<F>anyHash()).hash(this);
  }

}
