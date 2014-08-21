package fj.data;

import fj.*;
import org.junit.Assert;
import org.junit.Test;

import static fj.data.Option.some;
import static fj.data.Stream.unfold;

/**
 * Created by mperry on 4/08/2014.
 */
public class TestRngState {

	static String expected1 = "<4,4,2,2,2,5,3,3,1,5>";
	static int size = 10;

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
		Assert.assertTrue(s.take(size).toList().toString().equals(expected1));
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
		Assert.assertTrue(ints.toString().equals(expected1));
    }

	@Test
	public void testSequence() {
		List<Integer> list = State.sequence(List.replicate(size, defaultState())).eval(defaultRng());
		Assert.assertTrue(list.toString().equals(expected1));
	}

    @Test
    public void testTraverse() {
        List<Integer> list = State.traverse(List.range(1, 10), a -> (State.unit((Rng s) -> num(s, a)))).eval(defaultRng());
//        System.out.println(list.toString());
        String expected = "<1,2,3,5,6,7,7,9,10>";
        Assert.assertTrue(list.toString().equals(expected));
    }

}
