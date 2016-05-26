package fj.data;

import fj.*;
import fj.function.Effect1;

import static fj.Function.curry;
import static fj.P.p;

import static fj.Unit.unit;
import static fj.Bottom.error;
import static fj.data.List.list;

import java.util.Iterator;

/**
 * Isomorphic to {@link Either} but has renamed functions and represents failure on the left and success on the right.
 * This type also has accumulating functions that accept a {@link Semigroup} for binding computation while keeping error
 * values
 *
 * @version %build.number%
 */
public class Validation<E, T> implements Iterable<T> {
  private final Either<E, T> e;

  protected Validation(final Either<E, T> e) {
    this.e = e;
  }

  /**
   * Returns <code>true</code> if this is a failure, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this is a failure, <code>false</code> otherwise.
   */
  public final boolean isFail() {
    return e.isLeft();
  }

  /**
   * Returns <code>true</code> if this is a success, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this is a success, <code>false</code> otherwise.
   */
  public final boolean isSuccess() {
    return e.isRight();
  }

  /**
   * Returns the failing value, or throws an error if there is no failing value.
   *
   * @return the failing value, or throws an error if there is no failing value.
   */
  public final E fail() {
    if (isFail())
      return e.left().value();
    else
      throw error("Validation: fail on success value");
  }

  /**
   * Returns the success value, or throws an error if there is no success value.
   *
   * @return the success value, or throws an error if there is no success value.
   */
  public final T success() {
    if (isSuccess())
      return e.right().value();
    else
      throw error("Validation: success on fail value");
  }

  /**
   * The catamorphism for validation. Folds over this validation breaking into left or right.
   *
   * @param fail    The function to call if this failed.
   * @param success The function to call if this succeeded.
   * @return The reduced value.
   */
  public final <X> X validation(final F<E, X> fail, final F<T, X> success) {
    return e.either(fail, success);
  }

  /**
   * Returns a failing projection of this validation.
   *
   * @return a failing projection of this validation.
   */
  public final FailProjection<E, T> f() {
    return new FailProjection<>(this);
  }

  /**
   * Returns an either projection of this validation.
   *
   * @return An either projection of this validation.
   */
  public final Either<E, T> toEither() {
    return e;
  }

  /**
   * Returns the success value or fails with the given error message.
   *
   * @param err The error message to fail with.
   * @return The success value.
   */
  public final T successE(final F0<String> err) {
    return e.right().valueE(err);
  }

  /**
   * Returns the success value or fails with the given error message.
   *
   * @param err The error message to fail with.
   * @return The success value.
   */
  public final T successE(final String err) {
    return e.right().valueE(p(err));
  }

  /**
   * Returns the success value or the given value.
   *
   * @param t The value to return if this is failure.
   * @return The success value or the given value.
   */
  public final T orSuccess(final F0<T> t) {
    return e.right().orValue(t);
  }

  /**
   * Returns the success value or the given value.
   *
   * @param t The value to return if this is failure.
   * @return The success value or the given value.
   */
  public final T orSuccess(final T t) {
    return e.right().orValue(p(t));
  }

  /**
   * The success value or the application of the given function to the failing value.
   *
   * @param f The function to execute on the failing value.
   * @return The success value or the application of the given function to the failing value.
   */
  public final T on(final F<E, T> f) {
    return e.right().on(f);
  }

  /**
   * Executes a side-effect on the success value if there is one.
   *
   * @param f The side-effect to execute.
   * @return The unit value.
   */
  public final Unit foreach(final F<T, Unit> f) {
    return e.right().foreach(f);
  }

  /**
   * Executes a side-effect on the success value if there is one.
   *
   * @param f The side-effect to execute.
   */
  public final void foreachDoEffect(final Effect1<T> f) {
    e.right().foreachDoEffect(f);
  }

  /**
   * Maps the given function across the success side of this validation.
   *
   * @param f The function to map.
   * @return A new validation with the function mapped.
   */
  @SuppressWarnings("unchecked")
  public final <A> Validation<E, A> map(final F<T, A> f) {
    return isFail() ?
        Validation.fail(fail()) :
        Validation.success(f.f(success()));
  }

