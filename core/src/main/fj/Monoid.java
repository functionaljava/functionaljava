package fj;

import static fj.Function.curry;
import static fj.Function.compose;
import static fj.Function.flip;
import fj.data.Array;
import fj.data.List;
import fj.data.Natural;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import static fj.data.Stream.iterableStream;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must
 * follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @version %build.number%
 */
public final class Monoid<A> {
  private final F<A, F<A, A>> sum;
  private final A zero;

  private Monoid(final F<A, F<A, A>> sum, final A zero) {
    this.sum = sum;
    this.zero = zero;
  }

  /**
   * Returns a semigroup projection of this monoid.
   *
   * @return A semigroup projection of this monoid.
   */
  public Semigroup<A> semigroup() {
    return Semigroup.semigroup(sum);
  }

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The of the two given arguments.
   */
  public A sum(final A a1, final A a2) {
    return sum.f(a1).f(a2);
  }

  /**
   * Returns a function that sums the given value according to this monoid.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this monoid.
   */
  public F<A, A> sum(final A a1) {
    return sum.f(a1);
  }

  /**
   * Returns a function that sums according to this monoid.
   *
   * @return A function that sums according to this monoid.
   */
  public F<A, F<A, A>> sum() {
    return sum;
  }

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  public A zero() {
    return zero;
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumRight(final List<A> as) {
    return as.foldRight(sum, zero);
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumRight(final Stream<A> as) {
    return as.foldRight(new F2<A, P1<A>, A>() {
      public A f(final A a, final P1<A> ap1) {
        return sum(a, ap1._1());
      }
    }, zero);
  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final List<A> as) {
    return as.foldLeft(sum, zero);
  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final Stream<A> as) {
    return as.foldLeft(sum, zero);
  }

  /**
   * Returns a function that sums the given values with left-fold.
   *
   * @return a function that sums the given values with left-fold.
   */
  public F<List<A>, A> sumLeft() {
    return new F<List<A>, A>() {
      public A f(final List<A> as) {
        return sumLeft(as);
      }
    };
  }

  /**
   * Returns a function that sums the given values with right-fold.
   *
   * @return a function that sums the given values with right-fold.
   */
  public F<List<A>, A> sumRight() {
    return new F<List<A>, A>() {
      public A f(final List<A> as) {
        return sumRight(as);
      }
    };
  }

  /**
   * Returns a function that sums the given values with left-fold.
   *
   * @return a function that sums the given values with left-fold.
   */
  public F<Stream<A>, A> sumLeftS() {
    return new F<Stream<A>, A>() {
      public A f(final Stream<A> as) {
        return sumLeft(as);
      }
    };
  }

  /**
   * Intersperses the given value between each two elements of the iterable, and sums the result.
   *
   * @param as An iterable of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public A join(final Iterable<A> as, final A a) {
    final Stream<A> s = iterableStream(as);
    return s.isEmpty() ?
           zero :
           s.foldLeft1(compose(sum, flip(sum).f(a)));
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal
   * laws.
   *
   * @param sum  The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final F<A, F<A, A>> sum, final A zero) {
    return new Monoid<A>(sum, zero);
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal
   * laws.
   *
   * @param sum  The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final F2<A, A, A> sum, final A zero) {
    return new Monoid<A>(curry(sum), zero);
  }

  /**
   * Constructs a monoid from the given semigroup and zero value, which must follow the monoidal laws.
   *
   * @param s    The semigroup for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final Semigroup<A> s, final A zero) {
    return new Monoid<A>(s.sum(), zero);
  }

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAdditionMonoid = monoid(Semigroup.intAdditionSemigroup, 0);

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplicationMonoid = monoid(Semigroup.intMultiplicationSemigroup, 1);

  /**
   * A monoid that adds doubles.
   */
  public static final Monoid<Double> doubleAdditionMonoid = monoid(Semigroup.doubleAdditionSemigroup, 0.0);

  /**
   * A monoid that multiplies doubles.
   */
  public static final Monoid<Double> doubleMultiplicationMonoid = monoid(Semigroup.doubleMultiplicationSemigroup, 1.0);

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoid(Semigroup.bigintAdditionSemigroup, BigInteger.ZERO);

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid =
      monoid(Semigroup.bigintMultiplicationSemigroup, BigInteger.ONE);

  /**
   * A monoid that adds big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalAdditionMonoid =
      monoid(Semigroup.bigdecimalAdditionSemigroup, BigDecimal.ZERO);

  /**
   * A monoid that multiplies big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalMultiplicationMonoid =
      monoid(Semigroup.bigdecimalMultiplicationSemigroup, BigDecimal.ONE);

  /**
   * A monoid that adds natural numbers.
   */
  public static final Monoid<Natural> naturalAdditionMonoid =
      monoid(Semigroup.naturalAdditionSemigroup, Natural.ZERO);

  /**
   * A monoid that multiplies natural numbers.
   */
  public static final Monoid<Natural> naturalMultiplicationMonoid =
      monoid(Semigroup.naturalMultiplicationSemigroup, Natural.ONE);

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAdditionMonoid = monoid(Semigroup.longAdditionSemigroup, 0L);

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplicationMonoid = monoid(Semigroup.longMultiplicationSemigroup, 1L);

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunctionMonoid = monoid(Semigroup.disjunctionSemigroup, false);

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoid(Semigroup.exclusiveDisjunctionSemiGroup, false);

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoid(Semigroup.conjunctionSemigroup, true);

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = monoid(Semigroup.stringSemigroup, "");

  /**
   * A monoid that appends string buffers.
   */
  public static final Monoid<StringBuffer> stringBufferMonoid = monoid(Semigroup.stringBufferSemigroup, new StringBuffer());

  /**
   * A monoid that appends string builders.
   */
  public static final Monoid<StringBuilder> stringBuilderMonoid = monoid(Semigroup.stringBuilderSemigroup, new StringBuilder());

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<F<A, B>> functionMonoid(final Monoid<B> mb) {
    return monoid(Semigroup.<A, B>functionSemigroup(mb.semigroup()), Function.<A, B>constant(mb.zero));
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> listMonoid() {
    return monoid(Semigroup.<A>listSemigroup(), List.<A>nil());
  }

  /**
   * A monoid for options.
   *
   * @return A monoid for options.
   */
  public static <A> Monoid<Option<A>> optionMonoid() {
    return monoid(Semigroup.<A>optionSemigroup(), Option.<A>none());
  }

  /**
   * A monoid for options that take the first available value.
   *
   * @return A monoid for options that take the first available value.
   */
  public static <A> Monoid<Option<A>> firstOptionMonoid() {
    return monoid(Semigroup.<A>firstOptionSemigroup(), Option.<A>none());
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoid(Semigroup.<A>lastOptionSemigroup(), Option.<A>none());
  }

  /**
   * A monoid for streams.
   *
   * @return A monoid for streams.
   */
  public static <A> Monoid<Stream<A>> streamMonoid() {
    return monoid(Semigroup.<A>streamSemigroup(), Stream.<A>nil());
  }

  /**
   * A monoid for arrays.
   *
   * @return A monoid for arrays.
   */
  @SuppressWarnings({"unchecked"})
  public static <A> Monoid<Array<A>> arrayMonoid() {
    return monoid(Semigroup.<A>arraySemigroup(), Array.<A>empty());
  }

  /**
   * A monoid for sets.
   *
   * @param o An order for set elements.
   * @return A monoid for sets whose elements have the given order.
   */
  public static <A> Monoid<Set<A>> setMonoid(final Ord<A> o) {
    return monoid(Semigroup.<A>setSemigroup(), Set.empty(o));
  }

}
