package fj.data

import org.scalacheck.Prop._
import ArbitraryHashMap._
import pre.Equal.{intEqual, stringEqual, optionEqual}
import pre.Hash.{intHash, stringHash}
import fjs.F._

object CheckHashMap {
  implicit val equalInt: pre.Equal[Int] = intEqual comap ((x: Int) => (x: java.lang.Integer))
  implicit val hashInt: pre.Hash[Int] = intHash comap ((x: Int) => (x: java.lang.Integer))

  val prop_eq = forAll((m: HashMap[Int, String], x: Int, y: Int) => m.eq(x, y) == equalInt.eq(x, y))

  val prop_hash = forAll((m: HashMap[Int, String], x: Int) => m.hash(x) == hashInt.hash(x))

  val prop_get = forAll((m: HashMap[Int, String], k: Int) => optionEqual(stringEqual).eq(m.get(k), m.get.f(k)))

  val prop_set = forAll((m: HashMap[Int, String], k: Int, v: String) => {
    m.set(k, v)
    m.get(k).some == v
  })

  val prop_clear = forAll((m: HashMap[Int, String], k: Int) => {
    m.clear
    m.get(k).isNone
  })

  val prop_contains = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isSome == m.contains(k))

  val prop_keys = forAll((m: HashMap[Int, String]) => m.keys.forall((k: Int) => (m.get(k).isSome): java.lang.Boolean))

  val prop_isEmpty = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isNone || !m.isEmpty)

  val prop_size = forAll((m: HashMap[Int, String], k: Int) => m.get(k).isNone || m.size != 0)

  val prop_delete = forAll((m: HashMap[Int, String], k: Int) => {
    m.delete(k)
    m.get(k).isNone
  })

  val prop_getDelete = forAll((m: HashMap[Int, String], k: Int) => {
    val x = m.get(k)
    val y = m.getDelete(k)
    val z = m.get(k)

    z.isNone && optionEqual(stringEqual).eq(x, y)
  })

  val tests = scala.List(
      ("prop_eq", prop_eq),
      ("prop_hash", prop_hash),
      ("prop_get", prop_get),
      ("prop_set", prop_set),
      ("prop_clear", prop_clear),
      ("prop_contains", prop_contains),
      ("prop_keys", prop_keys),
      ("prop_size", prop_size),
      ("prop_isEmpty", prop_isEmpty),
      ("prop_delete", prop_delete),
      ("prop_getDelete", prop_getDelete)
  ).map { case (n, p) => ("HashMap." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)
}
