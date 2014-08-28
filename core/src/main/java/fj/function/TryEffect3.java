package fj.function;

/**
 * Created by mperry on 28/08/2014.
 */
public interface TryEffect3 <A, B, C, Z extends Exception> {

	void f(A a, B b, C c) throws Z;

}
