package fj.data

import org.scalacheck.Arbitrary

/**
  *
  */
object ArbitraryIO {

  implicit def arbitraryIO[T](implicit arbT: Arbitrary[T]): Arbitrary[IO[T]] =
    Arbitrary(arbT.arbitrary.map(t => new IO[T]() {

      override def run(): T = t

      override def toString: String = t.toString

      override def hashCode(): Int = t.hashCode()

      override def equals(obj: scala.Any): Boolean = ???
    }))

}
