package fj.function;

public interface TryEffect1<A, Z extends Exception> {

	void f(A a) throws Z;

}
