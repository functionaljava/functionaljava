package fj.data;

import fj.P2;
import org.junit.Test;

import static fj.Semigroup.firstSemigroup;
import static fj.data.Validation.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ValidationTest {
    @Test
    public void testParseShort() {
        final List<Validation<NumberFormatException, Short>> l =
                List.list(parseShort("10"), parseShort("x"), parseShort("20"));
        assertThat(successes(l).foldLeft1((s, a) -> (short)(s + a)), is((short)30));
    }

    @Test
    public void testParseLong() {
        final List<Validation<NumberFormatException, Long>> l =
                List.list(parseLong("10"), parseLong("x"), parseLong("20"));
        P2<List<NumberFormatException>, List<Long>> p2 = partition(l);
        assertThat(p2._1().length(), is(1));
        assertThat(p2._2().length(), is(2));
    }

    @Test
    public void testParseInt() {
        final List<Validation<NumberFormatException, Integer>> l =
                List.list(parseInt("10"), parseInt("x"), parseInt("20"));
        assertThat(l.map(v -> v.validation(e -> 0, i -> 1)).foldLeft1((s, a) -> s + a),
                is(2));
    }

    @Test
    public void testParseFloat() {
        final List<Validation<NumberFormatException, Float>> l =
                List.list(parseFloat("2.0"), parseFloat("x"), parseFloat("3.0"));
        assertThat(l.map(v -> v.validation(e -> 0, i -> 1)).foldLeft1((s, a) -> s + a),
                is(2));
    }

    @Test
    public void testParseByte() {
        final List<Validation<NumberFormatException, Byte>> l =
                List.list(parseByte("10"), parseByte("x"), parseByte("-10"));
        assertThat(l.map(v -> v.validation(e -> 0, i -> 1)).foldLeft1((s, a) -> s + a),
                is(2));
    }

    @Test
    public void testAccumulate1() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("10.0").accumulate(
                        f1 -> f1);
        assertThat(v.success(), is(10.0));
    }

    @Test
    public void testAccumulate1Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        f1 -> f1);
        assertThat(v.fail().length(), is(1));
    }

    @Test
    public void testAccumulate2() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        (f1, f2) -> f1 + f2);
        assertThat(v.success(), is(3.0));
    }

    @Test
    public void testAccumulate2Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("y"),
                        (f1, f2) -> f1 + f2);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate3() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        (f1, f2, f3) -> f1 + f2 + f3);
        assertThat(v.success(), is(6.0));
    }

    @Test
    public void testAccumulate3Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("y"),
                        (f1, f2, f3) -> f1 + f2 + f3);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate4() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        (f1, f2, f3, f4) -> f1 + f2 + f3 + f4);
        assertThat(v.success(), is(10.0));
    }

    @Test
    public void testAccumulate4Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("y"),
                        (f1, f2, f3, f4) -> f1 + f2 + f3 + f4);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate5() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        (f1, f2, f3, f4, f5) -> f1 + f2 + f3 + f4 + f5);
        assertThat(v.success(), is(15.0));
    }

    @Test
    public void testAccumulate5Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("y"),
                        (f1, f2, f3, f4, f5) -> f1 + f2 + f3 + f4 + f5);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate6() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("6.0"),
                        (f1, f2, f3, f4, f5, f6) -> f1 + f2 + f3 + f4 + f5 + f6);
        assertThat(v.success(), is(21.0));
    }

    @Test
    public void testAccumulate6Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("y"),
                        (f1, f2, f3, f4, f5, f6) -> f1 + f2 + f3 + f4 + f5 + f6);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate7() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("6.0"),
                        parseDouble("7.0"),
                        (f1, f2, f3, f4, f5, f6, f7) -> f1 + f2 + f3 + f4 + f5 + f6 + f7);
        assertThat(v.success(), is(28.0));
    }

    @Test
    public void testAccumulate7Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("6.0"),
                        parseDouble("y"),
                        (f1, f2, f3, f4, f5, f6, f7) -> f1 + f2 + f3 + f4 + f5 + f6 + f7);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate8() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("1.0").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("6.0"),
                        parseDouble("7.0"),
                        parseDouble("8.0"),
                        (f1, f2, f3, f4, f5, f6, f7, f8) -> f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8);
        assertThat(v.success(), is(36.0));
    }

    @Test
    public void testAccumulate8Fail() {
        final Validation<List<NumberFormatException>, Double> v =
                parseDouble("x").accumulate(
                        parseDouble("2.0"),
                        parseDouble("3.0"),
                        parseDouble("4.0"),
                        parseDouble("5.0"),
                        parseDouble("6.0"),
                        parseDouble("7.0"),
                        parseDouble("y"),
                        (f1, f2, f3, f4, f5, f6, f7, f8) -> f1 + f2 + f3 + f4 + f5 + f6 + f7 + f8);
        assertThat(v.fail().length(), is(2));
    }

    @Test
    public void testAccumulate8s() {
        final Validation<NumberFormatException, Integer> v1 = parseInt("1");
        final Validation<NumberFormatException, Integer> v2 = parseInt("2");
        final Validation<NumberFormatException, Integer> v3 = parseInt("3");
        final Validation<NumberFormatException, Integer> v4 = parseInt("4");
        final Validation<NumberFormatException, Integer> v5 = parseInt("5");
        final Validation<NumberFormatException, Integer> v6 = parseInt("6");
        final Validation<NumberFormatException, Integer> v7 = parseInt("7");
        final Validation<NumberFormatException, Integer> v8 = parseInt("8");
        final Option<NumberFormatException> on2 = v1.accumulate(firstSemigroup(), v2);
        assertThat(on2, is(Option.none()));
        final Option<NumberFormatException> on3 = v1.accumulate(firstSemigroup(), v2, v3);
        assertThat(on3, is(Option.none()));
        final Option<NumberFormatException> on4 = v1.accumulate(firstSemigroup(), v2, v3, v4);
        assertThat(on4, is(Option.none()));
        final Option<NumberFormatException> on5 = v1.accumulate(firstSemigroup(), v2, v3, v4, v5);
        assertThat(on5, is(Option.none()));
        final Option<NumberFormatException> on6 = v1.accumulate(firstSemigroup(), v2, v3, v4, v5, v6);
        assertThat(on6, is(Option.none()));
        final Option<NumberFormatException> on7 = v1.accumulate(firstSemigroup(), v2, v3, v4, v5, v6, v7);
        assertThat(on7, is(Option.none()));
        final Option<NumberFormatException> on8 = v1.accumulate(firstSemigroup(), v2, v3, v4, v5, v6, v7, v8);
        assertThat(on8, is(Option.none()));
    }

    @Test
    public void testAccumulate8sFail() {
        final Option<NumberFormatException> on =
                parseInt("x").accumulate(
                        firstSemigroup(),
                        parseInt("2"),
                        parseInt("3"),
                        parseInt("4"),
                        parseInt("5"),
                        parseInt("6"),
                        parseInt("7"),
                        parseInt("y"));
        assertThat(on.some().getMessage(), is("For input string: \"x\""));
    }

    @Test(expected = Error.class)
    public void testSuccess() {
        parseShort("x").success();
    }

    @Test(expected = Error.class)
    public void testFail() {
        parseShort("12").fail();
    }

    @Test
    public void testCondition() {
        final Validation<String, String> one = condition(true, "not 1", "one");
        assertThat(one.success(), is("one"));
        final Validation<String, String> fail = condition(false, "not 1", "one");
        assertThat(fail.fail(), is("not 1"));
    }

    @Test
    public void testNel() {
        assertThat(Validation.success("success").nel().success(), is("success"));
        assertThat(Validation.fail("fail").nel().fail().head(), is("fail"));
    }

    @Test
    public void testFailNEL() {
        Validation<NonEmptyList<Exception>, Integer> v = failNEL(new Exception("failed"));
        assertThat(v.isFail(), is(true));
    }

    @Test
    public void testEither() {
        assertThat(either().f(Validation.success("success")).right().value(), is("success"));
        assertThat(either().f(Validation.fail("fail")).left().value(), is("fail"));
    }

    @Test
    public void testValidation() {
        assertThat(validation().f(Either.right("success")).success(), is("success"));
        assertThat(validation().f(Either.left("fail")).fail(), is("fail"));
    }
}
