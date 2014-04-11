package fj.demo.euler;

import fj.F;
import fj.data.Stream;

import static fj.data.List.range;
import static fj.function.Integers.sum;

import static java.lang.System.out;

/**
 * Add all the natural numbers below one thousand that are multiples of 3 or 5.
 */
public class Problem1 {
	public static void main(final String[] args) {
		java7();
		java8();
	}

	public static void java7() {
		out.println(sum(range(0, 1000).filter(new F<Integer, Boolean>() {
			public Boolean f(final Integer a) { return a % 3 == 0 || a % 5 == 0;}
		})));
	}

	public static void java8() {
		out.println(Stream.range(0, 1000).filter(i -> i % 3 == 0 || i % 5 == 0).foldLeft((acc, i) -> acc + i, 0));
	}

}
