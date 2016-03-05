package fj.data;

import fj.F;

import static fj.Function.*;
import static fj.data.Option.none;
import static fj.data.Option.some;

import fj.Function;
import fj.Ord;

import static fj.Ord.*;
import fj.Ordering;
import static fj.Ordering.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Abstracts over a type that may have a successor and/or predecessor value. This implies ordering for that type. A user
 * may construct an enumerator with an optimised version for <code>plus</code>, otherwise a default is implemented using
 * the given successor/predecessor implementations.
 * <p/>
 * For any enumerator e, the following laws must satisfy:
 * <ul>
 * <li>forall a. e.successor(a).forall(\t -> e.predecessor(t).forall(\z -> z == a))</li>
 * <li>forall a. e.predecessor(a).forall(\t -> e.successor(t).forall(\z -> z == a))</li>
 * <li>e.max().forall(\t -> e.successor(t).isNone)</li>
 * <li>e.min().forall(\t -> e.predecessor(t).isNone)</li>
 * <li>forall a n. e.plus(a, 0) == Some(a)</li>
 * <li>forall a n | n > 0. e.plus(a, n) == e.plus(a, n - 1)</li>
 * <li>forall a n | n < 0. e.plus(a, n) == e.plus(a, n + 1)</li>
 * </ul>
 *
 * @version %build.number%
 */
public final class Enumerator<A> {
  private final F<A, Option<A>> successor;
  private final F<A, Option<A>> predecessor;
  private final Option<A> max;
  private final Option<A> min;
  private final Ord<A> order;
  private final F<A, F<Long, Option<A>>> plus;

  private Enumerator(final F<A, Option<A>> successor, final F<A, Option<A>> predecessor, final Option<A> max,
                     final Option<A> min, final Ord<A> order, final F<A, F<Long, Option<A>>> plus) {
    this.successor = successor;
    this.predecessor = predecessor;
    this.max = max;
    this.min = min;
    this.order = order;
    this.plus = plus;
  }

  /**
   * Returns the potential successor of a value for this enumerator in curried form.
   *
   * @return The potential successor of a value for this enumerator in curried form.
   */
  public F<A, Option<A>> successor() {
    return successor;
  }

  /**
   * Returns the potential successor of a value for this enumerator.
   *
   * @param a The value to return the successor of.
   * @return The potential successor of a value for this enumerator.
   */
  public Option<A> successor(final A a) {
    return successor.f(a);
  }

  /**
   * Returns the potential predecessor of a value for this enumerator in curried form.
   *
   * @return The potential predecessor of a value for this enumerator in curried form.
   */
  public F<A, Option<A>> predecessor() {
    return predecessor;
  }

  /**
   * Returns the potential predecessor of a value for this enumerator.
   *
   * @param a The value to return the predecessor of.
   * @return The potential predecessor of a value for this enumerator.
   */
  public Option<A> predecessor(final A a) {
    return predecessor.f(a);
  }

  /**
   * Returns the maximum value for this enumerator if there is one.
   *
   * @return The maximum value for this enumerator if there is one.
   */
  public Option<A> max() {
    return max;
  }

  /**
   * Returns the minimum value for this enumerator if there is one.
   *
   * @return The minimum value for this enumerator if there is one.
   */
  public Option<A> min() {
    return min;
  }

  /**
   * Returns a function that moves a value along the enumerator a given number of times.
   *
   * @return A function that moves a value along the enumerator a given number of times.
   */
  public F<A, F<Long, Option<A>>> plus() {
    return plus;
  }

  /**
   * Returns a function that moves a value along the enumerator a given number of times.
   *
   * @param a The value to begin moving along from.
   * @return A function that moves a value along the enumerator a given number of times.
   */
  public F<Long, Option<A>> plus(final A a) {
    return plus.f(a);
  }

  /**
   * Returns a function that moves a value along the enumerator a given number of times.
   *
   * @param l The number of times to move along the enumerator.
   * @return A function that moves a value along the enumerator a given number of times.
   */
  public F<A, Option<A>> plus(final long l) {
    return flip(plus).f(l);
  }

  /**
   * Moves a value along the enumerator a given number of times.
   *
   * @param a The value to begin moving along from.
   * @param l The number of times to move along the enumerator.
   * @return A potential value after having moved the given number of times.
   */
  public Option<A> plus(final A a, final long l) {
    return plus.f(a).f(l);
  }

