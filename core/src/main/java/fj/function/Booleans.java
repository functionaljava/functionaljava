package fj.function;


import static fj.Function.*;

import fj.F;
import fj.Monoid;
import fj.Semigroup;
import fj.data.List;
import fj.data.Stream;

import static fj.Semigroup.disjunctionSemigroup;
import static fj.Semigroup.conjunctionSemigroup;
import static fj.Semigroup.exclusiveDisjunctionSemiGroup;

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
  public static final F<Boolean, F<Boolean, Boolean>> implies = curry((p, q) -> !p || q);

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
     * maps given function to the predicate function
     * @param p predicate to be mapped over
     * @param f function
     * @return predicate function
     */
    public static <A, B>  F<B, Boolean> contramap(F<B, A> f, F<A, Boolean> p){
        return compose(p, f);
    }

    /**
     * alias for contramap
     * @param p predicate to be mapped over
     * @param f function
     * @return predicate function
     */
    public static <A, B>  F<B, Boolean> is(F<B, A> f, F<A, Boolean> p){
        return contramap(f, p);
    }

    /**
     * returns inverse of contramap
     * @param p predicate to be mapped over
     * @param f function
     * @return predicate function
     */
    public static <A, B>  F<B, Boolean> isnot(F<B, A> f, F<A, Boolean> p){
        return compose(not, contramap(f, p));
    }

    /**
     * composes the given predicate using conjunction
     * @param p1 first predicate
     * @param p2 second predicate
     * @return composed predicate function
     */
    public static <A>  F<A, Boolean> and(F<A, Boolean> p1, F<A, Boolean> p2){
        return Semigroup.<A, Boolean>functionSemigroup(conjunctionSemigroup).sum(p1, p2);
    }

    /**
     * composes the given predicate using exclusive disjunction
     * @param p1 first predicate
     * @param p2 second predicate
     * @return composed predicate function
     */
    public static <A>  F<A, Boolean> xor(F<A, Boolean> p1, F<A, Boolean> p2){
        return Semigroup.<A, Boolean>functionSemigroup(exclusiveDisjunctionSemiGroup).sum(p1, p2);
    }

    /**
     * returns composed predicate using disjunction
     * @param p1 first predicate
     * @param p2 second predicate
     * @return composed predicate
     */
    public static <A>  F<A, Boolean> or(F<A, Boolean> p1, F<A, Boolean> p2){
        return Semigroup.<A, Boolean>functionSemigroup(disjunctionSemigroup).sum(p1, p2);
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
   * Returns composed predicate
   *
   * @param l A stream of predicates
   * @return composed predicate
   */
  public static <A> F<A, Boolean> andAll(final Stream<F<A, Boolean>> l) {
    return Monoid.<A, Boolean>functionMonoid(Monoid.conjunctionMonoid).sumLeft(l);
  }

  /**
   * Returns a composed predicate of given List of predicates
   *
   * @param l A list of predicate functions
   * @return composed predicate function
   */
  public static <A> F<A, Boolean> andAll(final List<F<A, Boolean>> l) {
    return Monoid.<A, Boolean>functionMonoid(Monoid.conjunctionMonoid).sumLeft(l);
  }

  /**
   * Returns a composed predicate of given List of predicates
   *
   * @param l A list of predicate functions
   * @return composed predicate function
   */
  public static <A> F<A, Boolean> orAll(final List<F<A, Boolean>> l) {
    return Monoid.<A, Boolean>functionMonoid(Monoid.disjunctionMonoid).sumLeft(l);
  }

  /**
   * Returns a composed predicate of given Stream of predicates
   *
   * @param l A stream of predicate functions
   * @return composed predicate function
   */
  public static <A> F<A, Boolean> orAll(final Stream<F<A, Boolean>> l) {
    return Monoid.<A, Boolean>functionMonoid(Monoid.disjunctionMonoid).sumLeft(l);
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
    return curry((p, a1, a2) -> p ? a1 : a2);
  }
}
