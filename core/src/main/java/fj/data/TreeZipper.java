package fj.data;

import fj.*;
import fj.function.Booleans;

import java.util.Iterator;

import static fj.Equal.p3Equal;
import static fj.Equal.p4Equal;
import static fj.Equal.streamEqual;
import static fj.Equal.treeEqual;
import static fj.Function.compose;
import static fj.Function.curry;
import static fj.Function.flip;
import static fj.Function.uncurryF2;
import static fj.Show.p3Show;
import static fj.Show.p4Show;
import static fj.Show.streamShow;
import static fj.Show.treeShow;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Stream.nil;
import static fj.data.Stream.unfold;
import static fj.data.Tree.node;
import static fj.data.Tree.unfoldTree;

/**
 * Provides a zipper structure for rose trees, which is a Tree supplied with a location within that tree.
 * Provides navigation, insertion, deletion, and memorization of visited locations within a tree.
 */
public final class TreeZipper<A> implements Iterable<TreeZipper<A>> {

  /**
   * Returns an iterator of all the positions of this TreeZipper. Exists for use with the foreach syntax.
   *
   * @return An iterator of all the positions of this TreeZipper.
   */
  public Iterator<TreeZipper<A>> iterator() {
    return positions().toTree().iterator();
  }

  private final Tree<A> tree;
  private final Stream<Tree<A>> lefts;
  private final Stream<Tree<A>> rights;
  private final Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>> parents;

  private TreeZipper(final Tree<A> tree,
                     final Stream<Tree<A>> lefts,
                     final Stream<Tree<A>> rights,
                     final Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>> parents) {
    this.tree = tree;
    this.lefts = lefts;
    this.rights = rights;
    this.parents = parents;
  }

  /**
   * Creates a new tree zipper given a currently selected tree, a forest on the left, a forest on the right,
   * and a stream of parent contexts.
   *
   * @param tree    The currently selected tree.
   * @param lefts   The selected tree's left siblings, closest first.
   * @param rights  The selected tree's right siblings, closest first.
   * @param parents The parent of the selected tree, and the parent's siblings.
   * @return A new zipper with the given tree selected, and the given forests on the left and right.
   */
  public static <A> TreeZipper<A> treeZipper(final Tree<A> tree,
                                             final Stream<Tree<A>> lefts,
                                             final Stream<Tree<A>> rights,
                                             final Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>> parents) {
    return new TreeZipper<A>(tree, lefts, rights, parents);
  }

  /**
   * First-class constructor for tree zippers.
   *
   * @return A function that returns a new tree zipper, given a selected tree, left and right siblings,
   *         and a parent context.
   */
  public static <A>
  F<Tree<A>, F<Stream<Tree<A>>, F<Stream<Tree<A>>, F<Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>, TreeZipper<A>>>>>
  treeZipper() {
    return curry(
            (tree, lefts, rights, parents) -> treeZipper(tree, lefts, rights, parents));
  }

  /**
   * Returns the product-4 representation of this zipper.
   *
   * @return the product-4 representation of this zipper.
   */
  public P4<Tree<A>, Stream<Tree<A>>, Stream<Tree<A>>, Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>> p() {
    return P.p(tree, lefts, rights, parents);
  }

  /**
   * A first-class function that returns the product-4 representation of a given zipper.
   *
   * @return a function that converts a given zipper to its product-4 representation.
   */
  public static <A>
  F<TreeZipper<A>, P4<Tree<A>, Stream<Tree<A>>, Stream<Tree<A>>, Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>>> p_() {
    return a -> a.p();
  }

  /**
   * An Equal instance for tree zippers.
   *
   * @param e An Equal instance for tree elements.
   * @return An Equal instance for tree zippers.
   */
  public static <A> Equal<TreeZipper<A>> eq(final Equal<A> e) {
    return p4Equal(
        treeEqual(e),
        streamEqual(treeEqual(e)),
        streamEqual(treeEqual(e)),
        streamEqual(p3Equal(streamEqual(treeEqual(e)), e, streamEqual(treeEqual(e))))).comap(TreeZipper.<A>p_());
  }

