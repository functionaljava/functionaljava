package fj.function;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.Function;
import fj.Monoid;
import fj.P1;
import fj.P2;
import fj.data.List;
import fj.data.Option;

import static fj.Function.compose;
import static fj.Function.curry;
import static fj.data.List.lookup;

/**
 * The essence of the visitor design pattern expressed polymorphically.
 *
 * @version %build.number%
 */
public final class Visitor {
  private Visitor() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the first value available in the given list of optional values. If none is found return the given default value.
   *
   * @param values The optional values to search.
   * @param def The default value if no value is found in the list.
   * @return The first value available in the given list of optional values. If none is found return the given default value.
   */
  public static <X> X findFirst(final List<Option<X>> values, final P1<X> def) {
    return Monoid.<X>firstOptionMonoid().sumLeft(values).orSome(def);
  }

  /**
   * Returns the first non-<code>null</code> value in the given list of optional values. If none is found return the given default value.
   *
   * @param values The potentially <code>null</code> values to search.
   * @param def The default value if no value is found in the list.
   * @return The first non-<code>null</code> value in the given list of optional values. If none is found return the given default value.
   */
  public static <X> X nullablefindFirst(final List<X> values, final P1<X> def) {
    return findFirst(values.map(Option.<X>fromNull()), def);
  }

  /**
   * Returns the first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   *
   * @param visitors The list of visitors to apply.
   * @param def The default if none of the visitors yield a value.
   * @param value The value to apply to the visitors.
   * @return The first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   */
  public static <A, B> B visitor(final List<F<A, Option<B>>> visitors, final P1<B> def, final A value) {
    return findFirst(visitors.map(Function.<A, Option<B>>apply(value)), def);
  }

  /**
   * Returns the first non-<code>null</code> value found in the list of visitors after application of the given value,
   * otherwise returns the given default.
   *
   * @param visitors The list of visitors to apply looking for a non-<code>null</code>.
   * @param def The default if none of the visitors yield a non-<code>null</code> value.
   * @param value The value to apply to the visitors.
   * @return The first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   */
  public static <A, B> B nullableVisitor(final List<F<A, B>> visitors, final P1<B> def, final A value) {
    return visitor(visitors.map(new F<F<A, B>, F<A, Option<B>>>() {
      public F<A, Option<B>> f(final F<A, B> k) {
        return compose(Option.<B>fromNull(), k);
      }
    }), def, value);
  }

  /**
   * Uses an association list to perform a lookup with equality and returns a function that can be applied to a default,
   * followed by the associated key to return a value.
   *
   * @param x The association list.
   * @param eq The equality for the association list keys.
   * @return A function that can be applied to a default value (there is no association) and an associated key.
   */
  public static <A, B> F<B, F<A, B>> association(final List<P2<A, B>> x, final Equal<A> eq) {
    return curry(new F2<B, A, B>() {
      public B f(final B def, final A a) {
        return lookup(eq, x, a).orSome(def);
      }
    });
  }

  /**
   * Uses an association list to perform a lookup with equality and returns a function that can be applied to a default,
   * followed by the associated key to return a value.
   *
   * @param x The association list.
   * @param eq The equality for the association list keys.
   * @return A function that can be applied to a default value (there is no association) and an associated key.
   */
  public static <A, B> F<P1<B>, F<A, B>> associationLazy(final List<P2<A, B>> x, final Equal<A> eq) {
    return curry(new F2<P1<B>, A, B>() {
      public B f(final P1<B> def, final A a) {
        return lookup(eq, x, a).orSome(def);
      }
    });
  }
}
