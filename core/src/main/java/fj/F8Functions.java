package fj;

import fj.data.Validation;

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
    static public <A, B, C, D, E, F, G, H, I> F8<A, B, C, D, E, F, G, H, Validation<Exception, I>> toF8(final TryCatch8<A, B, C, D, E, F, G, H, I> t) {
        return (a, b, c, d, e, f, g, h) -> {
            try {
                return Validation.success(t.f(a, b, c, d, e, f, g, h));
            } catch (Exception ex) {
                return Validation.fail(ex);
            }
        };
    }

}
