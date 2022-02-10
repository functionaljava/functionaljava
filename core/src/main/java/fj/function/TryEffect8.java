package fj.function;

public interface TryEffect8<A, B, C, D, E, F, G, H, Z extends Exception> {

	void f(A a, B b, C c, D d, E e, F f, G g, H h) throws Z;

}
