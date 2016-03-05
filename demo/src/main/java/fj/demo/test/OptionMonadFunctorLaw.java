package fj.demo.test;

import static fj.Function.andThen;
import fj.data.Option;
import static fj.Equal.optionEqual;
import static fj.Equal.stringEqual;
import fj.test.Arbitrary;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbOption;
import static fj.test.Arbitrary.arbString;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/*
For any Option (o) and any function (f), then calling flatMap on o with a
function that puts its result in Some is equivalent to calling map on o with f.
*/
public final class OptionMonadFunctorLaw {
  public static void main(final String[] args) {
    final Property unitMap = property(arbOption(arbInteger), Arbitrary.<Integer, String>arbFInvariant(arbString), (o, f) -> prop(optionEqual(stringEqual).eq(o.bind(andThen(f, Option.some_())), o.map(f))));
    summary.println(unitMap.minSuccessful(500)); // OK, passed 500 tests.     
  }
}
