package fj.data;

import fj.*;
import fj.function.Effect1;

import java.util.Collection;
import java.util.Iterator;

import static fj.Function.identity;
import static fj.P.p;
import static fj.Unit.unit;
import static fj.data.Array.mkArray;
import static fj.data.List.*;
import static fj.data.Option.none;
import static fj.data.Option.some;

public abstract class Either3<A, B, C> {

    private Either3() {}

    private static final class Left<A, B, C> extends Either3<A, B, C> {
        private final A a;

        Left(A a) {
            this.a = a;
        }

        @Override
        public <D> D either(F<A, D> fa, F<B, D> fb, F<C, D> fc) {
            return fa.f(a);
        }

    }

    private static final class Middle<A, B, C> extends Either3<A, B, C> {
        private final B b;

        Middle(B b) {
            this.b = b;
        }

        @Override
        public <D> D either(F<A, D> fa, F<B, D> fb, F<C, D> fc) {
            return fb.f(b);
        }
    }

    private static final class Right<A, B, C> extends Either3<A, B, C> {
        private final C c;
        Right(C c) {
            this.c = c;
        }

        @Override
        public <D> D either(F<A, D> fa, F<B, D> fb, F<C, D> fc) {
            return fc.f(c);
        }



    }

    public static final class LeftProjection<A, B, C> {
        private final Either3<A, B, C> e;

        private LeftProjection(final Either3<A, B, C> e) {
            this.e = e;
        }

        public <X> Either3<X, B, C> apply(final Either3<F<A, X>, B, C> e) {
            return e.left().bind(this::map);
        }

        public <X> Either3<X, B, C> bind(F<A, Either3<X, B, C>> f) {
            return e.either(a -> f.f(a), b -> middle(b), c -> right(c));
        }

        public Either3<A, B, C> either() {
            return e;
        }

        public boolean exists(final F<A, Boolean> f) {
            return e.either(a -> f.f(a), b -> false, c -> false);
        }

        public <X, Y> Option<Either3<A, X, Y>> filter(final F<A, Boolean> f) {
            return e.either(a -> f.f(a) ? some(left(a)) : none(), b -> none(), c -> none());
        }

        public boolean forall(final F<A, Boolean> f) {
            return e.either(a -> f.f(a), b -> true, c -> true);
        }

        public Unit foreach(final F<A, Unit> f) {
            return e.either(a -> f.f(a), b -> unit(), c -> unit());
        }

        public void foreachDoEffect(final Effect1<A> f) {
            e.either(a -> f.toF().f(a), b -> unit(), c -> unit());
        }

        public Iterator<A> iterator() {
            return toList().iterator();
        }

        public <X> Either3<X, B, C> map(final F<A, X> f) {
            return e.either(a -> left(f.f(a)), b -> middle(b), c -> right(c));
        }

        public A orValue(final A value) {
            return orValue(() -> value);
        }

        public A orValue(final F0<A> f) {
            return e.either(a -> a, b -> f.f(), c -> f.f());
        }

        public <X> Either3<X, B, C> sequence(final Either3<X, B, C> e) {
            return bind(Function.constant(e));
        }

        public Array<A> toArray() {
            return e.either(
                a -> Array.single(a),
                b -> Array.empty(),
                c -> Array.empty()
            );
        }

        public Collection<A> toCollection() {
            return toList().toCollection();
        }

        public List<A> toList() {
            return e.either(a -> single(a), b -> nil(), c -> nil());
        }

        public Option<A> toOption() {
            return e.either(a -> some(a), b -> none(), c -> none());
        }

        public Stream<A> toStream() {
            return e.either(a -> Stream.single(a), b -> Stream.nil(), c -> Stream.nil());
        }

        public <X> IO<Either3<X, B, C>> traverseIO(final F<A, IO<X>> f) {
            return e.either(
                a -> f.f(a).map(Either3::left),
                b -> IOFunctions.unit(middle(b)),
                c -> IOFunctions.unit(right(c))
            );
        }

        public <X> List<Either3<X, B, C>> traverseList1(final F<A, List<X>> f) {
            return e.either(
                    a -> f.f(a).map(Either3::left),
                    b -> single(middle(b)),
                    c -> single(right(c))
            );
        }

        public <X> Option<Either3<X, B, C>> traverseOption(final F<A, Option<X>> f) {
            return e.either(
                    a -> f.f(a).map(Either3::left),
                    b -> some(middle(b)),
                    c -> some(right(c))
            );

        }

