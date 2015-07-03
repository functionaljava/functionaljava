package fj.data.properties;

import fj.P2;
import fj.data.List;
import fj.data.Validation;
import fj.test.Arbitrary;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.*;

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
            return Property.prop(b1 && b2 && b3);
        });
    }

}
