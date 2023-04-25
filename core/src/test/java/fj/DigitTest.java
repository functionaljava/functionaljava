package fj;

import fj.data.Option;

import org.junit.jupiter.api.Test;

import static fj.data.Array.range;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DigitTest {
  @Test
  void testInteger() {
    for (Integer i : range(0, 10)) {
      assertThat(Digit.fromLong(i).toLong(), is(i.longValue()));
    }
  }

  @Test
  void testChar() {
    for (Integer i : range(0, 10)) {
      Character c = Character.forDigit(i, 10);
      assertThat(Digit.fromChar(c).some().toChar(), is(c));
    }
  }

  @Test
  void testCharNone() {
    assertThat(Digit.fromChar('x'), is(Option.none()));
  }
}
