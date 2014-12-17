package fj.data;

import fj.F;
import fj.F2;
import fj.test.*;
import org.junit.Test;

import static fj.F1Functions.bind;
import static fj.F1Functions.map;
import static fj.F2Functions.curry;
import static fj.test.Arbitrary.arbF;
import static fj.test.Arbitrary.arbF2;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Coarbitrary.coarbInteger;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 4/12/2014.
 */
public class ReaderTest {

    @Test
    public void testMap() {
        // (3 + 8) * 11
        // example taken from http://learnyouahaskell.com/for-a-few-monads-more
        int x = Reader.unit((Integer i) -> i + 3).map(i -> i * 5).f(8);
        assertTrue(x == 55);
//        System.out.println(x); // 55
    }

    @Test
    public void testFlatMap() {
        // (3 * 2) + (3 + 10)
        // example taken from http://learnyouahaskell.com/for-a-few-monads-more
        int y = Reader.unit((Integer i) -> i * 2).flatMap(a -> Reader.unit((Integer i) -> i + 10).map(b -> a + b)).f(3);
//        System.out.println(y); // 19
        assertTrue(y == 19);
    }

    @Test
    public void testMapProp() {
        CheckResult cr = property(
                arbF(coarbInteger, arbInteger),
                arbF(coarbInteger, arbInteger),
                arbInteger,
                (f, g, i) -> {
                    int expected = map(f, g).f(i);
//                    System.out.println(String.format("input: %d, result: %d", i, expected));
                    return prop(expected == Reader.unit(f).map(g).f(i));
                }).check();
        CheckResult.summary.println(cr);
        assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    }

    @Test
    public void testFlatMapProp() {
        CheckResult cr = property(
                arbF(coarbInteger, arbInteger),
                arbF2(coarbInteger, coarbInteger, arbInteger),
                arbInteger,
                (f, g, i) -> {
                    int expected = bind(f, curry(g)).f(i);
//              System.out.println(String.format("input: %d, result: %d", i, expected));
                    return prop(expected == Reader.unit(f).flatMap(toBindable(g)).f(i));
                }
        ).check();
        CheckResult.summary.println(cr);
        assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    }

    @Test
    public void testLeftIdentity() {
        CheckResult cr = Property.property(
                arbInteger,
                arbF(coarbInteger, arbInteger),
                arbF2(coarbInteger, coarbInteger, arbInteger),
                (i, f, g) -> {
//            F<Integer, Reader<Integer, Integer>> h = convert(g);
            int a = Reader.unit(f).flatMap(toBindable(g)).f(i);
            int b = g.f(f.f(i), i);
//            System.out.println(String.format("i=%d, a=%d, b=%d, truth=%b", i, a, b, a == b));
            return prop(a == b);
        }).check();
        CheckResult.summary.println(cr);
        assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    }

    F<Integer, Reader<Integer, Integer>> toBindable(F2<Integer, Integer, Integer> f) {
//        F<Integer, Reader<Integer, Integer>> h = map(curry(f), z -> Reader.unit(z));
        return map(curry(f), z -> Reader.unit(z));
    }

    @Test
    public void testRightIdentity() {
        CheckResult cr = Property.property(
                arbInteger,
                arbF(coarbInteger, arbInteger),
                (i, f) -> {
                    Reader<Integer, Integer> r = Reader.unit(f);
                    boolean b = r.flatMap(a -> r).f(i) == r.f(i);
//            System.out.println(String.format("i=%d, a=%d, b=%d, truth=%b", i, a, b, a == b));
                    return prop(b);
                }).check();
        CheckResult.summary.println(cr);
        assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    }



    @Test
    public void testAssociativity() {
        CheckResult cr = Property.property(
                arbInteger,
                arbF(coarbInteger, arbInteger),
                arbF2(coarbInteger, coarbInteger, arbInteger),
                arbF2(coarbInteger, coarbInteger, arbInteger),
                (i, f, g, h) -> {
                    Reader<Integer, Integer> r = Reader.unit(f);
                    int a = r.flatMap(toBindable(g)).flatMap(toBindable(h)).f(i);
                    int b = r.flatMap(x -> toBindable(g).f(x).flatMap(toBindable(h))).f(i);
//            System.out.println(String.format("i=%d, a=%d, b=%d, truth=%b", i, a, b, a == b));
                    return prop(a == b);
                }).check();
        CheckResult.summary.println(cr);
        assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    }



}
