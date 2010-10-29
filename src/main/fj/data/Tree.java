package fj.data;

import fj.F;
import fj.F2;
import fj.P;
import fj.P1;
import fj.P2;
import static fj.Function.*;
import static fj.data.Stream.*;
import fj.Monoid;
import fj.Show;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides a lazy, immutable, non-empty, multi-way tree (a rose tree).
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          <li>Author: runar</li>
 *          </ul>
 */
public final class Tree<A> implements Iterable<A> {
  /**
   * Returns an iterator for this tree. This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this tree.
   */
  public Iterator<A> iterator() {
    return flatten().iterator();
  }

  private final A root;
  private final P1<Stream<Tree<A>>> subForest;

  private Tree(final A root, final P1<Stream<Tree<A>>> subForest) {
    this.root = root;
    this.subForest = subForest;
  }

  /**
   * Creates a nullary tree.
   *
   * @param root The root element of the tree.
   * @return A nullary tree with the root element in it.
   */
  public static <A> Tree<A> leaf(final A root) {
    return node(root, Stream.<Tree<A>>nil());
  }

  /**
   * Creates a new tree given a root and a (potentially infinite) subforest.
   *
   * @param root   The root element of the tree.
   * @param forest A stream of the tree's subtrees.
   * @return A newly sprouted tree.
   */
  public static <A> Tree<A> node(final A root, final P1<Stream<Tree<A>>> forest) {
    return new Tree<A>(root, forest);
  }

  /**
   * Creates a new tree given a root and a (potentially infinite) subforest.
   *
   * @param root   The root element of the tree.
   * @param forest A stream of the tree's subtrees.
   * @return A newly sprouted tree.
   */
  public static <A> Tree<A> node(final A root, final Stream<Tree<A>> forest) {
    return new Tree<A>(root, P.p(forest));
  }

  /**
   * Creates a new n-ary given a root and a subforest of length n.
   *
   * @param root   The root element of the tree.
   * @param forest A list of the tree's subtrees.
   * @return A newly sprouted tree.
   */
  public static <A> Tree<A> node(final A root, final List<Tree<A>> forest) {
    return node(root, forest.toStream());
  }

  /**
   * First-class constructor of trees.
   *
   * @return A function that constructs an n-ary tree given a root and a subforest or length n.
   */
  public static <A> F<A, F<P1<Stream<Tree<A>>>, Tree<A>>> node() {
    return curry(new F2<A, P1<Stream<Tree<A>>>, Tree<A>>() {
      public Tree<A> f(final A a, final P1<Stream<Tree<A>>> p1) {
        return node(a, p1);
      }
    });
  }

  /**
   * Returns the root element of the tree.
   *
   * @return The root element of the tree.
   */
  public A root() {
    return root;
  }

  /**
   * Returns a stream of the tree's subtrees.
   *
   * @return A stream of the tree's subtrees.
   */
  public P1<Stream<Tree<A>>> subForest() {
    return subForest;
  }

  /**
   * Provides a transformation from a tree to its root.
   *
   * @return A transformation from a tree to its root.
   */
  public static <A> F<Tree<A>, A> root_() {
    return new F<Tree<A>, A>() {
      public A f(final Tree<A> a) {
        return a.root();
      }
    };
  }

  /**
   * Provides a transformation from a tree to its subforest.
   *
   * @return A transformation from a tree to its subforest.
   */
  public static <A> F<Tree<A>, P1<Stream<Tree<A>>>> subForest_() {
    return new F<Tree<A>, P1<Stream<Tree<A>>>>() {
      public P1<Stream<Tree<A>>> f(final Tree<A> a) {
        return a.subForest();
      }
    };
  }

