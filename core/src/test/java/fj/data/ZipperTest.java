package fj.data;

import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class ZipperTest {
  @Test
  void testZipper() {
    Zipper<Integer> z = Zipper.zipper(Stream.nil(), 0, Stream.range(1, 9));
    assertThat(z.map(i -> i + 13).toStream(), is(Stream.range(13, 22)));
  }

  @Test
  void testNext() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
    z = z.next().some();
    assertThat(z.lefts(), is(Stream.arrayStream(new Integer[]{2, 1})));
    assertThat(z.focus(), is(3));
    assertThat(z.rights(), is(Stream.nil()));
  }

  @Test
  void testNextNone() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.nil());
    assertThat(z.next().isNone(), is(true));
  }

  @Test
  void testCycleNext() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
    assertThat(z.cycleNext(), is(z.next().some()));
  }

  @Test
  void testCycleNextLast() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.nil());
    z = z.cycleNext();
    assertThat(z.lefts(), is(Stream.nil()));
    assertThat(z.focus(), is(1));
    assertThat(z.rights(), is(Stream.single(2)));
  }

  @Test
  void testPrevious() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
    z = z.previous().some();
    assertThat(z.lefts(), is(Stream.nil()));
    assertThat(z.focus(), is(1));
    assertThat(z.rights(), is(Stream.arrayStream(new Integer[]{2, 3})));
  }

  @Test
  void testPreviousNone() {
    Zipper<Integer> z = Zipper.zipper(Stream.nil(), 2, Stream.single(3));
    assertThat(z.previous().isNone(), is(true));
  }

  @Test
  void testCyclePrevious() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
    assertThat(z.cyclePrevious(), is(z.previous().some()));
  }

  @Test
  void testCyclePreviousFirst() {
    Zipper<Integer> z = Zipper.zipper(Stream.nil(), 1, Stream.single(2));
    z = z.cyclePrevious();
    assertThat(z.lefts(), is(Stream.single(1)));
    assertThat(z.focus(), is(2));
    assertThat(z.rights(), is(Stream.nil()));
  }

  @Test
  void testInsertLeft() {
    Zipper<Integer> z = Zipper.single(2);
    z = z.insertLeft(1);
    assertThat(z.lefts(), is(Stream.nil()));
    assertThat(z.focus(), is(1));
    assertThat(z.rights(), is(Stream.single(2)));
  }

  @Test
  void testInsertRight() {
    Zipper<Integer> z = Zipper.single(2);
    z = z.insertRight(3);
    assertThat(z.lefts(), is(Stream.single(2)));
    assertThat(z.focus(), is(3));
    assertThat(z.rights(), is(Stream.nil()));
  }

  @Test
  void testDeleteOthers() {
    Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
    z = z.deleteOthers();
    assertThat(z.lefts(), is(Stream.nil()));
    assertThat(z.focus(), is(2));
    assertThat(z.rights(), is(Stream.nil()));
  }

  @Test
  void testFind() {
    Zipper<Integer> z = Zipper.zipper(Stream.nil(), 0, Stream.range(1));
    z = z.find(i -> i == 4).some();
    assertThat(z.lefts(), is(Stream.arrayStream(new Integer[]{3, 2, 1, 0})));
    assertThat(z.focus(), is(4));
    assertThat(z.rights().take(3), is(Stream.range(5, 8)));
  }
}
