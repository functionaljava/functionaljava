package fj.data

import org.scalacheck.Arbitrary

/**
  * A Scalacheck [[Arbitrary]] for [[Natural]].
  */
object ArbitraryNatural {

  implicit def arbitraryNatural: Arbitrary[Natural] =
    Arbitrary(Arbitrary.arbBigInt.arbitrary.map(_.abs).map(bi => Natural.natural(bi.bigInteger).some()))

}
