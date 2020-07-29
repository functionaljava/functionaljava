package fj.data;

import org.junit.Test;

import java.io.IOException;

import static fj.Function.constant;
import static fj.P.p;
import static fj.Unit.unit;
import static fj.data.Either.*;
import static org.junit.Assert.*;

public final class EitherTest {

  public static final class LeftProjectionTest {
    @Test
    public void testIterator() {
      assertEquals(0L, (long) left(0L).left().iterator().next());
      assertFalse(right(0).left().iterator().hasNext());
    }

    @Test
    public void testEither() {
      assertEquals(left(0), left(0).left().either());
      assertEquals(right(0), right(0).left().either());
    }

    @Test
    public void testValueEString() {
      assertEquals(0L, (long) left(0L).left().valueE("zero"));

      try {
        right(0L).left().valueE("zero");
        fail();
      } catch (final Error error) {
        assertEquals("zero", error.getMessage());
      }
    }

    @Test
    public void testValueEF0() {
      assertEquals(0L, (long) left(0L).left().valueE(() -> "zero"));

      try {
        right(0L).left().valueE(() -> "zero");
        fail();
      } catch (final Error error) {
        assertEquals("zero", error.getMessage());
      }
    }

    @Test
    public void testValue() {
      assertEquals(0L, (long) left(0L).left().value());

      try {
        right(0L).left().value();
        fail();
      } catch (final Error error) {
        // pass
      }
    }

    @Test
    public void testOrValue() {
      assertEquals(0L, (long) left(0L).left().orValue(1L));
      assertEquals(1L, (long) right(0L).left().orValue(1L));
    }

    @Test
    public void testOrValueF0() {
      assertEquals(0L, (long) left(0L).left().orValue(() -> 1L));
      assertEquals(1L, (long) right(0L).left().orValue(() -> 1L));
    }

    @Test
    public void testOn() {
      assertEquals(0L, (long) Either.<Long, Long>left(0L).left().on(constant(1L)));
      assertEquals(1L, (long) Either.<Long, Long>right(0L).left().on(constant(1L)));
    }

    @Test
    public void testForeach() {
      left(0).left().foreach(constant(unit()));
      right(0).left().foreach(ignore -> {
        fail();
        return unit();
      });
    }

    @Test
    public void testForeachDoEffect() {
      left(0).left().foreachDoEffect(ignore -> {
      });
      right(0).left().foreachDoEffect(ignore -> fail());
    }

    @Test
    public void testMap() {
      assertEquals(left(0), left("zero").left().map(constant(0)));
      assertEquals(right("zero"), right("zero").left().map(constant(0)));
    }

    @Test
    public void testBind() {
      assertEquals(left(0), left("zero").left().bind(constant(left(0))));
      assertEquals(right("zero"), right("zero").left().bind(constant(left(0))));
    }

    @Test
    public void testSequence() {
      assertEquals(left(0), left("zero").left().sequence(left(0)));
      assertEquals(right(0), left("zero").left().sequence(right(0)));
      assertEquals(right("zero"), right("zero").left().sequence(left(0)));
      assertEquals(right("zero"), right("zero").left().sequence(right("one")));
    }

    @Test
    public void testTraverseList() {
      assertEquals(List.nil(), left("zero").left().traverseList(constant(List.nil())));
      assertEquals(List.single(left(0)), left("zero").left().traverseList(constant(List.single(0))));
      assertEquals(List.arrayList(left(0), left(1)), left("zero").left().traverseList(constant(List.arrayList(0, 1))));
      assertEquals(List.single(right("zero")), right("zero").left().traverseList(constant(List.nil())));
      assertEquals(List.single(right("zero")), right("zero").left().traverseList(constant(List.single(0))));
      assertEquals(List.single(right("zero")), right("zero").left().traverseList(constant(List.arrayList(0, 1))));
    }

