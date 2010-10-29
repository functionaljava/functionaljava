package fj.data.fingertrees;

import fj.Monoid;
import fj.F;

/**
 * Determines how the elements of a tree are measured and how measures are summed. Consists of a monoid and a
 * measuring function. Different instances of this class will result in different behaviours for the tree.
 */
public final class Measured<V, A> {
  private final Monoid<V> m;
  private final F<A, V> measure;

  private Measured(final Monoid<V> m, final F<A, V> measure) {
    this.m = m;
    this.measure = measure;
  }

  public static <V, A> Measured<V, A> measured(final Monoid<V> m, final F<A, V> measure) {
    return new Measured<V, A>(m, measure);
  }

  /**
   * Returns the monoid used to sum measures.
   *
   * @return the monoid used to sum measures.
   */
  public Monoid<V> monoid() {
    return m;
  }

  /**
   * Returns the measuring function.
   *
   * @return the measuring function.
   */
  public F<A, V> measure() {
    return measure;
  }

  /**
   * Measures a given element.
   *
   * @param a An element to measure.
   * @return the element's measurement.
   */
  public V measure(final A a) {
    return measure.f(a);
  }

  /**
   * Sums the given measurements with the monoid.
   *
   * @param a A measurement to add to another.
   * @param b A measurement to add to another.
   * @return The sum of the two measurements.
   */
  public V sum(final V a, final V b) {
    return m.sum(a, b);
  }

  /**
   * Returns the identity measurement for the monoid.
   *
   * @return the identity measurement for the monoid.
   */
  public V zero() {
    return m.zero();
  }

  /**
   * A measured instance for nodes.
   *
   * @return A measured instance for nodes.
   */
  public Measured<V, Node<V, A>> nodeMeasured() {
    return new Measured<V, Node<V, A>>(m, new F<Node<V, A>, V>() {
      public V f(final Node<V, A> node) {
        return node.measure();
      }
    });
  }

  /**
   * A measured instance for digits.
   *
   * @return A measured instance for digits.
   */
  public Measured<V, Digit<V, A>> digitMeasured() {
    return new Measured<V, Digit<V, A>>(m, new F<Digit<V, A>, V>() {
      public V f(final Digit<V, A> d) {
        return d.measure();
      }
    });
  }

}
