package fj.test;

import fj.data.test.PropertyAssert;
import org.junit.Test;

import static fj.test.Arbitrary.arbBoolean;
import static fj.test.Bool.bool;
import static fj.test.Property.property;

public class TestBool {

    @Test
    public void testBool() {
        final Property p = property(arbBoolean, arbBoolean, (m1, m2) -> bool(m1.equals(m2))
                .implies(m1 == m2));
        PropertyAssert.assertResult(p);
    }
}
