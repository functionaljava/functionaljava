package fj.data;

import fj.Equal;
import fj.F;
import fj.F1Functions;
import fj.Hash;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.P3;
import fj.Show;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import static fj.Function.compose;
import static fj.Function.flip;
import static fj.P.p;
import static fj.data.IterableW.join;
import static fj.data.List.iterableList;

/**
 * An immutable, in-memory map, backed by a red-black tree.
 */
public final class TreeMap<K, V> implements Iterable<P2<K, V>> {
  private final Set<P2<K, Option<V>>> tree;

  private TreeMap(final Set<P2<K, Option<V>>> tree) {
    this.tree = tree;
  }

  private static <K, V> Ord<P2<K, V>> ord(final Ord<K> keyOrd) {
    return keyOrd.contramap(P2.__1());
  }

  /**
   * Constructs an empty tree map.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @return an empty TreeMap with the given key order.
   */
  public static <K, V> TreeMap<K, V> empty(final Ord<K> keyOrd) {
    return new TreeMap<>(Set.empty(TreeMap.ord(keyOrd)));
  }

  @Override
  public boolean equals(Object other) {
    return Equal.equals0(TreeMap.class, this, other, () -> Equal.treeMapEqual(Equal.anyEqual(), Equal.anyEqual()));
  }

  @Override
  public int hashCode() {
    return Hash.treeMapHash(Hash.<K>anyHash(), Hash.<V>anyHash()).hash(this);
  }

  @Override
  public String toString() {
    return Show.treeMapShow(Show.<K>anyShow(), Show.<V>anyShow()).showS(this);
  }

  /**
   * Constructs a tree map from the given elements.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @param p2s The elements to construct the tree map with.
   * @return a TreeMap with the given elements.
   */
  @SafeVarargs public static <K, V> TreeMap<K, V> treeMap(final Ord<K> keyOrd, final P2<K, V>... p2s) {
    return arrayTreeMap(keyOrd, p2s);
  }

  /**
   * Constructs a tree map from the given elements.
   *
   * @deprecated As of release 4.5, use {@link #iterableTreeMap(Ord, Iterable)}
   *
   * @param keyOrd An order for the keys of the tree map.
   * @param list The elements to construct the tree map with.
   * @return a TreeMap with the given elements.
   */
  @Deprecated
  public static <K, V> TreeMap<K, V> treeMap(final Ord<K> keyOrd, final List<P2<K, V>> list) {
    return iterableTreeMap(keyOrd, list);
  }

  /**
   * Constructs a tree map from the given elements.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @param it The elements to construct the tree map with.
   * @return A TreeMap with the given elements.
   */
  public static <K, V> TreeMap<K, V> iterableTreeMap(final Ord<K> keyOrd, final Iterable<P2<K, V>> it) {
    TreeMap<K, V> tm = empty(keyOrd);
    for (final P2<K, V> p2 : it) {
      tm = tm.set(p2._1(), p2._2());
    }
    return tm;
  }

  /**
   * Constructs a tree map from the given elements.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @param it The elements to construct the tree map with.
   * @return A TreeMap with the given elements.
   */
  public static <K, V> TreeMap<K, V> iteratorTreeMap(final Ord<K> keyOrd, final Iterator<P2<K, V>> it) {
    return iterableTreeMap(keyOrd, () -> it);
  }

  /**
   * Constructs a tree map from the given elements.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @param ps The elements to construct the tree map with.
   * @return A TreeMap with the given elements.
   */
  @SafeVarargs
  public static <K, V> TreeMap<K, V> arrayTreeMap(final Ord<K> keyOrd, final P2<K, V>...ps) {
    return iterableTreeMap(keyOrd, Array.array(ps));
  }

  /**
   * Returns a potential value that the given key maps to.
   *
   * @param k The key to look up in the tree map.
   * @return A potential value for the given key.
   */
  public Option<V> get(final K k) {
    final Option<P2<K, Option<V>>> x = tree.split(p(k, Option.none()))._2();
    return x.bind(P2.__2());
  }

  /**
   * Inserts the given key and value association into the tree map.
   * If the given key is already mapped to a value, the old value is replaced with the given one.
   *
   * @param k The key to insert.
   * @param v The value to insert.
   * @return A new tree map with the given value mapped to the given key.
   */
  public TreeMap<K, V> set(final K k, final V v) {
      return new TreeMap<>(tree.insert(p(k, Option.some(v))));
  }

  /**
   * Deletes the entry in the tree map that corresponds to the given key.
   *
   * @param k The key to delete from this tree map.
   * @return A new tree map with the entry corresponding to the given key removed.
   */
  public TreeMap<K, V> delete(final K k) {
    return new TreeMap<>(tree.delete(p(k, Option.none())));
  }

  /**
   * Returns the number of entries in this tree map.
   *
   * @return The number of entries in this tree map.
   */
  public int size() {
    return tree.size();
  }

