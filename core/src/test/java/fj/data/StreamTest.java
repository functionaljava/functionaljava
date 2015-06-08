package fj.data;

import org.junit.Test;

import java.io.IOException;

import static fj.data.IOFunctions.stdinReadLine;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Zheka Kozlov on 27.05.2015.
 */
public class StreamTest {

  @Test
  public void infiniteStream() {
    Stream<Integer> s = Stream.forever(Enumerator.intEnumerator, 0).bind(Stream::single);
    assertEquals(List.range(0, 5), s.take(5).toList());
  }

  @Test
  public void testToString() {
    Stream<Integer> range = Stream.range(1);
    assertThat(range.toString(), is(equalTo("Cons(1, ?)")));
  }

}
