package fj;

public abstract class Rng {

	public abstract P2<Rng, Integer> nextInt();

    public abstract P2<Rng, Long> nextLong();

    // [low, high] inclusive
    public final P2<Rng, Integer> range(int low, int high) {
        return nextNatural().map2(x -> (x % (high - low + 1)) + low);
    }


	public final P2<Rng, Integer> nextNatural() {
		return nextInt().map2(x -> x < 0 ? -(x + 1) : x);
	}


}
