package fj;

import fj.data.Validation;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F6Functions {

    /**
     * Promotes the TryCatch6 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A TryCatch6 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, G> F6<A, B, C, D, E, F, Validation<Exception, G>> toF6(final TryCatch6<A, B, C, D, E, F, G> t) {
        return (a, b, c, d, e, f) -> {
            try {
                return Validation.success(t.f(a, b, c, d, e, f));
            } catch (Exception ex) {
                return Validation.fail(ex);
            }
        };
    }


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
