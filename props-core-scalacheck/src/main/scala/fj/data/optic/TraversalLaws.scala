package fj
package data
package optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import P.p1
import Function.{identity, constant}
import Equal.{optionEqual, listEqual}
import Option.some

object TraversalLaws {

  def apply[S: Arbitrary, A: Arbitrary](traversal: PTraversal[S, S, A, A])(implicit sEqual: Equal[S], aEqual: Equal[A]) = new Properties("Traversal") {

    /** set does not change the number of targets */
    property("you get what you set") = forAll { (s: S, a: A) =>
      listEqual(aEqual).eq(traversal.getAll(traversal.set(a).f(s)), traversal.getAll(s).map(constant(a)))
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      sEqual.eq(traversal.set(a).f(traversal.set(a).f(s)), traversal.set(a).f(s))
    }

    /** modifyF does not change the number of targets */
    property("modifyF preserves the structure") = forAll { s: S =>
      sEqual.eq(traversal.modifyP1F(p1()).f(s)._1(), s)
    }

    /** modify does not change the number of targets */
    property("modify preserves the structure") = forAll { s: S =>
      sEqual.eq(traversal.modify(identity()).f(s), s)
    }

    property("headMaybe returns the first element of getAll (if getAll is finite)") = forAll { s: S =>
      optionEqual(aEqual).eq(traversal.headOption(s), traversal.getAll(s).headOption())
    }

  }

}
