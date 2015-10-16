package fj.test;

import fj.Ord;
import fj.data.Stream;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 4/06/2015.
 */
public class TestRand {

    @Test
    public void testRandLowHighInclusive() {
        int min = 5;
        int max = 10;
        int n = 100;
        Stream<Integer> s = Stream.range(0, n).map(i -> Rand.standard.choose(min, max)).sort(Ord.intOrd);
//        System.out.println(s);
        assertTrue(s.head() == min && s.last() == max);
    }

}