  /**
   * Determines if this tree map has any entries.
   *
   * @return <code>true</code> if this tree map has no entries, <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return tree.isEmpty();
  }

  /**
   * Returns all values in this tree map.
   *
   * @return All values in this tree map.
   */
  public List<V> values() {
    return iterableList(join(tree.toList().map(compose(IterableW.wrap(), P2.__2()))));
  }

  /**
   * Returns all keys in this tree map.
   *
   * @return All keys in this tree map.
   */
  public List<K> keys() {
    return tree.toList().map(P2.__1());
  }

  /**
   * Determines if the given key value exists in this tree map.
   *
   * @param k The key value to look for in this tree map.
   * @return <code>true</code> if this tree map contains the given key, <code>false</code> otherwise.
   */
  public boolean contains(final K k) {
    return tree.member(p(k, Option.none()));
  }

  /**
   * Returns an iterator for this map's key-value pairs.
   * This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this map's key-value pairs.
   */
  public Iterator<P2<K, V>> iterator() {
    return join(tree.toStream().map(P2.map2_(IterableW.wrap())
    ).map(P2.tuple(compose(IterableW.map(), P.p2())))).iterator();
  }

  /**
   * A mutable map projection of this tree map.
   *
   * @return A new mutable map isomorphic to this tree map.
   */
  public Map<K, V> toMutableMap() {
    final F<K, P2<K, Option<V>>> fakePair = k -> p(k, Option.none());
	final Comparator<K> comparator = tree.ord().contramap(fakePair).toComparator();
	final Map<K, V> m = new java.util.TreeMap<>(comparator);
    for (final P2<K, V> e : this) {
      m.put(e._1(), e._2());
    }
    return m;
  }

    public Stream<P2<K, V>> toStream() {
        return tree.toStream().map(p -> p.map2(o -> o.some()));
    }

    public Stream<P2<K, V>> toStreamReverse() {
        return tree.toStreamReverse().map(p -> p.map2(o -> o.some()));
    }

    public List<P2<K, V>> toList() {
        return tree.toList().map(p -> p.map2(o -> o.some()));
    }

    public List<P2<K, V>> toListReverse() {
        return tree.toListReverse().map(p -> p.map2(o -> o.some()));
    }

  /**
   * An immutable projection of the given mutable map.
   *
   * @param ord An order for the map's keys.
   * @param m   A mutable map to project to an immutable one.
   * @return A new immutable tree map isomorphic to the given mutable map.
   */
  public static <K, V> TreeMap<K, V> fromMutableMap(final Ord<K> ord, final Map<K, V> m) {
    TreeMap<K, V> t = empty(ord);
    for (final Map.Entry<K, V> e : m.entrySet()) {
      t = t.set(e.getKey(), e.getValue());
    }
    return t;
  }

  /**
   * Returns a first-class version of the get method for this TreeMap.
   *
   * @return a functional representation of this TreeMap.
   */
  public F<K, Option<V>> get() {
    return this::get;
  }

  /**
   * Modifies the value for the given key, if present, by applying the given function to it.
   *
   * @param k The key for the value to modify.
   * @param f A function with which to modify the value.
   * @return A new tree map with the value for the given key transformed by the given function,
   *         paired with True if the map was modified, otherwise False.
   */
  public P2<Boolean, TreeMap<K, V>> update(final K k, final F<V, V> f) {
    final P2<Boolean, Set<P2<K, Option<V>>>> up =
        tree.update(p(k, Option.none()), compose(P2.tuple(P.p2()), P2.map2_(Option.<V, V>map().f(f))));
    return p(up._1(), new TreeMap<>(up._2()));
  }

  /**
   * Modifies the value for the given key, if present, by applying the given function to it, or
   * inserts the given value if the key is not present.
   *
   * @param k The key for the value to modify.
   * @param f A function with which to modify the value.
   * @param v A value to associate with the given key if the key is not already present.
   * @return A new tree map with the value for the given key transformed by the given function.
   */
  public TreeMap<K, V> update(final K k, final F<V, V> f, final V v) {
    final P2<Boolean, TreeMap<K, V>> up = update(k, f);
    return up._1() ? up._2() : set(k, v);
  }

  /**
   * Splits this TreeMap at the given key. Returns a triple of:
   * <ul>
   * <li>A set containing all the values of this map associated with keys less than the given key.</li>
   * <li>An option of a value mapped to the given key, if it exists in this map, otherwise None.
   * <li>A set containing all the values of this map associated with keys greater than the given key.</li>
   * </ul>
   *
   * @param k A key at which to split this map.
   * @return Two sets and an optional value, where all elements in the first set are mapped to keys less than the given
   *         key in this map, all the elements in the second set are mapped to keys greater than the given key,
   *         and the optional value is the value associated with the given key if present, otherwise None.
   */
  public P3<Set<V>, Option<V>, Set<V>> split(Ord<V> ord, final K k) {
    final F<Set<P2<K, Option<V>>>, Set<V>> getSome = F1Functions.mapSet(F1Functions.o(Option.fromSome(), P2.__2()), ord);
    return tree.split(p(k, Option.none())).map1(getSome).map3(getSome)
        .map2(F1Functions.o(Option.join(), F1Functions.mapOption(P2.__2())));
  }

