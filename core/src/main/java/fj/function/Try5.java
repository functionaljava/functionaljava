package fj.function;

/**
 * A transformation function of arity-5 from <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code> and <code>E</code> to <code>F</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F5</code>.
 *
 * @see fj.Try#f(Try5)
 * @version %build.number%
 */

public interface Try5<A, B, C, D, E, F, Z extends Exception> {

    F f(A a, B b, C c, D d, E e) throws Z;

}
