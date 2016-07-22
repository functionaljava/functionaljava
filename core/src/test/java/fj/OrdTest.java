package fj;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrdTest {

  @Test
  public void isGreaterThan() {
    F<Long, Boolean> pred = Ord.longOrd.isGreaterThan(1L);

    assertThat(pred.f(0L), is(false));
    assertThat(pred.f(1L), is(false));
    assertThat(pred.f(2L), is(true));
  }

  @Test
  public void isLessThan() {
    F<Long, Boolean> pred = Ord.longOrd.isLessThan(1L);

    assertThat(pred.f(0L), is(true));
    assertThat(pred.f(1L), is(false));
    assertThat(pred.f(2L), is(false));
  }
}
