package fj.data.vector;

import fj.*;
import fj.data.Array;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VTest {
    @Test
    public void testVectorUp(){
        final P2<Integer, Integer> p2 = P.p(2, 1);
        final V2<Integer> v2 = V2.p(p2);
        final V3<Integer> v3 = V3.cons(P.p(3), v2);
        final V4<Integer> v4 = V4.cons(P.p(4), v3);
        final V5<Integer> v5 = V5.cons(P.p(5), v4);
        final V6<Integer> v6 = V6.cons(P.p(6), v5);
        final V7<Integer> v7 = V7.cons(P.p(7), v6);
        final V8<Integer> v8 = V8.cons(P.p(8), v7);
        assertThat(v8.toArray(), is(Array.range(1, 9).reverse()));
    }

    @Test
    public void testVectorP(){
        final P2<Integer, Integer> p2 = P.p(1, 2);
        final V2<Integer> v2 = V2.p(p2);
        assertThat(v2.toArray(), is(Array.range(1, 3)));
        assertThat(v2.p(), is(p2));
        final P3<Integer, Integer, Integer> p3 = p2.append(3);
        final V3<Integer> v3 = V3.p(p3);
        assertThat(v3.toArray(), is(Array.range(1, 4)));
        assertThat(v3.p(), is(p3));
        final P4<Integer, Integer, Integer, Integer> p4 = p3.append(4);
        final V4<Integer> v4 = V4.p(p4);
        assertThat(v4.toArray(), is(Array.range(1, 5)));
        assertThat(v4.p(), is(p4));
        final P5<Integer, Integer, Integer, Integer, Integer> p5 = p4.append(5);
        final V5<Integer> v5 = V5.p(p5);
        assertThat(v5.toArray(), is(Array.range(1, 6)));
        assertThat(v5.p(), is(p5));
        final P6<Integer, Integer, Integer, Integer, Integer, Integer> p6 = p5.append(6);
        final V6<Integer> v6 = V6.p(p6);
        assertThat(v6.toArray(), is(Array.range(1, 7)));
        assertThat(v6.p(), is(p6));
        final P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> p7 = p6.append(7);
        final V7<Integer> v7 = V7.p(p7);
        assertThat(v7.toArray(), is(Array.range(1, 8)));
        assertThat(v7.p(), is(p7));
        final P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> p8 = p7.append(8);
        final V8<Integer> v8 = V8.p(p8);
        assertThat(v8.toArray(), is(Array.range(1, 9)));
        assertThat(v8.p(), is(p8));
    }

    @Test
    public void testVectorFunc2() {
        V2<Integer> v2 = V.v(() -> 2, () -> 1);
        F2<Integer, Integer, V2<Integer>> fv2 = V.v2();
        V2<Integer> vf2 = fv2.f(2, 1);
        assertThat(vf2, is(v2));
    }

    @Test
    public void testVectorFunc3() {
        V3<Integer> v3 = V.v(P.p(3), () -> 2, () -> 1);
        F3<Integer, Integer, Integer, V3<Integer>> fv3 = V.v3();
        V3<Integer> vf3 = fv3.f(3, 2, 1);
        assertThat(vf3, is(v3));
    }

    @Test
    public void testVectorFunc4() {
        V4<Integer> v4 = V.v(P.p(4), P.p(3), () -> 2, () -> 1);
        F4<Integer, Integer, Integer, Integer, V4<Integer>> fv4 = V.v4();
        V4<Integer> vf4 = fv4.f(4, 3, 2, 1);
        assertThat(vf4, is(v4));
    }

    @Test
    public void testVectorFunc5() {
        V5<Integer> v5 = V.v(P.p(5), P.p(4), P.p(3), () -> 2, () -> 1);
        F5<Integer, Integer, Integer, Integer, Integer, V5<Integer>> fv5 = V.v5();
        V5<Integer> vf5 = fv5.f(5, 4, 3, 2, 1);
        assertThat(vf5, is(v5));
    }

    @Test
    public void testVectorMap() {
        final V2<Integer> v2 = V.v(() -> 2, () -> 1);
        assertThat(v2, is(v2.map(i -> i * 1)));
        final V3<Integer> v3 = V3.cons(P.p(3), v2);
        assertThat(v3, is(v3.map(i -> i * 1)));
        final V4<Integer> v4 = V4.cons(P.p(4), v3);
        assertThat(v4, is(v4.map(i -> i * 1)));
        final V5<Integer> v5 = V5.cons(P.p(5), v4);
        assertThat(v5, is(v5.map(i -> i * 1)));
        final V6<Integer> v6 = V6.cons(P.p(6), v5);
        assertThat(v6, is(v6.map(i -> i * 1)));
        final V7<Integer> v7 = V7.cons(P.p(7), v6);
        assertThat(v7, is(v7.map(i -> i * 1)));
        final V8<Integer> v8 = V8.cons(P.p(8), v7);
        assertThat(v8, is(v8.map(i -> i * 1)));
    }

}
