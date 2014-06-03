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

}
