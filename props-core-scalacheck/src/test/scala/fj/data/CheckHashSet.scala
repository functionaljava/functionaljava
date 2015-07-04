package fj
package data

import org.scalacheck.Prop._
import ArbitraryHashSet._
import Equal.intEqual
import Hash.intHash
import org.scalacheck.Properties

object CheckHashSet extends Properties("List") {
  implicit val equalInt: Equal[Int] = intEqual contramap ((x: Int) => (x: java.lang.Integer))
  implicit val hashInt: Hash[Int] = intHash contramap ((x: Int) => (x: java.lang.Integer))

  property("eq") = forAll((s: HashSet[Int], x: Int, y: Int) => s.eq(x, y) == equalInt.eq(x, y))

  property("hash") = forAll((s: HashSet[Int], x: Int) => s.hash(x) == hashInt.hash(x))

  property("set") = forAll((s: HashSet[Int], x: Int) => {
    s.set(x)
    s.contains(x)
  })

  property("clear") = forAll((s: HashSet[Int], k: Int) => {
    s.clear
    !s.contains(k)
  })

  property("isEmpty") = forAll((s: HashSet[Int], k: Int) => !s.contains(k) || !s.isEmpty)

  property("size") = forAll((s: HashSet[Int], k: Int) => !s.contains(k) || s.size != 0)

  property("delete") = forAll((s: HashSet[Int], k: Int) => {
    s.delete(k)
    !s.contains(k)
  })
}
