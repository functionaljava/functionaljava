package fj
package data

import Ord.{intOrd, stringOrd}
import org.scalacheck.Prop._
import ArbitraryTreeMap._
import org.scalacheck.Properties

object CheckTreeMap extends Properties("TreeMap") {
  def idInt(n: Int) = n:java.lang.Integer
  implicit def oi : Ord[Int] = intOrd.comap(idInt _)
  implicit def os : Ord[String] = stringOrd

  property("set") = forAll((m: TreeMap[Int, String], k: Int, v: String) => m.set(k, v).get(k).some == v)

  property("updateId") = forAll((m: TreeMap[Int, String], k: Int, v: String) =>
    m.set(k, v).update(k, (x: String) => x)._2.get(k).some == v)

  property("update") = forAll((m: TreeMap[Int, String], k: Int, v: String, c: Char) =>
    m.set(k, v).update(k, (x: String) => c + x)._2.get(k).some.equals(c + v))
}
