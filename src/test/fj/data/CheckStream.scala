package fj
package data

import control.parallel.Strategy
import org.scalacheck.Prop._
import ArbitraryStream.arbitraryStream
import control.parallel.ArbitraryStrategy.arbitraryStrategy
import ArbitraryP.arbitraryP1
import Equal.{streamEqual, stringEqual}
import Unit.unit
import Stream.{nil, single, join, iterableStream}
import Ord.stringOrd

object CheckStream {
  val prop_isEmpty = forAll((a: Stream[Int]) =>
    a.isEmpty != a.isNotEmpty)

  val prop_isNotEmpty = forAll((a: Stream[Int]) =>
    a.length > 0 ==> a.isNotEmpty)

  val prop_orHead = forAll((a: Stream[Int], n: P1[Int]) =>
    a.isNotEmpty ==>
    (a.orHead(n) == a.head))

  val prop_orTail = forAll((a: Stream[String], n: P1[Stream[String]]) =>
    a.isNotEmpty ==>
    (streamEqual(stringEqual).eq(a.orTail(n)._1, a.tail._1)))

  val prop_toOption = forAll((a: Stream[Int]) =>
    a.toOption.isNone || a.toOption.some == a.head)

  val prop_toEither = forAll((a: Stream[Int], n: P1[Int]) =>
    (a.toEither(n).isLeft && a.toEither(n).left.value == n._1) || (a.toEither(n).right.value == a.head))

  val prop_cons1 = forAll((a: Stream[Int], n: Int) =>
    a.cons(n).head == n)

  val prop_cons2 = forAll((a: Stream[Int], n: Int) =>
    a.cons(n).length == a.length + 1)

  val prop_mapId = forAll((a: Stream[String]) =>
    streamEqual(stringEqual).eq(a.map((x: String) => x), a))

  val prop_mapCompose = forAll((a: Stream[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    streamEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  val prop_foreach = forAll((a: Stream[Int]) => {
    var i = 0
    a.foreach({
      (x: Int) => i = i + x
      unit
    })
  
    var j = 0
  
    val aa = a.toArray
  
    for(x <- 0 until aa.length)
       j = j + aa.get(x)
  
    i == j
  })

  val prop_filter1 = forAll((a: Stream[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)))

  val prop_filter2 = forAll((a: Stream[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).length <= a.length)

  val prop_bindLeftIdentity = forAll((a: Stream[String], s: String) => {
    def f(s: String) = single[String](s.reverse)
    streamEqual(stringEqual).eq(
      single[String](s).bind(f(_: String)),
      f(s))})

  val prop_bindRightIdentity = forAll((a: Stream[String]) =>
    streamEqual(stringEqual).eq(
      a.bind((x: String) => single[String](x)),
      a))

  val prop_bindAssociativity = forAll((a: Stream[String]) => {
    def f(s: String) = single[String](s.reverse)
    def g(s: String) = single[String](s.toUpperCase)
    streamEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  val prop_sequence = forAll((a: Stream[String], b: Stream[String]) =>
    streamEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  val prop_append = forAll((a: Stream[String], b: String) =>
    streamEqual(stringEqual).eq(
      single(b).append(a),
      a.cons(b)))

  val prop_foldRight = forAll((a: Stream[String]) => streamEqual(stringEqual).eq(
      a.foldRight((a: String, b: P1[Stream[String]]) => b._1.cons(a), nil[String]), a))
                                     
  val prop_foldLeft = forAll((a: Stream[String], s: String) =>
    streamEqual(stringEqual).eq(
      a.foldLeft(((a: Stream[String], b: String) => single(b).append(a)), nil[String]),
      a.reverse.foldRight((a: String, b: P1[Stream[String]]) => single(a).append(b._1), nil[String])))

  val prop_length = forAll((a: Stream[String]) =>
    a.length != 0 ==>
    (a.length - 1 == a.tail._1.length))

  val prop_reverse = forAll((a: Stream[String], b: Stream[String]) =>
    streamEqual(stringEqual).eq(
      (a append b).reverse,
      b.reverse.append(a.reverse)))

  val prop_index = forAll((a: Stream[String], n: Int) =>
    (n > 0 && n < a.length) ==>
    (a.index(n) == a.tail._1.index(n - 1)))

  val prop_forall = forAll((a: Stream[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val prop_exists = forAll((a: Stream[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  val prop_find = forAll((a: Stream[Int]) => {
    val s = a.find((x: Int) => (x % 2 == 0): java.lang.Boolean)
    s.forall((x: Int) => (x % 2 == 0): java.lang.Boolean)
  })

  val prop_join = forAll((a: Stream[Stream[String]]) =>
    streamEqual(stringEqual).eq(
      a.foldRight((a: Stream[String], b: P1[Stream[String]]) => a.append(b._1), nil[String]),
      join(a)))

  val prop_sort = forAll((a: Stream[String]) => {
    val d = a.sort(stringOrd)
    val e = streamEqual(stringEqual)
    e.eq(d, a.toList.sort(stringOrd).toStream)
  })

  val prop_parallel_sort = forAll((a: Stream[String], s: Strategy[Unit]) => {
    val d = a.sort(stringOrd, s)
    val e = streamEqual(stringEqual)
    e.eq(d, a.sort(stringOrd))
  })

  val prop_iterable = forAll((a: Stream[String]) => {
    val e = streamEqual(stringEqual)
    e.eq(a, iterableStream(a))
  })

  val tests = scala.List(
      ("prop_isEmpty", prop_isEmpty),
      ("prop_isNotEmpty", prop_isNotEmpty),
      ("prop_orHead", prop_orHead),
      ("prop_toOption", prop_toOption),
      ("prop_toEither", prop_toEither),
      ("prop_cons1", prop_cons1),
      ("prop_cons2", prop_cons2),
      ("prop_mapId", prop_mapId),
      ("prop_mapCompose", prop_mapCompose),
      ("prop_foreach", prop_foreach),
      ("prop_filter1", prop_filter1),
      ("prop_filter2", prop_filter2),
      ("prop_bindLeftIdentity", prop_bindLeftIdentity),
      ("prop_bindRightIdentity", prop_bindRightIdentity),
      ("prop_bindAssociativity", prop_bindAssociativity),
      ("prop_sequence", prop_sequence),
      ("prop_append", prop_append),
      ("prop_foldRight", prop_foldRight),
      ("prop_foldLeft", prop_foldLeft),
      ("prop_length", prop_length),
      ("prop_reverse", prop_reverse),
      ("prop_index", prop_index),
      ("prop_forall", prop_forall),
      ("prop_exists", prop_exists),
      ("prop_find", prop_find),
      ("prop_join", prop_join),
      ("prop_sort", prop_sort),
      // TODO Reinstate: java.lang.NullPointerException at fj.data.Stream$21.f(Stream.java:841)
      // ("prop_parallel_sort", prop_parallel_sort),
      ("prop_iterable", prop_iterable)
  ).map { case (n, p) => ("Stream." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)
}

