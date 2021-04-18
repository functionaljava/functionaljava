package fj.function;

import fj.F;
import fj.P;
import fj.P1;
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

	default <C> F<A, C> bind(final F<Unit, F<A, C>> g) {
		return a -> {
			return g.f(toF().f(a)).f(a);
		};
	}

	default void apply(A a) {
		f(a);
	}

	default <C> Effect1<C> contramap(F<C, A> f) {
		return o(f);
	}

	default <C> F<A, C> map(F<Unit, C> f) {
		return a -> f.f(toF().f(a));
	}

	default <B> F<A, B> andThen(final F<Unit, B> f) {
		return map(f);
	}

	default <C> Effect1<C> o(final F<C, A> f) {
		return c -> f(f.f(c));
	}

	default F<A, Unit> toF() {
		return a -> {
			f(a);
			return unit();
		};
	}

	static <A> Effect1<A> fromF(F<A, Unit> f) {
		return a -> f.f(a);
	}

	default <C, D> F<C, D> dimap(F<C, A> f, F<Unit, D> g) {
		return c -> g.f(toF().f(f.f(c)));
	}

	default P1<Unit> partial(final A a) {
		return P.lazy(() -> toF().f(a));
	}

}
