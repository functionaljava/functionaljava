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
    static public <A, B, C, D, E> F4<A, B, C, D, Validation<Exception, E>> toF4(final TryCatch4<A, B, C, D, E> t) {
        return (a, b, c, d) -> {
            try {
                return Validation.success(t.f(a, b, c, d));
            } catch (Exception ex) {
                return Validation.fail(ex);
            }
        };
    }

}
