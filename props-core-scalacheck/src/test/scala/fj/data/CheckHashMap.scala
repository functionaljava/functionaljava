package fj
package data

import fj.function.Effect1
import org.scalacheck.Prop._
import ArbitraryHashMap._
import Equal._
import Hash._
import Ord._
import fj.data.Option._
import scala.collection.JavaConversions._
import org.scalacheck.{Arbitrary, Properties}
import data.ArbitraryList._
import org.scalacheck.Arbitrary._
import java.util.Map

object CheckHashMap extends Properties("HashMap") {
  implicit val equalInt: Equal[Int] = intEqual contramap ((x: Int) => (x: java.lang.Integer))
  implicit val hashInt: Hash[Int] = intHash contramap ((x: Int) => (x: java.lang.Integer))

  implicit def arbitraryListOfIterableP2: Arbitrary[java.lang.Iterable[P2[Int, String]]] =
    Arbitrary(listOf(arbitrary[(Int, String)])
      .map(_.map((tuple: (Int, String)) => P.p(tuple._1, tuple._2))
      .asInstanceOf[java.lang.Iterable[P2[Int, String]]]))

  property("eq") = forAll((m: HashMap[Int, String], x: Int, y: Int) => m.eq(x, y) == equalInt.eq(x, y))

  property("hash") = forAll((m: HashMap[Int, String], x: Int) => m.hash(x) == hashInt.hash(x))

  property("get") = forAll((m: HashMap[Int, String], k: Int) => optionEqual(stringEqual).eq(m.get(k), m.get.f(k)))

  property("set") = forAll((m: HashMap[Int, String], k: Int, v: String) => {
    m.set(k, v)
    m.get(k).some == v
  })

  property("clear") = forAll((m: HashMap[Int, String], k: Int) => {
    m.clear
    m.get(k).isNone
  })

  property("contains") = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isSome == m.contains(k))

  property("keys") = forAll((m: HashMap[Int, String]) => m.keys.forall((k: Int) => (m.get(k).isSome): java.lang.Boolean))

  property("isEmpty") = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isNone || !m.isEmpty)

  property("size") = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isNone || m.size != 0)

  property("delete") = forAll((m: HashMap[Int, String], k: Int) => {
    m.delete(k)
    m.get(k).isNone
  })

  property("toList") = forAll((m: HashMap[Int, String]) => {
    val list = m.toList
    list.length() == m.keys().length() && list.forall((entry: P2[Int, String]) =>
      optionEqual(stringEqual).eq(m.get(entry._1()), some(entry._2())).asInstanceOf[java.lang.Boolean])
  })

  property("getDelete") = forAll((m: HashMap[Int, String], k: Int) => {
    val x = m.get(k)
    val y = m.getDelete(k)
    val z = m.get(k)

    z.isNone && optionEqual(stringEqual).eq(x, y)
  })

  property("from") = forAll((entries: java.lang.Iterable[P2[Int, String]]) => {
    val map = HashMap.iterableHashMap[Int, String](equalInt, hashInt, entries)
    entries.groupBy(_._1)
      .forall((e: (Int, Iterable[P2[Int, String]])) => e._2
      .exists((elem: P2[Int, String]) => optionEqual(stringEqual).eq(map.get(e._1), Option.some(elem._2))))
  })

  property("map") = forAll((m: HashMap[Int, String]) => {
    val keyFunction: F[Int, String] = (i: Int) => i.toString
    val valueFunction: (String) => String = (s: String) => s + "a"
    val mapped = m.map(keyFunction, valueFunction, stringEqual, stringHash)
    val keysAreEqual = m.keys().map(keyFunction).toSet == mapped.keys.toSet
    val appliedFunctionsToKeysAndValues: Boolean = m.keys().forall((key: Int) => {
      val mappedValue = mapped.get(keyFunction.f(key))
      val oldValueMapped = some(valueFunction.f(m.get(key).some()))
      Equal.optionEqual(stringEqual).eq(mappedValue, oldValueMapped)
    })

    keysAreEqual && appliedFunctionsToKeysAndValues
  })

  property("toMap") = forAll((m: HashMap[Int, String]) => {
    val toMap: Map[Int, String] = m.toMap
    m.keys().forall((key: Int) => m.get(key).some() == toMap.get(key))
  })

  property("fromMap") = forAll((m: HashMap[Int, String]) => {
    val map = new java.util.HashMap[Int, String]()
    m.keys().foreach((key: Int) => {
      map.put(key, m.get(key).some())
      Unit.unit()
    })
    val fromMap: HashMap[Int, String] = new HashMap[Int, String](map)
    val keysAreEqual = m.keys.toSet == fromMap.keys.toSet
    val valuesAreEqual = m.keys().forall((key: Int) =>
      optionEqual(stringEqual).eq(m.get(key), fromMap.get(key)))
    keysAreEqual && valuesAreEqual
  })

  property("No null values") = forAll((m: List[Int]) => {
    val map = HashMap.hashMap[Int, Int]()
    m.foreachDoEffect(new Effect1[Int] {
      def f(a: Int) {
        map.set(a, null.asInstanceOf[Int])
      }
    })
    m.forall(new F[Int, java.lang.Boolean]() {
      def f(a: Int) = map.contains(a) == false
    })
  })
}