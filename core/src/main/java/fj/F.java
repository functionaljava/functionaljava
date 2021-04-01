package fj;

import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.function.Function;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

/**
 * A transformation or function from <code>A</code> to <code>B</code>. This type can be represented
 * using the Java 7 closure syntax.
 *
 * @version %build.number%
 */
@FunctionalInterface
public interface F<A, B> extends Function<A, B> {
  /**
   * Transform <code>A</code> to <code>B</code>.
   *
   * @param a The <code>A</code> to transform.
   * @return The result of the transformation.
   */
  B f(A a);

  default B apply(A a) {
    return f(a);
  }

  /**
   * Function composition
   *
   * @param g A function to compose with this one.
   * @return The composed function such that this function is applied last.
   */
  default <C> F<C, B> o(final F<C, A> g) {
    return c -> f(g.f(c));
  }

  /**
   * First-class function composition
   *
   * @return A function that composes this function with another.
   */
  default <C> F<F<C, A>, F<C, B>> o() {
    return g -> o(g);
  }

  /**
   * Function composition flipped.
   *
   * @param g A function with which to compose this one.
   * @return The composed function such that this function is applied first.
   */
  @SuppressWarnings("unchecked")
  default <C> F<A, C> andThen(final F<B, C> g) {
    return g.o(this);
  }

  /**
   * First-class composition flipped.
   *
   * @return A function that invokes this function and then a given function on the result.
   */
  default <C> F<F<B, C>, F<A, C>> andThen() {
    return g -> andThen(g);
  }

  /**
   * Binds a given function across this function (Reader Monad).
   *
   * @param g A function that takes the return value of this function as an argument, yielding a new function.
   * @return A function that invokes this function on its argument and then the given function on the result.
   */
  default <C> F<A, C> bind(final F<B, F<A, C>> g) {
    return a -> g.f(f(a)).f(a);
  }

  /**
   * First-class function binding.
   *
   * @return A function that binds another function across this function.
   */
  default <C> F<F<B, F<A, C>>, F<A, C>> bind() {
    return g -> bind(g);
  }

  /**
   * Function application in an environment (Applicative Functor).
   *
   * @param g A function with the same argument type as this function, yielding a function that takes the return
   *          value of this function.
   * @return A new function that invokes the given function on its argument, yielding a new function that is then
   *         applied to the result of applying this function to the argument.
   */
  default <C> F<A, C> apply(final F<A, F<B, C>> g) {
    return a -> g.f(a).f(f(a));
  }

  /**
   * First-class function application in an environment.
   *
   * @return A function that applies a given function within the environment of this function.
   */
  default <C> F<F<A, F<B, C>>, F<A, C>> apply() {
    return g -> apply(g);
  }

  /**
   * Applies this function over the arguments of another function.
   *
   * @param g The function over whose arguments to apply this function.
   * @return A new function that invokes this function on its arguments before invoking the given function.
   */
  default <C> F<A, F<A, C>> on(final F<B, F<B, C>> g) {
    return a1 -> a2 -> g.f(f(a1)).f(f(a2));
  }



  /**
   * Applies this function over the arguments of another function.
   *
   * @return A function that applies this function over the arguments of another function.
   */
  default <C> F<F<B, F<B, C>>, F<A, F<A, C>>> on() {
    return g -> on(g);
  }

  /**
   * Promotes this function so that it returns its result in a product-1. Kleisli arrow for P1.
   *
   * @return This function promoted to return its result in a product-1.
   */
  default F<A, P1<B>> lazy() {
    return a -> P.lazy(() -> f(a));
  }

  /**
   * Partial application.
   *
   * @param a The <code>A</code> to which to apply this function.
   * @return The function partially applied to the given argument to return a lazy value.
   */
  default P1<B> partial(final A a) {
    return P.lazy(() -> f(a));
  }

  /**
   * Promotes this function to map over a product-1.
   *
   * @return This function promoted to map over a product-1.
   */
  default F<P1<A>, P1<B>> mapP1() {
    return p -> p.map(this);
  }

  /**
   * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
   *
   * @return This function promoted to return its result in an Option.
   */
  default F<A, Option<B>> optionK() {
    return a -> some(f(a));
  }

  /**
   * Promotes this function to map over an optional value.
   *
   * @return This function promoted to map over an optional value.
   */
  default F<Option<A>, Option<B>> mapOption() {
    return o -> o.map(this);
  }

  /**
   * Promotes this function so that it returns its result in a List. Kleisli arrow for List.
   *
   * @return This function promoted to return its result in a List.
   */
  default F<A, List<B>> listK() {
    return a -> List.single(f(a));
  }

  /**
   * Promotes this function to map over a List.
   *
   * @return This function promoted to map over a List.
   */
  default F<List<A>, List<B>> mapList() {
    return x -> x.map(this);
  }

