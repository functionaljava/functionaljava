package fj.demo.test;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/*
Checks that adding any two integers, a + b, is equivalent to b + a.
This is the commutative property of addition.
*/
public final class AdditionCommutes {
  public static void main(final String[] args) {
    final Property p = property(arbInteger, arbInteger, (a, b) -> prop(a + b == b + a));
    summary.println(p.check()); // OK, passed 100 tests.
  }
}
