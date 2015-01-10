package fj;

import fj.function.Effect0;
import fj.function.Effect1;
import fj.function.Effect2;
import fj.function.Effect3;
import fj.function.Effect4;
import fj.function.Effect5;
import fj.function.Effect6;
import fj.function.Effect7;
import fj.function.Effect8;

import static fj.Unit.unit;

/**
 * Represents a side-effect.
 *
 * @version %build.number%
 */
public class Effect {

	private Effect() {}

    public static P1<Unit> f(Effect0 e) {
        return P.lazy(u -> unit());
    }

    /**
   * Returns a function for the given effect.
   *
   * @return The function using the given effect.
   */
  public static final <A> F<A, Unit> f(Effect1<A> e1) {
    return a -> {
        e1.f(a);
        return unit();
    };
  }

    public static <A, B> F2<A, B, Unit> f(Effect2<A, B> e) {
        return (a, b) -> {
            e.f(a, b);
            return unit();
        };
    }

    public static <A, B, C> F3<A, B, C, Unit> f(Effect3<A, B, C> e) {
        return (a, b, c) -> {
            e.f(a, b, c);
            return unit();
        };
    }

    public static <A, B, C, D> F4<A, B, C, D, Unit> f(Effect4<A, B, C, D> e) {
        return (a, b, c, d) -> {
            e.f(a, b, c, d);
            return unit();
        };
    }

    public static <A, B, C, D, E> F5<A, B, C, D, E, Unit> f(Effect5<A, B, C, D, E> z) {
        return (a, b, c, d, e) -> {
            z.f(a, b, c, d, e);
            return unit();
        };
    }

    public static <A, B, C, D, E, $F> F6<A, B, C, D, E, $F, Unit> f(Effect6<A, B, C, D, E, $F> z) {
        return (a, b, c, d, e, f) -> {
            z.f(a, b, c, d, e, f);
            return unit();
        };
    }

    public static <A, B, C, D, E, $F, G> F7<A, B, C, D, E, $F, G, Unit> f(Effect7<A, B, C, D, E, $F, G> z) {
        return (a, b, c, d, e, f, g) -> {
            z.f(a, b, c, d, e, f, g);
            return unit();
        };
    }

    public static <A, B, C, D, E, $F, G, H> F8<A, B, C, D, E, $F, G, H, Unit> f(Effect8<A, B, C, D, E, $F, G, H> z) {
        return (a, b, c, d, e, f, g, h) -> {
            z.f(a, b, c, d, e, f, g, h);
            return unit();
        };
    }

    /**
   * A contra-variant functor on effect.
   *
   * @param f The function to map over the effect.
   * @return An effect after a contra-variant map.
   */
  public final <A, B> Effect1<B> comap(Effect1<A> e1, final F<B, A> f) {
    return b -> e1.f(f.f(b));
  }
  
  public static <A> Effect1<A> lazy(final F<A, Unit> f) {
    return a -> f.f(a);

  }

//	public static <A> void f(Effect1<A> )

}
