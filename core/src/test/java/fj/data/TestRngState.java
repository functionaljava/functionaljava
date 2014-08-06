package fj.data;

import fj.*;
import fj.data.State;
import fj.data.Stream;
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

    static RNG initRNG() {
        return new SimpleRNG(1);
    }

	@Test
    public void testUnfold() {
        Stream<Integer> s = unfold(r -> some(num(r).swap()), initRNG());
		Assert.assertTrue(s.take(size).toList().toString().equals(expected1));
    }

	@Test
    public void testTransitions() {
		P2<List<State<RNG, Integer>>, State<RNG, Integer>> p = List.replicate(size, nextState()).foldLeft(
			(P2<List<State<RNG, Integer>>, State<RNG, Integer>> p2, F<State<RNG, Integer>, State<RNG, Integer>> f) -> {
				State<RNG, Integer> s = f.f(p2._2());
				return P.p(p2._1().snoc(p2._2()), s);
			}
				, P.p(List.nil(),  defaultState())
		);
		List<Integer> ints = p._1().map(s -> s.eval(initRNG()));
		Assert.assertTrue(ints.toString().equals(expected1));
    }


    static P2<RNG, Integer> num(RNG r) {
        return r.range(1, 5);
    }

	static State<RNG, Integer> defaultState() {
		return State.unit(s -> num(s));
	}

    static F<State<RNG, Integer>, State<RNG, Integer>> nextState() {
        return s -> s.mapState(p2 -> num(p2._1()));
    }

	@Test
	public void testSequence() {
		List<Integer> list = State.sequence(List.replicate(size, defaultState())).eval(initRNG());
		Assert.assertTrue(list.toString().equals(expected1));
	}

}