  /**
   * Promotes this function so that it returns its result in a Stream. Kleisli arrow for Stream.
   *
   * @return This function promoted to return its result in a Stream.
   */
  default F<A, Stream<B>> streamK() {
    return a -> Stream.single(f(a));
  }

  /**
   * Promotes this function to map over a Stream.
   *
   * @return This function promoted to map over a Stream.
   */
  default F<Stream<A>, Stream<B>> mapStream() {
    return x -> x.map(this);
  }

  /**
   * Promotes this function so that it returns its result in a Array. Kleisli arrow for Array.
   *
   * @return This function promoted to return its result in a Array.
   */
  default F<A, Array<B>> arrayK() {
    return a -> Array.single(f(a));

  }

  /**
   * Promotes this function to map over a Array.
   *
   * @return This function promoted to map over a Array.
   */
  default F<Array<A>, Array<B>> mapArray() {
    return x -> x.map(this);
  }

  /**
   * Returns a function that contramaps over a given actor.
   *
   * @return A function that contramaps over a given actor.
   */
  default F<Actor<B>, Actor<A>> contramapActor() {
    return a -> a.contramap(this);
  }

  /**
   * Promotes this function to a concurrent function that returns a Promise of a value.
   *
   * @param s A parallel strategy for concurrent execution.
   * @return A concurrent function that returns a Promise of a value.
   */
  default F<A, Promise<B>> promiseK(final Strategy<Unit> s) {
    return Promise.promise(s, this);
  }

  /**
   * Promotes this function to map over a Promise.
   *
   * @return This function promoted to map over Promises.
   */
  default F<Promise<A>, Promise<B>> mapPromise() {
    return p -> p.fmap(this);
  }

  /**
   * Promotes this function so that it returns its result on the left side of an Either.
   * Kleisli arrow for the Either left projection.
   *
   * @return This function promoted to return its result on the left side of an Either.
   */
  @SuppressWarnings("unchecked")
  default <C> F<A, Either<B, C>> eitherLeftK() {
    return Either.<B, C>left_().o(this);
  }

  /**
   * Promotes this function so that it returns its result on the right side of an Either.
   * Kleisli arrow for the Either right projection.
   *
   * @return This function promoted to return its result on the right side of an Either.
   */
  @SuppressWarnings("unchecked")
  default <C> F<A, Either<C, B>> eitherRightK() {
    return Either.<C, B>right_().o(this);
  }

  /**
   * Promotes this function to map over the left side of an Either.
   *
   * @return This function promoted to map over the left side of an Either.
   */
  @SuppressWarnings("unchecked")
  default <X> F<Either<A, X>, Either<B, X>> mapLeft() {
    return Either.<A, X, B>leftMap_().f(this);
  }

  /**
   * Promotes this function to map over the right side of an Either.
   *
   * @return This function promoted to map over the right side of an Either.
   */
  @SuppressWarnings("unchecked")
  default <X> F<Either<X, A>, Either<X, B>> mapRight() {
    return Either.<X, A, B>rightMap_().f(this);
  }

  /**
   * Returns a function that returns the left side of a given Either, or this function applied to the right side.
   *
   * @return a function that returns the left side of a given Either, or this function applied to the right side.
   */
  default F<Either<B, A>, B> onLeft() {
    return e -> e.left().on(this);
  }

  /**
   * Returns a function that returns the right side of a given Either, or this function applied to the left side.
   *
   * @return a function that returns the right side of a given Either, or this function applied to the left side.
   */
  default F<Either<A, B>, B> onRight() {
    return e -> e.right().on(this);
  }

  /**
   * Promotes this function to return its value in an Iterable.
   *
   * @return This function promoted to return its value in an Iterable.
   */
  @SuppressWarnings("unchecked")
  default F<A, IterableW<B>> iterableK() {
    return IterableW.<A, B>arrow().f(this);
  }

  /**
   * Promotes this function to map over Iterables.
   *
   * @return This function promoted to map over Iterables.
   */
  @SuppressWarnings("unchecked")
  default F<Iterable<A>, IterableW<B>> mapIterable() {
    return IterableW.<A, B>map().f(this).o(IterableW.wrap());
  }

  /**
   * Promotes this function to return its value in a NonEmptyList.
   *
   * @return This function promoted to return its value in a NonEmptyList.
   */
  @SuppressWarnings("unchecked")
  default F<A, NonEmptyList<B>> nelK() {
    return NonEmptyList.<B>nel().o(this);
  }

  /**
   * Promotes this function to map over a NonEmptyList.
   *
   * @return This function promoted to map over a NonEmptyList.
   */
  default F<NonEmptyList<A>, NonEmptyList<B>> mapNel() {
    return list -> list.map(this);
  }

