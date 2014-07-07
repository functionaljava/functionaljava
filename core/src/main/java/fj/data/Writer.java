package fj.data;

import fj.F;
import fj.F2;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public class Writer<W, A> {

	private A value;
	private W log;
	private F2<W, W, W> plus;

	public static final F<Object, String> LOG_FUNCTION = o -> "Added " + o + " to the log\n";
	public static final F2<String, String, String> STRING_CONCAT = (String a, String b) -> a + b;
	public static final String STRING_EMPTY = "";

	private Writer(A a, W w, F2<W, W, W> f) {
		value = a;
		log = w;
		plus = f;
	}

	public static <W, B> Writer<W, B> unit(B a, W w, F2<W, W, W> f) {
		return new Writer<W, B>(a, w, f);
	}

	public Writer<W, A> tell(W w) {
		return unit(value, plus.f(log, w), plus);
	}

	public <B> Writer<W, B> map(F<A, B> f) {
		return unit(f.f(value), log, plus);
	}

	public <B> Writer<W, B> flatMap(F<A, Writer<W, B>> f) {
		Writer<W, B> writer = f.f(value);
		return unit(writer.value, plus.f(log, writer.log), plus);
	}

	public static <B> Writer<String, B> unit(B b) {
		return unit(b, STRING_EMPTY, STRING_CONCAT);
	}

	public static <W, B> Writer<String, B> log(B b) {
		return unit(b, LOG_FUNCTION.f(b), STRING_CONCAT);
	}

	public static <A> F<A, Writer<String, A>> log() {
		return a -> Writer.unit(a, LOG_FUNCTION.f(a), STRING_CONCAT);
	}

}
