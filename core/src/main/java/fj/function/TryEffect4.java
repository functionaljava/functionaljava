package fj.function;

/**
 * Created by mperry on 28/08/2014.
 */
public interface TryEffect4<A, B, C, D, Z extends Exception> {

	void f(A a, B b, C c, D d) throws Z;

}
