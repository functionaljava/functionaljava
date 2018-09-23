package fj.data;

import fj.Equal;
import fj.P2;
import org.junit.Test;

import java.util.ConcurrentModificationException;

import static fj.data.Stream.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Zheka Kozlov on 27.05.2015.
 */
public class StreamTest {

  @Test
  public void infiniteStream() {
    Stream<Integer> s = Stream.forever(Enumerator.intEnumerator, 0).bind(Stream::single);
    assertThat(List.range(0, 5), is(s.take(5).toList()));
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

    @Test(expected=Error.class)
    public void testCycleNil(){
        cycle(Stream.nil());
    }

    @Test
    public void testCycle() {
        Stream<Character> s = stream(new Character[]{'a', 'b'});
        assertThat(cycle(s).take(5),
                is(stream(new Character[]{'a', 'b', 'a', 'b', 'a'})));
    }

    @Test
    public void testIterate() {
        assertThat(iterate(a -> 2 * a + 1, 1).take(5),
                is(stream(new Integer[]{1, 3, 7, 15, 31})));
    }

    @Test
    public void testArrayStreamEmpty() {
        assertThat(arrayStream(new Integer[]{}), is(Stream.nil()));
    }

    @Test(expected=Error.class)
    public void testNilHead() {
        Stream.nil().head();
    }

    @Test(expected=Error.class)
    public void testNilTail() {
        Stream.nil().tail();
    }

    @Test
    public void testArray() {
        Character[] a = new Character[]{'a', 'b', 'c'};
        Stream<Character> s = stream(a);
        assertThat(s.array(Character[].class), is(a));
    }

    @Test
    public void testZipIndex() {
        Character[] a = new Character[]{'a', 'b', 'c'};
        P2<Stream<Character>, Stream<Integer>> p = unzip(stream(a).zipIndex().drop(1));
        assertThat(p._1(), is(stream(new Character[]{'b', 'c'})));
        assertThat(p._2(), is(stream(new Integer[]{1, 2})));
    }

    @Test
    public void testMinus() {
        Character[] a1 = new Character[]{'a', 'b', 'c', 'd', 'e'};
        Stream<Character> s1 = stream(a1);
        Character[] a2 = new Character[]{'b', 'e'};
        Stream<Character> s2 = stream(a2);
        assertThat(s1.minus(Equal.charEqual, s2),
                is(stream(new Character[]{'a', 'c', 'd'})));
    }
}
