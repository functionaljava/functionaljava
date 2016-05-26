package fj.control.parallel;

import fj.*;

import static fj.Function.curry;

import fj.data.Either;
import static fj.data.Either.left;
import static fj.data.Either.right;
import fj.data.List;
import fj.data.Option;
import static fj.data.Option.none;
import static fj.data.Option.some;

import java.util.concurrent.Callable;

/**
 * Monadic functions and conversion methods for java.util.concurrent.Callable.
 *
 * @version %build.number%
 */
public final class Callables {
  private Callables() {
  }

  /**
   * Returns a callable that completely preserves the argument. The unit function for Callables.
   *
   * @param a A value to preserve in a Callable
   * @return A Callable that yields the argument when called.
   */
  public static <A> Callable<A> callable(final A a) {
    return () -> a;
  }

  /**
   * Returns a callable that throws the given exception. The unit function for Callables.
   *
   * @param e The exception to throw when the Callable is called.
   * @return A callable that always throws the given exception.
   */
  public static <A> Callable<A> callable(final Exception e) {
    return () -> {
      throw e;
    };
  }

  /**
   * Provides a transformation from a value to a Callable that completely preserves that value.
   *
   * @return A function from a value to a Callable that completely preserves that value.
   */
  public static <A> F<A, Callable<A>> callable() {
    return Callables::callable;
  }

  /**
   * Wraps a given function's return value in a Callable.
   * The Kleisli arrow for Callables.
   *
   * @param f The function whose return value to wrap in a Callable.
   * @return The equivalent function whose return value is wrapped in a Callable.
   */
  public static <A, B> F<A, Callable<B>> callable(final F<A, B> f) {
    return a -> () -> f.f(a);
  }

  /**
   * Provides a transformation from a function to a Callable-valued function that is equivalent to it.
   * The first-class Kleisli arrow for Callables.
   *
   * @return A transformation from a function to the equivalent Callable-valued function.
   */
  public static <A, B> F<F<A, B>, F<A, Callable<B>>> arrow() {
    return Callables::callable;
  }

  /**
   * Binds the given function to the value in a Callable with a final join.
   *
   * @param a A value in a Callable to which to apply a function.
   * @param f A function to apply to the value in a Callable.
   * @return The result of applying the function in the second argument to the value of the Callable in the first.
   */
  public static <A, B> Callable<B> bind(final Callable<A> a, final F<A, Callable<B>> f) {
    return () -> f.f(a.call()).call();
  }

  /**
   * Lifts any function to a function on Callables.
   *
   * @param f A function to lift to a function on Callables.
   * @return That function lifted to a function on Callables.
   */
  public static <A, B> F<Callable<A>, Callable<B>> fmap(final F<A, B> f) {
    return a -> bind(a, callable(f));
  }

  /**
   * Performs function application within a callable (applicative functor pattern).
   *
   * @param ca The callable to which to apply a function.
   * @param cf The callable function to apply.
   * @return A new callable after applying the given callable function to the first argument.
   */
  public static <A, B> Callable<B> apply(final Callable<A> ca, final Callable<F<A, B>> cf) {
    return bind(cf, f -> fmap(f).f(ca));
  }

  /**
   * Binds the given function to the values in the given callables with a final join.
   *
   * @param ca A given callable to bind the given function with.
   * @param cb A given callable to bind the given function with.
   * @param f  The function to apply to the values in the given callables.
   * @return A new callable after performing the map, then final join.
   */
  public static <A, B, C> Callable<C> bind(final Callable<A> ca, final Callable<B> cb, final F<A, F<B, C>> f) {
    return apply(cb, fmap(f).f(ca));
  }

  /**
   * Joins a Callable of a Callable with a bind operation.
   *
   * @param a The Callable of a Callable to join.
   * @return A new Callable that is the join of the given Callable.
   */
  public static <A> Callable<A> join(final Callable<Callable<A>> a) {
    return bind(a, Function.identity());
  }

  /**
   * Promotes a function of arity-2 to a function on callables.
   *
   * @param f The function to promote.
   * @return A function of arity-2 promoted to map over callables.
   */
  public static <A, B, C> F<Callable<A>, F<Callable<B>, Callable<C>>> liftM2(final F<A, F<B, C>> f) {
    return curry((ca, cb) -> bind(ca, cb, f));
  }

