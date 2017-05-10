package fj

import fj.ArbitraryP._
import fj.ArbitraryUnit.arbitraryUnit
import fj.Ord.intOrd
import fj.data.ArbitraryArray.arbitraryArray
import fj.data.ArbitraryIO.arbitraryIO
import fj.data.ArbitraryList.arbitraryList
import fj.data.ArbitraryNatural.arbitraryNatural
import fj.data.ArbitraryNonEmptyList.arbitraryNonEmptyList
import fj.data.ArbitraryOption.arbitraryOption
import fj.data.ArbitrarySet.arbitrarySet
import fj.data.ArbitraryStream.arbitraryStream
import fj.data.IO
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

/**
  * Scalacheck [[Properties]] parameterized for [[Semigroup]] implementations.
  *
  * @param s         a Semigroup implementation.
  * @param desc      a description of the Semigroup implementation.
  * @param arbitrary a Scalacheck [[Arbitrary]] implementation for the Semigroup's type.
  * @tparam T the type to which the Semigroup applies.
  */
case class SemigroupProperties[T](s: Semigroup[T], desc: String)(implicit val arbitrary: Arbitrary[T]) extends Properties(desc) {


  property("sum(x,y)") = forAll((x: T, y: T, z: T) =>
    s.sum(s.sum(x, y), z) == s.sum(x, s.sum(y, z)))

  property("sum()") = forAll((x: T, y: T, z: T) => {
    val sf = s.sum()
    sf.f(sf.f(x).f(y)).f(z) == sf.f(x).f(sf.f(y).f(z))
  })

  property("dual()") = forAll((x: T, y: T, z: T) => {
    val sd = s.dual()
    sd.sum(sd.sum(x, y), z) == sd.sum(x, sd.sum(y, z))
  })

  property("sum(x)") = forAll((x: T, y: T) =>
    s.sum(x).f(y) == s.sum(x, y))

}

/**
  * A specialized Scalacheck [[Properties]] object for testing the [[Semigroup.ioSemigroup()]] method.
  */
object CheckIOSemigroup extends Properties("ioSemigroup") {

  val s = Semigroup.ioSemigroup(Semigroup.stringSemigroup)

  property("sum(x,y)") = forAll((x: IO[String], y: IO[String], z: IO[String]) =>
    s.sum(s.sum(x, y), z).run() == s.sum(x, s.sum(y, z)).run())

  property("sum()") = forAll((x: IO[String], y: IO[String], z: IO[String]) => {
    val sf = s.sum()
    sf.f(sf.f(x).f(y)).f(z).run() == sf.f(x).f(sf.f(y).f(z)).run()
  })

  property("dual()") = forAll((x: IO[String], y: IO[String], z: IO[String]) => {
    val sd = s.dual()
    sd.sum(sd.sum(x, y), z).run() == sd.sum(x, sd.sum(y, z)).run()
  })

  property("sum(x)") = forAll((x: IO[String], y: IO[String]) =>
    s.sum(x).f(y).run() == s.sum(x, y).run())

}

/**
  * A [[Properties]] implementation for testing [[Semigroup]] implementations that
  * apply to mutable builder style classes such as [[java.lang.StringBuilder]] and
  * [[java.lang.StringBuffer]].
  *
  * @param s          a Semigroup implementation to test.
  * @param desc       a description of the Semigroup implementation under test.
  * @param conversion a function that converts a value of type T to a new instance of type S.
  * @tparam T the type that the builder constructs.
  * @tparam S the type to which the Semigroup applies.
  */
case class CheckMutableBuilder[S, T](s: Semigroup[S], desc: String, conversion: T => S)(implicit val arbitrary: Arbitrary[T]) extends Properties(desc) {

  implicit def toBuilder(t: T): S = conversion(t)

  property("sum(x,y)") = forAll((x: T, y: T, z: T) =>
    s.sum(s.sum(x, y), z).toString == s.sum(x, s.sum(y, z)).toString
  )

  property("sum()") = forAll((x: T, y: T, z: T) => {
    val sf = s.sum()
    sf.f(sf.f(x).f(y)).f(z).toString == sf.f(x).f(sf.f(y).f(z)).toString
  })

  property("dual()") = forAll((x: T, y: T, z: T) => {
    val sd = s.dual()
    sd.sum(sd.sum(x, y), z).toString == sd.sum(x, sd.sum(y, z)).toString
  })

  property("sum(x)") = forAll((x: T, y: T) =>
    s.sum(x).f(y).toString == s.sum(x, y).toString)

}


/**
  * A Scalacheck [[Properties]] object that aggregates the tests for all [[Semigroup]] implementations.
  */
object CheckSemigroup extends Properties("Semigroup") {


  def idInt(n: Int): java.lang.Integer = n

  implicit def oi: Ord[Int] = intOrd.contramap(idInt _)

  implicit lazy val arbJavaBigDecimal: Arbitrary[java.math.BigDecimal] = Arbitrary(
    Arbitrary.arbLong.arbitrary.map(l => new java.math.BigDecimal(l))
  )

  implicit lazy val arbJavaBigInteger: Arbitrary[java.math.BigInteger] = Arbitrary(
    Arbitrary.arbBigInt.arbitrary.map(_.bigInteger)
  )

  implicit lazy val arbJavaInteger: Arbitrary[Integer] = Arbitrary(
    Arbitrary.arbInt.arbitrary.map(i => i)
  )

  implicit lazy val arbJavaLong: Arbitrary[java.lang.Long] = Arbitrary(
    Arbitrary.arbLong.arbitrary.map(i => i)
  )

