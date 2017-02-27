package fj;

import static fj.Unit.unit;

/**
 * Functions across products.
 *
 * @version %build.number%
 */
public final class P {
  private P() {
    throw new UnsupportedOperationException();
  }

  /**
   * A function that puts an element in a product-1.
   *
   * @return A function that puts an element in a product-1.
   */
  public static <A> F<A, P1<A>> p1() {
    return P::p;
  }

  /**
   * A function that puts an element in a product-1.
   *
   * @param a The element.
   * @return The product-1.
   */
  public static <A> P1<A> p(final A a) {
    return new P1<A>() {
      @Override public A _1() {
        return a;
      }
      @Override public P1<A> hardMemo() { return this; }
      @Override public P1<A> weakMemo() { return this; }
      @Override public P1<A> softMemo() { return this; }
    };
  }

  /**
   * Convert a F0 into a P1, using call-by-need semantic:
   * function f is evaluated at most once, at first to {@link P1#_1()}.
   */
  public static <A> P1<A> hardMemo(F0<A> f) {
    return new P1.Memo<>(f);
  }

  /**
   * Convert a F0 into a P1, using weak call-by-need semantic:
   * function f is evaluated at first call to {@link P1#_1()}
   * and at each subsequent call if and only if the reference have been garbage collected.
   */
  public static <A> P1<A> weakMemo(F0<A> f) {
    return new P1.WeakReferenceMemo<>(f);
  }

  /**
   * Convert a F0 into a P1, using soft call-by-need semantic:
   * function f is evaluated at first call to {@link P1#_1()}
   * and at each subsequent call if and only if the reference have been garbage collected
   * due of shortage of memory (ie. to avoid OutOfMemoryErrors).
   */
  public static <A> P1<A> softMemo(F0<A> f) {
    return new P1.SoftReferenceMemo<>(f);
  }

  /**
   * Convert a F0 into a P1, using call-by-name semantic:
   * function f is evaluated at each call to {@link P1#_1()}.
   */
  public static <A> P1<A> lazy(F0<A> f) {
    return new P1<A>() {
      @Override
      public A _1() {
        return f.f();
      }
    };
  }

