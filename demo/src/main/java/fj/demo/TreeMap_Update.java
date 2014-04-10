package fj.demo;

import fj.data.TreeMap;
import static fj.data.TreeMap.empty;
import static fj.function.Integers.add;
import static fj.Ord.stringOrd;

/**
 * Queries and updates an entry in a TreeMap in one go.
 */
public class TreeMap_Update {
  public static void main(final String[] a) {
    TreeMap<String, Integer> map = empty(stringOrd);
    map = map.set("foo", 2);
    map = map.update("foo", add.f(3))._2();
    System.out.println(map.get("foo").some()); // 5 
  }
}

