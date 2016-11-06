package fj.test;

import fj.F;
import fj.data.Option;

import java.util.Random;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A random number generator.
 *
 * @version %build.number%
 */
public final class Rand {
  private final F<Option<Long>, F<Integer, F<Integer, Integer>>> f;
  private final F<Option<Long>, F<Double, F<Double, Double>>> g;

  // TODO Change to F<Long,Rand> when rand(f,g) is removed
  private final Option<F<Long, Rand>> optOnReseed;

  private Rand(
      F<Option<Long>, F<Integer, F<Integer, Integer>>> f,
      F<Option<Long>, F<Double, F<Double, Double>>> g,
      Option<F<Long, Rand>> optOnReseed) {

    this.f = f;
    this.g = g;
    this.optOnReseed = optOnReseed;
  }

  /**
   * Randomly chooses a value between the given range (inclusive).
   *
   * @param seed The seed to use for random generation.
   * @param from The minimum value to choose.
   * @param to   The maximum value to choose.
   * @return A random value in the given range.
   */
  public int choose(final long seed, final int from, final int to) {
    return f.f(some(seed)).f(from).f(to);
  }

  /**
   * Randomly chooses a value between the given range (inclusive).
   *
   * @param from The minimum value to choose.
   * @param to   The maximum value to choose.
   * @return A random value in the given range.
   */
  public int choose(final int from, final int to) {
    return f.f(Option.none()).f(from).f(to);
  }

  public long choose(final long from, final long to) {
    return g.f(Option.none()).f((double) from).f((double) to).longValue();
  }
  /**
   * Randomly chooses a value between the given range (inclusive).
   *
   * @param seed The seed to use for random generation.
   * @param from The minimum value to choose.
   * @param to   The maximum value to choose.
   * @return A random value in the given range.
   */
  public double choose(final long seed, final double from, final double to) {
    return g.f(some(seed)).f(from).f(to);
  }

  /**
   * Randomly chooses a value between the given range (inclusive).
   *
   * @param from The minimum value to choose.
   * @param to   The maximum value to choose.
   * @return A random value in the given range.
   */
  public double choose(final double from, final double to) {
    return g.f(Option.none()).f(from).f(to);
  }

  /**
   * Gives this random generator a new seed.
   *
   * @param seed The seed of the new random generator.
   * @return A random generator with the given seed.
   */
  public Rand reseed(long seed) {
    return optOnReseed.<Rand>option(
        () -> {
          throw new IllegalStateException("reseed() called on a Rand created with deprecated rand() method");
        },
        onReseed -> onReseed.f(seed));
  }

  /**
   * Constructs a random generator from the given functions that supply a range to produce a
   * result.
   * <p>
   * Calling {@link #reseed(long)} on an instance returned from this method will
   * result in an exception being thrown.
   *
   * @deprecated As of release 4.6, use {@link #rand(F, F, F)}.
   *
   * @param f The integer random generator.
   * @param g The floating-point random generator.
   * @return A random generator from the given functions that supply a range to produce a result.
   */
  // TODO Change Option<F<Long,Rand>> optOnReseed to F<Long,Road> onReseed when removing this method
  @Deprecated
  public static Rand rand(
      F<Option<Long>, F<Integer, F<Integer, Integer>>> f,
      F<Option<Long>, F<Double, F<Double, Double>>> g) {

    return new Rand(f, g, none());
  }

  /**
   * Constructs a reseedable random generator from the given functions that supply a range to produce a
   * result.
   *
   * @param f        The integer random generator.
   * @param g        The floating-point random generator.
   * @param onReseed Function to create a reseeded Rand.
   * @return A random generator from the given functions that supply a range to produce a result.
   */
  public static Rand rand(
      F<Option<Long>, F<Integer, F<Integer, Integer>>> f,
      F<Option<Long>, F<Double, F<Double, Double>>> g,
      F<Long, Rand> onReseed) {

    return new Rand(f, g, some(onReseed));
  }

  /**
   * A standard random generator that uses {@link Random}.
   */
  public static final Rand standard = createStandard(new Random());

  private static Rand createStandard(Random defaultRandom) {
    return rand(
        optSeed -> from -> to ->
            standardChooseInt(optSeed.<Random>option(() -> defaultRandom, Random::new), from, to),
        optSeed -> from -> to ->
            standardChooseDbl(optSeed.<Random>option(() -> defaultRandom, Random::new), from, to),
        newSeed -> createStandard(new Random(newSeed)));
  }

  /*
   * Returns a uniformly distributed value between min(from,to) (inclusive) and max(from,to) (inclusive).
   */
  private static int standardChooseInt(Random random, int from, int to) {
    int result;
    if (from != to) {
      int min = min(from, to);
      int max = max(from, to);
      long range = (1L + max) - min;
      long bound = Long.MAX_VALUE - (Long.MAX_VALUE % range);
      long r = random.nextLong() & Long.MAX_VALUE;
      while (r >= bound) {
        // Ensure uniformity
        r = random.nextLong() & Long.MAX_VALUE;
      }
      result = (int) ((r % range) + min);
    } else {
      result = from;
    }
    return result;
  }

  /*
   * Returns a uniformly distributed value between min(from,to) (inclusive) and max(from,to) (exclusive)
   *
   * In theory, this differs from the choose() contract, which specifies a closed interval.
   * In practice, the difference shouldn't matter.
   */
  private static double standardChooseDbl(Random random, double from, double to) {
    double min = min(from, to);
    double max = max(from, to);
    return ((max - min) * random.nextDouble()) + min;
  }

}
