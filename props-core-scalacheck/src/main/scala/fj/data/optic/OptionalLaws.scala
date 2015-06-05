package fj
package data.optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import P.p1
import Function.{identity, constant}
import Equal.optionEqual

object OptionalLaws {

  def apply[S: Arbitrary, A: Arbitrary](optional: POptional[S, S, A, A])(implicit sEqual: Equal[S], aEqual: Equal[A]) = new Properties("Optional") {

    property("setting what you get does not do anything") = forAll { s: S =>
      sEqual.eq(optional.getOrModify(s).either(identity(), {a:A => optional.set(a).f(s)}), s)
    }

    property("you get what you set") = forAll { (s: S, a: A) =>
       optionEqual(aEqual).eq(optional.getOption(optional.set(a).f(s)),  optional.getOption(s).map(constant(a)))
    }

    /** calling set twice is the same as calling set once */
    property("set is idempotent") = forAll { (s: S, a: A) =>
      sEqual.eq(optional.set(a).f(optional.set(a).f(s)), optional.set(a).f(s))
    }

    /** modifyF does not change the number of targets */
    property("modifyF with Id does not do anything") = forAll { s: S =>
      sEqual.eq(optional.modifyP1F(p1()).f(s)._1(), s)
    }

    /** modify does not change the number of targets */
    property("modify with id does not do anything") = forAll { s: S =>
      sEqual.eq(optional.modify(identity()).f(s), s)
    }

    property("setOption only succeeds when the Optional is matching") = forAll { (s: S, a: A) =>
      optionEqual(sEqual).eq(optional.setOption(a).f(s), optional.getOption(s).map(Function.constant(optional.set(a).f(s))))
    }

    property("modifyOption with id is isomorphomic to isMatching") = forAll { s: S =>
      optionEqual(sEqual).eq(optional.modifyOption(identity()).f(s),  optional.getOption(s).map(constant(s)))
    }

  }

}
