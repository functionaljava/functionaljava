package fj.data.hamt;

import fj.F2;
import fj.Show;
import fj.data.List;
import fj.data.Stream;
import fj.function.Integers;

/**
 * Created by maperr on 31/05/2016.
 *
 * A sequence of bits representing a value.  The most significant bit (the
 * bit with the highest value) is the leftmost bit and has the highest index.
 * For example, the BitSet("1011") represents the decimal number 11 and has
 * indices [3, 0] inclusive where the bit with the lowest value has the lowest
 * index and is the rightmost bit.
 *
 */
public final class BitSet {

    public static final int TRUE_BIT = 1;
    public static final int FALSE_BIT = 0;

    public static final long BASE_LONG = 1L;
    public static final int MAX_BIT_SIZE = Long.SIZE;

    private final long value;

    private BitSet(final long l) {
        value = l;
    }

    public static BitSet empty() {
        return new BitSet(FALSE_BIT);
    }

    public static BitSet fromLong(final long l) {
        return new BitSet(l);
    }

    public static BitSet fromList(final List<Boolean> list) {
        final int n = MAX_BIT_SIZE;
        if (list.length() > n) {
            throw new IllegalArgumentException("Does not support lists greater than " + n + " bits");
        }
        long result = 0;
        for (Boolean b: list) {
            result = (result << 1) | toInt(b);
        }
        return fromLong(result);
    }

    public static BitSet fromStream(final Stream<Boolean> s) {
        return fromList(s.toList());
    }

    public static BitSet fromString(final String s) {
        return fromStream(Stream.fromString(s).map(c -> toBoolean(c)));
    }

    public boolean isSet(final int index) {
        return (value & (BASE_LONG << index)) != 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public BitSet set(final int index) {
        return fromLong(value | (BASE_LONG << index));
    }

    public BitSet set(final int index, boolean b) {
        return b ? set(index) : clear(index);
    }

    public BitSet clear(final int index) {
        return fromLong(value & ~(BASE_LONG << index));
    }

    public long longValue() {
        return value;
    }

    public BitSet and(final BitSet bs) {
        return fromLong(value & bs.longValue());
    }

    public BitSet or(final BitSet bs) {
        return fromLong(value | bs.longValue());
    }

    public BitSet shiftRight(final int n) {
        return fromLong(value >> n);
    }

    public BitSet shiftLeft(final int n) {
        return fromLong(value << n);
    }

    public int bitsUsed() {
        return toStream().length();
    }

    public Stream<Boolean> toStream() {
        return Stream.fromString(Long.toBinaryString(value)).map(c -> toBoolean(c)).dropWhile(b -> !b);
    }

    public String toString() {
        return Show.bitSetShow.showS(this);
    }

    public int bitsToRight(final int index) {
        if (index >= MAX_BIT_SIZE) {
            throw new IllegalArgumentException("Does not support lists " +
                    "greater than or equal to " + MAX_BIT_SIZE + " bits");
        }
        //  fromString("10101111").bitsRoRight(2)= 2
        int pos = index - 1;
        long mask = BASE_LONG << (pos);
        int result = 0;
        while (pos >= 0) {
            if ((mask & value) != 0) {
                result++;
            }
            mask = mask >> 1;
            pos--;
        }
        return result;
    }

    public List<Boolean> toList() {
        return toStream().toList();
    }

    public <A> A foldRight(final F2<Boolean, A, A> f, A acc) {
        return toStream().foldRight(b -> p -> f.f(b, p._1()), acc);
    }

    public <A> A foldLeft(final F2<A, Boolean, A> f, A acc) {
        return toStream().foldLeft((a, b) -> f.f(a, b), acc);
    }

    public BitSet xor(final BitSet bs) {
        return fromLong(value ^ bs.longValue());
    }

    public BitSet not() {
        return fromLong(~value);
    }

    public BitSet takeLower(final int n) {
        return fromStream(toStream().reverse().take(n).reverse());
    }

    public BitSet takeUpper(final int n) {
        return fromStream(toStream().take(n));
    }

    // min index starts from the least significant bit (on the right), e.g. "101101".range(1, 4) == "110"
    public BitSet range(final int highIndex, final int lowIndex) {
        int max = Math.max(lowIndex, highIndex);
        int min = Math.min(lowIndex, highIndex);
        return fromStream(toStream().reverse().drop(min).take(max - min).reverse());
    }

    public static boolean toBoolean(final char c) {
        return Character.toString(c).equals(Integer.toString(TRUE_BIT));
    }

    public static boolean toBoolean(final int i) {
        return i != FALSE_BIT;
    }

    public static int toInt(final boolean b) {
        return b ? TRUE_BIT : FALSE_BIT;
    }

    public String asString() {
        return Long.toBinaryString(value);
    }

}
