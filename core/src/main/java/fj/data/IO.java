package fj.data;

/**
 * Created by MarkPerry on 19/06/2014.
 */

import java.io.IOException;

/**
 * IO monad for processing files
 *
 * @author Martin Grotzke
 *
 * @param <A> the type of the result produced by the IO
 */
public interface IO<A> {

	public A run() throws IOException;

}
