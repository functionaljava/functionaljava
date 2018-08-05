package fj.data.optic;

import fj.data.Option;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PrismTest {
    @Test
    public void testPrismSome() {
        Prism<String, Integer> prism = Prism.prism(s -> decode(s), i -> i.toString());
        assertThat(prism.getOption("18"), is(Option.some(18)));
    }

    @Test
    public void testPrismNone() {
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
