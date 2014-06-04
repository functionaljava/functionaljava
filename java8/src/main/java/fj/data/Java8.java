package fj.data;

import fj.*;

import java.util.Optional;
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

    public static <A> P1<A> Supplier_P1(final Supplier<A> s) {
        return Java8.<A>Supplier_P1().f(s);
    }

    public static <A> F<Supplier<A>, P1<A>> Supplier_P1() {
        return s -> () -> s.get();
    }

    public static <A> Supplier<A> P1_Supplier(final P1<A> p) {
        return Java8.<A>P1_Supplier().f(p);
    }

    public static <A> F<P1<A>, Supplier<A>> P1_Supplier() {
        return (p) -> () -> p._1();
    }

    public static <A, B> F<A, B> Function_F(final Function<A, B> f) {
        return Java8.<A, B>Function_F().f(f);
    }

    public static <A, B> F<Function<A, B>, F<A, B>> Function_F() {
        return f -> a -> f.apply(a);
    }

    public static <A, B> Function<A, B> F_Function(final F<A, B> f) {
        return Java8.<A, B>F_Function().f(f);
    }

    public static <A, B> F<F<A, B>, Function<A, B>> F_Function() {
        return f -> a -> f.f(a);
    }

    public static <A, B, C> F2<A, B, C> BiFunction_F2(final BiFunction<A, B, C> f) {
        return Java8.<A, B, C>BiFunction_F2().f(f);
    }

    public static <A, B, C> F<BiFunction<A, B, C>, F2<A, B, C>> BiFunction_F2() {
        return f -> (a, b) -> f.apply(a, b);
    }

    public static <A, B, C> BiFunction<A, B, C> F2_BiFunction(final F2<A, B, C> f) {
        return Java8.<A, B, C>F2_BiFunction().f(f);
    }

    public static <A, B, C> F<F2<A, B, C>, BiFunction<A, B, C>> F2_BiFunction() {
        return f -> (a, b) -> f.f(a, b);
    }

    static public <A> Supplier<Validation<Exception, A>> TryCatch0_Supplier(final TryCatch0<A> t) {
        return Java8.<A>TryCatch0_Supplier().f(t);
    }

    static public <A> F<TryCatch0<A>, Supplier<Validation<Exception, A>>> TryCatch0_Supplier() {
        return t -> () -> P1Functions.toP1(t)._1();
    }

    static public <A, B> Function<A, Validation<Exception, B>> TryCatch1_Function(final TryCatch1<A, B> t) {
        return Java8.<A, B>TryCatch1_Function().f(t);
    }

    static public <A, B> F<TryCatch1<A, B>, Function<A, Validation<Exception, B>>> TryCatch1_Function() {
        return t -> a -> F1Functions.toF1(t).f(a);
    }

    static public <A, B, C> BiFunction<A, B, Validation<Exception, C>> TryCatch2_BiFunction(final TryCatch2<A, B, C> t) {
        return Java8.<A, B, C>TryCatch2_BiFunction().f(t);
    }

    static public <A, B, C> F<TryCatch2<A, B, C>, BiFunction<A, B, Validation<Exception, C>>> TryCatch2_BiFunction() {
        return t -> (a, b) -> F2Functions.toF2(t).f(a, b);
    }

    static public <A> Option<A> Optional_Option(final Optional<A> o) {
        return Java8.<A>Optional_Option().f(o);
    }

    static public <A> F<Optional<A>, Option<A>> Optional_Option() {
        return o -> o.isPresent() ? Option.fromNull(o.get()) : Option.none();
    }

    static public <A> Optional<A> Option_Optional(final Option<A> o) {
        return Java8.<A>Option_Optional().f(o);
    }

    static public <A> F<Option<A>, Optional<A>> Option_Optional() {
        return o -> o.isSome() ? Optional.ofNullable(o.some()) : Optional.empty();
    }

}
