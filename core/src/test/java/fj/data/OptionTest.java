package fj.data;

import org.junit.Test;

import static fj.data.List.arrayList;
import static fj.data.List.nil;
import static fj.data.Option.none;
import static fj.data.Option.sequence;
import static fj.data.Option.some;
import static fj.data.Validation.fail;
import static fj.data.Validation.success;
import static org.junit.Assert.assertEquals;
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
        assertTrue(actual.equals(expected));
    }

    @Test
    public void sequenceListTest() {
        assertEquals(some(nil()), sequence(nil()));
        assertEquals(none(), sequence(arrayList(none())));
        assertEquals(some(arrayList(1)), sequence(arrayList(some(1))));
        assertEquals(none(), sequence(arrayList(none(), none())));
        assertEquals(none(), sequence(arrayList(some(1), none())));
        assertEquals(none(), sequence(arrayList(none(), some(2))));
        assertEquals(some(arrayList(1, 2)), sequence(arrayList(some(1), some(2))));
    }

    @Test
    public void sequenceValidationTest() {
        assertEquals(some(fail(1)), sequence(Validation.<Integer, Option<String>>fail(1)));
        assertEquals(none(), sequence(Validation.<Integer, Option<String>>success(none())));
        assertEquals(some(success("string")), sequence(Validation.<Integer, Option<String>>success(some("string"))));
    }
}
