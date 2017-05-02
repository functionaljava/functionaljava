package fj.data

import fj.data.NonEmptyList.nel
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

/**
  * A Scalacheck [[Arbitrary]] for [[NonEmptyList]].
  */
object ArbitraryNonEmptyList {
  implicit def arbitraryNonEmptyList[A](implicit a: Arbitrary[A]): Arbitrary[NonEmptyList[A]] =
    Arbitrary(nelOf(arbitrary[A]))

  def nelOf[A](g: => Gen[A]): Gen[NonEmptyList[A]] =
    Gen.nonEmptyListOf(g).map(l => l.tail.foldRight(nel(l.head))((x, n) => n.cons(x)))
}
