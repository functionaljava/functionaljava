package fj;

import org.hamcrest.Matcher;
import org.hamcrest.core.Is;
import org.junit.Test;

import static fj.Ordering.EQ;
import static fj.Ordering.GT;
import static fj.Ordering.LT;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OrderingTest {

  @Test
  public void reverse() throws Exception {
    assertThat(GT.reverse(), is(LT));
    assertThat(LT.reverse(), is(GT));
    assertThat(EQ.reverse(), is(EQ));
  }
}