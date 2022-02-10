package fj.function;

public interface TryEffect7<A, B, C, D, E, F, G, Z extends Exception> {

	void f(A a, B b, C c, D d, E e, F f, G g) throws Z;

}
