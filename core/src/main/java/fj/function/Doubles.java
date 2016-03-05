package fj.function;

import fj.F;
import fj.Monoid;
import fj.data.List;
import fj.data.Option;

import static fj.Function.curry;
import static fj.Semigroup.doubleAdditionSemigroup;
import static fj.Semigroup.doubleMultiplicationSemigroup;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static java.lang.Math.abs;

/**
 * Curried functions over Doubles.
 *
 * @version %build.number%
 */
public final class Doubles {
  private Doubles() {
    throw new UnsupportedOperationException();
  }

  /**
   * Curried Double addition.
   */
  public static final F<Double, F<Double, Double>> add = doubleAdditionSemigroup.sum();

  /**
   * Curried Double multiplication.
   */
  public static final F<Double, F<Double, Double>> multiply = doubleMultiplicationSemigroup.sum();

  /**
   * Curried Double subtraction.
   */
  public static final F<Double, F<Double, Double>> subtract = curry((x, y) -> x - y);

  /**
   * Negation.
   */
  public static final F<Double, Double> negate = x -> x * -1;

  /**
   * Absolute value.
   */
  public static final F<Double, Double> abs = Math::abs;

  /**
   * Remainder.
   */
  public static final F<Double, F<Double, Double>> remainder = curry((a, b) -> a % b);

  /**
   * Power.
   */
  public static final F<Double, F<Double, Double>> power = curry(StrictMath::pow);

  /**
   * Evenness.
   */
  public static final F<Double, Boolean> even = i -> i % 2 == 0;

  /**
   * Sums a list of doubles.
   *
   * @param doubles A list of doubles to sum.
   * @return The sum of the doubless in the list.
   */
  public static double sum(final List<Double> doubles) {
    return Monoid.doubleAdditionMonoid.sumLeft(doubles);
  }

  /**
   * Returns the product of a list of doubles.
   *
   * @param doubles A list of doubles to multiply together.
   * @return The product of the doubles in the list.
   */
  public static double product(final List<Double> doubles) {
    return Monoid.doubleMultiplicationMonoid.sumLeft(doubles);
  }

  /**
   * A function that converts strings to doubles.
   *
   * @return A function that converts strings to doubles.
   */
  public static F<String, Option<Double>> fromString() {
    return s -> {
        try { return some(Double.valueOf(s)); }
        catch (final NumberFormatException ignored) {
          return none();
        }
    };
  }

  /**
   * A function that returns true if the given double is greater than zero.
   */
  public static final F<Double, Boolean> gtZero = i -> Double.compare(i, 0) > 0;

  /**
   * A function that returns true if the given double is greater than or equal to zero.
   */
  public static final F<Double, Boolean> gteZero = i -> Double.compare(i, 0) >= 0;

  /**
   * A function that returns true if the given double is less than zero.
   */
  public static final F<Double, Boolean> ltZero = i -> Double.compare(i, 0) < 0;

  /**
   * A function that returns true if the given double is less than or equal to zero.
   */
  public static final F<Double, Boolean> lteZero = i -> Double.compare(i, 0) <= 0;

}
