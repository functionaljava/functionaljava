package fj.data.hamt;

import fj.Equal;
import fj.Ord;
import fj.P;
import fj.P3;
import fj.data.List;
import fj.data.Seq;
import fj.data.test.PropertyAssert;
import fj.function.Booleans;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static fj.Equal.bitSetSequal;
import static fj.Equal.booleanEqual;
import static fj.Equal.listEqual;
import static fj.Equal.stringEqual;
import static fj.Function.identity;
import static fj.data.hamt.BitSet.MAX_BIT_SIZE;
import static fj.data.hamt.BitSet.listBitSet;
import static fj.data.hamt.BitSet.longBitSet;
import static fj.test.Arbitrary.arbBoolean;
import static fj.test.Arbitrary.arbLong;
import static fj.test.Property.impliesBoolean;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by maperr on 31/05/2016.
 */

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class BitSetProperties {

    Property andTest() {
        return property(arbLong, arbLong, (a, b) -> prop(longBitSet(a).and(longBitSet(b)).longValue() == (a & b)));
    }

    Property asStringTest() {
        return property(arbLong, a -> prop(longBitSet(a).asString().equals(Long.toBinaryString(a))));
    }

    Property bitsToRightTest() {
        return property(arbLong, arbBitSetSize, (a, i) ->
            prop(
                longBitSet(a).bitsToRight(i) ==
                longBitSet(a).toList().reverse().take(i).filter(identity()).length()
        ));
    }

    Property longRoundTripTest() {
        return property(arbNaturalLong, l -> prop(longBitSet(l).longValue() == l));
    }

    Property empty() {
        return prop(BitSet.empty().isEmpty() && BitSet.empty().longValue() == 0);
    }

    Property generalEmptinessTest() {
        return property(arbListBoolean, list ->
                prop(list.dropWhile(Booleans.not).isEmpty() == listBitSet(list).isEmpty())
        );
    }

    Property foldLeftTest() {
        return property(arbLong, l -> prop(
            BitSet.longBitSet(l).toList().dropWhile(b -> !b).foldLeft(
                    (acc, b) -> acc + 1, 0
            ) == BitSet.longBitSet(l).bitsUsed()
        ));
    }

    Property fromListTest() {
        return property(arbListBoolean, l -> prop(listBitSet(l).toList().equals(l.dropWhile(b -> !b))));
    }

    Property fromStreamTest() {
        return property(arbListBoolean, l -> prop(listBitSet(l).toStream().toList().equals(l.dropWhile(b -> !b))));
    }

    Property fromLongTest() {
        return property(arbLong, l -> prop(BitSet.longBitSet(l).longValue() == l));
    }

    Property fromStringTest() {
        Gen<String> g = arbListBoolean.map(l -> l.map(b -> Integer.toString(BitSet.toInt(b))).foldLeft((acc, s) -> acc + s, ""));
        return property(g, (s) -> {
            boolean zeroLength = s.isEmpty();
            return Property.implies(!zeroLength, () -> {
                long x = new BigInteger(s, 2).longValue();
                long y = BitSet.stringBitSet(s).longValue();
                return prop(x == y);
            });
        });
    }

    Gen<List<Boolean>> arbListBoolean = Gen.choose(0, MAX_BIT_SIZE).bind(i -> Gen.sequenceN(i, arbBoolean));

    Property toListTest() {
        return property(arbListBoolean, list -> {
            List<Boolean> expected = list.dropWhile(Booleans.not);
            List<Boolean> actual = listBitSet(list).toList();
            return prop(Equal.listEqual(Equal.booleanEqual).eq(expected, actual));
        });
    }

    Property clearTest() {
        return property(arbLong, arbBitSetSize, (l, i) ->
                prop(BitSet.longBitSet(l).clear(i).isSet(i) == false)
        );
    }

    Property bitsUsedTest() {
        return property(arbListBoolean, list -> prop(
                    list.dropWhile(b -> !b).length() ==
                    listBitSet(list).bitsUsed()
        ));
    }

    Property isSetTest() {
        return property(arbNaturalLong, Gen.choose(0, MAX_BIT_SIZE),
                (Long l, Integer i) -> prop(longBitSet(l).isSet(i) == ((l & (1L << i)) != 0))
        );
    }

    Property stringBitSet() {
        return property(arbNaturalLong, l ->
            prop(BitSet.stringBitSet(BitSet.longBitSet(l).asString()).longValue() == l)
        );
    }

    Property notTest() {
        return property(arbLong, l -> prop(longBitSet(l).not().longValue() == ~l));
    }

    Property orTest() {
        return property(arbLong, arbLong, (x, y) -> prop(
                longBitSet(x).or(longBitSet(y)).longValue() == (x | y)
        ));
    }

    Gen<List<Integer>> bitSetIndices(int n) {
        return Gen.listOfSorted(Gen.choose(0, MAX_BIT_SIZE - 1), n, Ord.intOrd);
    }

    Property rangeTest() {
        return property(arbNaturalLong, bitSetIndices(4), (x, list) -> {
            int l = list.index(0);
            int h = list.index(2);
            int m = Math.max(l, Math.min(list.index(1), h - 1));
            int vh = list.index(3);

            BitSet bs1 = longBitSet(x);
            BitSet bs2 = bs1.range(l, h);
            boolean b =
                bs1.isSet(m) == bs2.isSet(m - l) &&
                bs2.isSet(vh - l) == false;
            return prop(b);
        });
    }

    Property setTest() {
        return property(arbNaturalLong, arbBitSetSize, (l, i) -> prop(longBitSet(l).set(i).isSet(i)));
    }

    Property setBooleanTest() {
        return property(arbNaturalLong, arbBitSetSize, arbBoolean, (l, i, b) -> prop(longBitSet(l).set(i, b).isSet(i) == b));
    }

    Property shiftLeftTest() {
        return property(arbNaturalLong, arbBitSetSize, (l, i) -> {
            BitSet bs = longBitSet(l);
            boolean b = bs.shiftLeft(i).longValue() == (l << i);
            return impliesBoolean(bs.bitsUsed() + i < MAX_BIT_SIZE, b);
        });
    }

    Property shiftRightTest() {
        return property(arbNaturalLong, arbBitSetSize, (l, i) -> {
            return prop(longBitSet(l).shiftRight(i).longValue() == (l >> i));
        });
    }

    Property takeLowerTest() {
        return property(arbNaturalLong, arbBitSetSize, (l, i) -> {
            return prop(bitSetSequal.eq(longBitSet(l).takeLower(i), longBitSet(l).range(0, i)));
        });
    }

    Property takeUpperTest() {
        return property(arbNaturalLong, arbBitSetSize, (l, i) -> {
            return prop(bitSetSequal.eq(longBitSet(l).takeUpper(i), longBitSet(l).range(MAX_BIT_SIZE, MAX_BIT_SIZE - i)));
        });
    }

    Property toStreamTest() {
        return property(arbNaturalLong, l -> {
            return prop(listEqual(booleanEqual).eq(longBitSet(l).toList(), longBitSet(l).toStream().toList()));
        });
    }

    Property xorTest() {
        return property(arbNaturalLong, arbNaturalLong, (a, b) -> {
            return prop(longBitSet(a).xor(longBitSet(b)).longValue() == (a ^ b));
        });
    }
    static final Gen<Long> arbNaturalLong = Gen.choose(0, Long.MAX_VALUE);

    static final Gen<Integer> arbBitSetSize = Gen.choose(0, MAX_BIT_SIZE - 1);

}
