package fj;

import fj.data.Tree;
import fj.data.TreeZipper;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FFunctionsTest {
    @Test
    public void testApply() {
        F8<Integer, Integer, Integer, Integer,
                Integer, Integer, Integer, Integer, Integer> f8 =
                (i1, i2, i3, i4, i5, i6, i7, i8) ->
                        i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8;
        F7<Integer, Integer, Integer, Integer,
                Integer, Integer, Integer, Integer> f7 = F8Functions.f(f8, 8);
        F6<Integer, Integer, Integer, Integer, Integer, Integer, Integer> f6 =
                F7Functions.f(f7, 7);
        F5<Integer, Integer, Integer, Integer, Integer, Integer> f5 = F6Functions.f(f6, 6);
        F4<Integer, Integer, Integer, Integer, Integer> f4 = F5Functions.f(f5, 5);
        F3<Integer, Integer, Integer, Integer> f3 = F4Functions.f(f4, 4);
        F2<Integer, Integer, Integer> f2 = F3Functions.f(f3, 3);
        F<Integer, Integer> f1 = f2.f(2);
        assertThat(f1.f(1), is(36));
    }

    @Test
    public void testTreeK() {
        final Tree<Integer> t1 = Function.<Integer>identity().treeK().f(1);
        final Tree<Integer> t2 = Function.<Integer>identity().treeK().f(2);
        F<Integer, Integer> f = i -> i + 1;
        F<Integer, Integer> g = i -> i * 1;
        assertThat(f.mapTree().f(t1),
                is(g.mapTree().f(t2)));
    }

    @Test
    public void testTreeZipperK() {
        final TreeZipper<Integer> tz1 = Function.<Integer>identity().treeZipperK().f(1);
        final TreeZipper<Integer> tz2 = Function.<Integer>identity().treeZipperK().f(2);
        F<Integer, Integer> f = i -> i + 1;
        F<Integer, Integer> g = i -> i * 1;
        assertThat(f.mapTreeZipper().f(tz1),
                is(g.mapTreeZipper().f(tz2)));
    }
}
