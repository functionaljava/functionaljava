package fj;

/**
 * A transformation function from <code>A</code> to <code>B</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F</code>.
 *
 * @see F1Functions#toF1
 * @version %build.number%
 */

public interface TryCatch1<A, B> {

    B f(A a) throws Exception;

}
