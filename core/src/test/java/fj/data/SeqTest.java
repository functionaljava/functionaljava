package fj.data;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 16/01/2015.
 */
public class SeqTest {

    @Test
    public void objectMethods() {
        Seq<Integer> s1 = Seq.seq(1, 2, 3);
        Seq<Integer> s2 = Seq.seq(1, 2, 3);
        assertTrue(s1.toString().equals("Seq(1,2,3)"));
        assertTrue(s1.equals(s2));
        assertFalse(s1 == s2);

    }

}
