package fj;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public abstract class RNG {

	public abstract P2<RNG, Integer> nextInt();

    public abstract P2<RNG, Long> nextLong();

    public P2<RNG, Integer> range(int low, int high) {
        return nextNatural().map2(x -> (x % (high - low + 1)) + low);
    }


	public P2<RNG, Integer> nextNatural() {
		return nextInt().map2(x -> x < 0 ? -(x + 1) : x);
	}


}
