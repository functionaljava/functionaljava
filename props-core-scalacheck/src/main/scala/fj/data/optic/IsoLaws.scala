package fj
package data.optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import P.p1
import Function.identity

object IsoLaws {

  def apply[S: Arbitrary, A: Arbitrary](iso: PIso[S, S, A, A])(implicit sEqual: Equal[S], aEqual: Equal[A]) = new Properties("Iso") {

    property("get and reverseGet forms an Isomorphism") = forAll { (s: S, a: A) =>
      sEqual.eq(iso.reverseGet(iso.get(s)), s)
      aEqual.eq(iso.get(iso.reverseGet(a)), a)
    }

    property("set is a weaker version of reverseGet") = forAll { (s: S, a: A) =>
      sEqual.eq(iso.set(a).f(s), iso.reverseGet(a))
    }

    property("modifyF with Id does not do anything") = forAll { s: S =>
      sEqual.eq(iso.modifyP1F(p1()).f(s)._1(), s)
    }

    property("modify with id does not do anything") = forAll { s: S =>
      sEqual.eq(iso.modify(identity()).f(s), s)
    }

  }

}
