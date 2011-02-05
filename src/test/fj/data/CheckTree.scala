package fj
package data

import org.scalacheck.Prop._
import ArbitraryTree.arbitraryTree
import Equal.{treeEqual, streamEqual, stringEqual, p1Equal}
import Tree.{root_, subForest_}
import Stream.{join}
import Monoid.{intAdditionMonoid}
import org.scalacheck.Properties

object CheckTree extends Properties("Tree") {

  property("root") = forAll((a: Tree[Int]) =>
    a.root == root_[Int].f(a))

  property("subForest") = forAll((a: Tree[Int]) =>
    a.subForest == subForest_[Int].f(a))

  property("flatten_foldmap") = forAll((a: Tree[Int]) => {
    def const(f: Int) = 1:java.lang.Integer
    a.foldMap[java.lang.Integer](const _, intAdditionMonoid) == a.flatten.length})

  property("levels_length") = forAll((a: Tree[Int]) =>
    a.flatten.length == join(a.levels).length)

  property("fmap_assoc") = forAll((a: Tree[String]) => {
    def f(s: String) = s.toUpperCase
    streamEqual(stringEqual).eq(a.flatten.map(f _), a.fmap(f _).flatten)})

  /*property("unfoldTree") = property((s: String, p: Tuple2[Int, List[String]]) => {
    def f(s: String) = p
    todo: Check unfold
  }*/
}
