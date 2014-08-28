package fj;

import fj.data.Validation;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F4Functions {


    /**
     * Promotes the TryCatch4 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A TryCatch4 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, Z extends Exception> F4<A, B, C, D, Validation<Z, E>> toF4(final Try4<A, B, C, D, E, Z> t) {
        return (a, b, c, d) -> {
            try {
                return Validation.success(t.f(a, b, c, d));
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
    static public <A, B, C, D, E> F3<B, C, D, E> f(final F4<A, B, C, D, E> f, final A a) {
        return (b, c, d) -> f.f(a, b, c, d);
    }

}
