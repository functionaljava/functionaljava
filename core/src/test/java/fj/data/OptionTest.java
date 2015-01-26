package fj.data;

import org.junit.Assert;
import org.junit.Test;

import static fj.data.Option.some;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 15/01/2015.
 */
public class OptionTest {

    @Test
    public void equals() {
        int max = 4;
        assertTrue(some(1).equals(some(1)));
        assertTrue(some(List.range(1, max)).equals(some(List.range(1, max))));
    }

    @Test
    public void traverseList() {
        int max = 3;
        List<Option<Integer>> actual = some(max).traverseList(a -> List.range(1, a + 1));
        List<Option<Integer>> expected = List.range(1, max + 1).map(i -> some(i));
        System.out.println(String.format("actual: %s, expected: %s", actual.toString(), expected.toString()));
        assertTrue(actual.equals(expected));
    }

}
