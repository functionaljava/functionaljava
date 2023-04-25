package fj.control.parallel;

import fj.Equal;
import fj.F;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.data.Stream;
import fj.function.Strings;
import fj.test.Arbitrary;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.Equal.stringEqual;
import static fj.Monoid.stringMonoid;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbP1;
import static fj.test.Arbitrary.arbParModule;
import static fj.test.Arbitrary.arbStream;
import static fj.test.Arbitrary.arbString;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
public class CheckParModuleTest {

  public Property parFlatMap() {
    return property(arbStream(arbString), arbParModule(), (str, pm) -> {
      F<String, Stream<String>> f = s3 -> Stream.stream(s3, Strings.reverse().f(s3));
      return prop(Equal.streamEqual(stringEqual).eq(str.bind(f), pm.parFlatMap(str, f).claim()));
    });
  }

  public Property parFoldMap() {
    return property(arbStream(arbString), arbParModule(), (str, pm) -> {
      F<Stream<String>, P2<Stream<String>, Stream<String>>> chunk = x -> P.p(Stream.stream(x.head()), x.tail()._1());
      return prop(stringEqual.eq(
          stringMonoid.sumLeft(str.map(Strings.reverse())),
          pm.parFoldMap(str, Strings.reverse(), stringMonoid, chunk).claim()
      ));
    });
  }


}
