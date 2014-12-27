package fj.data;

import static fj.Bottom.error;
import fj.F;
import fj.F2;
import static fj.Monoid.naturalAdditionMonoid;
import static fj.Monoid.naturalMultiplicationMonoid;
import static fj.Function.curry;
import fj.data.vector.V2;
import fj.data.vector.V;

import java.math.BigInteger;

/**
 * Represents a natural number (zero, one, two, etc.)
 */
public final class Natural extends Number {
  private final BigInteger value;
  private static final long serialVersionUID = -588673650944359682L;

  private Natural(final BigInteger i) {
    if (i.compareTo(BigInteger.ZERO) < 0)
      throw error("Natural less than zero");
    value = i;
  }

  /**
   * Returns the natural number equal to the given BigInteger
   *
   * @param i A given BigInteger
   * @return An optional natural number, or none if the given BigInteger is less than zero.
   */
  public static Option<Natural> natural(final BigInteger i) {
    return i.compareTo(BigInteger.ZERO) < 0
           ? Option.<Natural>none()
           : Option.some(new Natural(i));
  }

  /**
   * A function that returns the natural number equal to a given BigInteger
   */
  public static final F<BigInteger, Option<Natural>> fromBigInt =
          i -> natural(i);

  /**
   * Returns the natural number equal to the given long
   *
   * @param i A given long
   * @return An optional natural number, or none if the given long is less than zero.
   */
  public static Option<Natural> natural(final long i) {
    return natural(BigInteger.valueOf(i));
  }

  /**
   * The natural number zero
   */
  public static final Natural ZERO = natural(0).some();

  /**
   * The natural number one
   */
  public static final Natural ONE = natural(1).some();

  /**
   * Return the successor of this natural number
   *
   * @return the successor of this natural number
   */
  public Natural succ() {
    return add(ONE);
  }

  /**
   * First-class successor function.
   *
   * @return A function that returns the successor of a given natural number.
   */
  public static F<Natural, Natural> succ_() {
    return natural -> natural.succ();
  }

  /**
   * Return the predecessor of this natural number
   *
   * @return the predecessor of this natural number
   */
  public Option<Natural> pred() {
    return subtract(ONE);
  }

  /**
   * First-class predecessor function.
   *
   * @return A function that returns the predecessor of a given natural number, or None if it's zero.
   */
  public static F<Natural, Option<Natural>> pred_() {
    return natural -> natural.pred();
  }

  /**
   * Add two natural numbers together.
   *
   * @param n A natural number to add to this one.
   * @return the sum of the two natural numbers.
   */
  public Natural add(final Natural n) {
    return natural(n.value.add(value)).some();
  }

  /**
   * A function that adds two natural numbers.
   */
  public static final F<Natural, F<Natural, Natural>> add = curry((n1, n2) -> n1.add(n2));


  /**
   * Subtract a natural number from another.
   *
   * @param n A natural number to subtract from this one.
   * @return The difference between the two numbers, if this number is larger than the given one. Otherwise none.
   */
  public Option<Natural> subtract(final Natural n) {
    return natural(n.value.subtract(value));
  }

  /**
   * A function that subtracts its first argument from its second.
   */
  public static final F<Natural, F<Natural, Option<Natural>>> subtract =
      curry((o, o1) -> o1.subtract(o));

  /**
   * Multiply a natural number by another.
   *
   * @param n A natural number to multiply by this one.
   * @return The product of the two numbers.
   */
  public Natural multiply(final Natural n) {
    return natural(n.value.multiply(value)).some();
  }

  /**
   * A function that multiplies a natural number by another.
   */
  public static final F<Natural, F<Natural, Natural>> multiply = curry((n1, n2) -> n1.multiply(n2));


  /**
   * A function that divides its second argument by its first.
   */
  public static final F<Natural, F<Natural, Natural>> divide =
      curry((n1, n2) -> n2.divide(n1));

  /**
   * Divide a natural number by another.
   *
   * @param n A natural number to divide this one by.
   * @return The quotient of this number and the highest number, less than or equal to the given number,
   *         that divides this number.
   */
  public Natural divide(final Natural n) {
    return natural(value.divide(n.value)).some();
  }

  /**
   * Take the remainder of a natural number division.
   *
   * @param n A natural number to divide this one by.
   * @return The remainder of division of this number by the given number.
   */
  public Natural mod(final Natural n) {
    return natural(value.mod(n.value)).some();
  }

  /**
   * A function that yields the remainder of division of its second argument by its first.
   */
  public static final F<Natural, F<Natural, Natural>> mod =
      curry((n1, n2) -> n2.mod(n1));

  /**
   * Divide a natural number by another yielding both the quotient and the remainder.
   *
   * @param n A natural number to divide this one by.
   * @return The quotient and the remainder, in that order.
   */
  public V2<Natural> divmod(final Natural n) {
    final BigInteger[] x = value.divideAndRemainder(n.value);
    return V.v(natural(x[0]).some(), natural(x[1]).some());
  }

  /**
   * A function that divides its second argument by its first, yielding both the quotient and the remainder.
   */
  public static final F<Natural, F<Natural, V2<Natural>>> divmod =
      curry((n1, n2) -> n2.divmod(n1));


  /**
   * Return the BigInteger value of this natural number.
   *
   * @return the BigInteger value of this natural number.
   */
  public BigInteger bigIntegerValue() {
    return value;
  }

  /**
   * Return the long value of this natural number.
   *
   * @return the long value of this natural number.
   */
  public long longValue() {
    return value.longValue();
  }

  /**
   * Return the float value of this natural number.
   *
   * @return the float value of this natural number.
   */
  public float floatValue() {
    return value.floatValue();
  }

  /**
   * Return the double value of this natural number.
   *
   * @return the double value of this natural number.
   */
  public double doubleValue() {
    return value.doubleValue();
  }

  /**
   * Return the int value of this natural number.
   *
   * @return the int value of this natural number.
   */
  public int intValue() {
    return value.intValue();
  }

  /**
   * A function that returns the BigInteger value of a given Natural.
   */
  public static final F<Natural, BigInteger> bigIntegerValue = n -> n.bigIntegerValue();

  /**
   * Sums a stream of natural numbers.
   *
   * @param ns A stream of natural numbers.
   * @return The sum of all the natural numbers in the stream.
   */
  public static Natural sum(final Stream<Natural> ns) {
    return naturalAdditionMonoid.sumLeft(ns);
  }

  /**
   * Takes the product of a stream of natural numbers.
   *
   * @param ns A stream of natural numbers.
   * @return The product of all the natural numbers in the stream.
   */
  public static Natural product(final Stream<Natural> ns) {
    return naturalMultiplicationMonoid.sumLeft(ns);
  }

  /**
   * Sums a list of natural numbers.
   *
   * @param ns A list of natural numbers.
   * @return The sum of all the natural numbers in the list.
   */
  public static Natural sum(final List<Natural> ns) {
    return naturalAdditionMonoid.sumLeft(ns);
  }

  /**
   * Takes the product of a list of natural numbers.
   *
   * @param ns A list of natural numbers.
   * @return The product of all the natural numbers in the list.
   */
  public static Natural product(final List<Natural> ns) {
    return naturalMultiplicationMonoid.sumLeft(ns);
  }
}
