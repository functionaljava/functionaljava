package fj
package data
package optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import Function.identity

object SetterLaws {

  def apply[S: Arbitrary, A: Arbitrary](setter: PSetter[S, S, A, A])(implicit sEqual: Equal[S]) = new Properties("Setter") {

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      sEqual.eq(setter.set(a).f(setter.set(a).f(s)), setter.set(a).f(s))
    }

    /** modify does not change the number of targets */
    property("modify preserves the structure") = forAll { s: S =>
      sEqual.eq(setter.modify(identity()).f(s), s)
    }

  }

}