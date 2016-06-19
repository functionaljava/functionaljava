package fj.data.hamt;

import fj.Equal;
import fj.data.List;
import fj.function.Booleans;
import org.junit.Test;

import static fj.Equal.booleanEqual;
import static fj.Equal.listEqual;
import static fj.data.List.list;
import static fj.data.hamt.BitSet.longBitSet;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by maperr on 31/05/2016.
 */
public class BitSetTest {

    public static final Equal<List<Boolean>> listBooleanEqual = listEqual(booleanEqual);

    @Test
    public void fromLong() {
        longBitSet(1);
    }

    @Test
    public void bitsToRightOfNegative() {
        long l = -6;
        BitSet bs = longBitSet(l);
        out.println(bs);
        int n = 4;
        int index = bs.bitsToRight(n);
        out.println(index);
        assertThat(index, equalTo(2));

        int j = bs.toList().reverse().take(n).filter(b -> b).length();
        out.println(j);
        assertThat(index, equalTo(j));

        long a = -601295421440L;
        int i = 32;
        boolean b2 = longBitSet(a).bitsToRight(i) == longBitSet(a).toList().reverse().take(i).filter(b -> b).length();
        out.println(b2);

        a = 19250043420672L;
        i = 63;
        int x = longBitSet(a).bitsToRight(i);
        List<Boolean> list = longBitSet(a).toList().reverse();
        int y = list.take(i).filter(b -> b).length();
        boolean b3 = x == y;
        out.println(b3);
    }

    @Test
    public void fromList() {
        List<Boolean> list = list(true, false);
        BitSet bs = BitSet.listBitSet(list);
        assertThat(bs.longValue(), is(2L));
    }

    @Test
    public void fromList2() {
        // test with leading false
        List<Boolean> list = list(false, true, true);
        BitSet bs = BitSet.listBitSet(list);
        assertThat(bs.longValue(), is(3L));
    }

    @Test
    public void toList() {
        assertThat(toList(6), is(list(true, true, false)));
        assertThat(toList(3), is(list(true, true)));
//        assertThat(isMatch(3, list(false, true, true)), is(true));
    }

    public boolean isMatch(long l, List<Boolean> list) {
        return listBooleanEqual.eq(longBitSet(l).toList(), list);
    }

    List<Boolean> toList(long l) {
        return longBitSet(l).toList();
    }


    @Test
    public void reverseStream() {
        List<Boolean> l = longBitSet(6).toStream().reverse().toList();
        assertThat(l, is(list(false, true, true)));
    }

    @Test
    public void strings() {
        long l = 472446402560L;
        long actual = BitSet.stringBitSet(longBitSet(l).asString()).longValue();
        assertThat(actual, is(l));

    }


    @Test
    public void negativeLong() {
        long l = -1760752564951867896L;
        BitSet bs = longBitSet(l);
        List<Boolean> list = bs.toList();
        out.println(list);

    }

    @Test
    public void negativeRoundTrip() {
        long expected = -2;
        BitSet bs1 = longBitSet(expected);
        List<Boolean> list1 = bs1.toList();
        BitSet bs2 = BitSet.listBitSet(list1);
        long actual = bs2.longValue();
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void negativeLong2() {
        long l = -10;
        String s = Long.toBinaryString(l);
        BitSet bs = longBitSet(l);
        List<Boolean> list = bs.toList();
        out.println(list);

    }

    @Test
    public void falseFalseList() {
        List<Boolean> list = List.list(false, false);
        int n = list.length();
        out.println("list size: " + n);
        List<Boolean> list1 = list.dropWhile(Booleans.not);
//            int n2 = list1.length();
        BitSet bs1 = BitSet.listBitSet(list);
        long l = bs1.longValue();
        out.println("value: " + l);
        List<Boolean> list2 = bs1.toList();

        boolean b = Equal.listEqual(Equal.booleanEqual).eq(list1, list2);
        assertTrue(b);
    }

    @Test
    public void clear() {
//        assertThat(4L, is(4L));
        BitSet bs = longBitSet(6).clear(1);
        assertThat(bs.longValue(), equalTo(4L));
    }

    @Test
    public void takeUpper() {
        BitSet bs1 = longBitSet(127);
        BitSet bs2 = bs1.takeUpper(62);
        System.out.println(bs1);
        System.out.println(bs2);
    }

}
