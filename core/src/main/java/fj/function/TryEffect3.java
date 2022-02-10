package fj.function;

public interface TryEffect3 <A, B, C, Z extends Exception> {

	void f(A a, B b, C c) throws Z;

}
