package fj.data;

import fj.*;
import fj.function.Effect1;

import java.util.Collection;
import java.util.Iterator;

import static fj.P.p;
import static fj.data.Option.fromNull;

/**
 * A mutable hash map providing O(1) lookup.
 *
 * @version %build.number%
 * @see java.util.HashMap
 */
public final class HashMap<K, V> implements Iterable<K> {
  private final class Key<K> {
    private final K k;
    private final Equal<K> e;
    private final Hash<K> h;

    Key(final K k, final Equal<K> e, final Hash<K> h) {
      this.k = k;
      this.e = e;
      this.h = h;
    }

    K k() {
      return k;
    }

    @SuppressWarnings({"unchecked"})
    public boolean equals(final Object o) {
      return o instanceof Key && e.eq(k, (K) ((Key<?>) o).k());
    }

    public int hashCode() {
      return h.hash(k);
    }
  }

  /**
   * Returns an iterator for this map's keys. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this map's keys.
   */
  public Iterator<K> iterator() {
    return keys().iterator();
  }

  private final java.util.HashMap<Key<K>, V> m;

  private final Equal<K> e;
  private final Hash<K> h;

  /**
   * Construct a hash map with the given equality and hashing strategy.
   *
   * @param e The equality strategy.
   * @param h The hashing strategy.
   */
  public HashMap(final Equal<K> e, final Hash<K> h) {
    m = new java.util.HashMap<Key<K>, V>();
    this.e = e;
    this.h = h;
  }

  public HashMap(java.util.Map<K, V> map, final Equal<K> e, final Hash<K> h) {
    this(e, h);
    for (K key : map.keySet()) {
      set(key, map.get(key));
    }
  }

  /**
   * Construct a hash map with the given equality and hashing strategy.
   *
   * @param e               The equality strategy.
   * @param h               The hashing strategy.
   * @param initialCapacity The initial capacity.
   */
  public HashMap(final Equal<K> e, final Hash<K> h, final int initialCapacity) {
    m = new java.util.HashMap<Key<K>, V>(initialCapacity);
    this.e = e;
    this.h = h;
  }

    public HashMap(java.util.Map<K, V> map) {
        this(map, Equal.<K>anyEqual(), Hash.<K>anyHash());
    }

    /**
   * Construct a hash map with the given equality and hashing strategy.
   *
   * @param e               The equality strategy.
   * @param h               The hashing strategy.
   * @param initialCapacity The initial capacity.
   * @param loadFactor      The load factor.
   */
  public HashMap(final Equal<K> e, final Hash<K> h, final int initialCapacity, final float loadFactor) {
    m = new java.util.HashMap<Key<K>, V>(initialCapacity, loadFactor);
    this.e = e;
    this.h = h;
  }

  /**
   * Construct a hash map that uses {@link Object#equals} and {@link Object#hashCode}.
   *
   * @return A new hash map that uses {@link Object#equals} and {@link Object#hashCode}.
   */
  public static <K, V> HashMap<K, V> hashMap() {
    final Equal<K> e = Equal.anyEqual();
    final Hash<K> h = Hash.anyHash();
    return new HashMap<K, V>(e, h);
  }

  /**
   * Compare two key values for equality using the underlying equality strategy.
   *
   * @param k1 One key value to compare.
   * @param k2 The other key value to compare.
   * @return <code>true</code> if the two key values are equal, <code>false</code> otherwise.
   */
  public boolean eq(final K k1, final K k2) {
    return e.eq(k1, k2);
  }

  /**
   * Compute the hash of the given key value using the underlying hashing strategy.
   *
   * @param k The key value to computer the hash of.
   * @return The hash of the given key value.
   */
  public int hash(final K k) {
    return h.hash(k);
  }

  /**
   * Returns a potential value that the given key maps to.
   *
   * @param k The key to look up in the hash map.
   * @return A potential value for the given key.
   */
  public Option<V> get(final K k) {
    return fromNull(m.get(new Key<K>(k, e, h)));
  }

  /**
   * A curried version of {@link #get(Object)}.
   *
   * @return A curried version of {@link #get(Object)}.
   */
  public F<K, Option<V>> get() {
    return new F<K, Option<V>>() {
      public Option<V> f(final K k) {
        return get(k);
      }
    };
  }

  /**
   * Clear all entries from this hash map.
   */
  public void clear() {
    m.clear();
  }

  /**
   * Determines if the given key value exists in this hash map.
   *
   * @param k The key value to look for in this hash map.
   * @return <code>true</code> if this hash map contains the given key, <code>false</code> otherwise.
   */
  public boolean contains(final K k) {
    return m.containsKey(new Key<K>(k, e, h));
  }

