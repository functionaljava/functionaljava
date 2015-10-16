package fj
package data

import ArbitraryArray.arbitraryArray
import ArbitraryP.arbitraryP1
import org.scalacheck.Prop._
import data.Array.{array, empty, join}
import Equal.{arrayEqual, stringEqual}
import Unit.unit
import org.scalacheck.Properties

object CheckArray extends Properties("Array") {

  property("isEmpty") = forAll((a: Array[Int]) =>
    a.isEmpty != a.isNotEmpty)

  property("isNotEmpty") = forAll((a: Array[Int]) =>
    a.length > 0 ==> a.isNotEmpty)

  property("toOption") = forAll((a: Array[Int]) =>
    a.toOption.isNone || a.toOption.some == a.get(0))

  // crashes the type checker for unknown reason
  // property("toEither") = property((a: Array[Int], n: P1[Int]) =>
  //   (a.toEither(n).isLeft && a.toEither(n).left.value == n._1) || (a.toEither(n).right.value == a.get(0)))

  property("mapId") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a.map((x: String) => x), a))

  property("mapCompose") = forAll((a: Array[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    arrayEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  property("filter1") = forAll((a: Array[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)))

  property("filter2") = forAll((a: Array[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).length <= a.length)

  property("foreach") = forAll((a: Array[Int]) => {
    var i = 0
    a.foreach({
      (x: Int) => i = i + x
      unit
    })

    var j = 0

    for(x <- 0 until a.length)
      j = j + a.get(x)

    i == j
  })

  property("foldRight") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.foldRight((a: String, b: Array[String]) => array[String](scala.Array(a): _*).append(b), empty[String]), a))

  property("foldLeft") = forAll((a: Array[String], s: String) =>
    arrayEqual(stringEqual).eq(
      a.foldLeft(((a: Array[String], b: String) => array[String](scala.Array(b): _*).append(a)), empty[String]),
      a.reverse.foldRight((a: String, b: Array[String]) => array[String](scala.Array(a): _*).append(b), empty[String])))

  property("scans") = forAll((a: Array[Int], z: Int) => {
    val add = (x: Int, y: Int) => x + y
    val left = a.scanLeft(add, z)
    val right = a.reverse().scanRight(add, z).reverse()
    
    arrayEqual(Equal.anyEqual[Int]).eq(left, right)
  })

  property("scans1") = forAll((a: Array[Int]) =>
    (a.length() > 0) ==> {
      val add = (x: Int, y: Int) => x + y
      val left = a.scanLeft1(add)
      val right = a.reverse().scanRight1(add).reverse()
      
      arrayEqual(Equal.anyEqual[Int]).eq(left, right)
  })

  property("bindLeftIdentity") = forAll((a: Array[String], s: String) => {
    def f(s: String) = array[String](scala.Array(s.reverse): _*)
    arrayEqual(stringEqual).eq(
      array[String](scala.Array(s): _*).bind(f(_: String)),
      f(s))})

  property("bindRightIdentity") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.bind((x: String) => array[String](scala.Array(x): _*)),
      a))

  property("bindAssociativity") = forAll((a: Array[String]) => {
    def f(s: String) = array[String](scala.Array(s.reverse): _*)
    def g(s: String) = array[String](scala.Array(s.toUpperCase): _*)
    arrayEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  property("sequence") = forAll((a: Array[String], b: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  property("reverseIdentity") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(
      a.reverse.reverse,
      a))

  property("reverse") = forAll((a: Array[String], x: Byte) =>
    (a.length > 0) ==> {
      val n = math.abs(x) % a.length
      (a.reverse.get(n) == a.get(a.length - 1 - n))
    })

  property("appendLeftIdentity") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a.append(empty[String]), a))

  property("appendRightIdentity") = forAll((a: Array[String]) =>
    arrayEqual(stringEqual).eq(a, a.append(empty[String])))

  property("appendAssociativity") = forAll((a: Array[String], b: Array[String], c: Array[String]) =>
    arrayEqual(stringEqual).eq(a.append(b).append(c), a.append(b.append(c))))

  property("appendLength") = forAll((a: Array[String], b: Array[String]) =>
    a.append(b).length == a.length + b.length)

  property("array") = forAll((a: scala.Array[String], x: Byte) =>
    (a.length > 0) ==> {
      val n = math.abs(x) % a.length
      array[String](a: _*).length == a.length && array[String](a: _*).get(n) == a(n)
    })

  property("join") = forAll((a: Array[Array[String]]) =>
    arrayEqual(stringEqual).eq(
      a.foldRight((a: Array[String], b: Array[String]) => a.append(b), empty[String]),
      join(a)))

  property("forall") = forAll((a: Array[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("exists") = forAll((a: Array[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))
}
