package fj.data;

import fj.Equal;
import fj.P2;
import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;
import java.util.ConcurrentModificationException;

import static fj.Function.constant;
import static fj.Ord.*;
import static fj.P.p;
import static fj.Semigroup.listSemigroup;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.arrayList;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Seq.arraySeq;
import static fj.data.Seq.empty;
import static fj.data.Stream.sequenceEitherLeft;
import static fj.data.Stream.sequenceEitherRight;
import static fj.data.Stream.sequenceF;
import static fj.data.Stream.sequenceIO;
import static fj.data.Stream.sequenceList;
import static fj.data.Stream.sequenceOption;
import static fj.data.Stream.sequenceP1;
import static fj.data.Stream.sequenceSeq;
import static fj.data.Stream.sequenceSet;
import static fj.data.Stream.sequenceStream;
import static fj.data.Stream.sequenceTrampoline;
import static fj.data.Stream.sequenceValidation;
import static fj.data.Stream.*;
import static fj.data.Validation.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Zheka Kozlov on 27.05.2015.
 */
public class StreamTest {

  @Test
  public void infiniteStream() {
    Stream<Integer> s = Stream.forever(Enumerator.intEnumerator, 0).bind(Stream::single);
    assertThat(List.range(0, 5), is(s.take(5).toList()));
  }

  @Test
  public void testToString() {
    Stream<Integer> range = Stream.range(1);
    assertThat(range.toString(), is(equalTo("Cons(1, ?)")));
  }

    /**
     * This test demonstrates the known problem of creating streams from mutable structures.
     *
     * Some of the ways streams created in this way can fail is:
     * - weak stream references getting garbage collected
     * - underlying mutable data structure changes
     * - iterator gets updated (e.g. iterator used to create 2 different streams).
     */
    @Test(expected = ConcurrentModificationException.class)
    public void iterableStreamWithStructureUpdate() {
        java.util.List<Integer> list = List.list(1, 2, 3).toJavaList();
        Stream<Integer> s1 = Stream.iterableStream(list);
        int x = s1.head();
        list.remove(1);
        Stream<Integer> s2 = s1.tail()._1();
        x = s2.head();
    }

    @Test(expected=Error.class)
    public void testCycleNil(){
        cycle(Stream.nil());
    }

    @Test
    public void testCycle() {
        Stream<Character> s = stream(new Character[]{'a', 'b'});
        assertThat(cycle(s).take(5),
                is(stream(new Character[]{'a', 'b', 'a', 'b', 'a'})));
    }

    @Test
    public void testIterate() {
        assertThat(iterate(a -> 2 * a + 1, 1).take(5),
                is(stream(new Integer[]{1, 3, 7, 15, 31})));
    }

    @Test
    public void testArrayStreamEmpty() {
        assertThat(arrayStream(new Integer[]{}), is(Stream.nil()));
    }

    @Test(expected=Error.class)
    public void testNilHead() {
        Stream.nil().head();
    }

    @Test(expected=Error.class)
    public void testNilTail() {
        Stream.nil().tail();
    }

    @Test
    public void testArray() {
        Character[] a = new Character[]{'a', 'b', 'c'};
        Stream<Character> s = stream(a);
        assertThat(s.array(Character[].class), is(a));
    }

    @Test
    public void testZipIndex() {
        Character[] a = new Character[]{'a', 'b', 'c'};
        P2<Stream<Character>, Stream<Integer>> p = unzip(stream(a).zipIndex().drop(1));
        assertThat(p._1(), is(stream(new Character[]{'b', 'c'})));
        assertThat(p._2(), is(stream(new Integer[]{1, 2})));
    }

