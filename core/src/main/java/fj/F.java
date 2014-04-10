package fj;

import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.Array;
import fj.data.Either;
import fj.data.IterableW;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeZipper;
import fj.data.Validation;
import fj.data.Zipper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

/**
 * A transformation or function from <code>A</code> to <code>B</code>. This type can be represented
 * using the Java 7 closure syntax.
 *
 * @version %build.number%
 */
public interface F<A, B> {
  /**
   * Transform <code>A</code> to <code>B</code>.
   *
   * @param a The <code>A</code> to transform.
   * @return The result of the transformation.
   */
  public abstract B f(A a);

}
