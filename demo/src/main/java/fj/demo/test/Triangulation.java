package fj.demo.test;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/*
Any integer is either positive, zero or negative. Less succintly, isPositive applied to any integer
is equivalent in truth to the conjunction (&&) of isNegative applied to that integer and a test for
equivalence to zero.
*/
public final class Triangulation {
  static boolean isNegative(final int i) {
    return i < 0;
  }

  static boolean isPositive(final int i) {
    // bzzt!
    return i == i;
  }

  public static void main(final String[] args) {
    final Property p = property(arbInteger, a -> prop(isPositive(a) == (a != 0 && !isNegative(a))));
    summary.println(p.check()); // Falsified after 0 passed tests with argument: 0
  }
}
