package fj.function;

import static org.junit.Assert.*;

import fj.F;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.core.Is.is;

import static fj.data.List.list;

public class DoublesTest {

    @Test
    public void testSum() {
        assertThat(Doubles.sum(list(3.0, 4.0, 5.0)), is(12.0));
    }

    @Test
    public void testProduct() {
        assertThat(Doubles.product(list(3.0, 4.0, 5.0)), is(60.0));
    }

    @Test
    public void testAdd() {
        assertThat(Doubles.add.f(10.0).f(20.0), is(30.0));
    }

    @Test
    public void testMultiply() {
        assertThat(Doubles.multiply.f(3.0).f(5.0), is(15.0));
    }

    @Test
    public void testAbs() {
        assertThat(Doubles.abs.f(-5.0), is(5.0));
    }

    @Test
    public void testFromString() {
        assertThat(Doubles.fromString().f("-123.45").some(), is(-123.45));
    }

    @Test
    public void testCannotInstantiate() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException {
        Constructor<Doubles> constructor = Doubles.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        try {
            constructor.newInstance(new Object[0]);
            fail("expected InvocationTargetException");
        } catch (InvocationTargetException ite) {
            assertTrue(ite.getCause() instanceof UnsupportedOperationException);
        }
    }

}
