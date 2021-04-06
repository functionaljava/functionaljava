package fj.data;

import fj.Equal;
import fj.F;
import fj.Hash;

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

        public boolean exists(final F<A, Boolean> f) {
            return e.either(a -> f.f(a), b -> false, c -> false);
        }

        public <X> Either3<X, B, C> bind(F<A, Either3<X, B, C>> f) {
            return e.either(a -> f.f(a), b -> middle(b), c -> right(c));
        }

        public <X, Y> Option<Either3<A, X, Y>> filter(final F<A, Boolean> f) {
            return e.either(a -> f.f(a) ? some(left(a)) : none(), b -> none(), c -> none());
        }


        public <X> Either3<X, B, C> map(final F<A, X> f) {
            return e.either(a -> left(f.f(a)), b -> middle(b), c -> right(c));
        }

    }

    public static final class MiddleProjection<A, B, C> {
        private final Either3<A, B, C> e;

        private MiddleProjection(final Either3<A, B, C> e) {
            this.e = e;
        }

        public <X> Either3<A, X, C> bind(F<B, Either3<A, X, C>> f) {
            return e.either(a -> left(a), b -> f.f(b), c -> right(c));
        }

    }

    public static final class RightProjection<A, B, C> {
        private final Either3<A, B, C> e;

        private RightProjection(final Either3<A, B, C> e) {
            this.e = e;
        }

        public <X> Either3<A, B, X> bind(F<C, Either3<A, B, X>> f) {
            return e.either(a -> left(a), b -> middle(b), c -> f.f(c));
        }
    }

    public static <A, B, C> Either3<A, B, C> left(A a) {
        return new Left<>(a);
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
