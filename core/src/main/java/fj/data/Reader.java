package fj.data;

import fj.F;
import fj.F1Functions;

/**
 * The Reader monad (also called the function monad, so equivalent to the idea of F).
 * Created by MarkPerry on 7/07/2014.
 */
public class Reader<A, B> {

	private final F<A, B> function;

	public Reader(F<A, B> f) {
		function = f;
	}

	public final F<A, B> getFunction() {
		return function;
	}

	public static <A, B> Reader<A, B> unit(F<A, B> f) {
		return new Reader<>(f);
	}

	public static <A, B> Reader<A, B> constant(B b) {
		return unit(a -> b);
	}

	public final B f(A a) {
		return function.f(a);
	}

	public final <C> Reader<A, C> map(F<B, C> f) {
		return unit(F1Functions.andThen(function, f));
	}

	public final <C> Reader<A, C> andThen(F<B, C> f) {
		return map(f);
	}

	public final <C> Reader<A, C> flatMap(F<B, Reader<A, C>> f) {
		return unit(a -> f.f(function.f(a)).f(a));
	}

	public final <C> Reader<A, C> bind(F<B, Reader<A, C>> f) {
		return flatMap(f);
	}

}
