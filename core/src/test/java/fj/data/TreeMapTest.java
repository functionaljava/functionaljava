package fj.data;

import fj.Ord;
import fj.P2;
import java.util.List;
import java.util.ArrayList;
import junit.framework.TestCase;

public class TreeMapTest extends TestCase {
    public void testLargeInserts() {
        TreeMap<Integer, String> m = TreeMap.empty(Ord.intOrd);
        List<TreeMap<Integer, String>> states = new ArrayList<TreeMap<Integer, String>>();
        for (int i = 0; i < 10000; i++) {
            m = m.set(i, "abc " + i);
            states.add(m);
        }

        for (P2<Integer, String> i : states.get(50)) {
            System.out.println(i._1() + " -> " + i._2());
        }
    }
}
