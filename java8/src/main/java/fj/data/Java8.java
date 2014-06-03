package fj.data;

import fj.*;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by mperry on 3/06/2014.
 */
public final class Java8 {

    private Java8() {
        throw new UnsupportedOperationException();
    }

    public static <A> P1<A> toP1(Supplier<A> f) {
        return () -> f.get();
    }

    public static <A> Supplier<A> toSupplier(P1<A> p) {
        return () -> p._1();
    }

    public static <A, B> F<A, B> toF(Function<A, B> f) {
        return a -> f.apply(a);
    }

    public static <A, B> Function<A, B> toFunction(F<A, B> f) {
        return a -> f.f(a);
    }

    public static <A, B, C> F2<A, B, C> toF2(BiFunction<A, B, C> f) {
        return (a, b) -> f.apply(a, b);
    }

    public static <A, B, C> BiFunction<A, B, C> toBiFunction(F2<A, B, C> f) {
        return (a, b) -> f.f(a, b);
    }

    static public <A> Supplier<Validation<Exception, A>> toSupplier(final TryCatch0<A> t) {
        return toSupplier(P1Functions.toP1(t));
    }

    static public <A, B> Function<A, Validation<Exception, B>> toFunction(final TryCatch1<A, B> t) {
        return toFunction(F1Functions.toF1(t));
    }

    static public <A, B, C> BiFunction<A, B, Validation<Exception, C>> toBiFunction(final TryCatch2<A, B, C> t) {
        return toBiFunction(F2Functions.toF2(t));
    }

}
