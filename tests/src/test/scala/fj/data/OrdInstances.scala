package fj
package data

object OrdInstances {

  implicit val ordBoolean: Ord[Boolean] =
    Ord.booleanOrd.comap[Boolean]((b: Boolean) ⇒ b: java.lang.Boolean)

  implicit val ordInt: Ord[Int] =
    Ord.intOrd.comap[Int]((i: Int) ⇒ i: java.lang.Integer)
}
