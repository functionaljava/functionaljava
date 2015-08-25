package fj.consume.euler;

import static fj.data.List.range;
import static fj.function.Integers.sum;

import static java.lang.System.out;

/**
 * Add all the natural numbers below one thousand that are multiples of 3 or 5.
 */
public class Problem1 {

	public static void main(final String[] args) {
		calc();
	}

	public static void calc() {
		out.println(sum(range(0, 1000).filter(a -> a % 3 == 0 || a % 5 == 0)));
	}

}
