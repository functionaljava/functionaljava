package fj.data;

import fj.F;
import fj.Unit;

import static fj.Bottom.error;

public final class Case<A, B> {
    private final F<A, Boolean> c;
    private final F<A, B> f;

    public Case(F<A, Boolean> c, F<A, B> f) {
        this.c = c;
        this.f = f;
    }

    public static <A, B> B match(final A a, final F<Unit, List<Case<A, B>>> f) {
        final List<Case<A, B>> cases = f.f(Unit.unit());
        final Option<Case<A, B>> option = cases.find(new F<Case<A, B>, Boolean>() {
            @Override
            public Boolean f(Case<A, B> that) {
                return that.c.f(a);
            }
        });

        if(option.isNone()) throw error("partial function unmatched");
        else return option.some().f.f(a);
    }

    public static <A, B> Case when(F<A, Boolean> c, F<A, B> f) {
        return new Case<>(c, f);
    }

    public static <A, B> Case when(final Class<A> type, F<A, B> f) {
        return when(new F<A, Boolean>() {
            @Override
            public Boolean f(A o) {
                return type.isInstance(o);
            }
        }, f);
    }

    public static <A, B> Case otherwise(F<A, B> f) {
        return new Case<>(new F<A, Boolean>() {
            @Override
            public Boolean f(A a) {
                return true;
            }
        }, f);
    }
}
