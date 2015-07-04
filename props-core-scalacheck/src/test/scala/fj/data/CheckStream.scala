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
import org.scalacheck.{Gen, Properties}
import collection.JavaConversions

object CheckStream extends Properties("Stream") {
  property("isEmpty") = forAll((a: Stream[Int]) =>
    a.isEmpty != a.isNotEmpty)

  property("isNotEmpty") = forAll((a: Stream[Int]) =>
    a.length > 0 ==> a.isNotEmpty)

  property("orHead") = forAll((a: Stream[Int], n: P1[Int]) =>
    a.isNotEmpty ==>
    (a.orHead(n) == a.head))

  property("orTail") = forAll((a: Stream[String], n: P1[Stream[String]]) =>
    a.isNotEmpty ==>
    (streamEqual(stringEqual).eq(a.orTail(n)._1, a.tail._1)))

  property("toOption") = forAll((a: Stream[Int]) =>
    a.toOption.isNone || a.toOption.some == a.head)

  property("toEither") = forAll((a: Stream[Int], n: P1[Int]) =>
    (a.toEither(n).isLeft && a.toEither(n).left.value == n._1) || (a.toEither(n).right.value == a.head))

  property("cons1") = forAll((a: Stream[Int], n: Int) =>
    a.cons(n).head == n)

  property("cons2") = forAll((a: Stream[Int], n: Int) =>
    a.cons(n).length == a.length + 1)

  property("mapId") = forAll((a: Stream[String]) =>
    streamEqual(stringEqual).eq(a.map((x: String) => x), a))

  property("mapCompose") = forAll((a: Stream[String]) => {
    def f(s: String) = s.toLowerCase
    def g(s: String) = s.toUpperCase
    streamEqual(stringEqual).eq(a.map((x: String) => f(g(x))), a.map((x: String) => g(x)).map((x: String) => f(x)))})

  val length = Gen.choose(0, 5000)

  property("bindStackOverflow") = forAll(length)(size => {
    val stream = Stream.range(1, size + 1)
    val bound: Stream[Integer] = stream.bind(new F[Integer, Stream[Integer]] {
      def f(a: Integer) = single(a)
    })
    val zipped = stream.zip(bound)
    val allmatch = zipped.forall(new F[P2[Integer, Integer], java.lang.Boolean] {
      def f(a: P2[Integer, Integer]) = a._1() == a._2()
    })
    allmatch && zipped.length() == size
  })

  property("foreach") = forAll((a: Stream[Int]) => {
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

  property("filter1") = forAll((a: Stream[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)))

  property("filter2") = forAll((a: Stream[Int]) =>
    a.filter((x: Int) => ((x % 2 == 0): java.lang.Boolean)).length <= a.length)

  property("bindLeftIdentity") = forAll((a: Stream[String], s: String) => {
    def f(s: String) = single[String](s.reverse)
    streamEqual(stringEqual).eq(
      single[String](s).bind(f(_: String)),
      f(s))})

  property("bindRightIdentity") = forAll((a: Stream[String]) =>
    streamEqual(stringEqual).eq(
      a.bind((x: String) => single[String](x)),
      a))

  property("bindAssociativity") = forAll((a: Stream[String]) => {
    def f(s: String) = single[String](s.reverse)
    def g(s: String) = single[String](s.toUpperCase)
    streamEqual(stringEqual).eq(
      a.bind(f(_: String)).bind(g(_: String)),
      a.bind(f(_: String).bind(g(_: String))))})

  property("sequence") = forAll((a: Stream[String], b: Stream[String]) =>
    streamEqual(stringEqual).eq(
      a.sequence(b),
      a.bind((x: String) => b)))

  property("append") = forAll((a: Stream[String], b: String) =>
    streamEqual(stringEqual).eq(
      single(b).append(a),
      a.cons(b)))

  property("foldRight") = forAll((a: Stream[String]) => streamEqual(stringEqual).eq(
      a.foldRight((a: String, b: P1[Stream[String]]) => b._1.cons(a), nil[String]), a))
                                     
  property("foldLeft") = forAll((a: Stream[String], s: String) =>
    streamEqual(stringEqual).eq(
      a.foldLeft(((a: Stream[String], b: String) => single(b).append(a)), nil[String]),
      a.reverse.foldRight((a: String, b: P1[Stream[String]]) => single(a).append(b._1), nil[String])))

  property("length") = forAll((a: Stream[String]) =>
    a.length != 0 ==>
    (a.length - 1 == a.tail._1.length))

  property("reverse") = forAll((a: Stream[String], b: Stream[String]) =>
    streamEqual(stringEqual).eq(
      (a append b).reverse,
      b.reverse.append(a.reverse)))

  property("index") = forAll((a: Stream[String], x: Byte) =>
    (a.length > 0) ==> {
      val n = math.abs(x) % a.length + 1
      (n < a.length) ==> (a.index(n) == a.tail._1.index(n - 1))
    })

  property("forall") = forAll((a: Stream[Int]) =>
    a.forall((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.exists((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("exists") = forAll((a: Stream[Int]) =>
    a.exists((x: Int) => ((x % 2 == 0): java.lang.Boolean)) ==
    !a.forall((x: Int) => ((x % 2 != 0): java.lang.Boolean)))

  property("find") = forAll((a: Stream[Int]) => {
    val s = a.find((x: Int) => (x % 2 == 0): java.lang.Boolean)
    s.forall((x: Int) => (x % 2 == 0): java.lang.Boolean)
  })

  property("join") = forAll((a: Stream[Stream[String]]) =>
    streamEqual(stringEqual).eq(
      a.foldRight((a: Stream[String], b: P1[Stream[String]]) => a.append(b._1), nil[String]),
      join(a)))
                  /*
  property("sort") = forAll((a: Stream[String]) => {
    val d = a.sort(stringOrd)
    val e = streamEqual(stringEqual)
    e.eq(d, a.toList.sort(stringOrd).toStream)
  })

  // TODO Broken: java.lang.NullPointerException at fj.data.Stream$21.f(Stream.java:841)
  property("parallel_sort") = forAll((a: Stream[String], s: Strategy[Unit]) => {
    val d = a.sort(stringOrd, s)
    val e = streamEqual(stringEqual)
    e.eq(d, a.sort(stringOrd))
  })

  property("iterable") = forAll((a: Stream[String]) => {
    val e = streamEqual(stringEqual)
    e.eq(a, iterableStream(a))
  })
  */
}

