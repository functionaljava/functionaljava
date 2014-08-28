package fj.function;

/**
 * A transformation function from <code>A</code> to <code>B</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F</code>.
 *
 * @see fj.F1Functions#toF1
 * @version %build.number%
 */

public interface Try1<A, B, E extends Exception> {

    B f(A a) throws E;

}
