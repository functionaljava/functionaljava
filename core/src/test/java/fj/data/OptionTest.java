package fj.data;

import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;

import static fj.Function.constant;
import static fj.Ord.*;
import static fj.P.p;
import static fj.data.Either.*;
import static fj.data.List.*;
import static fj.data.Option.sequence;
import static fj.data.Option.sequenceEitherLeft;
import static fj.data.Option.sequenceEitherRight;
import static fj.data.Option.sequenceF;
import static fj.data.Option.sequenceIO;
import static fj.data.Option.sequenceList;
import static fj.data.Option.sequenceOption;
import static fj.data.Option.sequenceP1;
import static fj.data.Option.sequenceSeq;
import static fj.data.Option.sequenceSet;
import static fj.data.Option.sequenceStream;
import static fj.data.Option.sequenceTrampoline;
import static fj.data.Option.sequenceValidation;
import static fj.data.Option.*;
import static fj.data.Validation.fail;
import static fj.data.Validation.*;
import static org.junit.Assert.*;

/**
 * Created by MarkPerry on 15/01/2015.
 */
public final class OptionTest {

    @Test
    public void equals() {
        int max = 4;
        assertTrue(some(1).equals(some(1)));
        assertTrue(some(List.range(1, max)).equals(some(List.range(1, max))));
    }

    @Test
    public void traverseList() {
        int max = 3;
        List<Option<Integer>> actual = some(max).traverseList(a -> List.range(1, a + 1));
        List<Option<Integer>> expected = List.range(1, max + 1).map(i -> some(i));
        assertTrue(actual.equals(expected));
    }

  @Test
  public void sequenceListTest() {
    assertEquals(some(nil()), sequence(nil()));
    assertEquals(none(), sequence(arrayList(none())));
    assertEquals(some(arrayList(1)), sequence(arrayList(some(1))));
    assertEquals(none(), sequence(arrayList(none(), none())));
    assertEquals(none(), sequence(arrayList(some(1), none())));
    assertEquals(none(), sequence(arrayList(none(), some(2))));
    assertEquals(some(arrayList(1, 2)), sequence(arrayList(some(1), some(2))));
  }

  @Test
  public void sequenceValidationTest() {
    assertEquals(some(fail(1)), sequence(Validation.<Integer, Option<String>>fail(1)));
    assertEquals(none(), sequence(Validation.<Integer, Option<String>>success(none())));
    assertEquals(some(success("string")), sequence(Validation.<Integer, Option<String>>success(some("string"))));
  }

  @Test
  public void testSequenceEitherLeft() {
    assertEquals(left(none()), sequenceEitherLeft(none()));
    assertEquals(left(some("zero")), sequenceEitherLeft(some(left("zero"))));
    assertEquals(right("zero"), sequenceEitherLeft(some(right("zero"))));
  }

  @Test
  public void testSequenceEitherRight() {
    assertEquals(right(none()), sequenceEitherRight(none()));
    assertEquals(right(some("zero")), sequenceEitherRight(some(right("zero"))));
    assertEquals(left("zero"), sequenceEitherRight(some(left("zero"))));
  }

  @Test
  public void testSequenceF() {
    assertEquals(constant(none()).f(1), sequenceF(none()).f(1));
    assertEquals(constant(some("zero")).f(1), sequenceF(some(constant("zero"))).f(1));
  }

