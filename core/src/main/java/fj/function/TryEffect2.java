package fj.function;

public interface TryEffect2<A, B, Z extends Exception> {

	void f(A a, B b) throws Z;

}
