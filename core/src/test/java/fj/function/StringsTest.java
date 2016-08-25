package fj.function;

import fj.Function;
import org.junit.Test;

import static fj.F1Functions.o;
import static fj.Function.compose;
import static fj.function.Strings.*;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

public class StringsTest {
    @Test
    public void testLines() {
        assertThat(compose(unlines(), lines()).f("one two three"), is("one two three"));
    }

    @Test
    public void testLinesEmpty() {
        assertThat(o(unlines(), lines()).f(""), is(""));
    }

    @Test
    public void testLength() {
        assertThat(length.f("functionaljava"), is(14));
    }

    @Test
    public void testMatches() {
        assertThat(matches.f("foo").f("foo"), is(true));
    }

    @Test
    public void testContains() {
        assertThat(contains.f("bar").f("foobar1"), is(true));
    }

    @Test(expected = NullPointerException.class)
    public void testIsEmptyException() {
        assertThat(isEmpty.f(null), is(true));
    }

    @Test
    public void testIsEmpty() {
        assertThat(isEmpty.f(""), is(true));
    }

    @Test
    public void testIsNotNullOrEmpty() {
        assertThat(isNotNullOrEmpty.f("foo"), is(true));
    }

    @Test
    public void testIsNullOrEmpty() {
        assertThat(isNullOrEmpty.f(null), is(true));
    }

    @Test
    public void testIsNotNullOrBlank() {
        assertThat(isNotNullOrBlank.f("foo"), is(true));
    }

    @Test
    public void testIsNullOrBlank() {
        assertThat(isNullOrBlank.f("  "), is(true));
    }
}