  /**
   * Promotes this function to return its value in a Set.
   *
   * @param o An order for the set.
   * @return This function promoted to return its value in a Set.
   */
  default F<A, Set<B>> setK(final Ord<B> o) {
    return a -> Set.single(o, f(a));
  }

  /**
   * Promotes this function to map over a Set.
   *
   * @param o An order for the resulting set.
   * @return This function promoted to map over a Set.
   */
  default F<Set<A>, Set<B>> mapSet(final Ord<B> o) {
    return s -> s.map(o, this);
  }

  /**
   * Promotes this function to return its value in a Tree.
   *
   * @return This function promoted to return its value in a Tree.
   */
  default F<A, Tree<B>> treeK() {
    return a -> Tree.leaf(f(a));
  }

  /**
   * Promotes this function to map over a Tree.
   *
   * @return This function promoted to map over a Tree.
   */
  @SuppressWarnings("unchecked")
  default F<Tree<A>, Tree<B>> mapTree() {
    return Tree.<A, B>fmap_().f(this);
  }

  /**
   * Returns a function that maps this function over a tree and folds it with the given monoid.
   *
   * @param m The monoid with which to fold the mapped tree.
   * @return a function that maps this function over a tree and folds it with the given monoid.
   */
  default F<Tree<A>, B> foldMapTree(final Monoid<B> m) {
    return Tree.foldMap_(this, m);
  }

  /**
   * Promotes this function to return its value in a TreeZipper.
   *
   * @return This function promoted to return its value in a TreeZipper.
   */
  default F<A, TreeZipper<B>> treeZipperK() {
    return treeK().andThen(TreeZipper.fromTree());
  }

  /**
   * Promotes this function to map over a TreeZipper.
   *
   * @return This function promoted to map over a TreeZipper.
   */
  default F<TreeZipper<A>, TreeZipper<B>> mapTreeZipper() {
    return z -> z.map(this);
  }

  /**
   * Promotes this function so that it returns its result on the failure side of a Validation.
   * Kleisli arrow for the Validation failure projection.
   *
   * @return This function promoted to return its result on the failure side of a Validation.
   */
  default <C> F<A, Validation<B, C>> failK() {
    return a -> Validation.fail(f(a));

  }

  /**
   * Promotes this function so that it returns its result on the success side of an Validation.
   * Kleisli arrow for the Validation success projection.
   *
   * @return This function promoted to return its result on the success side of an Validation.
   */
  default <C> F<A, Validation<C, B>> successK() {
    return a -> Validation.success(f(a));
  }

  /**
   * Promotes this function to map over the failure side of a Validation.
   *
   * @return This function promoted to map over the failure side of a Validation.
   */
  default <X> F<Validation<A, X>, Validation<B, X>> mapFail() {
    return v -> v.f().map(this);
  }

  /**
   * Promotes this function to map over the success side of a Validation.
   *
   * @return This function promoted to map over the success side of a Validation.
   */
  default <X> F<Validation<X, A>, Validation<X, B>> mapSuccess() {
    return v -> v.map(this);
  }

  /**
   * Returns a function that returns the failure side of a given Validation,
   * or this function applied to the success side.
   *
   * @return a function that returns the failure side of a given Validation,
   *         or this function applied to the success side.
   */
  default F<Validation<B, A>, B> onFail() {
    return v -> v.f().on(this);
  }

  /**
   * Returns a function that returns the success side of a given Validation,
   * or this function applied to the failure side.
   *
   * @return a function that returns the success side of a given Validation,
   *         or this function applied to the failure side.
   */
  default F<Validation<A, B>, B> onSuccess() {
    return v -> v.on(this);
  }

  /**
   * Promotes this function to return its value in a Zipper.
   *
   * @return This function promoted to return its value in a Zipper.
   */
  default F<A, Zipper<B>> zipperK() {
    return streamK().andThen(s -> fromStream(s).some());
  }

  /**
   * Promotes this function to map over a Zipper.
   *
   * @return This function promoted to map over a Zipper.
   */
  default F<Zipper<A>, Zipper<B>> mapZipper() {
    return z -> z.map(this);
  }

  /**
   * Promotes this function to map over an Equal as a contravariant functor.
   *
   * @return This function promoted to map over an Equal as a contravariant functor.
   */
  default F<Equal<B>, Equal<A>> contramapEqual() {
    return e -> e.contramap(this);
  }

  /**
   * Promotes this function to map over a Hash as a contravariant functor.
   *
   * @return This function promoted to map over a Hash as a contravariant functor.
   */
  default F<Hash<B>, Hash<A>> contramapHash() {
    return h -> h.contramap(this);
  }

  /**
   * Promotes this function to map over a Show as a contravariant functor.
   *
   * @return This function promoted to map over a Show as a contravariant functor.
   */
  default F<Show<B>, Show<A>> contramapShow() {
    return s -> s.contramap(this);
  }

