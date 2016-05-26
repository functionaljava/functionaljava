package fj
package data

import fj.data.optic.PrismLaws
import org.scalacheck.Prop._
import ArbitraryOption.arbitraryOption
import ArbitraryP.arbitraryP1
import ArbitraryUnit._
import Equal.{optionEqual, stringEqual}
import Unit.unit
import Option.{none, some, join}
import org.scalacheck.Properties

object CheckOption extends Properties("Option") {
  property("isNone") = forAll((a: Option[Int]) =>
    a.isNone != a.isSome)

  property("orSome") = forAll((a: Option[Int], n: P1[Int]) =>
    a.orSome(n) == (if(a.isNone) n._1 else a.some))

  property("hashCode") = forAll((a: Option[Int]) =>
    if(a.isNone) a.hashCode == none.hashCode else a.hashCode == some(a.some).hashCode)

  property("equals") = forAll((a: Option[Int]) =>
    if(a.isNone) a == none else a == some(a.some))

  property("mapId") = forAll((a: Option[String]) =>
    optionEqual(stringEqual).eq(a.map((x: String) => x), a))

  property("mapCompose") = forAll((a: Option[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    optionEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  property("foreach") = forAll((a: Option[Int]) => {
    var i = 0
    a.foreach({
      (x: Int) => i = i + x
      unit
    })

    i == 0 || i == a.some
  })

  property("filter") = forAll((a: Option[Int]) => {
      def f(x: Int): java.lang.Boolean = x % 2 == 0
      val x = a.filter(f(_: Int))
      x.isNone || f(a.some).booleanValue
    })

  property("bindLeftIdentity") = forAll((a: Option[String], s: String) => {
    def f(s: String) = some[String](s.reverse)
    optionEqual(stringEqual).eq(
      some[String](s).bind(f(_: String)),
      f(s))})

  property("bindRightIdentity") = forAll((a: Option[String]) =>
    optionEqual(stringEqual).eq(
      a.bind((x: String) => some[String](x)),
      a))

  property("bindAssociativity") = forAll((a: Option[String]) => {
    def f(s: String) = some[String](s.reverse)
    def g(s: String) = some[String](s.toUpperCase)
    optionEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  property("sequence") = forAll((a: Option[String], b: Option[String]) =>
    optionEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  property("toList") = forAll((a: Option[String]) =>
    (a.isNone && a.toList.isEmpty) || (a.some == a.toList.head))

  property("forall") = forAll((a: Option[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("exists") = forAll((a: Option[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("join") = forAll((a: Option[Option[String]]) =>
    a.isNone || optionEqual(stringEqual).eq(
      join(a),
      a.some))

  property("Optic.pSome") = PrismLaws[Option[String], String](Option.Optic.pSome())

  property("Optic.some") = PrismLaws[Option[String], String](Option.Optic.some())

  property("Optic.none") = PrismLaws[Option[Unit], Unit](Option.Optic.none())
}
