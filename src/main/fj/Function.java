package fj;

import fj.data.Option;

/**
 * Transformations on functions.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
public final class Function {
  private Function() {
    throw new UnsupportedOperationException();
  }

  /**
   * Function application with the arguments flipped.
   *
   * @param a The value to apply the function to.
   * @return A function that is partially-applied to the given value.
   */
  public static <A, B> F<F<A, B>, B> apply(final A a) {
    return new F<F<A, B>, B>() {
      public B f(final F<A, B> k) {
        return k.f(a);
      }
    };
  }

  /**
   * Function composition.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<B, C>, F<F<A, B>, F<A, C>>> compose() {
    return new F<F<B, C>, F<F<A, B>, F<A, C>>>() {
      public F<F<A, B>, F<A, C>> f(final F<B, C> f) {
        return new F<F<A, B>, F<A, C>>() {
          public F<A, C> f(final F<A, B> g) {
            return compose(f, g);
          }
        };
      }
    };
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> compose(final F<B, C> f, final F<A, B> g) {
    return new F<A, C>() {
      public C f(final A a) {
        return f.f(g.f(a));
      }
    };
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C, D> F<A, F<B, D>> compose2(final F<C, D> f, final F<A, F<B, C>> g) {
    return new F<A, F<B, D>>() {
      public F<B, D> f(final A a) {
        return new F<B, D>() {
          public D f(final B b) {
            return f.f(g.f(a).f(b));
          }
        };
      }
    };
  }


  /**
   * Function composition flipped.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<A, B>, F<F<B, C>, F<A, C>>> andThen() {
    return new F<F<A, B>, F<F<B, C>, F<A, C>>>() {
      public F<F<B, C>, F<A, C>> f(final F<A, B> g) {
        return new F<F<B, C>, F<A, C>>() {
          public F<A, C> f(final F<B, C> f) {
            return Function.andThen(g, f);
          }
        };
      }
    };
  }

  /**
   * Function composition flipped.
   *
   * @param g A function to compose with another.
   * @param f A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> andThen(final F<A, B> g, final F<B, C> f) {
    return new F<A, C>() {
      public C f(final A a) {
        return f.f(g.f(a));
      }
    };
  }

  /**
   * The identity transformation.
   *
   * @return The identity transformation.
   */
  public static <A> F<A, A> identity() {
    return new F<A, A>() {
      public A f(final A a) {
        return a;
      }
    };
  }

  /**
   * Returns a function that given an argument, returns a function that ignores its argument.
   *
   * @return A function that given an argument, returns a function that ignores its argument.
   */
  public static <A, B> F<B, F<A, B>> constant() {
    return new F<B, F<A, B>>() {
      public F<A, B> f(final B b) {
        return constant(b);
      }
    };
  }

  /**
   * Returns a function that ignores its argument to constantly produce the given value.
   *
   * @param b The value to return when the returned function is applied.
   * @return A function that ignores its argument to constantly produce the given value.
   */
  public static <A, B> F<A, B> constant(final B b) {
    return new F<A, B>() {
      public B f(final A a) {
        return b;
      }
    };
  }

  /**
   * Simultaneously covaries and contravaries a function.
   *
   * @param f The function to vary.
   * @return A co- and contravariant function that invokes f on its argument.
   */
  public static <A, B> F<A, B> vary(final F<? super A, ? extends B> f) {
    return new F<A, B>() {
      public B f(final A a) {
        return f.f(a);
      }
    };
  }

  /**
   * Simultaneously covaries and contravaries a function.
   *
   * @return A function that varies and covaries a function.
   */
  public static <C, A extends C, B, D extends B> F<F<C, D>, F<A, B>> vary() {
    return new F<F<C, D>, F<A, B>>() {
      public F<A, B> f(final F<C, D> f) {
        return Function.<A, B>vary(f);
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @return A function that takes a function and flips its arguments.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F<B, F<A, C>>> flip() {
    return new F<F<A, F<B, C>>, F<B, F<A, C>>>() {
      public F<B, F<A, C>> f(final F<A, F<B, C>> f) {
        return flip(f);
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @param f The function to flip.
   * @return The given function flipped.
   */
  public static <A, B, C> F<B, F<A, C>> flip(final F<A, F<B, C>> f) {
    return new F<B, F<A, C>>() {
      public F<A, C> f(final B b) {
        return new F<A, C>() {
          public C f(final A a) {
            return f.f(a).f(b);
          }
        };
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @param f The function to flip.
   * @return The given function flipped.
   */
  public static <A, B, C> F2<B, A, C> flip(final F2<A, B, C> f) {
    return new F2<B, A, C>() {
      public C f(final B b, final A a) {
        return f.f(a, b);
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @return A function that flips the arguments of a given function.
   */
  public static <A, B, C> F<F2<A, B, C>, F2<B, A, C>> flip2() {
    return new F<F2<A, B, C>, F2<B, A, C>>() {
      public F2<B, A, C> f(final F2<A, B, C> f) {
        return flip(f);
      }
    };
  }

  /**
   * Return a function that inspects the argument of the given function for a <code>null</code> value and if so, does
   * not apply the value, instead returning an empty optional value.
   *
   * @param f The function to check for a <code>null</code> argument.
   * @return A function that inspects the argument of the given function for a <code>null</code> value and if so, does
   * not apply the value, instead returning an empty optional value.
   */
  public static <A, B> F<A, Option<B>> nullable(final F<A, B> f) {
    return new F<A, Option<B>>() {
      public Option<B> f(final A a) {
        return a == null ? Option.<B>none() : Option.some(f.f(a));
      }
    };
  }

  /**
   * Curry a function of arity-2.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C> F<A, F<B, C>> curry(final F2<A, B, C> f) {
    return new F<A, F<B, C>>() {
      public F<B, C> f(final A a) {
        return new F<B, C>() {
          public C f(final B b) {
            return f.f(a, b);
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-2.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C> F<B, C> curry(final F2<A, B, C> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @return An uncurried function.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F2<A, B, C>> uncurryF2() {
    return new F<F<A, F<B, C>>, F2<A, B, C>>() {
      public F2<A, B, C> f(final F<A, F<B, C>> f) {
        return uncurryF2(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C> F2<A, B, C> uncurryF2(final F<A, F<B, C>> f) {
    return new F2<A, B, C>() {
      public C f(final A a, final B b) {
        return f.f(a).f(b);
      }
    };
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<A, F<B, F<C, D>>> curry(final F3<A, B, C, D> f) {
    return new F<A, F<B, F<C, D>>>() {
      public F<B, F<C, D>> f(final A a) {
        return new F<B, F<C, D>>() {
          public F<C, D> f(final B b) {
            return new F<C, D>() {
              public D f(final C c) {
                return f.f(a, b, c);
              }
            };
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<B, F<C, D>> curry(final F3<A, B, C, D> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<C, D> curry(final F3<A, B, C, D> f, final A a, final B b) {
    return curry(f, a).f(b);
  }

  /**
   * Uncurry a function of arity-3.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D> F<F<A, F<B, F<C, D>>>, F3<A, B, C, D>> uncurryF3() {
    return new F<F<A, F<B, F<C, D>>>, F3<A, B, C, D>>() {
      public F3<A, B, C, D> f(final F<A, F<B, F<C, D>>> f) {
        return uncurryF3(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-3.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D> F3<A, B, C, D> uncurryF3(final F<A, F<B, F<C, D>>> f) {
    return new F3<A, B, C, D>() {
      public D f(final A a, final B b, final C c) {
        return f.f(a).f(b).f(c);
      }
    };
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<A, F<B, F<C, F<D, E>>>> curry(final F4<A, B, C, D, E> f) {
    return new F<A, F<B, F<C, F<D, E>>>>() {
      public F<B, F<C, F<D, E>>> f(final A a) {
        return new F<B, F<C, F<D, E>>>() {
          public F<C, F<D, E>> f(final B b) {
            return new F<C, F<D, E>>() {
              public F<D, E> f(final C c) {
                return new F<D, E>() {
                  public E f(final D d) {
                    return f.f(a, b, c, d);
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<B, F<C, F<D, E>>> curry(final F4<A, B, C, D, E> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<C, F<D, E>> curry(final F4<A, B, C, D, E> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<D, E> curry(final F4<A, B, C, D, E> f, final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F<F<A, F<B, F<C, F<D, E>>>>, F4<A, B, C, D, E>> uncurryF4() {
    return new F<F<A, F<B, F<C, F<D, E>>>>, F4<A, B, C, D, E>>() {
      public F4<A, B, C, D, E> f(final F<A, F<B, F<C, F<D, E>>>> f) {
        return uncurryF4(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F4<A, B, C, D, E> uncurryF4(final F<A, F<B, F<C, F<D, E>>>> f) {
    return new F4<A, B, C, D, E>() {
      public E f(final A a, final B b, final C c, final D d) {
        return f.f(a).f(b).f(c).f(d);
      }
    };
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F<E, F$>>>>> curry(final F5<A, B, C, D, E, F$> f) {
    return new F<A, F<B, F<C, F<D, F<E, F$>>>>>() {
      public F<B, F<C, F<D, F<E, F$>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F$>>>>() {
          public F<C, F<D, F<E, F$>>> f(final B b) {
            return new F<C, F<D, F<E, F$>>>() {
              public F<D, F<E, F$>> f(final C c) {
                return new F<D, F<E, F$>>() {
                  public F<E, F$> f(final D d) {
                    return new F<E, F$>() {
                      public F$ f(final E e) {
                        return f.f(a, b, c, d, e);
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<B, F<C, F<D, F<E, F$>>>> curry(final F5<A, B, C, D, E, F$> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<C, F<D, F<E, F$>>> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<D, F<E, F$>> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b,
                                                         final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<E, F$> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b, final C c,
                                                   final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Uncurry a function of arity-5.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F<F<A, F<B, F<C, F<D, F<E, F$>>>>>, F5<A, B, C, D, E, F$>> uncurryF5() {
    return new F<F<A, F<B, F<C, F<D, F<E, F$>>>>>, F5<A, B, C, D, E, F$>>() {
      public F5<A, B, C, D, E, F$> f(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
        return uncurryF5(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F5<A, B, C, D, E, F$> uncurryF5(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
    return new F5<A, B, C, D, E, F$>() {
      public F$ f(final A a, final B b, final C c, final D d, final E e) {
        return f.f(a).f(b).f(c).f(d).f(e);
      }
    };
  }

  /**
   * Curry a function of arity-6.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> curry(final F6<A, B, C, D, E, F$, G> f) {
    return new F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>() {
      public F<B, F<C, F<D, F<E, F<F$, G>>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F<F$, G>>>>>() {
          public F<C, F<D, F<E, F<F$, G>>>> f(final B b) {
            return new F<C, F<D, F<E, F<F$, G>>>>() {
              public F<D, F<E, F<F$, G>>> f(final C c) {
                return new F<D, F<E, F<F$, G>>>() {
                  public F<E, F<F$, G>> f(final D d) {
                    return new F<E, F<F$, G>>() {
                      public F<F$, G> f(final E e) {
                        return new F<F$, G>() {
                          public G f(final F$ f$) {
                            return f.f(a, b, c, d, e, f$);
                          }
                        };
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F<F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>, F6<A, B, C, D, E, F$, G>> uncurryF6() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>, F6<A, B, C, D, E, F$, G>>() {
      public F6<A, B, C, D, E, F$, G> f(final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
        return uncurryF6(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F6<A, B, C, D, E, F$, G> uncurryF6(
      final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
    return new F6<A, B, C, D, E, F$, G>() {
      public G f(final A a, final B b, final C c, final D d, final E e, final F$ f$) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$);
      }
    };
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> curry(
      final F7<A, B, C, D, E, F$, G, H> f) {
    return new F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>() {
      public F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>() {
          public F<C, F<D, F<E, F<F$, F<G, H>>>>> f(final B b) {
            return new F<C, F<D, F<E, F<F$, F<G, H>>>>>() {
              public F<D, F<E, F<F$, F<G, H>>>> f(final C c) {
                return new F<D, F<E, F<F$, F<G, H>>>>() {
                  public F<E, F<F$, F<G, H>>> f(final D d) {
                    return new F<E, F<F$, F<G, H>>>() {
                      public F<F$, F<G, H>> f(final E e) {
                        return new F<F$, F<G, H>>() {
                          public F<G, H> f(final F$ f$) {
                            return new F<G, H>() {
                              public H f(final G g) {
                                return f.f(a, b, c, d, e, f$, g);
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>> curry(
      final F7<A, B, C, D, E, F$, G, H> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<C, F<D, F<E, F<F$, F<G, H>>>>> curry(final F7<A, B, C, D, E, F$, G, H> f,
                                                                                 final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<D, F<E, F<F$, F<G, H>>>> curry(final F7<A, B, C, D, E, F$, G, H> f,
                                                                           final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<E, F<F$, F<G, H>>> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a,
                                                                     final B b, final C c, final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @param e An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<F$, F<G, H>> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a,
                                                               final B b, final C c, final D d, final E e) {
    return curry(f).f(a).f(b).f(c).f(d).f(e);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<G, H> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a, final B b,
                                                        final C c, final D d, final E e, final F$ f$) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$);
  }

  /**
   * Uncurry a function of arity-7.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>> uncurryF7() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>>() {
      public F7<A, B, C, D, E, F$, G, H> f(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return uncurryF7(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-7.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F7<A, B, C, D, E, F$, G, H> uncurryF7(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
    return new F7<A, B, C, D, E, F$, G, H>() {
      public H f(final A a, final B b, final C c, final D d, final E e, final F$ f$, final G g) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
      }
    };
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f) {
    return new F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>() {
      public F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>() {
          public F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>> f(final B b) {
            return new F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>() {
              public F<D, F<E, F<F$, F<G, F<H, I>>>>> f(final C c) {
                return new F<D, F<E, F<F$, F<G, F<H, I>>>>>() {
                  public F<E, F<F$, F<G, F<H, I>>>> f(final D d) {
                    return new F<E, F<F$, F<G, F<H, I>>>>() {
                      public F<F$, F<G, F<H, I>>> f(final E e) {
                        return new F<F$, F<G, F<H, I>>>() {
                          public F<G, F<H, I>> f(final F$ f$) {
                            return new F<G, F<H, I>>() {
                              public F<H, I> f(final G g) {
                                return new F<H, I>() {
                                  public I f(final H h) {
                                    return f.f(a, b, c, d, e, f$, g, h);
                                  }
                                };
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<D, F<E, F<F$, F<G, F<H, I>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<E, F<F$, F<G, F<H, I>>>> curry(final F8<A, B, C, D, E, F$, G, H, I> f,
                                                                              final A a, final B b, final C c,
                                                                              final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @param e An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<F$, F<G, F<H, I>>> curry(final F8<A, B, C, D, E, F$, G, H, I> f,
                                                                        final A a, final B b, final C c, final D d,
                                                                        final E e) {
    return curry(f).f(a).f(b).f(c).f(d).f(e);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<G, F<H, I>> curry(final F8<A, B, C, D, E, F$, G, H, I> f, final A a,
                                                                 final B b, final C c, final D d, final E e,
                                                                 final F$ f$) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @param g  An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<H, I> curry(final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b,
                                                           final C c, final D d, final E e, final F$ f$, final G g) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
  }

  /**
   * Uncurry a function of arity-8.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>, F8<A, B, C, D, E, F$, G, H, I>> uncurryF8() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>, F8<A, B, C, D, E, F$, G, H, I>>() {
      public F8<A, B, C, D, E, F$, G, H, I> f(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
        return uncurryF8(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-8.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F8<A, B, C, D, E, F$, G, H, I> uncurryF8(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
    return new F8<A, B, C, D, E, F$, G, H, I>() {
      public I f(final A a, final B b, final C c, final D d, final E e, final F$ f$, final G g, final H h) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g).f(h);
      }
    };
  }

  /**
   * Binds the function in the second argument to the function in the first argument.
   *
   * @param ma A function whose argument type is the same as the argument type of the return value.
   * @param f  A function whose argument type is the same as the return type of <em>ma</em>,
   *           and yields the return value.
   * @return A function that chains the given functions together such that the result of applying
   *         <em>ma</em> to the argument is given to <i>f</i>, yielding a function
   *         that is applied to the argument again.
   */
  public static <A, B, C> F<C, B> bind(final F<C, A> ma, final F<A, F<C, B>> f) {
    return new F<C, B>() {
      public B f(final C m) {
        return f.f(ma.f(m)).f(m);
      }
    };
  }

  /**
   * Performs function application within a higher-order function (applicative functor pattern).
   *
   * @param cab The higher-order function to apply a function to.
   * @param ca  A function to apply within a higher-order function.
   * @return A new function after applying the given higher-order function to the given function.
   */
  public static <A, B, C> F<C, B> apply(final F<C, F<A, B>> cab, final F<C, A> ca) {
    return bind(cab, new F<F<A, B>, F<C, B>>() {
      public F<C, B> f(final F<A, B> f) {
        return compose(new F<A, B>() {
          public B f(final A a) {
            return f.f(a);
          }
        }, ca);
      }
    });
  }

  /**
   * Binds the given function <em>f</em> to the values of the given functions, with a final join.
   *
   * @param ca A function to bind <em>f</em> function to.
   * @param cb A function to bind <em>f</em> function to.
   * @param f  The bound function to be composed with <em>ca</em> and then applied with <em>cb</em>
   * @return A new function after performing the composition, then application.
   */
  public static <A, B, C, D> F<D, C> bind(final F<D, A> ca, final F<D, B> cb, final F<A, F<B, C>> f) {
    return apply(compose(f, ca), cb);
  }

  /**
   * Applies a given function over the arguments of another function of arity-2.
   *
   * @param a The function whose arguments to apply another function over.
   * @param f The function to apply over the arguments of another function.
   * @return A function whose arguments are fed through function f, before being passed to function a.
   */
  public static <A, B, C> F<B, F<B, C>> on(final F<A, F<A, C>> a, final F<B, A> f) {
    return compose(compose(Function.<B, A, C>andThen().f(f), a), f);
  }

  /**
   * Promotes a function of arity-2 to a higher-order function.
   *
   * @param f The function to promote.
   * @return A function of arity-2 promoted to compose with two functions.
   */
  public static <A, B, C, D> F<F<D, A>, F<F<D, B>, F<D, C>>> lift(final F<A, F<B, C>> f) {
    return curry(new F2<F<D, A>, F<D, B>, F<D, C>>() {
      public F<D, C> f(final F<D, A> ca, final F<D, B> cb) {
        return bind(ca, cb, f);
      }
    });
  }

  /**
   * Joins two arguments of a function of arity-2 into one argument, yielding a function of arity-1.
   *
   * @param f A function whose arguments to join.
   * @return A function of arity-1 whose argument is substituted for both parameters of <em>f</em>.
   */
  public static <A, B> F<B, A> join(final F<B, F<B, A>> f) {
    return bind(f, Function.<F<B, A>>identity());
  }


  /**
   * Partial application of the second argument to the supplied function to get a function of type
   * <tt>A -> C</tt>. Same as <tt>flip(f).f(b)</tt>.
   *
   * @param f The function to partially apply.
   * @param b The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its second argument applied.
   */
  public static <A, B, C> F<A, C> partialApply2(final F<A, F<B, C>> f, final B b) {
    return new F<A, C>() {
      public C f(final A a) {
        return uncurryF2(f).f(a, b);
      }
    };
  }

  /**
   * Partial application of the third argument to the supplied function to get a function of type
   * <tt>A -> B -> D</tt>.
   *
   * @param f The function to partially apply.
   * @param c The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its third argument applied.
   */
  public static <A, B, C, D> F<A, F<B, D>> partialApply3(final F<A, F<B, F<C, D>>> f, final C c) {
    return new F<A, F<B, D>>() {
      public F<B, D> f(final A a) {
        return new F<B, D>() {
          public D f(final B b) {
            return uncurryF3(f).f(a, b, c);
          }
        };
      }
    };
  }

  /**
   * Partial application of the fourth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> E</tt>.
   *
   * @param f The function to partially apply.
   * @param d The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its fourth argument applied.
   */
  public static <A, B, C, D, E> F<A, F<B, F<C, E>>> partialApply4(final F<A, F<B, F<C, F<D, E>>>> f, final D d) {
    return new F<A, F<B, F<C, E>>>() {
      public F<B, F<C, E>> f(final A a) {
        return new F<B, F<C, E>>() {
          public F<C, E> f(final B b) {
            return new F<C, E>() {
              public E f(final C c) {
                return uncurryF4(f).f(a, b, c, d);
              }
            };
          }
        };
      }
    };
  }

  /**
   * Partial application of the fifth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> F$</tt>.
   *
   * @param f The function to partially apply.
   * @param e The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its fifth argument applied.
   */
  public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F$>>>> partialApply5(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f,
                                                                             final E e) {
    return new F<A, F<B, F<C, F<D, F$>>>>() {
      public F<B, F<C, F<D, F$>>> f(final A a) {
        return new F<B, F<C, F<D, F$>>>() {
          public F<C, F<D, F$>> f(final B b) {
            return new F<C, F<D, F$>>() {
              public F<D, F$> f(final C c) {
                return new F<D, F$>() {
                  public F$ f(final D d) {
                    return uncurryF5(f).f(a, b, c, d, e);
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Partial application of the sixth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> G</tt>.
   *
   * @param f  The function to partially apply.
   * @param f$ The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its sixth argument applied.
   */
  public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, G>>>>> partialApply6(
      final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f, final F$ f$) {
    return new F<A, F<B, F<C, F<D, F<E, G>>>>>() {
      public F<B, F<C, F<D, F<E, G>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, G>>>>() {
          public F<C, F<D, F<E, G>>> f(final B b) {
            return new F<C, F<D, F<E, G>>>() {
              public F<D, F<E, G>> f(final C c) {
                return new F<D, F<E, G>>() {
                  public F<E, G> f(final D d) {
                    return new F<E, G>() {
                      public G f(final E e) {
                        return uncurryF6(f).f(a, b, c, d, e, f$);
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Partial application of the seventh argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> F$ -> H</tt>.
   *
   * @param f The function to partially apply.
   * @param g The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its seventh argument applied.
   */
  public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, H>>>>>> partialApply7(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f, final G g) {
    return new F<A, F<B, F<C, F<D, F<E, F<F$, H>>>>>>() {
      public F<B, F<C, F<D, F<E, F<F$, H>>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F<F$, H>>>>>() {
          public F<C, F<D, F<E, F<F$, H>>>> f(final B b) {
            return new F<C, F<D, F<E, F<F$, H>>>>() {
              public F<D, F<E, F<F$, H>>> f(final C c) {
                return new F<D, F<E, F<F$, H>>>() {
                  public F<E, F<F$, H>> f(final D d) {
                    return new F<E, F<F$, H>>() {
                      public F<F$, H> f(final E e) {
                        return new F<F$, H>() {
                          public H f(final F$ f$) {
                            return uncurryF7(f).f(a, b, c, d, e, f$, g);
                          }
                        };
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }

  /**
   * Partial application of the eigth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> F$ -> G -> I</tt>.
   *
   * @param f The function to partially apply.
   * @param h The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its eigth argument applied.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, I>>>>>>> partialApply8(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f, final H h) {
    return new F<A, F<B, F<C, F<D, F<E, F<F$, F<G, I>>>>>>>() {
      public F<B, F<C, F<D, F<E, F<F$, F<G, I>>>>>> f(final A a) {
        return new F<B, F<C, F<D, F<E, F<F$, F<G, I>>>>>>() {
          public F<C, F<D, F<E, F<F$, F<G, I>>>>> f(final B b) {
            return new F<C, F<D, F<E, F<F$, F<G, I>>>>>() {
              public F<D, F<E, F<F$, F<G, I>>>> f(final C c) {
                return new F<D, F<E, F<F$, F<G, I>>>>() {
                  public F<E, F<F$, F<G, I>>> f(final D d) {
                    return new F<E, F<F$, F<G, I>>>() {
                      public F<F$, F<G, I>> f(final E e) {
                        return new F<F$, F<G, I>>() {
                          public F<G, I> f(final F$ f$) {
                            return new F<G, I>() {
                              public I f(final G g) {
                                return uncurryF8(f).f(a, b, c, d, e, f$, g, h);
                              }
                            };
                          }
                        };
                      }
                    };
                  }
                };
              }
            };
          }
        };
      }
    };
  }
}
