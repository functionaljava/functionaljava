package fj.data;

import fj.*;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public final class Writer<W, A> {

	private final A val;
	private final W logValue;
	private final Monoid<W> monoid;

	private Writer(A a, W w, Monoid<W> m) {
		val = a;
		logValue = w;
        monoid = m;
	}

	public P2<W, A> run() {
		return P.p(logValue, val);
	}

	public A value() {
		return val;
	}

	public W log() {
		return logValue;
	}

	public Monoid<W> monoid() {
		return monoid;
	}

	public static <W, A> Writer<W, A> unit(A a, W w, Monoid<W> m) {
		return new Writer<>(a, w, m);
	}

    public static <W, A> Writer<W, A> unit(A a, Monoid<W> m) {
        return new Writer<>(a, m.zero(), m);
    }

    public Writer<W, A> tell(W w) {
		return unit(val, monoid.sum(logValue, w), monoid);
	}

	public <B> Writer<W, B> map(F<A, B> f) {
		return unit(f.f(val), logValue, monoid);
	}

	public <B> Writer<W, B> flatMap(F<A, Writer<W, B>> f) {
		Writer<W, B> writer = f.f(val);
		return unit(writer.val, writer.monoid.sum(logValue, writer.logValue), writer.monoid);
	}

	public static <B> Writer<String, B> unit(B b) {
		return unit(b, Monoid.stringMonoid);
	}

	public static <A> F<A, Writer<String, A>> stringLogger() {
		return a -> unit(a, Monoid.stringMonoid);
	}

}
