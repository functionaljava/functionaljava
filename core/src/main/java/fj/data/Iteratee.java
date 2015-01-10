package fj.data;

import fj.F;
import fj.F1Functions;
import fj.Function;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;

/**
 * 
 */
public final class Iteratee {

  /** The input to an iteratee. */
  public static abstract class Input<E> {

    Input() {} // sealed

    public abstract <Z> Z apply(final P1<Z> empty, final P1<F<E, Z>> el, final P1<Z> eof);

    /** Input that has no values available */
    public static final <E> Input<E> empty() {
      return new Input<E>() {
        @Override
        public <Z> Z apply(final P1<Z> empty, final P1<F<E, Z>> el, final P1<Z> eof) {
          return empty._1();
        }
      };
    }

    /** Input that is exhausted */
    public static final <E> Input<E> eof() {
      return new Input<E>() {
        @Override
        public <Z> Z apply(final P1<Z> empty, final P1<F<E, Z>> el, final P1<Z> eof) {
          return eof._1();
        }
      };
    }

    /** Input that has a value available */
    public static final <E> Input<E> el(final E element) {
      return new Input<E>() {
        @Override
        public <Z> Z apply(final P1<Z> empty, final P1<F<E, Z>> el, final P1<Z> eof) {
          return el._1().f(element);
        }
      };
    }
  }

  /** A pure iteratee computation which is either done or needs more input */
  public static abstract class IterV<E, A> {

    IterV() {} // sealed

    /** A computation that takes an element from an input to yield a new computation */
    public static <E, A> IterV<E, A> cont(final F<Input<E>, IterV<E, A>> f) {
      return new IterV<E, A>() {
        @Override
        public <Z> Z fold(final F<P2<A, Input<E>>, Z> done, final F<F<Input<E>, IterV<E, A>>, Z> cont) {
          return cont.f(f);
        }
      };
    }

    public abstract <Z> Z fold(final F<P2<A, Input<E>>, Z> done, final F<F<Input<E>, IterV<E, A>>, Z> cont);

    /** A computation that has finished */
    public static <E, A> IterV<E, A> done(final A a, final Input<E> i) {
      final P2<A, Input<E>> p = P.p(a, i);
      return new IterV<E, A>() {
        @Override
        public <Z> Z fold(final F<P2<A, Input<E>>, Z> done, final F<F<Input<E>, IterV<E, A>>, Z> cont) {
          return done.f(p);
        }
      };
    }

    public final A run() {
      final F<IterV<E, A>, Option<A>> runCont =
        new F<IterV<E, A>, Option<A>>() {
          final F<P2<A, Input<E>>, Option<A>> done = F1Functions.andThen(P2.<A, Input<E>>__1(), Option.<A>some_());
          final F<F<Input<E>, IterV<E, A>>, Option<A>> cont = Function.constant(Option.<A>none());

          @Override
          public Option<A> f(final IterV<E, A> i) {
            return i.fold(done, cont);
          }
        };
      final F<P2<A, Input<E>>, A> done = P2.<A, Input<E>>__1();
      final F<F<Input<E>, IterV<E, A>>, A> cont =
              k -> runCont.f(k.f(Input.<E>eof())).valueE("diverging iteratee");
      return fold(done, cont);
    }

    /** TODO more documentation */
    public final <B> IterV<E, B> bind(final F<A, IterV<E, B>> f) {
      final F<P2<A, Input<E>>, IterV<E, B>> done =
        new F<P2<A, Input<E>>, IterV<E, B>>() {
          @Override
          public IterV<E, B> f(final P2<A, Input<E>> xe) {
            final Input<E> e = xe._2();
            final F<P2<B, Input<E>>, IterV<E, B>> done =
                    y_ -> {
                      final B y = y_._1();
                      return done(y, e);
                    };
            final F<F<Input<E>, IterV<E, B>>, IterV<E, B>> cont =
                    k -> k.f(e);
            final A x = xe._1();
            return f.f(x).fold(done, cont);
          }
        };
      final F<F<Input<E>, IterV<E, A>>, IterV<E, B>> cont =
              k -> cont(e -> k.f(e).bind(f));
      return this.fold(done, cont);
    }

    /** An iteratee that counts and consumes the elements of the input */
    public static final <E> IterV<E, Integer> length() {
      final F<Integer, F<Input<E>, IterV<E, Integer>>> step =
        new F<Integer, F<Input<E>, IterV<E, Integer>>>() {
          final F<Integer, F<Input<E>, IterV<E, Integer>>> step = this;

          @Override
          public F<Input<E>, IterV<E, Integer>> f(final Integer acc) {
            final P1<IterV<E, Integer>> empty =
              new P1<IterV<E, Integer>>() {
                @Override
                public IterV<E, Integer> _1() {
                  return cont(step.f(acc));
                }
              };
            final P1<F<E, IterV<E, Integer>>> el =
              new P1<F<E, IterV<E, Integer>>>() {
                @Override
                public F<E, IterV<E, Integer>> _1() {
                  return P.p(cont(step.f(acc + 1))).constant();
                }
              };
            final P1<IterV<E, Integer>> eof =
              new P1<IterV<E, Integer>>() {
                @Override
                public IterV<E, Integer> _1() {
                  return done(acc, Input.<E>eof());
                }
              };
            return s -> s.apply(empty, el, eof);
          }
        };
      return cont(step.f(0));
    }

