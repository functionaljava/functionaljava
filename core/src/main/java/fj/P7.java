package fj;

import static fj.P.weakMemo;

/**
 * A product-7.
 *
 * @version %build.number%
 */
@SuppressWarnings("UnnecessaryFullyQualifiedName")
public abstract class P7<A, B, C, D, E, F, G> {
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
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<X, B, C, D, E, F, G> map1(final fj.F<A, X> f) {
    return new P7<X, B, C, D, E, F, G>() {
      public X _1() {
        return f.f(P7.this._1());
      }

      public B _2() {
        return P7.this._2();
      }

      public C _3() {
        return P7.this._3();
      }

      public D _4() {
        return P7.this._4();
      }

      public E _5() {
        return P7.this._5();
      }

      public F _6() {
        return P7.this._6();
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, X, C, D, E, F, G> map2(final fj.F<B, X> f) {
    return new P7<A, X, C, D, E, F, G>() {
      public A _1() {
        return P7.this._1();
      }

      public X _2() {
        return f.f(P7.this._2());
      }

      public C _3() {
        return P7.this._3();
      }

      public D _4() {
        return P7.this._4();
      }

      public E _5() {
        return P7.this._5();
      }

      public F _6() {
        return P7.this._6();
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the third element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, B, X, D, E, F, G> map3(final fj.F<C, X> f) {
    return new P7<A, B, X, D, E, F, G>() {
      public A _1() {
        return P7.this._1();
      }

      public B _2() {
        return P7.this._2();
      }

      public X _3() {
        return f.f(P7.this._3());
      }

      public D _4() {
        return P7.this._4();
      }

      public E _5() {
        return P7.this._5();
      }

      public F _6() {
        return P7.this._6();
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the fourth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, B, C, X, E, F, G> map4(final fj.F<D, X> f) {
    return new P7<A, B, C, X, E, F, G>() {
      public A _1() {
        return P7.this._1();
      }

      public B _2() {
        return P7.this._2();
      }

      public C _3() {
        return P7.this._3();
      }

      public X _4() {
        return f.f(P7.this._4());
      }

      public E _5() {
        return P7.this._5();
      }

      public F _6() {
        return P7.this._6();
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the fifth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, B, C, D, X, F, G> map5(final fj.F<E, X> f) {
    return new P7<A, B, C, D, X, F, G>() {
      public A _1() {
        return P7.this._1();
      }

      public B _2() {
        return P7.this._2();
      }

      public C _3() {
        return P7.this._3();
      }

      public D _4() {
        return P7.this._4();
      }

      public X _5() {
        return f.f(P7.this._5());
      }

      public F _6() {
        return P7.this._6();
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the sixth element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, B, C, D, E, X, G> map6(final fj.F<F, X> f) {
    return new P7<A, B, C, D, E, X, G>() {
      public A _1() {
        return P7.this._1();
      }

      public B _2() {
        return P7.this._2();
      }

      public C _3() {
        return P7.this._3();
      }

      public D _4() {
        return P7.this._4();
      }

      public E _5() {
        return P7.this._5();
      }

      public X _6() {
        return f.f(P7.this._6());
      }

      public G _7() {
        return P7.this._7();
      }
    };
  }

  /**
   * Map the seventh element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public final <X> P7<A, B, C, D, E, F, X> map7(final fj.F<G, X> f) {
    return new P7<A, B, C, D, E, F, X>() {
      public A _1() {
        return P7.this._1();
      }

      public B _2() {
        return P7.this._2();
      }

      public C _3() {
        return P7.this._3();
      }

      public D _4() {
        return P7.this._4();
      }

      public E _5() {
        return P7.this._5();
      }

      public F _6() {
        return P7.this._6();
      }

      public X _7() {
        return f.f(P7.this._7());
      }
    };
  }

  /**
   * Returns the 1-product projection over the first element.
   *
   * @return the 1-product projection over the first element.
   */
  public final P1<A> _1_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__1()).f(this);
  }

  /**
   * Returns the 1-product projection over the second element.
   *
   * @return the 1-product projection over the second element.
   */
  public final P1<B> _2_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__2()).f(this);
  }

  /**
   * Returns the 1-product projection over the third element.
   *
   * @return the 1-product projection over the third element.
   */
  public final P1<C> _3_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__3()).f(this);
  }

  /**
   * Returns the 1-product projection over the fourth element.
   *
   * @return the 1-product projection over the fourth element.
   */
  public final P1<D> _4_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__4()).f(this);
  }

  /**
   * Returns the 1-product projection over the fifth element.
   *
   * @return the 1-product projection over the fifth element.
   */
  public final P1<E> _5_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__5()).f(this);
  }

  /**
   * Returns the 1-product projection over the sixth element.
   *
   * @return the 1-product projection over the sixth element.
   */
  public final P1<F> _6_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__6()).f(this);
  }

  /**
   * Returns the 1-product projection over the seventh element.
   *
   * @return the 1-product projection over the seventh element.
   */
  public final P1<G> _7_() {
    return F1Functions.lazy(P7.<A, B, C, D, E, F, G>__7()).f(this);
  }

  /**
   * Creates a {@link P8} by adding the given element to the current {@link P7}
   *
   * @param el the element to append
   * @return A {@link P8} containing the original {@link P7} with the extra element added at the end
   */
  public final <H> P8<A, B, C, D, E, F, G, H> append(H el) {
    return P.p(_1(), _2(), _3(), _4(), _5(), _6(), _7(), el);
  }

  /**
   * Provides a memoising P7 that remembers its values.
   *
   * @return A P7 that calls this P7 once for any given element and remembers the value for subsequent calls.
   */
  public final P7<A, B, C, D, E, F, G> memo() {
      P7<A, B, C, D, E, F, G> self = this;
    return new P7<A, B, C, D, E, F, G>() {
      private final P1<A> a = weakMemo(self::_1);
      private final P1<B> b = weakMemo(self::_2);
      private final P1<C> c = weakMemo(self::_3);
      private final P1<D> d = weakMemo(self::_4);
      private final P1<E> e = weakMemo(self::_5);
      private final P1<F> f = weakMemo(self::_6);
      private final P1<G> g = weakMemo(self::_7);

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
    };
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, A> __1() {
    return P7::_1;
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, B> __2() {
    return P7::_2;
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, C> __3() {
    return P7::_3;
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, D> __4() {
    return P7::_4;
  }

  /**
   * Returns a function that returns the fifth element of a product.
   *
   * @return A function that returns the fifth element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, E> __5() {
    return P7::_5;
  }

  /**
   * Returns a function that returns the sixth element of a product.
   *
   * @return A function that returns the sixth element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, F$> __6() {
    return P7::_6;
  }

  /**
   * Returns a function that returns the seventh element of a product.
   *
   * @return A function that returns the seventh element of a product.
   */
  public static <A, B, C, D, E, F$, G> fj.F<P7<A, B, C, D, E, F$, G>, G> __7() {
    return P7::_7;
  }

  @Override
	public final String toString() {
		return Show.p7Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow(), Show.<E>anyShow(), Show.<F>anyShow(), Show.<G>anyShow()).showS(this);
	}

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(P7.class, this, other,
        () -> Equal.p7Equal(Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.p7Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash(), Hash.<E>anyHash(), Hash.<F>anyHash(), Hash.<G>anyHash()).hash(this);
  }

}