  @Test
  public void testSequenceIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(none())).run(), sequenceIO(none()).run());
    assertEquals(IOFunctions.lazy(constant(some("zero"))).run(), sequenceIO(some(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(List.single(none()), sequenceList(none()));
    assertEquals(List.nil(), sequenceList(some(List.nil())));
    assertEquals(List.single(some("zero")), sequenceList(some(List.single("zero"))));
    assertEquals(List.arrayList(some("zero"), some("one")), sequenceList(some(List.arrayList("zero", "one"))));
  }

  @Test
  public void testSequenceOption() {
    assertEquals(some(none()), sequenceOption(none()));
    assertEquals(none(), sequenceOption(some(none())));
    assertEquals(some(some("zero")), sequenceOption(some(some("zero"))));
  }

  @Test
  public void testSequenceP1() {
    assertEquals(p(none()), sequenceP1(none()));
    assertEquals(p(some("zero")), sequenceP1(some(p("zero"))));
  }

  @Test
  public void testSequenceSeq() {
    assertEquals(Seq.single(none()), sequenceSeq(none()));
    assertEquals(Seq.empty(), sequenceSeq(some(Seq.empty())));
    assertEquals(Seq.single(some("zero")), sequenceSeq(some(Seq.single("zero"))));
    assertEquals(Seq.arraySeq(some("zero"), some("one")), sequenceSeq(some(Seq.arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.arraySet(optionOrd(stringOrd), none()), sequenceSet(stringOrd, none()));
    assertEquals(Set.empty(optionOrd(stringOrd)), sequenceSet(stringOrd, some(Set.empty(stringOrd))));
    assertEquals(Set.arraySet(optionOrd(stringOrd), some("zero")), sequenceSet(stringOrd, some(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(optionOrd(stringOrd), some("zero"), some("one")), sequenceSet(stringOrd, some(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(Stream.single(none()), sequenceStream(none()));
    assertEquals(Stream.nil(), sequenceStream(some(Stream.nil())));
    assertEquals(Stream.single(some("zero")), sequenceStream(some(Stream.single("zero"))));
    assertEquals(Stream.arrayStream(some("zero"), some("one")), sequenceStream(some(Stream.arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(none()).run(), sequenceTrampoline(none()).run());
    assertEquals(Trampoline.pure(some(0)).run(), sequenceTrampoline(some(Trampoline.pure(0))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(Validation.success(none()), sequenceValidation(none()));
    assertEquals(Validation.fail(0), sequenceValidation(some(Validation.fail(0))));
    assertEquals(Validation.success(some(0)), sequenceValidation(some(Validation.success(0))));
  }

  @Test
  public void testTraverseEitherLeft() {
    assertEquals(left(none()), none().traverseEitherLeft(constant(left(0))));
    assertEquals(left(some(0)), some("zero").traverseEitherLeft(constant(left(0))));
    assertEquals(left(none()), none().traverseEitherLeft(constant(right(0))));
    assertEquals(right(0), some("zero").traverseEitherLeft(constant(right(0))));
  }

  @Test
  public void testTraverseEitherRight() {
    assertEquals(right(none()), none().traverseEitherRight(constant(right(0))));
    assertEquals(right(some(0)), some("zero").traverseEitherRight(constant(right(0))));
    assertEquals(right(none()), none().traverseEitherRight(constant(left(0))));
    assertEquals(left(0), some("zero").traverseEitherRight(constant(left(0))));
  }

  @Test
  public void testTraverseF() {
    assertEquals(constant(none()).f(1), none().traverseF(constant(constant(0))).f(1));
    assertEquals(constant(some(0)).f(1), some("zero").traverseF(constant(constant(0))).f(1));
  }

  @Test
  public void testTraverseIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(none())).run(), none().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(IOFunctions.lazy(constant(some(0))).run(), some("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(List.single(none()), none().traverseList(constant(List.nil())));
    assertEquals(List.nil(), some("zero").traverseList(constant(List.nil())));
    assertEquals(List.single(none()), none().traverseList(constant(List.single(0))));
    assertEquals(List.single(some(0)), some("zero").traverseList(constant(List.single(0))));
    assertEquals(List.single(none()), none().traverseList(constant(List.arrayList(0, 1))));
    assertEquals(List.arrayList(some(0), some(1)), some("zero").traverseList(constant(List.arrayList(0, 1))));
  }

  @Test
  public void testTraverseOption() {
    assertEquals(some(none()), none().traverseOption(constant(none())));
    assertEquals(none(), some("zero").traverseOption(constant(none())));
    assertEquals(some(none()), none().traverseOption(constant(some(0))));
    assertEquals(some(some(0)), some("zero").traverseOption(constant(some(0))));
  }

  @Test
  public void testTraverseP1() {
    assertEquals(p(none()), none().traverseP1(constant(p(0))));
    assertEquals(p(some(0)), some("zero").traverseP1(constant(p(0))));
  }

  @Test
  public void testTraverseSeq() {
    assertEquals(Seq.single(none()), none().traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.empty(), some("zero").traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.single(none()), none().traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(some(0)), some("zero").traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(none()), none().traverseSeq(constant(Seq.arraySeq(0, 1))));
    assertEquals(Seq.arraySeq(some(0), some(1)), some("zero").traverseSeq(constant(Seq.arraySeq(0, 1))));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.arraySet(optionOrd(intOrd), none()), none().traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.empty(optionOrd(intOrd)), some("zero").traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.arraySet(optionOrd(intOrd), none()), none().traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.arraySet(optionOrd(intOrd), some(0)), some("zero").traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.arraySet(optionOrd(intOrd), none()), none().traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
    assertEquals(Set.arraySet(optionOrd(intOrd), some(0), some(1)), some("zero").traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
  }

  @Test
  public void testTraverseStream() {
    assertEquals(Stream.single(none()), none().traverseStream(constant(Stream.nil())));
    assertEquals(Stream.nil(), some("zero").traverseStream(constant(Stream.nil())));
    assertEquals(Stream.single(none()), none().traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(some(0)), some("zero").traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(none()), none().traverseStream(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.arrayStream(some(0), some(1)), some("zero").traverseStream(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testTraverseTrampoline() {
    assertEquals(Trampoline.pure(none()).run(), none().traverseTrampoline(constant(Trampoline.pure(0))).run());
    assertEquals(Trampoline.pure(some(0)).run(), some("zero").traverseTrampoline(constant(Trampoline.pure(0))).run());
  }

  @Test
  public void testTraverseValidation() {
    assertEquals(success(none()), none().traverseValidation(constant(Validation.fail(0))));
    assertEquals(fail(0), some("zero").traverseValidation(constant(Validation.fail(0))));
    assertEquals(success(none()), none().traverseValidation(constant(Validation.success(0))));
    assertEquals(success(some(0)), some("zero").traverseValidation(constant(Validation.success(0))));
  }
}
