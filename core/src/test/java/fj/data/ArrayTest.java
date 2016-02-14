package fj.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 14 Feb 16.
 */
public class ArrayTest {

    @Test
    public void toJavaArray() {
        final int max = 3;
        List<Integer> list = List.range(1, max + 1);
        assertThat(list.toArray().toJavaArray(), equalTo(list.toJavaArray()));
    }

}
