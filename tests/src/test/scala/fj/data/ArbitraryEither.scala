package fj
package data

import fj.data.Either.{left, right}
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._


object ArbitraryEither {
  implicit def arbitraryEither[A: Arbitrary, B: Arbitrary]: Arbitrary[Either[A, B]] =
    Arbitrary(sized(n ⇒ if (n == 0) arbitrary[A].map(left[A, B]) else resize(n - 1, arbitrary[B].map(right[A, B]))))
}