    public static <A, B> P2<A, B> lazy(final F0<A> pa, final F0<B> pb) {
        return new P2<A, B>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
        };
    }

    public static <A, B, C> P3<A, B, C> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc) {
        return new P3<A, B, C>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }
        };
    }

    public static <A, B, C, D> P4<A, B, C, D> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc, final F0<D> pd) {
        return new P4<A, B, C, D>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }

            @Override
            public D _4() {
                return pd.f();
            }
        };
    }

    public static <A, B, C, D, E> P5<A, B, C, D, E> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc, final F0<D> pd, F0<E> pe) {
        return new P5<A, B, C, D, E>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }

            @Override
            public D _4() {
                return pd.f();
            }

            @Override
            public E _5() {
                return pe.f();
            }
        };
    }

    public static <A, B, C, D, E, F> P6<A, B, C, D, E, F> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc, final F0<D> pd, F0<E> pe, F0<F> pf) {
        return new P6<A, B, C, D, E, F>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }

            @Override
            public D _4() {
                return pd.f();
            }

            @Override
            public E _5() {
                return pe.f();
            }

            @Override
            public F _6() {
                return pf.f();
            }
        };
    }

    public static <A, B, C, D, E, F, G> P7<A, B, C, D, E, F, G> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc, final F0<D> pd, F0<E> pe, F0<F> pf, F0<G> pg) {
        return new P7<A, B, C, D, E, F, G>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }

            @Override
            public D _4() {
                return pd.f();
            }

            @Override
            public E _5() {
                return pe.f();
            }

            @Override
            public F _6() {
                return pf.f();
            }

            @Override
            public G _7() {
                return pg.f();
            }
        };
    }

    public static <A, B, C, D, E, F, G, H> P8<A, B, C, D, E, F, G, H> lazy(final F0<A> pa, final F0<B> pb, final F0<C> pc, final F0<D> pd, F0<E> pe, F0<F> pf, F0<G> pg, F0<H> ph) {
        return new P8<A, B, C, D, E, F, G, H>() {
            @Override
            public A _1() {
                return pa.f();
            }
            @Override
            public B _2() {
                return pb.f();
            }
            @Override
            public C _3() {
                return pc.f();
            }

            @Override
            public D _4() {
                return pd.f();
            }

            @Override
            public E _5() {
                return pe.f();
            }

            @Override
            public F _6() {
                return pf.f();
            }

            @Override
            public G _7() {
                return pg.f();
            }

            @Override
            public H _8() {
                return ph.f();
            }
        };
    }

    public static <A, B> P2<A, B> lazyProduct(F0<P2<A, B>> f) {
        return lazy(() -> f.f()._1(), () -> f.f()._2());
    }

    /**
   * A function that puts an element in a product-2.
   *
   * @return A function that puts an element in a product-2.
   */
  public static <A, B> F<A, F<B, P2<A, B>>> p2() {
    return a -> b -> p(a, b);
  }

  /**
   * A function that puts elements in a product-2.
   *
   * @param a An element.
   * @param b An element.
   * @return The product-2.
   */
  public static <A, B> P2<A, B> p(final A a, final B b) {
    return new P2<A, B>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }
    };
  }

  /**
   * A function that puts elements in a product-3.
   *
   * @return A function that puts elements in a product-3.
   */
  public static <A, B, C> F<A, F<B, F<C, P3<A, B, C>>>> p3() {
    return a -> b -> c -> p(a, b, c);
  }

  /**
   * A function that puts elements in a product-3.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @return The product-3.
   */
  public static <A, B, C> P3<A, B, C> p(final A a, final B b, final C c) {
    return new P3<A, B, C>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }
    };
  }

  /**
   * A function that puts an element in a product-4.
   *
   * @return A function that puts an element in a product-4.
   */
  public static <A, B, C, D> F<A, F<B, F<C, F<D, P4<A, B, C, D>>>>> p4() {
    return a -> b -> c -> d -> p(a, b, c, d);
  }

  /**
   * A function that puts elements in a product-4.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @param d An element.
   * @return The product-4.
   */
  public static <A, B, C, D> P4<A, B, C, D> p(final A a, final B b, final C c, final D d) {
    return new P4<A, B, C, D>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }

      public D _4() {
        return d;
      }
    };
  }

  /**
   * A function that puts an element in a product-5.
   *
   * @return A function that puts an element in a product-5.
   */
  public static <A, B, C, D, E> F<A, F<B, F<C, F<D, F<E, P5<A, B, C, D, E>>>>>> p5() {
    return a -> b -> c -> d -> e -> p(a, b, c, d, e);
  }

  /**
   * A function that puts elements in a product-5.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @param d An element.
   * @param e An element.
   * @return The product-5.
   */
  public static <A, B, C, D, E> P5<A, B, C, D, E> p(final A a, final B b, final C c, final D d, final E e) {
    return new P5<A, B, C, D, E>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }

      public D _4() {
        return d;
      }

      public E _5() {
        return e;
      }
    };
  }

  /**
   * A function that puts an element in a product-6.
   *
   * @return A function that puts an element in a product-6.
   */
  public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>>>> p6() {
    return a -> b -> c -> d -> e -> f -> p(a, b, c, d, e, f);
  }

  /**
   * A function that puts elements in a product-6.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @param d An element.
   * @param e An element.
   * @param f An element.
   * @return The product-6.
   */
  public static <A, B, C, D, E, F$> P6<A, B, C, D, E, F$> p(final A a, final B b, final C c, final D d, final E e, final F$ f) {
    return new P6<A, B, C, D, E, F$>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }

      public D _4() {
        return d;
      }

      public E _5() {
        return e;
      }

      public F$ _6() {
        return f;
      }
    };
  }

  /**
   * A function that puts an element in a product-7.
   *
   * @return A function that puts an element in a product-7.
   */
  public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>>>> p7() {
    return a -> b -> c -> d -> e -> f -> g -> p(a, b, c, d, e, f, g);
  }
  
  /**
   * A function that puts elements in a product-7.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @param d An element.
   * @param e An element.
   * @param f An element.
   * @param g An element.
   * @return The product-7.
   */
  public static <A, B, C, D, E, F$, G> P7<A, B, C, D, E, F$, G> p(final A a, final B b, final C c, final D d, final E e, final F$ f, final G g) {
    return new P7<A, B, C, D, E, F$, G>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }

      public D _4() {
        return d;
      }

      public E _5() {
        return e;
      }

      public F$ _6() {
        return f;
      }

      public G _7() {
        return g;
      }
    };
  }

  /**
   * A function that puts an element in a product-8.
   *
   * @return A function that puts an element in a product-8.
   */
  public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>>>> p8() {
    return a -> b -> c -> d -> e -> f -> g -> h -> p(a, b, c, d, e, f, g, h);
  }

  /**
   * A function that puts elements in a product-8.
   *
   * @param a An element.
   * @param b An element.
   * @param c An element.
   * @param d An element.
   * @param e An element.
   * @param f An element.
   * @param g An element.
   * @param h An element.
   * @return The product-8.
   */
  public static <A, B, C, D, E, F$, G, H> P8<A, B, C, D, E, F$, G, H> p(final A a, final B b, final C c, final D d, final E e, final F$ f, final G g, final H h) {
    return new P8<A, B, C, D, E, F$, G, H>() {
      public A _1() {
        return a;
      }

      public B _2() {
        return b;
      }

      public C _3() {
        return c;
      }

      public D _4() {
        return d;
      }

      public E _5() {
        return e;
      }

      public F$ _6() {
        return f;
      }

      public G _7() {
        return g;
      }

      public H _8() {
        return h;
      }
    };
  }

    public static <A> P1<A> lazy(F<Unit, A> f) {
        return lazy(() -> f.f(unit()));
    }

    public static <A, B> P2<A, B> lazy(F<Unit, A> fa, F<Unit, B> fb) {
        return lazy(() -> fa.f(unit()), () -> fb.f(unit()));
    }

    public static <A, B, C> P3<A, B, C> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc) {
        return new P3<A, B, C>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
        };
    }


    public static <A, B, C, D> P4<A, B, C, D> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc, F<Unit, D> fd) {
        return new P4<A, B, C, D>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
            @Override
            public D _4() {
                return fd.f(unit());
            }
        };
    }

    public static <A, B, C, D, E> P5<A, B, C, D, E> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc, F<Unit, D> fd, F<Unit, E> fe) {
        return new P5<A, B, C, D, E>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
            @Override
            public D _4() {
                return fd.f(unit());
            }
            @Override
            public E _5() {
                return fe.f(unit());
            }
        };
    }

    public static <A, B, C, D, E, F$> P6<A, B, C, D, E, F$> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc, F<Unit, D> fd, F<Unit, E> fe, F<Unit, F$> ff) {
        return new P6<A, B, C, D, E, F$>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
            @Override
            public D _4() {
                return fd.f(unit());
            }
            @Override
            public E _5() {
                return fe.f(unit());
            }
            @Override
            public F$ _6() {
                return ff.f(unit());
            }
        };
    }

    public static <A, B, C, D, E, F$, G> P7<A, B, C, D, E, F$, G> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc, F<Unit, D> fd, F<Unit, E> fe, F<Unit, F$> ff, F<Unit, G> fg) {
        return new P7<A, B, C, D, E, F$, G>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
            @Override
            public D _4() {
                return fd.f(unit());
            }
            @Override
            public E _5() {
                return fe.f(unit());
            }
            @Override
            public F$ _6() {
                return ff.f(unit());
            }
            @Override
            public G _7() {
                return fg.f(unit());
            }
        };
    }

    public static <A, B, C, D, E, F$, G, H> P8<A, B, C, D, E, F$, G, H> lazy(F<Unit, A> fa, F<Unit, B> fb, F<Unit, C> fc, F<Unit, D> fd, F<Unit, E> fe, F<Unit, F$> ff, F<Unit, G> fg, F<Unit, H> fh) {
        return new P8<A, B, C, D, E, F$, G, H>() {
            @Override
            public A _1() {
                return fa.f(unit());
            }
            @Override
            public B _2() {
                return fb.f(unit());
            }
            @Override
            public C _3() {
                return fc.f(unit());
            }
            @Override
            public D _4() {
                return fd.f(unit());
            }
            @Override
            public E _5() {
                return fe.f(unit());
            }
            @Override
            public F$ _6() {
                return ff.f(unit());
            }
            @Override
            public G _7() {
                return fg.f(unit());
            }
            @Override
            public H _8() {
                return fh.f(unit());
            }
        };
    }

}
