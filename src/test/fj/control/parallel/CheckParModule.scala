package fj.control.parallel

import org.scalacheck.Prop._
import fj.ArbitraryP.arbitraryP1
import fj.Implicit._
import fj.data.ArbitraryList.arbitraryList
import fj.data.ArbitraryStream.arbitraryStream
import fj.data.ArbitraryArray.arbitraryArray
import fj.data.List
import fj.data.Stream
import fj.data.Array
import fj.pre.Equal.{listEqual, stringEqual, streamEqual, arrayEqual}
import fj.pre.Monoid.stringMonoid
import fj.P

object CheckParModule {
  def rev = (x: String) => x.reverse: String

  implicit val strategy = ArbitraryStrategy.arbitraryStrategy[Unit]
  implicit val parmodule = ArbitraryParModule.arbitraryParModule

  val prop_promise = forAll((a: P1[Int], p: ParModule) => p.promise(a).claim == a._1)

  val prop_promisef = forAll((s: String, p: ParModule) => p.promise(rev).f(s).claim == rev(s))

  val prop_sequenceList = forAll((s: List[String], p: ParModule) => 
    listEqual(stringEqual).eq(s.map(rev), p.sequence(s.map((x: String) => p.promise(rev).f(x))).claim))

  val prop_sequenceStream = forAll((s: Stream[String], p: ParModule) => 
    streamEqual(stringEqual).eq(s.map(rev), p.sequence(s.map((x: String) => p.promise(rev).f(x))).claim))

  val prop_mapList = forAll((s: List[String], p: ParModule) =>
    listEqual(stringEqual).eq(s.map(rev), p.mapM(s, p.promise(rev)).claim))

  val prop_mapStream = forAll((s: Stream[String], p: ParModule) =>
    streamEqual(stringEqual).eq(s.map(rev), p.mapM(s, p.promise(rev)).claim))

  val prop_parMapList = forAll((s: List[String], p: ParModule) =>
    listEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim))

  val prop_parMapStream = forAll((s: Stream[String], p: ParModule) =>
    streamEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim))

  val prop_parMapArray = forAll((s: Array[String], p: ParModule) =>
    arrayEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim)) 

  val prop_parFlatMap = forAll((s: Stream[String], p: ParModule) => {
    val f = (x: String) => Stream.stream(x, rev(x)) : Stream[String]
    streamEqual(stringEqual).eq(s.bind(f), p.parFlatMap(s, f).claim)})

  val prop_parFoldMap = forAll((s: Stream[String], p: ParModule) => {
    val chunk = (x: Stream[String]) => P.p(Stream.stream(x.head), x.tail._1)
    stringEqual.eq(stringMonoid.sumLeft(s.map(rev)), p.parFoldMap(s, rev, stringMonoid, chunk).claim)})

  val tests = scala.List(
    ("prop_promise", prop_promise),
    ("prop_sequenceList", prop_sequenceList),
    ("prop_sequenceStream", prop_sequenceStream),
    ("prop_mapList", prop_mapList),
    ("prop_mapStream", prop_mapStream),
    ("prop_parMapList", prop_parMapList),
    ("prop_parMapStream", prop_parMapStream),
    ("prop_parMapArray", prop_parMapArray),
    ("prop_parFoldMap", prop_parFoldMap),
    ("prop_parFlatMap", prop_parFlatMap),
    ("prop_promisef", prop_promisef)).map{case (n, p) => ("ParModule." + n, p)}

  def main(args: scala.Array[String]) = Tests.run(tests)
}
