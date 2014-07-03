package fj;

import fj.data.Validation;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F7Functions {

    /**
     * Promotes the TryCatch7 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A TryCatch7 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, G, H, Z extends Exception> F7<A, B, C, D, E, F, G, Validation<Z, H>> toF7(final TryCatch7<A, B, C, D, E, F, G, H, Z> t) {
        return (a, b, c, d, e, f, g) -> {
            try {
                return Validation.success(t.f(a, b, c, d, e, f, g));
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
	static public <A, B, C, D, E, F$, G, H> F6<B, C, D, E, F$, G, H> f(final F7<A, B, C, D, E, F$, G, H> func, final A a) {
		return (b, c, d, e, f, g) -> func.f(a, b, c, d, e, f, g);
	}


}