  /**
   * A Show instance for tree zippers.
   *
   * @param s A Show instance for tree elements.
   * @return A Show instance for tree zippers.
   */
  public static <A> Show<TreeZipper<A>> show(final Show<A> s) {
    return p4Show(
        treeShow(s),
        streamShow(treeShow(s)),
        streamShow(treeShow(s)),
        streamShow(p3Show(streamShow(treeShow(s)), s, streamShow(treeShow(s))))).comap(TreeZipper.<A>p_());
  }

  private static <A> Stream<Tree<A>> combChildren(final Stream<Tree<A>> ls,
                                                  final Tree<A> t,
                                                  final Stream<Tree<A>> rs) {
    return ls.foldLeft(compose(flip(Stream.<Tree<A>>cons()), P.<Stream<Tree<A>>>p1()), Stream.cons(t, P.p(rs)));
  }

  /**
   * Navigates to the parent of the current location.
   *
   * @return A new tree zipper focused on the parent node of the current node,
   *         or none if the current node is the root node.
   */
  public Option<TreeZipper<A>> parent() {
    if (parents.isEmpty())
      return none();
    else {
      final P3<Stream<Tree<A>>, A, Stream<Tree<A>>> p = parents.head();
      return some(treeZipper(node(p._2(), combChildren(lefts, tree, rights)), p._1(), p._3(), parents.tail()._1()));
    }
  }

  /**
   * Navigates to the top-most parent of the current location.
   *
   * @return A new tree zipper focused on the top-most parent of the current node.
   */
  public TreeZipper<A> root() {
    return parent().option(this, TreeZipper.<A>root_());
  }

  /**
   * A first-class version of the root function.
   *
   * @return A function that returns a new tree-zipper focused on the root of the given tree zipper's tree.
   */
  public static <A> F<TreeZipper<A>, TreeZipper<A>> root_() {
    return a -> a.root();
  }

  /**
   * Navigates to the left sibling of the current location.
   *
   * @return A new tree zipper focused on the left sibling of the current node,
   *         or none if there are no siblings on the left.
   */
  public Option<TreeZipper<A>> left() {
    return lefts.isEmpty() ? Option.<TreeZipper<A>>none()
                           : some(treeZipper(lefts.head(), lefts.tail()._1(), rights.cons(tree), parents));
  }

  /**
   * Navigates to the right sibling of the current location.
   *
   * @return A new tree zipper focused on the right sibling of the current node,
   *         or none if there are no siblings on the right.
   */
  public Option<TreeZipper<A>> right() {
    return rights.isEmpty() ? Option.<TreeZipper<A>>none()
                            : some(treeZipper(rights.head(), lefts.cons(tree), rights.tail()._1(), parents));
  }

  /**
   * Navigtes to the first child of the current location.
   *
   * @return A new tree zipper focused on the first child of the current node, or none if the node has no children.
   */
  public Option<TreeZipper<A>> firstChild() {
    final Stream<Tree<A>> ts = tree.subForest()._1();
    return ts.isEmpty() ? Option.<TreeZipper<A>>none()
                        : some(treeZipper(ts.head(), Stream.<Tree<A>>nil(), ts.tail()._1(), downParents()));
  }

  /**
   * Navigtes to the last child of the current location.
   *
   * @return A new tree zipper focused on the last child of the current node, or none if the node has no children.
   */
  public Option<TreeZipper<A>> lastChild() {
    final Stream<Tree<A>> ts = tree.subForest()._1().reverse();
    return ts.isEmpty() ? Option.<TreeZipper<A>>none()
                        : some(treeZipper(ts.head(), ts.tail()._1(), Stream.<Tree<A>>nil(), downParents()));
  }

  /**
   * Navigates to the given child of the current location, starting at index 0.
   *
   * @param n The index of the child to which to navigate.
   * @return An optional tree zipper focused on the child node at the given index, or none if there is no such child.
   */
  public Option<TreeZipper<A>> getChild(final int n) {
    Option<TreeZipper<A>> r = none();
    for (final P2<Stream<Tree<A>>, Stream<Tree<A>>> lr
        : splitChildren(Stream.<Tree<A>>nil(), tree.subForest()._1(), n)) {
      r = some(treeZipper(lr._1().head(), lr._1().tail()._1(), lr._2(), downParents()));
    }
    return r;
  }

