package fj.function;

/**
 * A transformation function of arity-6 from <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code> and <code>F</code> to <code>G</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F6</code>.
 *
 * @see fj.Try#f(Try6)
 * @version %build.number%
 */

public interface Try6<A, B, C, D, E, F, G, Z extends Exception> {

    G f(A a, B b, C c, D d, E e, F f) throws Z;

}
