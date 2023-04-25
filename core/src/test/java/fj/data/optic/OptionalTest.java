package fj.data.optic;

import fj.data.Option;

import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class OptionalTest {
  @Test
  void testOptionalSome() {
    Optional<String, Integer> o = Optional.optional(this::decode, i -> s -> s);
    assertThat(o.getOption("18"), is(Option.some(18)));
  }

  @Test
  void testOptionalNone() {
    Optional<String, Integer> o = Optional.optional(this::decode, i -> s -> s);
    assertThat(o.getOption("Z"), is(Option.none()));
  }

  private Option<Integer> decode(String s) {
    try {
      return Option.some(Integer.decode(s));
    } catch (NumberFormatException nfe) {
      return Option.none();
    }
  }
}
