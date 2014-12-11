package fj

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryP {
  implicit def arbitraryP1[A](implicit a: Arbitrary[A]): Arbitrary[P1[A]] =
    Arbitrary(arbitrary[A].map(a => new P1[A]{
      def _1 = a
    }))

  implicit def arbitraryP2[A: Arbitrary, B: Arbitrary]: Arbitrary[P2[A, B]] =
    Arbitrary(arbitrary[A].flatMap(a => arbitrary[B].map(b => new P2[A, B]{
      def _1 = a
      def _2 = b
    })))
}
