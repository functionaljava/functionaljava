package fj;

import fj.control.parallel.Promise;
import fj.data.*;

import java.util.function.BiFunction;

import static fj.P.p;
import static fj.data.IterableW.wrap;
import static fj.data.Set.iterableSet;
import static fj.data.Tree.node;
import static fj.data.TreeZipper.treeZipper;
import static fj.data.Zipper.zipper;

/**
 * A transformation function of arity-2 from <code>A</code> and <code>B</code> to <code>C</code>.
 * This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%
 */
@FunctionalInterface
public interface F2<A, B, C> extends BiFunction<A, B, C> {
  /**
   * Transform <code>A</code> and <code>B</code> to <code>C</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @return The result of the transformation.
   */
  C f(A a, B b);

  default C apply(A a, B b) {
    return f(a, b);
  }

  /**
   * Partial application.
   *
   * @param a The <code>A</code> to which to apply this function.
   * @return The function partially applied to the given argument.
   */
  default F<B, C> f(final A a) {
    return b -> f(a, b);
  }

  /**
   * Curries this wrapped function to a wrapped function of arity-1 that returns another wrapped function.
   *
   * @return a wrapped function of arity-1 that returns another wrapped function.
   */
  default F<A, F<B, C>> curry() {
    return a -> b -> f(a, b);
  }

  /**
   * Flips the arguments of this function.
   *
   * @return A new function with the arguments of this function flipped.
   */
  default F2<B, A, C> flip() {
    return (b, a) -> f(a, b);
  }

  /**
   * Uncurries this function to a function on tuples.
   *
   * @return A new function that calls this function with the elements of a given tuple.
   */
  default F<P2<A, B>, C> tuple() {
    return p -> f(p._1(), p._2());
  }

  /**
   * Promotes this function to a function on Arrays.
   *
   * @return This function promoted to transform Arrays.
   */
  default F2<Array<A>, Array<B>, Array<C>> arrayM() {
    return (a, b) -> a.bind(b, curry());
  }

  /**
   * Promotes this function to a function on Promises.
   *
   * @return This function promoted to transform Promises.
   */
  default F2<Promise<A>, Promise<B>, Promise<C>> promiseM() {
    return (a, b) -> a.bind(b, curry());
  }

  /**
   * Promotes this function to a function on Iterables.
   *
   * @return This function promoted to transform Iterables.
   */
  default F2<Iterable<A>, Iterable<B>, IterableW<C>> iterableM() {
    return (a, b) -> IterableW.liftM2(curry()).f(a).f(b);
  }

  /**
   * Promotes this function to a function on Lists.
   *
   * @return This function promoted to transform Lists.
   */
  default F2<List<A>, List<B>, List<C>> listM() {
    return (a, b) -> List.liftM2(curry()).f(a).f(b);
  }

  /**
   * Promotes this function to a function on non-empty lists.
   *
   * @return This function promoted to transform non-empty lists.
   */
  default F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> nelM() {
    return (as, bs) -> NonEmptyList.fromList(as.toList().bind(bs.toList(), this)).some();
  }

  /**
   * Promotes this function to a function on Options.
   *
   * @return This function promoted to transform Options.
   */
  default F2<Option<A>, Option<B>, Option<C>> optionM() {
    return (a, b) -> Option.liftM2(curry()).f(a).f(b);
  }

  /**
   * Promotes this function to a function on Sets.
   *
   * @param o An ordering for the result of the promoted function.
   * @return This function promoted to transform Sets.
   */
  default F2<Set<A>, Set<B>, Set<C>> setM(final Ord<C> o) {
    return (as, bs) -> {
      Set<C> cs = Set.empty(o);
      for (final A a : as)
        for (final B b : bs)
          cs = cs.insert(f(a, b));
      return cs;
    };
  }

  /**
   * Promotes this function to a function on Streams.
   *
   * @return This function promoted to transform Streams.
   */
  default F2<Stream<A>, Stream<B>, Stream<C>> streamM() {
    return (as, bs) -> as.bind(bs, this);
  }

  /**
   * Promotes this function to a function on Trees.
   *
   * @return This function promoted to transform Trees.
   */
  default F2<Tree<A>, Tree<B>, Tree<C>> treeM() {
    F2<A, B, C> outer = this;
    return new F2<Tree<A>, Tree<B>, Tree<C>>() {
      public Tree<C> f(final Tree<A> as, final Tree<B> bs) {
        final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
        return node(outer.f(as.root(), bs.root()), P.lazy(() -> self.streamM().f(as.subForest()._1(), bs.subForest()._1())));
      }
    };
  }

  /**
   * Promotes this function to zip two arrays, applying the function lock-step over both Arrays.
   *
   * @return A function that zips two arrays with this function.
   */
  default F2<Array<A>, Array<B>, Array<C>> zipArrayM() {
    return (as, bs) -> as.zipWith(bs, this);
  }

