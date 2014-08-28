package fj;

import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.Validation;

import java.io.IOException;

/**
 * Created by mperry on 24/07/2014.
 */
public class Try {

    public static <A, Z extends Exception> P1<Validation<Z, A>> f(Try0<A, Z> t) {
        return P1.toP1(t);
    }

    public static <A, B, Z extends Exception> F<A, Validation<Z, B>> f(Try1<A, B, Z> t) {
        return F1Functions.toF1(t);
    }

    public static <A, B, C, Z extends Exception> F2<A, B, Validation<Z, C>> f(Try2<A, B, C, Z> t) {
        return F2Functions.toF2(t);
    }

    public static <A, B, C, D, Z extends Exception> F3<A, B, C, Validation<Z, D>> f(Try3<A, B, C, D, Z> t) {
        return F3Functions.toF3(t);
    }

    public static <A, B, C, D, E, Z extends Exception> F4<A, B, C, D, Validation<Z, E>> f(Try4<A, B, C, D, E, Z> t) {
        return F4Functions.toF4(t);
    }

    public static <A, B, C, D, E, F$, Z extends Exception> F5<A, B, C, D, E, Validation<Z, F$>> f(Try5<A, B, C, D, E, F$, Z> t) {
        return F5Functions.toF5(t);
    }

    public static <A, B, C, D, E, F$, G, Z extends Exception> F6<A, B, C, D, E, F$, Validation<Z, G>> f(Try6<A, B, C, D, E, F$, G, Z> t) {
        return F6Functions.toF6(t);
    }

    public static <A, B, C, D, E, F$, G, H, Z extends Exception> F7<A, B, C, D, E, F$, G, Validation<Z, H>> f(Try7<A, B, C, D, E, F$, G, H, Z> t) {
        return F7Functions.toF7(t);
    }

    public static <A, B, C, D, E, F$, G, H, I, Z extends Exception> F8<A, B, C, D, E, F$, G, H, Validation<Z, I>> f(Try8<A, B, C, D, E, F$, G, H, I, Z> t) {
        return F8Functions.toF8(t);
    }

    public static <A> IO<A> io(Try0<A, ? extends IOException> t) {
        return IOFunctions.io(t);
    }

}
