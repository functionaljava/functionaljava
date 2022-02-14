package fj.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LazyStringTest {

    @Test
    public void testToString() {
        Stream<LazyString> s = Stream.repeat(LazyString.str("abc"));
        // FJ 4.3 blows the stack when printing infinite streams of lazy strings.
        assertThat(s.toString(), is(equalTo("Cons(LazyString(a, ?), ?)")));
    }

}
