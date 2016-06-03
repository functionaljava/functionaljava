package fj.data;

import fj.Equal;
import fj.F;
import fj.Monoid;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.fingertrees.FingerTree;

/**
 * Created by MarkPerry on 31 May 16.
 */
public class PriorityQueue<K, A> {

    private final FingerTree<K, P2<K, A>> ftree;
    private final Equal<K> equal;

    private PriorityQueue(Equal<K> e, FingerTree<K, P2<K, A>> ft) {
        equal = e;
        ftree = ft;
    }

    public static <K, A> PriorityQueue<K, A> priorityQueue(Equal<K> e, FingerTree<K, P2<K, A>> ft) {
        return new PriorityQueue<K, A>(e, ft);
    }

    public static <K, A> PriorityQueue<K, A> empty(Monoid<K> m, Equal<K> e) {
        return priorityQueue(e, FingerTree.empty(m, (P2<K, A> p) -> p._1()));
    }

    public static <A> PriorityQueue<Integer, A> emptyInt() {
        return priorityQueue(Equal.intEqual, FingerTree.empty(Monoid.intMaxMonoid, (P2<Integer, A> p) -> p._1()));
    }

    public <B> PriorityQueue<K, B> map(F<A, B> f) {
        return priorityQueue(equal,
                ftree.map(p2 -> p2.map2(a -> f.f(a)),
                FingerTree.measured(ftree.measured().monoid(), (P2<K, B> p2) -> p2._1()))
        );
    }

    public PriorityQueue<K, A> filter(F<A, Boolean> f) {
        return priorityQueue(equal, ftree.filter(p2 -> f.f(p2._2())));
    }

    public boolean isEmpty() {
        return ftree.isEmpty();
    }

    public Option<P2<K, A>> top() {
        K top = ftree.measure();
        P2<FingerTree<K, P2<K, A>>, FingerTree<K, P2<K, A>>> p = ftree.split(k -> equal.eq(top, k));
        return p._2().headOption();
    }

    public PriorityQueue<K, A> enqueue(K k, A a) {
        return priorityQueue(equal, ftree.cons(P.p(k, a)));
    }

    public PriorityQueue<K, A> enqueue(List<P2<K, A>> list) {
        return list.foldLeft(pq -> p -> pq.enqueue(p._1(), p._2()), this);
    }

    public PriorityQueue<K, A> dequeue() {
        K top = ftree.measure();
        P3<FingerTree<K, P2<K, A>>, P2<K, A>, FingerTree<K, P2<K, A>>> p = ftree.split1(k -> equal.eq(k, top));
        return priorityQueue(equal, p._1().append(p._3()));
    }

    public PriorityQueue<K, A> dequeue(int n) {
        int i = n;
        PriorityQueue<K, A> result = this;
        while (i > 0) {
            i--;
            result = result.dequeue();
        }
        return result;
    }

}
