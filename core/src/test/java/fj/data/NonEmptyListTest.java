package fj.data;

import fj.control.Trampoline;
import org.junit.Test;

import java.io.IOException;

import static fj.Function.constant;
import static fj.Ord.*;
import static fj.P.p;
import static fj.Semigroup.nonEmptyListSemigroup;
import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.List.arrayList;
import static fj.data.NonEmptyList.*;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Stream.nil;
import static org.junit.Assert.assertEquals;

public class NonEmptyListTest {
  @Test
  public void testSequenceEither() {
    assertEquals(right(nel("zero")), sequenceEither(nel(right("zero"))));
    assertEquals(left("zero"), sequenceEither(nel(left("zero"))));

    assertEquals(right(nel("zero", "one")), sequenceEither(nel(right("zero"), right("one"))));
    assertEquals(left("one"), sequenceEither(nel(right("zero"), left("one"))));
    assertEquals(left("zero"), sequenceEither(nel(left("zero"), right("one"))));
    assertEquals(left("zero"), sequenceEither(nel(left("zero"), left("one"))));
  }

  @Test
  public void testSequenceEitherLeft() {
    assertEquals(right("zero"), sequenceEitherLeft(nel(right("zero"))));
    assertEquals(left(nel("zero")), sequenceEitherLeft(nel(left("zero"))));

    assertEquals(right("zero"), sequenceEitherLeft(nel(right("zero"), right("one"))));
    assertEquals(right("zero"), sequenceEitherLeft(nel(right("zero"), left("one"))));
    assertEquals(right("one"), sequenceEitherLeft(nel(left("zero"), right("one"))));
    assertEquals(left(nel("zero", "one")), sequenceEitherLeft(nel(left("zero"), left("one"))));
  }

  @Test
  public void testSequenceEitherRight() {
    assertEquals(right(nel("zero")), sequenceEitherRight(nel(right("zero"))));
    assertEquals(left("zero"), sequenceEitherRight(nel(left("zero"))));

    assertEquals(right(nel("zero", "one")), sequenceEitherRight(nel(right("zero"), right("one"))));
    assertEquals(left("one"), sequenceEitherRight(nel(right("zero"), left("one"))));
    assertEquals(left("zero"), sequenceEitherRight(nel(left("zero"), right("one"))));
    assertEquals(left("zero"), sequenceEitherRight(nel(left("zero"), left("one"))));
  }

  @Test
  public void testSequenceF() {
    assertEquals(constant(nel("zero")).f(1), sequenceF(nel(constant("zero"))).f(1));
    assertEquals(constant(nel("zero", "one")).f(1), sequenceF(nel(constant("zero"), constant("one"))).f(1));
  }

  @Test
  public void testSequenceIO()
          throws IOException {
    assertEquals(IOFunctions.lazy(constant(nel("zero"))).run(), sequenceIO(nel(IOFunctions.lazy(constant("zero")))).run());
  }

  @Test
  public void testSequenceList() {
    assertEquals(arrayList(nel("zero")), sequenceList(nel(arrayList("zero"))));
    assertEquals(arrayList(nel("zero"), nel("one")), sequenceList(nel(arrayList("zero", "one"))));
  }

  @Test
  public void testSequenceOption() {
    assertEquals(none(), sequenceOption(nel(none())));
    assertEquals(some(nel("zero")), sequenceOption(nel(some("zero"))));
  }

  @Test
  public void testSequenceP1() {
    assertEquals(p(nel("zero")), sequenceP1(nel(p("zero"))));
  }

  @Test
  public void testSequenceSeq() {
    assertEquals(Seq.empty(), sequenceSeq(nel(Seq.empty())));
    assertEquals(Seq.single(nel("zero")), sequenceSeq(nel(Seq.single("zero"))));
    assertEquals(Seq.arraySeq(nel("zero"), nel("one")), sequenceSeq(nel(Seq.arraySeq("zero", "one"))));
  }

  @Test
  public void testSequenceSet() {
    assertEquals(Set.empty(nonEmptyListOrd(stringOrd)), sequenceSet(stringOrd, nel(Set.empty(stringOrd))));
    assertEquals(Set.arraySet(nonEmptyListOrd(stringOrd), nel("zero")), sequenceSet(stringOrd, nel(Set.single(stringOrd, "zero"))));
    assertEquals(Set.arraySet(nonEmptyListOrd(stringOrd), nel("zero"), nel("one")), sequenceSet(stringOrd, nel(Set.arraySet(stringOrd, "zero", "one"))));
  }

