package fj.data.properties;

import fj.P2;
import fj.data.List;
import fj.data.Validation;
import fj.test.Arbitrary;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;

/**
 * Created by MarkPerry on 3/07/2015.
 */
@RunWith(PropertyTestRunner.class)
public class ValidationProperties {

    public Property partition() {
        Arbitrary<List<Validation<String, Integer>>> al = arbList(arbValidation(arbUSASCIIString, arbInteger));
        return Property.property(al, list -> {
            P2<List<String>, List<Integer>> p = Validation.partition(list);
            boolean b1 = p._1().length() + p._2().length() == list.length();
            boolean b2 = p._1().map(s -> Validation.<String, Integer>fail(s)).equals(list.filter(v -> v.isFail()));
            boolean b3 = p._2().map(s -> Validation.<String, Integer>success(s)).equals(list.filter(v -> v.isSuccess()));
            return prop(b1 && b2 && b3);
        });
    }

    public Property sequenceReduce() {
        Arbitrary<List<Validation<String, Integer>>> al = arbList(arbValidation(arbUSASCIIString, arbInteger));
        return Property.property(al, list -> {
            Validation<List<String>, List<Integer>> v = Validation.sequenceReduce(list);
            Property p1 = implies(
                    list.exists(v1 -> v1.isFail()),
                    () -> prop(v.fail().equals(list.filter(v2 -> v2.isFail()).map(v2 -> v2.fail())))
            );
            Property p2 = implies(
                    list.forall(v1 -> v1.isSuccess()),
                    () -> prop(v.success().equals(list.filter(v2 -> v2.isSuccess()).map(v2 -> v2.success())))
            );
            return p1.and(p2);
        });
    }

}
