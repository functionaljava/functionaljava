package fj.data;

import fj.Ord;
import fj.P2;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by MarkPerry on 2/06/2014.
 */
public class TestTreeMap {

    @Test
    public void testLargeInserts() {
        // check that inserting a large number of items performs ok
        // taken from https://code.google.com/p/functionaljava/issues/detail?id=31 and
        // https://github.com/functionaljava/functionaljava/pull/13/files
        final int n = 10000;
        TreeMap<Integer, String> m = TreeMap.empty(Ord.intOrd);
        for (int i = 0; i < n; i++) {
            m = m.set(i, "abc " + i);
        }
    }

}
