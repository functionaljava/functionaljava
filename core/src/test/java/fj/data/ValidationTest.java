package fj.data;

import fj.*;
import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;

import static fj.Function.*;
import static fj.Ord.*;
import static fj.P.*;
import static fj.data.Either.*;
import static fj.data.List.*;
import static fj.data.Option.*;
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
import static org.junit.Assert.assertEquals;

public class ValidationTest {

  @Test
  public void testAccumulateSemigroup2() {
    range(0, 2).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
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
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), 0), list.index(0).accumulate());
        });
  }

  @Test
  public void testAccumulate1() {
    range(0, 1).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), 0), list.index(0).accumulate(identity()));
        });

  }

  @Test
  public void testAccumulate2() {
    range(0, 2).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1)), list.index(0).accumulate(list.index(1), P::p));
        });
  }

  @Test
  public void testAccumulate3() {
    range(0, 3).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2)), list.index(0).accumulate(list.index(1), list.index(2), P::p));
        });

  }

  @Test
  public void testAccumulate4() {
    range(0, 4).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), P::p));
        });

  }

  @Test
  public void testAccumulate5() {
    range(0, 5).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), P::p));
        });
  }

  @Test
  public void testAccumulate6() {
    range(0, 6).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), P::p));
        });
  }

  @Test
  public void testAccumulate7() {
    range(0, 7).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
          assertEquals(condition(list.forall(Validation::isSuccess), list.filter(Validation::isFail).map(validation -> validation.fail()), p(0, 1, 2, 3, 4, 5, 6)), list.index(0).accumulate(list.index(1), list.index(2), list.index(3), list.index(4), list.index(5), list.index(6), P::p));
        });
  }

  @Test
  public void testAccumulate8() {
    range(0, 8).map(i -> List.<Validation<List<String>, Integer>>arrayList(fail(arrayList(String.valueOf(i))), success(i)))
        .foldLeft(accumulator -> list -> accumulator.isEmpty() ?
            list.map(List::single) :
            accumulator.bind(accumulatorElement -> list.map(accumulatorElement::snoc)), List.<List<Validation<List<String>, Integer>>>nil())
        .forEach(list -> {
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
