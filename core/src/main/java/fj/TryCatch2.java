package fj;

/**
 * A transformation function of arity-2 from <code>A</code> and <code>B</code> to <code>C</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F2</code>.
 *
 * @see F2Functions#toF2
 * @version %build.number%
 */

public interface TryCatch2<A, B, C, E extends Exception> {

    C f(A a, B b) throws E;

}
