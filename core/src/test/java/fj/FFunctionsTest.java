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
        F<Integer, Integer> f1 = F2Functions.f(f2, 2);
        assertThat(F1Functions.f(f1, 1).f(), is(36));
    }

    @Test
    public void testTreeK() {
        final Tree<Integer> t1 = F1Functions.<Integer, Integer>treeK(Function.identity()).f(1);
        final Tree<Integer> t2 = F1Functions.<Integer, Integer>treeK(Function.identity()).f(2);
        assertThat(F1Functions.<Integer, Integer>mapTree(i -> i + 1).f(t1),
                is(F1Functions.<Integer, Integer>mapTree(i -> i * 1).f(t2)));
    }

    @Test
    public void testTreeZipperK() {
        final TreeZipper<Integer> tz1 = F1Functions.<Integer, Integer>treeZipperK(Function.identity()).f(1);
        final TreeZipper<Integer> tz2 = F1Functions.<Integer, Integer>treeZipperK(Function.identity()).f(2);
        assertThat(F1Functions.<Integer, Integer>mapTreeZipper(i -> i + 1).f(tz1),
                is(F1Functions.<Integer, Integer>mapTreeZipper(i -> i * 1).f(tz2)));
    }
}
