package fj.data.optic;

import fj.Monoid;
import fj.data.Either;

import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TraversalTest {
  @Test
  void testTraversalLeft() {
    final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
    assertThat(t.fold(Monoid.intMinMonoid).f(Either.left(3)), is(3));
  }

  @Test
  void testTraversalRight() {
    final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
    assertThat(t.fold(Monoid.intMinMonoid).f(Either.right(2)), is(2));
  }

}
