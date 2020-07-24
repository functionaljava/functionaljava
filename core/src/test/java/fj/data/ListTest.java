package fj.data;

import fj.*;
import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static fj.Function.constant;
import static fj.Ord.*;
import static fj.P.*;
import static fj.Semigroup.listSemigroup;
import static fj.data.Either.*;
import static fj.data.List.sequenceEither;
import static fj.data.List.sequenceEitherLeft;
import static fj.data.List.sequenceEitherRight;
import static fj.data.List.sequenceF;
import static fj.data.List.sequenceIO;
import static fj.data.List.sequenceList;
import static fj.data.List.sequenceOption;
import static fj.data.List.sequenceP1;
import static fj.data.List.sequenceSeq;
import static fj.data.List.sequenceSet;
import static fj.data.List.sequenceStream;
import static fj.data.List.sequenceTrampoline;
import static fj.data.List.sequenceValidation;
import static fj.data.List.*;
import static fj.data.Option.*;
import static fj.data.Validation.fail;
import static fj.data.Validation.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by MarkPerry on 16/01/2015.
 */
public class ListTest {

    @Test
    public void objectMethods() {

        int max = 5;
        List<Integer> list = List.range(1, max);

        assertTrue(list.equals(list));
        assertTrue(list.equals(List.range(1, max)));

        assertFalse(list.equals(List.single(1)));
        assertFalse(list.equals(true));
        assertFalse(list.equals(null));



        assertTrue(List.list(1, 2).toString().equals("List(1,2)"));

    }

    @Test
    public void integration() {
        java.util.List<Integer> ul = Arrays.asList(1, 2, 3);
        List<Integer> dl = List.iterableList(ul);
        assertTrue(ul.equals(dl.toJavaList()));

    }

    @Test
    public void convertToString() {
        final int n = 10000;
        final StringBuilder expected = new StringBuilder("List(");
        for (int i = 0; i < n; i++) {
            expected.append(i);
            if (i < n - 1) {
                expected.append(',');
            }
        }
        expected.append(')');
        assertEquals(expected.toString(), List.range(0, n).toString());
    }

    @Test
    public void partition() {
        P2<List<Integer>, List<Integer>> p = List.range(1, 5).partition(i -> i % 2 == 0);
        Equal<List<Integer>> e = Equal.listEqual(Equal.intEqual);
        assertTrue(e.eq(p._1(), List.list(2, 4)));
        assertTrue(e.eq(p._2(), List.list(1, 3)));
    }

    @Test
    public void intersperseOverflow() {
        // should not overflow
        int n = 100000;
        List<Integer> list = List.replicate(n, 1).intersperse(2);
        String s = list.toString();
    }

    @Test
    public void listReduce() {
        String list = List.range(1, 11).uncons((a, la) -> List.cons(a, la).toString(), "");
        String expected = List.range(1, 11).toString();
        assertThat(expected, equalTo(list));
    }

    @Test
    public void array() {
        final int max = 3;
        Integer[] ints = new Integer[max];
        for (int i = 0; i < max; i++) {
            ints[i] = i + 1;
        };
        assertThat(List.range(1, max + 1).array(Integer[].class), equalTo(ints));
    }

  @Test
  public void testSequenceEither() {
    assertEquals(right(nil()), sequenceEither(nil()));
    assertEquals(right(single("zero")), sequenceEither(single(right("zero"))));
    assertEquals(left("zero"), sequenceEither(single(left("zero"))));
  }

  @Test
  public void testSequenceEitherLeft() {
    assertEquals(left(nil()), sequenceEitherLeft(nil()));
    assertEquals(left(single("zero")), sequenceEitherLeft(single(left("zero"))));
    assertEquals(right("zero"), sequenceEitherLeft(single(right("zero"))));
  }

  @Test
  public void testSequenceEitherRight() {
    assertEquals(right(nil()), sequenceEitherRight(nil()));
    assertEquals(right(single("zero")), sequenceEitherRight(single(right("zero"))));
    assertEquals(left("zero"), sequenceEitherRight(single(left("zero"))));
  }

  @Test
  public void testSequenceF() {
    assertEquals(constant(nil()).f(1), sequenceF(nil()).f(1));
    assertEquals(constant(single("zero")).f(1), sequenceF(single(constant("zero"))).f(1));
  }

