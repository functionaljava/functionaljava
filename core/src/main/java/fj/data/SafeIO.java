package fj.data;

import fj.F0;
import fj.P;
import fj.P1;

import java.io.IOException;

/**
 * Created by MarkPerry on 3/07/2014.
 */
@FunctionalInterface
public interface SafeIO<A> extends IO<A> {

	@Override
	A run();

	@Override
	default A f() {
		return run();
	}

}

