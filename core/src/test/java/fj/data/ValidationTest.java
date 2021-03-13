package fj.data;

import fj.P;
import fj.P2;
import fj.Semigroup;
import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;

import static fj.Function.*;
import static fj.Ord.*;
import static fj.P.*;
import static fj.Semigroup.firstSemigroup;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.*;
import static fj.data.Option.*;
import static fj.data.Validation.parseByte;
import static fj.data.Validation.parseDouble;
import static fj.data.Validation.parseFloat;
import static fj.data.Validation.parseInt;
import static fj.data.Validation.parseLong;
import static fj.data.Validation.parseShort;
import static fj.data.Validation.sequenceEitherLeft;
import static fj.data.Validation.sequenceEitherRight;
import static fj.data.Validation.sequenceF;
import static fj.data.Validation.sequenceIO;
import static fj.data.Validation.sequenceList;
import static fj.data.Validation.sequenceOption;
import static fj.data.Validation.sequenceP1;
import static fj.data.Validation.sequenceSeq;
import static fj.data.Validation.sequenceSet;
import static fj.data.Validation.sequenceStream;
import static fj.data.Validation.sequenceTrampoline;
import static fj.data.Validation.sequenceValidation;
import static fj.data.Validation.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
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

  @Test
  public void testAccumulateSemigroup2() {
    range(0, 2).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(), list.index(1)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1)), list.index(0).accumulate(Semigroup.listSemigroup(), list.index(1), p2()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1)), list.index(0).accumulate(Semigroup.listSemigroup(), list.index(1), uncurryF2(p2())));
        });
  }

  @Test
  public void testAccumulateSemigroup3() {
    range(0, 3).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list ->
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), p3()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), uncurryF3(p3())));
        });

  }

  @Test
  public void testAccumulateSemigroup4() {
    range(0, 4).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list ->
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), p4()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), uncurryF4(p4())));
        });

  }

  @Test
  public void testAccumulateSemigroup5() {
    range(0, 5).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list ->
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), p5()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), uncurryF5(p5())));
        });
  }

  @Test
  public void testAccumulateSemigroup6() {
    range(0, 6).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list ->
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), p6()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), uncurryF6(p6())));
        });
  }

  @Test
  public void testAccumulateSemigroup7() {
    range(0, 7).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> 
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), p7()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), uncurryF7(p7())));
        });
  }

  @Test
  public void testAccumulateSemigroup8() {
    range(0, 8).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list ->
            accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(iif(list.exists(Validation::isFail), list.filter(Validation::isFail).bind(validation -> validation.fail())), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), list.index(7)));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6, 7)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), list.index(7), P.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>p8()));
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).bind(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6, 7)), list.index(0).accumulate(Semigroup.listSemigroup(),list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), list.index(7), uncurryF8(P.<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>p8())));
        });
  }

  @Test
  public void testAccumulate0() {
    range(0, 1).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), 0), list.index(0).accumulate());
        });
  }

  @Test
  public void testAccumulate1Complex() {
    range(0, 1).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), 0), list.index(0).accumulate(identity()));
        });

  }

  @Test
  public void testAccumulate2Complex() {
    range(0, 2).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1)), list.index(0).accumulate(list.index(1), P::p));
        });
  }

  @Test
  public void testAccumulate3Complex() {
    range(0, 3).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2)), list.index(0).accumulate(list.index(1), list.index(2), P::p));
        });

  }

  @Test
  public void testAccumulate4Complex() {
    range(0, 4).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), P::p));
        });

  }

  @Test
  public void testAccumulate5Complex() {
    range(0, 5).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), P::p));
        });
  }

  @Test
  public void testAccumulate6Complex() {
    range(0, 6).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), P::p));
        });
  }

  @Test
  public void testAccumulate7Complex() {
    range(0, 7).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), P::p));
        });
  }

  @Test
  public void testAccumulate8Complex() {
    range(0, 8).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .foreachDoEffect(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6, 7)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), list.index(7), P::p));
        });
  }

  @Test
  public void testMap() {
    assertEquals(Validation.<String, Integer>fail("zero"), Validation.<String, String>fail("zero").map(constant(0)));
    assertEquals(Validation.<String, Integer>success(0), Validation.<String, String>success("zero").map(constant(0)));
    assertEquals(Validation.<String, Integer>fail("zero"), Validation.<String, String>fail("zero").map(constant(0)));
    assertEquals(Validation.<String, Integer>success(0), Validation.<String, String>success("zero").map(constant(0)));
  }

  @Test
  public void testBind() {
    assertEquals(Validation.<String, Integer>fail("zero"), Validation.<String, String>fail("zero").bind(constant(Validation.<String, Integer>fail("zero"))));
    assertEquals(Validation.<String, Integer>fail("zero"), Validation.<String, String>success("zero").bind(constant(Validation.<String, Integer>fail("zero"))));
    assertEquals(Validation.<String, Integer>fail("zero"), Validation.<String, String>fail("zero").bind(constant(Validation.<String, Integer>success(0))));
    assertEquals(Validation.<String, Integer>success(0), Validation.<String, String>success("zero").bind(constant(Validation.<String, Integer>success(0))));
  }

  @Test
  public void testSequenceEitherLeft() {
    assertEquals(left(fail("zero")), sequenceEitherLeft(fail("zero")));
    assertEquals(left(success("zero")), sequenceEitherLeft(success(left("zero"))));
    assertEquals(right("zero"), sequenceEitherLeft(success(right("zero"))));
  }

  @Test
  public void testSequenceEitherRight() {
    assertEquals(right(fail("zero")), sequenceEitherRight(fail("zero")));
    assertEquals(right(success("zero")), sequenceEitherRight(success(right("zero"))));
    assertEquals(left("zero"), sequenceEitherRight(success(left("zero"))));
  }

  @Test
  public void testSequenceF() {
    assertEquals(constant(fail("zero")).f(1), sequenceF(fail("zero")).f(1));
    assertEquals(constant(success("zero")).f(1), sequenceF(success(constant("zero"))).f(1));
  }

  @Test
  public void testSequenceIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(fail("zero"))).run(), sequenceIO(fail("zero")).run());
    assertEquals(IOFunctions.lazy(constant(success("zero"))).run(), sequenceIO(success(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(single(fail("zero")), sequenceList(fail("zero")));
    assertEquals(nil(), sequenceList(success(nil())));
    assertEquals(single(success("zero")), sequenceList(success(single("zero"))));
    assertEquals(arrayList(success("zero"), success("one")), sequenceList(success(arrayList("zero", "one"))));
  }

  @Test
  public void testSequenceOption() {
    assertEquals(some(fail("zero")), sequenceOption(fail("zero")));
    assertEquals(none(), sequenceOption(success(none())));
    assertEquals(some(success("zero")), sequenceOption(success(some("zero"))));
  }

  @Test
  public void testSequenceP1() {
    assertEquals(p(fail("zero")), sequenceP1(fail("zero")));
    assertEquals(p(success("zero")), sequenceP1(success(p("zero"))));
  }

  @Test
  public void testSequenceSeq() {
    assertEquals(Seq.single(fail("zero")), sequenceSeq(fail("zero")));
    assertEquals(Seq.empty(), sequenceSeq(success(Seq.empty())));
    assertEquals(Seq.single(success("zero")), sequenceSeq(success(Seq.single("zero"))));
    assertEquals(Seq.arraySeq(success("zero"), success("one")), sequenceSeq(success(Seq.arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.single(validationOrd(stringOrd, intOrd), fail("zero")), sequenceSet(stringOrd, intOrd, fail("zero")));
    assertEquals(Set.empty(validationOrd(stringOrd, intOrd)), sequenceSet(stringOrd, intOrd, success(Set.empty(intOrd))));
    assertEquals(Set.single(validationOrd(intOrd, stringOrd), success("zero")), sequenceSet(intOrd, stringOrd, success(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(validationOrd(intOrd, stringOrd), success("zero"), success("one")), sequenceSet(intOrd, stringOrd, success(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(Stream.single(fail("zero")), sequenceStream(fail("zero")));
    assertEquals(Stream.nil(), sequenceStream(success(Stream.nil())));
    assertEquals(Stream.single(success("zero")), sequenceStream(success(Stream.single("zero"))));
    assertEquals(Stream.arrayStream(success("zero"), success("one")), sequenceStream(success(Stream.arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(fail("zero")).run(), sequenceTrampoline(fail("zero")).run());
    assertEquals(Trampoline.pure(success(0)).run(), sequenceTrampoline(success(Trampoline.pure(0))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(success(fail("zero")), sequenceValidation(fail("zero")));
    assertEquals(fail("zero"), sequenceValidation(success(fail("zero"))));
    assertEquals(success(success(0)), sequenceValidation(success(success(0))));
  }

  @Test
  public void testTraverseEitherLeft() {
    assertEquals(left(fail("zero")), fail("zero").traverseEitherLeft(constant(left(0))));
    assertEquals(left(success(0)), success("zero").traverseEitherLeft(constant(left(0))));
    assertEquals(left(fail("zero")), fail("zero").traverseEitherLeft(constant(right(0))));
    assertEquals(right(0), success("zero").traverseEitherLeft(constant(right(0))));
  }

  @Test
  public void testTraverseEitherRight() {
    assertEquals(right(fail("zero")), fail("zero").traverseEitherRight(constant(right(0))));
    assertEquals(right(success(0)), success("zero").traverseEitherRight(constant(right(0))));
    assertEquals(right(fail("zero")), fail("zero").traverseEitherRight(constant(left(0))));
    assertEquals(left(0), success("zero").traverseEitherRight(constant(left(0))));
  }

  @Test
  public void testTraverseF() {
    assertEquals(constant(fail("zero")).f(1), fail("zero").traverseF(constant(constant(0))).f(1));
    assertEquals(constant(success(0)).f(1), success("zero").traverseF(constant(constant(0))).f(1));
  }

  @Test
  public void testTraverseIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(fail("zero"))).run(), fail("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(IOFunctions.lazy(constant(success(0))).run(), success("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(single(fail("zero")), fail("zero").traverseList(constant(nil())));
    assertEquals(nil(), success("zero").traverseList(constant(nil())));
    assertEquals(single(fail("zero")), fail("zero").traverseList(constant(single(0))));
    assertEquals(single(success(0)), success("zero").traverseList(constant(single(0))));
    assertEquals(single(fail("zero")), fail("zero").traverseList(constant(arrayList(0, 1))));
    assertEquals(arrayList(success(0), success(1)), success("zero").traverseList(constant(arrayList(0, 1))));
  }

  @Test
  public void testTraverseOption() {
    assertEquals(some(fail("zero")), fail("zero").traverseOption(constant(none())));
    assertEquals(none(), success("zero").traverseOption(constant(none())));
    assertEquals(some(fail("zero")), fail("zero").traverseOption(constant(some(0))));
    assertEquals(some(success(0)), success("zero").traverseOption(constant(some(0))));
  }

  @Test
  public void testTraverseP1() {
    assertEquals(p(fail("zero")), fail("zero").traverseP1(constant(p(0))));
    assertEquals(p(success(0)), success("zero").traverseP1(constant(p(0))));
  }

  @Test
  public void testTraverseSeq() {
    assertEquals(Seq.single(fail("zero")), fail("zero").traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.empty(), success("zero").traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.single(fail("zero")), fail("zero").traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(success(0)), success("zero").traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(fail("zero")), fail("zero").traverseSeq(constant(Seq.arraySeq(0, 1))));
    assertEquals(Seq.arraySeq(success(0), success(1)), success("zero").traverseSeq(constant(Seq.arraySeq(0, 1))));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.single(validationOrd(stringOrd, intOrd), fail("zero")), fail("zero").traverseSet(stringOrd, intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.empty(validationOrd(stringOrd, intOrd)), Validation.<String, String>success("zero").traverseSet(stringOrd, intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.single(validationOrd(stringOrd, intOrd), fail("zero")), fail("zero").traverseSet(stringOrd, intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(validationOrd(stringOrd, intOrd), success(0)), Validation.<String, String>success("zero").traverseSet(stringOrd, intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(validationOrd(stringOrd, intOrd), fail("zero")), fail("zero").traverseSet(stringOrd, intOrd, constant(Set.arraySet(intOrd, 0, 1))));
    assertEquals(Set.arraySet(validationOrd(stringOrd, intOrd), success(0), success(1)), Validation.<String, String>success("zero").traverseSet(stringOrd, intOrd, constant(Set.arraySet(intOrd, 0, 1))));
  }

  @Test
  public void testTraverseStream() {
    assertEquals(Stream.single(fail("zero")), fail("zero").traverseStream(constant(Stream.nil())));
    assertEquals(Stream.nil(), success("zero").traverseStream(constant(Stream.nil())));
    assertEquals(Stream.single(fail("zero")), fail("zero").traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(success(0)), success("zero").traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(fail("zero")), fail("zero").traverseStream(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.arrayStream(success(0), success(1)), success("zero").traverseStream(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testTraverseTrampoline() {
    assertEquals(Trampoline.pure(fail("zero")).run(), fail("zero").traverseTrampoline(constant(Trampoline.pure(0))).run());
    assertEquals(Trampoline.pure(success(0)).run(), success("zero").traverseTrampoline(constant(Trampoline.pure(0))).run());
  }

  @Test
  public void testTraverseValidation() {
    assertEquals(Validation.<String, Validation<String, Integer>>success(fail("zero")), Validation.<String, String>fail("zero").traverseValidation(constant(Validation.<Integer, Integer>fail(0))));
    assertEquals(Validation.<Integer, Validation<String, Integer>>fail(0), Validation.<String, String>success("zero").traverseValidation(constant(Validation.<Integer, Integer>fail(0))));
    assertEquals(Validation.<String, Validation<String, Integer>>success(fail("zero")), Validation.<String, String>fail("zero").traverseValidation(constant(Validation.<Integer, Integer>success(0))));
    assertEquals(Validation.<String, Validation<String, Integer>>success(success(0)), Validation.<String, String>success("zero").traverseValidation(constant(Validation.<Integer, Integer>success(0))));
  }

}
