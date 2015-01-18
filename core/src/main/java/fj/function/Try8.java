package fj.function;

/**
 * A transformation function of arity-8 from <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code>, <code>F</code>, <code>G</code> and <code>H</code> to <code>I</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F8</code>.
 *
 * @see fj.Try#f(Try8)
 * @version %build.number%
 */

public interface Try8<A, B, C, D, E, F, G, H, I, Z extends Exception> {

    I f(A a, B b, C c, D d, E e, F f, G g, H h) throws Z;

}
