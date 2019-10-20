package fj;

import fj.data.Enumerator;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import org.junit.Test;

import static fj.data.Option.some;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MonoidTest {

  @Test
  public void lifted_sum_of_two_numbers() {
    Monoid<Option<Integer>> optionMonoid = Semigroup.intAdditionSemigroup.lift();
    assertThat(optionMonoid.sum(some(3), some(5)), is(some(8)));
    assertThat(optionMonoid.sumLeft(Stream.arrayStream(some(3), some(5))), is(some(8)));
  }

  @Test
  public void intersection_monoid_test() {
    Bounded<Integer> integersBounded = Bounded.bounded(0, 10);
    Monoid<Set<Integer>> intersectionMonoid = Monoid.setIntersectionMonoid(integersBounded, Enumerator.intEnumerator, Ord.intOrd);
    Set<Integer> first = Set.set(Ord.intOrd, 1, 2, 3, 4);
    Set<Integer> second = Set.set(Ord.intOrd, 3, 4, 5, 6);
    Set<Integer> zero = intersectionMonoid.zero();
    Set<Integer> actual = intersectionMonoid.sum(intersectionMonoid.sum(zero, first), second);
    assertThat(actual, is(Set.set(Ord.intOrd, 3, 4)));
  }

  @Test
  public void union_monoid_test() {
    Monoid<Set<Integer>> unionMonoid = Monoid.setMonoid(Ord.intOrd);
    Set<Integer> first = Set.set(Ord.intOrd, 1, 2, 3, 4);
    Set<Integer> second = Set.set(Ord.intOrd, 3, 4, 5, 6);
    Set<Integer> zero = unionMonoid.zero();
    Set<Integer> actual = unionMonoid.sum(unionMonoid.sum(zero, first), second);
    assertThat(actual, is(Set.set(Ord.intOrd, 1, 2, 3, 4, 5, 6)));
  }

}
