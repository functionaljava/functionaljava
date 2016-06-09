package fj.data.hamt;

import fj.F;
import fj.P2;
import fj.Show;
import fj.data.Either;
import fj.data.Option;
import fj.data.Stream;

/**
 * Created by maperr on 31/05/2016.
 *
 *
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

    public Option<V> find(final F<P2<K, V>, Option<V>> f, final F<HashArrayMappedTrie<K, V>, Option<V>> g) {
        return match(p -> f.f(p), hamt -> g.f(hamt));
    }

    public <B> B match(final F<P2<K, V>, B> f, final F<HashArrayMappedTrie<K, V>, B> g) {
        return either.either(p -> f.f(p), hamt -> g.f(hamt));
    }

    public Stream<P2<K, V>> toStream() {
        return match(p -> Stream.single(p), h -> h.toStream());
    }

    public String toString() {
        return Show.hamtNodeShow(Show.<K>anyShow(), Show.<V>anyShow()).showS(this);
    }

}