  /**
   * Navigates to the first child of the current location, that satisfies the given predicate.
   *
   * @param p A predicate to be satisfied by the child node.
   * @return An optional tree zipper focused on the first child node that satisfies the given predicate,
   *         or none if there is no such child.
   */
  public Option<TreeZipper<A>> findChild(final F<Tree<A>, Boolean> p) {
    Option<TreeZipper<A>> r = none();

    final F2<Stream<Tree<A>>, Stream<Tree<A>>, Option<P3<Stream<Tree<A>>, Tree<A>, Stream<Tree<A>>>>> split =
        new F2<Stream<Tree<A>>, Stream<Tree<A>>, Option<P3<Stream<Tree<A>>, Tree<A>, Stream<Tree<A>>>>>() {
          public Option<P3<Stream<Tree<A>>, Tree<A>, Stream<Tree<A>>>> f(final Stream<Tree<A>> acc,
                                                                         final Stream<Tree<A>> xs) {
            return xs.isNotEmpty()
                     ? p.f(xs.head()) ? some(P.p(acc, xs.head(), xs.tail()._1()))
                                      : f(acc.cons(xs.head()), xs.tail()._1())
                     : Option.<P3<Stream<Tree<A>>, Tree<A>, Stream<Tree<A>>>>none();
          }
        };

    Stream<Tree<A>> subforest = tree.subForest()._1();
    if (subforest.isNotEmpty()) {
      for (final P3<Stream<Tree<A>>, Tree<A>, Stream<Tree<A>>> ltr
        : split.f(Stream.<Tree<A>>nil(), subforest)) {
        r = some(treeZipper(ltr._2(), ltr._1(), ltr._3(), downParents()));
      }
    }
    return r;
  }

  private Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>> downParents() {
    return parents.cons(P.p(lefts, tree.root(), rights));
  }

  private static <A> Option<P2<Stream<A>, Stream<A>>> splitChildren(final Stream<A> acc,
                                                                    final Stream<A> xs,
                                                                    final int n) {
    return n == 0 ? some(P.p(acc, xs))
                  : xs.isNotEmpty() ? splitChildren(acc.cons(xs.head()), xs.tail()._1(), n - 1)
                                    : Option.<P2<Stream<A>, Stream<A>>>none();
  }

  private static <A> Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>> lp3nil() {
    return nil();
  }

  /**
   * Creates a new tree zipper focused on the root of the given tree.
   *
   * @param t A tree over which to create a new zipper.
   * @return a new tree zipper focused on the root of the given tree.
   */
  public static <A> TreeZipper<A> fromTree(final Tree<A> t) {
    return treeZipper(t, Stream.<Tree<A>>nil(), Stream.<Tree<A>>nil(), TreeZipper.<A>lp3nil());
  }

  /**
   * Creates a new tree zipper focused on the first element of the given forest.
   *
   * @param ts A forest over which to create a new zipper.
   * @return a new tree zipper focused on the first element of the given forest.
   */
  public static <A> Option<TreeZipper<A>> fromForest(final Stream<Tree<A>> ts) {
    return ts.isNotEmpty()
           ? some(treeZipper(ts.head(), Stream.<Tree<A>>nil(), ts.tail()._1(), TreeZipper.<A>lp3nil()))
           : Option.<TreeZipper<A>>none();
  }

  /**
   * Returns the tree containing this location.
   *
   * @return the tree containing this location.
   */
  public Tree<A> toTree() {
    return root().tree;
  }

  /**
   * Returns the forest containing this location.
   *
   * @return the forest containing this location.
   */
  public Stream<Tree<A>> toForest() {
    final TreeZipper<A> r = root();
    return combChildren(r.lefts, r.tree, r.rights);
  }

  /**
   * Returns the tree at the currently focused node.
   *
   * @return the tree at the currently focused node.
   */
  public Tree<A> focus() {
    return tree;
  }

  /**
   * Returns the left siblings of the currently focused node.
   *
   * @return the left siblings of the currently focused node.
   */
  public Stream<Tree<A>> lefts() {
    return lefts;
  }

  /**
   * Returns the right siblings of the currently focused node.
   *
   * @return the right siblings of the currently focused node.
   */
  public Stream<Tree<A>> rights() {
    return rights;
  }

