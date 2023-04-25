package fj.data;

import fj.F;
import fj.function.Booleans;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static fj.P1.curry;
import static fj.data.List.list;
import static fj.function.Booleans.isnot;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class BooleansTest {

  @Test
  void testAnd() {
    F<String, Boolean> f1 = a -> a.startsWith("fj");
    F<String, Boolean> f2 = a -> a.contains("data");

    F<String, Boolean> f3 = Booleans.and(f1, f2);

    Assertions.assertTrue(f3.f("fj.data"));
    Assertions.assertTrue(f3.f("fj.data.Function"));

  }

  @Test
  void testOr() {
    F<String, Boolean> f1 = a -> a.startsWith("fj");
    F<String, Boolean> f2 = a -> a.startsWith("someOtherPackage");

    F<String, Boolean> f3 = Booleans.or(f1, f2);

    Assertions.assertTrue(f3.f("fj.data"));
    Assertions.assertTrue(f3.f("someOtherPackage.fj.data"));
    Assertions.assertFalse(f3.f("something.fj.data.Function"));

  }

  @Test
  void testContramap() {
    F<String, Boolean> f1 = a -> a.length() > 3;
    F<Integer, String> f2 = a -> a.toString();

    F<Integer, Boolean> f3 = Booleans.contramap(f2, f1);

    Assertions.assertTrue(f3.f(1000));
    Assertions.assertFalse(f3.f(100));

  }

  @SuppressWarnings("unchecked")
  @Test
  void testAndAll() {
    F<String, Boolean> f1 = a -> a.endsWith("fj");
    F<String, Boolean> f2 = a -> a.startsWith("someOtherPackage");
    F<String, Boolean> f3 = a -> a.length() < 20;

    F<String, Boolean> f4 = Booleans.andAll(Stream.<F<String, Boolean>>stream(f1, f2, f3));

    Assertions.assertTrue(f4.f("someOtherPackage.fj"));
    Assertions.assertFalse(f4.f("otther"));
    Assertions.assertFalse(f4.f("someOtherPackage.fj.data.something.really.big"));

  }

  @SuppressWarnings("unchecked")
  @Test
  void testIsNot() {
    F<Integer, Boolean> f1 = a -> a == 4;
    List<String> result = list("some", "come", "done!").filter(isnot(String::length, f1));

    assertThat(result.length(), is(1));
    Assertions.assertEquals(result, list("done!"));

  }
}