  /**
   * Returns the ordering for the enumerator.
   *
   * @return The ordering for the enumerator.
   */
  public Ord<A> order() {
    return order;
  }

  /**
   * Invariant functor map over this enumerator.
   *
   * @param f The covariant map.
   * @param g The contra-variant map.
   * @return An enumerator after the given functions are applied.
   */
  public <B> Enumerator<B> xmap(final F<A, B> f, final F<B, A> g) {
    final F<Option<A>, Option<B>> of = o -> o.map(f);
    return enumerator(compose(compose(of, successor), g),
                      compose(compose(of, predecessor), g),
                      max.map(f),
                      min.map(f),
                      order.contramap(g),
                      compose(compose(Function.<Long, Option<A>, Option<B>>compose().f(of), plus), g));
  }

  /**
   * Returns a stream of the values from this enumerator, starting at the given value, counting up.
   *
   * @param a A value at which to begin the stream.
   * @return a stream of the values from this enumerator, starting at the given value, counting up.
   */
  public Stream<A> toStream(final A a) {
    final F<A, A> id = identity();
    return Stream.fromFunction(this, id, a);
  }

  /**
   * Create a new enumerator with the given minimum value.
   *
   * @param min A minimum value.
   * @return A new enumerator identical to this one, but with the given minimum value.
   */
  public Enumerator<A> setMin(final Option<A> min) {
    return enumerator(successor, predecessor, max, min, order, plus);
  }

  /**
   * Create a new enumerator with the given maximum value.
   *
   * @param max A maximum value.
   * @return A new enumerator identical to this one, but with the given maximum value.
   */
  public Enumerator<A> setMax(final Option<A> max) {
    return enumerator(successor, predecessor, max, min, order, plus);
  }

  /**
   * Construct an enumerator.    `
   *
   * @param successor   The successor function.
   * @param predecessor The predecessor function.
   * @param max         The potential maximum value.
   * @param min         The potential minimum value.
   * @param order       The ordering for the type.
   * @param plus        The function to move the enumeration a given number of times. This may be supplied for a performance
   *                    enhancement for certain types.
   * @return An enumerator with the given values.
   */
  public static <A> Enumerator<A> enumerator(final F<A, Option<A>> successor, final F<A, Option<A>> predecessor,
                                             final Option<A> max, final Option<A> min, final Ord<A> order,
                                             final F<A, F<Long, Option<A>>> plus) {
    return new Enumerator<>(successor, predecessor, max, min, order, plus);
  }

  /**
   * Construct an enumerator. The <code>plus</code> function is derived from the <code>successor</code> and
   * <code>predecessor</code>.
   *
   * @param successor   The successor function.
   * @param predecessor The predecessor function.
   * @param max         The potential maximum value.
   * @param min         The potential minimum value.
   * @param order       The ordering for the type.
   * @return An enumerator with the given values.
   */
  public static <A> Enumerator<A> enumerator(final F<A, Option<A>> successor, final F<A, Option<A>> predecessor,
                                             final Option<A> max, final Option<A> min, final Ord<A> order) {
    return new Enumerator<>(successor, predecessor, max, min, order, curry((a, l) -> {
      if (l == 0L)
        return some(a);
      else if (l < 0L) {
        A aa = a;
        for (long x = l; x < 0; x++) {
          final Option<A> s = predecessor.f(aa);
          if (s.isNone())
            return none();
          else
            aa = s.some();
        }
        return some(aa);
      } else {
        A aa = a;
        for (long x = l; x > 0; x--) {
          final Option<A> s = successor.f(aa);
          if (s.isNone())
            return none();
          else
            aa = s.some();
        }
        return some(aa);
      }
    }));
  }

  /**
   * An enumerator for <code>boolean</code>.
   */
  public static final Enumerator<Boolean> booleanEnumerator = enumerator(
      b -> b ? Option.none() : some(true),
      b -> b ? some(false) : Option.none(),
      some(true), some(false), booleanOrd
  );

