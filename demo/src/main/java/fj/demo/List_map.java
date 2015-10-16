package fj.demo;

import fj.data.List;
import static fj.data.List.list;
import static fj.function.Integers.add;
import static fj.Show.intShow;
import static fj.Show.listShow;

public final class List_map {
    public static void main(final String[] args) {
        final List<Integer> a = list(1, 2, 3);
        final List<Integer> b = a.map(add.f(42));
        final List<Integer> c = a.map(i -> i = 42);
        listShow(intShow).println(b); // [43,44,45]
    }
}
