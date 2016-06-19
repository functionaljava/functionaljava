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

import static fj.data.List.list;

/**
 * A priority queue implementation backed by a
 * {@link fj.data.fingertrees.FingerTree}.  The finger tree nodes are
 * annotated with type K, are combined using a monoid of K and both the
 * key and value are stored in the leaf.  Priorities of the same value
 * are returned FIFO (first in, first out).
 *
 * Created by MarkPerry on 31 May 16.
 */
public final class PriorityQueue<K, A> {

    private final FingerTree<K, P2<K, A>> ftree;
    private final Equal<K> equal;

    private PriorityQueue(Equal<K> e, FingerTree<K, P2<K, A>> ft) {
        equal = e;
        ftree = ft;
    }

    /**
     * Creates a priority queue from a finger tree.
     */
    public static <K, A> PriorityQueue<K, A> priorityQueue(Equal<K> e, FingerTree<K, P2<K, A>> ft) {
        return new PriorityQueue<>(e, ft);
    }

    /**
     * Creates an empty priority queue.
     *
     * @param m A monoid to combine node annotations.
     * @param e A value to compare key equality.
     */
    public static <K, A> PriorityQueue<K, A> empty(Monoid<K> m, Equal<K> e) {
        return priorityQueue(e, FingerTree.empty(m, P2.__1()));
    }

    /**
     * An empty priority queue with integer priorities.
     */
    public static <A> PriorityQueue<Integer, A> emptyInt() {
        return empty(Monoid.intMaxMonoid, Equal.intEqual);
    }

    /**
     * Maps the values in each node with function f.
     */
    public <B> PriorityQueue<K, B> map(F<A, B> f) {
        return priorityQueue(equal,
                ftree.map(P2.map2_(f),
                        FingerTree.measured(ftree.measured().monoid(), P2.__1())
                )
        );
    }

    /**
     * Filters nodes based on the value inside each node.
     */
    public PriorityQueue<K, A> filterValues(F<A, Boolean> f) {
        return priorityQueue(equal, ftree.filter(p2 -> f.f(p2._2())));
    }

    /**
     * Filters the nodes based on the annotation of each node.
     */
    public PriorityQueue<K, A> filterKeys(F<K, Boolean> f) {
        return priorityQueue(equal, ftree.filter(p2 -> f.f(p2._1())));
    }

    /**
     * Is the tree empty?
     */
    public boolean isEmpty() {
        return ftree.isEmpty();
    }

    /**
     * If the tree is not empty, returns the node with highest priority otherwise returns nothing.
     */
    public Option<P2<K, A>> top() {
        K top = ftree.measure();
        P2<FingerTree<K, P2<K, A>>, FingerTree<K, P2<K, A>>> p = ftree.split(k -> equal.eq(top, k));
        return p._2().headOption();
    }

    /**
     * Returns all the elements of the queue with the highest (same) priority.
     */
    public List<P2<K, A>> topN() {
        Stream<P2<K, A>> s = toStream();
        if (s.isEmpty()) {
            return List.nil();
        } else {
            final K k = s.head()._1();
            return s.takeWhile(p -> equal.eq(k, p._1())).toList();
        }
    }

    /**
     * Adds a node with priority k and value a.  This operation take O(1).
     */
    public PriorityQueue<K, A> enqueue(K k, A a) {
        return priorityQueue(equal, ftree.snoc(P.p(k, a)));
    }

    /**
     * Adds nodes using the list of products with priority k and value a.  This operation takes O(list.length()).
     */
    public PriorityQueue<K, A> enqueue(List<P2<K, A>> list) {
        return list.foldLeft(pq -> p -> pq.enqueue(p._1(), p._2()), this);
    }

    /**
     * Does the priority k exist already?
     */
    public boolean contains(final K k) {
        return !ftree.split(k2 -> equal.eq(k, k2))._2().isEmpty();
    }

    /**
     * Adds nodes using the iterable of products with priority k and value a.
     */
    public PriorityQueue<K, A> enqueue(Iterable<P2<K, A>> it) {
        PriorityQueue<K, A> result = this;
        for (P2<K, A> p: it) {
            result = result.enqueue(p);
        }
        return result;
    }

    /**
     * Adds a node with priority k and value a.  This operation take O(1).
     */
    public PriorityQueue<K, A> enqueue(P2<K, A> p) {
        return enqueue(p._1(), p._2());
    }

    /**
     * Removes the node with the highest priority.
     */
    public PriorityQueue<K, A> dequeue() {
        K top = ftree.measure();
        P2<FingerTree<K, P2<K, A>>, FingerTree<K, P2<K, A>>> p = ftree.split(k -> equal.eq(k, top));
        FingerTree<K, P2<K, A>> right = p._2();
        return right.isEmpty() ? this : priorityQueue(equal, p._1().append(right.tail()));
    }

    /**
     * Returns a tuple of the node with the highest priority and the rest of the priority queue.
     */
    public P2<Option<P2<K, A>>, PriorityQueue<K, A>> topDequeue() {
        return P.p(top(), dequeue());
    }

    /**
     * Removes the top n elements with the highest priority.
     */
    public PriorityQueue<K, A> dequeue(int n) {
        int i = n;
        PriorityQueue<K, A> result = this;
        while (i > 0) {
            i--;
            result = result.dequeue();
        }
        return result;
    }

    /**
     * Does the top of the queue have lower priority than k?
     */
    public boolean isLessThan(Ord<K> ok, K k) {
        return top().map(p -> ok.isLessThan(p._1(), k)).orSome(true);
    }

    public boolean isGreaterThan(Ord<K> ok, K k) {
        return top().map(p -> ok.isGreaterThan(p._1(), k)).orSome(false);
    }

    public boolean isEqual(Ord<K> ok, K k) {
        return top().map(p -> ok.eq(p._1(), k)).orSome(false);
    }

    /**
     * Returns a stream of products with priority k and value a.
     */
    public Stream<P2<K, A>> toStream() {
        return top().map(p -> Stream.cons(p, () -> dequeue().toStream())).orSome(() -> Stream.nil());
    }

    /**
     * Returns a list of products with priority k and value a.
     */
    public List<P2<K, A>> toList() {
        return toStream().toList();
    }

    public String toString() {
        return Show.priorityQueueShow(Show.<K>anyShow(), Show.<A>anyShow()).showS(this);
    }

}
