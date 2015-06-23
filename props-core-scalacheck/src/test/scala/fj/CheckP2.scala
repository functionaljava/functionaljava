package fj

import java.lang
import org.scalacheck.Prop._
import data.ArbitraryList._
import ArbitraryP.arbitraryP2
import ArbitraryUnit._
import P.p
import Unit.unit
import org.scalacheck.Properties
import fj.data.optic.PrismLaws
import fj.data.optic.OptionalLaws
import fj.data.optic.LensLaws

object CheckP2 extends Properties("P2") {
  
  property("Optic._1") = LensLaws[P2[String, Int], String](P2.Optic._1())
  
  property("Optic._2") = LensLaws[P2[String, Int], Int](P2.Optic._2())
  
}
