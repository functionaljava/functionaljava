package fj.function;

import fj.F;

import static fj.Function.curry;
import static fj.Semigroup.longAdditionSemigroup;
import static fj.Semigroup.longMultiplicationSemigroup;

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

}
