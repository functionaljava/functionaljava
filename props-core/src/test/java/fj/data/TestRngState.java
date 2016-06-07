package fj.data;

import fj.*;
import fj.test.Arbitrary;
import fj.test.Cogen;
import fj.test.Gen;
import fj.test.Property;
import org.junit.Assert;
import org.junit.Test;

import static fj.data.Option.some;
import static fj.data.Stream.unfold;
import static fj.data.test.PropertyAssert.assertResult;
import static fj.test.Arbitrary.*;
import static fj.test.Cogen.cogenInteger;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static fj.test.Variant.variant;

/**
 * Created by mperry on 4/08/2014.
 */
public class TestRngState {

	static List<Integer> expected1 = List.list(4,4,2,2,2,5,3,3,1,5);
	static int size = 10;
    static final Equal<List<Integer>> listIntEqual = Equal.listEqual(Equal.intEqual);

    static Rng defaultRng() {
        return new LcgRng(1);
    }

    static P2<Rng, Integer> num(Rng r) {
        return r.range(1, 5);
    }

    static State<Rng, Integer> defaultState() {
        return State.unit(s -> num(s));
    }

    static F<State<Rng, Integer>, State<Rng, Integer>> nextState() {
        return s -> s.mapState(p2 -> num(p2._1()));
    }

	static P2<Rng, Integer> num(Rng r, int x) {
		return r.range(x, x + 1);
	}

	@Test
    public void testUnfold() {
        Stream<Integer> s = unfold(r -> some(num(r).swap()), defaultRng());
		Assert.assertTrue(listIntEqual.eq(s.take(size).toList(), expected1));
    }

    @Test
    public void testTransitions() {
		P2<List<State<Rng, Integer>>, State<Rng, Integer>> p = List.replicate(size, nextState()).foldLeft(
			(P2<List<State<Rng, Integer>>, State<Rng, Integer>> p2, F<State<Rng, Integer>, State<Rng, Integer>> f) -> {
				State<Rng, Integer> s = f.f(p2._2());
				return P.p(p2._1().snoc(p2._2()), s);
			}
				, P.p(List.nil(),  defaultState())
		);
		List<Integer> ints = p._1().map(s -> s.eval(defaultRng()));
		Assert.assertTrue(listIntEqual.eq(ints, expected1));
    }

	@Test
	public void testSequence() {
		List<Integer> list = State.sequence(List.replicate(size, defaultState())).eval(defaultRng());
		Assert.assertTrue(listIntEqual.eq(list, expected1));
	}

    @Test
    public void testTraverse() {
        List<Integer> list = State.traverse(List.range(1, 10), a -> (State.unit((Rng s) -> num(s, a)))).eval(defaultRng());
//        System.out.println(list.toString());
        List<Integer> expected = List.list(1,2,3,5,6,7,7,9,10);
        Assert.assertTrue(listIntEqual.eq(list, expected));
    }

    public static Gen<State<LcgRng, Integer>> arbState() {
        return Arbitrary.arbState(Arbitrary.arbLcgRng(), Cogen.cogenLcgRng(), arbInteger);
    }

    public static Gen<F<LcgRng, P2<LcgRng, Integer>>> arbStateF() {
        return arbF(Cogen.cogenLcgRng(), arbP2(arbLcgRng(), arbInteger));
    }

    public static Cogen<State<LcgRng, Integer>> cogenState() {
        return Cogen.cogenState(Arbitrary.arbLcgRng(), (LcgRng s, Integer j) -> (long) (j >= 0 ? 2 * j : -2 * j + 1));
    }

    public static Gen<F<Integer, State<LcgRng, Integer>>> arbBindable() {
        return arbF(cogenInteger, arbState());
    }

    // Left identity: return i >>= f == f i
    @Test
    public void testLeftIdentity() {
        Property p = property(
                arbBindable(),
                arbInteger,
                arbLcgRng(),
                (f, i, r) -> {
                    int a = State.<LcgRng, Integer>constant(i).flatMap(f).eval(r);
                    int b = f.f(i).eval(r);
//                    System.out.println(String.format("a=%d, b=%d", a, b));
                    return prop(a == b);
                }
        );
        assertResult(p);
    }


    // Right identity: m >>= return == m
    @Test
    public void testRightIdentity() {
        Property p = Property.property(
                arbState(),
                arbLcgRng(),
                (s, r) -> {
                    int x = s.flatMap(a -> State.constant(a)).eval(r);
                    int y = s.eval(r);
//                    System.out.println(String.format("x=%d, y=%d", x, y));
                    return prop(x == y);
                }
        );
        assertResult(p);
    }

    // Associativity: (m >>= f) >>= g == m >>= (\x -> f x >>= g)
    @Test
    public void testAssociativity() {
        Property p = Property.property(
                arbState(),
                arbBindable(),
                arbBindable(),
                arbLcgRng(),
                (s, f, g, r) -> {
                    int t = s.flatMap(f).flatMap(g).eval(r);
                    int u = s.flatMap(x -> f.f(x).flatMap(g)).eval(r);
//                    System.out.println(String.format("x=%d, y=%d", t, u));
                    return prop(t == u);
                });
        assertResult(p);
    }


}
