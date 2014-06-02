package fj;

/**
 * Created by MarkPerry on 2/06/2014.
 */
public interface Try3<A, B, C, D> {

    D f(A a, B b, C c) throws Exception;

}
