package fj.data;

import fj.F;
import fj.Unit;
import fj.function.Try0;

import java.io.IOException;

/**
 * IO monad for processing files
 *
 * @author Martin Grotzke
 *
 * @param <A> the type of the result produced by the IO
 */
@FunctionalInterface
public interface IO<A> extends Try0<A, IOException> {

	A run() throws IOException;

	default A f() throws IOException {
		return run();
	}

	default SafeIO<Validation<IOException, A>> safe() {
		return IOFunctions.toSafeValidation(this);
	}

	default <B> IO<B> map(F<A, B> f) {
		return IOFunctions.map(this, f);
	}

	default <B> IO<B> bind(F<A, IO<B>> f) {
		return IOFunctions.bind(this, f);
	}

	default <B> IO<B> append(IO<B> iob) {
		return IOFunctions.append(this, iob);
	}

	public static IO<LazyString> getContents() {
		return () -> IOFunctions.getContents().run();
	}

	public static IO<Unit> interact(F<LazyString, LazyString> f) {
		return () -> IOFunctions.interact(f).run();
	}

}
