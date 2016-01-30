package fj.data;

import fj.F;
import fj.P;
import fj.control.Trampoline;

/**
 * Difference List. It converts left associative appends into right associative ones to improve performance.
 *
 * @version %build.number%
 */
public class DList<A> {
    private final F<List<A>,Trampoline<List<A>>> appendFn;
    
    private DList(F<List<A>,Trampoline<List<A>>> appendFn) {
        this.appendFn = appendFn;
    }
    
    public static <A> DList<A> fromList(List<A> a) {
        return new DList<>((List<A> tail) -> Trampoline.pure(a.append(tail)));
    }
    
    public List<A> run() {
        return appendFn.f(List.<A>nil()).run();
    }
    
    public static <A> DList<A> nil() {
        return new DList<>(Trampoline.<List<A>>pure());
    }
    
    public static <A> DList<A> single(A a) {
        return new DList<>((List<A> tail) -> Trampoline.pure(tail.cons(a)));
    }
    
    public DList<A> cons(A a) {
        return DList.single(a).append(this);
    }
    
    public DList<A> snoc(A a) {
        return this.append(DList.single(a));
    }
    
    public DList<A> append(DList<A> other) {
        return new DList<>(kleisliTrampCompose(this.appendFn, other.appendFn));
    }
    
    private static <A,B,C> F<A,Trampoline<C>> kleisliTrampCompose(F<B,Trampoline<C>> bc, F<A,Trampoline<B>> ab) {
        return (A a) -> ab.f(a).bind((B b) -> Trampoline.suspend(P.lazy(() -> bc.f(b))));
    }
}
