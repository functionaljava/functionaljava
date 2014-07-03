package fj.data;

import java.io.IOException;

/**
 * Created by MarkPerry on 3/07/2014.
 */
public interface SafeIO<E extends IOException, A> extends IO<Validation<E, A>> {

	@Override
	public Validation<E, A> run();

}

