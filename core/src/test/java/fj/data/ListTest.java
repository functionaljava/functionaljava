package fj.data;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        List<Integer> dl = List.list(ul);
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
}