    @Test
    public void testMinus() {
        Character[] a1 = new Character[]{'a', 'b', 'c', 'd', 'e'};
        Stream<Character> s1 = stream(a1);
        Character[] a2 = new Character[]{'b', 'e'};
        Stream<Character> s2 = stream(a2);
        assertThat(s1.minus(Equal.charEqual, s2),
                is(stream(new Character[]{'a', 'c', 'd'})));
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
  public void testSequenceIO()
          throws IOException {
    assertEquals(IOFunctions.lazy(constant(nil())).run(), sequenceIO(nil()).run());
    assertEquals(IOFunctions.lazy(constant(single("zero"))).run(), sequenceIO(single(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(List.single(nil()), sequenceList(nil()));
    assertEquals(List.nil(), sequenceList(single(List.nil())));
    assertEquals(List.single(single("zero")), sequenceList(single(List.single("zero"))));
    assertEquals(arrayList(single("zero"), single("one")), sequenceList(single(arrayList("zero", "one"))));
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
    assertEquals(arraySeq(single("zero"), single("one")), sequenceSeq(single(arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.arraySet(streamOrd(stringOrd), nil()), sequenceSet(stringOrd, nil()));
    assertEquals(Set.empty(streamOrd(stringOrd)), sequenceSet(stringOrd, single(Set.empty(stringOrd))));
    assertEquals(Set.arraySet(streamOrd(stringOrd), single("zero")), sequenceSet(stringOrd, single(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(streamOrd(stringOrd), single("zero"), single("one")), sequenceSet(stringOrd, single(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(single(nil()), sequenceStream(nil()));
    assertEquals(nil(), sequenceStream(single(nil())));
    assertEquals(single(single("zero")), sequenceStream(single(single("zero"))));
    assertEquals(arrayStream(single("zero"), single("one")), sequenceStream(single(arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(nil()).run(), sequenceTrampoline(nil()).run());
    assertEquals(Trampoline.pure(single(0)).run(), sequenceTrampoline(single(Trampoline.pure(0))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(success(nil()), sequenceValidation(nil()));
    assertEquals(fail(single(0)), sequenceValidation(single(fail(single(0)))));
    assertEquals(success(single(0)), sequenceValidation(single(success(0))));
  }

  @Test
  public void testSequenceValidationSemigroup() {
    assertEquals(success(nil()), sequenceValidation(listSemigroup(), nil()));
    assertEquals(fail(List.single(0)), sequenceValidation(listSemigroup(), single(fail(List.single(0)))));
    assertEquals(success(single(0)), sequenceValidation(listSemigroup(), single(success(0))));
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
  public void testTraverseIO()
          throws IOException {
    assertEquals(IOFunctions.lazy(constant(nil())).run(), nil().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(IOFunctions.lazy(constant(single(0))).run(), single("zero").traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(List.single(nil()), nil().traverseList(constant(List.nil())));
    assertEquals(List.nil(), single("zero").traverseList(constant(List.nil())));
    assertEquals(List.single(nil()), nil().traverseList(constant(List.single(0))));
    assertEquals(List.single(single(0)), single("zero").traverseList(constant(List.single(0))));
    assertEquals(List.single(nil()), nil().traverseList(constant(arrayList(0, 1))));
    assertEquals(arrayList(single(0), single(1)), single("zero").traverseList(constant(arrayList(0, 1))));
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
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(empty())));
    assertEquals(Seq.empty(), single("zero").traverseSeq(constant(empty())));
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(single(0)), single("zero").traverseSeq(constant(Seq.single(0))));
    assertEquals(Seq.single(nil()), nil().traverseSeq(constant(arraySeq(0, 1))));
    assertEquals(arraySeq(single(0), single(1)), single("zero").traverseSeq(constant(arraySeq(0, 1))));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.arraySet(streamOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.empty(streamOrd(intOrd)), single("zero").traverseSet(intOrd, constant(Set.empty(intOrd))));
    assertEquals(Set.single(streamOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(streamOrd(intOrd), single(0)), single("zero").traverseSet(intOrd, constant(Set.single(intOrd, 0))));
    assertEquals(Set.single(streamOrd(intOrd), nil()), nil().traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
    assertEquals(Set.arraySet(streamOrd(intOrd), single(0), single(1)), single("zero").traverseSet(intOrd, constant(Set.arraySet(intOrd, 0, 1))));
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
    assertEquals(success(nil()), nil().traverseValidation(constant(fail(single(0)))));
    assertEquals(fail(single(0)), single("zero").traverseValidation(constant(fail(single(0)))));
    assertEquals(success(nil()), nil().traverseValidation(constant(success(0))));
    assertEquals(success(single(0)), single("zero").traverseValidation(constant(success(0))));

    assertEquals(success(arraySeq(0, 2, 4, 6, 8)), arraySeq(0, 2, 4, 6, 8).traverseValidation(i -> condition(i % 2 == 0, List.single(i), i)));
    assertEquals(fail(List.single(1)), arraySeq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).traverseValidation(i -> condition(i % 2 == 0, List.single(i), i)));
  }

  @Test
  public void testTraverseValidationSemigroup() {
    assertEquals(success(nil()), nil().traverseValidation(listSemigroup(), constant(fail(List.single(0)))));
    assertEquals(fail(List.single(0)), single("zero").traverseValidation(listSemigroup(), constant(fail(List.single(0)))));
    assertEquals(success(nil()), nil().traverseValidation(listSemigroup(), constant(success(0))));
    assertEquals(success(single(0)), single("zero").traverseValidation(listSemigroup(), constant(success(0))));

    assertEquals(success(arraySeq(0, 2, 4, 6, 8)), arraySeq(0, 2, 4, 6, 8).traverseValidation(listSemigroup(), i -> condition(i % 2 == 0, List.single(i), i)));
    assertEquals(fail(arrayList(1, 3, 5, 7, 9)), arraySeq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9).traverseValidation(listSemigroup(), i -> condition(i % 2 == 0, List.single(i), i)));
  }
}
