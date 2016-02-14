package fj.data;

import java.util.Map;

import fj.Equal;
import fj.Ord;
import fj.P3;
import fj.Show;
import fj.P2;

import org.junit.Test;

import static fj.P.p;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.TreeMap.iterableTreeMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
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
        TreeMap<Integer, String> m2 = iterableTreeMap(Ord.intOrd, l.zip(l.map(i -> i.toString())));
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
        P3<Set<String>, Option<String>, Set<String>> expected = p(left, some(Integer.toString(pivot)), right);
        assertTrue(Equal.p3Equal(seq, Equal.optionEqual(Equal.stringEqual), seq).eq(p, expected));
    }

    private static Set<String> toSetString(List<Integer> list) {
        return Set.iterableSet(Ord.stringOrd, list.map(i -> i.toString()));
    }

    @Test
    public void splitLookup() {
        // do the split
        int pivot = 4;
        int max = 5;
        List<Integer> l = List.range(1, max + 1);
        TreeMap<Integer, String> m2 = iterableTreeMap(Ord.intOrd, l.zip(l.map(i -> i.toString())));
        P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>> p3 = m2.splitLookup(pivot);

        // create expected output
        List<Integer> leftList = List.range(1, pivot);
        TreeMap<Integer, String> leftMap = iterableTreeMap(Ord.intOrd, leftList.zip(leftList.map(i -> i.toString())));
        List<Integer> rightList = List.range(pivot + 1, max + 1);
        TreeMap<Integer, String> rightMap = iterableTreeMap(Ord.intOrd, rightList.zip(rightList.map(i -> i.toString())));

        // debug info
        if (true) {
            Show<TreeMap<Integer, String>> st = Show.treeMapShow(Show.intShow, Show.stringShow);
            Show<P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>>> sp3 = Show.p3Show(st, Show.optionShow(Show.stringShow), st);
            sp3.println(p3);
        }

        // do the assert
        Equal<TreeMap<Integer, String>> tme = Equal.treeMapEqual(Equal.intEqual, Equal.stringEqual);
        Equal<P3<TreeMap<Integer, String>, Option<String>, TreeMap<Integer, String>>> eq = Equal.p3Equal(tme, Equal.optionEqual(Equal.stringEqual), tme);
        assertTrue(eq.eq(p3, p(leftMap, some(Integer.toString(pivot)), rightMap)));
    }

    @Test
    public void toMutableMap() {
        int max = 5;
        List<List<Integer>> l = List.range(1, max + 1).map(n -> List.single(n));
        TreeMap<List<Integer>, String> m2 = iterableTreeMap(Ord.listOrd(Ord.intOrd), l.zip(l.map(i -> i.toString())));
        Map<List<Integer>, String> mm = m2.toMutableMap();
        assertEquals(m2.keys(), List.iterableList(mm.keySet()));
    }


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

	@Test
	public void testString() {
		TreeMap<Integer, String> t = TreeMap.treeMap(Ord.intOrd, p(1, "a"), p(2, "b"), p(3, "c"));
		TreeMap<Integer, String> t2 = TreeMap.treeMap(Ord.intOrd, p(3, "c"), p(2, "b"), p(1, "a"));
		Stream<P2<Integer, String>> s = Stream.stream(p(1, "a"), p(2, "b"), p(3, "c"));
		assertThat(t.toStream(), equalTo(s));
		assertThat(t2.toStream(), equalTo(s));
	}

    @Test
    public void minKey() {
        TreeMap<Integer, Integer> t1 = TreeMap.<Integer, Integer>empty(Ord.intOrd);
        assertThat(t1.minKey(), equalTo(none()));
        TreeMap<Integer, Integer> t2 = t1.set(1, 2).set(2, 4).set(10, 20).set(5, 10).set(0, 100);
        assertThat(t2.minKey(), equalTo(some(0)));
        assertThat(t2.delete(0).minKey(), equalTo(some(1)));
    }

    @Test
    public void emptyHashCode() {
        // Hash code of tree map should not throw NullPointerException
        // see https://github.com/functionaljava/functionaljava/issues/187
        int i = TreeMap.empty(Ord.stringOrd).hashCode();
        assertTrue(true);
    }

}
