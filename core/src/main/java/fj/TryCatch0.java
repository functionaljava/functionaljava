package fj;

/**
 * A product of <code>A</code> which may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to a <code>P1</code>.
 *
 * @see P1Functions#toP1
 * @version %build.number%
 */

public interface TryCatch0<A, E extends Exception> {

    A f() throws E;

}