        public <X> P1<Either3<X, B, C>> traverseP1(final F<A, P1<X>> f) {
            return e.either(
                    a -> f.f(a).map(Either3::left),
                    b -> p(middle(b)),
                    c -> p(right(c))
            );

        }

        public <X> Stream<Either3<X, B, C>> traverseStream(final F<A, Stream<X>> f) {
            return e.either(
                    a -> f.f(a).map(Either3::left),
                    b -> Stream.single(middle(b)),
                    c -> Stream.single(right(c))
            );

        }

    }

    public static final class MiddleProjection<A, B, C> {
        private final Either3<A, B, C> e;

        private MiddleProjection(final Either3<A, B, C> e) {
            this.e = e;
        }

        public <X> Either3<A, X, C> apply(final Either3<A, F<B, X>, C> e) {
            return e.middle().bind(this::map);
        }

        public <X> Either3<A, X, C> bind(F<B, Either3<A, X, C>> f) {
            return e.either(a -> left(a), b -> f.f(b), c -> right(c));
        }

        public Either3<A, B, C> either() {
            return e;
        }

        public boolean exists(final F<B, Boolean> f) {
            return e.either(a -> false, b -> f.f(b), c -> false);
        }

        public <X, Y> Option<Either3<X, B, Y>> filter(final F<B, Boolean> f) {
            return e.either(a -> none(), b -> f.f(b) ? some(middle(b)) : none(), c -> none());
        }

        public boolean forall(final F<B, Boolean> f) {
            return e.either(a -> true, b -> f.f(b), c -> true);
        }

        public Unit foreach(final F<B, Unit> f) {
            return e.either(a -> unit(), b -> f.f(b), c -> unit());
        }

        public void foreachDoEffect(final Effect1<B> f) {
            e.either(a -> unit(), b -> f.toF().f(b), c -> unit());
        }

        public Iterator<B> iterator() {
            return toList().iterator();
        }

        public <X> Either3<A, X, C> map(final F<B, X> f) {
            return e.either(a -> left(a), b -> middle(f.f(b)), c -> right(c));
        }

        public B orValue(final B value) {
            return orValue(() -> value);
        }

        public B orValue(final F0<B> f) {
            return e.either(a -> f.f(), b -> b, c -> f.f());
        }

        public <X> Either3<A, X, C> sequence(final Either3<A, X, C> e) {
            return bind(Function.constant(e));
        }

        public Array<B> toArray() {
            return e.either(
                    a -> Array.empty(),
                    b -> Array.single(b),
                    c -> Array.empty()
            );
        }

        public Collection<B> toCollection() {
            return toList().toCollection();
        }

        public List<B> toList() {
            return e.either(a -> nil(), b -> single(b), c -> nil());
        }

        public Option<B> toOption() {
            return e.either(a -> none(), b -> some(b), c -> none());
        }

        public Stream<B> toStream() {
            return e.either(a -> Stream.nil(), b -> Stream.single(b), c -> Stream.nil());
        }

        public <X> IO<Either3<A, X, C>> traverseIO(final F<B, IO<X>> f) {
            return e.either(
                    a -> IOFunctions.unit(left(a)),
                    b -> f.f(b).map(Either3::middle),
                    c -> IOFunctions.unit(right(c))
            );
        }

        public <X> List<Either3<A, X, C>> traverseList1(final F<B, List<X>> f) {
            return e.either(
                    a -> single(left(a)),
                    b -> f.f(b).map(Either3::middle),
                    c -> single(right(c))
            );
        }

        public <X> Option<Either3<A, X, C>> traverseOption(final F<B, Option<X>> f) {
            return e.either(
                    a -> some(left(a)),
                    b -> f.f(b).map(Either3::middle),
                    c -> some(right(c))
            );

        }

        public <X> P1<Either3<A, X, C>> traverseP1(final F<B, P1<X>> f) {
            return e.either(
                    a -> p(left(a)),
                    b -> f.f(b).map(Either3::middle),
                    c -> p(right(c))
            );

        }

        public <X> Stream<Either3<A, X, C>> traverseStream(final F<B, Stream<X>> f) {
            return e.either(
                    a -> Stream.single(left(a)),
                    b -> f.f(b).map(Either3::middle),
                    c -> Stream.single(right(c))
            );

        }


    }

    public final <X> Either3<X, B, C> leftMap(F<A, X> f) {
        return left().map(f);
    }

