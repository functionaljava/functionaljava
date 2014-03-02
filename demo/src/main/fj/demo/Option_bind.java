package fj.demo;

import static fj.Show.intShow;
import static fj.Show.optionShow;
import static fj.data.Option.none;
import static fj.data.Option.some;
import fj.data.Option;

public final class Option_bind {
	public static void main(final String[] args) {
		final Option<Integer> o1 = some(7);
		final Option<Integer> o2 = some(8);
		final Option<Integer> o3 = none();
		final Option<Integer> p1 = o1.bind(i -> i % 2 == 0 ? some(i * 3)
				: Option.none());
		final Option<Integer> p2 = o2.bind(i -> i % 2 == 0 ? some(i * 3)
				: Option.none());
		final Option<Integer> p3 = o3.bind(i -> i % 2 == 0 ? some(i * 3)
				: Option.none());
		optionShow(intShow).println(p1); // None
		optionShow(intShow).println(p2); // Some(24)
		optionShow(intShow).println(p3); // None
	}
}
