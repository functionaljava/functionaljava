package fj;

import fj.data.Option;

/**
 * Transformations on functions.
 *
 * @version %build.number%
 */
public final class Function {
  private Function() {
    throw new UnsupportedOperationException();
  }

  /**
   * Function application with the arguments flipped.
   *
   * @param a The value to apply the function to.
   * @return A function that is partially-applied to the given value.
   */
  public static <A, B> F<F<A, B>, B> apply(final A a) {
    return new F<F<A, B>, B>() {
      public B f(final F<A, B> k) {
        return k.f(a);
      }
    };
  }

  /**
   * Function composition.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<B, C>, F<F<A, B>, F<A, C>>> compose() {
    return new F<F<B, C>, F<F<A, B>, F<A, C>>>() {
      public F<F<A, B>, F<A, C>> f(final F<B, C> f) {
        return new F<F<A, B>, F<A, C>>() {
          public F<A, C> f(final F<A, B> g) {
            return compose(f, g);
          }
        };
      }
    };
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> compose(final F<B, C> f, final F<A, B> g) {
    return a -> f.f(g.f(a));
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C, D> F<A, F<B, D>> compose2(final F<C, D> f, final F<A, F<B, C>> g) {
    return a -> b -> f.f(g.f(a).f(b));
  }


  /**
   * Function composition flipped.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<A, B>, F<F<B, C>, F<A, C>>> andThen() {
    return g -> f -> Function.andThen(g, f);
  }

  /**
   * Function composition flipped.
   *
   * @param g A function to compose with another.
   * @param f A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> andThen(final F<A, B> g, final F<B, C> f) {
    return a -> f.f(g.f(a));
  }

  /**
   * The identity transformation.
   *
   * @return The identity transformation.
   */
  public static <A> F<A, A> identity() {
    return a -> a;
  }

  /**
   * Returns a function that given an argument, returns a function that ignores its argument.
   *
   * @return A function that given an argument, returns a function that ignores its argument.
   */
  public static <A, B> F<B, F<A, B>> constant() {
    return b -> constant(b);
  }

  /**
   * Returns a function that ignores its argument to constantly produce the given value.
   *
   * @param b The value to return when the returned function is applied.
   * @return A function that ignores its argument to constantly produce the given value.
   */
  public static <A, B> F<A, B> constant(final B b) {
    return a -> b;
  }

  /**
   * Simultaneously covaries and contravaries a function.
   *
   * @param f The function to vary.
   * @return A co- and contravariant function that invokes f on its argument.
   */
  public static <A, B> F<A, B> vary(final F<? super A, ? extends B> f) {
    return a -> f.f(a);
  }

  /**
   * Simultaneously covaries and contravaries a function.
   *
   * @return A function that varies and covaries a function.
   */
  public static <C, A extends C, B, D extends B> F<F<C, D>, F<A, B>> vary() {
    return f -> Function.<A, B>vary(f);
  }

  /**
   * Function argument flipping.
   *
   * @return A function that takes a function and flips its arguments.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F<B, F<A, C>>> flip() {
    return f -> flip(f);
  }

  /**
   * Function argument flipping.
   *
   * @param f The function to flip.
   * @return The given function flipped.
   */
  public static <A, B, C> F<B, F<A, C>> flip(final F<A, F<B, C>> f) {
    return b -> a -> f.f(a).f(b);
  }

  /**
   * Function argument flipping.
   *
   * @param f The function to flip.
   * @return The given function flipped.
   */
  public static <A, B, C> F2<B, A, C> flip(final F2<A, B, C> f) {
    return (b, a) -> f.f(a, b);
  }

  /**
   * Function argument flipping.
   *
   * @return A function that flips the arguments of a given function.
   */
  public static <A, B, C> F<F2<A, B, C>, F2<B, A, C>> flip2() {
    return f -> flip(f);
  }

  /**
   * Return a function that inspects the argument of the given function for a <code>null</code> value and if so, does
   * not apply the value, instead returning an empty optional value.
   *
   * @param f The function to check for a <code>null</code> argument.
   * @return A function that inspects the argument of the given function for a <code>null</code> value and if so, does
   * not apply the value, instead returning an empty optional value.
   */
  public static <A, B> F<A, Option<B>> nullable(final F<A, B> f) {
    return a -> a == null ? Option.<B>none() : Option.some(f.f(a));
  }

