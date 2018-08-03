package fj.data.optic;

import fj.Monoid;
import fj.data.Either;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TraversalTest {
    @Test
    public void testTraversalLeft() {
        final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
        assertThat(t.fold(Monoid.intMinMonoid).f(Either.left(3)), is(3));
    }

    @Test
    public void testTraversalRight() {
        final Traversal<Either<Integer, Integer>, Integer> t = Traversal.codiagonal();
        assertThat(t.fold(Monoid.intMinMonoid).f(Either.right(2)), is(2));
    }

}
