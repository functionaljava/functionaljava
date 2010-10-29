package fj;

/**
 * A transformation function of arity-3 from <code>A</code>, <code>B</code> and <code>C</code> to
 * <code>D</code>. This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 413 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:31:32 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
public abstract class F3<A, B, C, D> {
  /**
   * Transform <code>A</code>, <code>B</code> and <code>C</code> to <code>D</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @return The result of the transformation.
   */
  public abstract D f(A a, B b, C c);
}
