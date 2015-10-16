package fj.data.fingertrees;

import fj.data.List;
import org.junit.Test;

import static fj.test.Property.prop;
import static fj.test.Property.property;
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

}
