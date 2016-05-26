package fj.data;

import fj.Equal;
import fj.P2;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 16/01/2015.
 */
public class ListTest {

    @Test
    public void objectMethods() {

        int max = 5;
        List<Integer> list = List.range(1, max);

        assertTrue(list.equals(list));
        assertTrue(list.equals(List.range(1, max)));

        assertFalse(list.equals(List.single(1)));
        assertFalse(list.equals(true));
        assertFalse(list.equals(null));



        assertTrue(List.list(1, 2).toString().equals("List(1,2)"));

    }

    @Test
    public void integration() {
        java.util.List<Integer> ul = Arrays.asList(1, 2, 3);
        List<Integer> dl = List.iterableList(ul);
        assertTrue(ul.equals(dl.toJavaList()));

    }

    @Test
    public void convertToString() {
        final int n = 10000;
        final StringBuilder expected = new StringBuilder("List(");
        for (int i = 0; i < n; i++) {
            expected.append(i);
            if (i < n - 1) {
                expected.append(',');
            }
        }
        expected.append(')');
        assertEquals(expected.toString(), List.range(0, n).toString());
    }

    @Test
    public void partition() {
        P2<List<Integer>, List<Integer>> p = List.range(1, 5).partition(i -> i % 2 == 0);
        Equal<List<Integer>> e = Equal.listEqual(Equal.intEqual);
        assertTrue(e.eq(p._1(), List.list(2, 4)));
        assertTrue(e.eq(p._2(), List.list(1, 3)));
    }

    @Test
    public void intersperseOverflow() {
        // should not overflow
        int n = 100000;
        List<Integer> list = List.replicate(n, 1).intersperse(2);
        String s = list.toString();
    }

    @Test
    public void listReduce() {
        String list = List.range(1, 11).uncons((a, la) -> List.cons(a, la).toString(), "");
        String expected = List.range(1, 11).toString();
        assertThat(expected, equalTo(list));
    }

    @Test
    public void array() {
        final int max = 3;
        Integer[] ints = new Integer[max];
        for (int i = 0; i < max; i++) {
            ints[i] = i + 1;
        };
        assertThat(List.range(1, max + 1).array(Integer[].class), equalTo(ints));
    }

}
