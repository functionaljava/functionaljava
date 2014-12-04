package fj.data;

import fj.data.Reader;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 4/12/2014.
 *
 * Examples taken from http://learnyouahaskell.com/for-a-few-monads-more
 */
public class ReaderTest {

    @Test
    public void testMap() {
        int x = Reader.unit((Integer i) -> i + 3).map(i -> i * 5).f(8);
        assertTrue(x == 55);
//        System.out.println(x); // 55
    }

    @Test
    public void testFlatMap() {
        int y = Reader.unit((Integer i) -> i * 2).flatMap(a -> Reader.unit((Integer i) -> i + 10).map(b -> a + b)).f(3);
//        System.out.println(y); // 19
        assertTrue(y == 19);
    }

}
