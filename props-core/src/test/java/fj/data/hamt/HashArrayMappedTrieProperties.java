package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Set;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.Ord.intOrd;
import static fj.Ord.p2Ord;
import static fj.Ord.p2Ord2;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbP2;
import static fj.test.Arbitrary.arbSet;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by MarkPerry on 25 Jun 16.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 100)
public class HashArrayMappedTrieProperties {

    private static final HashArrayMappedTrie<Integer, Integer> empty = HashArrayMappedTrie.empty(Equal.intEqual, Hash.intHash);
//    private static final Gen<HashArrayMappedTrie<Integer, Integer>> arbHamt = arbHamt(Equal.intEqual, Hash.intHash, arbInteger, arbInteger);
    private static final Ord<P2<Integer, Integer>> hamtOrd = p2Ord2(intOrd);
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



}
