package fj.data.properties;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.PriorityQueue;
import fj.data.Set;
import fj.test.Arbitrary;
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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 18 Jun 16.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 100)
public class PriorityQueueProperties {

    public static Gen<PriorityQueue<Integer, String>> arbPriorityQueueIntegerString = arbUniqueQueue(arbAlphaNumString);

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

    Property addRemove() {
        return property(arbPriorityQueueIntegerString, arbInteger, arbAlphaNumString, (q, i, s) -> {
            Option<P2<Integer, String>> o = q.top();
            Option<P2<Integer, String>> o2 = q.enqueue(i, s).dequeue().top();
            return Property.impliesBoolean(
                    q.isGreaterThan(Ord.intOrd, i),
//                    o.map(p -> i > p._1()).orSome(true),
                    () -> o.equals(o2)
            );
        });
    }

    Property emptyTop() {
        return prop(emptyInt().top().isNone());
    }

    Property addTop() {
        return property(arbPriorityQueueIntegerString, arbInteger, arbAlphaNumString, (q, i, s) -> {
            Option<P2<Integer, String>> actual = q.enqueue(i, s).top();
            return impliesBoolean(
                    q.isGreaterThan(Ord.intOrd, i),
//                    q.top().map(p -> p._1() < i).orSome(true),
                    actual.equals(some(P.p(i, s))));
        });
    }

    Property sorted() {
        Gen<Set<Integer>> as = arbSet(Ord.intOrd, arbInteger);
        Gen<List<Integer>> ints = (as.map(si -> si.toList()));
        Gen<List<P2<Integer, String>>> alp = (
            ints.bind(li -> arbAlphaNumString.map(s -> li.map(i -> P.p(i, s))))
        );
        return property(alp, list -> {
            PriorityQueue<Integer, String> q = PriorityQueue.<String>emptyInt().enqueue(list);
            List<P2<Integer, String>> expected = list.sort(Ord.p2Ord1(Ord.intOrd.reverse()));
            List<P2<Integer, String>> actual = q.toStream().toList();
            assertThat(actual, equalTo(expected));
            System.out.println(actual);
            return prop(actual.equals(expected));
        });
    }

    public Property sorted2() {
        return property(arbPriorityQueueIntegerString, pq -> {
            List<P2<Integer, String>> expected = pq.toList().sort(Ord.p2Ord1(Ord.intOrd.reverse()));
            return prop(expected.equals(pq.toList()));
        });
    }

}
