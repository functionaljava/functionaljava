package fj.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 17/08/2015.
 */
public class ListBufferTest {

	@Test
	public void testSnoc() {
		// test case for #181
		List.Buffer<Integer> buf = List.Buffer.empty();
		buf.snoc(1).snoc(2).snoc(3);
		List<Integer> list1 = buf.toList();
		buf.snoc(4);
		List<Integer> list2 = buf.toList();
		assertThat(list2, equalTo(Stream.range(1, 5).toList()));
	}

}
