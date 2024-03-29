package fj;

import fj.data.Array;
import org.junit.Test;

import static fj.data.Array.array;
import static org.junit.Assert.assertTrue;

public class ShowTest {
    @Test
    public void arrayShow() {
        Array<Integer> a = array(3, 5, 7);
        String s = Show.arrayShow(Show.intShow).showS(a);
        assertTrue(s.equals("Array(3,5,7)"));
    }
}
