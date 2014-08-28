package fj;

import fj.function.Effect1;

import static fj.Unit.unit;

/**
 * Represents a side-effect.
 *
 * @version %build.number%
 */
public class Effect {

	private Effect() {}

  /**
   * Returns a function for the given effect.
   *
   * @return The function using the given effect.
   */
  public static final <A> F<A, Unit> f(Effect1<A> e1) {
    return new F<A, Unit>() {
      public Unit f(final A a) {
        e1.f(a);
        return unit();
      }
    };
  }

  /**
   * A contra-variant functor on effect.
   *
   * @param f The function to map over the effect.
   * @return An effect after a contra-variant map.
   */
  public final <A, B> Effect1<B> comap(Effect1<A> e1, final F<B, A> f) {
    return new Effect1<B>() {
      public void f(final B b) {
        e1.f(f.f(b));
      }
    };
  }
  
  public static <A> Effect1<A> f(final F<A, Unit> f) {
    return new Effect1<A>() {
      public void f(final A a) {
        f.f(a);
      }
    };
  }

//	public static <A> void f(Effect1<A> )

}