    @Test
    public void testTraverseIO() throws IOException {
      assertEquals(left(0), left("zero").left().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
      assertEquals(right("zero"), right("zero").left().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    }

    @Test
    public void testTraverseP1() {
      assertEquals(p(left(0)), left("zero").left().traverseP1(constant(p(0))));
      assertEquals(p(right("zero")), right("zero").left().traverseP1(constant(p(0))));
    }

    @Test
    public void testFilter() {
      assertEquals(Option.none(), left(0).left().filter(constant(false)));
      assertEquals(Option.none(), right(0).left().filter(constant(false)));
      assertEquals(Option.some(left(0)), left(0).left().filter(constant(true)));
      assertEquals(Option.none(), right(0).left().filter(constant(true)));
    }

    @Test
    public void testApply() {
      assertEquals(left(1), left("zero").left().apply(left(constant(1))));
      assertEquals(right("zero"), right("zero").left().apply(left(constant(1))));
    }

    @Test
    public void testForAll() {
      assertFalse(left(0).left().forall(constant(false)));
      assertTrue(right(0).left().forall(constant(false)));
      assertTrue(left(0).left().forall(constant(true)));
      assertTrue(right(0).left().forall(constant(true)));
    }

    @Test
    public void testExists() {
      assertFalse(left(0).left().exists(constant(false)));
      assertFalse(right(0).left().exists(constant(false)));
      assertTrue(left(0).left().exists(constant(true)));
      assertFalse(right(0).left().exists(constant(true)));
    }

    @Test
    public void testToList() {
      assertEquals(List.single(0), left(0).left().toList());
      assertEquals(List.nil(), right(0).left().toList());
    }

    @Test
    public void testToOption() {
      assertEquals(Option.some(0), left(0).left().toOption());
      assertEquals(Option.none(), right(0).left().toOption());
    }

    @Test
    public void testToArray() {
      assertEquals(Array.single(0), left(0).left().toArray());
      assertEquals(Array.empty(), right(0).left().toArray());
    }

    @Test
    public void testToStream() {
      assertEquals(Stream.single(0), left(0).left().toStream());
      assertEquals(Stream.nil(), right(0).left().toStream());
    }

    @Test
    public void testToCollection() {
      assertEquals(1L, left(0L).left().toCollection().size());
      assertEquals(0L, (long) left(0L).left().toCollection().iterator().next());
      assertTrue(right(0).left().toCollection().isEmpty());
    }

    @Test
    public void testTraverseOption() {
      assertEquals(Option.none(), left("zero").left().traverseOption(constant(Option.none())));
      assertEquals(Option.some(left(0)), left("zero").left().traverseOption(constant(Option.some(0))));
      assertEquals(Option.some(right("zero")), right("zero").left().traverseOption(constant(Option.none())));
      assertEquals(Option.some(right("zero")), right("zero").left().traverseOption(constant(Option.some(0))));
    }

    @Test
    public void testTraverseStream() {
      assertEquals(Stream.nil(), left("zero").left().traverseStream(constant(Stream.nil())));
      assertEquals(Stream.single(left(0)), left("zero").left().traverseStream(constant(Stream.single(0))));
      assertEquals(Stream.arrayStream(left(0), left(1)), left("zero").left().traverseStream(constant(Stream.arrayStream(0, 1))));
      assertEquals(Stream.single(right("zero")), right("zero").left().traverseStream(constant(Stream.nil())));
      assertEquals(Stream.single(right("zero")), right("zero").left().traverseStream(constant(Stream.single(0))));
      assertEquals(Stream.single(right("zero")), right("zero").left().traverseStream(constant(Stream.arrayStream(0, 1))));
    }
  }

  public static final class RightProjectionTest {
    @Test
    public void testIterator() {
      assertEquals(0L, (long) right(0L).right().iterator().next());
      assertFalse(left(0).right().iterator().hasNext());
    }

    @Test
    public void testEither() {
      assertEquals(right(0), right(0).right().either());
      assertEquals(left(0), left(0).right().either());
    }

    @Test
    public void testValueEString() {
      assertEquals(0L, (long) right(0L).right().valueE("zero"));

      try {
        left(0L).right().valueE("zero");
        fail();
      } catch (final Error error) {
        assertEquals("zero", error.getMessage());
      }
    }

    @Test
    public void testValueEF0() {
      assertEquals(0L, (long) right(0L).right().valueE(() -> "zero"));

      try {
        left(0L).right().valueE(() -> "zero");
        fail();
      } catch (final Error error) {
        assertEquals("zero", error.getMessage());
      }
    }

    @Test
    public void testValue() {
      assertEquals(0L, (long) right(0L).right().value());

      try {
        left(0L).right().value();
        fail();
      } catch (final Error error) {
        // pass
      }
    }

    @Test
    public void testOrValue() {
      assertEquals(0L, (long) right(0L).right().orValue(1L));
      assertEquals(1L, (long) left(0L).right().orValue(1L));
    }

    @Test
    public void testOrValueF0() {
      assertEquals(0L, (long) right(0L).right().orValue(() -> 1L));
      assertEquals(1L, (long) left(0L).right().orValue(() -> 1L));
    }

    @Test
    public void testOn() {
      assertEquals(0L, (long) Either.<Long, Long>right(0L).right().on(constant(1L)));
      assertEquals(1L, (long) Either.<Long, Long>left(0L).right().on(constant(1L)));
    }

    @Test
    public void testForeach() {
      right(0).right().foreach(constant(unit()));
      left(0).right().foreach(ignore -> {
        fail();
        return unit();
      });
    }

    @Test
    public void testForeachDoEffect() {
      right(0).right().foreachDoEffect(ignore -> {
      });
      left(0).right().foreachDoEffect(ignore -> fail());
    }

    @Test
    public void testMap() {
      assertEquals(right(0), right("zero").right().map(constant(0)));
      assertEquals(left("zero"), left("zero").right().map(constant(0)));
    }

    @Test
    public void testBind() {
      assertEquals(right(0), right("zero").right().bind(constant(right(0))));
      assertEquals(left("zero"), left("zero").right().bind(constant(right(0))));
    }

    @Test
    public void testSequence() {
      assertEquals(right(0), right("zero").right().sequence(right(0)));
      assertEquals(left(0), right("zero").right().sequence(left(0)));
      assertEquals(left("zero"), left("zero").right().sequence(right(0)));
      assertEquals(left("zero"), left("zero").right().sequence(left("one")));
    }

    @Test
    public void testTraverseList() {
      assertEquals(List.nil(), right("zero").right().traverseList(constant(List.nil())));
      assertEquals(List.single(right(0)), right("zero").right().traverseList(constant(List.single(0))));
      assertEquals(List.arrayList(right(0), right(1)), right("zero").right().traverseList(constant(List.arrayList(0, 1))));
      assertEquals(List.single(left("zero")), left("zero").right().traverseList(constant(List.nil())));
      assertEquals(List.single(left("zero")), left("zero").right().traverseList(constant(List.single(0))));
      assertEquals(List.single(left("zero")), left("zero").right().traverseList(constant(List.arrayList(0, 1))));
    }

    @Test
    public void testTraverseIO() throws IOException {
      assertEquals(right(0), right("zero").right().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
      assertEquals(left("zero"), left("zero").right().traverseIO(constant(IOFunctions.lazy(constant(0)))).run());
    }

    @Test
    public void testTraverseP1() {
      assertEquals(p(right(0)), right("zero").right().traverseP1(constant(p(0))));
      assertEquals(p(left("zero")), left("zero").right().traverseP1(constant(p(0))));
    }

    @Test
    public void testFilter() {
      assertEquals(Option.none(), right(0).right().filter(constant(false)));
      assertEquals(Option.none(), left(0).right().filter(constant(false)));
      assertEquals(Option.some(right(0)), right(0).right().filter(constant(true)));
      assertEquals(Option.none(), left(0).right().filter(constant(true)));
    }

    @Test
    public void testApply() {
      assertEquals(right(1), right("zero").right().apply(right(constant(1))));
      assertEquals(left("zero"), left("zero").right().apply(right(constant(1))));
    }

    @Test
    public void testForAll() {
      assertFalse(right(0).right().forall(constant(false)));
      assertTrue(left(0).right().forall(constant(false)));
      assertTrue(right(0).right().forall(constant(true)));
      assertTrue(left(0).right().forall(constant(true)));
    }

    @Test
    public void testExists() {
      assertFalse(right(0).right().exists(constant(false)));
      assertFalse(left(0).right().exists(constant(false)));
      assertTrue(right(0).right().exists(constant(true)));
      assertFalse(left(0).right().exists(constant(true)));
    }

    @Test
    public void testToList() {
      assertEquals(List.single(0), right(0).right().toList());
      assertEquals(List.nil(), left(0).right().toList());
    }

    @Test
    public void testToOption() {
      assertEquals(Option.some(0), right(0).right().toOption());
      assertEquals(Option.none(), left(0).right().toOption());
    }

    @Test
    public void testToArray() {
      assertEquals(Array.single(0), right(0).right().toArray());
      assertEquals(Array.empty(), left(0).right().toArray());
    }

    @Test
    public void testToStream() {
      assertEquals(Stream.single(0), right(0).right().toStream());
      assertEquals(Stream.nil(), left(0).right().toStream());
    }

    @Test
    public void testToCollection() {
      assertEquals(1L, right(0L).right().toCollection().size());
      assertEquals(0L, (long) right(0L).right().toCollection().iterator().next());
      assertTrue(left(0).right().toCollection().isEmpty());
    }

    @Test
    public void testTraverseOption() {
      assertEquals(Option.none(), right("zero").right().traverseOption(constant(Option.none())));
      assertEquals(Option.some(right(0)), right("zero").right().traverseOption(constant(Option.some(0))));
      assertEquals(Option.some(left("zero")), left("zero").right().traverseOption(constant(Option.none())));
      assertEquals(Option.some(left("zero")), left("zero").right().traverseOption(constant(Option.some(0))));
    }

    @Test
    public void testTraverseStream() {
      assertEquals(Stream.nil(), right("zero").right().traverseStream(constant(Stream.nil())));
      assertEquals(Stream.single(right(0)), right("zero").right().traverseStream(constant(Stream.single(0))));
      assertEquals(Stream.arrayStream(right(0), right(1)), right("zero").right().traverseStream(constant(Stream.arrayStream(0, 1))));
      assertEquals(Stream.single(left("zero")), left("zero").right().traverseStream(constant(Stream.nil())));
      assertEquals(Stream.single(left("zero")), left("zero").right().traverseStream(constant(Stream.single(0))));
      assertEquals(Stream.single(left("zero")), left("zero").right().traverseStream(constant(Stream.arrayStream(0, 1))));
    }
  }

  @Test
  public void testIsLeft() {
    assertTrue(left(0).isLeft());
    assertFalse(right(0).isLeft());
  }

  @Test
  public void testIsRight() {
    assertFalse(left(0).isRight());
    assertTrue(right(0).isRight());
  }

  @Test
  public void testEither() {
    assertEquals(-1L, (long) left("zero").either(constant(-1L), constant(1L)));
    assertEquals(1L, (long) right("zero").either(constant(-1L), constant(1L)));
  }

  @Test
  public void testBimap() {
    assertEquals(left(-1), left("zero").bimap(constant(-1), constant(1)));
    assertEquals(right(1), right("zero").bimap(constant(-1), constant(1)));
  }

  @Test
  public void testTestEquals() {
    assertNotEquals(null, left(0));
    assertNotEquals(new Object(), left(0));
    assertNotEquals(left(0), right(0));
    assertEquals(left(0), left(0));

    assertNotEquals(null, right(0));
    assertNotEquals(new Object(), right(0));
    assertEquals(right(0), right(0));
    assertNotEquals(right(0), left(0));
  }

  @Test
  public void testTestHashCode() {
    assertEquals(left(0).hashCode(), left(0).hashCode());
    assertEquals(left(0).hashCode(), right(0).hashCode());
    assertEquals(right(0).hashCode(), left(0).hashCode());
    assertEquals(right(0).hashCode(), left(0).hashCode());
  }

  @Test
  public void testSwap() {
    assertEquals(right(0), left(0).swap());
    assertEquals(left(0), right(0).swap());
  }

  @Test
  public void testLeft_() {
    assertEquals(left(0), left_().f(0));
  }

  @Test
  public void testRight_() {
    assertEquals(right(0), right_().f(0));
  }

  @Test
  public void testEither_() {
    assertEquals(-1L, (long) either_(constant(-1L), constant(1L)).f(left("zero")));
    assertEquals(1L, (long) either_(constant(-1L), constant(1L)).f(right("zero")));
  }

  @Test
  public void testLeftMap() {
    assertEquals(left(0), left("zero").leftMap(constant(0)));
    assertEquals(right("zero"), right("zero").leftMap(constant(0)));
  }

  @Test
  public void testLeftMap_() {
    assertEquals(left(0), leftMap_().f(constant(0)).f(left("zero")));
    assertEquals(right("zero"), leftMap_().f(constant(0)).f(right("zero")));
  }

  @Test
  public void testRightMap() {
    assertEquals(left("zero"), left("zero").rightMap(constant(0)));
    assertEquals(right(0), right("zero").rightMap(constant(0)));
  }

  @Test
  public void testRightMap_() {
    assertEquals(left("zero"), rightMap_().f(constant(0)).f(left("zero")));
    assertEquals(right(0), rightMap_().f(constant(0)).f(right("zero")));
  }

  @Test
  public void testJoinLeft() {
    assertEquals(left(0), joinLeft(left(left(0))));
    assertEquals(right(0), joinLeft(left(right(0))));
    assertEquals(right(left(0)), joinLeft(right(left(0))));
    assertEquals(right(right(0)), joinLeft(right(right(0))));
  }

  @Test
  public void testJoinRight() {
    assertEquals(left(left(0)), joinRight(left(left(0))));
    assertEquals(left(right(0)), joinRight(left(right(0))));
    assertEquals(left(0), joinRight(right(left(0))));
    assertEquals(right(0), joinRight(right(right(0))));
  }

  @Test
  public void testSequenceLeft() {
    assertEquals(left(List.nil()), sequenceLeft(List.nil()));
    assertEquals(left(List.single("zero")), sequenceLeft(List.single(left("zero"))));
    assertEquals(right("zero"), sequenceLeft(List.single(right("zero"))));
  }

  @Test
  public void testSequenceRight() {
    assertEquals(right(List.nil()), sequenceRight(List.nil()));
    assertEquals(right(List.single("zero")), sequenceRight(List.single(right("zero"))));
    assertEquals(left("zero"), sequenceRight(List.single(left("zero"))));
  }

  @Test
  public void testTraverseListRight() {
    assertEquals(List.single(left("zero")), left("zero").traverseListRight(constant(List.nil())));
    assertEquals(List.single(left("zero")), left("zero").traverseListRight(constant(List.single(0))));
    assertEquals(List.single(left("zero")), left("zero").traverseListRight(constant(List.arrayList(0, 1))));
    assertEquals(List.nil(), right("zero").traverseListRight(constant(List.nil())));
    assertEquals(List.single(right(0)), right("zero").traverseListRight(constant(List.single(0))));
    assertEquals(List.arrayList(right(0), right(1)), right("zero").traverseListRight(constant(List.arrayList(0, 1))));
  }

  @Test
  public void testTraverseListLeft() {
    assertEquals(List.nil(), left("zero").traverseListLeft(constant(List.nil())));
    assertEquals(List.single(left(0)), left("zero").traverseListLeft(constant(List.single(0))));
    assertEquals(List.arrayList(left(0), left(1)), left("zero").traverseListLeft(constant(List.arrayList(0, 1))));
    assertEquals(List.single(right("zero")), right("zero").traverseListLeft(constant(List.nil())));
    assertEquals(List.single(right("zero")), right("zero").traverseListLeft(constant(List.single(0))));
    assertEquals(List.single(right("zero")), right("zero").traverseListLeft(constant(List.arrayList(0, 1))));
  }

  @Test
  public void testTraverseIORight() throws IOException {
    assertEquals(left("zero"), left("zero").traverseIORight(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(right(0), right("zero").traverseIORight(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseIOLeft() throws IOException {
    assertEquals(left(0), left("zero").traverseIOLeft(constant(IOFunctions.lazy(constant(0)))).run());
    assertEquals(right("zero"), right("zero").traverseIOLeft(constant(IOFunctions.lazy(constant(0)))).run());
  }

  @Test
  public void testTraverseOptionRight() {
    assertEquals(Option.some(left("zero")), left("zero").traverseOptionRight(constant(Option.none())));
    assertEquals(Option.some(left("zero")), left("zero").traverseOptionRight(constant(Option.some(0))));
    assertEquals(Option.none(), right("zero").traverseOptionRight(constant(Option.none())));
    assertEquals(Option.some(right(0)), right("zero").traverseOptionRight(constant(Option.some(0))));
  }

  @Test
  public void testTraverseOptionLeft() {
    assertEquals(Option.none(), left("zero").traverseOptionLeft(constant(Option.none())));
    assertEquals(Option.some(left(0)), left("zero").traverseOptionLeft(constant(Option.some(0))));
    assertEquals(Option.some(right("zero")), right("zero").traverseOptionLeft(constant(Option.none())));
    assertEquals(Option.some(right("zero")), right("zero").traverseOptionLeft(constant(Option.some(0))));
  }

  @Test
  public void testTraverseStreamRight() {
    assertEquals(Stream.single(left("zero")), left("zero").traverseStreamRight(constant(Stream.nil())));
    assertEquals(Stream.single(left("zero")), left("zero").traverseStreamRight(constant(Stream.single(0))));
    assertEquals(Stream.single(left("zero")), left("zero").traverseStreamRight(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.nil(), right("zero").traverseStreamRight(constant(Stream.nil())));
    assertEquals(Stream.single(right(0)), right("zero").traverseStreamRight(constant(Stream.single(0))));
    assertEquals(Stream.arrayStream(right(0), right(1)), right("zero").traverseStreamRight(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testTraverseStreamLeft() {
    assertEquals(Stream.nil(), left("zero").traverseStreamLeft(constant(Stream.nil())));
    assertEquals(Stream.single(left(0)), left("zero").traverseStreamLeft(constant(Stream.single(0))));
    assertEquals(Stream.arrayStream(left(0), left(1)), left("zero").traverseStreamLeft(constant(Stream.arrayStream(0, 1))));
    assertEquals(Stream.single(right("zero")), right("zero").traverseStreamLeft(constant(Stream.nil())));
    assertEquals(Stream.single(right("zero")), right("zero").traverseStreamLeft(constant(Stream.single(0))));
    assertEquals(Stream.single(right("zero")), right("zero").traverseStreamLeft(constant(Stream.arrayStream(0, 1))));
  }

  @Test
  public void testReduce() {
    assertEquals(0L, (long) reduce(left(0L)));
    assertEquals(0L, (long) reduce(right(0L)));
  }

  @Test
  public void testIif() {
    assertEquals(right(-1), iif(true, () -> -1, () -> 1));
    assertEquals(left(1), iif(false, () -> -1, () -> 1));
  }

  @Test
  public void testLefts() {
    assertEquals(List.nil(), lefts(List.nil()));
    assertEquals(List.single(0), lefts(List.single(left(0))));
    assertEquals(List.nil(), lefts(List.single(right(0))));
    assertEquals(List.arrayList(0, 1), lefts(List.arrayList(left(0), left(1))));
    assertEquals(List.single(0), lefts(List.arrayList(left(0), right(1))));
    assertEquals(List.single(1), lefts(List.arrayList(right(0), left(1))));
    assertEquals(List.nil(), lefts(List.arrayList(right(0), right(1))));
  }

  @Test
  public void testRights() {
    assertEquals(List.nil(), rights(List.nil()));
    assertEquals(List.single(0), rights(List.single(right(0))));
    assertEquals(List.single(0), lefts(List.single(left(0))));
    assertEquals(List.arrayList(0, 1), rights(List.arrayList(right(0), right(1))));
    assertEquals(List.single(0), rights(List.arrayList(right(0), left(1))));
    assertEquals(List.single(1), rights(List.arrayList(left(0), right(1))));
    assertEquals(List.nil(), rights(List.arrayList(left(0), left(1))));
  }

  @Test
  public void testTestToString() {
    assertNotNull(left(0).toString());
    assertNotNull(right(0).toString());
  }
}