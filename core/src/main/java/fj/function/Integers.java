package fj.function;

import fj.F;

import static fj.Function.curry;

import fj.Monoid;
import fj.data.List;
import fj.data.Option;
import static fj.data.Option.some;
import static fj.data.Option.none;
import static fj.Semigroup.intAdditionSemigroup;
import static fj.Semigroup.intMultiplicationSemigroup;

import static java.lang.Math.abs;

/**
 * Curried functions over Integers.
 *
 * @version %build.number%
 */
public final class Integers {
  private Integers() {
    throw new UnsupportedOperationException();
  }

  /**
   * Curried Integer addition.
   */
  public static final F<Integer, F<Integer, Integer>> add = intAdditionSemigroup.sum();

  /**
   * Curried Integer multiplication.
   */
  public static final F<Integer, F<Integer, Integer>> multiply = intMultiplicationSemigroup.sum();

  /**
   * Curried Integer subtraction.
   */
  public static final F<Integer, F<Integer, Integer>> subtract = curry((x, y) -> x - y);

  /**
   * Negation.
   */
  public static final F<Integer, Integer> negate = x -> x * -1;

  /**
   * Absolute value.
   */
  public static final F<Integer, Integer> abs = Math::abs;

  /**
   * Remainder.
   */
  public static final F<Integer, F<Integer, Integer>> remainder = curry((a, b) -> a % b);

  /**
   * Power.
   */
  public static final F<Integer, F<Integer, Integer>> power = curry((a, b) -> (int) StrictMath.pow(a, b));

  /**
   * Evenness.
   */
  public static final F<Integer, Boolean> even = i -> i % 2 == 0;

  /**
   * Sums a list of integers.
   *
   * @param ints A list of integers to sum.
   * @return The sum of the integers in the list.
   */
  public static int sum(final List<Integer> ints) {
    return Monoid.intAdditionMonoid.sumLeft(ints);
  }

  /**
   * Returns the product of a list of integers.
   *
   * @param ints A list of integers to multiply together.
   * @return The product of the integers in the list.
   */
  public static int product(final List<Integer> ints) {
    return Monoid.intMultiplicationMonoid.sumLeft(ints);
  }

  /**
   * A function that converts strings to integers.
   *
   * @return A function that converts strings to integers.
   */
  public static F<String, Option<Integer>> fromString() {
    return s -> {
        try { return some(Integer.valueOf(s)); }
        catch (final NumberFormatException ignored) {
          return none();
        }
    };
  }

  /**
   * A function that returns true if the given integer is greater than zero.
   */
  public static final F<Integer, Boolean> gtZero = i -> i > 0;

  /**
   * A function that returns true if the given integer is greater than or equal to zero.
   */
  public static final F<Integer, Boolean> gteZero = i -> i >= 0;

  /**
   * A function that returns true if the given integer is less than zero.
   */
  public static final F<Integer, Boolean> ltZero = i -> i < 0;

  /**
   * A function that returns true if the given integer is less than or equal to zero. 
   */
  public static final F<Integer, Boolean> lteZero = i -> i <= 0;

}
