package fj.data.optic;

import fj.data.Option;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OptionalTest {
    @Test
    public void testOptionalSome() {
        Optional<String, Integer> o = Optional.optional(this::decode, i -> s -> s);
        assertThat(o.getOption("18"), is(Option.some(18)));
    }

    @Test
    public void testOptionalNone() {
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
