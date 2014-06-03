package fj.data;

import fj.F;
import fj.F2;
import fj.P1;

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

    public static <A, B> F<A, B> toFunction(F<A, B> f) {
        return a -> f.f(a);
    }

    public static <A, B, C> F2<A, B, C> toF2(BiFunction<A, B, C> f) {
        return (a, b) -> f.apply(a, b);
    }

    public static <A, B, C> BiFunction<A, B, C> toBiFunction(F2<A, B, C> f) {
        return (a, b) -> f.f(a, b);
    }

}
