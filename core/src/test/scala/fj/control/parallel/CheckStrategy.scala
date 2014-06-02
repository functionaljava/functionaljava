package fj
package control
package parallel

import org.scalacheck.Prop._
import ArbitraryP.arbitraryP1
import control.parallel.ArbitraryStrategy.arbitraryStrategy
import data.ArbitraryList.arbitraryList
import data.ArbitraryArray.arbitraryArray
import Strategy.parFlatMap
import Function.compose
import data.List.{single}
import data.List
import data.Array
import data.Array.array
import Equal.{listEqual, stringEqual, arrayEqual}
import org.scalacheck.Properties

object CheckStrategy extends Properties("Strategy") {

  def rev = (x: String) => x.reverse: String
  def id[A] = (x: A) => x: A

  property("par") = forAll((a: P1[Int], s: Strategy[Int]) => a._1 == s.par(a)._1)

  property("parMapList") = forAll((a: List[String], s: Strategy[String]) =>
    listEqual(stringEqual).eq(s.parMap(rev, a)._1, a.map(compose(P1.__1[String], s.concurry[String](rev)))))

  property("parMapArray") = forAll((a: Array[String], s: Strategy[String]) =>
    arrayEqual(stringEqual).eq(s.parMap(rev, a)._1, a.map(compose(P1.__1[String], s.concurry[String](rev)))))

  property("parFlatMapList") = forAll((a: List[String], st: Strategy[List[String]]) => {
    def f = (x: String) => single[String](x)
    listEqual(stringEqual).eq(parFlatMap(st, f, a)._1, a.bind(compose(P1.__1[List[String]], st.concurry[String](f))))
  })

  property("parFlatMapArray") = forAll((a: Array[String], st: Strategy[Array[String]]) => {
    def f = (x: String) => array[String](scala.Array(x): _*)
    arrayEqual(stringEqual).eq(parFlatMap(st, f, a)._1, a.bind(compose(P1.__1[Array[String]], st.concurry[String](f))))
  })

  property("xmapID") = forAll((s: Strategy[Int], n: P1[Int]) =>
    s.xmap(id[P1[Int]], id[P1[Int]]).par(n)._1 == s.par(n)._1)

  property("xmapCompose") = forAll((a: Strategy[String], s: P1[String]) => {
    def f = (s: P1[String]) => P.p(s._1.toLowerCase): P1[String]
    def g = (s: P1[String]) => P.p(s._1.toUpperCase): P1[String]
    def fr = (s: P1[String]) => P.p(rev(s._1.toLowerCase)): P1[String]
    def gr = (s: P1[String]) => P.p(rev(s._1.toUpperCase)): P1[String]
    stringEqual.eq(a.xmap(f, g).xmap(fr, gr).par(s) _1, a.xmap(f.compose(fr), gr.compose(g)).par(s)._1)
  })
}
