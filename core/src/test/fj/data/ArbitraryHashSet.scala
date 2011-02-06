package fj
package data

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryHashSet {
  implicit def arbitraryHashSet[A](implicit aa: Arbitrary[A], e: Equal[A], h: Hash[A]): Arbitrary[HashSet[A]] =
    Arbitrary(arbitrary[scala.List[A]].map(as => {
      val s = new HashSet[A](e, h)
      as.foreach(s.set(_))
      s
    }))

}
