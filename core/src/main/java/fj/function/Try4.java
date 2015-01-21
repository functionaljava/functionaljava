package fj.function;

/**
 * A transformation function of arity-4 from <code>A</code>, <code>B</code>, <code>C</code> and <code>D</code> to <code>E</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F4</code>.
 *
 * @see fj.Try#f(Try4)
 * @version %build.number%
 */

public interface Try4<A, B, C, D, E, Z extends Exception> {

    E f(A a, B b, C c, D d) throws Z;

}
