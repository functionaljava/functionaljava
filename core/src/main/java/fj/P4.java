package fj;

import static fj.P.weakMemo;

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
   * Creates a {@link P5} by adding the given element to the current {@link P4}
   *
   * @param el the element to append
   * @return A {@link P5} containing the original {@link P4} with the extra element added at the end
   */
  public final <E> P5<A, B, C, D, E> append(E el) {
    return P.p(_1(), _2(), _3(), _4(), el);
  }

  /**
   * Creates a {@link P6} by adding the given element to the current {@link P4}
   *
   * @param el the element to append
   * @return A {@link P6} containing the original {@link P4} with the extra element added at the end
   */
  public final <E, F> P6<A, B, C, D, E, F> append(P2<E, F> el) {
    return P.p(_1(), _2(), _3(), _4(), el._1(), el._2());
  }

  /**
   * Creates a {@link P7} by adding the given element to the current {@link P4}
   *
   * @param el the element to append
   * @return A {@link P7} containing the original {@link P4} with the extra element added at the end
   */
  public final <E, F, G> P7<A, B, C, D, E, F, G> append(P3<E, F, G> el) {
    return P.p(_1(), _2(), _3(), _4(), el._1(), el._2(), el._3());
  }

  /**
   * Creates a {@link P8} by adding the given element to the current {@link P4}
   *
   * @param el the element to append
   * @return A {@link P8} containing the original {@link P4} with the extra element added at the end
   */
  public final <E, F, G, H> P8<A, B, C, D, E, F, G, H> append(P4<E, F, G, H> el) {
    return P.p(_1(), _2(), _3(), _4(), el._1(), el._2(), el._3(), el._4());
  }




  /**
   * Provides a memoising P4 that remembers its values.
   *
   * @return A P4 that calls this P4 once for any given element and remembers the value for subsequent calls.
   */
  public final P4<A, B, C, D> memo() {
      P4<A, B, C, D> self = this;
    return new P4<A, B, C, D>() {
      private final P1<A> a = weakMemo(self::_1);
      private final P1<B> b = weakMemo(self::_2);
      private final P1<C> c = weakMemo(self::_3);
      private final P1<D> d = weakMemo(self::_4);

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
    return P4::_1;
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, B> __2() {
    return P4::_2;
  }

  /**
   * Returns a function that returns the third element of a product.
   *
   * @return A function that returns the third element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, C> __3() {
    return P4::_3;
  }

  /**
   * Returns a function that returns the fourth element of a product.
   *
   * @return A function that returns the fourth element of a product.
   */
  public static <A, B, C, D> F<P4<A, B, C, D>, D> __4() {
    return P4::_4;
  }

  @Override
	public final String toString() {
		return Show.p4Show(Show.<A>anyShow(), Show.<B>anyShow(), Show.<C>anyShow(), Show.<D>anyShow()).showS(this);
	}

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(P4.class, this, other,
        () -> Equal.p4Equal(Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.p4Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash(), Hash.<D>anyHash()).hash(this);
  }

}
