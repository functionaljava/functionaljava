package fj
package data.optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import P.p1
import Function.identity

object LensLaws {

  def apply[S: Arbitrary, A: Arbitrary](lens: PLens[S, S, A, A])(implicit sEqual: Equal[S], aEqual: Equal[A]) = new Properties("Lens") {

    property("setting what you get does not do anything") = forAll { s: S =>
       sEqual.eq(lens.set(lens.get(s)).f(s), s)
    }
    
    property("you get what you set") = forAll { (s: S, a: A) =>
      aEqual.eq(lens.get(lens.set(a).f(s)), a)
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      sEqual.eq(lens.set(a).f(lens.set(a).f(s)), lens.set(a).f(s))
    }

    property("modifyF with Id does not do anything") = forAll { s: S =>
      sEqual.eq(lens.modifyP1F(p1()).f(s)._1(), s)
    }

    property("modify with id does not do anything") = forAll { s: S =>
      sEqual.eq(lens.modify(identity()).f(s), s)
    }

  }

}
