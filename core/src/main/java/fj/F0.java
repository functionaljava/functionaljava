package fj;

import fj.function.Effect0;
import fj.function.Try0;
import fj.function.TryEffect0;

import java.util.function.Supplier;

@FunctionalInterface
public interface F0<A> extends Supplier<A> {

    A f();

    default A get() {
        return f();
    }

    default Effect0 toEffect0() {
        return () -> f();
    }

    default <Z extends Exception> TryEffect0<Z> toTryEffect0() {
        return () -> f();
    }

    default <Z extends Exception> Try0<A, Z> toTry0() {
        return () -> f();
    }

    default P1<A> toP1() {
        return P.lazy(() -> f());
    }

}
