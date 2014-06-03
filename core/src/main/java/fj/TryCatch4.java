package fj;

/**
 * A transformation function of arity-4 from <code>A</code>, <code>B</code>, <code>C</code> and <code>D</code> to <code>E</code> that may throw an <code>Exception</code>.
 *
 * Used to instantiate a lambda that may throw an <code>Exception</code> before converting to an <code>F4</code>.
 *
 * @see F4Functions#toF4
 * @version %build.number%
 */

public interface TryCatch4<A, B, C, D, E> {

    E f(A a, B b, C c, D d) throws Exception;

}