  @Test
  public void testSequenceStream() {
    assertEquals(nil(), sequenceStream(nel(nil())));
    assertEquals(Stream.single(nel("zero")), sequenceStream(nel(Stream.single("zero"))));
    assertEquals(Stream.arrayStream(nel("zero"), nel("one")), sequenceStream(nel(Stream.arrayStream("zero", "one"))));
  }

  @Test
  public void testSequenceTrampoline() {
    assertEquals(Trampoline.pure(nel(0)).run(), sequenceTrampoline(nel(Trampoline.pure(0))).run());
    assertEquals(Trampoline.pure(nel(0, 1)).run(), sequenceTrampoline(nel(Trampoline.pure(0), Trampoline.pure(1))).run());
  }

  @Test
  public void testSequenceValidation() {
    assertEquals(Validation.success(nel(0)), sequenceValidation(nel(Validation.success(0))));
    assertEquals(Validation.fail(nel(1)), sequenceValidation(nel(Validation.fail(nel(1)))));

    assertEquals(Validation.success(nel(0, 1)), sequenceValidation(nel(Validation.success(0), Validation.success(1))));
    assertEquals(Validation.fail(nel(1)), sequenceValidation(nel(Validation.success(0), Validation.fail(nel(1)))));
    assertEquals(Validation.fail(nel(0)), sequenceValidation(nel(Validation.fail(nel(0)), Validation.success(1))));
    assertEquals(Validation.fail(nel(0)), sequenceValidation(nel(Validation.fail(nel(0)), Validation.fail(nel(1)))));
  }

