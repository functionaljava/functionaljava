package fj.function;

/**
 * A transformation function of arity-2 from <code>A</code> and <code>B</code> to <code>C</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F2</code>.
 *
 * @see fj.Try#f(Try2)
 * @version %build.number%
 */

public interface Try2<A, B, C, Z extends Exception> {

    C f(A a, B b) throws Z;

}
