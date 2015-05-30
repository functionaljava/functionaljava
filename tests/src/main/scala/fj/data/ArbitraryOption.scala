package fj
package data

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{const, sized, resize}
import Option.{none, some}

object ArbitraryOption {
  implicit def arbitraryOption[A](implicit a: Arbitrary[A]): Arbitrary[Option[A]] =
    Arbitrary(sized(n => if(n == 0) const(none[A]) else resize(n - 1, arbitrary[A]).map(some(_))))
}
