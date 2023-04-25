package fj.function;

import fj.Equal;
import fj.F;
import fj.P;
import fj.P1;
import fj.data.List;

import org.junit.jupiter.api.Test;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.function.Visitor.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VisitorTest {
  @Test
  void testFindFirst() {
    assertThat(findFirst(List.list(none(), some(1), none()), () -> -1), is(1));
  }

  @Test
  void testFindFirstDef() {
    assertThat(findFirst(List.list(none(), none(), none()), () -> -1), is(-1));
  }

  @Test
  void testNullableFindFirst() {
    assertThat(nullablefindFirst(List.list(null, 1, null), () -> -1), is(1));
  }

  @Test
  void testNullableFindFirstDef() {
    assertThat(nullablefindFirst(List.list(null, null, null), () -> -1), is(-1));
  }

  @Test
  void testVisitor() {
    assertThat(visitor(List.list(i -> some(2 * i)), () -> -1, 10), is(20));
  }

  @Test
  void testVisitorDef() {
    assertThat(visitor(List.list(i -> none()), () -> "foo", 10), is("foo"));
  }

  @Test
  void testNullableVisitor() {
    assertThat(nullableVisitor(List.list(i -> 2 * i), () -> -1, 10), is(20));
  }

  @Test
  void testNullableVisitorDef() {
    assertThat(nullableVisitor(List.list(i -> null), () -> "foo", 10), is("foo"));
  }

  @Test
  void testAssociation() {
    final F<String, F<Integer, String>> a = association(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
    assertThat(a.f("foo").f(2), is("two"));
  }

  @Test
  void testAssociationDef() {
    final F<String, F<Integer, String>> a = association(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
    assertThat(a.f("foo").f(3), is("foo"));
  }

  @Test
  void testAssociationLazy() {
    final F<P1<String>, F<Integer, String>> a = associationLazy(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
    assertThat(a.f(P.p("foo")).f(2), is("two"));
  }

  @Test
  void testAssociationLazyDef() {
    final F<P1<String>, F<Integer, String>> a = associationLazy(List.list(P.p(1, "one"), P.p(2, "two")), Equal.intEqual);
    assertThat(a.f(P.p("foo")).f(3), is("foo"));
  }
}
