package fj.function;

/**
 * A transformation function of arity-3 from <code>A</code>, <code>B</code> and <code>C</code> to <code>D</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F3</code>.
 *
 * @see fj.Try#f(Try3)
 * @version %build.number%
 */

public interface Try3<A, B, C, D, Z extends Exception> {

    D f(A a, B b, C c) throws Z;

}
