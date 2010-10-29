package fj.data

import org.scalacheck.Prop._
import ArbitraryTree.arbitraryTree
import fj.pre.Equal.{treeEqual, streamEqual, stringEqual, p1Equal}
import Tree.{root_, subForest_}
import Stream.{join}
import Implicit._
import fj.pre.Monoid.{intAdditionMonoid}
import fj.pre.Monoid

object CheckTree {

  val prop_root = forAll((a: Tree[Int]) =>
    a.root == root_[Int].f(a))

  val prop_subForest = forAll((a: Tree[Int]) =>
    a.subForest == subForest_[Int].f(a))

  val prop_flatten_foldmap = forAll((a: Tree[Int]) => {
    def const(f: Int) = 1:java.lang.Integer
    a.foldMap[java.lang.Integer](const _, intAdditionMonoid) == a.flatten.length})

  val prop_levels_length = forAll((a: Tree[Int]) =>
    a.flatten.length == join(a.levels).length)

  val prop_fmap_assoc = forAll((a: Tree[String]) => {
    def f(s: String) = s.toUpperCase
    streamEqual(stringEqual).eq(a.flatten.map(f _), a.fmap(f _).flatten)})

  /*val prop_unfoldTree = property((s: String, p: Tuple2[Int, List[String]]) => {
    def f(s: String) = p
    todo: Check unfold
  }*/

  val tests = scala.List(
      ("prop_root", prop_root),
      ("prop_subForest", prop_subForest),
      ("prop_flatten_foldmap", prop_flatten_foldmap),
      ("prop_levels_length", prop_levels_length),
      ("prop_fmap_assoc", prop_fmap_assoc)
  ).map { case (n, p) => ("Tree." + n, p) }

  def main(args: scala.Array[String]) = Tests.run(tests)  
}
