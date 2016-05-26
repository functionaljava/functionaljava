package fj.test;

import static fj.Function.curry;
import static fj.Function.compose2;
import static fj.P.p;

import fj.*;

import static fj.P2.__2;
import fj.data.List;
import fj.data.Option;
import static fj.data.Option.none;
import fj.data.Stream;
import static fj.test.Arg.arg;
import static fj.test.CheckResult.exhausted;
import static fj.test.CheckResult.falsified;
import static fj.test.CheckResult.genException;
import static fj.test.CheckResult.passed;
import static fj.test.CheckResult.propException;
import static fj.test.CheckResult.proven;
import static fj.test.Result.noResult;
import static java.lang.Math.round;

/**
 * Represents an algebraic property about a program that may be {@link #check(Rand, int, int, int,
 * int) checked} for its truth value. For example, it is true that "for all integers (call it x) and
 * for all integers (call it y), then x + y is equivalent to y + x". This statement is a (algebraic)
 * property, proposition or theorem that, when checked, will at least (depending on arguments) fail
 * to be falsified &mdash; since there does not exist a counter-example to this statement.
 *
 * @version %build.number%
 */
public final class Property {
  private final F<Integer, F<Rand, Result>> f;

  private Property(final F<Integer, F<Rand, Result>> f) {
    this.f = f;
  }

  /**
   * Returns the result of applying the given size and random generator.
   *
   * @param i The size to use to obtain a result.
   * @param r The random generator to use to obtain a result.
   * @return The result of applying the given size and random generator.
   */
  public Result prop(final int i, final Rand r) {
    return f.f(i).f(r);
  }

  /**
   * Returns a generator of results from this property.
   *
   * @return A generator of results from this property.
   */
  public Gen<Result> gen() {
    return Gen.gen(i -> r -> f.f(i).f(r));
  }

  /**
   * Performs a conjunction of this property with the given property.
   *
   * @param p The property to perform the conjunction with.
   * @return A conjunction of this property with the given property.
   */
  public Property and(final Property p) {
    return fromGen(gen().bind(p.gen(), res1 -> res2 -> res1.isException() || res1.isFalsified() ? res1 : res2.isException() || res2.isFalsified() ? res2 : res1.isProven() || res1.isUnfalsified() ? res2 : res2.isProven() || res2.isUnfalsified() ? res1 : noResult()));
  }

  /**
   * Performs a disjunction of this property with the given property.
   *
   * @param p The property to perform the disjunction with.
   * @return A disjunction of this property with the given property.
   */
  public Property or(final Property p) {
    return fromGen(gen().bind(p.gen(), res1 -> res2 -> res1.isException() || res1.isFalsified() ? res1 : res2.isException() || res2.isFalsified() ? res2 : res1.isProven() || res1.isUnfalsified() ? res1 : res2.isProven() || res2.isUnfalsified() ? res2 : noResult()));
  }

  /**
   * Performs a sequence of this property with the given property. The returned property holds if
   * and only if this property and the given property also hold. If one property does not hold, but
   * the other does, then the returned property will produce the same result and the property that
   * holds.
   *
   * @param p The property to sequence this property with.
   * @return A sequence of this property with the given property.
   */
  public Property sequence(final Property p) {
    return fromGen(gen().bind(p.gen(), res1 -> res2 -> res1.isException() || res1.isProven() || res1.isUnfalsified() ? res1 : res2.isException() || res2.isProven() || res2.isUnfalsified() ? res2 : res1.isFalsified() ? res2 : res2.isFalsified() ? res1 : noResult()));
  }

