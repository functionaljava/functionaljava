package fj.control;

import fj.*;
import fj.data.Either;

import static fj.Function.curry;
import static fj.data.Either.left;
import static fj.data.Either.right;

/**
 * A Trampoline is a potentially branching computation that can be stepped through and executed in constant stack.
 * It represent suspendable coroutines with subroutine calls, reified as a data structure.
 */
public abstract class Trampoline<A> {

  // A Normal Trampoline is either done or suspended, and is allowed to be a subcomputation of a Codense.
  // This is the pointed functor part of the Trampoline monad.
  private static abstract class Normal<A> extends Trampoline<A> {
    public abstract <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k);

    public <B> Trampoline<B> bind(final F<A, Trampoline<B>> f) {
      return codense(this, f);
    }
  }

  // A Codense Trampoline delimits a subcomputation and tracks its current continuation. Subcomputations are only
  // allowed to be Normal, so all of the continuations accumulate on the right.
  private static final class Codense<A> extends Trampoline<A> {

    // The Normal subcomputation
    private final Normal<Object> sub;

    // The current continuation
    private final F<Object, Trampoline<A>> cont;

    private Codense(final Normal<Object> t, final F<Object, Trampoline<A>> k) {
      sub = t;
      cont = k;
    }

    public <R> R fold(final F<Normal<A>, R> n,
                      final F<Codense<A>, R> gs) {
      return gs.f(this);
    }

    // The monadic bind constructs a new Codense whose subcomputation is still `sub`, and Kleisli-composes the
    // continuations.
    public <B> Trampoline<B> bind(final F<A, Trampoline<B>> f) {
      return codense(sub, o -> suspend(P.lazy(u -> cont.f(o).bind(f))));
    }

    // The resumption of a Codense is the resumption of its subcomputation. If that computation is done, its result
    // gets shifted into the continuation.
    public Either<P1<Trampoline<A>>, A> resume() {
      return left(sub.resume().either(p -> {
        return p.map(ot -> {
          // WARNING: In JDK 8, update 25 (current version) the following code is a
          // workaround for an internal JDK compiler error, likely due to
          // https:bugs.openjdk.java.net/browse/JDK-8062253.
          F<Normal<Object>, Trampoline<A>> f = o -> o.foldNormal(o1 -> cont.f(o1), t -> t._1().bind(cont));
          F<Codense<Object>, Trampoline<A>> g = c -> codense(c.sub, o -> c.cont.f(o).bind(cont));
          return ot.fold(f, g);
        });
      }, o -> P.lazy(u -> cont.f(o))));
    }
  }

  // A suspended computation that can be resumed.
  private static final class Suspend<A> extends Normal<A> {

    private final P1<Trampoline<A>> suspension;

    private Suspend(final P1<Trampoline<A>> s) {
      suspension = s;
    }

    public <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k) {
      return k.f(suspension);
    }

    public <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs) {
      return n.f(this);
    }

    public Either<P1<Trampoline<A>>, A> resume() {
      return left(suspension);
    }
  }

  // A pure value at the leaf of a computation.
  private static final class Pure<A> extends Normal<A> {
    private final A value;

    private Pure(final A a) {
      value = a;
    }

    public <R> R foldNormal(final F<A, R> pure, final F<P1<Trampoline<A>>, R> k) {
      return pure.f(value);
    }

    public <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs) {
      return n.f(this);
    }

    public Either<P1<Trampoline<A>>, A> resume() {
      return right(value);
    }
  }

  @SuppressWarnings("unchecked")
  protected static <A, B> Codense<B> codense(final Normal<A> a, final F<A, Trampoline<B>> k) {
    return new Codense<B>((Normal<Object>) a, (F<Object, Trampoline<B>>) k);
  }

  /**
   * @return The first-class version of `pure`.
   */
  public static <A> F<A, Trampoline<A>> pure() {
    return a -> pure(a);
  }

  /**
   * Constructs a pure computation that results in the given value.
   *
   * @param a The value of the result.
   * @return A trampoline that results in the given value.
   */
  public static <A> Trampoline<A> pure(final A a) {
    return new Pure<A>(a);
  }

  /**
   * Suspends the given computation in a thunk.
   *
   * @param a A trampoline suspended in a thunk.
   * @return A trampoline whose next step runs the given thunk.
   */
  public static <A> Trampoline<A> suspend(final P1<Trampoline<A>> a) {
    return new Suspend<A>(a);
  }

  /**
   * @return The first-class version of `suspend`.
   */
  public static <A> F<P1<Trampoline<A>>, Trampoline<A>> suspend_() {
    return trampolineP1 -> suspend(trampolineP1);
  }

  protected abstract <R> R fold(final F<Normal<A>, R> n, final F<Codense<A>, R> gs);

  /**
   * Binds the given continuation to the result of this trampoline.
   *
   * @param f A function that constructs a trampoline from the result of this trampoline.
   * @return A new trampoline that runs this trampoline, then continues with the given function.
   */
  public abstract <B> Trampoline<B> bind(final F<A, Trampoline<B>> f);

  /**
   * Maps the given function across the result of this trampoline.
   *
   * @param f A function that gets applied to the result of this trampoline.
   * @return A new trampoline that runs this trampoline, then applies the given function to the result.
   */
  public final <B> Trampoline<B> map(final F<A, B> f) {
    return bind(F1Functions.o(Trampoline.<B>pure(), f));
  }

  /**
   * @return The first-class version of `bind`.
   */
  public static <A, B> F<F<A, Trampoline<B>>, F<Trampoline<A>, Trampoline<B>>> bind_() {
    return f -> a -> a.bind(f);
  }

  /**
   * @return The first-class version of `map`.
   */
  public static <A, B> F<F<A, B>, F<Trampoline<A>, Trampoline<B>>> map_() {
    return f -> a -> a.map(f);
  }

  /**
   * @return The first-class version of `resume`.
   */
  public static <A> F<Trampoline<A>, Either<P1<Trampoline<A>>, A>> resume_() {
    return aTrampoline -> aTrampoline.resume();
  }

  /**
   * Runs a single step of this computation.
   *
   * @return The next step of this compuation.
   */
  public abstract Either<P1<Trampoline<A>>, A> resume();

  /**
   * Runs this computation all the way to the end, in constant stack.
   *
   * @return The end result of this computation.
   */
  @SuppressWarnings("LoopStatementThatDoesntLoop")
  public A run() {
    Trampoline<A> current = this;
    while (true) {
      final Either<P1<Trampoline<A>>, A> x = current.resume();
      for (final P1<Trampoline<A>> t : x.left()) {
        current = t._1();
      }
      for (final A a : x.right()) {
        return a;
      }
    }
  }

  /**
   * Performs function application within a Trampoline (applicative functor pattern).
   *
   * @param lf A Trampoline resulting in the function to apply.
   * @return A new Trampoline after applying the given function through this Trampoline.
   */
  public final <B> Trampoline<B> apply(final Trampoline<F<A, B>> lf) {
    return lf.bind(f -> map(f));
  }

  /**
   * Binds the given function across the result of this Trampoline and the given Trampoline.
   *
   * @param lb A given Trampoline to bind the given function with.
   * @param f  The function to combine the results of this Trampoline and the given Trampoline.
   * @return A new Trampoline combining the results of the two trampolines with the given function.
   */
  public final <B, C> Trampoline<C> bind(final Trampoline<B> lb, final F<A, F<B, C>> f) {
    return lb.apply(map(f));
  }


  /**
   * Promotes the given function of arity-2 to a function on Trampolines.
   *
   * @param f The function to promote to a function on Trampolines.
   * @return The given function, promoted to operate on Trampolines.
   */
  public static <A, B, C> F<Trampoline<A>, F<Trampoline<B>, Trampoline<C>>> liftM2(final F<A, F<B, C>> f) {
    return curry((as, bs) -> as.bind(bs, f));
  }

  /**
   * Combines two trampolines so they run cooperatively. The results are combined with the given function.
   *
   * @param b Another trampoline to combine with this trampoline.
   * @param f A function to combine the results of the two trampolines.
   * @return A new trampoline that runs this trampoline and the given trampoline simultaneously.
   */
  @SuppressWarnings("LoopStatementThatDoesntLoop")
  public <B, C> Trampoline<C> zipWith(final Trampoline<B> b, final F2<A, B, C> f) {
    final Either<P1<Trampoline<A>>, A> ea = resume();
    final Either<P1<Trampoline<B>>, B> eb = b.resume();
    for (final P1<Trampoline<A>> x : ea.left()) {
      for (final P1<Trampoline<B>> y : eb.left()) {
        return suspend(x.bind(y, F2Functions.curry((ta, tb) -> suspend(P.<Trampoline<C>>lazy(u -> ta.zipWith(tb, f))))));
      }
      for (final B y : eb.right()) {
        return suspend(x.map(ta -> ta.map(F2Functions.f(F2Functions.flip(f), y))));
      }
    }
    for (final A x : ea.right()) {
      for (final B y : eb.right()) {
        return suspend(P.lazy(u -> pure(f.f(x, y))));
      }
      for (final P1<Trampoline<B>> y : eb.left()) {
        return suspend(y.map(liftM2(F2Functions.curry(f)).f(pure(x))));
      }
    }
    throw Bottom.error("Match error: Trampoline is neither done nor suspended.");
  }
}
