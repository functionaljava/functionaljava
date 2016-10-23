package fj.data.hamt;

import fj.Equal;
import fj.F2;
import fj.Monoid;
import fj.Show;
import fj.data.List;
import fj.data.Stream;

/**
 * A sequence of bits representing a value.  The most significant bit (the
 * bit with the highest value) is the leftmost bit and has the highest index.
 * For example, the BitSet("1011") represents the decimal number 11 and has
 * indices [3, 0] inclusive where the bit with the lowest value has the lowest
 * index and is the rightmost bit.
 *
 * Created by maperr on 31/05/2016.
 */
public final class BitSet {

    public static final int TRUE_BIT = 1;
    public static final int FALSE_BIT = 0;
    public static final BitSet EMPTY = new BitSet(FALSE_BIT);

    public static final long BASE_LONG = 1L;
    public static final int MAX_BIT_SIZE = Long.SIZE;
    public static final int MAX_BIT_INDEX = Long.SIZE - 1;

    private final long value;

    private BitSet(final long l) {
        value = l;
    }

    public static BitSet empty() {
        return EMPTY;
    }

    public static BitSet longBitSet(final long l) {
        return new BitSet(l);
    }

    public static BitSet listBitSet(final List<Boolean> list) {
        final int n = list.length();
        if (n > MAX_BIT_SIZE) {
            throw new IllegalArgumentException("Does not support lists greater than " + MAX_BIT_SIZE + " bits, actual size is " + n);
        }
        long result = 0;
        for (Boolean b: list) {
            result = (result << 1) | toInt(b);
        }
        return longBitSet(result);
    }

    public static BitSet streamBitSet(final Stream<Boolean> s) {
        return listBitSet(s.toList());
    }

    public static BitSet stringBitSet(final String s) {
        return streamBitSet(Stream.fromString(s).map(BitSet::toBoolean));
    }

    public boolean isSet(final int index) {
        return (value & (BASE_LONG << index)) != 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public BitSet set(final int index) {
        return longBitSet(value | (BASE_LONG << index));
    }

    public BitSet set(final int index, boolean b) {
        return b ? set(index) : clear(index);
    }

    public BitSet clear(final int index) {
        return longBitSet(value & ~(BASE_LONG << index));
    }

    public long longValue() {
        return value;
    }

    public BitSet and(final BitSet bs) {
        return longBitSet(value & bs.longValue());
    }

    public BitSet or(final BitSet bs) {
        return longBitSet(value | bs.longValue());
    }

    public BitSet shiftRight(final int n) {
        return longBitSet(value >> n);
    }

    public BitSet shiftLeft(final int n) {
        return longBitSet(value << n);
    }

    public int bitsUsed() {
        return toStream().length();
    }

    public int bitsOn() {
        return toStream().foldLeft((acc, b) -> acc + (b ? 1 : 0), 0);
    }

    /**
     * Returns a stream of boolean where the head is the most significant bit
     * (the bit with the largest value)
     */
    public Stream<Boolean> toStream() {
        return Stream.fromString(Long.toBinaryString(value)).map(BitSet::toBoolean).dropWhile(b -> !b);
    }

    @Override
    public String toString() {
        return Show.bitSetShow.showS(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Equal.equals0(BitSet.class, this, obj, Equal.bitSetSequal);
    }

    public int bitsToRight(final int index) {
        if (index >= MAX_BIT_SIZE) {
            throw new IllegalArgumentException("Does not support an index " +
                    "greater than or equal to " + MAX_BIT_SIZE + " bits, actual argument is " + index);
        }
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
        return toStream().foldLeft(f, acc);
    }

    public BitSet xor(final BitSet bs) {
        return longBitSet(value ^ bs.longValue());
    }

    public BitSet not() {
        return longBitSet(~value);
    }

    public BitSet takeLower(final int n) {
        return streamBitSet(toStream().reverse().take(n).reverse());
    }

    public BitSet takeUpper(final int n) {
        String zero = Integer.toString(FALSE_BIT);
        String current = asString();
        String pad = Monoid.stringMonoid.sumLeft(List.replicate(MAX_BIT_SIZE - current.length(), zero));
        return stringBitSet(pad + current.substring(0, Math.max(n - pad.length(), 0)));
    }

    /**
     * Returns the bit set from indices in the range from low (inclusive)
     * to high(exclusive) from the least significant bit (on the right),
     * e.g. "101101".range(1, 4) == "0110"
     */
    public BitSet range(final int highIndex, final int lowIndex) {
        int max = Math.max(lowIndex, highIndex);
        int min = Math.min(lowIndex, highIndex);
        return new BitSet(max == min ? 0L : (value << (64 - max)) >>> (64 - max + min));
    }

    public static boolean toBoolean(final char c) {
        return c != '0';
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
