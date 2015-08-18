package fj.data.properties;

import fj.Equal;
import fj.Ord;
import fj.data.Set;
import fj.test.Arbitrary;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.test.Property.property;

/**
 * Created by MarkPerry on 18/08/2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class SetProperties {

	Property setToListIsSorted() {
		Arbitrary<Set<Integer>> as = Arbitrary.arbSet(Ord.intOrd, Arbitrary.arbInteger);
		return property(as, s -> Property.prop(s.toList().equals(s.toList().sort(Ord.intOrd))));
	}

}
