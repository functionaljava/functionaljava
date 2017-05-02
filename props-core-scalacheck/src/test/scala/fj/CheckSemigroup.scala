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
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

/**
  * Scalacheck [[Properties]] for [[Semigroup]] implementations.
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
  *
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

  include(SemigroupProperties(Semigroup.ioSemigroup[String](Semigroup.stringSemigroup), "ioSemigroup(Semigroup)"))

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
  include(SemigroupProperties(Semigroup.stringBufferSemigroup, "stringBufferSemigroup"))
  include(SemigroupProperties(Semigroup.stringBuilderSemigroup, "stringBuilderSemigroup"))


  include(SemigroupProperties(Semigroup.unitSemigroup, "unitSemigroup"))


}
