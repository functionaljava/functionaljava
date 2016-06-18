package fj.data;

import fj.Equal;
import fj.F;
import fj.Monoid;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.P3;
import fj.Show;
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

    public PriorityQueue<K, A> filterValues(F<A, Boolean> f) {
        return priorityQueue(equal, ftree.filter(p2 -> f.f(p2._2())));
    }

    public PriorityQueue<K, A> filterKeys(F<K, Boolean> f) {
        return priorityQueue(equal, ftree.filter(p2 -> f.f(p2._1())));
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
        return priorityQueue(equal, ftree.snoc(P.p(k, a)));
    }

    public PriorityQueue<K, A> enqueue(List<P2<K, A>> list) {
        return list.foldLeft(pq -> p -> pq.enqueue(p._1(), p._2()), this);
    }

    public boolean contains(final K k1) {
        return !ftree.split(k2 -> equal.eq(k1, k2))._2().isEmpty();
    }

    public PriorityQueue<K, A> enqueue(Iterable<P2<K, A>> it) {
        PriorityQueue<K, A> result = this;
        for (P2<K, A> p: it) {
            result = result.enqueue(p);
        }
        return result;
    }

    public PriorityQueue<K, A> enqueue(P2<K, A> p) {
        return enqueue(p._1(), p._2());
    }

    public PriorityQueue<K, A> dequeue() {
        K top = ftree.measure();
        P2<FingerTree<K, P2<K, A>>, FingerTree<K, P2<K, A>>> p = ftree.split(k -> equal.eq(k, top));
        FingerTree<K, P2<K, A>> right = p._2();
        return right.isEmpty() ? this : priorityQueue(equal, p._1().append(right.tail()));
    }

    public P2<Option<P2<K, A>>, PriorityQueue<K, A>> dequeueTop() {
        return P.p(top(), dequeue());
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

    public boolean isGreaterThan(Ord<K> ok, K k) {
        return top().map(p -> ok.isGreaterThan(k, p._1())).orSome(true);
    }

    public Stream<P2<K, A>> toStream() {
        return top().map(p -> Stream.cons(p, () -> dequeue().toStream())).orSome(() -> Stream.nil());
    }

    public List<P2<K, A>> toList() {
        return toStream().toList();
    }

    public String toString() {
        return Show.priorityQueueShow(Show.<K>anyShow(), Show.<A>anyShow()).showS(this);
    }

}
