package fj.data;

import org.junit.Test;

import static fj.data.DList.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DListTest {
    @Test
    public void testConsSnoc() {
        assertThat(nil().snoc(2).cons(1).toJavaList(), is(single(1).snoc(2).toJavaList()));
    }

    @Test
    public void testListDList() {
        DList<Integer> d = listDList(List.range(0, 1000));
        assertThat(d.toJavaList(), is(List.range(0, 1000).toJavaList()));
    }

    @Test
    public void testArrayDList() {
        DList<Integer> d = arrayDList(Array.range(0, 1000).array(Integer[].class));
        assertThat(d.toJavaList(), is(Array.range(0, 1000).toJavaList()));
    }
    @Test
    public void testIter() {
        DList<Integer> d = iteratorDList(List.range(0, 1000).iterator());
        assertThat(d.toJavaList(), is(List.range(0, 1000).toJavaList()));
    }
}