  /**
   * Curry a function of arity-2.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C> F<A, F<B, C>> curry(final F2<A, B, C> f) {
    return a -> b -> f.f(a, b);
  }

  /**
   * Curry a function of arity-2.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C> F<B, C> curry(final F2<A, B, C> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @return An uncurried function.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F2<A, B, C>> uncurryF2() {
    return f -> uncurryF2(f);
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C> F2<A, B, C> uncurryF2(final F<A, F<B, C>> f) {
    return (a, b) -> f.f(a).f(b);
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<A, F<B, F<C, D>>> curry(final F3<A, B, C, D> f) {
    return a -> b -> c -> f.f(a, b, c);
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<B, F<C, D>> curry(final F3<A, B, C, D> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-3.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D> F<C, D> curry(final F3<A, B, C, D> f, final A a, final B b) {
    return curry(f, a).f(b);
  }

  /**
   * Uncurry a function of arity-3.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D> F<F<A, F<B, F<C, D>>>, F3<A, B, C, D>> uncurryF3() {
    return f -> uncurryF3(f);
  }

  /**
   * Uncurry a function of arity-3.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D> F3<A, B, C, D> uncurryF3(final F<A, F<B, F<C, D>>> f) {
    return (a, b, c) -> f.f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<A, F<B, F<C, F<D, E>>>> curry(final F4<A, B, C, D, E> f) {
    return a -> b -> c -> d -> f.f(a, b, c, d);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<B, F<C, F<D, E>>> curry(final F4<A, B, C, D, E> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<C, F<D, E>> curry(final F4<A, B, C, D, E> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-4.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E> F<D, E> curry(final F4<A, B, C, D, E> f, final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F<F<A, F<B, F<C, F<D, E>>>>, F4<A, B, C, D, E>> uncurryF4() {
    return f -> uncurryF4(f);
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F4<A, B, C, D, E> uncurryF4(final F<A, F<B, F<C, F<D, E>>>> f) {
    return (a, b, c, d) -> f.f(a).f(b).f(c).f(d);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F<E, F$>>>>> curry(final F5<A, B, C, D, E, F$> f) {
    return a -> b -> c -> d -> e -> f.f(a, b, c, d, e);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<B, F<C, F<D, F<E, F$>>>> curry(final F5<A, B, C, D, E, F$> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<C, F<D, F<E, F$>>> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<D, F<E, F$>> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b,
                                                         final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-5.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$> F<E, F$> curry(final F5<A, B, C, D, E, F$> f, final A a, final B b, final C c,
                                                   final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Uncurry a function of arity-5.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F<F<A, F<B, F<C, F<D, F<E, F$>>>>>, F5<A, B, C, D, E, F$>> uncurryF5() {
    return f -> uncurryF5(f);
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F5<A, B, C, D, E, F$> uncurryF5(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
    return (a, b, c, d, e) -> f.f(a).f(b).f(c).f(d).f(e);
  }

  /**
   * Curry a function of arity-6.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> curry(final F6<A, B, C, D, E, F$, G> f) {
    return a -> b -> c -> d -> e -> f$ -> f.f(a, b, c, d, e, f$);
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F<F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>, F6<A, B, C, D, E, F$, G>> uncurryF6() {
    return f -> uncurryF6(f);
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F6<A, B, C, D, E, F$, G> uncurryF6(
      final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
    return (a, b, c, d, e, f$) -> f.f(a).f(b).f(c).f(d).f(e).f(f$);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> curry(
      final F7<A, B, C, D, E, F$, G, H> f) {
    return a -> b -> c -> d -> e -> f$ -> g -> f.f(a, b, c, d, e, f$, g);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>> curry(
      final F7<A, B, C, D, E, F$, G, H> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<C, F<D, F<E, F<F$, F<G, H>>>>> curry(final F7<A, B, C, D, E, F$, G, H> f,
                                                                                 final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<D, F<E, F<F$, F<G, H>>>> curry(final F7<A, B, C, D, E, F$, G, H> f,
                                                                           final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<E, F<F$, F<G, H>>> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a,
                                                                     final B b, final C c, final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @param e An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<F$, F<G, H>> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a,
                                                               final B b, final C c, final D d, final E e) {
    return curry(f).f(a).f(b).f(c).f(d).f(e);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H> F<G, H> curry(final F7<A, B, C, D, E, F$, G, H> f, final A a, final B b,
                                                        final C c, final D d, final E e, final F$ f$) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$);
  }

  /**
   * Uncurry a function of arity-7.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>> uncurryF7() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>>() {
      public F7<A, B, C, D, E, F$, G, H> f(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return uncurryF7(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-7.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F7<A, B, C, D, E, F$, G, H> uncurryF7(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
    return (a, b, c, d, e, f$, g) -> f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f) {
    return a -> b -> c -> d -> e -> f$ -> g -> h -> f.f(a, b, c, d, e, f$, g, h);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a) {
    return curry(f).f(a);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b) {
    return curry(f).f(a).f(b);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<D, F<E, F<F$, F<G, F<H, I>>>>> curry(
      final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b, final C c) {
    return curry(f).f(a).f(b).f(c);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<E, F<F$, F<G, F<H, I>>>> curry(final F8<A, B, C, D, E, F$, G, H, I> f,
                                                                              final A a, final B b, final C c,
                                                                              final D d) {
    return curry(f).f(a).f(b).f(c).f(d);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f The function to curry.
   * @param a An argument to the curried function.
   * @param b An argument to the curried function.
   * @param c An argument to the curried function.
   * @param d An argument to the curried function.
   * @param e An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<F$, F<G, F<H, I>>> curry(final F8<A, B, C, D, E, F$, G, H, I> f,
                                                                        final A a, final B b, final C c, final D d,
                                                                        final E e) {
    return curry(f).f(a).f(b).f(c).f(d).f(e);
  }

  /**
   * Curry a function of arity-8.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<G, F<H, I>> curry(final F8<A, B, C, D, E, F$, G, H, I> f, final A a,
                                                                 final B b, final C c, final D d, final E e,
                                                                 final F$ f$) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$);
  }

  /**
   * Curry a function of arity-7.
   *
   * @param f  The function to curry.
   * @param a  An argument to the curried function.
   * @param b  An argument to the curried function.
   * @param c  An argument to the curried function.
   * @param d  An argument to the curried function.
   * @param e  An argument to the curried function.
   * @param f$ An argument to the curried function.
   * @param g  An argument to the curried function.
   * @return A curried form of the given function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<H, I> curry(final F8<A, B, C, D, E, F$, G, H, I> f, final A a, final B b,
                                                           final C c, final D d, final E e, final F$ f$, final G g) {
    return curry(f).f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
  }

  /**
   * Uncurry a function of arity-8.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>, F8<A, B, C, D, E, F$, G, H, I>> uncurryF8() {
    return f -> uncurryF8(f);
  }

  /**
   * Uncurry a function of arity-8.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F8<A, B, C, D, E, F$, G, H, I> uncurryF8(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
    return (a, b, c, d, e, f$, g, h) -> f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g).f(h);
  }

  /**
   * Binds the function in the second argument to the function in the first argument.
   *
   * @param ma A function whose argument type is the same as the argument type of the return value.
   * @param f  A function whose argument type is the same as the return type of <em>ma</em>,
   *           and yields the return value.
   * @return A function that chains the given functions together such that the result of applying
   *         <em>ma</em> to the argument is given to <i>f</i>, yielding a function
   *         that is applied to the argument again.
   */
  public static <A, B, C> F<C, B> bind(final F<C, A> ma, final F<A, F<C, B>> f) {
    return m -> f.f(ma.f(m)).f(m);
  }

