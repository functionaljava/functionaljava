package fj;

import org.junit.Test;

import static fj.Function.identity;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PTest {
    @Test
    public void testPF(){
        final F<Integer, P1<Integer>> p1f = P.p1();
        final P1<Integer> p1 = p1f.f(1);
        F<Integer, F<Integer, P2<Integer, Integer>>> p2f = P.p2();
        final P2<Integer, Integer> p2 = p2f.f(1).f(2);
        assertThat(P2.<Integer,Integer>__1().f(p2), is(P1.<Integer>__1().f(p1)));
        final F<Integer, F<Integer, F<Integer, P3<Integer, Integer, Integer>>>> p3f = P.p3();
        final P3<Integer, Integer, Integer> p3 = p3f.f(1).f(2).f(3);
        assertThat(P3.<Integer,Integer, Integer>__1().f(p3), is(P2.<Integer,Integer>__1().f(p2)));
        assertThat(P3.<Integer,Integer, Integer>__2().f(p3), is(P2.<Integer,Integer>__2().f(p2)));
        final F<Integer, F<Integer, F<Integer, F<Integer, P4<Integer, Integer, Integer, Integer>>>>> p4f = P.p4();
        final P4<Integer, Integer, Integer, Integer> p4 = p4f.f(1).f(2).f(3).f(4);
        assertThat(P4.<Integer,Integer,Integer,Integer>__1().f(p4), is(P3.<Integer,Integer,Integer>__1().f(p3)));
        assertThat(P4.<Integer,Integer,Integer,Integer>__2().f(p4), is(P3.<Integer,Integer,Integer>__2().f(p3)));
        assertThat(P4.<Integer,Integer,Integer,Integer>__3().f(p4), is(P3.<Integer,Integer,Integer>__3().f(p3)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, P5<Integer, Integer, Integer, Integer, Integer>>>>>> p5f = P.p5();
        final P5<Integer, Integer, Integer, Integer, Integer> p5 = p5f.f(1).f(2).f(3).f(4).f(5);
        assertThat(P5.<Integer,Integer,Integer,Integer,Integer>__1().f(p5), is(P4.<Integer,Integer,Integer,Integer>__1().f(p4)));
        assertThat(P5.<Integer,Integer,Integer,Integer,Integer>__2().f(p5), is(P4.<Integer,Integer,Integer,Integer>__2().f(p4)));
        assertThat(P5.<Integer,Integer,Integer,Integer,Integer>__3().f(p5), is(P4.<Integer,Integer,Integer,Integer>__3().f(p4)));
        assertThat(P5.<Integer,Integer,Integer,Integer,Integer>__4().f(p5), is(P4.<Integer,Integer,Integer,Integer>__4().f(p4)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer,
                P6<Integer, Integer, Integer, Integer, Integer, Integer>>>>>>> p6f = P.p6();
        final P6<Integer, Integer, Integer, Integer, Integer, Integer> p6 = p6f.f(1).f(2).f(3).f(4).f(5).f(6);
        assertThat(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__1().f(p6),
                is(P5.<Integer,Integer,Integer,Integer,Integer>__1().f(p5)));
        assertThat(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__2().f(p6),
                is(P5.<Integer,Integer,Integer,Integer,Integer>__2().f(p5)));
        assertThat(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__3().f(p6),
                is(P5.<Integer,Integer,Integer,Integer,Integer>__3().f(p5)));
        assertThat(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__4().f(p6),
                is(P5.<Integer,Integer,Integer,Integer,Integer>__4().f(p5)));
        assertThat(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__5().f(p6),
                is(P5.<Integer,Integer,Integer,Integer,Integer>__5().f(p5)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer,
                P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>>>>>> p7f = P.p7();
        final P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> p7 =
                p7f.f(1).f(2).f(3).f(4).f(5).f(6).f(7);
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__1().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__1().f(p6)));
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__2().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__2().f(p6)));
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__3().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__3().f(p6)));
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__4().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__4().f(p6)));
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__5().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__5().f(p6)));
        assertThat(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__6().f(p7),
                is(P6.<Integer,Integer,Integer,Integer,Integer,Integer>__6().f(p6)));
        final F<Integer, F<Integer, F<Integer, F<Integer, F<Integer, F<Integer,
                F<Integer, F<Integer, P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>>>>>>>>> p8f = P.p8();
        final P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> p8 =
                p8f.f(1).f(2).f(3).f(4).f(5).f(6).f(7).f(8);
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__1().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__1().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__2().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__2().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__3().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__3().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__4().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__4().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__5().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__5().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__6().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__6().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__7().f(p8),
                is(P7.<Integer,Integer,Integer,Integer,Integer,Integer,Integer>__7().f(p7)));
        assertThat(P8.<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Integer>__8().f(p8), is(8));
    }

    @Test
    public void testPProject1() {
        final P1<Integer> p1 = P.p(1);
        assertThat(p1.map(identity()), is(p1));
    }

    @Test
    public void testPProject2() {
        final P2<Integer, Integer> p2 = P.p(1, 2);
        assertThat(p2.map1(identity()), is(p2));
        assertThat(p2.map2(identity()), is(p2));
    }

    @Test
    public void testPProject3() {
        final P3<Integer, Integer, Integer> p3 = P.p(1, 2, 3);
        assertThat(p3.map1(identity()), is(p3));
        assertThat(p3.map2(identity()), is(p3));
        assertThat(p3.map3(identity()), is(p3));
    }

    @Test
    public void testPProject4() {
        final P4<Integer, Integer, Integer, Integer> p4 = P.p(1, 2, 3, 4);
        assertThat(p4.map1(identity()), is(p4));
        assertThat(p4.map2(identity()), is(p4));
        assertThat(p4.map3(identity()), is(p4));
        assertThat(p4.map4(identity()), is(p4));
    }

    @Test
    public void testPProject5() {
        final P5<Integer, Integer, Integer, Integer, Integer> p5 = P.p(1, 2, 3, 4, 5);
        assertThat(p5.map1(identity()), is(p5));
        assertThat(p5.map2(identity()), is(p5));
        assertThat(p5.map3(identity()), is(p5));
        assertThat(p5.map4(identity()), is(p5));
        assertThat(p5.map5(identity()), is(p5));
    }

    @Test
    public void testPProject6() {
        final P6<Integer, Integer, Integer, Integer, Integer, Integer> p6 = P.p(1, 2, 3, 4, 5, 6);
        assertThat(p6.map1(identity()), is(p6));
        assertThat(p6.map2(identity()), is(p6));
        assertThat(p6.map3(identity()), is(p6));
        assertThat(p6.map4(identity()), is(p6));
        assertThat(p6.map5(identity()), is(p6));
        assertThat(p6.map6(identity()), is(p6));
    }

    @Test
    public void testPProject7() {
        final P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> p7 =
                P.p(1, 2, 3, 4, 5, 6, 7);
        assertThat(p7.map1(identity()), is(p7));
        assertThat(p7.map2(identity()), is(p7));
        assertThat(p7.map3(identity()), is(p7));
        assertThat(p7.map4(identity()), is(p7));
        assertThat(p7.map5(identity()), is(p7));
        assertThat(p7.map6(identity()), is(p7));
        assertThat(p7.map7(identity()), is(p7));
    }

    @Test
    public void testPProject8() {
        final P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> p8 =
                P.p(1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(p8.map1(identity()), is(p8));
        assertThat(p8.map2(identity()), is(p8));
        assertThat(p8.map3(identity()), is(p8));
        assertThat(p8.map4(identity()), is(p8));
        assertThat(p8.map5(identity()), is(p8));
        assertThat(p8.map6(identity()), is(p8));
        assertThat(p8.map7(identity()), is(p8));
        assertThat(p8.map8(identity()), is(p8));
    }
}
