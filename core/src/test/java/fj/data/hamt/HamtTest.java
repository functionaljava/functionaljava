package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.P2;
import fj.data.List;
import fj.data.Stream;
import org.junit.Test;

import static fj.P.p;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by maperr on 3/06/2016.
 */
public class HamtTest {

    HashArrayMappedTrie<Integer, Integer> empty() {
        HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.empty(Equal.intEqual, Hash.intHash);
        return empty;
    }

    @Test
    public void testEmpty() {
        HashArrayMappedTrie<Integer, Integer> h = empty();
//        out.println(h.toString());
        assertThat(h.length(), equalTo(0));
    }

    @Test
    public void one() {
        HashArrayMappedTrie<Integer, Integer> h = empty().set(3, 6);
//        out.println(h);
        assertThat(h.length(), equalTo(1));
    }

    @Test
    public void update() {
        HashArrayMappedTrie<Integer, Integer> h1 = empty();
        HashArrayMappedTrie<Integer, Integer> h2 = h1.set(3, 3);
        HashArrayMappedTrie<Integer, Integer> h3 = h2.set(3, 5);
//        out.println(h1);
//        out.println(h2);
//        out.println(h3);
        assertThat(h3.length(), equalTo(1));
    }

    @Test
    public void subtrieLength() {
        List<P2<Integer, Integer>> list = List.list(p(0, 1), p(31, 1), p(32, 1), p(33, 1));
        HashArrayMappedTrie<Integer, Integer> h = empty();
        HashArrayMappedTrie<Integer, Integer> h2 = h.set(list);
//        out.println(h);
//        out.println(h2);
        assertThat(h2.toStream().length(), equalTo(list.length()));
    }

}
