package fj.data;

import fj.P2;
import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;

import static fj.Function.constant;
import static fj.Ord.*;
import static fj.P.p;
import static fj.Semigroup.listSemigroup;
import static fj.data.Either.*;
import static fj.data.List.arrayList;
import static fj.data.Option.*;
import static fj.data.Seq.sequenceEither;
import static fj.data.Seq.sequenceEitherLeft;
import static fj.data.Seq.sequenceEitherRight;
import static fj.data.Seq.sequenceF;
import static fj.data.Seq.sequenceIO;
import static fj.data.Seq.sequenceList;
import static fj.data.Seq.sequenceOption;
import static fj.data.Seq.sequenceP1;
import static fj.data.Seq.sequenceSeq;
import static fj.data.Seq.sequenceSet;
import static fj.data.Seq.sequenceStream;
import static fj.data.Seq.sequenceTrampoline;
import static fj.data.Seq.sequenceValidation;
import static fj.data.Seq.*;
import static fj.data.Validation.fail;
import static fj.data.Validation.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by MarkPerry on 16/01/2015.
 */
public class SeqTest {

    @Test
    public void objectMethods() {
        Seq<Integer> s1 = Seq.seq(1, 2, 3);
        Seq<Integer> s2 = Seq.seq(1, 2, 3);
        assertTrue(s1.toString().equals("Seq(1,2,3)"));
        assertTrue(s1.equals(s2));
        assertFalse(s1 == s2);

    }

    @Test
    public void convertToString() {
        final int n = 10000;
        final StringBuilder expected = new StringBuilder("Seq(");
        for (int i = 0; i < n; i++) {
            expected.append(i);
            if (i < n - 1) {
                expected.append(',');
            }
        }
        expected.append(')');
        assertEquals(expected.toString(), Seq.seq(Array.range(0, 10000).array()).toString());
    }


    @Test
    public void test() {
        P2<Seq<Integer>, Seq<Integer>> p2 = Seq.single(1).split(5);
        assertThat(p2._1(), is(Seq.single(1)));
        assertThat(p2._2(), is(Seq.empty()));
    }

  @Test
  public void testBind() {
    assertEquals(empty(), empty().bind(constant(empty())));
    assertEquals(empty(), empty().bind(constant(single(0))));
    assertEquals(empty(), empty().bind(constant(arraySeq(0, 1))));
    assertEquals(empty(), single("zero").bind(constant(empty())));
    assertEquals(single(0), single("zero").bind(constant(single(0))));
    assertEquals(arraySeq(0, 1), single("zero").bind(constant(arraySeq(0, 1))));
    assertEquals(empty(), arraySeq("zero", "one").bind(constant(empty())));
    assertEquals(arraySeq(0, 0), arraySeq("zero", "one").bind(constant(single(0))));
    assertEquals(arraySeq(0, 1, 0, 1), arraySeq("zero", "one").bind(constant(arraySeq(0, 1))));
  }

  @Test
  public void testSequenceEither() {
    assertEquals(right(empty()), sequenceEither(empty()));
    assertEquals(right(single("zero")), sequenceEither(single(right("zero"))));
    assertEquals(left("zero"), sequenceEither(single(left("zero"))));
  }

  @Test
  public void testSequenceEitherLeft() {
    assertEquals(left(empty()), sequenceEitherLeft(empty()));
    assertEquals(left(single("zero")), sequenceEitherLeft(single(left("zero"))));
    assertEquals(right("zero"), sequenceEitherLeft(single(right("zero"))));
  }

  @Test
  public void testSequenceEitherRight() {
    assertEquals(right(empty()), sequenceEitherRight(empty()));
    assertEquals(right(single("zero")), sequenceEitherRight(single(right("zero"))));
    assertEquals(left("zero"), sequenceEitherRight(single(left("zero"))));
  }

  @Test
  public void testSequenceF() {
    assertEquals(constant(empty()).f(1), sequenceF(empty()).f(1));
    assertEquals(constant(single("zero")).f(1), sequenceF(single(constant("zero"))).f(1));
  }

