package fj.data;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

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

    /**
     * This test demonstrates the known problem of creating streams from mutable structures.
     *
     * Some of the ways streams created in this way can fail is:
     * - weak stream references getting garbage collected
     * - underlying mutable data structure changes
     * - iterator gets updated (e.g. iterator used to create 2 different streams).
     */
    @Test(expected = ConcurrentModificationException.class)
    public void iterableStreamWithStructureUpdate() {
        java.util.List<Integer> list = List.list(1, 2, 3).toJavaList();
        Stream<Integer> s1 = Stream.iterableStream(list);
        int x = s1.head();
        list.remove(1);
        Stream<Integer> s2 = s1.tail()._1();
        x = s2.head();
    }


}
