package fj.demo;

import fj.F;
import fj.data.Option;
import static fj.Show.intShow;
import static fj.Show.optionShow;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.function.Integers.even;

public final class Option_filter {
    public static void main(final String[] args) {
        final Option<Integer> o1 = some(7);
        final Option<Integer> o2 = none();
        final Option<Integer> o3 = some(8);

        final Option<Integer> o4 = o1.filter(even);
        final Option<Integer> o5 = o2.filter(even);
        final Option<Integer> o6 = o3.filter(even);

        F<Integer, Boolean> f = i -> i % 2 == 0;
        final Option<Integer> o7 = o4.filter(f);
        final Option<Integer> o8 = o5.filter(f);
        final Option<Integer> o9 = o6.filter(i -> i % 2 == 0);

        optionShow(intShow).println(o7); // None
        optionShow(intShow).println(o8); // None
        optionShow(intShow).println(o9); // Some(8)
    }
}
