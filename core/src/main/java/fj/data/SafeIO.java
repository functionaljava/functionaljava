package fj.data;

import fj.F0;

/**
 * Created by MarkPerry on 3/07/2014.
 */
@FunctionalInterface
public interface SafeIO<A> extends IO<A>, F0<A> {

	@Override
	A run();

	@Override
	default A f() {
		return run();
	}

}

