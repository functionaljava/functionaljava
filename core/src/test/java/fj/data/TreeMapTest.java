package fj.data;

import java.util.Map;

import fj.Equal;
import fj.Ord;
import fj.P3;
import fj.Show;
import fj.P;

import org.junit.Test;

import static fj.data.Option.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 11/01/2015.
 */
public class TreeMapTest {

    @Test
    public void split() {
        // do the split
        int pivot = 4;
        int max = 5;
        List<Integer> l = List.range(1, max + 1);
        TreeMap<Integer, String> m2 = TreeMap.treeMap(Ord.intOrd, l.zip(l.map(i -> i.toString())));
        P3<Set<String>, Option<String>, Set<String>> p = m2.split(Ord.stringOrd, pivot);

        // print debug info
        Show<TreeMap<Integer, String>> st = Show.treeMapShow(Show.intShow, Show.stringShow);
        Show<Set<String>> ss = Show.setShow(Show.stringShow);
        Show<Option<String>> so = Show.optionShow(Show.stringShow);
        Show<P3<Set<String>, Option<String>, Set<String>>> sp3 = Show.p3Show(ss, so, ss);
        if (true) {
            st.println(m2);
            sp3.println(p);
        }

        // assert equals
        Equal<Set<String>> seq = Equal.setEqual(Equal.stringEqual);
        Set<String> left = toSetString(List.range(1, pivot));
        Set<String> right = toSetString(List.range(pivot + 1, max + 1));
        P3<Set<String>, Option<String>, Set<String>> expected = P.p(left, some(Integer.toString(pivot)), right);
        assertTrue(Equal.p3Equal(seq, Equal.optionEqual(Equal.stringEqual), seq).eq(p, expected));
    }

    private static Set<String> toSetString(List<Integer> list) {
        return Set.set(Ord.stringOrd, list.map(i -> i.toString()));
    }

    @Test
    public void splitLookup() {
        // do the split
        int pivot = 4;
        int max = 5;
        List<Integer> l = List.range(1, max + 1);
        TreeMap<Integer, String> m2 = TreeMap.treeMap(Ord.intOrd, l.zip(l.map(i -> i.toString())));
        P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>> p3 = m2.splitLookup(pivot);

        // create expected output
        List<Integer> leftList = List.range(1, pivot);
        TreeMap<Integer, String> leftMap = TreeMap.treeMap(Ord.intOrd, leftList.zip(leftList.map(i -> i.toString())));
        List<Integer> rightList = List.range(pivot + 1, max + 1);
        TreeMap<Integer, String> rightMap = TreeMap.treeMap(Ord.intOrd, rightList.zip(rightList.map(i -> i.toString())));

        // debug info
        if (true) {
            Show<TreeMap<Integer, String>> st = Show.treeMapShow(Show.intShow, Show.stringShow);
            Show<P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>>> sp3 = Show.p3Show(st, Show.optionShow(Show.stringShow), st);
            sp3.println(p3);
        }

        // do the assert
        Equal<TreeMap<Integer, String>> tme = Equal.treeMapEqual(Equal.intEqual, Equal.stringEqual);
        Equal<P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>>> eq = Equal.p3Equal(tme, Equal.optionEqual(Equal.stringEqual), tme);
        assertTrue(eq.eq(p3, P.p(leftMap, some(Integer.toString(pivot)), rightMap)));
    }

    @Test
    public void toMutableMap() {
        int max = 5;
        List<List<Integer>> l = List.range(1, max + 1).map(n -> List.single(n));
        TreeMap<List<Integer>, String> m2 = TreeMap.treeMap(Ord.listOrd(Ord.intOrd), l.zip(l.map(i -> i.toString())));
        Map<List<Integer>, String> mm = m2.toMutableMap();
        assertEquals(m2.keys().reverse(), List.iterableList(mm.keySet()));
    }
}
