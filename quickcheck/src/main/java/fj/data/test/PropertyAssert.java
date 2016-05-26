package fj.data.test;

import fj.Unit;
import fj.test.CheckResult;
import fj.test.Property;
import org.junit.Assert;

/**
 * Created by MarkPerry on 18/12/2014.
 */
public final class PropertyAssert {

    private PropertyAssert(){}

    public static Unit assertResult(Property p) {
        CheckResult cr = p.check();
        CheckResult.summary.println(cr);
        Assert.assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
        return Unit.unit();
    }

}
