package fj.data.hamt;

import fj.Equal;
import fj.Hash;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

import static fj.P.p;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 * Created by maperr on 31/05/2016.
 */
public final class HashArrayMappedTrie<K, V> {

    private final Seq<Node<K, V>> seq;
    private final BitSet bitSet;
    private final Hash<K> hash;
    private final Equal<K> equal;

    public static final int BITS_IN_INDEX = 5;
    public static final int SIZE = (int) Math.pow(2, BITS_IN_INDEX);
    public static final int MIN_INDEX = 0;
    public static final int MAX_INDEX = SIZE - 1;

    private HashArrayMappedTrie(final BitSet bs, final Seq<Node<K, V>> s, final Equal<K> e, final Hash<K> h) {
        bitSet = bs;
        seq = s;
        hash = h;
        equal = e;
    }

    public static <K, V> HashArrayMappedTrie<K, V> empty(final Equal<K> e, final Hash<K> h) {
        return new HashArrayMappedTrie<>(BitSet.empty(), Seq.empty(), e, h);
    }

    public static <K, V> HashArrayMappedTrie<K, V> hamt(final BitSet bs, final Seq<Node<K, V>> s, final Equal<K> e, final Hash<K> h) {
        return new HashArrayMappedTrie<>(bs, s, e, h);
    }

    public Option<V> find(final K k) {
        return find(k, 0, BITS_IN_INDEX);
    }

    public Option<V> find(final K k, final int lowIndex, final int highIndex) {
        final int bitIndex = bitsBetween(hash.hash(k), lowIndex, highIndex);
        // look up the bit bitmap
        final int seqIndex = bitSet.bitsToRight(bitIndex);
        if (seqIndex >= seq.length()) {
            return none();
        } else {
            return seq.index(seqIndex).find(n -> {
                final boolean b = equal.eq(n._1(), k);
                return b ? some(n._2()) : none();
            }, hamt -> hamt.find(k, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX));
        }
    }

    public HashArrayMappedTrie<K, V> set(final K k, final V v) {
        return set(k, v, 0, BITS_IN_INDEX);
    }

    public HashArrayMappedTrie<K, V> set(final List<P2<K, V>> list) {
        return list.foldLeft(h -> p -> h.set(p._1(), p._2()), this);
    }

    public HashArrayMappedTrie<K, V> set(final K k, V v, final int lowIndex, final int highIndex) {
        final int bsIndex = bitsBetween(hash.hash(k), lowIndex, highIndex);
        if (!bitSet.isSet(bsIndex)) {
            // append new p2Node
            final Node<K, V> sn1 = Node.p2Node(p(k, v));
            return HashArrayMappedTrie.hamt(bitSet.set(bsIndex), SeqUtil.insert(seq, bsIndex, sn1), equal, hash);
        } else {
            final int index = bitSet.bitsToRight(bsIndex);
            final Node<K, V> oldNode = seq.index(index);
            final Node<K, V> newNode = oldNode.match(n -> {
                final boolean b = equal.eq(n._1(), k);
                if (b) {
                    return Node.p2Node(p(k, v));
                } else {
                    final HashArrayMappedTrie<K, V> hamt = HashArrayMappedTrie.<K, V>empty(equal, hash)
                            .set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX)
                            .set(n._1(), n._2(), lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
                    return Node.hamtNode(hamt);
                }
            }, hamt -> Node.hamtNode(hamt.set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX)));
            return hamt(bitSet, seq.update(index, newNode), equal, hash);
        }
    }

    // bits between low (inclusive) and high (exclusive)
    public static int bitsBetween(final int n, final int low, final int high) {
        return (int) BitSet.fromLong(n).range(high, low).longValue();
    }

    public Stream<P2<K, V>> toStream() {
        return seq.toStream().bind(sn -> sn.toStream());
    }

    public String toString() {
        return "HashArrayMappedTrie(" + bitSet.toString() + ", " + seq.toString() + ")";
    }

    public int length() {
        return seq.foldLeft((acc, sn) -> sn.match(sn2 -> acc + 1, hamt -> acc + hamt.length()), 0);
    }

}