  @Test
  public void testSequenceValidationSemigroup() {
    assertEquals(Validation.success(nel(0)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.success(0))));
    assertEquals(Validation.fail(nel(1)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.fail(nel(1)))));

    assertEquals(Validation.success(nel(0, 1)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.success(0), Validation.success(1))));
    assertEquals(Validation.fail(nel(1)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.success(0), Validation.fail(nel(1)))));
    assertEquals(Validation.fail(nel(0)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.fail(nel(0)), Validation.success(1))));
    assertEquals(Validation.fail(nel(0, 1)), sequenceValidation(nonEmptyListSemigroup(), nel(Validation.fail(nel(0)), Validation.fail(nel(1)))));
  }

  @Test
  public void testTraverseEither() {
    assertEquals(right(nel(4)), nel(Either.<String, String>right("zero")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));

    assertEquals(right(nel(4, 3)), nel(Either.<String, String>right("zero"), Either.<String, String>right("one")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(3), nel(Either.<String, String>right("zero"), Either.<String, String>left("one")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero"), Either.<String, String>right("one")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero"), Either.<String, String>left("one")).traverseEither(either -> either.bimap(o -> o.length(), o -> o.length())));
  }

  @Test
  public void testTraverseEitherLeft() {
    assertEquals(right(4), nel(Either.<String, String>right("zero")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(nel(4)), nel(Either.<String, String>left("zero")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));

    assertEquals(right(4), nel(Either.<String, String>right("zero"), Either.<String, String>right("one")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(right(4), nel(Either.<String, String>right("zero"), Either.<String, String>left("one")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(right(3), nel(Either.<String, String>left("zero"), Either.<String, String>right("one")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(nel(4, 3)), nel(Either.<String, String>left("zero"), Either.<String, String>left("one")).traverseEitherLeft(either -> either.bimap(o -> o.length(), o -> o.length())));
  }

  @Test
  public void testTraverseEitherRight() {
    assertEquals(right(nel(4)), nel(Either.<String, String>right("zero")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));

    assertEquals(right(nel(4, 3)), nel(Either.<String, String>right("zero"), Either.<String, String>right("one")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(3), nel(Either.<String, String>right("zero"), Either.<String, String>left("one")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero"), Either.<String, String>right("one")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));
    assertEquals(left(4), nel(Either.<String, String>left("zero"), Either.<String, String>left("one")).traverseEitherRight(either -> either.bimap(o -> o.length(), o -> o.length())));
  }

  @Test
  public void testTraverseF() {
    assertEquals(constant(nel("two")).f(1), nel(constant("zero")).traverseF(constant(constant("two"))).f(1));
    assertEquals(constant(nel("two", "two")).f(1), nel(constant("zero"), constant("one")).traverseF(constant(constant("two"))).f(1));
  }

  @Test
  public void testTraverseIO()
          throws IOException {
    assertEquals(IOFunctions.lazy(constant(nel("one"))).run(), nel(IOFunctions.lazy(constant("zero"))).traverseIO(constant(IOFunctions.lazy(constant("one")))).run());
  }

  @Test
  public void testTraverseList() {
    assertEquals(arrayList(nel("two")), nel(arrayList("zero")).traverseList(list -> list.map(constant("two"))));
    assertEquals(arrayList(nel("two"), nel("two")), nel(arrayList("zero", "one")).traverseList(list -> list.map(constant("two"))));
  }

  @Test
  public void testTraverseOption() {
    assertEquals(none(), nel(Option.<String>none()).traverseOption(option -> option.map(o -> o.length())));
    assertEquals(some(nel(4)), nel(some("zero")).traverseOption(option -> option.map(o -> o.length())));
  }

  @Test
  public void testTraverseP1() {
    assertEquals(p(nel(4)), nel(p("zero")).traverseP1(p -> p.map(o -> o.length())));
  }

  @Test
  public void testTraverseSeq() {
    assertEquals(Seq.empty(), nel(Seq.<String>empty()).traverseSeq(seq -> seq.map(o -> o.length())));
    assertEquals(Seq.single(nel(4)), nel(Seq.single("zero")).traverseSeq(seq -> seq.map(o -> o.length())));
    assertEquals(Seq.arraySeq(nel(4), nel(3)), nel(Seq.arraySeq("zero", "one")).traverseSeq(seq -> seq.map(o -> o.length())));
  }

  @Test
  public void testTraverseSet() {
    assertEquals(Set.empty(nonEmptyListOrd(intOrd)), nel(Set.empty(stringOrd)).traverseSet(intOrd, set -> set.map(intOrd, o -> o.length())));
    assertEquals(Set.arraySet(nonEmptyListOrd(intOrd), nel(4)), nel(Set.single(stringOrd, "zero")).traverseSet(intOrd, set -> set.map(intOrd, o -> o.length())));
    assertEquals(Set.arraySet(nonEmptyListOrd(intOrd), nel(4), nel(3)), nel(Set.arraySet(stringOrd, "zero", "one")).traverseSet(intOrd, set -> set.map(intOrd, o -> o.length())));
  }

  @Test
  public void testTraverseStream() {
    assertEquals(Stream.single(nel(4)), nel(Stream.single("zero")).traverseStream(stream -> stream.map(o -> o.length())));
    assertEquals(Stream.arrayStream(nel(4), nel(3)), nel(Stream.arrayStream("zero", "one")).traverseStream(stream -> stream.map(o -> o.length())));
  }

  @Test
  public void testTraverseTrampoline() {
    assertEquals(Trampoline.pure(nel(1)).run(), nel(Trampoline.pure(0)).traverseTrampoline(trampoline -> trampoline.map(i -> i + 1)).run());
    assertEquals(Trampoline.pure(nel(1, 2)).run(), nel(Trampoline.pure(0), Trampoline.pure(1)).traverseTrampoline(trampoline -> trampoline.map(i -> i + 1)).run());
  }

  @Test
  public void testTraverseValidation() {
    assertEquals(Validation.success(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0)).traverseValidation(validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(validation -> validation.map(i -> i + 1)));

    assertEquals(Validation.success(nel(1, 2)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0), Validation.success(1)).traverseValidation(validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0), Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(0)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(0)), Validation.<NonEmptyList<Integer>, Integer>success(1)).traverseValidation(validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(0)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(0)), Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(validation -> validation.map(i -> i + 1)));
  }

  @Test
  public void testTraverseValidationSemigroup() {
    assertEquals(Validation.success(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0)).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));

    assertEquals(Validation.success(nel(1, 2)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0), Validation.<NonEmptyList<Integer>, Integer>success(1)).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(1)), nel(Validation.<NonEmptyList<Integer>, Integer>success(0), Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(0)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(0)), Validation.<NonEmptyList<Integer>, Integer>success(1)).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));
    assertEquals(Validation.fail(nel(0, 1)), nel(Validation.<NonEmptyList<Integer>, Integer>fail(nel(0)), Validation.<NonEmptyList<Integer>, Integer>fail(nel(1))).traverseValidation(nonEmptyListSemigroup(), validation -> validation.map(i -> i + 1)));
  }

}
