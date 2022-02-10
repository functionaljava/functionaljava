package fj.function;

import fj.F0;
import fj.P;
import fj.P1;
import fj.data.Option;
import fj.data.Validation;

import static fj.data.Validation.fail;
import static fj.data.Validation.success;

/**
 * A product of <code>A</code> which may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to a <code>P1</code>.
 *
 * @see fj.Try#f(Try0)
 */

public interface Try0<A, Z extends Exception> {

    A f() throws Z;

    @SuppressWarnings("unchecked")
    default F0<Validation<Z, A>> toF0() {
        return () -> {
            try {
                return success(f());
            } catch (Exception e) {
                return fail((Z) e);
            }
        };
    }

    default TryEffect0<Z> toTryEffect0() {
        return () -> f();
    }

    default Effect0 toEffect0() {
        return () -> {
            try {
                f();
            } catch (Exception e) {
            }
        };
    }

    default P1<Validation<Z, A>> toP1() {
        return P.lazy(() -> toF0().f());
    }

}