  /**
   * Puts the elements of the tree into a Stream, in pre-order.
   *
   * @return The elements of the tree in pre-order.
   */
  public Stream<A> flatten() {
    final F2<Tree<A>, P1<Stream<A>>, Stream<A>> squish = new F2<Tree<A>, P1<Stream<A>>, Stream<A>>() {
      public Stream<A> f(final Tree<A> t, final P1<Stream<A>> xs) {
        return cons(t.root(), t.subForest().map(Stream.<Tree<A>, Stream<A>>foldRight().f(curry()).f(xs._1())));
      }
    };
    return squish.f(this, P.p(Stream.<A>nil()));
  }

  /**
   * flatten :: Tree a -> [a]
   * flatten t = squish t []
   * where squish (Node x ts) xs = x:Prelude.foldr squish xs ts
   * Puts the elements of the tree into a Stream, in pre-order.
   *
   * @return The elements of the tree in pre-order.
   */
  public static <A> F<Tree<A>, Stream<A>> flatten_() {
    return new F<Tree<A>, Stream<A>>() {
      public Stream<A> f(final Tree<A> t) {
        return t.flatten();
      }
    };
  }

  /**
   * Provides a stream of the elements of the tree at each level, in level order.
   *
   * @return The elements of the tree at each level.
   */
  public Stream<Stream<A>> levels() {
    final F<Stream<Tree<A>>, Stream<Tree<A>>> flatSubForests =
        Stream.<Tree<A>, Tree<A>>bind_().f(compose(P1.<Stream<Tree<A>>>__1(), Tree.<A>subForest_()));
    final F<Stream<Tree<A>>, Stream<A>> roots = Stream.<Tree<A>, A>map_().f(Tree.<A>root_());
    return iterateWhile(flatSubForests, Stream.<Tree<A>>isNotEmpty_(), single(this)).map(roots);
  }

  /**
   * Maps the given function over this tree.
   *
   * @param f The function to map over this tree.
   * @return The new Tree after the function has been applied to each element in this Tree.
   */
  public <B> Tree<B> fmap(final F<A, B> f) {
    return node(f.f(root()), subForest().map(Stream.<Tree<A>, Tree<B>>map_().f(Tree.<A, B>fmap_().f(f))));
  }

  /**
   * Provides a transformation to lift any function so that it maps over Trees.
   *
   * @return A transformation to lift any function so that it maps over Trees.
   */
  public static <A, B> F<F<A, B>, F<Tree<A>, Tree<B>>> fmap_() {
    return new F<F<A, B>, F<Tree<A>, Tree<B>>>() {
      public F<Tree<A>, Tree<B>> f(final F<A, B> f) {
        return new F<Tree<A>, Tree<B>>() {
          public Tree<B> f(final Tree<A> a) {
            return a.fmap(f);
          }
        };
      }
    };
  }

  /**
   * Folds this tree using the given monoid.
   *
   * @param f A transformation from this tree's elements, to the monoid.
   * @param m The monoid to fold this tree with.
   * @return The result of folding the tree with the given monoid.
   */
  public <B> B foldMap(final F<A, B> f, final Monoid<B> m) {
    return m.sum(f.f(root()), m.sumRight(subForest()._1().map(foldMap_(f, m)).toList()));
  }

  /**
   * Projects an immutable collection of this tree.
   *
   * @return An immutable collection of this tree.
   */
  public Collection<A> toCollection() {
    return flatten().toCollection();
  }

  /**
   * Provides a function that folds a tree with the given monoid.
   *
   * @param f A transformation from a tree's elements to the monoid.
   * @param m A monoid to fold the tree with.
   * @return A function that, given a tree, folds it with the given monoid.
   */
  public static <A, B> F<Tree<A>, B> foldMap_(final F<A, B> f, final Monoid<B> m) {
    return new F<Tree<A>, B>() {
      public B f(final Tree<A> t) {
        return t.foldMap(f, m);
      }
    };
  }

