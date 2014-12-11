package fj
package data

import ArbitraryAnyVal._
import ArbitraryEither._
import ArbitraryLens._
import ArbitraryOption._
import ArbitraryP._
import ArbitrarySet._
import ArbitraryTreeMap._
import EqualInstances._
import OrdInstances._
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

object CheckLens extends Properties("Lens") {

  property("associative") = forAll { (ab: Lens[Int, Int], bc: Lens[Int, Int], cd: Lens[Int, Int]) ⇒
    val ad1 = cd.compose(bc.compose(ab))
    val ad2 = cd.compose(bc).compose(ab)
    implicitly[Equal[Lens[Int, Int]]].eq(ad1, ad2)
  }

  property("left identity") = forAll { (ab: Lens[Int, Int]) ⇒
    val ab1 = ab.compose(Lens.id())
    implicitly[Equal[Lens[Int, Int]]].eq(ab, ab1)
  }

  property("left identity") = forAll { (ab: Lens[Int, Int]) ⇒
    val ab1 = Lens.id().compose(ab)
    implicitly[Equal[Lens[Int, Int]]].eq(ab, ab1)
  }

  include(LensLaws("id", Lens.id[Int]()))
  include(LensLaws("trivial", Lens.trivial[Int]()))
  include(LensLaws("codiag", Lens.codiag[Int]()))
  include(LensLaws("P2.first", Lens.first[Int, Int]()))
  include(LensLaws("P2.second", Lens.second[Int, Int]()))
  include(LensLaws("Map.member", Lens.mapValue[Boolean, Int](true)))
  include(LensLaws("Set.member", Lens.setMembership[Int](0)))
  include(LensLaws("sum", Lens.first[Int, String]().sum(Lens.first[Int, String]())))

  case class LensLaws[A, B](override val name: String, l: Lens[A, B])(implicit A: Arbitrary[A], B: Arbitrary[B], EA: Equal[A], EB: Equal[B]) extends Properties(name) {
    property("identity") = forAll((a: A) ⇒
      EA.eq(l.set(a, l.get(a)), a))

    property("retention") = forAll((a: A, b: B) ⇒
      EB.eq(l.get(l.set(a, b)), b))

    property("doubleSet") = forAll((a: A, b1: B, b2: B) ⇒
      EA.eq(l.set(l.set(a, b1), b2), l.set(a, b2)))
  }
}
