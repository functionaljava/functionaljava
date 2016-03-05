package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public final class F7Functions {

	private F7Functions() {
	}

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	public static <A, B, C, D, E, F$, G, H> F6<B, C, D, E, F$, G, H> f(final F7<A, B, C, D, E, F$, G, H> func, final A a) {
		return (b, c, d, e, f, g) -> func.f(a, b, c, d, e, f, g);
	}


}
