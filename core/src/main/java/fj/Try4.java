package fj;

/**
 * Created by MarkPerry on 2/06/2014.
 */
public interface Try4<A, B, C, D, E> {

    E f(A a, B b, C c, D d) throws Exception;

}
