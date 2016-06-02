package fj.data.fingertrees;

import fj.Function;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Seq;
import org.junit.Test;

import static fj.Monoid.intAdditionMonoid;
import static fj.Monoid.intMaxMonoid;
import static fj.P.p;
import static fj.data.fingertrees.FingerTree.measured;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 10/10/2015.
 */
public class FingerTreeTest {

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

    @Test
    public void testSeqString() {
        FingerTree<Integer, Integer> ft = FingerTree.emptyIntAddition();
//        out.println(ft.toString());
        String actual = List.range(1, 10).foldLeft(ft2 -> i -> {
            FingerTree<Integer, Integer> ft3 = ft2.snoc(i);
//            out.println(ft3.toString());
            return ft3;
        }, ft).toString();
        String expected = "Deep(9 -> One(1 -> 1), Deep(6 -> One(3 -> Node3(3 -> V3(2,3,4))), Empty(), One(3 -> Node3(3 -> V3(5,6,7)))), Two(2 -> V2(8,9)))";
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testQueueString() {
        FingerTree<Integer, P2<Integer, Integer>> ft = FingerTree.emptyIntMax();
//        out.println(ft.toString());
        String actual = List.range(1, 10).foldLeft(ft2 -> i -> {
            int j = i % 2 == 0 ? 2 * i : i;
            FingerTree<Integer, P2<Integer, Integer>> ft3 = ft2.snoc(P.p(j, j));
//            out.println(ft3.toString());
            return ft3;
        }, ft).toString();
        String expected = "Deep(16 -> One(1 -> (1,1)), Deep(12 -> One(8 -> Node3(8 -> V3((4,4),(3,3),(8,8)))), Empty(), One(12 -> Node3(12 -> V3((5,5),(12,12),(7,7))))), Two(16 -> V2((16,16),(9,9))))";
        assertThat(actual, equalTo(expected));

    }


}
