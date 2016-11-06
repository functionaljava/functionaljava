package fj.data.hamt;

import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import org.junit.Test;

import static fj.Equal.intEqual;
import static fj.Equal.optionEqual;
import static fj.Hash.intHash;
import static fj.P.p;
import static fj.data.List.list;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Mark Perry
 */
public class HamtTest {

    public static final HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.emptyKeyInteger();

    @Test
    public void empty() {
        assertThat(empty.length(), equalTo(0));
    }

    @Test
    public void lengthOne() {
        assertThat(empty.set(3, 6).length(), equalTo(1));
    }

    @Test
    public void updateLength() {
        HashArrayMappedTrie<Integer, Integer> h1 = empty.set(3, 3).set(3, 5);
        assertThat(h1.length(), equalTo(1));
    }

    @Test
    public void streamLength() {
        List<P2<Integer, Integer>> list = list(p(0, 1), p(31, 1), p(32, 1), p(33, 1));
        HashArrayMappedTrie<Integer, Integer> h2 = empty.set(list);
        assertThat(h2.toStream().length(), equalTo(list.length()));
    }

    @Test
    public void allIn() {
        List<P2<Integer, Integer>> list = List.list(p(-5, 0), p(-1, -5), p(2, 4), p(4, -2));
        HashArrayMappedTrie<Integer, Integer> h = empty.set(list);
        Boolean b = list.foldLeft((acc, p) -> h.find(p._1()).option(false, i -> true && acc), true);
        assertThat(b, equalTo(true));
    }

    @Test
    public void sampleInts() {
        List<P2<Integer, Integer>> ps = List.list(p(-3, 0), p(1, 2));
        int key = -3;
        HashArrayMappedTrie<Integer, Integer> h = empty.set(ps);
        Option<Integer> o1 = ps.find(p -> intEqual.eq(p._1(), key)).map(p -> p._2());
        Option<Integer> o2 = h.find(key);
        boolean b = optionEqual(intEqual).eq(o1, o2);
        assertThat(b, equalTo(true));
    }

}
