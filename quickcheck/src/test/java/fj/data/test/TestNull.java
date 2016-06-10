package fj.data.test;

import fj.F2;
import fj.F3;
import fj.data.List;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import org.junit.Test;

import static fj.Function.compose;
import static fj.test.Arbitrary.arbLong;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 3/07/2014.
 */
public class TestNull {

	@Test
	public void testShowNullParameters() {
		Property p = property(Gen.<Integer>value(null), (Integer i) -> {
				return prop(i != null);
		});
		CheckResult.summary.println(p.check());
	}

}
