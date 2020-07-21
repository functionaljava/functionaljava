package fj.test;

import fj.Equal;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.function.Effect1;
import org.junit.Test;

import static fj.Ord.charOrd;
import static fj.data.List.list;
import static fj.data.List.range;
import static fj.data.NonEmptyList.nel;
import static fj.data.Option.somes;
import static fj.data.Validation.fail;
import static fj.data.Validation.success;
import static fj.test.Gen.combinationOf;
import static fj.test.Gen.listOf;
import static fj.test.Gen.permutationOf;
import static fj.test.Gen.pickOne;
import static fj.test.Gen.selectionOf;
import static fj.test.Gen.sequence;
import static fj.test.Gen.streamOf;
import static fj.test.Gen.wordOf;
import static fj.test.Rand.standard;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class GenTest {

  private static final List<Character> AS = list('A', 'B', 'C');

  @Test
  public void testCombinationOf_none() {
    Gen<List<Character>> instance = combinationOf(0, AS);
    testPick(100, instance, actual -> assertTrue(actual.isEmpty()));
  }

  @Test
  public void testCombinationOf_one() {
    Gen<List<Character>> instance = combinationOf(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testCombinationOf_two() {
    Gen<List<Character>> instance = combinationOf(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> charOrd.isGreaterThan(a, l.head()))));
    });
  }

  @Test
  public void testCombinationOf_three() {
    Gen<List<Character>> instance = combinationOf(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> charOrd.isGreaterThan(a, l.head()))));
    });
  }

  @Test
  public void testSelectionOf_none() {
    Gen<List<Character>> instance = selectionOf(0, AS);
    testPick(100, instance, actual -> assertTrue(actual.isEmpty()));
  }

  @Test
  public void testSelectionOf_one() {
    Gen<List<Character>> instance = selectionOf(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testSelectionOf_two() {
    Gen<List<Character>> instance = selectionOf(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testSelectionOf_three() {
    Gen<List<Character>> instance = selectionOf(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testSelectionOf_four() {
    Gen<List<Character>> instance = selectionOf(4, AS);
    testPick(100, instance, actual -> {
      assertEquals(4, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testPermutationOf_none() {
    Gen<List<Character>> instance = permutationOf(0, AS);
    testPick(100, instance, actual -> assertTrue(actual.isEmpty()));
  }

  @Test
  public void testPermutationOf_one() {
    Gen<List<Character>> instance = permutationOf(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testPermutationOf_two() {
    Gen<List<Character>> instance = combinationOf(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !a.equals(l.head()))));
    });
  }

  @Test
  public void testPermutationOf_three() {
    Gen<List<Character>> instance = permutationOf(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !a.equals(l.head()))));
    });
  }

  @Test
  public void testWordOf_none() {
    Gen<List<Character>> instance = wordOf(0, AS);
    testPick(100, instance, actual -> assertTrue(actual.isEmpty()));
  }

  @Test
  public void testWordOf_one() {
    Gen<List<Character>> instance = wordOf(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testWordOf_two() {
    Gen<List<Character>> instance = wordOf(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testWordOf_three() {
    Gen<List<Character>> instance = wordOf(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testWordOf_four() {
    Gen<List<Character>> instance = wordOf(4, AS);
    testPick(100, instance, actual -> {
      assertEquals(4, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testStreamOf() {
    final Gen<Stream<Character>> instance = streamOf(pickOne(AS));
    testPick(100, instance.map(stream -> stream.take(4).toList()), actual -> {
      assertEquals(4, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testSequenceValidation() {
    final Gen<List<Validation<NonEmptyList<Exception>, Character>>> success = listOf(sequence(success(pickOne(AS))), 4);
    testPick(100, success, list -> assertEquals(list.length(),somes(list.map(v -> Option.sequence(v.map(c -> AS.elementIndex(Equal.anyEqual(), c))))).length()));

    final Gen<List<Validation<NonEmptyList<Exception>, Gen<Character>>>> failure = listOf(sequence(fail(nel(new Exception()))), 4);
    testPick(100, failure, list -> assertTrue(list.forall(a -> a.isFail())));
  }

  private static <A> void testPick(int n, Gen<List<A>> instance, Effect1<List<A>> test) {
    range(0, n).map(ignore -> instance.gen(0, standard)).foreachDoEffect(test);
  }

}
