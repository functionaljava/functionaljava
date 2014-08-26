package fj.data;

import java.io.IOException;

/**
 * Created by MarkPerry on 3/07/2014.
 */
public interface SafeIO<A> extends IO<A> {

	@Override
	public A run();

}

