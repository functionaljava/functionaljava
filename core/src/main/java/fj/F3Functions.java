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
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument.
     */
    static public <A, B, C, D> F2<B, C, D> f(final F3<A, B, C, D> f, final A a) {
        return (b, c) -> f.f(a, b, c);
    }

}
