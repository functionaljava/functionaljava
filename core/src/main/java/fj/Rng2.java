package fj;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public abstract class Rng2 {

	public abstract P2<Rng2, Integer> nextInt();

    public abstract P2<Rng2, Long> nextLong();

    public P2<Rng2, Integer> range(int low, int high) {
        return nextNatural().map2(x -> (x % (high - low + 1)) + low);
    }


	public P2<Rng2, Integer> nextNatural() {
		return nextInt().map2(x -> x < 0 ? -(x + 1) : x);
	}


}
