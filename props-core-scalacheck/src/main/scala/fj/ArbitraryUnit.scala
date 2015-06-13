package fj

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryUnit {
  implicit def arbitraryUnit: Arbitrary[Unit] =
    Arbitrary(Unit.unit())
}