package fj.data;

import fj.Equal;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.*;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 18/06/2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class StreamProperties {

    public Property bindStackOverflow() {
        int max = 5000;
        return property(arbitrary(Gen.choose(1, max)), (n) -> {
            Stream<Integer> s1 = Stream.range(1, n);
            Stream<Integer> s2 = s1.bind(j -> Stream.single(j));
            return prop(s1.zip(s2).forall(p2 -> Equal.intEqual.eq(p2._1(), p2._2())));
        });
     }

}
