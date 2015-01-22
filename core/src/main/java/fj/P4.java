package fj;

/**
 * A product-4.
 *
 * @version %build.number%
 */
public abstract class P4<A, B, C, D> {
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
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P4<X, B, C, D> map1(final F<A, X> f) {
    return new P4<X, B, C, D>() {
      public X _1() {
        return f.f(P4.this._1());
      }

      public B _2() {
        return P4.this._2();
      }

      public C _3() {
        return P4.this._3();
      }

      public D _4() {
        return P4.this._4();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P4<A, X, C, D> map2(final F<B, X> f) {
    return new P4<A, X, C, D>() {
      public A _1() {
        return P4.this._1();
      }

      public X _2() {
        return f.f(P4.this._2());
      }

      public C _3() {
        return P4.this._3();
      }

      public D _4() {
        return P4.this._4();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P4<A, B, X, D> map3(final F<C, X> f) {
    return new P4<A, B, X, D>() {
      public A _1() {
        return P4.this._1();
      }

      public B _2() {
        return P4.this._2();
      }

      public X _3() {
        return f.f(P4.this._3());
      }

      public D _4() {
        return P4.this._4();
      }
    };
  }

  /**
   * Map the fourth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P4<A, B, C, X> map4(final F<D, X> f) {
    return new P4<A, B, C, X>() {
      public A _1() {
        return P4.this._1();
      }

      public B _2() {
        return P4.this._2();
      }

      public C _3() {
        return P4.this._3();
      }

      public X _4() {
        return f.f(P4.this._4());
      }
    };
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return F1Functions.lazy(P4.<A, B, C, D>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P4.<A, B, C, D>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P4.<A, B, C, D>__3()).f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return F1Functions.lazy(P4.<A, B, C, D>__4()).f(this);
  }

  /**
   * Provides a memoising P4 that remembers its values.
   *
   * @return A P4 that calls this P4 once for any given element and remembers the value for subsequent calls.
   */
  public final P4<A, B, C, D> memo() {
      P4<A, B, C, D> self = this;
    return new P4<A, B, C, D>() {
      private final P1<A> a = P1.memo(u -> self._1());
      private final P1<B> b = P1.memo(u -> self._2());
      private final P1<C> c = P1.memo(u -> self._3());
      private final P1<D> d = P1.memo(u -> self._4());

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
    };
  }


  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, A> __1() {
    return new F<P4<A, B, C, D>, A>() {
      public A f(final P4<A, B, C, D> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, B> __2() {
    return new F<P4<A, B, C, D>, B>() {
      public B f(final P4<A, B, C, D> p) {
        return p._2();
      }
    };
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, C> __3() {
    return new F<P4<A, B, C, D>, C>() {
      public C f(final P4<A, B, C, D> p) {
        return p._3();
      }
    };
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, D> __4() {
    return new F<P4<A, B, C, D>, D>() {
      public D f(final P4<A, B, C, D> p) {
        return p._4();
      }
    };
  }

  @Override
	public String toString() {
		return Show.p4Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow()).showS(this);
	}

  @Override
  public boolean equals(Object other) {
    return Equal.shallowEqualsO(this, other).orSome(P.lazy(u -> Equal.p4Equal(Equal.<A>anyEqual(), Equal.<B>anyEqual(), Equal.<C>anyEqual(), Equal.<D>anyEqual()).eq(this, (P4<A, B, C, D>) other)));
  }

  @Override
  public int hashCode() {
    return Hash.p4Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash()).hash(this);
  }

}
