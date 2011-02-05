package fj
package data

import org.scalacheck.{Arbitrary, Gen}
import TreeMap.empty;
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryTreeMap {
  implicit def arbitraryTreeMap[K, V](implicit ak: Arbitrary[K],
                                               av: Arbitrary[V],
                                               o: Ord[K]): Arbitrary[TreeMap[K, V]] =
    Arbitrary(treeOf(arbitrary[(K, V)], o))

  def treeOf[K, V](g : => Gen[(K, V)], o : Ord[K]) : Gen[TreeMap[K, V]] =
    Gen.listOf(g).map(_.foldLeft(empty(o):TreeMap[K, V])((m:TreeMap[K, V], p:(K, V)) => m.set(p._1, p._2)))  
}
