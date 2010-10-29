package fj.control.parallel

import org.scalacheck.Arbitrary
import ParModule.parModule
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryParModule {
  implicit def arbitraryParModule(implicit s: Arbitrary[Strategy[Unit]]): Arbitrary[ParModule] = 
    Arbitrary(arbitrary[Strategy[Unit]].map((x) => parModule(x)))
}
