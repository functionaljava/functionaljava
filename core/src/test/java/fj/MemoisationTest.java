package fj;

import fj.test.Arbitrary;
import fj.test.Property;
import org.junit.Test;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbString;
import static fj.test.CheckResult.summary;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by mperry on 14/07/2014.
 */
public class MemoisationTest {

    @Test
    public void test1() {
        final Property p = property(arbInteger, a -> {
            P1<Integer> t = P1Functions.memo(P.p(a));
            return prop(t._1() == t._1());
        });
        summary.println(p.check());
    }

    @Test
    public void test2() {
        final Property p = property(arbInteger, arbInteger, (a, b) -> {
            P2<Integer, Integer> t = P.p(a, b).memo();
            return prop(t._1() == t._1() && t._2() == t._2());
        });
        summary.println(p.check());
    }

    @Test
    public void test3() {
        final Property p = property(arbInteger, arbInteger, arbInteger, (a, b, c) -> {
            P3<Integer, Integer, Integer> t = P.p(a, b, c).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3());
        });
        summary.println(p.check());
    }

    @Test
    public void test4() {
        final Property p = property(arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d) -> {
            P4<Integer, Integer, Integer, Integer> t = P.p(a, b, c, d).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4());
        });
        summary.println(p.check());
    }

    @Test
    public void test5() {
        final Property p = property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e) -> {
            P5<Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5());
        });
        summary.println(p.check());
    }

    @Test
    public void test6() {
        final Property p = property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f) -> {
            P6<Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6());
        });
        summary.println(p.check());
    }

    @Test
    public void test7() {
        final Property p = property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f, g) -> {
            P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f, g).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6() && t._7() == t._7());
        });
        summary.println(p.check());
    }

    @Test
    public void test8() {
        final Property p = property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f, g, h) -> {
            P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f, g, h).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6() && t._7() == t._7() && t._8() == t._8());
        });
        summary.println(p.check());
    }


}
