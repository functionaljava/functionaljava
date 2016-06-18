package fj.data.test;

import fj.F2;
import fj.F3;
import fj.data.List;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import org.junit.*;

import static fj.Function.compose;
import static fj.test.Arbitrary.arbLong;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static org.junit.Assert.*;

public class TestCheck {

	@Test(timeout=5000 /*ms*/)
	public void testExceptionsThrownFromGeneratorsArePropagated() {
		Gen<Integer> failingGen = Gen.<Integer>value(0).map((i) -> {
			throw new RuntimeException("test failure");
		});

		Property p = property(failingGen, (Integer i) -> {
			return prop(i == 0);
		});

		CheckResult res = p.check(
			1, /*minSuccessful*/
			0, /*maxDiscarded*/
			0, /*minSize*/
			1 /*maxSize*/
		);
		assertTrue("Exception not propagated!", res.isGenException());
	}
}
