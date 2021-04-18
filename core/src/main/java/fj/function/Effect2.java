package fj.function;

import fj.F2;
import fj.Unit;

import java.util.function.BiConsumer;

import static fj.Unit.unit;

/**
 * Created by mperry on 28/08/2014.
 */
public interface Effect2<A, B> extends BiConsumer<A, B> {

	void f(A a, B b);

	default void accept(A a, B b) {
		f(a, b);
	}

	default F2<A, B, Unit> toF2() {
		return (a, b) -> {
			f(a, b);
			return unit();
		};
	}

}
