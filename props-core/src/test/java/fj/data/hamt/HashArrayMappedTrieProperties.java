package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.Equal.intEqual;
import static fj.Equal.optionEqual;
import static fj.Ord.intOrd;
import static fj.Ord.p2Ord;
import static fj.Ord.p2Ord2;
import static fj.data.Option.some;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbListInteger;
import static fj.test.Arbitrary.arbP2;
import static fj.test.Arbitrary.arbSet;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * @author Mark Perry
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 100)
public class HashArrayMappedTrieProperties {

    private static final HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.emptyKeyInteger();
    private static final Gen<List<P2<Integer, Integer>>> arbListProducts = arbSet(intOrd, arbInteger).bind(s -> Gen.listOf(arbInteger, s.size()).map(list -> s.toList().zip(list)));
    private static final Gen<HashArrayMappedTrie<Integer, Integer>> arbHamt = arbListProducts.map(l -> empty.set(l));

    Property empty() {
        return prop(empty.isEmpty());
    }

    Property setFromList() {
        return property(arbListProducts, list -> {
            List<P2<Integer, Integer>> actual = empty.set(list).toList(intOrd);
            List<P2<Integer, Integer>> expected = list.sort(p2Ord(intOrd, intOrd));
            boolean b = actual.equals(expected);
            return prop(b);
        });
    }

    Property overwriteKey() {
        return property(arbHamt, arbInteger, arbInteger, arbInteger, (h, k, v1, v2) -> {
            Option<Integer> actual = h.set(k, v1).set(k, v2).find(k);
            return prop(optionEqual(intEqual).eq(actual, some(v2)));
        });
    }

    Property allIn() {
        return property(arbListProducts, list -> {
            HashArrayMappedTrie<Integer, Integer> h = empty.set(list);
            Boolean b = list.foldLeft((acc, p) -> h.find(p._1()).option(false, i -> true && acc), true);
            return prop(b);
        });
    }

    Property sampleInts() {
        return property(arbListProducts, arbInteger, (ps, i) -> {
            HashArrayMappedTrie<Integer, Integer> h = empty.set(ps);
            Option<Integer> o1 = ps.find(p -> intEqual.eq(p._1(), i)).map(p -> p._2());
            Option<Integer> o2 = h.find(i);
            return prop(optionEqual(intEqual).eq(o1, o2));
        });
    }

    Property fold() {
        return property(arbListProducts, list -> {
            Integer actual = empty.set(list).foldLeft((acc, p) -> acc + p._2(), 0);
            Integer expected = list.foldLeft((acc, p) -> acc + p._2(), 0);
            return prop(intEqual.eq(actual, expected));
        });
    }

    Property length() {
        return property(arbListProducts, list -> {
            Integer actual = empty.set(list).length();
            Integer expected = list.length();
            return prop(intEqual.eq(actual, expected));
        });
    }

}
