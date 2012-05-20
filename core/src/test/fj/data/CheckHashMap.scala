package fj
package data

import org.scalacheck.Prop._
import ArbitraryHashMap._
import Equal.{intEqual, stringEqual, optionEqual}
import Hash.{intHash, stringHash}
import fj.data.Option._
import scala.collection.JavaConversions._
import org.scalacheck.{Arbitrary, Properties}
import data.ArbitraryList._
import org.scalacheck.Arbitrary._

object CheckHashMap extends Properties("HashMap") {
  implicit val equalInt: Equal[Int] = intEqual comap ((x: Int) => (x: java.lang.Integer))
  implicit val hashInt: Hash[Int] = intHash comap ((x: Int) => (x: java.lang.Integer))

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
    val map = HashMap.from[Int, String](entries, equalInt, hashInt)
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
}
