package fj;

import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.Validation;
import fj.function.*;

import java.io.IOException;

import static fj.data.Validation.fail;
import static fj.data.Validation.success;

/**
 * Created by mperry on 24/07/2014.
 */
public class Try {

    /**
     * Promotes the Try0 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try0 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, E extends Exception> P1<Validation<E, A>> f(final Try0<A, E> t) {
        return P.lazy(u -> {
            try {
                return success(t.f());
            } catch (Exception e) {
                return fail((E) e);
            }
        });
    }

    /**
     * Promotes the Try1 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try1 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, E extends Exception> F<A, Validation<E, B>> f(final Try1<A, B, E> t) {
        return a -> {
            try {
                return Validation.success(t.f(a));
            } catch (Exception e) {
                return fail((E) e);
            }
        };
    }

    /**
     * Promotes the Try2 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try2 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, E extends Exception> F2<A, B, Validation<E, C>> f(final Try2<A, B, C, E> t) {
        return (a, b) -> {
            try {
                return success(t.f(a, b));
            } catch (Exception e) {
                return fail((E) e);
            }
        };
    }

    /**
     * Promotes the Try3 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try3 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E extends Exception> F3<A, B, C, Validation<E, D>> f(final Try3<A, B, C, D, E> t) {
        return (a, b, c) -> {
            try {
                return success(t.f(a, b, c));
            } catch (Exception e) {
                return fail((E) e);
            }
        };
    }

    /**
     * Promotes the Try4 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try4 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, Z extends Exception> F4<A, B, C, D, Validation<Z, E>> f(final Try4<A, B, C, D, E, Z> t) {
        return (a, b, c, d) -> {
            try {
                return success(t.f(a, b, c, d));
            } catch (Exception ex) {
                return fail((Z) ex);
            }
        };
    }

    /**
     * Promotes the Try5 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try5 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, Z extends Exception> F5<A, B, C, D, E, Validation<Z, F>> f(final Try5<A, B, C, D, E, F, Z> t) {
        return (a, b, c, d, e) -> {
            try {
                return success(t.f(a, b, c, d, e));
            } catch (Exception ex) {
                return fail((Z) ex);
            }
        };
    }

    /**
     * Promotes the Try6 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try6 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, G, Z extends Exception> F6<A, B, C, D, E, F, Validation<Z, G>> f(final Try6<A, B, C, D, E, F, G, Z> t) {
        return (a, b, c, d, e, f) -> {
            try {
                return success(t.f(a, b, c, d, e, f));
            } catch (Exception ex) {
                return fail((Z) ex);
            }
        };
    }

    /**
     * Promotes the Try7 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try7 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    static public <A, B, C, D, E, F, G, H, Z extends Exception> F7<A, B, C, D, E, F, G, Validation<Z, H>> f(final Try7<A, B, C, D, E, F, G, H, Z> t) {
        return (a, b, c, d, e, f, g) -> {
            try {
                return success(t.f(a, b, c, d, e, f, g));
            } catch (Exception ex) {
                return fail((Z) ex);
            }
        };
    }

    /**
     * Promotes the Try8 to a Validation that returns an Exception on the failure side and its result on the success side.
     *
     * @param t A Try8 to promote
     * @return A Validation with an Exception on the failure side and its result on the success side.
     */
    public static <A, B, C, D, E, F, G, H, I, Z extends Exception> F8<A, B, C, D, E, F, G, H, Validation<Z, I>> f(final Try8<A, B, C, D, E, F, G, H, I, Z> t) {
        return (a, b, c, d, e, f, g, h) -> {
            try {
                return success(t.f(a, b, c, d, e, f, g, h));
            } catch (Exception ex) {
                return fail((Z) ex);
            }
        };
    }

    public static <A> IO<A> io(Try0<A, ? extends IOException> t) {
        return IOFunctions.io(t);
    }

}
