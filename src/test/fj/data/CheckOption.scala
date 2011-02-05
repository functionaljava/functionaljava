package fj
package data

import org.scalacheck.Prop._
import ArbitraryOption.arbitraryOption
import ArbitraryP.arbitraryP1
import Equal.{optionEqual, stringEqual}
import Unit.unit
import Option.{none, some, join}

object CheckOption {
  val prop_isNone = forAll((a: Option[Int]) =>
    a.isNone != a.isSome)

  val prop_orSome = forAll((a: Option[Int], n: P1[Int]) =>
    a.orSome(n) == (if(a.isNone) n._1 else a.some))

  val prop_mapId = forAll((a: Option[String]) =>
    optionEqual(stringEqual).eq(a.map((x: String) => x), a))

  val prop_mapCompose = forAll((a: Option[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    optionEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  val prop_foreach = forAll((a: Option[Int]) => {
    var i = 0
    a.foreach({
      (x: Int) => i = i + x
      unit
    })

    i == 0 || i == a.some
  })

  val prop_filter = forAll((a: Option[Int]) => {
      def f(x: Int): java.lang.Boolean = x % 2 == 0
      val x = a.filter(f(_: Int))
      x.isNone || f(a.some).booleanValue
    })

  val prop_bindLeftIdentity = forAll((a: Option[String], s: String) => {
    def f(s: String) = some[String](s.reverse)
    optionEqual(stringEqual).eq(
      some[String](s).bind(f(_: String)),
      f(s))})

  val prop_bindRightIdentity = forAll((a: Option[String]) =>
    optionEqual(stringEqual).eq(
      a.bind((x: String) => some[String](x)),
      a))

  val prop_bindAssociativity = forAll((a: Option[String]) => {
    def f(s: String) = some[String](s.reverse)
    def g(s: String) = some[String](s.toUpperCase)
    optionEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  val prop_sequence = forAll((a: Option[String], b: Option[String]) =>
    optionEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  val prop_toList = forAll((a: Option[String]) =>
    (a.isNone && a.toList.isEmpty) || (a.some == a.toList.head))

  val prop_forall = forAll((a: Option[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val prop_exists = forAll((a: Option[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val prop_join = forAll((a: Option[Option[String]]) =>
    a.isNone || optionEqual(stringEqual).eq(
      join(a),
      a.some))

  val tests = scala.List(
      ("prop_isNone", prop_isNone),
      ("prop_orSome", prop_orSome),
      ("prop_mapId", prop_mapId),
      ("prop_mapCompose", prop_mapCompose),
      ("prop_foreach", prop_foreach),
      ("prop_filter", prop_filter),
      ("prop_bindLeftIdentity", prop_bindLeftIdentity),
      ("prop_bindRightIdentity", prop_bindRightIdentity),
      ("prop_bindAssociativity", prop_bindAssociativity),
      ("prop_sequence", prop_sequence),
      ("prop_toList", prop_toList),
      ("prop_forall", prop_forall),
      ("prop_exists", prop_exists),
      ("prop_join", prop_join)
  ).map { case (n, p) => ("Option." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)
}
