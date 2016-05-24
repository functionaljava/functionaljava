package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public final class F8Functions {

	private F8Functions() {
	}

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	public static <A, B, C, D, E, F$, G, H, I> F7<B, C, D, E, F$, G, H, I> f(final F8<A, B, C, D, E, F$, G, H, I> func, final A a) {
		return (b, c, d, e, f, g, h) -> func.f(a, b, c, d, e, f, g, h);
	}

}