  /**
   * Indicates whether the current node is at the top of the tree.
   *
   * @return true if the current node is the root of the tree, otherwise false.
   */
  public boolean isRoot() {
    return parents.isEmpty();
  }

  /**
   * Indicates whether the current node is the leftmost tree in the current forest.
   *
   * @return true if the current node has no left siblings, otherwise false.
   */
  public boolean isFirst() {
    return lefts.isEmpty();
  }

  /**
   * Indicates whether the current node is the rightmost tree in the current forest.
   *
   * @return true if the current node has no siblings on its right, otherwise false.
   */
  public boolean isLast() {
    return rights.isEmpty();
  }

  /**
   * Indicates whether the current node is at the bottom of the tree.
   *
   * @return true if the current node has no child nodes, otherwise false.
   */
  public boolean isLeaf() {
    return tree.subForest()._1().isEmpty();
  }

  /**
   * Indicates whether the current node is a child node of another node.
   *
   * @return true if the current node has a parent node, otherwise false.
   */
  public boolean isChild() {
    return !isRoot();
  }

  /**
   * Indicates whether the current node has any child nodes.
   *
   * @return true if the current node has child nodes, otherwise false.
   */
  public boolean hasChildren() {
    return !isLeaf();
  }

  /**
   * Replaces the current node with the given tree.
   *
   * @param t A tree with which to replace the current node.
   * @return A new tree zipper in which the focused node is replaced with the given tree.
   */
  public TreeZipper<A> setTree(final Tree<A> t) {
    return treeZipper(t, lefts, rights, parents);
  }

  /**
   * Modifies the current node with the given function.
   *
   * @param f A function with which to modify the current tree.
   * @return A new tree zipper in which the focused node has been transformed by the given function.
   */
  public TreeZipper<A> modifyTree(final F<Tree<A>, Tree<A>> f) {
    return setTree(f.f(tree));
  }

  /**
   * Modifies the label at the current node with the given function.
   *
   * @param f A function with which to transform the current node's label.
   * @return A new tree zipper with the focused node's label transformed by the given function.
   */
  public TreeZipper<A> modifyLabel(final F<A, A> f) {
    return setLabel(f.f(getLabel()));
  }

  /**
   * Replaces the label of the current node with the given value.
   *
   * @param v The new value for the node's label.
   * @return A new tree zipper with the focused node's label replaced by the given value.
   */
  public TreeZipper<A> setLabel(final A v) {
    return modifyTree(t -> Tree.node(v, t.subForest()));
  }

  /**
   * Returns the label at the current node.
   *
   * @return the label at the current node.
   */
  public A getLabel() {
    return tree.root();
  }

  /**
   * Inserts a tree to the left of the current position. The inserted tree becomes the current tree.
   *
   * @param t A tree to insert to the left of the current position.
   * @return A new tree zipper with the given tree in focus and the current tree on the right.
   */
  public TreeZipper<A> insertLeft(final Tree<A> t) {
    return treeZipper(t, lefts, rights.cons(tree), parents);
  }

  /**
   * Inserts a tree to the right of the current position. The inserted tree becomes the current tree.
   *
   * @param t A tree to insert to the right of the current position.
   * @return A new tree zipper with the given tree in focus and the current tree on the left.
   */
  public TreeZipper<A> insertRight(final Tree<A> t) {
    return treeZipper(t, lefts.cons(tree), rights, parents);
  }

  /**
   * Inserts a tree as the first child of the current node. The inserted tree becomes the current tree.
   *
   * @param t A tree to insert.
   * @return A new tree zipper with the given tree in focus, as the first child of the current node.
   */
  public TreeZipper<A> insertDownFirst(final Tree<A> t) {
    return treeZipper(t, Stream.<Tree<A>>nil(), tree.subForest()._1(), downParents());
  }

  /**
   * Inserts a tree as the last child of the current node. The inserted tree becomes the current tree.
   *
   * @param t A tree to insert.
   * @return A new tree zipper with the given tree in focus, as the last child of the current node.
   */
  public TreeZipper<A> insertDownLast(final Tree<A> t) {
    return treeZipper(t, tree.subForest()._1().reverse(), Stream.<Tree<A>>nil(), downParents());
  }

