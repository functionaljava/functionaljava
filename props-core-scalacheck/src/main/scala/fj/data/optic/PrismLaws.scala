package fj
package data
package optic

import org.scalacheck.Prop._
import org.scalacheck.{ Arbitrary, Properties }
import P.p1
import Function.{identity, constant}
import Equal.optionEqual
import Option.some

object PrismLaws {

  def apply[S: Arbitrary, A: Arbitrary](prism: PPrism[S, S, A, A])(implicit sEqual: Equal[S], aEqual: Equal[A]) = new Properties("Prism") {

    property("reverseGet produces a value") = forAll { a: A =>
      optionEqual(aEqual).eq(prism.getOption(prism.reverseGet(a)), some(a))
    }

    property("if a Prism match you can always go back to the source") = forAll { s: S =>
      sEqual.eq(prism.getOrModify(s).either(identity(), {a:A => prism.reverseGet(a)}), s)
    }

    /** modifyF does not change the number of targets */
    property("modifyF with Id does not do anything") = forAll { s: S =>
      sEqual.eq(prism.modifyP1F(p1()).f(s)._1(), s)
    }

    /** modify does not change the number of targets */
    property("modify with id does not do anything") = forAll { s: S =>
      sEqual.eq(prism.modify(identity()).f(s), s)
    }

    property("setOption only succeeds when the prism is matching") = forAll { (s: S, a: A) =>
      optionEqual(sEqual).eq(prism.setOption(a).f(s),  prism.getOption(s).map(constant(prism.set(a).f(s))))
    }

    property("modifyOption with id is isomorphomic to isMatching") = forAll { s: S =>
      optionEqual(sEqual).eq(prism.modifyOption(identity()).f(s),  prism.getOption(s).map(constant(s)))
    }

  }

}
