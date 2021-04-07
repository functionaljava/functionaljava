package fj.function;

import fj.F;
import fj.Unit;

import java.util.function.Consumer;

import static fj.Unit.unit;

/**
 * Created by mperry on 28/08/2014.
 */
public interface Effect1<A> extends Consumer<A> {

	void f(A a);

	default void accept(A a) {
		f(a);
	}

	default F<A, Unit> toF() {
		return a -> {
			f(a);
			return unit();
		};
	}

}
