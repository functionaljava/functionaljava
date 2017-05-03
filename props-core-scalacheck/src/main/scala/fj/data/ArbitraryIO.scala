package fj.data

import org.scalacheck.Arbitrary

/**
  *
  */
object ArbitraryIO {


  private case class ArbIO[T](value: T) extends IO[T] {
    override def run(): T = value
  }

  implicit def arbitraryIO[T](implicit arbT: Arbitrary[T]): Arbitrary[IO[T]] =
    Arbitrary(arbT.arbitrary.map(ArbIO(_)))

}
