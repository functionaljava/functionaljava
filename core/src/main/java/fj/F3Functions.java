package fj;

import fj.data.Validation;

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
    static public <A, B, C, D> F3<A, B, C, Validation<Exception, D>> toF3(final TryCatch3<A, B, C, D> t) {
        return (a, b, c) -> {
            try {
                return success(t.f(a, b, c));
            } catch (Exception e) {
                return fail(e);
            }
        };
    }

}
