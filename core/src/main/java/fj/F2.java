package fj;

import fj.control.parallel.Promise;
import fj.data.Array;
import fj.data.IterableW;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeZipper;
import fj.data.Zipper;

import static fj.data.Tree.node;
import static fj.P.p;
import static fj.data.IterableW.wrap;
import static fj.data.Set.iterableSet;
import static fj.data.TreeZipper.treeZipper;
import static fj.data.Zipper.zipper;

/**
 * A transformation function of arity-2 from <code>A</code> and <code>B</code> to <code>C</code>.
 * This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%
 */
public interface F2<A, B, C> {
  /**
   * Transform <code>A</code> and <code>B</code> to <code>C</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @return The result of the transformation.
   */
  public C f(A a, B b);

}