  /**
   * Binds the given function across this validation's success value if it has one.
   *
   * @param f The function to bind across this validation.
   * @return A new validation value after binding.
   */
  @SuppressWarnings("unchecked")
  public final <A> Validation<E, A> bind(final F<T, Validation<E, A>> f) {
    return isSuccess() ? f.f(success()) : Validation.fail(fail());
  }

  /**
   * Anonymous bind through this validation.
   *
   * @param v The value to bind with.
   * @return A validation after binding.
   */
  public final <A> Validation<E, A> sequence(final Validation<E, A> v) {
    return bind(Function.constant(v));
  }

  /**
   * If list contains a failure, returns a failure of the reduction of
   * all the failures using the semigroup, otherwise returns the successful list.
   */
  public static <E, A> Validation<E, List<A>> sequence(final Semigroup<E> s, final List<Validation<E, A>> list) {
    if (list.exists(Validation::isFail)) {
      return Validation.fail(list.filter(Validation::isFail).map(v -> v.fail()).foldLeft1((F2<E, E, E>) s::sum));
    } else {
      return success(list.foldLeft((List<A> acc, Validation<E, A> v) -> acc.cons(v.success()), List.nil()).reverse());
    }
  }

  /**
   * Returns <code>None</code> if this is a failure or if the given predicate <code>p</code> does not hold for the
   * success value, otherwise, returns a success in <code>Some</code>.
   *
   * @param f The predicate function to test on this success value.
   * @return <code>None</code> if this is a failure or if the given predicate <code>p</code> does not hold for the
   *         success value, otherwise, returns a success in <code>Some</code>.
   */
  public final <A> Option<Validation<A, T>> filter(final F<T, Boolean> f) {
    return e.right().<A>filter(f).map(Validation.validation());
  }

  /**
   * Function application on the success value.
   *
   * @param v The validation of the function to apply on the success value.
   * @return The result of function application in validation.
   */
  public final <A> Validation<E, A> apply(final Validation<E, F<T, A>> v) {
    return v.bind(this::map);
  }

  /**
   * Returns <code>true</code> if this is a failure or returns the result of the application of the given
   * function to the success value.
   *
   * @param f The predicate function to test on this success value.
   * @return <code>true</code> if this is a failure or returns the result of the application of the given
   *         function to the success value.
   */
  public final boolean forall(final F<T, Boolean> f) {
    return e.right().forall(f);
  }

  /**
   * Returns <code>false</code> if this is a failure or returns the result of the application of the given
   * function to the success value.
   *
   * @param f The predicate function to test on this success value.
   * @return <code>false</code> if this is a failure or returns the result of the application of the given
   *         function to the success value.
   */
  public final boolean exists(final F<T, Boolean> f) {
    return e.right().exists(f);
  }

