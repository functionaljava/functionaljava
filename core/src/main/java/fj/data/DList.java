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
    
    /**
     * Wraps a list with a DList.
     * @param <A>
     * @param a the regular List
     * @return the DList
     */
    public static <A> DList<A> fromList(List<A> a) {
        return new DList<>((List<A> tail) -> Trampoline.pure(a.append(tail)));
    }
    
    /**
     * Concatenates all the internal Lists together that are held in
     * the DList's lambda's state to produce a List.
     * This is what converts the appending operation from left associative to right associative,
     * giving DList it's speed.
     * @return the final List
     */
    public List<A> run() {
        return appendFn.f(List.<A>nil()).run();
    }
    
    /**
     * A empty DList.
     * @param <A>
     * @return a empty DList.
     */
    public static <A> DList<A> nil() {
        return new DList<>(Trampoline.<List<A>>pure());
    }
    
    /**
     * Produces a DList with one element.
     * @param <A>
     * @param a the element in the DList.
     * @return a DList with one element.
     */
    public static <A> DList<A> single(A a) {
        return new DList<>((List<A> tail) -> Trampoline.pure(tail.cons(a)));
    }
    
    /**
     * Prepends a single element on the DList to produce a new DList.
     * @param a the element to append.
     * @return the new DList.
     */
    public DList<A> cons(A a) {
        return DList.single(a).append(this);
    }
    
    /**
     * Appends a single element on the end of the DList to produce a new DList.
     * @param a the element to append.
     * @return the new DList.
     */
    public DList<A> snoc(A a) {
        return this.append(DList.single(a));
    }
    
    /**
     * Appends two DLists together to produce a new DList.
     * @param other the other DList to append on the end of this one.
     * @return the new DList.
     */
    public DList<A> append(DList<A> other) {
        return new DList<>(kleisliTrampCompose(this.appendFn, other.appendFn));
    }
    
    private static <A,B,C> F<A,Trampoline<C>> kleisliTrampCompose(F<B,Trampoline<C>> bc, F<A,Trampoline<B>> ab) {
        return (A a) -> ab.f(a).bind((B b) -> Trampoline.suspend(P.lazy(() -> bc.f(b))));
    }
}
