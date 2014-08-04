package fj;

/**
 * Created by MarkPerry on 7/07/2014.
 *
 * https://en.wikipedia.org/wiki/Linear_congruential_generator
 */
public class SimpleRNG extends RNG {

	private Long seed;

    public SimpleRNG() {
        this(System.currentTimeMillis());
    }

	public SimpleRNG(long s) {
		seed = s;
	}

	public P2<RNG, Integer> nextInt() {
        P2<RNG, Long> p = nextLong();
        int i = (int) p._2().longValue();
        return P.p(p._1(), i);
	}

    public P2<RNG, Long> nextLong() {
        P2<Long, Long> p = nextLong(seed);
        return P.p(new SimpleRNG(p._1()), p._2());
    }

    /**
     *
     * @param seed
     * @return Product of Seed and value
     */
    static P2<Long, Long> nextLong(long seed) {
        long newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
        long n = (Long) (newSeed >>> 16);
        return P.p(newSeed, n);
    }

}