  @Test
  public void testSequenceIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(empty())).run(), sequenceIO(empty()).run());
    assertEquals(IOFunctions.lazy(constant(single("zero"))).run(), sequenceIO(single(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(List.single(empty()), sequenceList(empty()));
    assertEquals(List.nil(), sequenceList(single(List.nil())));
    assertEquals(List.single(single("zero")), sequenceList(single(List.single("zero"))));
    assertEquals(arrayList(single("zero"), single("one")), sequenceList(single(arrayList("zero", "one"))));
  }

  @Test
  public void testSequenceOption() {
    assertEquals(some(empty()), sequenceOption(empty()));
    assertEquals(none(), sequenceOption(single(none())));
    assertEquals(some(single("zero")), sequenceOption(single(some("zero"))));
  }

  @Test
  public void testSequenceP1() {
    assertEquals(p(empty()), sequenceP1(empty()));
    assertEquals(p(single("zero")), sequenceP1(single(p("zero"))));
  }

  @Test
  public void testSequenceSeq() {
    assertEquals(single(empty()), sequenceSeq(empty()));
    assertEquals(empty(), sequenceSeq(single(empty())));
    assertEquals(single(single("zero")), sequenceSeq(single(single("zero"))));
    assertEquals(arraySeq(single("zero"), single("one")), sequenceSeq(single(arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.arraySet(seqOrd(stringOrd), empty()), sequenceSet(stringOrd, empty()));
    assertEquals(Set.empty(seqOrd(stringOrd)), sequenceSet(stringOrd, single(Set.empty(stringOrd))));
    assertEquals(Set.arraySet(seqOrd(stringOrd), single("zero")), sequenceSet(stringOrd, single(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(seqOrd(stringOrd), single("zero"), single("one")), sequenceSet(stringOrd, single(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(Stream.single(empty()), sequenceStream(empty()));
    assertEquals(Stream.nil(), sequenceStream(single(Stream.nil())));
    assertEquals(Stream.single(single("zero")), sequenceStream(single(Stream.single("zero"))));
    assertEquals(Stream.arrayStream(single("zero"), single("one")), sequenceStream(single(Stream.arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(empty()).run(), sequenceTrampoline(empty()).run());
    assertEquals(Trampoline.pure(single(0)).run(), sequenceTrampoline(single(Trampoline.pure(0))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(success(empty()), sequenceValidation(empty()));
    assertEquals(fail(single(0)), sequenceValidation(single(fail(single(0)))));
    assertEquals(success(single(0)), sequenceValidation(single(success(0))));
  }

  @Test
  public void testSequenceValidationSemigroup() {
    assertEquals(success(empty()), sequenceValidation(listSemigroup(), empty()));
    assertEquals(fail(List.single(0)), sequenceValidation(listSemigroup(), single(fail(List.single(0)))));
    assertEquals(success(single(0)), sequenceValidation(listSemigroup(), single(success(0))));
  }

  @Test
  public void testTraverseEitherLeft() {
    assertEquals(left(empty()), empty().traverseEitherLeft(constant(left(0))));
    assertEquals(left(single(0)), single("zero").traverseEitherLeft(constant(left(0))));
    assertEquals(left(empty()), empty().traverseEitherLeft(constant(right(0))));
    assertEquals(right(0), single("zero").traverseEitherLeft(constant(right(0))));
  }

  @Test
  public void testTraverseEitherRight() {
    assertEquals(right(empty()), empty().traverseEitherRight(constant(right(0))));
    assertEquals(right(single(0)), single("zero").traverseEitherRight(constant(right(0))));
    assertEquals(right(empty()), empty().traverseEitherRight(constant(left(0))));
    assertEquals(left(0), single("zero").traverseEitherRight(constant(left(0))));
  }

  @Test
  public void testTraverseF() {
    assertEquals(constant(empty()).f(1), empty().traverseF(constant(constant(0))).f(1));
    assertEquals(constant(single(0)).f(1), single("zero").traverseF(constant(constant(0))).f(1));
  }

  @Test
  public void testTraverseIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(empty())).run(), empty().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(IOFunctions.lazy(constant(single(0))).run(), single("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(List.single(empty()), empty().traverseList(constant(List.nil())));
    assertEquals(List.nil(), single("zero").traverseList(constant(List.nil())));
    assertEquals(List.single(empty()), empty().traverseList(constant(List.single(0))));
    assertEquals(List.single(single(0)), single("zero").traverseList(constant(List.single(0))));
    assertEquals(List.single(empty()), empty().traverseList(constant(arrayList(0, 1))));
    assertEquals(arrayList(single(0), single(1)), single("zero").traverseList(constant(arrayList(0, 1))));
  }

  @Test
  public void testTraverseOption() {
    assertEquals(some(empty()), empty().traverseOption(constant(none())));
    assertEquals(none(), single("zero").traverseOption(constant(none())));
    assertEquals(some(empty()), empty().traverseOption(constant(some(0))));
    assertEquals(some(single(0)), single("zero").traverseOption(constant(some(0))));
  }

  @Test
  public void testTraverseP1() {
    assertEquals(p(empty()), empty().traverseP1(constant(p(0))));
    assertEquals(p(single(0)), single("zero").traverseP1(constant(p(0))));
  }

  @Test
  public void testTraverseSeq() {
    assertEquals(single(empty()), empty().traverseSeq(constant(empty())));
    assertEquals(empty(), single("zero").traverseSeq(constant(empty())));
    assertEquals(single(empty()), empty().traverseSeq(constant(single(0))));
    assertEquals(single(single(0)), single("zero").traverseSeq(constant(single(0))));
    assertEquals(single(empty()), empty().traverseSeq(constant(arraySeq(0, 1))));
    assertEquals(arraySeq(single(0), single(1)), single("zero").traverseSeq(constant(arraySeq(0, 1))));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.arraySet(seqOrd(intOrd), empty()), empty().traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.empty(seqOrd(intOrd)), single("zero").traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.single(seqOrd(intOrd), empty()), empty().traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(seqOrd(intOrd), single(0)), single("zero").traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(seqOrd(intOrd), empty()), empty().traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
    assertEquals(Set.arraySet(seqOrd(intOrd), single(0), single(1)), single("zero").traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
  }

  @Test
  public void testTraverseStream() {
    assertEquals(Stream.single(empty()), empty().traverseStream(constant(Stream.nil())));
    assertEquals(Stream.nil(), single("zero").traverseStream(constant(Stream.nil())));
    assertEquals(Stream.single(empty()), empty().traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(single(0)), single("zero").traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(empty()), empty().traverseStream(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.arrayStream(single(0), single(1)), single("zero").traverseStream(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testTraverseTrampoline() {
    assertEquals(Trampoline.pure(empty()).run(), empty().traverseTrampoline(constant(Trampoline.pure(0))).run());
    assertEquals(Trampoline.pure(single(0)).run(), single("zero").traverseTrampoline(constant(Trampoline.pure(0))).run());
  }

  @Test
  public void testTraverseValidation() {
    assertEquals(success(empty()), empty().traverseValidation(constant(fail(single(0)))));
    assertEquals(fail(single(0)), single("zero").traverseValidation(constant(fail(single(0)))));
    assertEquals(success(empty()), empty().traverseValidation(constant(success(0))));
    assertEquals(success(single(0)), single("zero").traverseValidation(constant(success(0))));

    assertEquals(success(arraySeq(0, 2, 4, 6, 8)), arraySeq(0, 2, 4, 6, 8).traverseValidation(i -> condition(i% 2 == 0, List.single(i), i)));
    assertEquals(fail(List.single(1)), arraySeq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).traverseValidation(i -> condition(i% 2 == 0, List.single(i), i)));
  }

  @Test
  public void testTraverseValidationSemigroup() {
    assertEquals(success(empty()), empty().traverseValidation(listSemigroup(), constant(fail(List.single(0)))));
    assertEquals(fail(List.single(0)), single("zero").traverseValidation(listSemigroup(), constant(fail(List.single(0)))));
    assertEquals(success(empty()), empty().traverseValidation(listSemigroup(), constant(success(0))));
    assertEquals(success(single(0)), single("zero").traverseValidation(listSemigroup(), constant(success(0))));

    assertEquals(success(arraySeq(0, 2, 4, 6, 8)), arraySeq(0, 2, 4, 6, 8).traverseValidation(listSemigroup(),i -> condition(i% 2 == 0, List.single(i), i)));
    assertEquals(fail(arrayList(1, 3, 5, 7, 9)), arraySeq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).traverseValidation(listSemigroup(),i -> condition(i% 2 == 0, List.single(i), i)));
  }
}
