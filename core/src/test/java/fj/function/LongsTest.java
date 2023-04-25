package fj.function;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static fj.data.List.list;
import static fj.data.Option.none;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;


public class LongsTest {

  @Test
  void testSum() {
    assertThat(Longs.sum(list(3L, 4L, 5L)), is(12L));
  }

  @Test
  void testProduct() {
    assertThat(Longs.product(list(3L, 4L, 5L)), is(60L));
  }

  @Test
  void testAdd() {
    assertThat(Longs.add.f(10L).f(20L), is(30L));
  }

  @Test
  void testMultiply() {
    assertThat(Longs.multiply.f(3L).f(5L), is(15L));
  }

  @Test
  void testAbs() {
    assertThat(Longs.abs.f(-5L), is(5L));
  }

  @Test
  void testFromString() {
    assertThat(Longs.fromString().f("-123").some(), is(-123L));
  }

  @Test
  void testFromStringFail() {
    assertThat(Longs.fromString().f("w"), is(none()));
  }

  @Test
  void testCannotInstantiate() throws NoSuchMethodException, IllegalAccessException, InstantiationException {
    Constructor<Longs> constructor = Longs.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("expected InvocationTargetException");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof UnsupportedOperationException);
    }
  }

}
