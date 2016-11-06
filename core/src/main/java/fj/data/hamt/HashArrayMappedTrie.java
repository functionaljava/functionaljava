package fj.data.hamt;

import fj.Equal;
import fj.F2;
import fj.Hash;
import fj.Ord;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Stream;

import static fj.P.p;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.hamt.BitSet.longBitSet;

/**
 * A hash array mapped trie (HAMT) is an implementation of an associative
 * array that combines the characteristics of a hash table and an array
 * mapped trie.  It is a refined version of the more general notion of
 * a hash tree.
 *
 * @author Mark Perry
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
    public static final int SIZE = (int) StrictMath.pow(2, BITS_IN_INDEX);
    public static final int MIN_INDEX = 0;
    public static final int MAX_INDEX = SIZE - 1;

    /**
     * Creates an empty trie for the bitset, sequence of nodes, equal and hash.
     *
     * @param bs - The set of bits to indicate which of the SIZE nodes in the sequence are used.
     * @param s - The sequence of HAMT nodes - either a HAMT or a key-value pair.
     * @param e - Equality instance for keys.
     * @param h - Hash instance for keys.
     */
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

    /**
     * Create and empty trie keyed by integer.
     */
    public static <V> HashArrayMappedTrie<Integer, V> emptyKeyInteger() {
        return empty(Equal.intEqual, Hash.intHash);
    }

    /**
     * Returns if the trie is empty.
     */
    public boolean isEmpty() {
        return bitSet.isEmpty();
    }

    /**
     * Static constructor for a HAMT instance.
     */
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
        BitSet bs1 = longBitSet(hash.hash(k)).range(lowIndex, highIndex);
        int i = (int) bs1.longValue();
        boolean b = bitSet.isSet(i);
        final int index = bitSet.bitsToRight(i);
        if (!b) {
            return none();
        } else {
            final Node<K, V> oldNode = seq.index(index);
            return oldNode.match(
                n -> equal.eq(n._1(), k) ? some(n._2()) : none(),
                hamt -> hamt.find(k, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX)
            );
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
     * Sets the key-value pair (k, v) for the bit range lowIndex (inclusive) to highIndex (exclusive).
     */
    private HashArrayMappedTrie<K, V> set(final K k, final V v, final int lowIndex, final int highIndex) {
        final BitSet bs1 = longBitSet(hash.hash(k)).range(lowIndex, highIndex);
        final int i = (int) bs1.longValue();
        final boolean b = bitSet.isSet(i);
        final int index = bitSet.bitsToRight(i);

        if (!b) {
            // append new node
            final Node<K, V> sn1 = Node.p2Node(p(k, v));
            return hamt(bitSet.set(i), seq.insert(index, sn1), equal, hash);
        } else {
            final Node<K, V> oldNode = seq.index(index);
            final Node<K, V> newNode = oldNode.match(n -> {
                if (equal.eq(n._1(), k)) {
                    return Node.p2Node(p(k, v));
                } else {
                    final HashArrayMappedTrie<K, V> e = HashArrayMappedTrie.empty(equal, hash);
                    final HashArrayMappedTrie<K, V> h1 =  e.set(n._1(), n._2(), lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
                    final HashArrayMappedTrie<K, V> h2 = h1.set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX);
                    return Node.hamtNode(h2);
                }
            }, hamt -> Node.hamtNode(hamt.set(k, v, lowIndex + BITS_IN_INDEX, highIndex + BITS_IN_INDEX))
            );
            return hamt(bitSet, seq.update(index, newNode), equal, hash);
        }
    }

    /**
     * Returns a stream of key-value pairs.
     */
    public Stream<P2<K, V>> toStream() {
        return seq.toStream().bind(Node::toStream);
    }

    /**
     * Returns the list of key-value pairs, ordered by key.
     */
    public List<P2<K, V>> toList(Ord<K> o) {
        return toStream().sort(Ord.p2Ord1(o)).toList();
    }

    /**
     * Returns a list of key-value pairs.
     */
    public List<P2<K, V>> toList() {
        return toStream().toList();
    }

    @Override
    public String toString() {
        return Show.hamtShow(Show.<K>anyShow(), Show.<V>anyShow()).showS(this);
    }

    /**
     * Performs a left-fold reduction across this trie.
     */
    public <B> B foldLeftOnNode(F2<B, Node<K, V>, B> f, B b) {
        return seq.foldLeft(f, b);
    }

    /**
     * Performs a left-fold reduction across this trie.
     */
    public <B> B foldLeft(F2<B, P2<K, V>, B> f, F2<B, HashArrayMappedTrie<K, V>, B> g, B b) {
        return foldLeftOnNode((acc, n) -> n.match(p -> f.f(acc, p), h -> g.f(acc, h)), b);
    }

    /**
     * Performs a left-fold reduction across this trie.
     */
    public <B> B foldLeft(F2<B, P2<K, V>, B> f, B b) {
        return foldLeftOnNode((acc, n) -> n.match(p -> f.f(acc, p), h -> h.foldLeft(f, acc)), b);
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
