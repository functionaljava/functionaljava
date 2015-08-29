package fj.data.properties;

import fj.Equal;
import fj.Ord;
import fj.data.List;
import fj.data.Set;
import fj.data.Stream;
import fj.test.Arbitrary;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 18/08/2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class SetProperties {

    public final static Arbitrary<Set<Integer>> as = Arbitrary.arbSet(Ord.intOrd, Arbitrary.arbInteger, 5);

	Property setToListIsSorted() {
		return property(as, s -> prop(s.toList().equals(s.toList().sort(Ord.intOrd))));
	}

    Property stream() {
        return property(as, s -> {
            Equal<List<Integer>> eq = Equal.listEqual(Equal.intEqual);
            List<Integer> l1 = s.toList();
            List<Integer> l2 = s.toStream().toList();
            return prop(eq.eq(l1, l2));
        });
    }

}
