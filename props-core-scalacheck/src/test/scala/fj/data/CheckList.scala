package fj
package data

import java.lang
import fj.Monoid
import org.scalacheck.Prop._
import ArbitraryList._
import ArbitraryP._
import ArbitraryUnit._
import Equal.{listEqual, stringEqual, p2Equal}
import P.p
import Unit.unit
import List.{nil, single, join, iterateWhile}
import org.scalacheck.Properties
import fj.data.optic.PrismLaws
import fj.data.optic.OptionalLaws
import fj.data.optic.TraversalLaws
import fj.data.optic.Traversal

object CheckList extends Properties("List") {
  property("isEmpty") = forAll((a: List[Int]) =>
    a.isEmpty != a.isNotEmpty)

  property("isNotEmpty") = forAll((a: List[Int]) =>
    a.length > 0 ==> a.isNotEmpty)

  property("orHead") = forAll((a: List[Int], n: P1[Int]) =>
    a.isNotEmpty ==>
    (a.orHead(n) == a.head))

  property("orTail") = forAll((a: List[String], n: P1[List[String]]) =>
    a.isNotEmpty ==>
    (listEqual(stringEqual).eq(a.orTail(n), a.tail)))

  property("toOption") = forAll((a: List[Int]) =>
    a.headOption.isNone || a.headOption.some == a.head)

  // crashes the type checker for unknown reason
  // property("toEither") = property((a: List[Int], n: P1[Int]) =>
  //   (a.toEither(n).isLeft && a.toEither(n).left.value == n._1) || (a.toEither(n).right.value == a.head))

  property("cons1") = forAll((a: List[Int], n: Int) =>
    a.cons(n).head == n)

  property("cons2") = forAll((a: List[Int], n: Int) =>
    a.cons(n).length == a.length + 1)

  property("mapId") = forAll((a: List[String]) =>
    listEqual(stringEqual).eq(a.map((x: String) => x), a))

