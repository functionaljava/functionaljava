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
import static fj.test.Arbitrary.arbSet;
import static fj.test.Property.impliesBoolean;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 18 Jun 16.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 100)
public class PriorityQueueProperties {

    public static Gen<PriorityQueue<Integer, String>> arbPriorityQueueIntegerString = arbUniqueQueue(arbAlphaNumString);

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

    Property empty() {
        PriorityQueue<Integer, Object> pq = emptyInt();
        return prop(pq.isEmpty());
    }

    /**
     * Adding a priority that is at the top and then removing it returns the original top.
     */
    Property addRemove() {
        return property(arbPriorityQueueIntegerString, arbInteger, arbAlphaNumString, (q, i, s) -> {
            Option<P2<Integer, String>> o = q.top();
            Option<P2<Integer, String>> o2 = q.enqueue(i, s).dequeue().top();
            return Property.impliesBoolean(
                    q.isGreaterThan(Ord.intOrd, i),
                    () -> o.equals(o2)
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
            return impliesBoolean(
                    q.isGreaterThan(Ord.intOrd, i),
                    actual.equals(some(P.p(i, s))));
        });
    }

    /**
     * Sorting a list returns the same as putting the list into a priority queue and getting
     * the queue as a list.
     */
    public Property sorted() {
        return property(arbPriorityQueueIntegerString, pq -> {
            List<P2<Integer, String>> expected = pq.toList().sort(Ord.p2Ord1(Ord.intOrd.reverse()));
            return prop(expected.equals(pq.toList()));
        });
    }

}
