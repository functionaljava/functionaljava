package fj;

import fj.data.Set;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SemigroupTest {

    @Test
    public void intersection_semigroup_test() {
        Semigroup<Set<Integer>> intersectionSemigroup = Semigroup.setIntersectionSemigroup();
        Set<Integer> first = Set.set(Ord.intOrd, 1, 2, 3, 4);
        Set<Integer> second = Set.set(Ord.intOrd, 3, 4, 5, 6);
        assertThat(intersectionSemigroup.sum(first, second), is(Set.set(Ord.intOrd, 3, 4)));
    }

    @Test
    public void union_semigroup_test() {
        Semigroup<Set<Integer>> unionSemigroup = Semigroup.setSemigroup();
        Set<Integer> first = Set.set(Ord.intOrd, 1, 2, 3, 4);
        Set<Integer> second = Set.set(Ord.intOrd, 3, 4, 5, 6);
        assertThat(unionSemigroup.sum(first, second), is(Set.set(Ord.intOrd, 1, 2, 3, 4, 5, 6)));
    }
}
