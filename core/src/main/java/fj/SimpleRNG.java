package fj;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public class SimpleRNG extends RNG {

	private Long seed;

	public SimpleRNG(long s) {
		seed = s;
	}

	public P2<Integer, RNG> nextInt() {
		long newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL;
		SimpleRNG nextRng = new SimpleRNG(newSeed);
		long n = (Long) (newSeed >>> 16);
		Integer i = (int) n;
		return P.p(i, nextRng);
	}

}
