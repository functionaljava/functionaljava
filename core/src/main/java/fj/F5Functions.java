package fj;

import fj.data.Validation;
import fj.function.Try5;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F5Functions {

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	static public <A, B, C, D, E, F$> F4<B, C, D, E, F$> f(final F5<A, B, C, D, E, F$> f, final A a) {
		return (b, c, d, e) -> f.f(a, b, c, d, e);
	}

}
