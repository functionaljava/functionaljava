package fj.data;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArrayTest {

    @Test
    public void array_is_safe() {
        List<Integer> list = List.range(1, 2);

        assertThat(list.toArray().array(Integer[].class), instanceOf(Integer[].class));
    }

}
