package fj
package control
package parallel

import org.scalacheck.Arbitrary
import ParModule.parModule
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryParModule {
  implicit def arbitraryParModule(implicit s: Arbitrary[Strategy[fj.Unit]]): Arbitrary[ParModule] =
    Arbitrary(arbitrary[Strategy[fj.Unit]].map(x => parModule(x)))
}
