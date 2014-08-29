package fj;

import fj.data.Validation;
import fj.function.Try6;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F6Functions {

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	static public <A, B, C, D, E, F$, G> F5<B, C, D, E, F$, G> f(final F6<A, B, C, D, E, F$, G> func, final A a) {
		return (b, c, d, e, f) -> func.f(a, b, c, d, e, f);
	}


}
