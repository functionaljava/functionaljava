package fj;

import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.CheckResult.summary;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static org.junit.Assert.assertTrue;

/**
 * Created by mperry on 14/07/2014.
 */
@RunWith(PropertyTestRunner.class)
public class MemoisationTest {

    public Property test1() {
        return property(arbInteger, a -> {
            P1<Integer> t = P.weakMemo(() -> a);
            return prop(t._1().equals(t._1())).and(prop(t._1().equals(a)));
        });
    }

    public Property test1_hardMemo() {
        return property(arbInteger, a -> {
            P1<Integer> t = P.hardMemo(() -> new Integer(a));
            return prop(t._1() == t._1()).and(prop(t._1().equals(a)));
        });
    }

    @Test
    public Property test2() {
        return property(arbInteger, arbInteger, (a, b) -> {
            P2<Integer, Integer> t = P.lazy(u -> new Integer(a), u -> new Integer(b)).memo();
            return prop(t._1().equals(t._1()) && t._1().equals(a) && t._2().equals(t._2()) && t._2().equals(b) );
        });
    }

    @Test
    public Property test3() {
        return property(arbInteger, arbInteger, arbInteger, (a, b, c) -> {
            P3<Integer, Integer, Integer> t = P.p(a, b, c).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3());
        });
    }

    @Test
    public Property test4() {
        return property(arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d) -> {
            P4<Integer, Integer, Integer, Integer> t = P.p(a, b, c, d).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4());
        });
    }

    @Test
    public Property test5() {
        return property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e) -> {
            P5<Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5());
        });
    }

    @Test
    public Property test6() {
        return property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f) -> {
            P6<Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6());
        });
    }

    @Test
    public Property test7() {
        return property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f, g) -> {
            P7<Integer, Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f, g).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6() && t._7() == t._7());
        });
    }

    @Test
    public Property test8() {
        return property(arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, arbInteger, (a, b, c, d, e, f, g, h) -> {
            P8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t = P.p(a, b, c, d, e, f, g, h).memo();
            return prop(t._1() == t._1() && t._2() == t._2() && t._3() == t._3() && t._4() == t._4() && t._5() == t._5() && t._6() == t._6() && t._7() == t._7() && t._8() == t._8());
        });
    }

}