  /**
   * Builds a tree from a seed value.
   *
   * @param f A function with which to build the tree.
   * @return A function which, given a seed value, yields a tree.
   */
  public static <A, B> F<B, Tree<A>> unfoldTree(final F<B, P2<A, P1<Stream<B>>>> f) {
    return new F<B, Tree<A>>() {
      public Tree<A> f(final B b) {
        final P2<A, P1<Stream<B>>> p = f.f(b);
        return node(p._1(), p._2().map(Stream.<B, Tree<A>>map_().f(unfoldTree(f))));
      }
    };
  }

  /**
   * Applies the given function to all subtrees of this tree, returning a tree of the results (comonad pattern).
   *
   * @param f A function to bind across all the subtrees of this tree.
   * @return A new tree, with the results of applying the given function to each subtree of this tree. The result
   *         of applying the function to the entire tree is the root label, and the results of applying to the
   *         root's children are labels of the root's subforest, etc.
   */
  public <B> Tree<B> cobind(final F<Tree<A>, B> f) {
    return unfoldTree(new F<Tree<A>, P2<B, P1<Stream<Tree<A>>>>>() {
      public P2<B, P1<Stream<Tree<A>>>> f(final Tree<A> t) {
        return P.p(f.f(t), t.subForest());
      }
    }).f(this);
  }

  /**
   * Expands this tree into a tree of trees, with this tree as the root label, and subtrees as the labels of
   * child nodes (comonad pattern).
   *
   * @return A tree of trees, with this tree as its root label, and subtrees of this tree as the labels of
   *         its child nodes.
   */
  public Tree<Tree<A>> cojoin() {
    final F<Tree<A>, Tree<A>> id = identity();
    return cobind(id);
  }

  private static <A> Stream<String> drawSubTrees(final Show<A> s, final Stream<Tree<A>> ts) {
    return ts.isEmpty() ? Stream.<String>nil()
                        : ts.tail()._1().isEmpty() ? shift("`- ", "   ", ts.head().drawTree(s)).cons("|")
                                                   : shift("+- ", "|  ", ts.head().drawTree(s))
                                                       .append(drawSubTrees(s, ts.tail()._1()));
  }

  private static Stream<String> shift(final String f, final String o, final Stream<String> s) {
    return Stream.repeat(o).cons(f).zipWith(s, Monoid.stringMonoid.sum());
  }

  private Stream<String> drawTree(final Show<A> s) {
    return drawSubTrees(s, subForest._1()).cons(s.showS(root));
  }

  /**
   * Draws a 2-dimensional representation of a tree.
   *
   * @param s A show instance for the elements of the tree.
   * @return a String showing this tree in two dimensions.
   */
  public String draw(final Show<A> s) {
    return Monoid.stringMonoid.join(drawTree(s), "\n");
  }

  /**
   * Provides a show instance that draws a 2-dimensional representation of a tree.
   *
   * @param s A show instance for the elements of the tree.
   * @return a show instance that draws a 2-dimensional representation of a tree.
   */
  public static <A> Show<Tree<A>> show2D(final Show<A> s) {
    return Show.showS(new F<Tree<A>, String>() {
      public String f(final Tree<A> tree) {
        return tree.draw(s);
      }
    });
  }

  /**
   * Zips this tree with another, using the given function. The resulting tree is the structural intersection
   * of the two trees.
   *
   * @param bs A tree to zip this tree with.
   * @param f  A function with which to zip together the two trees.
   * @return A new tree of the results of applying the given function over this tree and the given tree, position-wise.
   */
  public <B, C> Tree<C> zipWith(final Tree<B> bs, final F2<A, B, C> f) {
    return f.zipTreeM().f(this, bs);
  }

  /**
   * Zips this tree with another, using the given function. The resulting tree is the structural intersection
   * of the two trees.
   *
   * @param bs A tree to zip this tree with.
   * @param f  A function with which to zip together the two trees.
   * @return A new tree of the results of applying the given function over this tree and the given tree, position-wise.
   */
  public <B, C> Tree<C> zipWith(final Tree<B> bs, final F<A, F<B, C>> f) {
    return zipWith(bs, uncurryF2(f));
  }
}