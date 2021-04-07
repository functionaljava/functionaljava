package fj.function;

import java.util.function.BiConsumer;

/**
 * Created by mperry on 28/08/2014.
 */
public interface Effect2<A, B> extends BiConsumer<A, B> {

	void f(A a, B b);

	default void accept(A a, B b) {
		f(a, b);
	}

}
