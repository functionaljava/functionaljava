package fj;

import fj.data.Option;
import org.junit.Test;

import static fj.data.Array.range;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DigitTest {
    @Test
    public void testInteger() {
        for (Integer i: range(0, 10)) {
            assertThat(Digit.fromLong(i).toLong(), is(i.longValue()));
        }
    }

    @Test
    public void testChar() {
        for (Integer i: range(0, 10)) {
            Character c = Character.forDigit(i, 10);
            assertThat(Digit.fromChar(c).some().toChar(), is(c));
        }
    }

    @Test
    public void testCharNone() {
        assertThat(Digit.fromChar('x'), is(Option.none()));
    }
}