  /**
   * Inserts a tree at the specified location in the current node's stream of children. The inserted tree
   * becomes the current node.
   *
   * @param n The index at which to insert the given tree, starting at 0.
   * @param t A tree to insert.
   * @return A new tree zipper with the given tree in focus, at the specified index in the current node's stream
   *         of children, or None if the current node has fewer than <code>n</code> children.
   */
  public Option<TreeZipper<A>> insertDownAt(final int n, final Tree<A> t) {
    Option<TreeZipper<A>> r = none();
    for (final P2<Stream<Tree<A>>, Stream<Tree<A>>> lr
        : splitChildren(Stream.<Tree<A>>nil(), tree.subForest()._1(), n)) {
      r = some(treeZipper(t, lr._1(), lr._2(), downParents()));
    }
    return r;
  }

  /**
   * Removes the current node from the tree. The new position becomes the right sibling, or the left sibling
   * if the current node has no right siblings, or the parent node if the current node has no siblings.
   *
   * @return A new tree zipper with the current node removed.
   */
  public Option<TreeZipper<A>> delete() {
    Option<TreeZipper<A>> r = none();
    if (rights.isNotEmpty())
      r = some(treeZipper(rights.head(), lefts, rights.tail()._1(), parents));
    else if (lefts.isNotEmpty())
      r = some(treeZipper(lefts.head(), lefts.tail()._1(), rights, parents));
    else for (final TreeZipper<A> loc : parent())
        r = some(loc.modifyTree(t -> node(t.root(), Stream.<Tree<A>>nil())));
    return r;
  }

  /**
   * Zips the nodes in this zipper with a boolean that indicates whether that node has focus.
   * All of the booleans will be false, except for the focused node.
   *
   * @return A new zipper of pairs, with each node of this zipper paired with a boolean that is true if that
   *         node has focus, and false otherwise.
   */
  public TreeZipper<P2<A, Boolean>> zipWithFocus() {
    final F<A, P2<A, Boolean>> f = flip(P.<A, Boolean>p2()).f(false);
    return map(f).modifyLabel(P2.<A, Boolean, Boolean>map2_(Booleans.not));
  }

  /**
   * Maps the given function across this zipper (covariant functor pattern).
   *
   * @param f A function to map across this zipper.
   * @return A new zipper with the given function applied to the label of every node.
   */
  public <B> TreeZipper<B> map(final F<A, B> f) {
    final F<Tree<A>, Tree<B>> g = Tree.<A, B>fmap_().f(f);
    final F<Stream<Tree<A>>, Stream<Tree<B>>> h = Stream.<Tree<A>, Tree<B>>map_().f(g);
    return treeZipper(tree.fmap(f), lefts.map(g), rights.map(g), parents.map(
            p -> p.map1(h).map2(f).map3(h)));
  }

  /**
   * First-class conversion of a Tree to the corresponding tree zipper.
   *
   * @return A function that takes a tree to its tree zipper representation.
   */
  public static <A> F<Tree<A>, TreeZipper<A>> fromTree() {
    return t -> fromTree(t);
  }

  /**
   * A first-class version of the left() function.
   *
   * @return A function that focuses the given tree zipper on its left sibling.
   */
  public static <A> F<TreeZipper<A>, Option<TreeZipper<A>>> left_() {
    return z -> z.left();
  }

  /**
   * A first-class version of the right() function.
   *
   * @return A function that focuses the given tree zipper on its right sibling.
   */
  public static <A> F<TreeZipper<A>, Option<TreeZipper<A>>> right_() {
    return z -> z.right();
  }

