package fj.data.hamt;

import fj.F;
import fj.P2;
import fj.Show;
import fj.data.Either;
import fj.data.Option;
import fj.data.Stream;

/**
 * A Hash Array Mapped Trie node that is either a key-value pair or a
 * Hash Array Mapped Trie.
 *
 * Created by maperr on 31/05/2016.
 */
public final class Node<K, V> {

    private final Either<P2<K, V>, HashArrayMappedTrie<K, V>> either;

    public Node(final Either<P2<K, V>, HashArrayMappedTrie<K, V>> e) {
        either = e;
    }

    public Node(final P2<K, V> simpleNode) {
        this(Either.left(simpleNode));
    }

    public Node(final HashArrayMappedTrie<K, V> hamt) {
        this(Either.right(hamt));
    }

    public static <K, V> Node<K, V> p2Node(final P2<K, V> p) {
        return new Node<>(p);
    }

    public static <K, V> Node<K, V> hamtNode(final HashArrayMappedTrie<K, V> hamt) {
        return new Node<>(hamt);
    }

    /**
     * Performs a reduction on this Node using the given arguments.
     */
    public <B> B match(final F<P2<K, V>, B> f, final F<HashArrayMappedTrie<K, V>, B> g) {
        return either.either(f, g);
    }

    public Stream<P2<K, V>> toStream() {
        return match(Stream::single, HashArrayMappedTrie::toStream);
    }

    @Override
    public String toString() {
        return Show.hamtNodeShow(Show.<K>anyShow(), Show.<V>anyShow()).showS(this);
    }

}
