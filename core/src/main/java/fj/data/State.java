package fj.data;

import fj.*;

import java.util.*;

import static fj.P.p;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public class State<S, A> {

	private F<S, P2<S, A>> run;

	private State(F<S, P2<S, A>> f) {
		run = f;
	}

	public P2<S, A> run(S s) {
		return run.f(s);
	}

	public static <S, A> State<S, A> unit(F<S, P2<S, A>> f) {
		return new State<S, A>(f);
	}

	public static <S> State<S, S> units(F<S, S> f) {
		return unit((S s) -> {
			S s2 = f.f(s);
			return p(s2, s2);
		});
	}

	public static <S, A> State<S, A> constant(A a) {
		return unit(s -> p(s, a));
	}

	public <B> State<S, B> map(F<A, B> f) {
		return unit((S s) -> {
			P2<S, A> p2 = run(s);
			B b = f.f(p2._2());
			return p(p2._1(), b);
		});
	}

	public static <S> State<S, Unit> modify(F<S, S> f) {
		return State.<S>init().flatMap(s -> unit(s2 -> p(f.f(s), Unit.unit())));
	}

	public <B> State<S, B> mapState(F<P2<S, A>, P2<S, B>> f) {
		return unit(s -> f.f(run(s)));
	}

	public static <S, B, C> State<S, C> flatMap(State<S, B> mb, F<B, State<S, C>> f) {
		return mb.flatMap(f);
	}

	public <B> State<S, B> flatMap(F<A, State<S, B>> f) {
		return unit((S s) -> {
			P2<S, A> p = run(s);
			A a = p._2();
			S s2 = p._1();
			State<S, B> smb = f.f(a);
			return smb.run(s2);
		});
	}

	public static <S> State<S, S> init() {
		return unit(s -> p(s, s));
	}

	public State<S, S> gets() {
		return unit(s -> {
			P2<S, A> p = run(s);
			S s2 = p._1();
			return p(s2, s2);
		});
	}

	public static <S> State<S, Unit> put(S s) {
		return State.unit((S z) -> p(s, Unit.unit()));
	}

	public A eval(S s) {
		return run(s)._2();
	}

	public S exec(S s) {
		return run(s)._1();
	}

	public State<S, A> withs(F<S, S> f) {
		return unit(F1Functions.andThen(f, run));
	}

	public static <S, A> State<S, A> gets(F<S, A> f) {
		return State.<S>init().map(s -> f.f(s));
	}

	/**
	 * Evaluate each action in the sequence from left to right, and collect the results.
	 */
	public static <S, A> State<S, List<A>> sequence(List<State<S, A>> list) {
		return list.foldLeft((State<S, List<A>> acc, State<S, A> ma) ->
			acc.flatMap((List<A> xs) -> ma.map((A x) -> xs.snoc(x))
		), constant(List.<A>nil()));
	}

	/**
	 * Map each element of a structure to an action, evaluate these actions from left to right
	 * and collect the results.
	 */
	public static <S, A, B> State<S, List<B>> traverse(List<A> list, F<A, State<S, B>> f) {
		return list.foldLeft((State<S, List<B>> acc, A a) ->
			acc.flatMap(bs -> f.f(a).map(b -> bs.snoc(b))
		), constant(List.<B>nil()));
	}

}
