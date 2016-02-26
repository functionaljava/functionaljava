package fj.test;

import fj.data.List;
import fj.function.Effect1;
import org.junit.Test;

import static fj.Ord.charOrd;
import static fj.data.List.list;
import static fj.data.List.range;
import static fj.test.Gen.pickCombinationWithReplacement;
import static fj.test.Gen.pickCombinationWithoutReplacement;
import static fj.test.Gen.pickPermutationWithReplacement;
import static fj.test.Gen.pickPermutationWithoutReplacement;
import static fj.test.Rand.standard;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class TestGen {

  private static final List<Character> AS = list('A', 'B', 'C');

  @Test
  public void testPickCombinationWithoutReplacement_none() {
    Gen<List<Character>> instance = pickCombinationWithoutReplacement(0, AS);
    testPick(100, instance, actual -> {
      assertTrue(actual.isEmpty());
    });
  }

  @Test
  public void testPickCombinationWithoutReplacement_one() {
    Gen<List<Character>> instance = pickCombinationWithoutReplacement(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testPickCombinationWithoutReplacement_two() {
    Gen<List<Character>> instance = pickCombinationWithoutReplacement(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> charOrd.isGreaterThan(a, l.head()))));
    });
  }

  @Test
  public void testPickCombinationWithoutReplacement_three() {
    Gen<List<Character>> instance = pickCombinationWithoutReplacement(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> charOrd.isGreaterThan(a, l.head()))));
    });
  }

  @Test
  public void testPickCombinationWithReplacement_none() {
    Gen<List<Character>> instance = pickCombinationWithReplacement(0, AS);
    testPick(100, instance, actual -> {
      assertTrue(actual.isEmpty());
    });
  }

  @Test
  public void testPickCombinationWithReplacement_one() {
    Gen<List<Character>> instance = pickCombinationWithReplacement(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testPickCombinationWithReplacement_two() {
    Gen<List<Character>> instance = pickCombinationWithReplacement(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testPickCombinationWithReplacement_three() {
    Gen<List<Character>> instance = pickCombinationWithReplacement(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testPickCombinationWithReplacement_four() {
    Gen<List<Character>> instance = pickCombinationWithReplacement(4, AS);
    testPick(100, instance, actual -> {
      assertEquals(4, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !charOrd.isLessThan(a, l.head()))));
    });
  }

  @Test
  public void testPickPermutationWithoutReplacement_none() {
    Gen<List<Character>> instance = pickPermutationWithoutReplacement(0, AS);
    testPick(100, instance, actual -> {
      assertTrue(actual.isEmpty());
    });
  }

  @Test
  public void testPickPermutationWithoutReplacement_one() {
    Gen<List<Character>> instance = pickPermutationWithoutReplacement(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testPickPermutationWithoutReplacement_two() {
    Gen<List<Character>> instance = pickCombinationWithoutReplacement(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !a.equals(l.head()))));
    });
  }

  @Test
  public void testPickPermutationWithoutReplacement_three() {
    Gen<List<Character>> instance = pickPermutationWithoutReplacement(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
      assertTrue(actual.tails().forall(l -> l.isEmpty() || l.tail().forall(a -> !a.equals(l.head()))));
    });
  }

  @Test
  public void testPickPermutationWithReplacement_none() {
    Gen<List<Character>> instance = pickPermutationWithReplacement(0, AS);
    testPick(100, instance, actual -> {
      assertTrue(actual.isEmpty());
    });
  }

  @Test
  public void testPickPermutationWithReplacement_one() {
    Gen<List<Character>> instance = pickPermutationWithReplacement(1, AS);
    testPick(100, instance, actual -> {
      assertEquals(1, actual.length());
      assertTrue(AS.exists(a -> a.equals(actual.head())));
    });
  }

  @Test
  public void testPickPermutationWithReplacement_two() {
    Gen<List<Character>> instance = pickPermutationWithReplacement(2, AS);
    testPick(100, instance, actual -> {
      assertEquals(2, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testPickPermutationWithReplacement_three() {
    Gen<List<Character>> instance = pickPermutationWithReplacement(3, AS);
    testPick(100, instance, actual -> {
      assertEquals(3, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  @Test
  public void testPickPermutationWithReplacement_four() {
    Gen<List<Character>> instance = pickPermutationWithReplacement(4, AS);
    testPick(100, instance, actual -> {
      assertEquals(4, actual.length());
      assertTrue(actual.forall(actualA -> AS.exists(a -> a.equals(actualA))));
    });
  }

  private static <A> void testPick(int n, Gen<List<A>> instance, Effect1<List<A>> test) {
    range(0, n).map(i -> instance.gen(0, standard)).foreachDoEffect(test::f);
  }

}