  /**
   * Performs function application within a higher-order function (applicative functor pattern).
   *
   * @param cab The higher-order function to apply a function to.
   * @param ca  A function to apply within a higher-order function.
   * @return A new function after applying the given higher-order function to the given function.
   */
  public static <A, B, C> F<C, B> apply(final F<C, F<A, B>> cab, final F<C, A> ca) {
    return bind(cab, f -> compose(a -> f.f(a), ca));
  }

  /**
   * Binds the given function <em>f</em> to the values of the given functions, with a final join.
   *
   * @param ca A function to bind <em>f</em> function to.
   * @param cb A function to bind <em>f</em> function to.
   * @param f  The bound function to be composed with <em>ca</em> and then applied with <em>cb</em>
   * @return A new function after performing the composition, then application.
   */
  public static <A, B, C, D> F<D, C> bind(final F<D, A> ca, final F<D, B> cb, final F<A, F<B, C>> f) {
    return apply(compose(f, ca), cb);
  }

  /**
   * Applies a given function over the arguments of another function of arity-2.
   *
   * @param a The function whose arguments to apply another function over.
   * @param f The function to apply over the arguments of another function.
   * @return A function whose arguments are fed through function f, before being passed to function a.
   */
  public static <A, B, C> F<B, F<B, C>> on(final F<A, F<A, C>> a, final F<B, A> f) {
    return compose(compose(Function.<B, A, C>andThen().f(f), a), f);
  }

