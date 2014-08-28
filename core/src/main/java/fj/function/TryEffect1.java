package fj.function;

/**
 * Created by mperry on 28/08/2014.
 */
public interface TryEffect1<A, Z extends Exception> {

	void f(A a) throws Z;

}
