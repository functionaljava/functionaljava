package fj.function;

import fj.*;

import static fj.Unit.unit;

/**
 * Created by mperry on 28/08/2014.
 */
public interface Effect0 {

	void f();

	default F0<Unit> toF0() {
		return () -> {
			f();
			return unit();
		};
	}

	default <E extends Exception> TryEffect0<E> toTryEffect0() {
		return () -> f();
	}

	default <E extends Exception> Try0<Unit, E> toTry0() {
		return () -> {
			f();
			return unit();
		};
	}

	default P1<Unit> toP1() {
		return P.lazy(() -> {
			f();
			return unit();
		});
	}

}
