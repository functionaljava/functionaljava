package fj.data;

import org.junit.Test;

import static fj.data.Validation.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import fj.*;

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

    @Test(expected = Error.class)
    public void testSuccess() {
        parseShort("x").success();
    }

    @Test(expected = Error.class)
    public void testFail() {
        parseShort("12").fail();
    }
}
