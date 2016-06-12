package fj.data.properties;

import fj.Equal;
import fj.Ord;
import fj.data.List;
import fj.data.Set;
import fj.data.Stream;
import fj.test.Arbitrary;
import fj.test.Gen;
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

    public final static int maxSize = 20;
    public final static Gen<Set<Integer>> as = Arbitrary.arbSet(Ord.intOrd, Arbitrary.arbInteger, maxSize);
    public final static Equal<List<Integer>> eq = Equal.listEqual(Equal.intEqual);

	Property setToListIsSorted() {
		return property(as, s -> prop(s.toList().equals(s.toList().sort(Ord.intOrd))));
	}

    Property stream() {
        return property(as, s -> {
            List<Integer> l1 = s.toList();
            List<Integer> l2 = s.toStream().toList();
            return prop(eq.eq(l1, l2));
        });
    }

    Property listReverse() {
        return property(as, s -> {
            return prop(eq.eq(s.toList().reverse(), s.toListReverse()));
        });
    }

    Property streamReverse() {
        return property(as, s -> prop(eq.eq(s.toStream().toList().reverse(), s.toStreamReverse().toList())));
    }

}
