package fj.test;

import fj.F;
import fj.data.Option;
import static fj.data.Option.some;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.Random;

/**
 * A random number generator.
 * 
 * @version %build.number%
 */
public final class Rand {
	private final F<Option<Long>, F<Integer, F<Integer, Integer>>> f;
	private final F<Option<Long>, F<Double, F<Double, Double>>> g;

	private Rand(final F<Option<Long>, F<Integer, F<Integer, Integer>>> f,
			final F<Option<Long>, F<Double, F<Double, Double>>> g) {
		this.f = f;
		this.g = g;
	}

	/**
	 * Randomly chooses a value between the given range (inclusive).
	 * 
	 * @param seed
	 *            The seed to use for random generation.
	 * @param from
	 *            The minimum value to choose.
	 * @param to
	 *            The maximum value to choose.
	 * @return A random value in the given range.
	 */
	public int choose(final long seed, final int from, final int to) {
		return f.f(some(seed)).f(from).f(to);
	}

	/**
	 * Randomly chooses a value between the given range (inclusive).
	 * 
	 * @param from
	 *            The minimum value to choose.
	 * @param to
	 *            The maximum value to choose.
	 * @return A random value in the given range.
	 */
	public int choose(final int from, final int to) {
		return f.f(Option.<Long> none()).f(from).f(to);
	}

	/**
	 * Randomly chooses a value between the given range (inclusive).
	 * 
	 * @param seed
	 *            The seed to use for random generation.
	 * @param from
	 *            The minimum value to choose.
	 * @param to
	 *            The maximum value to choose.
	 * @return A random value in the given range.
	 */
	public double choose(final long seed, final double from, final double to) {
		return g.f(some(seed)).f(from).f(to);
	}

	/**
	 * Randomly chooses a value between the given range (inclusive).
	 * 
	 * @param from
	 *            The minimum value to choose.
	 * @param to
	 *            The maximum value to choose.
	 * @return A random value in the given range.
	 */
	public double choose(final double from, final double to) {
		return g.f(Option.<Long> none()).f(from).f(to);
	}

	/**
	 * Gives this random generator a new seed.
	 * 
	 * @param seed
	 *            The seed of the new random generator.
	 * @return A random generator with the given seed.
	 */
	public Rand reseed(final long seed) {
		return new Rand(old -> from -> to -> f.f(some(seed)).f(from).f(to),
				old -> from -> to -> g.f(some(seed)).f(from).f(to));
	}

	/**
	 * Constructs a random generator from the given functions that supply a
	 * range to produce a result.
	 * 
	 * @param f
	 *            The integer random generator.
	 * @param g
	 *            The floating-point random generator.
	 * @return A random generator from the given functions that supply a range
	 *         to produce a result.
	 */
	public static Rand rand(
			final F<Option<Long>, F<Integer, F<Integer, Integer>>> f,
			final F<Option<Long>, F<Double, F<Double, Double>>> g) {
		return new Rand(f, g);
	}

	private static final F<Long, Random> fr = x -> new Random(x);

	/**
	 * A standard random generator that uses {@link Random}.
	 */
	public static final Rand standard = new Rand(seed -> from -> to -> {
		final int f = min(from, to);
		final int t = max(from, to);
		return f + seed.map(fr).orSome(new Random()).nextInt(t - f + 1);
	}, seed -> from -> to -> {
		final double f = min(from, to);
		final double t = max(from, to);
		return seed.map(fr).orSome(new Random()).nextDouble() * (t - f) + f;
	});

}
