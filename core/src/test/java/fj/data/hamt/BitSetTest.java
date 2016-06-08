package fj.data.hamt;

import fj.Equal;
import fj.data.List;
import fj.function.Booleans;
import org.junit.Test;

import static fj.Equal.booleanEqual;
import static fj.Equal.listEqual;
import static fj.data.List.list;
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
        BitSet.fromLong(1);
    }


    @Test
    public void fromList() {
        List<Boolean> list = list(true, false);
        BitSet bs = BitSet.fromList(list);
        out.println(bs.toString());
        assertThat(bs.longValue(), is(2L));
    }

    @Test
    public void fromList2() {
        List<Boolean> list = list(false, true, true);
        BitSet bs = BitSet.fromList(list);
        out.println(bs.toString());
        assertThat(bs.longValue(), is(3L));
    }



    @Test
    public void fromList3() {
        List<Boolean> list = list(false,false,true,true,true,true,true,true,true,false,false,true,false,true);
        list = list(false, true, false, true);
        BitSet bs = BitSet.fromList(list);
        out.println(bs.toString());
//        assertThat(bs.longValue(), is(4069L));
        List<Boolean> list1 = list.dropWhile(b -> !b);
        assertThat(bs.toList(), is(list1));
    }





    @Test
    public void toList() {
        assertThat(toList(6), is(list(true, true, false)));
        assertThat(toList(3), is(list(true, true)));
//        assertThat(isMatch(3, list(false, true, true)), is(true));
    }

    public boolean isMatch(long l, List<Boolean> list) {
        return listBooleanEqual.eq(BitSet.fromLong(l).toList(), list);
    }

    List<Boolean> toList(long l) {
        return BitSet.fromLong(l).toList();
    }


    @Test
    public void reverseStream() {
        List<Boolean> l = BitSet.fromLong(5).toReverseStream().toList();
        assertThat(l, is(list(true, false, true)));
    }

    @Test
    public void strings() {
        long l = 472446402560L;
        long actual = BitSet.fromString(BitSet.fromLong(l).asString()).longValue();
        assertThat(actual, is(l));

    }


    @Test
    public void negativeLong() {
        long l = -1760752564951867896L;
        BitSet bs = BitSet.fromLong(l);
        List<Boolean> list = bs.toList();
        out.println(list);

    }

    @Test
    public void negativeRoundTrip() {
        long expected = -2;
        BitSet bs1 = BitSet.fromLong(expected);
        List<Boolean> list1 = bs1.toList();
        BitSet bs2 = BitSet.fromList(list1);
        long actual = bs2.longValue();
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void negativeLong2() {
        long l = -10;
        String s = Long.toBinaryString(l);
        BitSet bs = BitSet.fromLong(l);
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
        BitSet bs1 = BitSet.fromList(list);
        long l = bs1.longValue();
        out.println("value: " + l);
        List<Boolean> list2 = bs1.toList();

        boolean b = Equal.listEqual(Equal.booleanEqual).eq(list1, list2);

        assertTrue(b);



    }


}
