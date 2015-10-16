package fj.data;

import fj.F;
import fj.function.Booleans;
import org.junit.Assert;
import org.junit.Test;

import static fj.P1.curry;
import static fj.data.List.list;
import static fj.function.Booleans.isnot;
import static org.hamcrest.core.Is.is;

/**
 * Created by amar on 28/01/15.
 */
public class BooleansTest {

    @Test
    public void testAnd(){
        F<String, Boolean> f1 = a -> a.startsWith("fj");
        F<String, Boolean> f2 = a -> a.contains("data");

        F<String, Boolean> f3 = Booleans.and(f1, f2);

        Assert.assertTrue(f3.f("fj.data"));
        Assert.assertTrue(f3.f("fj.data.Function"));

    }

    @Test
    public void testOr(){
        F<String, Boolean> f1 = a -> a.startsWith("fj");
        F<String, Boolean> f2 = a -> a.startsWith("someOtherPackage");

        F<String, Boolean> f3 = Booleans.or(f1, f2);

        Assert.assertTrue(f3.f("fj.data"));
        Assert.assertTrue(f3.f("someOtherPackage.fj.data"));
        Assert.assertFalse(f3.f("something.fj.data.Function"));

    }

    @Test
    public void testContramap(){
        F<String, Boolean> f1 = a -> a.length() > 3;
        F<Integer, String> f2 = a -> a.toString();

        F<Integer, Boolean> f3 = Booleans.contramap(f2, f1);

        Assert.assertTrue(f3.f(1000));
        Assert.assertFalse(f3.f(100));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAndAll(){
        F<String, Boolean> f1 = a -> a.endsWith("fj");
        F<String, Boolean> f2 = a -> a.startsWith("someOtherPackage");
        F<String, Boolean> f3 = a -> a.length() < 20;

        F<String, Boolean> f4 = Booleans.andAll(Stream.<F<String, Boolean>>stream(f1, f2, f3));

        Assert.assertTrue(f4.f("someOtherPackage.fj"));
        Assert.assertFalse(f4.f("otther"));
        Assert.assertFalse(f4.f("someOtherPackage.fj.data.something.really.big"));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsNot(){
        F<Integer, Boolean> f1 = a -> a == 4;
        List<String> result = list("some", "come", "done!").filter(isnot(String::length, f1));

        Assert.assertThat(result.length(), is(1));
        Assert.assertEquals(result, list("done!"));

    }
}
