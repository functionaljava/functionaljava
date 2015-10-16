package fj;

import fj.data.Array;
import org.junit.Test;

import static fj.data.Array.array;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 4/06/2015.
 */
public class ShowTest {



    @Test
    public void arrayShow() {
        Array<Integer> a = array(3, 5, 7);
        String s = Show.arrayShow(Show.intShow).showS(a);
        System.out.println(s);
        assertTrue(s.equals("Array(3,5,7)"));

    }

}
