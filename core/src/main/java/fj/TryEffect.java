package fj;

import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.Validation;
import fj.function.*;

import java.io.IOException;

/**
 * Created by mperry on 29/08/2014.
 */
public class TryEffect {

    private TryEffect(){}

    public static <A, Z extends Exception> P1<Validation<Z, Unit>> f(TryEffect0<Z> t) {
        return P.lazy(u -> {
            try {
                t.f();
                return Validation.success(Unit.unit());
            } catch (Exception e) {
                return Validation.fail((Z) e);
            }
        });
    }

    public static <A, Z extends Exception> F<A, Validation<Z, Unit>> f(TryEffect1<A, Z> t) {
        return a -> {
            try {
                t.f(a);
                return Validation.success(Unit.unit());
            } catch (Exception e) {
                return Validation.fail((Z) e);
            }
        };

    }

    public static <A, B, Z extends Exception> F2<A, B, Validation<Z, Unit>> f(TryEffect2<A, B, Z> t) {
        return (a, b) -> {
            try {
                t.f(a, b);
                return Validation.success(Unit.unit());
            } catch (Exception e) {
                return Validation.fail((Z) e);
            }
        };

    }

    public static <A, B, C, Z extends Exception> F3<A, B, C, Validation<Z, Unit>> f(TryEffect3<A, B, C, Z> t) {
        return (a, b, c) -> {
            try {
                t.f(a, b, c);
                return Validation.success(Unit.unit());
            } catch (Exception e) {
                return Validation.fail((Z) e);
            }
        };
    }

    public static <A, B, C, D, Z extends Exception> F4<A, B, C, D, Validation<Z, Unit>> f(TryEffect4<A, B, C, D, Z> t) {
        return (a, b, c, d) -> {
            try {
                t.f(a, b, c, d);
                return Validation.success(Unit.unit());
            } catch (Exception e) {
                return Validation.fail((Z) e);
            }
        };
    }

    public static <A, B, C, D, E, Z extends Exception> F5<A, B, C, D, E, Validation<Z, Unit>> f(TryEffect5<A, B, C, D, E, Z> t) {
        return (a, b, c, d, e) -> {
            try {
                t.f(a, b, c, d, e);
                return Validation.success(Unit.unit());
            } catch (Exception z) {
                return Validation.fail((Z) z);
            }
        };
    }

    public static <A, B, C, D, E, $F, Z extends Exception> F6<A, B, C, D, E, $F, Validation<Z, Unit>> f(TryEffect6<A, B, C, D, E, $F, Z> t) {
        return (a, b, c, d, e, f) -> {
            try {
                t.f(a, b, c, d, e, f);
                return Validation.success(Unit.unit());
            } catch (Exception z) {
                return Validation.fail((Z) z);
            }
        };
    }

    public static <A, B, C, D, E, $F, G, Z extends Exception> F7<A, B, C, D, E, $F, G, Validation<Z, Unit>> f(TryEffect7<A, B, C, D, E, $F, G, Z> t) {
        return (a, b, c, d, e, f, g) -> {
            try {
                t.f(a, b, c, d, e, f, g);
                return Validation.success(Unit.unit());
            } catch (Exception z) {
                return Validation.fail((Z) z);
            }
        };
    }

    public static <A, B, C, D, E, $F, G, H, Z extends Exception> F8<A, B, C, D, E, $F, G, H, Validation<Z, Unit>> f(TryEffect8<A, B, C, D, E, $F, G, H, Z> t) {
        return (a, b, c, d, e, f, g, h) -> {
            try {
                t.f(a, b, c, d, e, f, g, h);
                return Validation.success(Unit.unit());
            } catch (Exception z) {
                return Validation.fail((Z) z);
            }
        };
    }

}