  /**
   * Checks this property using the given arguments and produces a result.
   *
   * @param r             The random generator to use for checking.
   * @param minSuccessful The minimum number of successful tests before a result is reached.
   * @param maxDiscarded  The maximum number of tests discarded because they did not satisfy
   *                      pre-conditions (i.e. {@link #implies(boolean, F0)}).
   * @param minSize       The minimum size to use for checking.
   * @param maxSize       The maximum size to use for checking.
   * @return A result after checking this property.
   */
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  public CheckResult check(final Rand r,
                           final int minSuccessful,
                           final int maxDiscarded,
                           final int minSize,
                           final int maxSize) {
    int s = 0;
    int d = 0;
    float sz = minSize;
    CheckResult res;

    while (true) {
      final float size = s == 0 && d == 0 ? minSize : sz + (maxSize - sz) / (minSuccessful - s);
      try {
        final Result x = f.f(round(size)).f(r);
        if (x.isNoResult())
          if (d + 1 >= maxDiscarded) {
            res = exhausted(s, d + 1);
            break;
          } else {
            sz = size;
            d++;
          }
        else if (x.isProven()) {
          res = proven(x.args().some(), s + 1, d);
          break;
        } else if (x.isUnfalsified())
          if (s + 1 >= minSuccessful) {
            res = passed(s + 1, d);
            break;
          } else {
            sz = size;
            s++;
          }
        else if (x.isFalsified()) {
          res = falsified(x.args().some(), s, d);
          break;
        } else if (x.isException()) {
          res = propException(x.args().some(), x.exception().some(), s, d);
          break;
        }
      } catch (final Throwable t) {
        res = genException(t, s, d);
        break;
      }
    }

    return res;
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator} and the given
   * arguments to produce a result.
   *
   * @param minSuccessful The minimum number of successful tests before a result is reached.
   * @param maxDiscarded  The maximum number of tests discarded because they did not satisfy
   *                      pre-conditions (i.e. {@link #implies(boolean, F0)}).
   * @param minSize       The minimum size to use for checking.
   * @param maxSize       The maximum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult check(final int minSuccessful,
                           final int maxDiscarded,
                           final int minSize,
                           final int maxSize) {
    return check(Rand.standard, minSuccessful, maxDiscarded, minSize, maxSize);
  }

  /**
   * Checks this property using the given random generator, 100 minimum successful checks, 500
   * maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @param r The random generator.
   * @return A result after checking this property.
   */
  public CheckResult check(final Rand r) {
    return check(r, 100, 500, 0, 100);
  }

  /**
   * Checks this property using the given random generator, 100 minimum successful checks, 500
   * maximum discarded tests, the given minimum size and the given maximum size.
   *
   * @param r       The random generator.
   * @param minSize The minimum size to use for checking.
   * @param maxSize The maximum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult check(final Rand r, final int minSize, final int maxSize) {
    return check(r, 100, 500, minSize, maxSize);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, 100 minimum
   * successful checks, 500 maximum discarded tests and the given arguments to produce a result.
   *
   * @param minSize The minimum size to use for checking.
   * @param maxSize The maximum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult check(final int minSize,
                           final int maxSize) {
    return check(100, 500, minSize, maxSize);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, 100 minimum
   * successful checks, 500 maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @return A result after checking this property.
   */
  public CheckResult check() {
    return check(0, 100);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, the given minimum
   * successful checks, 500 maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @param minSuccessful The minimum number of successful tests before a result is reached.
   * @return A result after checking this property.
   */
  public CheckResult minSuccessful(final int minSuccessful) {
    return check(minSuccessful, 500, 0, 100);
  }

  /**
   * Checks this property using the given random generator, the given minimum
   * successful checks, 500 maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @param r             The random generator.
   * @param minSuccessful The minimum number of successful tests before a result is reached.
   * @return A result after checking this property.
   */
  public CheckResult minSuccessful(final Rand r, final int minSuccessful) {
    return check(r, minSuccessful, 500, 0, 100);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, 100 minimum
   * successful checks, the given maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @param maxDiscarded The maximum number of tests discarded because they did not satisfy
   *                     pre-conditions (i.e. {@link #implies(boolean, F0)}).
   * @return A result after checking this property.
   */
  public CheckResult maxDiscarded(final int maxDiscarded) {
    return check(100, maxDiscarded, 0, 100);
  }

  /**
   * Checks this property using a the given random generator}, 100 minimum
   * successful checks, the given maximum discarded tests, minimum size of 0, maximum size of 100.
   *
   * @param r            The random generator.
   * @param maxDiscarded The maximum number of tests discarded because they did not satisfy
   *                     pre-conditions (i.e. {@link #implies(boolean, F0)}).
   * @return A result after checking this property.
   */
  public CheckResult maxDiscarded(final Rand r, final int maxDiscarded) {
    return check(r, 100, maxDiscarded, 0, 100);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, 100 minimum
   * successful checks, 500 maximum discarded tests, the given minimum size, maximum size of 100.
   *
   * @param minSize The minimum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult minSize(final int minSize) {
    return check(100, 500, minSize, 100);
  }

  /**
   * Checks this property using the given random generator, 100 minimum
   * successful checks, 500 maximum discarded tests, the given minimum size, maximum size of 100.
   *
   * @param r       The random generator.
   * @param minSize The minimum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult minSize(final Rand r, final int minSize) {
    return check(r, 100, 500, minSize, 100);
  }

  /**
   * Checks this property using a {@link Rand#standard standard random generator}, 100 minimum
   * successful checks, 500 maximum discarded tests, minimum size of 0, the given maximum size.
   *
   * @param maxSize The maximum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult maxSize(final int maxSize) {
    return check(100, 500, 0, maxSize);
  }

  /**
   * Checks this property using the given random generator, 100 minimum
   * successful checks, 500 maximum discarded tests, minimum size of 0, the given maximum size.
   *
   * @param r       The random generator.
   * @param maxSize The maximum size to use for checking.
   * @return A result after checking this property.
   */
  public CheckResult maxSize(final Rand r, final int maxSize) {
    return check(r, 100, 500, 0, maxSize);
  }

  /**
   * Returns a property that produces a result only if the given condition satisfies. The result
   * will be taken from the given property.
   *
   * @param b The condition that, if satisfied, produces the given property.
   * @param p The property to return if the condition satisfies.
   * @return A property that produces a result only if the given condition satisfies.
   */
  public static Property implies(final boolean b, final F0<Property> p) {
    return b ? p.f() : new Property(i -> r -> noResult());
  }

    /**
     * Returns a property that produces a result only if the given condition satisfies. The result
     * will be taken from the given boolean b.
     */
    public static Property impliesBoolean(final boolean a, final boolean b) {
        return implies(a, () -> prop(b));
    }

    /**
     * Returns a property that produces a result only if the given condition satisfies. The result
     * will be taken from the given lazy boolean b.
     */
    public static Property impliesBoolean(final boolean a, final F0<Boolean> b) {
        return implies(a, () -> prop(b.f()));
    }

  /**
   * Returns a property from the given function.
   *
   * @param f The function to construct the returned property with.
   * @return A property from the given function.
   */
  public static Property prop(final F<Integer, F<Rand, Result>> f) {
    return new Property(f);
  }

  /**
   * Returns a property that always has the given result.
   *
   * @param r The result of the returned property.
   * @return A property that always has the given result.
   */
  public static Property prop(final Result r) {
    return new Property(integer -> x -> r);
  }

  /**
   * Returns a property that is either proven (the given condition satsifies) or falsified
   * otherwise.
   *
   * @param b The condition that, if satisfied, returns a property that is proven; otherwise, the
   *          property is falsified.
   * @return A property that is either proven (the given condition satsifies) or falsified
   *         otherwise.
   */
  public static Property prop(final boolean b) {
    return b ? prop(Result.proven(List.nil())) : prop(Result.falsified(List.nil()));
  }

  /**
   * Constructs a property from a generator of results.
   *
   * @param g The generator of results to constructor a property with.
   * @return A property from a generator of results.
   */
  public static Property fromGen(final Gen<Result> g) {
    return prop(i -> r -> g.gen(i, r));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param g      The generator to produces values from to produce the property with.
   * @param shrink The shrink strategy to use upon falsification.
   * @param f      The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A> Property forall(final Gen<A> g, final Shrink<A> shrink, final F<A, P1<Property>> f) {
    return prop(i -> r -> {
      final class Util {
        @SuppressWarnings("IfMayBeConditional")
        Option<P2<A, Result>> first(final Stream<A> as, final int shrinks) {
          final Stream<Option<P2<A, Result>>> results = as.map(a -> {
            final Result result = exception(f.f(a)).prop(i, r);

            return result.toOption().map(result1 -> p(a, result1.provenAsUnfalsified().addArg(arg(a, shrinks))));
          });

          if (results.isEmpty())
            return none();
          else return results.find(this::failed).orSome(results::head);
        }

        public boolean failed(final Option<P2<A, Result>> o) {
          return o.isSome() && o.some()._2().failed();
        }
      }

      final Util u = new Util();

      Option<P2<A, Result>> x = u.first(Stream.single(g.gen(i, r)), 0);
      final F<P2<A, Result>, Result> __2 = __2();
      if (u.failed(x)) {
        Option<Result> or;
        int shrinks = 0;

        do {
          shrinks++;
          or = x.map(__2);
          x = u.first(shrink.shrink(x.some()._1()), shrinks);
        }
        while (u.failed(x));

        return noResult(or);
      } else
        return noResult(x.map(__2));
    });
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A> Property propertyP(final Arbitrary<A> aa, final Shrink<A> sa, final F<A, P1<Property>> f) {
    return forall(aa.gen, sa, f);
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A> Property property(final Arbitrary<A> aa, final Shrink<A> sa, final F<A, Property> f) {
    return propertyP(aa, sa, P1.curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A> Property propertyP(final Arbitrary<A> aa, final F<A, P1<Property>> f) {
    return propertyP(aa, Shrink.empty(), f);
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A> Property property(final Arbitrary<A> aa, final F<A, Property> f) {
    return propertyP(aa, P1.curry(f));
  }


  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property propertyP(final Arbitrary<A> aa, final Arbitrary<B> ab, final Shrink<A> sa, final Shrink<B> sb, final F<A, F<B, P1<Property>>> f) {
    return property(aa, sa, a -> {
      return propertyP(ab, sb, b -> {
        return f.f(a).f(b);
      });
    });
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property property(final Arbitrary<A> aa, final Arbitrary<B> ab, final Shrink<A> sa, final Shrink<B> sb, final F<A, F<B, Property>> f) {
    return propertyP(aa, ab, sa, sb, compose2(P.p1(), f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property propertyP(final Arbitrary<A> aa, final Arbitrary<B> ab, final F<A, F<B, P1<Property>>> f) {
    return property(aa, a -> propertyP(ab, b -> f.f(a).f(b)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property property(final Arbitrary<A> aa, final Arbitrary<B> ab, final F<A, F<B, Property>> f) {
    return propertyP(aa, ab, compose2(P.p1(), f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property propertyP(final Arbitrary<A> aa, final Arbitrary<B> ab, final Shrink<A> sa, final Shrink<B> sb, final F2<A, B, P1<Property>> f) {
    return propertyP(aa, ab, sa, sb, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property property(final Arbitrary<A> aa, final Arbitrary<B> ab, final Shrink<A> sa, final Shrink<B> sb, final F2<A, B, Property> f) {
    return propertyP(aa, ab, sa, sb, compose2(P.p1(), curry(f)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property propertyP(final Arbitrary<A> aa, final Arbitrary<B> ab, final F2<A, B, P1<Property>> f) {
    return propertyP(aa, ab, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B> Property property(final Arbitrary<A> aa, final Arbitrary<B> ab, final F2<A, B, Property> f) {
    return propertyP(aa, ab, compose2(P.p1(), curry(f)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C> Property property(final Arbitrary<A> aa,
                                            final Arbitrary<B> ab,
                                            final Arbitrary<C> ac,
                                            final Shrink<A> sa,
                                            final Shrink<B> sb,
                                            final Shrink<C> sc,
                                            final F<A, F<B, F<C, Property>>> f) {
    return property(aa, ab, sa, sb, a -> b -> property(ac, sc, c -> {
      return f.f(a).f(b).f(c);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C> Property property(final Arbitrary<A> aa,
                                            final Arbitrary<B> ab,
                                            final Arbitrary<C> ac,
                                            final F<A, F<B, F<C, Property>>> f) {
    return property(aa, ab, a -> b -> property(ac, c -> f.f(a).f(b).f(c)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C> Property property(final Arbitrary<A> aa,
                                            final Arbitrary<B> ab,
                                            final Arbitrary<C> ac,
                                            final Shrink<A> sa,
                                            final Shrink<B> sb,
                                            final Shrink<C> sc,
                                            final F3<A, B, C, Property> f) {
    return property(aa, ab, ac, sa, sb, sc, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C> Property property(final Arbitrary<A> aa,
                                            final Arbitrary<B> ab,
                                            final Arbitrary<C> ac,
                                            final F3<A, B, C, Property> f) {
    return property(aa, ab, ac, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D> Property property(final Arbitrary<A> aa,
                                               final Arbitrary<B> ab,
                                               final Arbitrary<C> ac,
                                               final Arbitrary<D> ad,
                                               final Shrink<A> sa,
                                               final Shrink<B> sb,
                                               final Shrink<C> sc,
                                               final Shrink<D> sd,
                                               final F<A, F<B, F<C, F<D, Property>>>> f) {
    return property(aa, ab, ac, sa, sb, sc, a -> b -> c -> property(ad, sd, d -> {
      return f.f(a).f(b).f(c).f(d);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D> Property property(final Arbitrary<A> aa,
                                               final Arbitrary<B> ab,
                                               final Arbitrary<C> ac,
                                               final Arbitrary<D> ad,
                                               final F<A, F<B, F<C, F<D, Property>>>> f) {
    return property(aa, ab, ac, a -> b -> c -> property(ad, d -> f.f(a).f(b).f(c).f(d)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D> Property property(final Arbitrary<A> aa,
                                               final Arbitrary<B> ab,
                                               final Arbitrary<C> ac,
                                               final Arbitrary<D> ad,
                                               final Shrink<A> sa,
                                               final Shrink<B> sb,
                                               final Shrink<C> sc,
                                               final Shrink<D> sd,
                                               final F4<A, B, C, D, Property> f) {
    return property(aa, ab, ac, ad, sa, sb, sc, sd, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D> Property property(final Arbitrary<A> aa,
                                               final Arbitrary<B> ab,
                                               final Arbitrary<C> ac,
                                               final Arbitrary<D> ad,
                                               final F4<A, B, C, D, Property> f) {
    return property(aa, ab, ac, ad, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E> Property property(final Arbitrary<A> aa,
                                                  final Arbitrary<B> ab,
                                                  final Arbitrary<C> ac,
                                                  final Arbitrary<D> ad,
                                                  final Arbitrary<E> ae,
                                                  final Shrink<A> sa,
                                                  final Shrink<B> sb,
                                                  final Shrink<C> sc,
                                                  final Shrink<D> sd,
                                                  final Shrink<E> se,
                                                  final F<A, F<B, F<C, F<D, F<E, Property>>>>> f) {
    return property(aa, ab, ac, ad, sa, sb, sc, sd, a -> b -> c -> d -> property(ae, se, e -> {
      return f.f(a).f(b).f(c).f(d).f(e);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E> Property property(final Arbitrary<A> aa,
                                                  final Arbitrary<B> ab,
                                                  final Arbitrary<C> ac,
                                                  final Arbitrary<D> ad,
                                                  final Arbitrary<E> ae,
                                                  final F<A, F<B, F<C, F<D, F<E, Property>>>>> f) {
    return property(aa, ab, ac, ad, a -> b -> c -> d -> property(ae, e -> f.f(a).f(b).f(c).f(d).f(e)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E> Property property(final Arbitrary<A> aa,
                                                  final Arbitrary<B> ab,
                                                  final Arbitrary<C> ac,
                                                  final Arbitrary<D> ad,
                                                  final Arbitrary<E> ae,
                                                  final Shrink<A> sa,
                                                  final Shrink<B> sb,
                                                  final Shrink<C> sc,
                                                  final Shrink<D> sd,
                                                  final Shrink<E> se,
                                                  final F5<A, B, C, D, E, Property> f) {
    return property(aa, ab, ac, ad, ae, sa, sb, sc, sd, se, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E> Property property(final Arbitrary<A> aa,
                                                  final Arbitrary<B> ab,
                                                  final Arbitrary<C> ac,
                                                  final Arbitrary<D> ad,
                                                  final Arbitrary<E> ae,
                                                  final F5<A, B, C, D, E, Property> f) {
    return property(aa, ab, ac, ad, ae, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$> Property property(final Arbitrary<A> aa,
                                                      final Arbitrary<B> ab,
                                                      final Arbitrary<C> ac,
                                                      final Arbitrary<D> ad,
                                                      final Arbitrary<E> ae,
                                                      final Arbitrary<F$> af,
                                                      final Shrink<A> sa,
                                                      final Shrink<B> sb,
                                                      final Shrink<C> sc,
                                                      final Shrink<D> sd,
                                                      final Shrink<E> se,
                                                      final Shrink<F$> sf,
                                                      final F<A, F<B, F<C, F<D, F<E, F<F$, Property>>>>>> f) {
    return property(aa, ab, ac, ad, ae, sa, sb, sc, sd, se, a -> b -> c -> d -> e -> property(af, sf, f$ -> {
      return f.f(a).f(b).f(c).f(d).f(e).f(f$);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$> Property property(final Arbitrary<A> aa,
                                                      final Arbitrary<B> ab,
                                                      final Arbitrary<C> ac,
                                                      final Arbitrary<D> ad,
                                                      final Arbitrary<E> ae,
                                                      final Arbitrary<F$> af,
                                                      final F<A, F<B, F<C, F<D, F<E, F<F$, Property>>>>>> f) {
    return property(aa, ab, ac, ad, ae, a -> b -> c -> d -> e -> property(af, f$ -> f.f(a).f(b).f(c).f(d).f(e).f(f$)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$> Property property(final Arbitrary<A> aa,
                                                      final Arbitrary<B> ab,
                                                      final Arbitrary<C> ac,
                                                      final Arbitrary<D> ad,
                                                      final Arbitrary<E> ae,
                                                      final Arbitrary<F$> af,
                                                      final Shrink<A> sa,
                                                      final Shrink<B> sb,
                                                      final Shrink<C> sc,
                                                      final Shrink<D> sd,
                                                      final Shrink<E> se,
                                                      final Shrink<F$> sf,
                                                      final F6<A, B, C, D, E, F$, Property> f) {
    return property(aa, ab, ac, ad, ae, af, sa, sb, sc, sd, se, sf, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$> Property property(final Arbitrary<A> aa,
                                                      final Arbitrary<B> ab,
                                                      final Arbitrary<C> ac,
                                                      final Arbitrary<D> ad,
                                                      final Arbitrary<E> ae,
                                                      final Arbitrary<F$> af,
                                                      final F6<A, B, C, D, E, F$, Property> f) {
    return property(aa, ab, ac, ad, ae, af, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param sg The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G> Property property(final Arbitrary<A> aa,
                                                         final Arbitrary<B> ab,
                                                         final Arbitrary<C> ac,
                                                         final Arbitrary<D> ad,
                                                         final Arbitrary<E> ae,
                                                         final Arbitrary<F$> af,
                                                         final Arbitrary<G> ag,
                                                         final Shrink<A> sa,
                                                         final Shrink<B> sb,
                                                         final Shrink<C> sc,
                                                         final Shrink<D> sd,
                                                         final Shrink<E> se,
                                                         final Shrink<F$> sf,
                                                         final Shrink<G> sg,
                                                         final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, Property>>>>>>> f) {
    return property(aa, ab, ac, ad, ae, af, sa, sb, sc, sd, se, sf, a -> b -> c -> d -> e -> f$ -> property(ag, sg, g -> {
      return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G> Property property(final Arbitrary<A> aa,
                                                         final Arbitrary<B> ab,
                                                         final Arbitrary<C> ac,
                                                         final Arbitrary<D> ad,
                                                         final Arbitrary<E> ae,
                                                         final Arbitrary<F$> af,
                                                         final Arbitrary<G> ag,
                                                         final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, Property>>>>>>> f) {
    return property(aa, ab, ac, ad, ae, af, a -> b -> c -> d -> e -> f$ -> property(ag, g -> f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param sg The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G> Property property(final Arbitrary<A> aa,
                                                         final Arbitrary<B> ab,
                                                         final Arbitrary<C> ac,
                                                         final Arbitrary<D> ad,
                                                         final Arbitrary<E> ae,
                                                         final Arbitrary<F$> af,
                                                         final Arbitrary<G> ag,
                                                         final Shrink<A> sa,
                                                         final Shrink<B> sb,
                                                         final Shrink<C> sc,
                                                         final Shrink<D> sd,
                                                         final Shrink<E> se,
                                                         final Shrink<F$> sf,
                                                         final Shrink<G> sg,
                                                         final F7<A, B, C, D, E, F$, G, Property> f) {
    return property(aa, ab, ac, ad, ae, af, ag, sa, sb, sc, sd, se, sf, sg, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G> Property property(final Arbitrary<A> aa,
                                                         final Arbitrary<B> ab,
                                                         final Arbitrary<C> ac,
                                                         final Arbitrary<D> ad,
                                                         final Arbitrary<E> ae,
                                                         final Arbitrary<F$> af,
                                                         final Arbitrary<G> ag,
                                                         final F7<A, B, C, D, E, F$, G, Property> f) {
    return property(aa, ab, ac, ad, ae, af, ag, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param ah The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param sg The shrink strategy to use upon falsification.
   * @param sh The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G, H> Property property(final Arbitrary<A> aa,
                                                            final Arbitrary<B> ab,
                                                            final Arbitrary<C> ac,
                                                            final Arbitrary<D> ad,
                                                            final Arbitrary<E> ae,
                                                            final Arbitrary<F$> af,
                                                            final Arbitrary<G> ag,
                                                            final Arbitrary<H> ah,
                                                            final Shrink<A> sa,
                                                            final Shrink<B> sb,
                                                            final Shrink<C> sc,
                                                            final Shrink<D> sd,
                                                            final Shrink<E> se,
                                                            final Shrink<F$> sf,
                                                            final Shrink<G> sg,
                                                            final Shrink<H> sh,
                                                            final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, Property>>>>>>>> f) {
    return property(aa, ab, ac, ad, ae, af, ag, sa, sb, sc, sd, se, sf, sg, a -> b -> c -> d -> e -> f$ -> g -> property(ah, sh, h -> {
      return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g).f(h);
    }));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param ah The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G, H> Property property(final Arbitrary<A> aa,
                                                            final Arbitrary<B> ab,
                                                            final Arbitrary<C> ac,
                                                            final Arbitrary<D> ad,
                                                            final Arbitrary<E> ae,
                                                            final Arbitrary<F$> af,
                                                            final Arbitrary<G> ag,
                                                            final Arbitrary<H> ah,
                                                            final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, Property>>>>>>>> f) {
    return property(aa, ab, ac, ad, ae, af, ag, a -> b -> c -> d -> e -> f$ -> g -> property(ah, h -> f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g).f(h)));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param ah The arbitrrary to produces values from to produce the property with.
   * @param sa The shrink strategy to use upon falsification.
   * @param sb The shrink strategy to use upon falsification.
   * @param sc The shrink strategy to use upon falsification.
   * @param sd The shrink strategy to use upon falsification.
   * @param se The shrink strategy to use upon falsification.
   * @param sf The shrink strategy to use upon falsification.
   * @param sg The shrink strategy to use upon falsification.
   * @param sh The shrink strategy to use upon falsification.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G, H> Property property(final Arbitrary<A> aa,
                                                            final Arbitrary<B> ab,
                                                            final Arbitrary<C> ac,
                                                            final Arbitrary<D> ad,
                                                            final Arbitrary<E> ae,
                                                            final Arbitrary<F$> af,
                                                            final Arbitrary<G> ag,
                                                            final Arbitrary<H> ah,
                                                            final Shrink<A> sa,
                                                            final Shrink<B> sb,
                                                            final Shrink<C> sc,
                                                            final Shrink<D> sd,
                                                            final Shrink<E> se,
                                                            final Shrink<F$> sf,
                                                            final Shrink<G> sg,
                                                            final Shrink<H> sh,
                                                            final F8<A, B, C, D, E, F$, G, H, Property> f) {
    return property(aa, ab, ac, ad, ae, af, ag, ah, sa, sb, sc, sd, se, sf, sg, sh, curry(f));
  }

  /**
   * Returns a property where its result is derived from universal quantification across the
   * application of its arguments. No shrinking occurs upon falsification.
   *
   * @param aa The arbitrrary to produces values from to produce the property with.
   * @param ab The arbitrrary to produces values from to produce the property with.
   * @param ac The arbitrrary to produces values from to produce the property with.
   * @param ad The arbitrrary to produces values from to produce the property with.
   * @param ae The arbitrrary to produces values from to produce the property with.
   * @param af The arbitrrary to produces values from to produce the property with.
   * @param ag The arbitrrary to produces values from to produce the property with.
   * @param ah The arbitrrary to produces values from to produce the property with.
   * @param f  The function to produce properties with results.
   * @return A property where its result is derived from universal quantification across the
   *         application of its arguments.
   */
  public static <A, B, C, D, E, F$, G, H> Property property(final Arbitrary<A> aa,
                                                            final Arbitrary<B> ab,
                                                            final Arbitrary<C> ac,
                                                            final Arbitrary<D> ad,
                                                            final Arbitrary<E> ae,
                                                            final Arbitrary<F$> af,
                                                            final Arbitrary<G> ag,
                                                            final Arbitrary<H> ah,
                                                            final F8<A, B, C, D, E, F$, G, H, Property> f) {
    return property(aa, ab, ac, ad, ae, af, ag, ah, curry(f));
  }

  /**
   * Returns a property that has a result of exception, if the evaluation of the given property
   * throws an exception; otherwise, the given property is returned.
   *
   * @param p A property to evaluate to check for an exception.
   * @return A property that has a result of exception, if the evaluation of the given property
   *         throws an exception; otherwise, the given property is returned.
   */
  public static Property exception(final F0<Property> p) {
    try {
      return p.f();
    } catch (final Throwable t) {
      return new Property(i -> r -> Result.exception(List.nil(), t));
    }
  }



}
