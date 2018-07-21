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

  @Test
  public void contramapShouldWork() {
    Ord<String> lengthOrd = Ord.contramap(String::length, Ord.intOrd);

    assertThat(lengthOrd.compare("str", "rts"), is(Ordering.EQ));
    assertThat(lengthOrd.compare("strlong", "str"), is(Ordering.GT));
  }

  @Test
  public void thenShouldWork() {
    Ord<String> lengthThenLastDigitOrd = Ord.on(String::length, Ord.intOrd)
                                            .then(s -> s.charAt(s.length() - 1), Ord.charOrd).ord();

    assertThat(lengthThenLastDigitOrd.compare("str", "dyr"), is(Ordering.EQ));
    assertThat(lengthThenLastDigitOrd.compare("stt", "str"), is(Ordering.GT));
    assertThat(lengthThenLastDigitOrd.compare("str", "strr"), is(Ordering.LT));
  }
}
