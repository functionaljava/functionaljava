package fj.control;

import fj.F;
import fj.P1;
import fj.data.Either;

import static fj.data.Either.left;
import static fj.data.Either.right;

/**
 * A Trampoline is a potentially branching computation that can be stepped through and executed in constant stack.
 */
public abstract class Trampoline<A> {
  private static abstract class Normal<A> extends Trampoline<A> {
    public abstract <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k);

    public <B> Trampoline<B> bind(final F<A, Trampoline<B>> f) {
      return codense(this, f);
    }
  }

  private static final class Codense<A> extends Trampoline<A> {
    private final Normal<Object> sub;
    private final F<Object, Trampoline<A>> cont;

    private Codense(final Normal<Object> t, final F<Object, Trampoline<A>> k) {
      sub = t;
      cont = k;
    }

    public <R> R fold(final F<Normal<A>, R> n,
                      final F<Codense<A>, R> gs) {
      return gs.f(this);
    }

    public <B> Trampoline<B> bind(final F<A, Trampoline<B>> f) {
      return codense(sub, new F<Object, Trampoline<B>>() {
        public Trampoline<B> f(final Object o) {
          return suspend(new P1<Trampoline<B>>() {
            public Trampoline<B> _1() {
              return cont.f(o).bind(f);
            }
          });
        }
      });
    }

    public Either<P1<Trampoline<A>>, A> resume() {
      return left(sub.resume().either(new F<P1<Trampoline<Object>>, P1<Trampoline<A>>>() {
            public P1<Trampoline<A>> f(final P1<Trampoline<Object>> p) {
              return p.map(new F<Trampoline<Object>, Trampoline<A>>() {
                public Trampoline<A> f(final Trampoline<Object> ot) {
                  return ot.fold(new F<Normal<Object>, Trampoline<A>>() {
                        public Trampoline<A> f(final Normal<Object> o) {
                          return o.foldNormal(new F<Object, Trampoline<A>>() {
                                public Trampoline<A> f(final Object o) {
                                  return cont.f(o);
                                }
                              }, new F<P1<Trampoline<Object>>, Trampoline<A>>() {
                            public Trampoline<A> f(final P1<Trampoline<Object>> t) {
                              return t._1().bind(cont);
                            }
                          }
                          );
                        }
                      }, new F<Codense<Object>, Trampoline<A>>() {
                    public Trampoline<A> f(final Codense<Object> c) {
                      return codense(c.sub, new F<Object, Trampoline<A>>() {
                        public Trampoline<A> f(final Object o) {
                          return c.cont.f(o).bind(cont);
                        }
                      });
                    }
                  }
                  );
                }
              });
            }
          }, new F<Object, P1<Trampoline<A>>>() {
        public P1<Trampoline<A>> f(final Object o) {
          return new P1<Trampoline<A>>() {
            public Trampoline<A> _1() {
              return cont.f(o);
            }
          };
        }
      }
      ));
    }
  }

  private static final class Suspend<A> extends Normal<A> {
    private final P1<Trampoline<A>> suspension;

    private Suspend(final P1<Trampoline<A>> s) {
      suspension = s;
    }

    public <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k) {
      return k.f(suspension);
    }

    public <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs) {
      return n.f(this);
    }

    public Either<P1<Trampoline<A>>, A> resume() {
      return left(suspension);
    }
  }

  private static final class Pure<A> extends Normal<A> {
    private final A value;

    private Pure(final A a) {
      value = a;
    }

    public <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k) {
      return pure.f(value);
    }

    public <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs) {
      return n.f(this);
    }

    public Either<P1<Trampoline<A>>, A> resume() {
      return right(value);
    }
  }

  @SuppressWarnings("unchecked")
  protected static <A, B> Codense<B> codense(final Normal<A> a, final F<A, Trampoline<B>> k) {
    return new Codense<B>((Normal<Object>) a, (F<Object, Trampoline<B>>) k);
  }

  /**
   * @return The first-class version of `pure`.
   */
  public static <A> F<A, Trampoline<A>> pure() {
    return new F<A, Trampoline<A>>() {
      public Trampoline<A> f(final A a) {
        return pure(a);
      }
    };
  }

  /**
   * Constructs a leaf computation that results in the given value.
   * @param a The value of the result.
   * @return A trampoline that results in the given value.
   */
  public static <A> Trampoline<A> pure(final A a) {
    return new Pure<A>(a);
  }

  /**
   * Suspends the given computation in a thunk.
   * @param a A trampoline suspended in a thunk.
   * @return A trampoline whose next step runs the given thunk.
   */
  public static <A> Trampoline<A> suspend(final P1<Trampoline<A>> a) {
    return new Suspend<A>(a);
  }

  /**
   * @return The first-class version of `suspend`.
   */
  public static <A> F<P1<Trampoline<A>>, Trampoline<A>> suspend_() {
    return new F<P1<Trampoline<A>>, Trampoline<A>>() {
      public Trampoline<A> f(final P1<Trampoline<A>> trampolineP1) {
        return suspend(trampolineP1);
      }
    };
  }

  protected abstract <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs);

  /**
   * Binds the given continuation to the result of this trampoline.
   * @param f A function that constructs a trampoline from the result of this trampoline.
   * @return A new trampoline that runs this trampoline, then continues with the given function.
   */
  public abstract <B> Trampoline<B> bind(final F<A, Trampoline<B>> f);

  /**
   * Maps the given function across the result of this trampoline. Monadic bind.
   * @param f A function that gets applied to the result of this trampoline.
   * @return A new trampoline that runs this trampoline, then applies the given function to the result.
   */
  public final <B> Trampoline<B> map(final F<A, B> f) {
    return bind(Trampoline.<B>pure().o(f));
  }

  /**
   * @return The first-class version of `bind`.
   */
  public static <A, B> F<F<A, Trampoline<B>>, F<Trampoline<A>, Trampoline<B>>> bind_() {
    return new F<F<A, Trampoline<B>>, F<Trampoline<A>, Trampoline<B>>>() {
      public F<Trampoline<A>, Trampoline<B>> f(final F<A, Trampoline<B>> f) {
        return new F<Trampoline<A>, Trampoline<B>>() {
          public Trampoline<B> f(final Trampoline<A> a) {
            return a.bind(f);
          }
        };
      }
    };
  }

  /**
   * @return The first-class version of `map`.
   */
  public static <A, B> F<F<A, B>, F<Trampoline<A>, Trampoline<B>>> map_() {
    return new F<F<A, B>, F<Trampoline<A>, Trampoline<B>>>() {
      public F<Trampoline<A>, Trampoline<B>> f(final F<A, B> f) {
        return new F<Trampoline<A>, Trampoline<B>>() {
          public Trampoline<B> f(final Trampoline<A> a) {
            return a.map(f);
          }
        };
      }
    };
  }

  /**
   * @return The first-class version of `resume`.
   */
  public static <A> F<Trampoline<A>, Either<P1<Trampoline<A>>, A>> resume_() {
    return new F<Trampoline<A>, Either<P1<Trampoline<A>>, A>>() {
      public Either<P1<Trampoline<A>>, A> f(final Trampoline<A> aTrampoline) {
        return aTrampoline.resume();
      }
    };
  }

  /**
   * Runs a single step of this computation.
   * @return The next step of this compuation.
   */
  public abstract Either<P1<Trampoline<A>>, A> resume();

  /**
   * Runs this computation all the way to the end, in constant stack.
   * @return The end result of this computation.
   */
  @SuppressWarnings("LoopStatementThatDoesntLoop")
  public A run() {
    Trampoline<A> current = this;
    while (true) {
      final Either<P1<Trampoline<A>>, A> x = current.resume();
      for (final P1<Trampoline<A>> t: x.left()) {
        current = t._1();
      }
      for (final A a: x.right()) {
        return a;
      }
    }
  }
}
