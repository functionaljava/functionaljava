package fj.data.fingertrees;

import fj.P2;
import fj.data.Stream;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 10/10/2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class FingerTreeProperties {

    Property size() {
        return property(arbList(arbInteger), list ->
            prop(list.foldLeft((acc, i) -> acc.snoc(i), FingerTree.<Integer>emptyIntAddition()).length() == list.length())
        );
    }

    Property stream() {
        return property(arbList(arbInteger), list -> {
            Stream<Integer> s1 = list.foldLeft((acc, i) -> acc.snoc(i), FingerTree.<Integer>emptyIntAddition()).toStream();
            Stream<Integer> s2 = list.toStream();
            return prop(s1.equals(s2));
    });
    }

}
