package fj.data;

import org.junit.Test;

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

}