  @Override
  public final boolean equals(Object other) {
    return Equal.equals0(Validation.class, this, other, () -> Equal.validationEqual(Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public final int hashCode() {
    return Hash.validationHash(Hash.<E>anyHash(), Hash.<T>anyHash()).hash(this);
  }

  /**
   * Returns a single element list if this is a success value, otherwise an empty list.
   *
   * @return A single element list if this is a success value, otherwise an empty list.
   */
  public final List<T> toList() {
    return e.right().toList();
  }

  /**
   * Returns the success value in <code>Some</code> if there is one, otherwise <code>None</code>.
   *
   * @return The success value in <code>Some</code> if there is one, otherwise <code>None</code>.
   */
  public final Option<T> toOption() {
    return e.right().toOption();
  }

  /**
   * Returns a single element array if this is a success value, otherwise an empty list.
   *
   * @return A single element array if this is a success value, otherwise an empty list.
   */
  public final Array<T> toArray() {
    return e.right().toArray();
  }

  /**
   * Returns a single element stream if this is a success value, otherwise an empty list.
   *
   * @return A single element stream if this is a success value, otherwise an empty list.
   */
  public final Stream<T> toStream() {
    return e.right().toStream();
  }

  /**
   * Function application on the successful side of this validation, or accumulating the errors on the failing side
   * using the given semigroup should one or more be encountered.
   *
   * @param s The semigroup to accumulate errors with if
   * @param v The validating function to apply.
   * @return A failing validation if this or the given validation failed (with errors accumulated if both) or a
   *         succeeding validation if both succeeded.
   */
  @SuppressWarnings("unchecked")
  public final <A> Validation<E, A> accumapply(final Semigroup<E> s, final Validation<E, F<T, A>> v) {
    return isFail() ?
        Validation.fail(v.isFail() ?
            s.sum(v.fail(), fail()) :
            fail()) :
        v.isFail() ?
            Validation.fail(v.fail()) :
            Validation.success(v.success().f(success()));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B> Validation<E, B> accumulate(final Semigroup<E> s, final Validation<E, A> va, final F<T, F<A, B>> f) {
    return va.accumapply(s, map(f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B> Validation<E, B> accumulate(final Semigroup<E> s, final Validation<E, A> va, final F2<T, A, B> f) {
    return va.accumapply(s, map(curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va) {
    return accumulate(s, va, (t, a) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C> Validation<E, C> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                     final Validation<E, B> vb, final F<T, F<A, F<B, C>>> f) {
    return vb.accumapply(s, accumulate(s, va, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C> Validation<E, C> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                     final Validation<E, B> vb, final F3<T, A, B, C> f) {
    return vb.accumapply(s, accumulate(s, va, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va, final Validation<E, B> vb) {
    return accumulate(s, va, vb, (t, a, b) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D> Validation<E, D> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                        final Validation<E, B> vb, final Validation<E, C> vc,
                                                        final F<T, F<A, F<B, F<C, D>>>> f) {
    return vc.accumapply(s, accumulate(s, va, vb, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D> Validation<E, D> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                        final Validation<E, B> vb, final Validation<E, C> vc,
                                                        final F4<T, A, B, C, D> f) {
    return vc.accumapply(s, accumulate(s, va, vb, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B, C> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va, final Validation<E, B> vb,
                                              final Validation<E, C> vc) {
    return accumulate(s, va, vb, vc, (t, a, b, c) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$> Validation<E, E$> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                             final Validation<E, B> vb, final Validation<E, C> vc,
                                                             final Validation<E, D> vd,
                                                             final F<T, F<A, F<B, F<C, F<D, E$>>>>> f) {
    return vd.accumapply(s, accumulate(s, va, vb, vc, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$> Validation<E, E$> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                             final Validation<E, B> vb, final Validation<E, C> vc,
                                                             final Validation<E, D> vd, final F5<T, A, B, C, D, E$> f) {
    return vd.accumapply(s, accumulate(s, va, vb, vc, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B, C, D> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va, final Validation<E, B> vb,
                                                 final Validation<E, C> vc, final Validation<E, D> vd) {
    return accumulate(s, va, vb, vc, vd, (t, a, b, c, d) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$> Validation<E, F$> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                 final Validation<E, B> vb, final Validation<E, C> vc,
                                                                 final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                 final F<T, F<A, F<B, F<C, F<D, F<E$, F$>>>>>> f) {
    return ve.accumapply(s, accumulate(s, va, vb, vc, vd, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$> Validation<E, F$> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                 final Validation<E, B> vb, final Validation<E, C> vc,
                                                                 final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                 final F6<T, A, B, C, D, E$, F$> f) {
    return ve.accumapply(s, accumulate(s, va, vb, vc, vd, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B, C, D, E$> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                     final Validation<E, B> vb, final Validation<E, C> vc,
                                                     final Validation<E, D> vd, final Validation<E, E$> ve) {
    return accumulate(s, va, vb, vc, vd, ve, (t, a, b, c, d, e1) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$, G> Validation<E, G> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                   final Validation<E, B> vb, final Validation<E, C> vc,
                                                                   final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                   final Validation<E, F$> vf,
                                                                   final F<T, F<A, F<B, F<C, F<D, F<E$, F<F$, G>>>>>>> f) {
    return vf.accumapply(s, accumulate(s, va, vb, vc, vd, ve, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$, G> Validation<E, G> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                   final Validation<E, B> vb, final Validation<E, C> vc,
                                                                   final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                   final Validation<E, F$> vf,
                                                                   final F7<T, A, B, C, D, E$, F$, G> f) {
    return vf.accumapply(s, accumulate(s, va, vb, vc, vd, ve, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B, C, D, E$, F$> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                         final Validation<E, B> vb, final Validation<E, C> vc,
                                                         final Validation<E, D> vd, final Validation<E, E$> ve,
                                                         final Validation<E, F$> vf) {
    return accumulate(s, va, vb, vc, vd, ve, vf, (t, a, b, c, d, e1, f) -> unit()).f().toOption();
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @param vg The eighth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$, G, H> Validation<E, H> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                      final Validation<E, B> vb, final Validation<E, C> vc,
                                                                      final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                      final Validation<E, F$> vf, final Validation<E, G> vg,
                                                                      final F<T, F<A, F<B, F<C, F<D, F<E$, F<F$, F<G, H>>>>>>>> f) {
    return vg.accumapply(s, accumulate(s, va, vb, vc, vd, ve, vf, f));
  }

  /**
   * Accumulates errors on the failing side of this or any given validation if one or more are encountered, or applies
   * the given function if all succeeded and returns that value on the successful side.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @param vg The eighth validation to accumulate errors with if it failed.
   * @param f  The function to apply if all validations have succeeded.
   * @return A succeeding validation if all validations succeeded, or a failing validation with errors accumulated if
   *         one or more failed.
   */
  public final <A, B, C, D, E$, F$, G, H> Validation<E, H> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                                      final Validation<E, B> vb, final Validation<E, C> vc,
                                                                      final Validation<E, D> vd, final Validation<E, E$> ve,
                                                                      final Validation<E, F$> vf, final Validation<E, G> vg,
                                                                      final F8<T, A, B, C, D, E$, F$, G, H> f) {
    return vg.accumapply(s, accumulate(s, va, vb, vc, vd, ve, vf, curry(f)));
  }

  /**
   * Accumulates errors anonymously.
   *
   * @param s  The semigroup to accumulate errors with if one or more validations fail.
   * @param va The second validation to accumulate errors with if it failed.
   * @param vb The third validation to accumulate errors with if it failed.
   * @param vc The fourth validation to accumulate errors with if it failed.
   * @param vd The fifth validation to accumulate errors with if it failed.
   * @param ve The sixth validation to accumulate errors with if it failed.
   * @param vf The seventh validation to accumulate errors with if it failed.
   * @param vg The eighth validation to accumulate errors with if it failed.
   * @return A <code>Some</code> if one or more validations failed (accumulated with the semigroup), otherwise,
   *         <code>None</code>.
   */
  public final <A, B, C, D, E$, F$, G> Option<E> accumulate(final Semigroup<E> s, final Validation<E, A> va,
                                                            final Validation<E, B> vb, final Validation<E, C> vc,
                                                            final Validation<E, D> vd, final Validation<E, E$> ve,
                                                            final Validation<E, F$> vf, final Validation<E, G> vg) {
    return accumulate(s, va, vb, vc, vd, ve, vf, vg, (t, a, b, c, d, e1, f, g) -> unit()).f().toOption();
  }

  /**
   * Returns an iterator for this validation. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this validation.
   */
  public final Iterator<T> iterator() {
    return toEither().right().iterator();
  }


    public final Validation<List<E>, T> accumulate() {
        if (isFail()) {
            return fail(List.single(fail()));
        } else {
            return success(success());
        }
    }

    public final <B> Validation<List<E>, B> accumulate(F<T, B> f) {
        if (isFail()) {
            return fail(List.single(fail()));
        } else {
            return success(f.f(success()));
        }
    }


    public final <B, C> Validation<List<E>, C> accumulate(Validation<E, B> v2, F2<T, B, C> f) {
        List<E> list = List.nil();
        if (isFail()) {
            list = list.cons(fail());
        }
        if (v2.isFail()) {
            list = list.cons(v2.fail());
        }
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success()));
        }
    }



    public final <B, C, D> Validation<List<E>, D> accumulate(Validation<E, B> v2, Validation<E, C> v3, F3<T, B, C, D> f) {
        List<E> list = fails(list(this, v2, v3));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success()));
        }
    }

    public final <B, C, D, $E> Validation<List<E>, $E> accumulate(Validation<E, B> v2, Validation<E, C> v3, Validation<E, D> v4, F4<T, B, C, D, $E> f) {
        List<E> list = fails(list(this, v2, v3, v4));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success(), v4.success()));
        }
    }

    public final <B, C, D, $E, $F> Validation<List<E>, $F> accumulate(Validation<E, B> v2, Validation<E, C> v3, Validation<E, D> v4, Validation<E, $E> v5, F5<T, B, C, D, $E, $F> f) {
        List<E> list = fails(list(this, v2, v3, v4, v5));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success(), v4.success(), v5.success()));
        }
    }


    public final <B, C, D, $E, $F, G> Validation<List<E>, G> accumulate(Validation<E, B> v2, Validation<E, C> v3, Validation<E, D> v4, Validation<E, $E> v5, Validation<E, $F> v6, F6<T, B, C, D, $E, $F, G> f) {
        List<E> list = fails(list(this, v2, v3, v4, v5));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success(), v4.success(), v5.success(), v6.success()));
        }
    }

    public final <B, C, D, $E, $F, G, H> Validation<List<E>, H> accumulate(Validation<E, B> v2, Validation<E, C> v3, Validation<E, D> v4, Validation<E, $E> v5, Validation<E, $F> v6, Validation<E, G> v7, F7<T, B, C, D, $E, $F, G, H> f) {
        List<E> list = fails(list(this, v2, v3, v4, v5));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success(), v4.success(), v5.success(), v6.success(), v7.success()));
        }
    }

    public final <B, C, D, $E, $F, G, H, I> Validation<List<E>, I> accumulate(Validation<E, B> v2, Validation<E, C> v3, Validation<E, D> v4, Validation<E, $E> v5, Validation<E, $F> v6, Validation<E, G> v7, Validation<E, H> v8, F8<T, B, C, D, $E, $F, G, H, I> f) {
        List<E> list = fails(list(this, v2, v3, v4, v5));
        if (!list.isEmpty()) {
            return fail(list);
        } else {
            return success(f.f(success(), v2.success(), v3.success(), v4.success(), v5.success(), v6.success(), v7.success(), v8.success()));
        }
    }

  /**
   * If the list contains a failure, returns a Validation of the list of
   * fails in the list, otherwise returns a successful Validation with
   * the list of successful values.  Does not accumulate the failures into a
   * single failure using a semigroup.
   */
    public static <A, E> Validation<List<E>, List<A>> sequenceNonCumulative(List<Validation<E, A>> list) {
      if (list.exists(Validation::isFail)) {
        F2<List<E>, Validation<E, A>, List<E>> f = (acc, v) -> acc.cons(v.fail());
        return fail(list.filter(Validation::isFail).foldLeft(f, List.nil()).reverse());
      } else {
        F2<List<A>, Validation<E, A>, List<A>> f = (acc, v) -> acc.cons(v.success());
        return success(list.filter(Validation::isSuccess).foldLeft(f, List.nil()).reverse());
      }
    }

    public final <C> List<Validation<E, C>> traverseList(F<T, List<C>> f){
        return isSuccess() ?
            f.f(success()).map(Validation::success) :
            List.iterableList(fail(e.left().value()));
    }

    public final <C> Stream<Validation<E, C>> traverseStream(F<T, Stream<C>> f){
        return isSuccess() ?
            f.f(success()).map(Validation::success) :
            Stream.iterableStream(fail(e.left().value()));
    }

    public final <C> Option<Validation<E, C>> traverseOption(F<T, Option<C>> f){
        return isSuccess() ?
            f.f(success()).map(Validation::success) :
            Option.some(fail(e.left().value()));
    }

    public final <C> IO<Validation<E, C>> traverseIO(F<T, IO<C>> f){
        return isSuccess() ?
            IOFunctions.map(f.f(success()), Validation::success) :
            IOFunctions.unit(fail(e.left().value()));
    }

    public final <C> P1<Validation<E, C>> traverseP1(F<T, P1<C>> f){
        return isSuccess() ?
                f.f(success()).map(Validation::success) :
                p(fail(e.left().value()));
    }


    public static <A, E> List<E> fails(List<Validation<E, ?>> list) {
        return list.filter(Validation::isFail).map(v -> v.fail());
    }

    public static <A, E> List<A> successes(List<Validation<?, A>> list) {
        return list.filter(Validation::isSuccess).map(v -> v.success());
    }

  /**
   * A failing projection of a validation.
   */
  public static final class FailProjection<E, T> implements Iterable<E> {
    private final Validation<E, T> v;

    private FailProjection(final Validation<E, T> v) {
      this.v = v;
    }

    /**
     * Returns the underlying validation.
     *
     * @return The underlying validation.
     */
    public Validation<E, T> validation() {
      return v;
    }

    /**
     * Returns the failing value or fails with the given error message.
     *
     * @param err The error message to fail with.
     * @return The failing value.
     */
    public E failE(final F0<String> err) {
      return v.toEither().left().valueE(err);
    }

    /**
     * Returns the failing value or fails with the given error message.
     *
     * @param err The error message to fail with.
     * @return The failing value.
     */
    public E failE(final String err) {
      return failE(p(err));
    }

    /**
     * Returns the failing value or the given value.
     *
     * @param e The value to return if this is success.
     * @return The failing value or the given value.
     */
    public E orFail(final F0<E> e) {
      return v.toEither().left().orValue(e);
    }

    /**
     * Returns the failing value or the given value.
     *
     * @param e The value to return if this is success.
     * @return The failing value or the given value.
     */
    public E orFail(final E e) {
      return orFail(p(e));
    }

    /**
     * The failing value or the application of the given function to the success value.
     *
     * @param f The function to execute on the success value.
     * @return The failing value or the application of the given function to the success value.
     */
    public E on(final F<T, E> f) {
      return v.toEither().left().on(f);
    }

    /**
     * Executes a side-effect on the failing value if there is one.
     *
     * @param f The side-effect to execute.
     * @return The unit value.
     */
    public Unit foreach(final F<E, Unit> f) {
      return v.toEither().left().foreach(f);
    }

    /**
     * Executes a side-effect on the failing value if there is one.
     *
     * @param f The side-effect to execute.
     */
    public void foreachDoEffect(final Effect1<E> f) {
      v.toEither().left().foreachDoEffect(f);
    }

    /**
     * Maps the given function across the failing side of this validation.
     *
     * @param f The function to map.
     * @return A new validation with the function mapped.
     */
    public <A> Validation<A, T> map(final F<E, A> f) {
      return Validation.validation(v.toEither().left().map(f));
    }

    /**
     * Binds the given function across this validation's failing value if it has one.
     *
     * @param f The function to bind across this validation.
     * @return A new validation value after binding.
     */
    public <A> Validation<A, T> bind(final F<E, Validation<A, T>> f) {
      return v.isFail() ? f.f(v.fail()) : Validation.success(v.success());
    }

    /**
     * Performs a bind across the validation, but ignores the element value in the function.
     *
     * @param v The validation value to apply in the final join.
     * @return A new validation value after the final join.
     */
    public <A> Validation<A, T> sequence(final Validation<A, T> v) {
      return bind(e1 -> v);
    }


	  /**
     * Returns <code>None</code> if this is a success or if the given predicate <code>p</code> does not hold for the
     * failing value, otherwise, returns a fail in <code>Some</code>.
     *
     * @param f The predicate function to test on this failing value.
     * @return <code>None</code> if this is a success or if the given predicate <code>p</code> does not hold for the
     *         failing value, otherwise, returns a fail in <code>Some</code>.
     */
    public <A> Option<Validation<E, A>> filter(final F<E, Boolean> f) {
      return v.toEither().left().<A>filter(f).map(Validation.validation());
    }

    /**
     * Function application on the failing value.
     *
     * @param v The validation of the function to apply on the failing value.
     * @return The result of function application in validation.
     */
    public <A> Validation<A, T> apply(final Validation<F<E, A>, T> v) {
      return v.f().bind(this::map);
    }

    /**
     * Returns <code>true</code> if this is a success or returns the result of the application of the given
     * function to the failing value.
     *
     * @param f The predicate function to test on this failing value.
     * @return <code>true</code> if this is a success or returns the result of the application of the given
     *         function to the failing value.
     */
    public boolean forall(final F<E, Boolean> f) {
      return v.toEither().left().forall(f);
    }

    /**
     * Returns <code>false</code> if this is a success or returns the result of the application of the given
     * function to the failing value.
     *
     * @param f The predicate function to test on this failing value.
     * @return <code>false</code> if this is a success or returns the result of the application of the given
     *         function to the failing value.
     */
    public boolean exists(final F<E, Boolean> f) {
      return v.toEither().left().exists(f);
    }

    /**
     * Returns a single element list if this is a failing value, otherwise an empty list.
     *
     * @return A single element list if this is a failing value, otherwise an empty list.
     */
    public List<E> toList() {
      return v.toEither().left().toList();
    }

    /**
     * Returns the failing value in <code>Some</code> if there is one, otherwise <code>None</code>.
     *
     * @return The failing value in <code>Some</code> if there is one, otherwise <code>None</code>.
     */
    public Option<E> toOption() {
      return v.toEither().left().toOption();
    }

    /**
     * Returns a single element array if this is a failing value, otherwise an empty list.
     *
     * @return A single element array if this is a failing value, otherwise an empty list.
     */
    public Array<E> toArray() {
      return v.toEither().left().toArray();
    }

    /**
     * Returns a single element stream if this is a failing value, otherwise an empty list.
     *
     * @return A single element stream if this is a failing value, otherwise an empty list.
     */
    public Stream<E> toStream() {
      return v.toEither().left().toStream();
    }

    /**
     * Returns an iterator for this projection. This method exists to permit the use in a <code>for</code>-each loop.
     *
     * @return A iterator for this projection.
     */
    public Iterator<E> iterator() {
      return v.toEither().left().iterator();
    }
  }

  /**
   * Puts this validation's failing value in a non-empty list if there is one.
   *
   * @return A validation with its failing value in a non-empty list if there is one.
   */
  @SuppressWarnings("unchecked")
  public final Validation<NonEmptyList<E>, T> nel() {
    return isSuccess() ?
        Validation.success(success()) :
        Validation.fail(NonEmptyList.nel(fail()));
  }

  /**
   * Construct a validation using the given either value.
   *
   * @param e The either value to construct a validation with.
   * @return A validation using the given either value.
   */
  public static <E, T> Validation<E, T> validation(final Either<E, T> e) {
    return new Validation<>(e);
  }

  /**
   * Returns a function that constructs a validation with an either.
   *
   * @return A function that constructs a validation with an either.
   */
  public static <E, T> F<Either<E, T>, Validation<E, T>> validation() {
    return Validation::validation;
  }

  /**
   * Returns a function that constructs an either with a validation.
   *
   * @return A function that constructs an either with a validation.
   */
  public static <E, T> F<Validation<E, T>, Either<E, T>> either() {
    return Validation::toEither;
  }

  /**
   * Returns a succeeding validation containing the given value.
   *
   * @param t The value to use in the succeeding validation.
   * @return A succeeding validation containing the given value.
   */
  public static <E, T> Validation<E, T> success(final T t) {
    return validation(Either.right(t));
  }

  /**
   * Returns a failing validation containing the given value.
   *
   * @param e The value to use in the failing validation.
   * @return A failing validation containing the given value.
   */
  public static <E, T> Validation<E, T> fail(final E e) {
    return validation(Either.left(e));
  }

  /**
   * Returns a failing validation containing a non-empty list that contains the given value.
   *
   * @param e The value to use in a non-empty list for the failing validation.
   * @return A failing validation containing a non-empty list that contains the given value.
   */
  public static <E, T> Validation<NonEmptyList<E>, T> failNEL(final E e) {
    return fail(NonEmptyList.nel(e));
  }

  /**
   * Returns a validation based on a boolean condition. If the condition is <code>true</code>, the validation succeeds,
   * otherwise it fails.
   *
   * @param c The condition to base the returned validation on.
   * @param e The failing value to use if the condition is <code>false</code>.
   * @param t The succeeding value to use if the condition is <code>true</code>.
   * @return A validation based on a boolean condition.
   */
  public static <E, T> Validation<E, T> condition(final boolean c, final E e, final T t) {
    return c ? Validation.success(t) : Validation.fail(e);
  }

  /**
   * Parses the given string into a byte.
   *
   * @param s The string to parse.
   * @return A successfully parse byte or a failing exception.
   */
  public static Validation<NumberFormatException, Byte> parseByte(final String s) {
    try {
      return success(Byte.parseByte(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into a byte.
   */
  public static final F<String, Validation<NumberFormatException, Byte>> parseByte = Validation::parseByte;

  /**
   * Parses the given string into a double.
   *
   * @param s The string to parse.
   * @return A successfully parse double or a failing exception.
   */
  public static Validation<NumberFormatException, Double> parseDouble(final String s) {
    try {
      return success(Double.parseDouble(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into a double.
   */
  public static final F<String, Validation<NumberFormatException, Double>> parseDouble = Validation::parseDouble;

  /**
   * Parses the given string into a float.
   *
   * @param s The string to parse.
   * @return A successfully parse float or a failing exception.
   */
  public static Validation<NumberFormatException, Float> parseFloat(final String s) {
    try {
      return success(Float.parseFloat(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into a float.
   */
  public static final F<String, Validation<NumberFormatException, Float>> parseFloat = Validation::parseFloat;

  /**
   * Parses the given string into a integer.
   *
   * @param s The string to parse.
   * @return A successfully parse integer or a failing exception.
   */
  public static Validation<NumberFormatException, Integer> parseInt(final String s) {
    try {
      return success(Integer.parseInt(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into an integer.
   */
  public static final F<String, Validation<NumberFormatException, Integer>> parseInt = Validation::parseInt;

  /**
   * Parses the given string into a long.
   *
   * @param s The string to parse.
   * @return A successfully parse long or a failing exception.
   */
  public static Validation<NumberFormatException, Long> parseLong(final String s) {
    try {
      return success(Long.parseLong(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into a long.
   */
  public static final F<String, Validation<NumberFormatException, Long>> parseLong = Validation::parseLong;

  /**
   * Parses the given string into a short.
   *
   * @param s The string to parse.
   * @return A successfully parse short or a failing exception.
   */
  public static Validation<NumberFormatException, Short> parseShort(final String s) {
    try {
      return success(Short.parseShort(s));
    } catch (NumberFormatException e) {
      return fail(e);
    }
  }

  /**
   * A function that parses a string into a short. 
   */
  public static final F<String, Validation<NumberFormatException, Short>> parseShort = Validation::parseShort;

  /**
   * Partitions the list into the list of fails and the list of successes
   */
  public static <A, B> P2<List<A>, List<B>> partition(List<Validation<A, B>> list) {
    return p(
            list.filter(Validation::isFail).map(v -> v.fail()),
            list.filter(Validation::isSuccess).map(v -> v.success())
    );
  }

    @Override
    public final String toString() {
        return Show.validationShow(Show.<E>anyShow(), Show.<T>anyShow()).showS(this);
    }




}
