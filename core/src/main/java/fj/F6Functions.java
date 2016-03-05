package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public final class F6Functions {

	private F6Functions() {
	}

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	public static <A, B, C, D, E, F$, G> F5<B, C, D, E, F$, G> f(final F6<A, B, C, D, E, F$, G> func, final A a) {
		return (b, c, d, e, f) -> func.f(a, b, c, d, e, f);
	}


}