  /**
   * An enumerator for <code>byte</code>.
   */
  public static final Enumerator<Byte> byteEnumerator = enumerator(
      b -> b == Byte.MAX_VALUE ? Option.none() : some((byte) (b + 1)),
      b -> b == Byte.MIN_VALUE ? Option.none() : some((byte) (b - 1)),
      some(Byte.MAX_VALUE), some(Byte.MIN_VALUE), byteOrd
  );

  /**
   * An enumerator for <code>char</code>.
   */
  public static final Enumerator<Character> charEnumerator = enumerator(
      c -> c == Character.MAX_VALUE ? Option.none() : some((char) (c + 1)),
      c -> c == Character.MIN_VALUE ? Option.none() : some((char) (c - 1)),
      some(Character.MAX_VALUE), some(Character.MIN_VALUE), charOrd
  );

  /**
   * An enumerator for <code>double</code>.
   */
  public static final Enumerator<Double> doubleEnumerator = enumerator(
      d -> d == Double.MAX_VALUE ? Option.none() : some(d + 1D),
      d -> d == Double.MIN_VALUE ? Option.none() : some(d - 1D),
      some(Double.MAX_VALUE), some(Double.MIN_VALUE), doubleOrd
  );

  /**
   * An enumerator for <code>float</code>.
   */
  public static final Enumerator<Float> floatEnumerator = enumerator(
      f -> f == Float.MAX_VALUE ? Option.none() : some(f + 1F),
      f -> f == Float.MIN_VALUE ? Option.none() : some(f - 1F),
      some(Float.MAX_VALUE), some(Float.MIN_VALUE), floatOrd
  );

  /**
   * An enumerator for <code>int</code>.
   */
  public static final Enumerator<Integer> intEnumerator = enumerator(
      i -> i == Integer.MAX_VALUE ? Option.none() : some(i + 1),
      i -> i == Integer.MIN_VALUE ? Option.none() : some(i - 1),
      some(Integer.MAX_VALUE), some(Integer.MIN_VALUE), intOrd
  );

  /**
   * An enumerator for <code>BigInteger</code>.
   */
  public static final Enumerator<BigInteger> bigintEnumerator = enumerator(
      i -> some(i.add(BigInteger.ONE)),
      i -> some(i.subtract(BigInteger.ONE)),
      Option.none(), Option.none(), bigintOrd,
      curry((i, l) -> some(i.add(BigInteger.valueOf(l))))
  );

  /**
   * An enumerator for <code>BigDecimal</code>.
   */
  public static final Enumerator<BigDecimal> bigdecimalEnumerator = enumerator(
      i -> some(i.add(BigDecimal.ONE)),
      i -> some(i.subtract(BigDecimal.ONE)),
      Option.none(), Option.none(), bigdecimalOrd,
      curry((d, l) -> some(d.add(BigDecimal.valueOf(l))))
  );

  /**
   * An enumerator for <code>long</code>.
   */
  public static final Enumerator<Long> longEnumerator = enumerator(
      i -> i == Long.MAX_VALUE ? Option.none() : some(i + 1L),
      i -> i == Long.MIN_VALUE ? Option.none() : some(i - 1L),
      some(Long.MAX_VALUE), some(Long.MIN_VALUE), longOrd
  );

  /**
   * An enumerator for <code>short</code>.
   */
  public static final Enumerator<Short> shortEnumerator = enumerator(
      i -> i == Short.MAX_VALUE ? Option.none() : some((short) (i + 1)),
      i -> i == Short.MIN_VALUE ? Option.none() : some((short) (i - 1)),
      some(Short.MAX_VALUE), some(Short.MIN_VALUE), shortOrd
  );

  /**
   * An enumerator for <code>Ordering</code>.
   */
  public static final Enumerator<Ordering> orderingEnumerator = enumerator(
      o -> o == LT ? some(EQ) : o == EQ ? some(GT) : Option.none(),
      o -> o == GT ? some(EQ) : o == EQ ? some(LT) : Option.none(),
      some(GT), some(LT), orderingOrd
  );

  /**
   * An enumerator for <code>Natural</code>
   */
  public static final Enumerator<Natural> naturalEnumerator = enumerator(
      n -> some(n.succ()),
      Natural::pred,
      Option.none(), some(Natural.ZERO), naturalOrd,
      curry((n, l) ->
        some(n).apply(
          Natural.natural(l).map(curry((n1, n2) -> n1.add(n2)))
        )
      )
  );

}
