package fj.data.vector;

import fj.F0;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.P;
import fj.P1;

/**
 * Functions across vectors.
 */
public final class V {

  private V() {
  }

  /**
   * Puts elements in a vector-2.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @return The vector-2.
   */
  public static <A> V2<A> v(final A a1, final A a2) {
    return V2.p(P.p(a1, a2));
  }

  /**
   * Puts elements in a vector-2.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @return The vector-2.
   */
  public static <A> V2<A> v(final F0<A> a1, final F0<A> a2) {
      return V2.p(P.lazy(a1, a2));
  }

  /**
   * Returns a function that puts elements in a vector-2.
   *
   * @return A function that puts elements in a vector-2.
   */
  public static <A> F2<A, A, V2<A>> v2() {
    return V::v;
  }

  /**
   * Puts elements in a vector-3.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @return The vector-3.
   */
  public static <A> V3<A> v(final A a1, final A a2, final A a3) {
    return V3.p(P.p(a1, a2, a3));
  }

  /**
   * Puts elements in a vector-3.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @return The vector-3.
   */
  public static <A> V3<A> v(final P1<A> a1, final F0<A> a2, final F0<A> a3) {
    return V3.cons(a1, v(a2, a3));
  }

  /**
   * Returns a function that puts elements in a vector-3.
   *
   * @return A function that puts elements in a vector-3.
   */
  public static <A> F3<A, A, A, V3<A>> v3() {
    return V::v;
  }

  /**
   * Puts elements in a vector-4.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @param a4 An element to put in a vector.
   * @return The vector-4.
   */
  public static <A> V4<A> v(final A a1, final A a2, final A a3, final A a4) {
    return V4.p(P.p(a1, a2, a3, a4));
  }

  /**
   * Puts elements in a vector-4.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @param a4 An element to put in a vector.
   * @return The vector-4.
   */
  public static <A> V4<A> v(final P1<A> a1, final P1<A> a2, final F0<A> a3, final F0<A> a4) {
    return V4.cons(a1, v(a2, a3, a4));
  }

  /**
   * Returns a function that puts elements in a vector-4.
   *
   * @return A function that puts elements in a vector-4.
   */
  public static <A> F4<A, A, A, A, V4<A>> v4() {
    return V::v;
  }


  /**
   * Puts elements in a vector-5.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @param a4 An element to put in a vector.
   * @param a5 An element to put in a vector.
   * @return The vector-5.
   */
  public static <A> V5<A> v(final A a1, final A a2, final A a3, final A a4, final A a5) {
    return V5.p(P.p(a1, a2, a3, a4, a5));
  }

  /**
   * Puts elements in a vector-5.
   *
   * @param a1 An element to put in a vector.
   * @param a2 An element to put in a vector.
   * @param a3 An element to put in a vector.
   * @param a4 An element to put in a vector.
   * @param a5 An element to put in a vector.
   * @return The vector-5.
   */
  public static <A> V5<A> v(final P1<A> a1, final P1<A> a2, final P1<A> a3, final F0<A> a4, final F0<A> a5) {
    return V5.cons(a1, v(a2, a3, a4, a5));
  }

  /**
   * Returns a function that puts elements in a vector-5.
   *
   * @return A function that puts elements in a vector-5.
   */
  public static <A> F5<A, A, A, A, A, V5<A>> v5() {
    return V::v;
  }

}