  /**
   * Internal construction of a TreeMap from the given set.
   * @param ord An order for the keys of the tree map.
   * @param s The elements to construct the tree map with.
   * @return a TreeMap with the given elements.
   */
  private static <K, V> TreeMap<K, V> treeMap(Ord<K> ord, Set<P2<K, Option<V>>> s) {
    TreeMap<K, V> empty = TreeMap.empty(ord);
    TreeMap<K, V> tree = s.toList().foldLeft((tm, p2) -> {
      Option<V> opt = p2._2();
      if (opt.isSome()) {
        return tm.set(p2._1(), opt.some());
      }
      return tm;
    }, empty);
    return tree;
  }

  /**
   * Splits this TreeMap at the given key. Returns a triple of:
   * <ul>
   * <li>A tree map containing all the values of this map associated with keys less than the given key.</li>
   * <li>An option of a value mapped to the given key, if it exists in this map, otherwise None.
   * <li>A tree map containing all the values of this map associated with keys greater than the given key.</li>
   * </ul>
   *
   * @param k A key at which to split this map.
   * @return Two tree maps and an optional value, where all keys in the first tree map are mapped
   * to keys less than the given key in this map, all the keys in the second tree map are mapped
   * to keys greater than the given key, and the optional value is the value associated with the
   * given key if present, otherwise None.
   */
  public P3<TreeMap<K, V>, Option<V>, TreeMap<K, V>> splitLookup(final K k) {
    P3<Set<P2<K, Option<V>>>, Option<P2<K, Option<V>>>, Set<P2<K, Option<V>>>> p3 = tree.split(p(k, get(k)));
    Ord<K> o = tree.ord().contramap(k2 -> p(k2, Option.none()));
    return p(treeMap(o, p3._1()), get(k), treeMap(o, p3._3()));
  }

  /**
   * Maps the given function across the values of this TreeMap.
   *
   * @param f A function to apply to the values of this TreeMap.
   * @return A new TreeMap with the values transformed by the given function.
   */
  @SuppressWarnings("unchecked")
  public <W> TreeMap<K, W> map(final F<V, W> f) {
    final F<P2<K, Option<V>>, P2<K, Option<W>>> g = P2.map2_(F1Functions.mapOption(f));
    final F<K, P2<K, Option<V>>> coord = flip(P.<K, Option<V>>p2()).f(Option.none());
    final Ord<K> o = tree.ord().contramap(coord);
    return new TreeMap<>(tree.map(TreeMap.ord(o), g));
  }

    /**
     * Returns the minimum (key, value) pair in the tree if the tree is not empty.
     */
    public Option<P2<K, V>> min() {
        return tree.min().map(p -> p(p._1(), p._2().some()));
    }

    /**
     * Returns the minimum key in the tree if the tree is not empty.
     */
    public Option<K> minKey() {
        return tree.min().map(P2::_1);
    }

    /**
     * Returns the maximum (key, value) pair in the tree if the tree is not empty.
     */
    public Option<P2<K, V>> max() {
        return tree.max().map(p -> p(p._1(), p._2().some()));
    }

    /**
     * Returns the maximum key in the tree if the tree is not empty.
     */
    public Option<K> maxKey() {
        return tree.max().map(P2::_1);
    }

  	/**
	 * The expression <code>t1.union(t2)</code> takes the left-biased union of <code>t1</code>
	 * and <code>t2</code>. It prefers <code>t1</code> when duplicate keys are encountered.
	 *
	 * @param t2 The other tree we wish to combine with this one
	 * @return The combined TreeMap
	 */
	public TreeMap<K, V> union(TreeMap<K, V> t2) {
		// TODO This could be implemented more efficiently using "hedge union"
		TreeMap<K, V> result = t2;
		for(P2<K,V> p : this) {
			result = result.set(p._1(), p._2());
		}
		return result;
	}

  	/**
	 * The expression <code>t1.union(t2)</code> takes the left-biased union of <code>t1</code>
	 * and <code>t2</code>. It prefers <code>t1</code> when duplicate keys are encountered.
	 *
	 * @param t2 The other list/set of pairs we wish to combine with this one
	 * @return The combined TreeMap
	 */
	public TreeMap<K, V> union(Iterable<P2<K, V>> t2) {
		TreeMap<K, V> result = this;
		for(P2<K,V> p : t2) {
			if(!this.contains(p._1())) {
				result = result.set(p._1(), p._2());
			}
		}
		return result;
	}

}
