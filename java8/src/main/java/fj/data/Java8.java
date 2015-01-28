package fj.data;

import fj.*;
import fj.function.Try0;
import fj.function.Try1;
import fj.function.Try2;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

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
        return s -> P.lazy(u -> s.get());
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

    static public <A, E extends Exception> Supplier<Validation<E, A>> TryCatch0_Supplier(final Try0<A, E> t) {
        return Java8.<A, E>TryCatch0_Supplier().f(t);
    }

    static public <A, E extends Exception> F<Try0<A, E>, Supplier<Validation<E, A>>> TryCatch0_Supplier() {
        return t -> () -> Try.f(t)._1();
    }

    static public <A, B, E extends Exception> Function<A, Validation<E, B>> TryCatch1_Function(final Try1<A, B, E> t) {
        return Java8.<A, B, E>TryCatch1_Function().f(t);
    }

    static public <A, B, E extends Exception> F<Try1<A, B, E>, Function<A, Validation<E, B>>> TryCatch1_Function() {
        return t -> a -> Try.f(t).f(a);
    }

    static public <A, B, C, E extends Exception> BiFunction<A, B, Validation<E, C>> TryCatch2_BiFunction(final Try2<A, B, C, E> t) {
        return Java8.<A, B, C, E>TryCatch2_BiFunction().f(t);
    }

    static public <A, B, C, E extends Exception> F<Try2<A, B, C, E>, BiFunction<A, B, Validation<E, C>>> TryCatch2_BiFunction() {
        return t -> (a, b) -> Try.f(t).f(a, b);
    }

    public static <A> java.util.stream.Stream<A> List_JavaStream(List<A> list) {
        return Iterable_JavaStream(list);
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

    static public <A> F<Consumer<A>, F<A, Unit>> Consumer_F() {
        return c -> Consumer_F(c);
    }

    public static <A> F<A, Unit> Consumer_F(Consumer<A> c) {
        return a -> {
            c.accept(a);
            return Unit.unit();
        };
    }

    static public <A> java.util.stream.Stream<A> Stream_JavaStream(fj.data.Stream<A> s) {
        return Iterable_JavaStream(s);
    }

    static public <A> java.util.stream.Stream<A> Iterable_JavaStream(Iterable<A> it) {
        return StreamSupport.stream(it.spliterator(), false);
    }

    static public <A> java.util.stream.Stream<A> Iterator_JavaStream(Iterator<A> it) {
        return Iterable_JavaStream(() -> it);
    }

    static public <A> F<fj.data.Stream<A>, java.util.stream.Stream<A>> Stream_JavaStream() {
        return s -> Stream_JavaStream(s);
    }

    static public <A> Stream<A> JavaStream_Stream(java.util.stream.Stream<A> s) {
        return s.collect(Collectors.toStream());
    }

    static public <A> List<A> JavaStream_List(java.util.stream.Stream<A> s) {
        return s.collect(Collectors.toList());
    }

    static public <A> Array<A> JavaStream_Array(java.util.stream.Stream<A> s) {
        return s.collect(Collectors.toArray());
    }

}