  /**
   * Returns a zipper over the tree of all possible permutations of this tree zipper (comonad pattern).
   * This tree zipper becomes the focused node of the new zipper.
   *
   * @return A tree zipper over the tree of all possible permutations of this tree zipper.
   */
  public TreeZipper<TreeZipper<A>> positions() {
    final Tree<TreeZipper<A>> t = unfoldTree(TreeZipper.<A>dwn()).f(this);
    final Stream<Tree<TreeZipper<A>>> l = uf(TreeZipper.<A>left_());
    final Stream<Tree<TreeZipper<A>>> r = uf(TreeZipper.<A>right_());
    final Stream<P3<Stream<Tree<TreeZipper<A>>>, TreeZipper<A>, Stream<Tree<TreeZipper<A>>>>> p = unfold(
        new F<Option<TreeZipper<A>>,
            Option<P2<
                P3<Stream<Tree<TreeZipper<A>>>, TreeZipper<A>, Stream<Tree<TreeZipper<A>>>>,
                Option<TreeZipper<A>>>>>() {
          public Option<P2<
              P3<Stream<Tree<TreeZipper<A>>>, TreeZipper<A>, Stream<Tree<TreeZipper<A>>>>,
              Option<TreeZipper<A>>>> f(final Option<TreeZipper<A>> o) {
            Option<P2<
                P3<Stream<Tree<TreeZipper<A>>>, TreeZipper<A>, Stream<Tree<TreeZipper<A>>>>,
                Option<TreeZipper<A>>>> r = none();
            for (final TreeZipper<A> z : o) {
              r = some(P.p(P.p(z.uf(TreeZipper.<A>left_()), z, z.uf(TreeZipper.<A>right_())), z.parent()));
            }
            return r;
          }
        }, parent());
    return treeZipper(t, l, r, p);
  }

  private Stream<Tree<TreeZipper<A>>> uf(final F<TreeZipper<A>, Option<TreeZipper<A>>> f) {
    return unfold(
            o -> {
              Option<P2<Tree<TreeZipper<A>>, Option<TreeZipper<A>>>> r = none();
              for (final TreeZipper<A> c : o) {
                r = some(P.p(unfoldTree(TreeZipper.<A>dwn()).f(c), f.f(c)));
              }
              return r;
            }, f.f(this));
  }

  private static <A> F<TreeZipper<A>, P2<TreeZipper<A>, P1<Stream<TreeZipper<A>>>>> dwn() {
    return tz -> P.<TreeZipper<A>, P1<Stream<TreeZipper<A>>>>p(tz, new P1<Stream<TreeZipper<A>>>() {
      private F<Option<TreeZipper<A>>, Option<P2<TreeZipper<A>, Option<TreeZipper<A>>>>> fwd() {
        return o -> {
          Option<P2<TreeZipper<A>, Option<TreeZipper<A>>>> r = none();
          for (final TreeZipper<A> c : o) {
            r = some(P.p(c, c.right()));
          }
          return r;
        };
      }

      public Stream<TreeZipper<A>> _1() {
        return unfold(fwd(), tz.firstChild());
      }
    });
  }

  /**
   * Maps the given function over the tree of all positions for this zipper (comonad pattern). Returns a zipper
   * over the tree of results of the function application.
   *
   * @param f A function to map over the tree of all positions for this zipper.
   * @return A zipper over the tree of results of the function application.
   */
  public <B> TreeZipper<B> cobind(final F<TreeZipper<A>, B> f) {
    return positions().map(f);
  }

  /**
   * A first-class version of the findChild function.
   *
   * @return a function that finds the first child, of a given tree zipper, that matches a given predicate.
   */
  public static <A> F2<F<Tree<A>, Boolean>, TreeZipper<A>, Option<TreeZipper<A>>> findChild() {
    return (f, az) -> az.findChild(f);
  }

  /**
   * Zips this TreeZipper with another, applying the given function lock-step over both zippers in all directions.
   * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
   *
   * @param bs A TreeZipper to zip this one with.
   * @param f  A function with which to zip together the two TreeZippers.
   * @return The result of applying the given function over this TreeZipper and the given TreeZipper, location-wise.
   */
  public <B, C> TreeZipper<C> zipWith(final TreeZipper<B> bs, final F2<A, B, C> f) {
    return F2Functions.zipTreeZipperM(f).f(this, bs);
  }

  /**
   * Zips this TreeZipper with another, applying the given function lock-step over both zippers in all directions.
   * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
   *
   * @param bs A TreeZipper to zip this one with.
   * @param f  A function with which to zip together the two TreeZippers.
   * @return The result of applying the given function over this TreeZipper and the given TreeZipper, location-wise.
   */
  public <B, C> TreeZipper<C> zipWith(final TreeZipper<B> bs, final F<A, F<B, C>> f) {
    return zipWith(bs, uncurryF2(f));
  }
}
