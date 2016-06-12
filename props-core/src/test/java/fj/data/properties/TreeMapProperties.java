package fj.data.properties;

import fj.Equal;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Stream;
import fj.data.TreeMap;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static fj.Equal.intEqual;
import static fj.Equal.p2Equal;
import static fj.Equal.stringEqual;
import static fj.Ord.intOrd;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbString;
import static fj.test.Arbitrary.arbTreeMap;
import static fj.test.Property.*;

/**
 * Created by MarkPerry on 29/08/2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class TreeMapProperties {

    private static final int smallMax = 5;
    private static final int midMax = 20;
    public static final Gen<TreeMap<Integer, String>> smallArbTreeMapIS = arbTreeMap(intOrd, arbInteger, arbString, smallMax);
    public static final Gen<TreeMap<Integer, String>> arbTreeMapIS = arbTreeMap(intOrd, arbInteger, arbString, midMax);
    public static final Ord<P2<Integer, String>> p2Ord = Ord.p2Ord1(Ord.intOrd);
    Equal<List<P2<Integer, String>>> listEq = Equal.listEqual(p2Equal(intEqual, stringEqual));


    public Property empty() {
        return property(smallArbTreeMapIS, tm -> impliesBoolean(tm.isEmpty(), tm.size() == 0));
    }

    public Property set() {
        return property(arbTreeMapIS, arbInteger, arbString, (tm, k, v) ->
            prop(stringEqual.eq(tm.set(k, v).get(k).some(), v))
        );
    }

    public Property updateId() {
        return property(arbTreeMapIS, arbInteger, arbString, (tm, k, v) ->
            prop(stringEqual.eq(tm.set(k, v).update(k, x -> x)._2().get(k).some(), v))
        );
    }

    public Property update() {
        return property(arbTreeMapIS, arbInteger, arbString, arbString, (tm, k, v, v2) ->
            prop(stringEqual.eq(tm.set(k, v).update(k, x -> x + v2)._2().get(k).some(), v + v2))
        );
    }

    public Property minKey() {
        return property(arbTreeMapIS, tm ->
            impliesBoolean(!tm.isEmpty(), () -> Equal.intEqual.eq(tm.minKey().some(), tm.toStream().head()._1()))
        );
    }

    public Property maxKey() {
        return property(arbTreeMapIS, tm ->
            impliesBoolean(!tm.isEmpty(),
                () -> Equal.intEqual.eq(tm.maxKey().some(), tm.toStream().reverse().head()._1())
            )
        );
    }

    Property listSize() {
        return property(arbTreeMapIS, tm -> prop(tm.size() == tm.toList().length()));
    }

    Property listSorted() {
        return property(arbTreeMapIS, tm -> prop(listEq.eq(tm.toList(), tm.toList().sort(p2Ord))));
    }

    Property listStreamEq() {
        return property(arbTreeMapIS, tm -> prop(listEq.eq(tm.toList(), tm.toStream().toList())));
    }

}
