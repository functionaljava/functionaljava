package fj.function;

/**
 * A product of <code>A</code> which may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to a <code>P1</code>.
 *
 * @see fj.Try#f(Try0)
 * @version %build.number%
 */

public interface Try0<A, Z extends Exception> {

    A f() throws Z;

}