    /** An iteratee that skips the first n elements of the input */
    public static final <E> IterV<E, Unit> drop(final int n) {
      final F<Input<E>, IterV<E, Unit>> step =
        new F<Input<E>, IterV<E, Unit>>() {
          final F<Input<E>, IterV<E, Unit>> step = this;

          final P1<IterV<E, Unit>> empty =
            new P1<IterV<E, Unit>>() {
              @Override
              public IterV<E, Unit> _1() {
                return cont(step);
              }
            };
          final P1<F<E, IterV<E, Unit>>> el =
            new P1<F<E, IterV<E, Unit>>>() {
              @Override
              public F<E, IterV<E, Unit>> _1() {
                return P.p(IterV.<E>drop(n - 1)).constant();
              }
            };
          final P1<IterV<E, Unit>> eof =
            new P1<IterV<E, Unit>>() {
              @Override
              public IterV<E, Unit> _1() {
                return done(Unit.unit(), Input.<E>eof());
              }
            };

          @Override
          public IterV<E, Unit> f(final Input<E> s) {
            return s.apply(empty, el, eof);
          }
        };
      return n == 0
        ? done(Unit.unit(), Input.<E>empty())
        : cont(step);
    }

    /** An iteratee that consumes the head of the input */
    public static final <E> IterV<E, Option<E>> head() {
      final F<Input<E>, IterV<E, Option<E>>> step =
        new F<Input<E>, IterV<E, Option<E>>>() {
          final F<Input<E>, IterV<E, Option<E>>> step = this;

          final P1<IterV<E, Option<E>>> empty =
            new P1<IterV<E, Option<E>>>() {
              @Override
              public IterV<E, Option<E>> _1() {
                return cont(step);
              }
            };
          final P1<F<E, IterV<E, Option<E>>>> el =
            new P1<F<E, IterV<E, Option<E>>>>() {
              @Override
              public F<E, IterV<E, Option<E>>> _1() {
                return e -> done(Option.<E>some(e), Input.<E>empty());
              }
            };
          final P1<IterV<E, Option<E>>> eof =
            new P1<IterV<E, Option<E>>>() {
              @Override
              public IterV<E, Option<E>> _1() {
                return done(Option.<E>none(), Input.<E>eof());
              }
            };

          @Override
          public IterV<E, Option<E>> f(final Input<E> s) {
            return s.apply(empty, el, eof);
          }
        };
      return cont(step);
    }

    /** An iteratee that returns the first element of the input */
    public static final <E> IterV<E, Option<E>> peek() {
      final F<Input<E>, IterV<E, Option<E>>> step =
        new F<Input<E>, IterV<E, Option<E>>>() {
          final F<Input<E>, IterV<E, Option<E>>> step = this;

          final P1<IterV<E, Option<E>>> empty =
            new P1<IterV<E, Option<E>>>() {
              @Override
              public IterV<E, Option<E>> _1() {
                return cont(step);
              }
            };
          final P1<F<E, IterV<E, Option<E>>>> el =
            new P1<F<E, IterV<E, Option<E>>>>() {
              @Override
              public F<E, IterV<E, Option<E>>> _1() {
                return e -> done(Option.<E>some(e), Input.<E>el(e));
              }
            };
          final P1<IterV<E, Option<E>>> eof =
            new P1<IterV<E, Option<E>>>() {
              @Override
              public IterV<E, Option<E>> _1() {
                return done(Option.<E>none(), Input.<E>eof());
              }
            };

          @Override
          public IterV<E, Option<E>> f(final Input<E> s) {
            return s.apply(empty, el, eof);
          }
        };
      return cont(step);
    }

    /** An iteratee that consumes the input elements and returns them as a list in reverse order,
     * so that the last line is the first element. This allows to build a list from 2 iteratees. */
    public static final <E> IterV<E, List<E>> list() {
        final F<List<E>, F<Input<E>, IterV<E, List<E>>>> step =
          new F<List<E>, F<Input<E>, IterV<E, List<E>>>>() {
            final F<List<E>, F<Input<E>, IterV<E, List<E>>>> step = this;

            @Override
            public F<Input<E>, IterV<E, List<E>>> f(final List<E> acc) {
              final P1<IterV<E, List<E>>> empty =
                new P1<IterV<E, List<E>>>() {
                  @Override
                  public IterV<E, List<E>> _1() {
                    return cont(step.f(acc));
                  }
                };
              final P1<F<E, IterV<E, List<E>>>> el =
                new P1<F<E, IterV<E, List<E>>>>() {
                  @Override
                  public F<E, IterV<E, List<E>>> _1() {
                    return e -> cont(step.f(acc.cons(e)));
                  }
                };
              final P1<IterV<E, List<E>>> eof =
                new P1<IterV<E, List<E>>>() {
                  @Override
                  public IterV<E, List<E>> _1() {
                    return done(acc, Input.<E>eof());
                  }
                };
              return s -> s.apply(empty, el, eof);
            }
          };
        return cont(step.f(List.<E> nil()));
    }
  }

  private Iteratee() {
    throw new UnsupportedOperationException();
  }

}
