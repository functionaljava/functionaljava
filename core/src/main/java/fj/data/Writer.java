package fj.data;

import fj.F;
import fj.F2;
import fj.P;
import fj.P2;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public class Writer<W, A> {

	private A val;
	private W logValue;
	private F2<W, W, W> append;

	public static final F<Object, String> LOG_FUNCTION = o -> "Added " + o + " to the log\n";
	public static final F2<String, String, String> STRING_CONCAT = (String a, String b) -> a + b;
	public static final String STRING_EMPTY = "";

	private Writer(A a, W w, F2<W, W, W> f) {
		val = a;
		logValue = w;
		append = f;
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

	public static <W, A> Writer<W, A> unit(A a, W w, F2<W, W, W> f) {
		return new Writer<W, A>(a, w, f);
	}

	public Writer<W, A> tell(W w) {
		return unit(val, append.f(logValue, w), append);
	}

	public <B> Writer<W, B> map(F<A, B> f) {
		return unit(f.f(val), logValue, append);
	}

	public <B> Writer<W, B> flatMap(F<A, Writer<W, B>> f) {
		Writer<W, B> writer = f.f(val);
		return unit(writer.val, writer.append.f(logValue, writer.logValue), writer.append);
	}

	public static <B> Writer<String, B> unit(B b) {
		return unit(b, STRING_EMPTY, STRING_CONCAT);
	}

	public static <W, B> Writer<String, B> log(B b) {
		return unit(b, LOG_FUNCTION.f(b), STRING_CONCAT);
	}

	public static <A> F<A, Writer<String, A>> stringLogger() {
		return a -> Writer.unit(a, LOG_FUNCTION.f(a), STRING_CONCAT);
	}

}
