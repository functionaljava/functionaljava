package fj.data;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;


public class ArrayTest {

  @Test
  void array_is_safe() {
    List<Integer> list = List.range(1, 2);

    assertThat(list.toArray().array(Integer[].class), instanceOf(Integer[].class));
  }

}
