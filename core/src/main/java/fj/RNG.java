package fj;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public abstract class RNG {

	public abstract P2<RNG, Integer> nextInt();

    public abstract P2<RNG, Long> nextLong();

    public P2<RNG, Integer> range(int low, int high) {
        return nextInt().map2(x -> (Math.abs(x) % (high - low + 1)) + low);
    }

}