  /**
   * Promotes this function to map over the first element of a pair.
   *
   * @return This function promoted to map over the first element of a pair.
   */
  default <C> F<P2<A, C>, P2<B, C>> mapFst() {
    return P2.map1_(this);
  }

  /**
   * Promotes this function to map over the second element of a pair.
   *
   * @return This function promoted to map over the second element of a pair.
   */
  default <C> F<P2<C, A>, P2<C, B>> mapSnd() {
    return P2.map2_(this);
  }

  /**
   * Promotes this function to map over both elements of a pair.
   *
   * @return This function promoted to map over both elements of a pair.
   */
  default F<P2<A, A>, P2<B, B>> mapBoth() {
    return p2 -> P2.map(this, p2);
  }

  /**
   * Maps this function over a SynchronousQueue.
   *
   * @param as A SynchronousQueue to map this function over.
   * @return A new SynchronousQueue with this function applied to each element.
   */
  default SynchronousQueue<B> mapJ(final SynchronousQueue<A> as) {
    final SynchronousQueue<B> bs = new SynchronousQueue<>();
    bs.addAll(iterableStream(as).map(this).toCollection());
    return bs;
  }


  /**
   * Maps this function over a PriorityBlockingQueue.
   *
   * @param as A PriorityBlockingQueue to map this function over.
   * @return A new PriorityBlockingQueue with this function applied to each element.
   */
  default PriorityBlockingQueue<B> mapJ(final PriorityBlockingQueue<A> as) {
    return new PriorityBlockingQueue<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a LinkedBlockingQueue.
   *
   * @param as A LinkedBlockingQueue to map this function over.
   * @return A new LinkedBlockingQueue with this function applied to each element.
   */
  default LinkedBlockingQueue<B> mapJ(final LinkedBlockingQueue<A> as) {
    return new LinkedBlockingQueue<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a CopyOnWriteArraySet.
   *
   * @param as A CopyOnWriteArraySet to map this function over.
   * @return A new CopyOnWriteArraySet with this function applied to each element.
   */
  default CopyOnWriteArraySet<B> mapJ(final CopyOnWriteArraySet<A> as) {
    return new CopyOnWriteArraySet<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a CopyOnWriteArrayList.
   *
   * @param as A CopyOnWriteArrayList to map this function over.
   * @return A new CopyOnWriteArrayList with this function applied to each element.
   */
  default CopyOnWriteArrayList<B> mapJ(final CopyOnWriteArrayList<A> as) {
    return new CopyOnWriteArrayList<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a ConcurrentLinkedQueue.
   *
   * @param as A ConcurrentLinkedQueue to map this function over.
   * @return A new ConcurrentLinkedQueue with this function applied to each element.
   */
  default ConcurrentLinkedQueue<B> mapJ(final ConcurrentLinkedQueue<A> as) {
    return new ConcurrentLinkedQueue<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over an ArrayBlockingQueue.
   *
   * @param as An ArrayBlockingQueue to map this function over.
   * @return A new ArrayBlockingQueue with this function applied to each element.
   */
  default ArrayBlockingQueue<B> mapJ(final ArrayBlockingQueue<A> as) {
    final ArrayBlockingQueue<B> bs = new ArrayBlockingQueue<>(as.size());
    bs.addAll(iterableStream(as).map(this).toCollection());
    return bs;
  }


  /**
   * Maps this function over a TreeSet.
   *
   * @param as A TreeSet to map this function over.
   * @return A new TreeSet with this function applied to each element.
   */
  default TreeSet<B> mapJ(final TreeSet<A> as) {
    return new TreeSet<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a PriorityQueue.
   *
   * @param as A PriorityQueue to map this function over.
   * @return A new PriorityQueue with this function applied to each element.
   */
  default java.util.PriorityQueue<B> mapJ(final java.util.PriorityQueue<A> as) {
    return new java.util.PriorityQueue<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a LinkedList.
   *
   * @param as A LinkedList to map this function over.
   * @return A new LinkedList with this function applied to each element.
   */
  default LinkedList<B> mapJ(final LinkedList<A> as) {
    return new LinkedList<>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over an ArrayList.
   *
   * @param as An ArrayList to map this function over.
   * @return A new ArrayList with this function applied to each element.
   */
  default ArrayList<B> mapJ(final ArrayList<A> as) {
    return new ArrayList<>(iterableStream(as).map(this).toCollection());
  }

  default <C> F<A, C> map(F<B, C> f) {
    return f.o(this);
  }

  default <C> F<C, B> contramap(F<C, A> f) {
    return o(f);
  }

  /**
   * Both map (with g) and contramap (with f) the target function. (Profunctor pattern)
   */
  default <C, D> F<C, D> dimap(F<C, A> f, F<B, D> g) {
    return c -> g.f(f(f.f(c)));
  }


}
