package fj.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Zheka Kozlov on 27.05.2015.
 */
public class StreamTest {

  @Test
  public void infiniteStream() {
    Stream<Integer> s = Stream.forever(Enumerator.intEnumerator, 0).bind(Stream::single);
    assertEquals(List.range(0, 5), s.take(5).toList());
  }
}
