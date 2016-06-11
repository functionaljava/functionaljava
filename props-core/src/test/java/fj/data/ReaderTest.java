package fj.data;

import fj.F;
import fj.data.test.PropertyAssert;
import fj.test.*;
import org.junit.Test;

import static fj.F1Functions.bind;
import static fj.F1Functions.map;
import static fj.test.Arbitrary.*;
import static fj.test.Cogen.cogenInteger;
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
        Property p = property(
                arbF(cogenInteger, arbInteger),
                arbF(cogenInteger, arbInteger),
                arbInteger,
                (f, g, i) -> {
                    int expected = map(f, g).f(i);
//                    System.out.println(String.format("input: %d, result: %d", i, expected));
                    return prop(expected == Reader.unit(f).map(g).f(i));
                });
        PropertyAssert.assertResult(p);
    }

    @Test
    public void testFlatMapProp() {
        Gen<F<Integer, Reader<Integer, Integer>>> a = arbF(cogenInteger, arbReader());
        Property p = property(
                arbF(cogenInteger, arbInteger),
                a,
                arbInteger,
                (f, g, i) -> {
                    int expected = bind(f, j -> g.f(j).getFunction()).f(i);
//              System.out.println(String.format("input: %d, result: %d", i, expected));
                    return prop(expected == Reader.unit(f).flatMap(g).f(i));
                }
        );
        PropertyAssert.assertResult(p);
    }

    // Left identity: return a >>= f == f a
    @Test
    public void testLeftIdentity() {
        Property p = Property.property(
                arbInteger,
                arbInteger,
                arbF(cogenInteger, arbReader()),
                (i, j, f) -> {
                    int a = Reader.<Integer, Integer>constant(i).flatMap(f).f(j);
                    int b = f.f(i).f(j);
                    return prop(a == b);
                });
        PropertyAssert.assertResult(p);
    }

    // Right identity: m >>= return == m
    @Test
    public void testRightIdentity() {
        Property p = Property.property(
                arbInteger,
                arbReader(),
                (i, r2) -> {
                    return prop(r2.flatMap(a -> Reader.constant(a)).f(i) == r2.f(i));
                });
        PropertyAssert.assertResult(p);
    }

    // Associativity: (m >>= f) >>= g == m >>= (\x -> f x >>= g)
    @Test
    public void testAssociativity() {
        Property p = Property.property(
                arbInteger,
                arbReader(),
                arbF(cogenInteger, arbReader()),
                arbF(cogenInteger, arbReader()),
                (i, r, f, g) -> {
                    boolean b2 = r.flatMap(f).flatMap(g).f(i) == r.flatMap(x -> f.f(x).flatMap(g)).f(i);
                    return prop(b2);
                });
        PropertyAssert.assertResult(p);
    }

    public Gen<Reader<Integer, Integer>> arbReader() {
        return Arbitrary.arbReader(cogenInteger, arbInteger);
    }


}