  @Test
  public void testSequenceIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(nil())).run(), sequenceIO(nil()).run());
    assertEquals(IOFunctions.lazy(constant(single("zero"))).run(), sequenceIO(single(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(single(nil()), sequenceList(nil()));
    assertEquals(List.nil(), sequenceList(single(nil())));
    assertEquals(single(single("zero")), sequenceList(single(single("zero"))));
    assertEquals(List.arrayList(single("zero"), single("one")), sequenceList(single(arrayList("zero", "one"))));
  }

  @Test
  public void testSequenceOption() {
    assertEquals(some(nil()), sequenceOption(nil()));
    assertEquals(none(), sequenceOption(single(none())));
    assertEquals(some(single("zero")), sequenceOption(single(some("zero"))));
  }

  @Test
  public void testSequenceP1() {
    assertEquals(p(nil()), sequenceP1(nil()));
    assertEquals(p(single("zero")), sequenceP1(single(p("zero"))));
  }

  @Test
  public void testSequenceSeq() {
    assertEquals(Seq.single(nil()), sequenceSeq(nil()));
    assertEquals(Seq.empty(), sequenceSeq(single(Seq.empty())));
    assertEquals(Seq.single(single("zero")), sequenceSeq(single(Seq.single("zero"))));
    assertEquals(Seq.arraySeq(single("zero"), single("one")), sequenceSeq(single(Seq.arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.arraySet(listOrd(stringOrd), nil()), sequenceSet(stringOrd, nil()));
    assertEquals(Set.empty(listOrd(stringOrd)), sequenceSet(stringOrd, single(Set.empty(stringOrd))));
    assertEquals(Set.arraySet(listOrd(stringOrd), single("zero")), sequenceSet(stringOrd, single(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(listOrd(stringOrd), single("zero"), single("one")), sequenceSet(stringOrd, single(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(Stream.single(nil()), sequenceStream(nil()));
    assertEquals(Stream.nil(), sequenceStream(single(Stream.nil())));
    assertEquals(Stream.single(single("zero")), sequenceStream(single(Stream.single("zero"))));
    assertEquals(Stream.arrayStream(single("zero"), single("one")), sequenceStream(single(Stream.arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(nil()).run(), sequenceTrampoline(nil()).run());
    assertEquals(Trampoline.pure(single(0)).run(), sequenceTrampoline(single(Trampoline.pure(0))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(Validation.success(nil()), sequenceValidation(listSemigroup(), nil()));
    assertEquals(Validation.fail(single(0)), sequenceValidation(listSemigroup(), single(Validation.fail(single(0)))));
    assertEquals(Validation.success(single(0)), sequenceValidation(listSemigroup(), single(Validation.success(0))));
  }

  @Test
  public void testTraverseEitherLeft() {
    assertEquals(left(nil()), nil().traverseEitherLeft(constant(left(0))));
    assertEquals(left(single(0)), single("zero").traverseEitherLeft(constant(left(0))));
    assertEquals(left(nil()), nil().traverseEitherLeft(constant(right(0))));
    assertEquals(right(0), single("zero").traverseEitherLeft(constant(right(0))));
  }

  @Test
  public void testTraverseEitherRight() {
    assertEquals(right(nil()), nil().traverseEitherRight(constant(right(0))));
    assertEquals(right(single(0)), single("zero").traverseEitherRight(constant(right(0))));
    assertEquals(right(nil()), nil().traverseEitherRight(constant(left(0))));
    assertEquals(left(0), single("zero").traverseEitherRight(constant(left(0))));
  }

  @Test
  public void testTraverseF() {
    assertEquals(constant(nil()).f(1), nil().traverseF(constant(constant(0))).f(1));
    assertEquals(constant(single(0)).f(1), single("zero").traverseF(constant(constant(0))).f(1));
  }

  @Test
  public void testTraverseIO() throws IOException {
    assertEquals(IOFunctions.lazy(constant(nil())).run(), nil().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(IOFunctions.lazy(constant(single(0))).run(), single("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(single(nil()), nil().traverseList(constant(List.nil())));
    assertEquals(List.nil(), single("zero").traverseList(constant(List.nil())));
    assertEquals(single(nil()), nil().traverseList(constant(single(0))));
    assertEquals(single(single(0)), single("zero").traverseList(constant(single(0))));
    assertEquals(single(nil()), nil().traverseList(constant(List.arrayList(0, 1))));
    assertEquals(List.arrayList(single(0), single(1)), single("zero").traverseList(constant(List.arrayList(0, 1))));
  }

  @Test
  public void testTraverseOption() {
    assertEquals(some(nil()), nil().traverseOption(constant(none())));
    assertEquals(none(), single("zero").traverseOption(constant(none())));
    assertEquals(some(nil()), nil().traverseOption(constant(some(0))));
    assertEquals(some(single(0)), single("zero").traverseOption(constant(some(0))));
  }

  @Test
  public void testTraverseP1() {
    assertEquals(p(nil()), nil().traverseP1(constant(p(0))));
    assertEquals(p(single(0)), single("zero").traverseP1(constant(p(0))));
  }

  @Test
  public void testTraverseSeq() {
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.empty(), single("zero").traverseSeq(constant(Seq.empty())));
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(single(0)), single("zero").traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(Seq.arraySeq(0, 1))));
    assertEquals(Seq.arraySeq(single(0), single(1)), single("zero").traverseSeq(constant(Seq.arraySeq(0, 1))));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.arraySet(listOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.empty(listOrd(intOrd)), single("zero").traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.single(listOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(listOrd(intOrd), single(0)), single("zero").traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(listOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
    assertEquals(Set.arraySet(listOrd(intOrd), single(0), single(1)), single("zero").traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
  }

  @Test
  public void testTraverseStream() {
    assertEquals(Stream.single(nil()), nil().traverseStream(constant(Stream.nil())));
    assertEquals(Stream.nil(), single("zero").traverseStream(constant(Stream.nil())));
    assertEquals(Stream.single(nil()), nil().traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(single(0)), single("zero").traverseStream(constant(Stream.single(0))));
    assertEquals(Stream.single(nil()), nil().traverseStream(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.arrayStream(single(0), single(1)), single("zero").traverseStream(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testTraverseTrampoline() {
    assertEquals(Trampoline.pure(nil()).run(), nil().traverseTrampoline(constant(Trampoline.pure(0))).run());
    assertEquals(Trampoline.pure(single(0)).run(), single("zero").traverseTrampoline(constant(Trampoline.pure(0))).run());
  }

  @Test
  public void testTraverseValidation() {
    assertEquals(success(nil()), nil().traverseValidation(listSemigroup(), constant(Validation.fail(single(0)))));
    assertEquals(fail(single(0)), single("zero").traverseValidation(listSemigroup(), constant(Validation.fail(single(0)))));
    assertEquals(success(nil()), nil().traverseValidation(listSemigroup(), constant(Validation.success(0))));
    assertEquals(success(single(0)), single("zero").traverseValidation(listSemigroup(), constant(Validation.success(0))));
  }
}
