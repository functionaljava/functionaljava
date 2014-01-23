package fj;

import static fj.Unit.unit;

/**
 * Represents a side-effect.
 * 
 * @version %build.number%
 */
@FunctionalInterface
public interface Effect<A> {
	void e(A a);

	/**
	 * Returns a function for the given effect.
	 * 
	 * @return The function using the given effect.
	 */
	public default F<A, Unit> e() {
		return a -> {
			e(a);
			return unit();
		};

	}

	/**
	 * A contra-variant functor on effect.
	 * 
	 * @param f
	 *            The function to map over the effect.
	 * @return An effect after a contra-variant map.
	 */
	public default <B> Effect<B> comap(final F<B, A> f) {
		return b -> e(f.f(b)); 
	}

	public static <A> Effect<A> f(final F<A, Unit> f) {
		return f::f;
	}
}
