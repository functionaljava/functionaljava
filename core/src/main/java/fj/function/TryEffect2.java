package fj.function;

/**
 * Created by mperry on 28/08/2014.
 */
public interface TryEffect2<A, B, Z extends Exception> {

	void f(A a, B b) throws Z;

}
