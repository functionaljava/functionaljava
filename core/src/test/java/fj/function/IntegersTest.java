package fj.function;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static fj.data.List.list;
import static fj.data.Option.none;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class IntegersTest {

    @Test
    public void testSum() {
        assertThat(Integers.sum(list(3, 4, 5)), is(12));
    }

    @Test
    public void testProduct() {
        assertThat(Integers.product(list(3, 4, 5)), is(60));
    }

    @Test
    public void testAdd() {
        assertThat(Integers.add.f(10).f(20), is(30));
    }

    @Test
    public void testMultiply() {
        assertThat(Integers.multiply.f(3).f(5), is(15));
    }

    @Test
    public void testAbs() {
        assertThat(Integers.abs.f(-5), is(5));
    }

    @Test
    public void testFromString() {
        assertThat(Integers.fromString().f("-123").some(), is(-123));
    }

    @Test
    public void testFromStringFail() {
        assertThat(Integers.fromString().f("w"), is(none()));
    }

    @Test
    public void testCannotInstantiate() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
        Constructor<Integers> constructor = Integers.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("expected InvocationTargetException");
        } catch (InvocationTargetException ite) {
            assertTrue(ite.getCause() instanceof UnsupportedOperationException);
        }
    }

}
