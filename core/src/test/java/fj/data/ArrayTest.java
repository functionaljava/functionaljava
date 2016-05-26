package fj.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 14 Feb 16.
 */
public class ArrayTest {

    @Test
    public void array_is_safe() {
        List<Integer> list = List.range(1, 2);

        assertThat(list.toArray().array(Integer[].class), instanceOf(Integer[].class));
    }

}
