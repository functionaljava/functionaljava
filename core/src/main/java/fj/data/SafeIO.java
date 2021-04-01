package fj.data;

/**
 * Created by MarkPerry on 3/07/2014.
 */
@FunctionalInterface
public interface SafeIO<A> extends IO<A> {

	@Override
	A run();

}

