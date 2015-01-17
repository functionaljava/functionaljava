package fj.function;


/**
 * A transformation function of arity-7 from <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code>, <code>F</code> and <code>G</code> to <code>H</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F7</code>.
 *
 * @see fj.Try#f(Try7)
 * @version %build.number%
 */

public interface Try7<A, B, C, D, E, F, G, H, Z extends Exception> {

    H f(A a, B b, C c, D d, E e, F f, G g) throws Z;

}