  property("mapCompose") = forAll((a: List[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    listEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  // crashes the type checker for unknown reason
  // property("foreach") = property((a: List[Int]) => {
  //   var i = 0
  //   a.foreach({
  //     (x: Int) => i = i + x
  //     unit
  //   })
  //
  //   var j = 0
  //
  //   val aa = a.toArray
  //
  //   for(x <- 0 until aa.length)
  //     j = j + aa.get(x)
  //
  //   i == j
  // })

  property("filter1") = forAll((a: List[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)))

  property("filter2") = forAll((a: List[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).length <= a.length)

  property("bindLeftIdentity") = forAll((s: String) => {
    def f(s: String) = single[String](s.reverse)
    listEqual(stringEqual).eq(
      single[String](s).bind(f(_: String)),
      f(s))})

  property("bindRightIdentity") = forAll((a: List[String]) =>
    listEqual(stringEqual).eq(
      a.bind((x: String) => single[String](x)),
      a))

  property("bindAssociativity") = forAll((a: List[String]) => {
    def f(s: String) = single[String](s.reverse)
    def g(s: String) = single[String](s.toUpperCase)
    listEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  property("sequence") = forAll((a: List[String], b: List[String]) =>
    listEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  property("append") = forAll((a: List[String], b: String) =>
    listEqual(stringEqual).eq(
      single(b).append(a),
      a.cons(b)))

  property("foldRight") = forAll((a: List[String]) => listEqual(stringEqual).eq(
      a.foldRight((a: String, b: List[String]) => b.cons(a), nil[String]), a))

  property("foldLeft") = forAll((a: List[String], s: String) =>
    listEqual(stringEqual).eq(
      a.foldLeft(((a: List[String], b: String) => single(b).append(a)), nil[String]),
      a.reverse.foldRight((a: String, b: List[String]) => single(a).append(b), nil[String])))

  property("length") = forAll((a: List[String]) =>
    a.length != 0 ==>
    (a.length - 1 == a.tail.length))

  property("reverse") = forAll((a: List[String], b: List[String]) =>
    listEqual(stringEqual).eq(
      (a append b).reverse,
      b.reverse.append(a.reverse)))

  property("index") = forAll((a: List[String], x: Byte) =>
    (a.length > 0) ==> {
      val n = math.abs(x) % a.length + 1
      (n < a.length) ==> (a.index(n) == a.tail.index(n - 1))
    })

  property("snoc") = forAll((a: List[String], s: String) =>
    listEqual(stringEqual).eq(
      a.snoc(s),
      a.append(single(s))))

  property("take") = forAll((a: List[String], n: Int) =>
    a.take(n).length() <= a.length())

  property("drop") = forAll((a: List[String], n: Int) =>
    a.drop(n).length() <= a.length())

  property("splitAt") = forAll((a: List[String], n: Int) =>
    p2Equal(listEqual(stringEqual), listEqual(stringEqual)).eq(
      a.splitAt(n),
      p(a.take(n), a.drop(n))))

  property("partition") = forAll((a: List[String], n: Int) => (a.length > 0 && n > 0) ==>
    {
      val as = a.partition(n)
      listEqual(stringEqual).eq(join(as), a) && as.forall((x: List[String]) => (x.length <= n):java.lang.Boolean)
    })

  property("inits") = forAll((a: List[String]) =>
    a.isEmpty || a.inits.length == a.length + 1 && join(a.inits).length == 1.to(a.length).foldLeft(0)(_+_))

  property("tails") = forAll((a: List[String]) =>
    a.isEmpty || a.tails.length == a.length + 1 && join(a.tails).length == 1.to(a.length).foldLeft(0)(_+_))

  property("sort") = forAll((a: List[String]) =>
    {
      val s = a.sort(fj.Ord.stringOrd)
      s.isEmpty || s.tail.isEmpty || s.head.compareTo(s.tail.head) <= 0
    })

  property("forall") = forAll((a: List[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("exists") = forAll((a: List[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("find") = forAll((a: List[Int]) => {
    val s = a.find((x: Int) => (x % 2 == 0): java.lang.Boolean)
    s.forall((x: Int) => (x % 2 == 0): java.lang.Boolean)
  })

  property("nub") = forAll((a: List[String], b: List[String]) =>
    listEqual(stringEqual).eq(a append b nub, a.nub.append(b.nub).nub))

  property("init") = forAll((a: List[String], b: String) =>
    listEqual(stringEqual).eq(a.snoc(b).init(), a))

  property("join") = forAll((a: List[List[String]]) =>
    listEqual(stringEqual).eq(
      a.foldRight((a: List[String], b: List[String]) => a.append(b), nil[String]),
      join(a)))

  property("groupBy") = forAll((a: List[Int]) => {
    val result = a.groupBy((x: Int) => (x % 2 == 0): lang.Boolean, Ord.booleanOrd)
    result.get(true).forall((xs: List[Int]) => xs.forall((x: Int) => (x % 2 == 0): lang.Boolean): lang.Boolean) &&
      result.get(false).forall((xs: List[Int]) => xs.forall((x: Int) => (x % 2 != 0): lang.Boolean): lang.Boolean) &&
      a.map((x: Int) => (x % 2) == 0: lang.Boolean).nub().length() == result.size()
  })

  property("groupByMonoid") = forAll((a: List[Int]) => {
    val result = a.groupBy((x: Int) => (x % 2 == 0): lang.Boolean, (x: Int) => x: lang.Integer, Monoid.intAdditionMonoid, Ord.booleanOrd)
    result.get(true).forall((x: lang.Integer) =>
      x == a.filter((x: Int) => (x % 2 == 0): lang.Boolean).
        map((x: Int) => x:lang.Integer).
        foldLeft(Function.uncurryF2[lang.Integer, lang.Integer, lang.Integer](Monoid.intAdditionMonoid.sum), Monoid.intAdditionMonoid.zero()): lang.Boolean) &&
    result.get(false).forall((x: lang.Integer) =>
      x == a.filter((x: Int) => (x % 2 != 0): lang.Boolean).
        map((x: Int) => x:lang.Integer).
        foldLeft(Function.uncurryF2[lang.Integer, lang.Integer, lang.Integer](Monoid.intAdditionMonoid.sum), Monoid.intAdditionMonoid.zero()): lang.Boolean)
  })
  
  
  property("Optic.nil") = PrismLaws[List[String], Unit](List.Optic.nil())
  
  property("Optic.cons") = PrismLaws[List[String], P2[String, List[String]]](List.Optic.cons())
  
  property("Optic.head") = OptionalLaws[List[String], String](List.Optic.head())
  
  property("Optic.tail") = OptionalLaws[List[String], List[String]](List.Optic.tail())
  
  property("Optic.traversal") = TraversalLaws[List[String], String](List.Optic.traversal())
  
  
  /*property("iterateWhile") = forAll((n: Int) => n > 0 ==>
    (iterateWhile(((x:Int) => x - 1), ((x:Int) => ((x > 0): java.lang.Boolean)), n).length == n))*/
}
