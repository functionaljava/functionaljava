package fj.demo;

import fj.data.Option;
import static fj.Show.intShow;
import static fj.Show.optionShow;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.function.Integers.add;

public final class Option_map {
    public static void main(final String[] args) {
        final Option<Integer> o1 = some(7);
        final Option<Integer> o2 = none();
        final Option<Integer> p1 = o1.map(add.f(42));
        final Option<Integer> p2 = o2.map(add.f(42));

        final Option<Integer> p3 = o1.map(i -> i + 42);
        final Option<Integer> p4 = o2.map(i -> i + 42);

        optionShow(intShow).println(p1); // Some(49)
        optionShow(intShow).println(p2); // None
    }
}
