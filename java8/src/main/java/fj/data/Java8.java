package fj.data;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import fj.F;
import fj.F2;
import fj.P;
import fj.P1;
import fj.Try;
import fj.Unit;
import fj.function.Try0;
import fj.function.Try1;
import fj.function.Try2;

/**
 * Created by mperry on 3/06/2014.
 */
public final class Java8 {

    private Java8() {
        throw new UnsupportedOperationException();
    }

    public static <A> P1<A> Supplier_P1(final Supplier<A> s) {
        return P.lazy(s::get);
    }

    public static <A> F<Supplier<A>, P1<A>> Supplier_P1() {
        return Java8::Supplier_P1;
    }

    public static <A> Supplier<A> P1_Supplier(final P1<A> p) {
        return p::_1;
    }

    public static <A> F<P1<A>, Supplier<A>> P1_Supplier() {
        return Java8::P1_Supplier;
    }

    public static <A, B> F<A, B> Function_F(final Function<A, B> f) {
        return f::apply;
    }

    public static <A, B> F<Function<A, B>, F<A, B>> Function_F() {
        return Java8::Function_F;
    }

    public static <A, B> Function<A, B> F_Function(final F<A, B> f) {
        return f::f;
    }

    public static <A, B> F<F<A, B>, Function<A, B>> F_Function() {
        return Java8::F_Function;
    }

    public static <A, B, C> F2<A, B, C> BiFunction_F2(final BiFunction<A, B, C> f) {
        return f::apply;
    }

    public static <A, B, C> F<BiFunction<A, B, C>, F2<A, B, C>> BiFunction_F2() {
        return Java8::BiFunction_F2;
    }

    public static <A, B, C> BiFunction<A, B, C> F2_BiFunction(final F2<A, B, C> f) {
        return f::f;
    }

    public static <A, B, C> F<F2<A, B, C>, BiFunction<A, B, C>> F2_BiFunction() {
        return Java8::F2_BiFunction;
    }

    public static <A, E extends Exception> Supplier<Validation<E, A>> TryCatch0_Supplier(final Try0<A, E> t) {
        return () -> Try.f(t)._1();
    }

    public static <A, E extends Exception> F<Try0<A, E>, Supplier<Validation<E, A>>> TryCatch0_Supplier() {
        return Java8::TryCatch0_Supplier;
    }

    public static <A, B, E extends Exception> Function<A, Validation<E, B>> TryCatch1_Function(final Try1<A, B, E> t) {
        return a -> Try.f(t).f(a);
    }

    public static <A, B, E extends Exception> F<Try1<A, B, E>, Function<A, Validation<E, B>>> TryCatch1_Function() {
        return Java8::TryCatch1_Function;
    }

    public static <A, B, C, E extends Exception> BiFunction<A, B, Validation<E, C>> TryCatch2_BiFunction(final Try2<A, B, C, E> t) {
        return (a, b) -> Try.f(t).f(a, b);
    }

    public static <A, B, C, E extends Exception> F<Try2<A, B, C, E>, BiFunction<A, B, Validation<E, C>>> TryCatch2_BiFunction() {
        return Java8::TryCatch2_BiFunction;
    }

    public static <A> java.util.stream.Stream<A> List_JavaStream(final List<A> list) {
        return Iterable_JavaStream(list);
    }

    public static <A> Option<A> Optional_Option(final Optional<A> o) {
        return o.isPresent() ? Option.some(o.get()) : Option.none();
    }

    public static <A> F<Optional<A>, Option<A>> Optional_Option() {
        return Java8::Optional_Option;
    }

    /**
     * Convert an Option to {@link Optional}. Will throw a {@link NullPointerException} if the Option is some(null).
     */
    public static <A> Optional<A> Option_Optional(final Option<A> o) {
        return o.option(Optional.empty(), Optional::of);
    }

    public static <A> F<Option<A>, Optional<A>> Option_Optional() {
        return Java8::Option_Optional;
    }

    public static <A> F<Consumer<A>, F<A, Unit>> Consumer_F() {
        return Java8::Consumer_F;
    }

    public static <A> F<A, Unit> Consumer_F(final Consumer<A> c) {
        return a -> {
            c.accept(a);
            return Unit.unit();
        };
    }

    public static <A> java.util.stream.Stream<A> Stream_JavaStream(final fj.data.Stream<A> s) {
        return Iterable_JavaStream(s);
    }

    public static <A> java.util.stream.Stream<A> Iterable_JavaStream(final Iterable<A> it) {
        return StreamSupport.stream(it.spliterator(), false);
    }

    public static <A> java.util.stream.Stream<A> Iterator_JavaStream(final Iterator<A> it) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }

    public static <A> F<fj.data.Stream<A>, java.util.stream.Stream<A>> Stream_JavaStream() {
        return Java8::Stream_JavaStream;
    }

    public static <A> Stream<A> JavaStream_Stream(final java.util.stream.Stream<A> s) {
        return Stream.iteratorStream(s.iterator());
    }

    public static <A> List<A> JavaStream_List(final java.util.stream.Stream<A> s) {
        return s.collect(Collectors.toList());
    }

    public static <A> Array<A> JavaStream_Array(final java.util.stream.Stream<A> s) {
        return s.collect(Collectors.toArray());
    }

}
