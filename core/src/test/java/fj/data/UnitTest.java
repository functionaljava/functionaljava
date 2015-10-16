package fj.data;

import fj.Unit;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by MarkPerry on 17/01/2015.
 */
public class UnitTest {

    @Test
    public void objectMethods() {
        Assert.assertTrue(Unit.unit().equals(Unit.unit()));
        Assert.assertFalse(Unit.unit().equals(3));
        Assert.assertTrue(Unit.unit().toString().equals("unit"));
    }

}