  /**
   * Promotes this function to zip two iterables, applying the function lock-step over both iterables.
   *
   * @return A function that zips two iterables with this function.
   */
  default F2<Iterable<A>, Iterable<B>, Iterable<C>> zipIterableM() {
    return (as, bs) -> wrap(as).zipWith(bs, this);
  }

  /**
   * Promotes this function to zip two lists, applying the function lock-step over both lists.
   *
   * @return A function that zips two lists with this function.
   */
  default F2<List<A>, List<B>, List<C>> zipListM() {
    return (as, bs) -> as.zipWith(bs, this);
  }


  /**
   * Promotes this function to zip two streams, applying the function lock-step over both streams.
   *
   * @return A function that zips two streams with this function.
   */
  default F2<Stream<A>, Stream<B>, Stream<C>> zipStreamM() {
    return (as, bs) -> as.zipWith(bs, this);
  }

  /**
   * Promotes this function to zip two non-empty lists, applying the function lock-step over both lists.
   *
   * @return A function that zips two non-empty lists with this function.
   */
  default F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> zipNelM() {
    return (as, bs) -> NonEmptyList.fromList(as.toList().zipWith(bs.toList(), this)).some();
  }

  /**
   * Promotes this function to zip two sets, applying the function lock-step over both sets.
   *
   * @param o An ordering for the resulting set.
   * @return A function that zips two sets with this function.
   */
  default F2<Set<A>, Set<B>, Set<C>> zipSetM(final Ord<C> o) {
    return (as, bs) -> iterableSet(o, as.toStream().zipWith(bs.toStream(), this));
  }

  /**
   * Promotes this function to zip two trees, applying the function lock-step over both trees.
   * The structure of the resulting tree is the structural intersection of the two trees.
   *
   * @return A function that zips two trees with this function.
   */
  default F2<Tree<A>, Tree<B>, Tree<C>> zipTreeM() {
    F2<A, B, C> outer = this;
    return new F2<Tree<A>, Tree<B>, Tree<C>>() {
      public Tree<C> f(final Tree<A> ta, final Tree<B> tb) {
        final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
        return node(outer.f(ta.root(), tb.root()), P.lazy(() -> self.zipStreamM().f(ta.subForest()._1(), tb.subForest()._1())));
      }
    };
  }

  /**
   * Promotes this function to zip two zippers, applying the function lock-step over both zippers in both directions.
   * The structure of the resulting zipper is the structural intersection of the two zippers.
   *
   * @return A function that zips two zippers with this function.
   */
  default F2<Zipper<A>, Zipper<B>, Zipper<C>> zipZipperM() {
    return (ta, tb) -> {
      final F2<Stream<A>, Stream<B>, Stream<C>> sf = this.zipStreamM();
      return zipper(sf.f(ta.lefts(), tb.lefts()), f(ta.focus(), tb.focus()), sf.f(ta.rights(), tb.rights()));
    };
  }

  /**
   * Promotes this function to zip two TreeZippers, applying the function lock-step over both zippers in all directions.
   * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
   *
   * @return A function that zips two TreeZippers with this function.
   */
  default F2<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>> zipTreeZipperM() {
    F2<A, B, C> outer = this;
    return (ta, tb) -> {
      final F2<Stream<Tree<A>>, Stream<Tree<B>>, Stream<Tree<C>>> sf = outer.treeM().zipStreamM();
      F2<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>, P3<Stream<Tree<B>>, B, Stream<Tree<B>>>, P3<Stream<Tree<C>>, C, Stream<Tree<C>>>> g =
              (pa, pb) -> p(outer.treeM().zipStreamM().f(pa._1(), pb._1()), outer.f(pa._2(), pb._2()),
                      outer.treeM().zipStreamM().f(pa._3(), pb._3()));
      final
      F2<Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>,
              Stream<P3<Stream<Tree<B>>, B, Stream<Tree<B>>>>,
              Stream<P3<Stream<Tree<C>>, C, Stream<Tree<C>>>>>
              pf = g.zipStreamM();
      return treeZipper(outer.treeM().f(ta.p()._1(), tb.p()._1()), sf.f(ta.lefts(), tb.lefts()),
              sf.f(ta.rights(), tb.rights()), pf.f(ta.p()._4(), tb.p()._4()));
    };
  }

  default <Z> F2<Z, B, C> contramapFirst(F<Z, A> f) {
    return (z, b) -> f(f.f(z), b);
  }

  default <Z> F2<A, Z, C> contramapSecond(F<Z, B> f) {
    return (a, z) -> f(a, f.f(z));
  }

  default <X, Y> F2<X, Y, C> contramap(F<X, A> f, F<Y, B> g) {
    return contramapFirst(f).contramapSecond(g);
  }

  default <Z> F2<A, B, Z> map(F<C, Z> f) {
    return (a, b) -> f.f(f(a, b));
  }


}
