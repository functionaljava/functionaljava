package fj;

import fj.data.Validation;
import fj.function.Try4;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F4Functions {

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
