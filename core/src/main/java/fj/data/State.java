package fj.data;

import fj.*;

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

	public static <S1, A1> State<S1, A1> unitF(F<S1, P2<S1, A1>> f) {
		return new State<S1, A1>(f);
	}

	public static <S1> State<S1, S1> unitS(F<S1, S1> f) {
		return unitF((S1 s) -> {
			S1 s2 = f.f(s);
			return p(s2, s2);
		});
	}

	public static <S1, A1> State<S1, A1> unit(A1 a) {
		return unitF(s -> p(s, a));
	}

	public <B> State<S, B> map(F<A, B> f) {
		return unitF((S s) -> {
			P2<S, A> p2 = run(s);
			B b = f.f(p2._2());
			return p(p2._1(), b);
		});
	}

	public static <S> State<S, Unit> modify(F<S, S> f) {
		return State.<S>get().flatMap(s -> unitF(s2 -> p(f.f(s), Unit.unit())));
	}

	public <B> State<S, B> mapState(F<P2<S, A>, P2<S, B>> f) {
		return unitF(s -> f.f(run(s)));
	}

	public static <S, B, C> State<S, C> flatMap(State<S, B> mb, F<B, State<S, C>> f) {
		return mb.flatMap(f);
	}

	public <B> State<S, B> flatMap(F<A, State<S, B>> f) {
		return unitF((S s) -> {
			P2<S, A> p = run(s);
			A a = p._2();
			S s2 = p._1();
			State<S, B> smb = f.f(a);
			return smb.run(s2);
		});
	}


	public static <S1> State<S1, S1> get() {
		return unitF(s -> p(s, s));
	}

	public State<S, S> gets() {
		return unitF(s -> {
			P2<S, A> p = run(s);
			S s2 = p._1();
			return p(s2, s2);
		});
	}

	public static <S1> State<S1, Unit> put(S1 s) {
		return State.unitF((S1 z) -> p(s, Unit.unit()));
	}

	public A eval(S s) {
		return run(s)._2();
	}

	public S exec(S s) {
		return run(s)._1();
	}

	public State<S, A> withs(F<S, S> f) {
		return unitF(F1Functions.andThen(f, run));
	}

	public static <S, A> State<S, A> gets(F<S, A> f) {
		return State.<S>get().map(s -> f.f(s));
	}

}
