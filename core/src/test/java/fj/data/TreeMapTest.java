package fj.data;

import fj.Equal;
import fj.Ord;
import fj.P3;
import fj.Show;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by MarkPerry on 11/01/2015.
 */
public class TreeMapTest {

    @Test
    public void split() {
        int pivot = 4;
        List<Integer> l = List.range(1, 6);
        TreeMap<Integer, String> m2 = TreeMap.map(Ord.intOrd, l.zip(l.map(i -> i.toString())));
        P3<Set<String>, Option<String>, Set<String>> p = m2.split(pivot);
        Show<TreeMap<Integer, String>> st = Show.treeMapShow(Show.intShow, Show.stringShow);
        Show<Set<String>> ss = Show.setShow(Show.stringShow);
        Show<Option<String>> so = Show.optionShow(Show.stringShow);
        Show<P3<Set<String>, Option<String>, Set<String>>> sp3 = Show.p3Show(ss, so, ss);

        st.println(m2);
        sp3.println(p);

        Equal<Set<String>> eq = Equal.setEqual(Equal.stringEqual);
        Set<String> left = toSetString(List.list(1, 2, 3));
        Set<String> right = toSetString(List.list(5));
        Assert.assertTrue("Left side of split unexpected", eq.eq(left, p._1()));
        Assert.assertTrue(eq.eq(right, p._1()));
        Assert.assertTrue(Equal.optionEqual(Equal.stringEqual).eq(p._2(), Option.some(Integer.toString(pivot))));
    }

    private static Set<String> toSetString(List<Integer> list) {
        return Set.set(Ord.stringOrd, list.map(i -> i.toString()));
    }

}
