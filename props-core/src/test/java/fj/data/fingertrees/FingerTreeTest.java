package fj.data.fingertrees;

import fj.P;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.Stream;
import org.junit.Test;

import static fj.P.p;
import static fj.Show.intShow;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 10/10/2015.
 */
public class FingerTreeTest {

    public static final int SIZE = 10;

    @Test
    public void size() {
        validateSize(List.list(-92, 68, 54, -77, -18, 67));
        validateSize(List.list(-92, 68, 54, -77, -18, 67, -60, 23, -70, 99, 66, -79, -5));
    }

    void validateSize(List<Integer> list) {
        FingerTree<Integer, Integer> ft = list.foldLeft(
            (acc, i) -> acc.snoc(i), FingerTree.<Integer>emptyIntAddition()
        );
        assertThat(ft.measure(), equalTo(list.length()));
        assertThat(ft.length(), equalTo(list.length()));
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
