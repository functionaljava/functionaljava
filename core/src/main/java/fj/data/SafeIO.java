package fj.data;

@FunctionalInterface
public interface SafeIO<A> extends IO<A> {

	@Override
	A run();

	@Override
	default A f() {
		return run();
	}

}

