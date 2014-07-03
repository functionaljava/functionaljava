package fj;

/**
 * A transformation function of arity-3 from <code>A</code>, <code>B</code> and <code>C</code> to <code>D</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F3</code>.
 *
 * @see F3Functions#toF3
 * @version %build.number%
 */

public interface TryCatch3<A, B, C, D, E extends Exception> {

    D f(A a, B b, C c) throws E;

}
