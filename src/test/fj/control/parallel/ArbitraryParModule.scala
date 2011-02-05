package fj.control.parallel

import org.scalacheck.Arbitrary
import ParModule.parModule
import org.scalacheck.Arbitrary.arbitrary
import fj.P1
import fj.Implicit._

object ArbitraryParModule {
  implicit def arbitraryParModule(implicit s: Arbitrary[Strategy[Unit]]): Arbitrary[ParModule] =
    Arbitrary(arbitrary[Strategy[Unit]].map(x => parModule({
      x.xmap((a: P1[Unit]) => new P1[fj.Unit] {
        def _1 = {
          a._1
          fj.Unit.unit
        }
      }, (a: P1[fj.Unit]) => new P1[Unit] {
        def _1 = {
          a._1
          ()
        }
      })
    })))
}
