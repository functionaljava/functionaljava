package fj;

/**
 * Created by MarkPerry on 2/06/2014.
 */
public interface Try2<A, B, C> {

    C f(A a, B b) throws Exception;

}
