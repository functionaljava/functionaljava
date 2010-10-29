package fj.data

import org.scalacheck.Prop._
import fj.data.ArbitrarySet.arbitrarySet
import fj.data.ArbitraryList.arbitraryList
import fj.ArbitraryP.arbitraryP1
import fj.pre.Equal.{setEqual, stringEqual, listEqual}
import fj.pre.Ord.intOrd
import fj.pre.Ord.stringOrd
import fj.pre.Ord
import fj.P.p
import fj.Unit.unit
import Set.{empty, single, join, iterableSet}
import fj.Implicit._

object CheckSet {

  def idInt(n: Int) = n:java.lang.Integer
  implicit def oi : Ord[Int] = intOrd.comap(idInt _)
  implicit def os : Ord[String] = stringOrd

  val prop_isEmpty = forAll((a: Set[Int]) =>
    a.isEmpty == (a.size == 0))

  val prop_isNotEmpty = forAll((a: Set[Int]) =>
    !a.isEmpty == (a.size > 0))

  val prop_insertMember = forAll((a: Set[Int], n: Int) =>
    a.insert(n).member(n))

  val prop_deleteInsertIsId = forAll((a: Set[String], s: String) =>
    setEqual(stringEqual).eq(a.delete(s).insert(s).delete(s), a.delete(s)))

  val prop_deleteSize = forAll((a: Set[String], s: String) =>
    (a.insert(s).size == a.size + 1) != a.member(s))

  val prop_singleMember = forAll((n: Int) =>
    single(oi, n).member(n))

  val prop_noDupesFromList = forAll((a: List[String]) =>
    setEqual(stringEqual).eq(iterableSet(os, a.nub(stringEqual)), iterableSet(os, a)))

  val prop_noDupesToList = forAll((a: List[String]) =>
    iterableSet(os, a).toList().length() == a.nub(stringEqual).length())

  val prop_subsetEmpty = forAll((a: Set[Int]) =>
    empty(oi).subsetOf(a))

  val prop_subsetUnion = forAll((a: Set[Int], b: Set[Int]) =>
    b.subsetOf(a.union(b)))

  val prop_subsetSelf = forAll((a: Set[Int]) =>
    a.subsetOf(a))

  val prop_subsetSize = forAll((a: Set[Int], b: Set[Int]) =>
    a.size > b.size ==> !a.subsetOf(b))

  val prop_mapId = forAll((a: Set[String]) =>
    setEqual(stringEqual).eq(a.map(os, (x: String) => x), a))

  val prop_updateId = forAll((a: Set[String], b: String) => {
    val s = a.insert(b)
    setEqual(stringEqual).eq(s.update(b, (x: String) => x)._2, s)
  })
  
  val prop_update = forAll((a: Set[String], b: String, c: Char) => {
    val s = a.insert(b).delete(c + b)
    !setEqual(stringEqual).eq(s.update(b, (x: String) => c + x)._2, s)
  })

    val tests = scala.List(
      ("prop_isEmpty", prop_isEmpty),
      ("prop_isNotEmpty", prop_isNotEmpty),
      ("prop_insertMember", prop_insertMember),
      ("prop_deleteInsertIsId", prop_deleteInsertIsId),
      ("prop_deleteSize", prop_deleteSize),
      ("prop_singleMember", prop_singleMember),
      ("prop_noDupesFromList", prop_noDupesFromList),
      ("prop_noDupesToList", prop_noDupesToList),
      ("prop_subsetEmpty", prop_subsetEmpty),
      ("prop_subsetUnion", prop_subsetUnion),
      ("prop_subsetSelf", prop_subsetSelf),
      ("prop_subsetSize", prop_subsetSize),
      ("prop_mapId", prop_mapId),
      ("prop_updateId", prop_updateId),
      ("prop_update", prop_update)
  ).map { case (n, p) => ("Set." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)
}
