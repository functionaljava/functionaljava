package fj;

/**
 * A product-5.
 *
 * @version %build.number%
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
    return F1Functions.lazy(P5.<A, B, C, D, E>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P5.<A, B, C, D, E>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P5.<A, B, C, D, E>__3()).f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return F1Functions.lazy(P5.<A, B, C, D, E>__4()).f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return F1Functions.lazy(P5.<A, B, C, D, E>__5()).f(this);
  }

  /**
   * Provides a memoising P5 that remembers its values.
   *
   * @return A P5 that calls this P5 once for any given element and remembers the value for subsequent calls.
   */
  public final P5<A, B, C, D, E> memo() {
      P5<A, B, C, D, E> self = this;
    return new P5<A, B, C, D, E>() {
      private final P1<A> a = P1.memo(u -> self._1());
      private final P1<B> b = P1.memo(u -> self._2());
      private final P1<C> c = P1.memo(u -> self._3());
      private final P1<D> d = P1.memo(u -> self._4());
      private final P1<E> e = P1.memo(u -> self._5());

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

  @Override
	public String toString() {
		return Show.p5Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow(), Show.<E>anyShow()).showS(this);
	}

  @Override
  public boolean equals(Object other) {
    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.p5Equal(Equal.<A>anyEqual(), Equal.<B>anyEqual(), Equal.<C>anyEqual(), Equal.<D>anyEqual(), Equal.<E>anyEqual()).eq(this, (P5<A, B, C, D, E>) other)));
  }

  @Override
  public int hashCode() {
    return Hash.p5Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash(), Hash.<E>anyHash()).hash(this);
  }

}
