package fj;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EqualTest {
  @Test
  public void contramapShouldWork() {
    Equal<String> equalByLength = Equal.contramap(String::length, Equal.intEqual);

    assertThat(equalByLength.eq("str1", "str2"), is(true));
    assertThat(equalByLength.eq("str1", "str11"), is(false));
  }

  @Test
  public void thenShouldWork() {
    Equal<String> equalByLengthThenLastDigit = Equal.on(String::length, Equal.intEqual)
                                                    .then(s -> s.charAt(s.length() - 1), Equal.charEqual).equal();

    assertThat(equalByLengthThenLastDigit.eq("str1", "spr1"), is(true));
    assertThat(equalByLengthThenLastDigit.eq("str1", "str2"), is(false));
    assertThat(equalByLengthThenLastDigit.eq("str1", "strr1"), is(false));
  }
}