  implicit lazy val arbJavaBoolean: Arbitrary[java.lang.Boolean] = Arbitrary(
    Arbitrary.arbBool.arbitrary.map(b => b)
  )

  implicit lazy val arbStringBuilder: Arbitrary[java.lang.StringBuilder] = Arbitrary(
    Arbitrary.arbString.arbitrary.map(new java.lang.StringBuilder(_))
  )

  implicit lazy val arbStringBuffer: Arbitrary[StringBuffer] = Arbitrary(
    Arbitrary.arbString.arbitrary.map(new StringBuffer(_))
  )

  include(SemigroupProperties(Semigroup.arraySemigroup[Int](), "arraySemigroup()"))

  include(SemigroupProperties(Semigroup.bigdecimalAdditionSemigroup, "bigdecimalAdditionSemigroup"))
  include(SemigroupProperties(Semigroup.bigdecimalMultiplicationSemigroup, "bigdecimalMultiplicationSemigroup"))
  include(SemigroupProperties(Semigroup.bigDecimalMaximumSemigroup, "bigDecimalMaximumSemigroup"))
  include(SemigroupProperties(Semigroup.bigDecimalMinimumSemigroup, "bigDecimalMinimumSemigroup"))

  include(SemigroupProperties(Semigroup.bigintAdditionSemigroup, "bigintAdditionSemigroup"))
  include(SemigroupProperties(Semigroup.bigintMultiplicationSemigroup, "bigintMultiplicationSemigroup"))
  include(SemigroupProperties(Semigroup.bigintMaximumSemigroup, "bigintMaximumSemigroup"))
  include(SemigroupProperties(Semigroup.bigintMinimumSemigroup, "bigintMinimumSemigroup"))

  include(SemigroupProperties(Semigroup.conjunctionSemigroup, "conjunctionSemigroup"))
  include(SemigroupProperties(Semigroup.disjunctionSemigroup, "disjunctionSemigroup"))
  include(SemigroupProperties(Semigroup.exclusiveDisjunctionSemiGroup, "exclusiveDisjunctionSemiGroup"))

  include(SemigroupProperties(Semigroup.firstOptionSemigroup[Int](), "firstOptionSemigroup()"))
  include(SemigroupProperties(Semigroup.firstSemigroup[Int](), "firstSemigroup()"))

  include(SemigroupProperties(Semigroup.intAdditionSemigroup, "intAdditionSemigroup"))
  include(SemigroupProperties(Semigroup.intMultiplicationSemigroup, "intMultiplicationSemigroup"))
  include(SemigroupProperties(Semigroup.intMaximumSemigroup, "intMaximumSemigroup"))
  include(SemigroupProperties(Semigroup.intMinimumSemigroup, "intMinimumSemigroup"))

  include(CheckIOSemigroup)

  include(SemigroupProperties(Semigroup.lastOptionSemigroup[Int](), "lastOptionSemigroup()"))
  include(SemigroupProperties(Semigroup.lastSemigroup[Int](), "lastSemigroup()"))

  include(SemigroupProperties(Semigroup.longAdditionSemigroup, "longAdditionSemigroup"))
  include(SemigroupProperties(Semigroup.longMultiplicationSemigroup, "longMultiplicationSemigroup"))
  include(SemigroupProperties(Semigroup.longMaximumSemigroup, "longMaximumSemigroup"))
  include(SemigroupProperties(Semigroup.longMinimumSemigroup, "longMinimumSemigroup"))


  include(SemigroupProperties(Semigroup.listSemigroup[Int], "listSemigroup"))

  include(SemigroupProperties(Semigroup.naturalAdditionSemigroup, "naturalAdditionSemigroup"))
  include(SemigroupProperties(Semigroup.naturalMaximumSemigroup, "naturalMaximumSemigroup"))
  include(SemigroupProperties(Semigroup.naturalMinimumSemigroup, "naturalMinimumSemigroup"))
  include(SemigroupProperties(Semigroup.naturalMultiplicationSemigroup, "naturalMultiplicationSemigroup"))

  include(SemigroupProperties(Semigroup.nonEmptyListSemigroup[Int], "nonEmptyListSemigroup"))

  include(SemigroupProperties(Semigroup.p1Semigroup(Semigroup.intAdditionSemigroup), "p1Semigroup(Semigroup<A>)"))
  include(SemigroupProperties(Semigroup.p2Semigroup(Semigroup.intAdditionSemigroup, Semigroup.stringSemigroup), "p2Semigroup(Semigroup<A>,Semigroup<B>)"))

  include(SemigroupProperties(Semigroup.semigroup[Int](new F2[Int, Int, Int] {
    def f(x: Int, y: Int): Int = x + y
  }), "semigroup(F<A,A,A>)"))

  include(SemigroupProperties(Semigroup.semigroup[Int](new F[Int, F[Int, Int]] {
    def f(x: Int): F[Int, Int] = (y: Int) => x + y
  }), "semigroup(F<A,F<A,A>>)"))

  include(SemigroupProperties(Semigroup.setSemigroup[Int](), "setSemigroup()"))
  include(SemigroupProperties(Semigroup.streamSemigroup[Int](), "streamSemigroup"))

  include(SemigroupProperties(Semigroup.stringSemigroup, "stringSemigroup"))
  include(CheckMutableBuilder(Semigroup.stringBufferSemigroup, "stringBufferSemigroup", (s: String) => new java.lang.StringBuffer(s)))
  include(CheckMutableBuilder(Semigroup.stringBuilderSemigroup, "stringBuilderSemigroup", (s: String) => new java.lang.StringBuilder(s)))


  include(SemigroupProperties(Semigroup.unitSemigroup, "unitSemigroup"))


}
