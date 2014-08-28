package fj;

import fj.data.Validation;
import fj.function.Try3;

import static fj.data.Validation.fail;
import static fj.data.Validation.success;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F3Functions {

    /**
     * Promotes the TryCatch3 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A TryCatch3 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E extends Exception> F3<A, B, C, Validation<E, D>> toF3(final Try3<A, B, C, D, E> t) {
        return (a, b, c) -> {
            try {
                return success(t.f(a, b, c));
            } catch (Exception e) {
                return fail((E) e);
            }
        };
    }

    /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument.
     */
    static public <A, B, C, D> F2<B, C, D> f(final F3<A, B, C, D> f, final A a) {
        return (b, c) -> f.f(a, b, c);
    }

}
