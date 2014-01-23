package fj.function;

import static fj.Function.compose;
import static fj.Function.compose2;
import static fj.Function.curry;
import static fj.Function.flip;
import static fj.Semigroup.conjunctionSemigroup;
import static fj.Semigroup.disjunctionSemigroup;
import static fj.Semigroup.exclusiveDisjunctionSemiGroup;
import fj.F;
import fj.F3;
import fj.Monoid;
import fj.data.List;
import fj.data.Stream;

/**
 * Curried logical functions.
 *
 * @version %build.number%
 */
public final class Booleans {
  private Booleans() {
    throw new UnsupportedOperationException();
  }

  /**
   * Curried form of logical "inclusive or" (disjunction).
   */
  public static final F<Boolean, F<Boolean, Boolean>> or = disjunctionSemigroup.sum();

  /**
   * Curried form of logical "and" (conjunction).
   */
  public static final F<Boolean, F<Boolean, Boolean>> and = conjunctionSemigroup.sum();


  /**
   * Curried form of logical xor (nonequivalence).
   */
  public static final F<Boolean, F<Boolean, Boolean>> xor = exclusiveDisjunctionSemiGroup.sum();

  /**
   * Logical negation.
   */
  public static final F<Boolean, Boolean> not = p -> !p;

  /**
   * Curried form of logical "only if" (material implication).
   */
  public static final F<Boolean, F<Boolean, Boolean>> implies = curry((p,q) -> !p || q);

  /**
   * Curried form of logical "if" (reverse material implication).
   */
  public static final F<Boolean, F<Boolean, Boolean>> if_ = flip(implies);

  /**
   * Curried form of logical "if and only if" (biconditional, equivalence).
   */
  public static final F<Boolean, F<Boolean, Boolean>> iff = compose2(not, xor);

  /**
   * Curried form of logical "not implies" (nonimplication).
   */
  public static final F<Boolean, F<Boolean, Boolean>> nimp = compose2(not, implies);

  /**
   * Curried form of logical "not if" (reverse nonimplication).
   */
  public static final F<Boolean, F<Boolean, Boolean>> nif = compose2(not, if_);

  /**
   * Curried form of logical "not or".
   */
  public static final F<Boolean, F<Boolean, Boolean>> nor = compose2(not, or);

  /**
   * Returns true if all the elements of the given list are true.
   *
   * @param l A list to check for all the elements being true.
   * @return true if all the elements of the given list are true. False otherwise.
   */
  public static boolean and(final List<Boolean> l) {
    return Monoid.conjunctionMonoid.sumLeft(l);
  }

  /**
   * Returns true if all the elements of the given stream are true.
   *
   * @param l A stream to check for all the elements being true.
   * @return true if all the elements of the given stream are true. False otherwise.
   */
  public static boolean and(final Stream<Boolean> l) {
    return Monoid.conjunctionMonoid.sumLeft(l);
  }

  /**
   * Returns true if any element of the given list is true.
   *
   * @param l A list to check for any element being true.
   * @return true if any element of the given list is true. False otherwise.
   */
  public static boolean or(final List<Boolean> l) {
    return Monoid.disjunctionMonoid.sumLeft(l);
  }

  /**
   * Returns true if any element of the given stream is true.
   *
   * @param l A stream to check for any element being true.
   * @return true if any element of the given stream is true. False otherwise.
   */
  public static boolean or(final Stream<Boolean> l) {
    return Monoid.disjunctionMonoid.sumLeft(l);
  }

  /**
   * Negates the given predicate.
   *
   * @param p A predicate to negate.
   * @return The negation of the given predicate.
   */
  public static <A> F<A, Boolean> not(final F<A, Boolean> p) {
    return compose(not, p);
  }

  /**
   * Curried form of conditional. If the first argument is true, returns the second argument,
   * otherwise the third argument.
   *
   * @return A function that returns its second argument if the first argument is true, otherwise the third argument.
   */
  public static <A> F<Boolean, F<A, F<A, A>>> cond() {
    return curry(new F3<Boolean, A, A, A>() {
      public A f(final Boolean p, final A a1, final A a2) {
        return p ? a1 : a2;
      }
    });
  }
}
