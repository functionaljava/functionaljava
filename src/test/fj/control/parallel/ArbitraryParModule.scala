package fj
package control
package parallel

import org.scalacheck.Arbitrary
import ParModule.parModule
import org.scalacheck.Arbitrary.arbitrary

object ArbitraryParModule {
  implicit def arbitraryParModule(implicit s: Arbitrary[Strategy[scala.Unit]]): Arbitrary[ParModule] =
    Arbitrary(arbitrary[Strategy[scala.Unit]].map(x => parModule({
      x.xmap((a: P1[scala.Unit]) => new P1[Unit] {
        def _1 = {
          a._1
          fj.Unit.unit
        }
      }, (a: P1[Unit]) => new P1[scala.Unit] {
        def _1 = {
          a._1
          ()
        }
      })
    })))
}