    public final <X> F<F<A, X>, Either3<X, B, C>> leftMap_() {
        return this::leftMap;
    }

    public final <X> Either3<A, X, C> middleMap(F<B, X> f) {
        return middle().map(f);
    }

    public final <X> F<F<B, X>, Either3<A, X, C>> middleMap_() {
        return this::middleMap;
    }

    public final <X> Either3<A, B, X> rightMap(F<C, X> f) {
        return right().map(f);
    }

    public final <X> F<F<C, X>, Either3<A, B, X>> rightMap_() {
        return this::rightMap;
    }

    public static final class RightProjection<A, B, C> {
        private final Either3<A, B, C> e;

        private RightProjection(final Either3<A, B, C> e) {
            this.e = e;
        }

        public <X> Either3<A, B, X> bind(F<C, Either3<A, B, X>> f) {
            return e.either(a -> left(a), b -> middle(b), c -> f.f(c));
        }

        public <X> Either3<A, B, X> map(final F<C, X> f) {
            return e.either(a -> left(a), b -> middle(b), c -> right(f.f(c)));
        }

    }

    public static <A, B, C> Either3<A, B, C> left(A a) {
        return new Left<>(a);
    }

    public static <A, B, C> F<A, Either3<A, B, C>> left_() {
        return Either3::left;
    }

    public static <A, B, C> Either3<A, B, C> middle(B b) {
        return new Middle<>(b);
    }

    public static <A, B, C> Either3<A, B, C> right(C c) {
        return new Right<>(c);
    }

    public boolean isLeft() {
        return either(a -> true, b -> false, c -> false);
    }

    public boolean isMiddle() {
        return either(a -> false, b -> true, c -> false);
    }

    public boolean isRight() {
        return either(a -> false, b -> false, c -> true);
    }

    public <X, Y, Z> Either3<X, Y, Z> map3(F<A, X> fl, F<B, Y> fm, F<C, Z> fr) {
        return either(
                a -> left(fl.f(a)),
                b -> middle(fm.f(b)),
                c -> right(fr.f(c))
        );
    }

    public abstract <D> D either(F<A, D> fa, F<B, D> fb, F<C, D> fc);

    public static <A, B, C, D> F<Either3<A, B, C>, D> either_(F<A, D> fa, F<B, D> fb, F<C, D> fc) {
        return e -> e.either(fa, fb, fc);
    }

    public static <A, B, C> Either3<A, B, C> joinLeft(final Either3<Either3<A, B, C>, B, C> e) {
        return e.left().bind(identity());
    }

    public static <A, B, C> Either3<A, B, C> joinMiddle(final Either3<A, Either3<A, B, C>, C> e) {
        return e.middle().bind(identity());
    }

    public static <A, B, C> Either3<A, B, C> joinRight(final Either3<A, B, Either3<A, B, C>> e) {
        return e.right().bind(identity());
    }

    public Either3<B, C, A> moveLeft() {
        return either(a -> right(a), b -> left(b), c -> middle(c));
    }

    public Either3<C, A, B> moveRight() {
        return either(a -> middle(a), b -> right(b), c -> left(c));
    }

    public Either3<C, B, A> swap() {
        return either(a -> right(a), b -> middle(b), c -> left(c));
    }

    public Either3<B, A, C> swapLefts() {
        return either(a -> middle(a), b -> left(b), c -> right(c));
    }

    public Either3<A, C, B> swapRights() {
        return either(a -> left(a), b -> right(b), c -> middle(c));
    }

    public Option<A> leftOption() {
        return either(a -> some(a), b -> none(), c -> none());
    }

    public Option<B> middleOption() {
        return either(a -> none(), b -> some(b), c -> none());
    }

    public Option<C> rightOption() {
        return either(a -> none(), b -> none(), c -> some(c));
    }

    public final LeftProjection<A, B, C> left() {
        return new LeftProjection<>(this);
    }

    public final MiddleProjection<A, B, C> middle() {
        return new MiddleProjection<>(this);
    }

    public final RightProjection<A, B, C> right() {
        return new RightProjection<>(this);
    }

    @Override
    public final boolean equals(Object other) {
        return Equal.equals0(Either3.class, this, other, () -> Equal.either3Equal(Equal.anyEqual(), Equal.anyEqual(), Equal.anyEqual()));
    }

    @Override
    public final int hashCode() {
        return Hash.either3Hash(Hash.<A>anyHash(), Hash.<B>anyHash(), Hash.<C>anyHash()).hash(this);
    }

}
