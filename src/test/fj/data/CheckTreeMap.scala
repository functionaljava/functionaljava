package fj.data

import pre.Ord.{intOrd, stringOrd}
import pre.Ord
import org.scalacheck.Prop._
import ArbitraryTreeMap._
import fjs.F._

object CheckTreeMap {
  def idInt(n: Int) = n:java.lang.Integer
  implicit def oi : Ord[Int] = intOrd.comap(idInt _)
  implicit def os : Ord[String] = stringOrd

  val prop_set = forAll((m: TreeMap[Int, String], k: Int, v: String) => m.set(k, v).get(k).some == v)
  val prop_updateId = forAll((m: TreeMap[Int, String], k: Int, v: String) => 
    m.set(k, v).update(k, (x: String) => x)._2.get(k).some == v)
  val prop_update = forAll((m: TreeMap[Int, String], k: Int, v: String, c: Char) =>
    m.set(k, v).update(k, (x: String) => c + x)._2.get(k).some.equals(c + v))

  val tests = scala.List(
      ("prop_set", prop_set),
      ("prop_updateId", prop_updateId),
      ("prop_update", prop_update)
  ).map { case (n, p) => ("TreeMap." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)

}
