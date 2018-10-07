package fj;

import fj.data.Option;
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
}
