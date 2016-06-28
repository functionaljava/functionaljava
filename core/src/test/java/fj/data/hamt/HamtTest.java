package fj.data.hamt;

import fj.Ord;
import fj.P2;
import fj.data.List;
import org.junit.Test;

import static fj.Equal.intEqual;
import static fj.Hash.intHash;
import static fj.P.p;
import static fj.data.List.list;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by maperr on 3/06/2016.
 */
public class HamtTest {

    HashArrayMappedTrie<Integer, Integer> empty() {
        return HashArrayMappedTrie.emptyKeyInteger();
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
        List<P2<Integer, Integer>> list = list(p(0, 1), p(31, 1), p(32, 1), p(33, 1));
        HashArrayMappedTrie<Integer, Integer> h = empty();
        HashArrayMappedTrie<Integer, Integer> h2 = h.set(list);
//        out.println(h);
//        out.println(h2);
        assertThat(h2.toStream().length(), equalTo(list.length()));
    }

    @Test
    public void setOverflow() {
//        List<P2<Integer, Integer>> list = List.list(p(-17, 4), p(-16, 16), p(11, -11));
//        List<P2<Integer, Integer>> list = List.list(p(11, -11));
//        List<P2<Integer, Integer>> list = List.list(p(-17, 4), p(-16, 16), p(11, -11), p(16, -20), p(20, 14));

        List<P2<Integer, Integer>> list = list(p(-5,2),p(-3,-6),p(0,-5),p(2,6),p(5,0));
        HashArrayMappedTrie<Integer, Integer> e = HashArrayMappedTrie.<Integer, Integer>empty(intEqual, intHash);
//        HashArrayMappedTrie<Integer, Integer> h1 = e.set(-5, -10);
//        HashArrayMappedTrie<Integer, Integer> h2 = h1.set(-3, -6);
        HashArrayMappedTrie<Integer, Integer> h2 = e.set(list);
        List<P2<Integer, Integer>> actual = h2.toList(Ord.intOrd);
        assertThat(list, equalTo(actual));
    }

    @Test
    public void overflow2() {
//        java.lang.Error: Exception on property evaluation with argument:
        List<P2<Integer, Integer>> list = list(p(-18, 9), p(-17, -7), p(-16, 9), p(-15, -14), p(-13, 9), p(-5, 11), p(-4, -6), p(1, -10), p(3, 15), p(6, 12), p(7, 16), p(16, 3));
        HashArrayMappedTrie<Integer, Integer> e = HashArrayMappedTrie.<Integer>emptyKeyInteger();
        HashArrayMappedTrie<Integer, Integer> h1 = e.set(list);



    }

    @Test
    public void overflow3() {
        overflowGen(list(p(6, 12), p(16, 3)));

    }

    public void overflowGen(List<P2<Integer, Integer>> list) {
        HashArrayMappedTrie<Integer, Integer> e = HashArrayMappedTrie.<Integer>emptyKeyInteger();
        HashArrayMappedTrie<Integer, Integer> h1 = e.set(list);
        List<P2<Integer, Integer>> actual = h1.toList(Ord.intOrd);
        assertThat(list, equalTo(actual));
    }

}
