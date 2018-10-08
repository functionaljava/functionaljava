package fj.data.fingertrees;

import fj.Function;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import org.junit.Test;

import static fj.Monoid.intAdditionMonoid;
import static fj.Monoid.intMinMonoid;
import static fj.data.fingertrees.FingerTree.measured;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Created by MarkPerry on 10/10/2015.
 */
public class FingerTreeTest {

    public static final int SIZE = 10;

    @Test
    public void size() {
        validateOperations(List.list(-92, 68, 54, -77, -18, 67));
        validateOperations(List.list(-92, 68, 54, -77, -18, 67, -60, 23, -70, 99, 66, -79, -5));
    }

    void validateOperations(List<Integer> list) {
        FingerTree<Integer, Integer> ft = list.foldLeft(
            (acc, i) -> acc.snoc(i), FingerTree.<Integer>emptyIntAddition()
        );
        assertThat(ft.measure(), equalTo(list.length()));
        assertThat(ft.foldLeft((s, i) -> s + 1, 0), equalTo(list.length()));
        assertThat(ft.foldRight((i, s) -> 1 + s, 0), equalTo(list.length()));
        assertThat(ft.filter(e -> e.equals(-77)).head(), equalTo(-77));
        assertThat(ft.length(), equalTo(list.length()));
    }

    @Test
    public void testHeadOption() {
        assertThat(Empty.emptyIntAddition().headOption(), is(Option.none()));
        FingerTree<Integer, Integer> ft = new MakeTree<Integer, Integer>(measured(intAdditionMonoid, Function.constant(1)))
            .single(1);
        assertThat(ft.headOption(), is(Option.some(1)));
    }

    @Test
    public void testUncons() {
        assertThat(Empty.emptyIntAddition().uncons(0, (h, t) -> h), is(0));
        FingerTree<Integer, Integer> ft = new MakeTree<Integer, Integer>(measured(intAdditionMonoid, Function.constant(1)))
            .single(1);
        assertThat(ft.uncons(0, (h, t) -> h), is(1));
    }

    public FingerTree<Integer, Integer> midSeq() {
        FingerTree<Integer, Integer> ft = FingerTree.emptyIntAddition();
        return List.range(1, SIZE).foldLeft(ft2 -> i -> ft2.snoc(i), ft);
    }

    @Test
    public void testSeqString() {
        String actual = midSeq().toString();
        String expected = "Deep(9 -> One(1 -> 1), Deep(6 -> One(3 -> Node3(3 -> V3(2,3,4))), Empty(), One(3 -> Node3(3 -> V3(5,6,7)))), Two(2 -> V2(8,9)))";
        assertThat(actual, equalTo(expected));
    }

    public FingerTree<Integer, P2<Integer, Integer>> midPriorityQueue() {
        FingerTree<Integer, P2<Integer, Integer>> ft = FingerTree.emptyIntMax();
        return List.range(1, SIZE).foldLeft(ft2 -> i -> {
            int j = i % 2 == 0 ? 2 * i : i;
            FingerTree<Integer, P2<Integer, Integer>> ft3 = ft2.snoc(P.p(j, j));
            return ft3;
        }, ft);
    }

    @Test
    public void testQueueString() {
        String actual = midPriorityQueue().toString();
        String expected = "Deep(16 -> One(1 -> (1,1)), Deep(12 -> One(8 -> Node3(8 -> V3((4,4),(3,3),(8,8)))), Empty(), One(12 -> Node3(12 -> V3((5,5),(12,12),(7,7))))), Two(16 -> V2((16,16),(9,9))))";
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void stream() {
        FingerTree<Integer, Integer> ft = midSeq();
        assertThat(ft.toStream().toList(), equalTo(List.range(1, SIZE)));
    }

    @Test
    public void split() {
        int splitPoint = 3;
        FingerTree<Integer, Integer> ft = FingerTree.emptyIntAddition();
        FingerTree<Integer, Integer> ft3 = List.range(1, SIZE).foldLeft(ft2 -> i -> ft2.snoc(i), ft);
        P2<FingerTree<Integer, Integer>, FingerTree<Integer, Integer>> p = ft3.split(v -> v >= splitPoint);
        assertThat(p._1().toStream().toList(), equalTo(List.range(1, splitPoint)));
        assertThat(p._2().toStream().toList(), equalTo(List.range(splitPoint, SIZE)));

    }

}
