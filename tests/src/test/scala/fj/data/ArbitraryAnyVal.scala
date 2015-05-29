package fj
package data

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._

object ArbitraryAnyVal {
  implicit val arbitraryUnit: Arbitrary[Unit] =
    Arbitrary(value(Unit.unit()))

  implicit val arbitraryInteger: Arbitrary[java.lang.Integer] =
    Arbitrary(arbitrary[Int].map(x ⇒ x: java.lang.Integer))

  implicit val arbitraryBoolean: Arbitrary[java.lang.Boolean] =
    Arbitrary(arbitrary[Boolean].map(b => b: java.lang.Boolean))
}