  /**
   * Turns a List of Callables into a single Callable of a List.
   *
   * @param as The list of callables to transform.
   * @return A single callable for the given List.
   */
  public static <A> Callable<List<A>> sequence(final List<Callable<A>> as) {
    return as.foldRight(Callables.liftM2(List.cons()), callable(List.nil()));
  }

  /**
   * A first-class version of the sequence method.
   *
   * @return A function from a List of Callables to a single Callable of a List.
   */
  public static <A> F<List<Callable<A>>, Callable<List<A>>> sequence_() {
    return Callables::sequence;
  }

  /**
   * Turns the given Callable into an optional value.
   *
   * @param a The callable to convert to an optional value.
   * @return An optional value that yields the value in the Callable, or None if the Callable fails.
   */
  public static <A> P1<Option<A>> option(final Callable<A> a) {
    return P.lazy(() -> {
        try {
          return some(a.call());
        } catch (Exception e) {
          return none();
        }
    });
  }

  /**
   * Returns a transformation from a Callable to an optional value.
   *
   * @return a function that turns a Callable into an optional value.
   */
  public static <A> F<Callable<A>, P1<Option<A>>> option() {
    return Callables::option;
  }

  /**
   * Turns the given Callable into either an exception or the value in the Callable.
   *
   * @param a The callable to convert to an Either value.
   * @return Either the value in the given Callable, or the Exception with which the Callable fails.
   */
  public static <A> P1<Either<Exception, A>> either(final Callable<A> a) {
    return P.lazy(() -> {
        try {
            return right(a.call());
        } catch (Exception e) {
            return left(e);
        }
    });
  }

  /**
   * Returns a transformation from a Callable to an Either.
   *
   * @return a function that turns a Callable into an Either.
   */
  public static <A> F<Callable<A>, P1<Either<Exception, A>>> either() {
    return Callables::either;
  }

  /**
   * Turns a given Either value into the equivalent Callable.
   *
   * @param e Either an exception or a value to wrap in a Callable
   * @return A Callable equivalent to the given Either value.
   */
  public static <A> Callable<A> fromEither(final F0<Either<Exception, A>> e) {
    return () -> {
      final Either<Exception, A> e1 = e.f();
      if (e1.isLeft())
        throw e1.left().value();
      else
        return e1.right().value();
    };
  }

  /**
   * Returns a transformation from an Either to a Callable.
   *
   * @return a function that turns an Either into a Callable.
   */
  public static <A> F<P1<Either<Exception, A>>, Callable<A>> fromEither() {
    return Callables::fromEither;
  }

  /**
   * Turns an optional value into a Callable.
   *
   * @param o An optional value to turn into a Callable.
   * @return A Callable that yields some value or throws an exception in the case of no value.
   */
  public static <A> Callable<A> fromOption(final F0<Option<A>> o) {
    return () -> {
      final Option<A> o1 = o.f();
      if (o1.isSome())
        return o1.some();
      else
        throw new Exception("No value.");
    };
  }

  /**
   * Returns a transformation from an optional value to a Callable
   *
   * @return A function that turns an optional value into a Callable that yields some value
   *         or throws an exception in the case of no value.
   */
  public static <A> F<P1<Option<A>>, Callable<A>> fromOption() {
    return Callables::fromOption;
  }

  /**
   * Normalises the given Callable by calling it and wrapping the result in a new Callable.
   * If the given Callable throws an Exception, the resulting Callable will throw that same Exception.
   *
   * @param a The callable to evaluate.
   * @return A normalised callable that just returns the result of calling the given callable.
   */
  public static <A> Callable<A> normalise(final Callable<A> a) {
    try {
      return callable(a.call());
    } catch (Exception e) {
      return callable(e);
    }
  }

  /**
   * A first-class version of the normalise function.
   *
   * @return A function that normalises the given Callable by calling it and wrapping the result in a new Callable.
   */
  public static <A> F<Callable<A>, Callable<A>> normalise() {
    return Callables::normalise;
  }

}
