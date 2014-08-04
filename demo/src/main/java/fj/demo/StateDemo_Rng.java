package fj.demo;

import fj.F;
import fj.P2;
import fj.RNG;
import fj.SimpleRNG;
import fj.data.State;
import fj.data.Stream;

import static fj.data.Option.some;
import static fj.data.Stream.unfold;

/**
 * Created by mperry on 4/08/2014.
 */
public class StateDemo_Rng {

    static public void main(String args[]) {
        listRandoms();
        stateExamples();
    }

    static RNG initRNG() {
        return new SimpleRNG(0);
    }

    private static void listRandoms() {
        RNG rng = initRNG();
        Stream<Integer> s = unfold(r -> some(num(r).swap()), rng);
        System.out.println(s.take(100).toList());
    }

    private static void stateExamples() {
        RNG rng = initRNG();
        State<RNG, Integer> s0 = State.unit(r -> num(r));

        State<RNG, Integer> s1 = nextState().f(s0);
        State<RNG, Integer> s2 = nextState().f(s1);
        State<RNG, Integer> s3 = nextState().f(s2);
        State<RNG, Integer> s4 = nextState().f(s3);
        State<RNG, Integer> s5 = nextState().f(s4);
        State<RNG, Integer> s6 = nextState().f(s5);

        System.out.println(s0.run(rng));
        System.out.println(s1.run(rng));
        System.out.println(s2.run(rng));
        System.out.println(s3.run(rng));
        System.out.println(s4.run(rng));
        System.out.println(s5.run(rng));
        System.out.println(s6.run(rng));
    }

    static P2<RNG, Integer> num(RNG r) {
        return r.range(1, 5);
    }

    static F<State<RNG, Integer>, State<RNG, Integer>> nextState() {
        return s -> s.mapState(p2 -> num(p2._1()));
    }

}
