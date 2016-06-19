package fj.data.properties;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.PriorityQueue;
import fj.data.Set;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.data.Option.some;
import static fj.data.PriorityQueue.emptyInt;
import static fj.test.Arbitrary.arbAlphaNumString;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbP2;
import static fj.test.Arbitrary.arbSet;
import static fj.test.Property.impliesBoolean;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 18 Jun 16.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 100)
public class PriorityQueueProperties {

    public static Gen<PriorityQueue<Integer, String>> arbPriorityQueueIntegerString = arbQueue(arbAlphaNumString);

    /**
     * Returns a queue with unique integer priorities.
     */
    public static <A> Gen<PriorityQueue<Integer, A>> arbUniqueQueue(Gen<A> aa) {
        Gen<Set<Integer>> as = arbSet(Ord.intOrd, arbInteger);
        Gen<List<Integer>> ints = (as.map(si -> si.toList()));
        Gen<List<P2<Integer, A>>> alp = (
                ints.bind(li -> aa.map(s -> li.map(i -> P.p(i, s))))
        );
        return (alp.map(l -> PriorityQueue.<A>emptyInt().enqueue(l)));
    }

    public static <A> Gen<PriorityQueue<Integer, A>> arbQueue(Gen<A> aa) {
        Gen<List<P2<Integer, A>>> g = arbList(arbP2(arbInteger, aa));
        return g.map(l -> PriorityQueue.<A>emptyInt().enqueue(l));
    }

    Property empty() {
        PriorityQueue<Integer, Object> pq = emptyInt();
        return prop(pq.isEmpty());
    }

    /**
     * Adding a priority that is at the top and then removing it returns the original top.
     */
    Property addRemove() {
        return property(arbPriorityQueueIntegerString, arbInteger, arbAlphaNumString, (q, i, s) -> {
            Option<P2<Integer, String>> t1 = q.top();
            Option<P2<Integer, String>> t2 = q.enqueue(i, s).dequeue().top();
            return prop(q.isLessThan(Ord.intOrd, i) ?
                    t1.equals(t2) : t2.map(p -> p._1() >= i).orSome(true)
            );
        });
    }

    /**
     * An empty queue has no top.
     */
    Property emptyTop() {
        return prop(emptyInt().top().isNone());
    }

    /**
     * Adding a value with the highest priority makes it the top item.
     */
    Property addTop() {
        return property(arbPriorityQueueIntegerString, arbInteger, arbAlphaNumString, (q, i, s) -> {
            Option<P2<Integer, String>> actual = q.enqueue(i, s).top();
            return prop(q.isLessThan(Ord.intOrd, i) ? actual.equals(some(P.p(i, s))) : actual.equals(q.top()));
        });
    }

    /**
     * Sorting a list returns the same as putting the list into a priority queue and getting the queue as a list.
     */
    public Property sorted() {
        return property(arbPriorityQueueIntegerString, pq -> {
            List<P2<Integer, String>> expected = pq.toList().sort(Ord.p2Ord1(Ord.intOrd.reverse()));
            List<P2<Integer, String>> actual = pq.toList();
            return prop(actual.equals(expected));
        });
    }

    /**
     * Where the top n of the queue has just one element then:
     * - Enqueueing and then topN of the queue should return a list of the top and the new item
     * - Enqueuing and then dequeueing and then topping the queue should return the new item
     */
    Property singleTopSame() {
        return property(arbPriorityQueueIntegerString, arbAlphaNumString, (pq, s) -> {
            Option<Integer> o1 = pq.top().map(p -> p._1());
            return o1.map(j -> {
                boolean b = pq.topN().length() == 1;
                Property p1 = impliesBoolean(b, () -> pq.enqueue(j, s).dequeue().top().equals(some(P.p(j, s))));
                Property p2 = impliesBoolean(b, () -> pq.enqueue(j, s).topN().equals(List.list(pq.top().some(), P.p(j, s))));
                return p1.and(p2);
            }).orSome(prop(true));
        });
    }

}
