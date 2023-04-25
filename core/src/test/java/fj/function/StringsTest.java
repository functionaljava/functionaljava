package fj.function;

import fj.Function;

import org.junit.jupiter.api.Test;

import static fj.Function.compose;
import static fj.function.Strings.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class StringsTest {
  @Test
  void testLines() {
    assertThat(compose(unlines(), lines()).f("one two three"), is("one two three"));
  }

  @Test
  void testLinesEmpty() {
    assertThat(unlines().o(lines()).f(""), is(""));
  }

  @Test
  void testLength() {
    assertThat(length.f("functionaljava"), is(14));
  }

  @Test
  void testMatches() {
    assertThat(matches.f("foo").f("foo"), is(true));
  }

  @Test
  void testContains() {
    assertThat(contains.f("bar").f("foobar1"), is(true));
  }

  @Test
  void testIsEmptyException() {
    assertThrows(NullPointerException.class, () -> {
      assertThat(isEmpty.f(null), is(true));
    });
  }

  @Test
  void testIsEmpty() {
    assertThat(isEmpty.f(""), is(true));
  }

  @Test
  void testIsNotNullOrEmpty() {
    assertThat(isNotNullOrEmpty.f("foo"), is(true));
  }

  @Test
  void testIsNullOrEmpty() {
    assertThat(isNullOrEmpty.f(null), is(true));
  }

  @Test
  void testIsNotNullOrBlank() {
    assertThat(isNotNullOrBlank.f("foo"), is(true));
  }

  @Test
  void testIsNullOrBlank() {
    assertThat(isNullOrBlank.f("  "), is(true));
  }
}