  /**
   * Promotes a function of arity-2 to a higher-order function.
   *
   * @param f The function to promote.
   * @return A function of arity-2 promoted to compose with two functions.
   */
  public static <A, B, C, D> F<F<D, A>, F<F<D, B>, F<D, C>>> lift(final F<A, F<B, C>> f) {
    return curry((ca, cb) -> bind(ca, cb, f));
  }

  /**
   * Joins two arguments of a function of arity-2 into one argument, yielding a function of arity-1.
   *
   * @param f A function whose arguments to join.
   * @return A function of arity-1 whose argument is substituted for both parameters of <em>f</em>.
   */
  public static <A, B> F<B, A> join(final F<B, F<B, A>> f) {
    return bind(f, Function.<F<B, A>>identity());
  }


  /**
   * Partial application of the second argument to the supplied function to get a function of type
   * <tt>A -> C</tt>. Same as <tt>flip(f).f(b)</tt>.
   *
   * @param f The function to partially apply.
   * @param b The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its second argument applied.
   */
  public static <A, B, C> F<A, C> partialApply2(final F<A, F<B, C>> f, final B b) {
    return a -> uncurryF2(f).f(a, b);
  }

  /**
   * Partial application of the third argument to the supplied function to get a function of type
   * <tt>A -> B -> D</tt>.
   *
   * @param f The function to partially apply.
   * @param c The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its third argument applied.
   */
  public static <A, B, C, D> F<A, F<B, D>> partialApply3(final F<A, F<B, F<C, D>>> f, final C c) {
    return a -> b -> uncurryF3(f).f(a, b, c);
  }

  /**
   * Partial application of the fourth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> E</tt>.
   *
   * @param f The function to partially apply.
   * @param d The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its fourth argument applied.
   */
  public static <A, B, C, D, E> F<A, F<B, F<C, E>>> partialApply4(final F<A, F<B, F<C, F<D, E>>>> f, final D d) {
    return a -> b -> c -> uncurryF4(f).f(a, b, c, d);
  }

  /**
   * Partial application of the fifth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> F$</tt>.
   *
   * @param f The function to partially apply.
   * @param e The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its fifth argument applied.
   */
  public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F$>>>> partialApply5(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f,
                                                                             final E e) {
    return a -> b -> c -> d -> uncurryF5(f).f(a, b, c, d, e);
  }

  /**
   * Partial application of the sixth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> G</tt>.
   *
   * @param f  The function to partially apply.
   * @param f$ The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its sixth argument applied.
   */
  public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, G>>>>> partialApply6(
      final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f, final F$ f$) {
    return a -> b -> c -> d -> e -> uncurryF6(f).f(a, b, c, d, e, f$);
  }

  /**
   * Partial application of the seventh argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> F$ -> H</tt>.
   *
   * @param f The function to partially apply.
   * @param g The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its seventh argument applied.
   */
  public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, H>>>>>> partialApply7(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f, final G g) {
    return a -> b -> c -> d -> e -> f$ -> uncurryF7(f).f(a, b, c, d, e, f$, g);
  }

  /**
   * Partial application of the eigth argument to the supplied function to get a function of type
   * <tt>A -> B -> C -> D -> E -> F$ -> G -> I</tt>.
   *
   * @param f The function to partially apply.
   * @param h The value to apply to the function.
   * @return A new function based on <tt>f</tt> with its eigth argument applied.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, I>>>>>>> partialApply8(
      final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f, final H h) {
    return a -> b -> c -> d -> e -> f$ -> g -> uncurryF8(f).f(a, b, c, d, e, f$, g, h);
  }
}