  /**
   * Returns all key entries in this hash map.
   *
   * @return All key entries in this hash map.
   */
  public List<K> keys() {
    final List.Buffer<K> b = new List.Buffer<K>();

    for (final Key<K> k : m.keySet()) {
      b.snoc(k.k());
    }

    return b.toList();
  }

  /**
   * Returns all values in this hash map.
   *
   * @return All values in this hash map.
   */
  public List<V> values() {
    return keys().map(new F<K, V>() {
      public V f(final K k) {
        return m.get(new Key<K>(k, e, h));
      }
    });
  }

  /**
   * Determines if this hash map has any entries.
   *
   * @return <code>true</code> if this hash map has no entries, <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return m.isEmpty();
  }

  /**
   * Returns the number of entries in this hash map.
   *
   * @return The number of entries in this hash map.
   */
  public int size() {
    return m.size();
  }

  /**
   * Inserts the given key and value association into the hash map.
   *
   * @param k The key to insert.
   * @param v The value to insert.
   */
  public void set(final K k, final V v) {
    if (v != null) {
        m.put(new Key<K>(k, e, h), v);
    }
  }

  /**
   * Deletes the entry in the hash map that corresponds to the given key.
   *
   * @param k The key to delete from this hash map.
   */
  public void delete(final K k) {
    m.remove(new Key<K>(k, e, h));
  }

  /**
   * Deletes the entry in the hash map that corresponds to the given key and returns any associated value.
   *
   * @param k The key to delete from this hash map.
   * @return The value that was associated with the given key, if there was one.
   */
  public Option<V> getDelete(final K k) {
    return fromNull(m.remove(new Key<K>(k, e, h)));
  }

  public <A, B> HashMap<A, B> map(F<K, A> keyFunction,
                                  F<V, B> valueFunction,
                                  Equal<A> equal, Hash<A> hash) {
    final HashMap<A, B> hashMap = new HashMap<A, B>(equal, hash);
    for (K key : keys()) {
      final A newKey = keyFunction.f(key);
      final B newValue = valueFunction.f(get(key).some());
      hashMap.set(newKey, newValue);
    }
    return hashMap;
  }

  public <A, B> HashMap<A, B> map(F<K, A> keyFunction,
                                  F<V, B> valueFunction) {
    return map(keyFunction, valueFunction, Equal.<A>anyEqual(), Hash.<A>anyHash());
  }

  public <A, B> HashMap<A, B> map(F<P2<K, V>, P2<A, B>> function, Equal<A> equal, Hash<A> hash) {
    return from(toStream().map(function), equal, hash);
  }

  public <A, B> HashMap<A, B> map(F<P2<K, V>, P2<A, B>> function) {
    return from(toStream().map(function));
  }

  public <A> HashMap<A, V> mapKeys(F<K, A> keyFunction, Equal<A> equal, Hash<A> hash) {
    return map(keyFunction, Function.<V>identity(), equal, hash);
  }

  public <A> HashMap<A, V> mapKeys(F<K, A> function) {
    return mapKeys(function, Equal.<A>anyEqual(), Hash.<A>anyHash());
  }

  public <B> HashMap<K, B> mapValues(F<V, B> function) {
    return map(Function.<K>identity(), function, e, h);
  }

  public void foreachDoEffect(Effect1<P2<K, V>> effect) {
    toStream().foreachDoEffect(effect);
  }

  public void foreach(F<P2<K, V>, Unit> function) {
    toStream().foreach(function);
  }

  public List<P2<K, V>> toList() {
    return keys().map(k -> p(k, get(k).some()));
  }

  /**
   * Projects an immutable collection of this hash map.
   *
   * @return An immutable collection of this hash map.
   */
  public Collection<P2<K, V>> toCollection() {
    return toList().toCollection();
  }

  public Stream<P2<K, V>> toStream() {
    return toList().toStream();
  }

  public Option<P2<K, V>> toOption() {
    return toList().toOption();
  }

  public Array<P2<K, V>> toArray() {
    return toList().toArray();
  }

  public java.util.Map<K, V> toMap() {
    final java.util.HashMap<K,V> result = new java.util.HashMap<K, V>();
    for (K key : keys()) {
      result.put(key, get(key).some());
    }
    return result;
  }

  public static <K, V> HashMap<K, V> from(Iterable<P2<K, V>> entries) {
    return from(entries, Equal.<K>anyEqual(), Hash.<K>anyHash());
  }

  public static <K, V> HashMap<K, V> from(Iterable<P2<K, V>> entries, Equal<K> equal, Hash<K> hash) {
    final HashMap<K, V> map = new HashMap<K, V>(equal, hash);
    for (P2<K, V> entry : entries) {
        map.set(entry._1(), entry._2());
    }
    return map;
  }
}
