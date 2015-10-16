package fj.function;

import fj.F;
import fj.F2;
import fj.Monoid;
import fj.data.List;
import static fj.Function.curry;

import java.math.BigInteger;

/**
 * Curried functions over Integers.
 *
 * @version %build.number%
 */
public final class BigIntegers {
  private BigIntegers() {
    throw new UnsupportedOperationException();
  }

  /**
   * Curried Integer addition.
   */
  public static final F<BigInteger, F<BigInteger, BigInteger>> add =
      curry((a1, a2) -> a1.add(a2));

  /**
   * Curried Integer multiplication.
   */
  public static final F<BigInteger, F<BigInteger, BigInteger>> multiply =
      curry((a1, a2) -> a1.multiply(a2));

  /**
   * Curried Integer subtraction.
   */
  public static final F<BigInteger, F<BigInteger, BigInteger>> subtract =
      curry((a1, a2) -> a1.subtract(a2));

  /**
   * Negation.
   */
  public static final F<BigInteger, BigInteger> negate = i -> i.negate();

  /**
   * Absolute value.
   */
  public static final F<BigInteger, BigInteger> abs = i -> i.abs();

  /**
   * Remainder.
   */
  public static final F<BigInteger, F<BigInteger, BigInteger>> remainder =
      curry((a1, a2) -> a1.remainder(a2));

  /**
   * Power.
   */
  public static final F<BigInteger, F<Integer, BigInteger>> power = curry((a1, a2) -> a1.pow(a2));

  /**
   * Sums a list of big integers.
   *
   * @param ints A list of big integers to sum.
   * @return The sum of the big integers in the list.
   */
  public static BigInteger sum(final List<BigInteger> ints) {
    return Monoid.bigintAdditionMonoid.sumLeft(ints);
  }

  /**
   * Returns the product of a list of big integers.
   *
   * @param ints A list of big integers to multiply together.
   * @return The product of the big integers in the list.
   */
  public static BigInteger product(final List<BigInteger> ints) {
    return Monoid.bigintMultiplicationMonoid.sumLeft(ints);
  }
}
