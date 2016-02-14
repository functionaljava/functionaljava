package fj
package data

import org.scalacheck.Prop._
import data.ArbitrarySet.arbitrarySet
import data.ArbitraryList.arbitraryList
import ArbitraryP.arbitraryP1
import Equal.{setEqual, stringEqual, listEqual}
import Ord.intOrd
import Ord.stringOrd
import P.p
import Unit.unit
import Set.{empty, single, join, iterableSet}
import org.scalacheck.Properties

object CheckSet extends Properties("Set") {

  def idInt(n: Int) = n:java.lang.Integer
  implicit def oi : Ord[Int] = intOrd.contramap(idInt _)
  implicit def os : Ord[String] = stringOrd

  property("isEmpty") = forAll((a: Set[Int]) =>
    a.isEmpty == (a.size == 0))

  property("isNotEmpty") = forAll((a: Set[Int]) =>
    !a.isEmpty == (a.size > 0))

  property("insertMember") = forAll((a: Set[Int], n: Int) =>
    a.insert(n).member(n))

  property("deleteInsertIsId") = forAll((a: Set[String], s: String) =>
    setEqual(stringEqual).eq(a.delete(s).insert(s).delete(s), a.delete(s)))

  property("deleteSize") = forAll((a: Set[String], s: String) =>
    (a.insert(s).size == a.size + 1) != a.member(s))

  property("singleMember") = forAll((n: Int) =>
    single(oi, n).member(n))

  property("noDupesFromList") = forAll((a: List[String]) =>
    setEqual(stringEqual).eq(iterableSet(os, a.nub(stringEqual)), iterableSet(os, a)))

  property("noDupesToList") = forAll((a: List[String]) =>
    iterableSet(os, a).toList().length() == a.nub(stringEqual).length())

  property("subsetEmpty") = forAll((a: Set[Int]) =>
    empty(oi).subsetOf(a))

  property("subsetUnion") = forAll((a: Set[Int], b: Set[Int]) =>
    b.subsetOf(a.union(b)))

  property("subsetSelf") = forAll((a: Set[Int]) =>
    a.subsetOf(a))

  property("subsetSize") = forAll((a: Set[Int], b: Set[Int]) =>
    a.size > b.size ==> !a.subsetOf(b))

  property("mapId") = forAll((a: Set[String]) =>
    setEqual(stringEqual).eq(a.map(os, (x: String) => x), a))

  property("updateId") = forAll((a: Set[String], b: String) => {
    val s = a.insert(b)
    setEqual(stringEqual).eq(s.update(b, (x: String) => x)._2, s)
  })
  
  property("update") = forAll((a: Set[String], b: String, c: Char) => {
    val s = a.insert(b).delete(c + b)
    !setEqual(stringEqual).eq(s.update(b, (x: String) => c + x)._2, s)
  })
}
