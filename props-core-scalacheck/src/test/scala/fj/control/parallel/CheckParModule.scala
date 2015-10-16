package fj
package control
package parallel

import org.scalacheck.Prop._
import ArbitraryP.arbitraryP1
import data.ArbitraryList.arbitraryList
import data.ArbitraryStream.arbitraryStream
import data.ArbitraryArray.arbitraryArray
import ArbitraryStrategy._
import data.List
import data.Stream
import data.Array
import Equal.{listEqual, stringEqual, streamEqual, arrayEqual}
import Monoid.stringMonoid
import org.scalacheck.Properties

object CheckParModule extends Properties("ParModule") {
  def rev = (x: String) => x.reverse: String

  implicit val strategy = ArbitraryStrategy.arbitraryStrategy[fj.Unit]
  implicit val parmodule = ArbitraryParModule.arbitraryParModule

  property("promise") = forAll((a: P1[Int], p: ParModule) => p.promise(a).claim == a._1)

  property("promisef") = forAll((s: String, p: ParModule) => p.promise(rev).f(s).claim == rev(s))

  property("sequenceList") = forAll((s: List[String], p: ParModule) =>
    listEqual(stringEqual).eq(s.map(rev), p.sequence(s.map((x: String) => p.promise(rev).f(x))).claim))

  property("sequenceStream") = forAll((s: Stream[String], p: ParModule) =>
    streamEqual(stringEqual).eq(s.map(rev), p.sequence(s.map((x: String) => p.promise(rev).f(x))).claim))

  property("mapList") = forAll((s: List[String], p: ParModule) =>
    listEqual(stringEqual).eq(s.map(rev), p.mapM(s, p.promise(rev)).claim))

  property("mapStream") = forAll((s: Stream[String], p: ParModule) =>
    streamEqual(stringEqual).eq(s.map(rev), p.mapM(s, p.promise(rev)).claim))

  property("parMapList") = forAll((s: List[String], p: ParModule) =>
    listEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim))

  property("parMapStream") = forAll((s: Stream[String], p: ParModule) =>
    streamEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim))

  property("parMapArray") = forAll((s: Array[String], p: ParModule) =>
    arrayEqual(stringEqual).eq(s.map(rev), p.parMap(s, rev).claim)) 

  property("parFlatMap") = forAll((s: Stream[String], p: ParModule) => {
    val f = (x: String) => Stream.stream(x, rev(x)) : Stream[String]
    streamEqual(stringEqual).eq(s.bind(f), p.parFlatMap(s, f).claim)})

  property("parFoldMap") = forAll((s: Stream[String], p: ParModule) => {
    val chunk = (x: Stream[String]) => P.p(Stream.stream(x.head), x.tail._1)
    stringEqual.eq(stringMonoid.sumLeft(s.map(rev)), p.parFoldMap(s, rev, stringMonoid, chunk).claim)})
}
