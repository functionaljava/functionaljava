package fj.data;

import org.junit.Test;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.Ord.intOrd;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 18/08/2015.
 */
public class SetTest {

	@Test
	public void toStream() {
		Set<Integer> s = Set.set(intOrd, 1, 2, 3);
		assertThat(s.toStream(), equalTo(Stream.stream(1, 2, 3)));
	}

	@Test
	public void testString() {
		Set<Integer> s = Set.set(intOrd, 1, 2, 3);
		assertThat(s.toString(), equalTo("Set(1,2,3)"));
	}

	@Test
	public void testLookups() {
		Set<Integer> s = Set.set(intOrd, 5, 1, 7, 8);
		assertThat(s.lookup(7), equalTo(some(7)));
		assertThat(s.lookup(4), equalTo(none()));
		assertThat(s.lookupLT(6), equalTo(some(5)));
		assertThat(s.lookupLT(1), equalTo(none()));
		assertThat(s.lookupGT(5), equalTo(some(7)));
		assertThat(s.lookupGT(9), equalTo(none()));
		assertThat(s.lookupLE(8), equalTo(some(8)));
		assertThat(s.lookupLE(0), equalTo(none()));
		assertThat(s.lookupGE(8), equalTo(some(8)));
		assertThat(s.lookupGE(9), equalTo(none()));
	}
}
