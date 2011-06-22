package fj

import org.scalacheck.Prop._
import fj.Monoid.doubleAdditionMonoid.{sum => dsum}
import fj.Monoid.intAdditionMonoid.{sum => isum}
import org.scalacheck.{Gen, Properties}
import org.scalacheck.Arbitrary

object CheckSemigroup extends Properties("Semigroup") {

  implicit def doubleGen: Arbitrary[Double] = Arbitrary(Gen.chooseNum(-1E10, 1E10))

  property("doubleAssociativity") = forAll {
    (x: Double, y: Double, z: Double) => {
      val a = dsum(dsum(x, y), z)
      val b = dsum(x, dsum(y, z))

      if (math.abs(a.doubleValue() - b.doubleValue()) <= 0.001) true else false
    }
  }

  property("intAssociativity") = forAll((x: Int, y: Int, z: Int) => isum(isum(x, y), z) == isum(x, isum(y, z)))
}
