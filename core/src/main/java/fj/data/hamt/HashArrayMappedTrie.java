package fj.data.hamt;

import fj.Equal;
import fj.F2;
import fj.Hash;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

import static fj.P.p;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 * A hash array mapped trie (HAMT) is an implementation of an associative
 * array that combines the characteristics of a hash table and an array
 * mapped trie.  It is a refined version of the more general notion of
 * a hash tree.
 *
 * Created by maperr on 31/05/2016.
 *
 * Based on "Ideal Hash Trees" by Phil Bagwell, available from
 * http://lampwww.epfl.ch/papers/idealhashtrees.pdf
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

    /**
     * Creates an empty trie.
     */
    public static <K, V> HashArrayMappedTrie<K, V> empty(final Equal<K> e, final Hash<K> h) {
        return new HashArrayMappedTrie<>(BitSet.empty(), Seq.empty(), e, h);
    }

    private static <K, V> HashArrayMappedTrie<K, V> hamt(final BitSet bs, final Seq<Node<K, V>> s, final Equal<K> e, final Hash<K> h) {
        return new HashArrayMappedTrie<>(bs, s, e, h);
    }

    /**
     * Returns an optional value for the given key k.
     */
    public Option<V> find(final K k) {
        return find(k, MIN_INDEX, MIN_INDEX + BITS_IN_INDEX);
    }

    /**
     * Returns an optional value for the given key k for those nodes between
     * lowIndex (inclusive) and highIndex (exclusive).
     */
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

    /**
     * Adds the key-value pair (k, v) to the trie.
     */
    public HashArrayMappedTrie<K, V> set(final K k, final V v) {
        return set(k, v, MIN_INDEX, MIN_INDEX + BITS_IN_INDEX);
    }

    /**
     * Adds the product of key-value (k, v) pairs to the trie.
     */
    public HashArrayMappedTrie<K, V> set(final List<P2<K, V>> list) {
        return list.foldLeft(h -> p -> h.set(p._1(), p._2()), this);
    }

    /**
     * Sets the key-value pair (k, v) for the bit range [lowIndex, highIndex).
     */
    private HashArrayMappedTrie<K, V> set(final K k, final V v, final int lowIndex, final int highIndex) {
        final int bsIndex = bitsBetween(hash.hash(k), lowIndex, highIndex);
        if (!bitSet.isSet(bsIndex)) {
            // append new node
            final Node<K, V> sn1 = Node.p2Node(p(k, v));
            return HashArrayMappedTrie.hamt(bitSet.set(bsIndex), SeqUtil.insert(seq, bsIndex, sn1), equal, hash);
        } else {
            final int index = bitSet.bitsToRight(bsIndex);
            final Node<K, V> oldNode = seq.index(index);
            final Node<K, V> newNode = oldNode.match(n -> {
                if (equal.eq(n._1(), k)) {
                    return Node.p2Node(p(k, v));
                } else {
                    final HashArrayMappedTrie<K, V> hamt = HashArrayMappedTrie.<K, V>empty(equal, hash)
                        .set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX)
                        .set(n._1(), n._2(), lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
                    return Node.hamtNode(hamt);
                }
            }, hamt -> Node.hamtNode(hamt.set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX))
            );
            return hamt(bitSet, seq.update(index, newNode), equal, hash);
        }
    }

    /**
     * Returns the bits between [low, high) for the bit set from the value n.
     */
    public static int bitsBetween(final int n, final int low, final int high) {
        return (int) BitSet.longBitSet(n).range(high, low).longValue();
    }

    /**
     * Returns a stream of key-value pairs.
     */
    public Stream<P2<K, V>> toStream() {
        return seq.toStream().bind(sn -> sn.toStream());
    }

    public String toString() {
        return Show.hamtShow(Show.<K>anyShow(), Show.<V>anyShow()).showS(this);
    }

    /**
     * Performs a left-fold reduction across this trie.
     */
    public <B> B foldLeft(F2<B, Node<K, V>, B> f, B b) {
        return seq.foldLeft(f, b);
    }

    public BitSet getBitSet() {
        return bitSet;
    }

    public Seq<Node<K, V>> getSeq() {
        return seq;
    }

    /**
     * Returns the number of elements in the trie.
     */
    public int length() {
        return seq.foldLeft(
            (acc, node) -> node.match(p2 -> acc + 1, hamt -> acc + hamt.length()), 0
        );
    }

}
