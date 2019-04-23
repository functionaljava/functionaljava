package fj.function;

import fj.F;
import fj.Monoid;
import fj.data.List;
import fj.data.Option;

import static fj.Function.curry;
import static fj.Semigroup.longAdditionSemigroup;
import static fj.Semigroup.longMultiplicationSemigroup;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static java.lang.Math.abs;

/**
 * Curried functions over Longs.
 *
 * @version %build.number%
 */
public final class Longs {
  private Longs() {
    throw new UnsupportedOperationException();
  }

  /**
   * Curried Long addition.
   */
  public static final F<Long, F<Long, Long>> add = longAdditionSemigroup.sum();

  /**
   * Curried Long multiplication.
   */
  public static final F<Long, F<Long, Long>> multiply = longMultiplicationSemigroup.sum();

  /**
   * Curried Long subtraction.
   */
  public static final F<Long, F<Long, Long>> subtract = curry((x, y) -> x - y);

  /**
   * Negation.
   */
  public static final F<Long, Long> negate = x -> x * -1L;

  /**
   * Absolute value.
   */
  public static final F<Long, Long> abs = Math::abs;

  /**
   * Remainder.
   */
  public static final F<Long, F<Long, Long>> remainder = curry((a, b) -> a % b);

  /**
   * Sums a list of longs.
   *
   * @param longs A list of longs to sum.
   * @return The sum of the longs in the list.
   */
  public static long sum(final List<Long> longs) {
    return Monoid.longAdditionMonoid.sumLeft(longs);
  }

  /**
   * Returns the product of a list of integers.
   *
   * @param longs A list of longs to multiply together.
   * @return The product of the longs in the list.
   */
  public static long product(final List<Long> longs) {
    return Monoid.longMultiplicationMonoid.sumLeft(longs);
  }

  /**
   * A function that converts strings to integers.
   *
   * @return A function that converts strings to integers.
   */
  public static F<String, Option<Long>> fromString() {
    return s -> {
      try { return some(Long.valueOf(s)); }
      catch (final NumberFormatException ignored) {
        return none();
      }
    };
  }

}
