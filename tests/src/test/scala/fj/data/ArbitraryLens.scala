package fj
package data

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.value

object ArbitraryLens {
  implicit def arbitratyLens: Arbitrary[Lens[Int, Int]] =
    Arbitrary(value(Lens.id[Int]()))
}
