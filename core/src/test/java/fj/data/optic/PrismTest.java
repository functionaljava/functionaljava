package fj.data.optic;

import fj.data.Option;

import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrismTest {
  @Test
  void testPrismSome() {
    Prism<String, Integer> prism = Prism.prism(s -> decode(s), i -> i.toString());
    assertThat(prism.getOption("18"), is(Option.some(18)));
  }

  @Test
  void testPrismNone() {
    Prism<String, Integer> prism = Prism.prism(s -> decode(s), i -> i.toString());
    assertThat(prism.getOption("Z"), is(Option.none()));
  }

  private Option<Integer> decode(String s) {
    try {
      return Option.some(Integer.decode(s));
    } catch (NumberFormatException nfe) {
      return Option.none();
    }
  }
}
