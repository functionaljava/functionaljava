package fj.demo;

import fj.F;
import fj.data.Option;
import static fj.Show.intShow;
import static fj.Show.optionShow;
import static fj.data.Option.none;
import static fj.data.Option.some;

public final class Option_bind {
    public static void main(final String[] args) {
        final Option<Integer> o1 = some(7);
        final Option<Integer> o2 = some(8);
        final Option<Integer> o3 = none();

        F<Integer, Option<Integer>> f = i -> i % 2 == 0 ? some(i * 3) : none();
        final Option<Integer> o4 = o1.bind(f);
        final Option<Integer> o5 = o2.bind(f);
        final Option<Integer> o6 = o3.bind(f);

        final Option<Integer> p1 = o1.bind(new F<Integer, Option<Integer>>() {
            public Option<Integer> f(final Integer i) {
                return i % 2 == 0 ? some(i * 3) : Option.<Integer>none();
            }
        });
        final Option<Integer> p2 = o2.bind(new F<Integer, Option<Integer>>() {
            public Option<Integer> f(final Integer i) {
                return i % 2 == 0 ? some(i * 3) : Option.<Integer>none();
            }
        });
        final Option<Integer> p3 = o3.bind(new F<Integer, Option<Integer>>() {
            public Option<Integer> f(final Integer i) {
                return i % 2 == 0 ? some(i * 3) : Option.<Integer>none();
            }
        });

        optionShow(intShow).println(o4); // None
        optionShow(intShow).println(o5); // Some(24)
        optionShow(intShow).println(o6); // None
    }
}
