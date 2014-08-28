package fj;

import fj.data.Validation;
import fj.function.Try8;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F8Functions {

    /**
     * Promotes the TryCatch8 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A TryCatch8 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, G, H, I, Z extends Exception> F8<A, B, C, D, E, F, G, H, Validation<Z, I>> toF8(final Try8<A, B, C, D, E, F, G, H, I, Z> t) {
        return (a, b, c, d, e, f, g, h) -> {
            try {
                return Validation.success(t.f(a, b, c, d, e, f, g, h));
            } catch (Exception ex) {
                return Validation.fail((Z) ex);
            }
        };
    }

	/**
	 * Partial application.
	 *
	 * @param a The <code>A</code> to which to apply this function.
	 * @return The function partially applied to the given argument.
	 */
	static public <A, B, C, D, E, F$, G, H, I> F7<B, C, D, E, F$, G, H, I> f(final F8<A, B, C, D, E, F$, G, H, I> func, final A a) {
		return (b, c, d, e, f, g, h) -> func.f(a, b, c, d, e, f, g, h);
	}

}
