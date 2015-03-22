package fj
package data

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen.{lzy, sized, resize, choose}
import org.scalacheck.Gen
import ArbitraryStream.arbitraryStream
import ArbitraryList.{listOf}
import Tree.{node, leaf}

object ArbitraryTree {
  implicit def arbitraryTree[A](implicit a: Arbitrary[A]): Arbitrary[Tree[A]] = {
    def tree(implicit a:Arbitrary[A], n:Int, g:Gen[A]) : Gen[Tree[A]] =  n match {
      case 0 => g.map(leaf(_))
      case n => choose(0, 10).flatMap(i =>
        lzy(Gen.zip(
          g,
          resize(i, listOf(tree(a, n/5, g)).map((x:fj.data.List[Tree[A]]) => P.p(x.toStream)))
        ).map(t => node(t._1, t._2)))
      )
    }
    Arbitrary(sized(tree(a, _, arbitrary[A])))
  }
}
