package fj;

import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.Array;
import fj.data.Either;
import fj.data.IterableW;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeZipper;
import fj.data.Validation;
import fj.data.Zipper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Function;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

@FunctionalInterface
public interface F<A, B> extends Function<A, B>{

	B apply(A a);

	public default B f(A a) {
		return apply(a);
	}

	/**
	 * Function composition
	 * 
	 * @param g
	 *            A function to compose with this one.
	 * @return The composed function such that this function is applied last.
	 */
	public default <C> F<C, B> o(final F<C, A> g) {
		return c -> f(g.f(c));
	}

	/**
	 * First-class function composition
	 * 
	 * @return A function that composes this function with another.
	 */
	public default <C> F<F<C, A>, F<C, B>> o() {
		return this::o;
	}

	/**
	 * Function composition flipped.
	 * 
	 * @param g
	 *            A function with which to compose this one.
	 * @return The composed function such that this function is applied first.
	 */
	public default <C> F<A, C> andThen(final F<B, C> g) {
		return g.o(this);
	}

	/**
	 * First-class composition flipped.
	 * 
	 * @return A function that invokes this function and then a given function
	 *         on the result.
	 */
	public default <C> F<F<B, C>, F<A, C>> andThen() {
		return this::andThen;
	}

	/**
	 * Binds a given function across this function (Reader Monad).
	 * 
	 * @param g
	 *            A function that takes the return value of this function as an
	 *            argument, yielding a new function.
	 * @return A function that invokes this function on its argument and then
	 *         the given function on the result.
	 */
	public default <C> F<A, C> bind(final F<B, F<A, C>> g) {
		return a -> g.f(f(a)).f(a);
	}

	/**
	 * First-class function binding.
	 * 
	 * @return A function that binds another function across this function.
	 */
	public default <C> F<F<B, F<A, C>>, F<A, C>> bind() {
		return this::bind;
	}

	/**
	 * Function application in an environment (Applicative Functor).
	 * 
	 * @param g
	 *            A function with the same argument type as this function,
	 *            yielding a function that takes the return value of this
	 *            function.
	 * @return A new function that invokes the given function on its argument,
	 *         yielding a new function that is then applied to the result of
	 *         applying this function to the argument.
	 */
	public default <C> F<A, C> apply(final F<A, F<B, C>> g) {
		return a -> g.f(a).f(f(a));
	}

	/**
	 * First-class function application in an environment.
	 * 
	 * @return A function that applies a given function within the environment
	 *         of this function.
	 */
	public default <C> F<F<A, F<B, C>>, F<A, C>> apply() {
		return this::apply;
	}

	/**
	 * Applies this function over the arguments of another function.
	 * 
	 * @param g
	 *            The function over whose arguments to apply this function.
	 * @return A new function that invokes this function on its arguments before
	 *         invoking the given function.
	 */
	public default <C> F<A, F<A, C>> on(final F<B, F<B, C>> g) {
		return a1 -> a2 -> g.f(f(a1)).f(f(a2));

	}

	/**
	 * Applies this function over the arguments of another function.
	 * 
	 * @return A function that applies this function over the arguments of
	 *         another function.
	 */
	public default <C> F<F<B, F<B, C>>, F<A, F<A, C>>> on() {
		return this::on;
	}

	/**
	 * Promotes this function so that it returns its result in a product-1.
	 * Kleisli arrow for P1.
	 * 
	 * @return This function promoted to return its result in a product-1.
	 */
	public default F<A, P1<B>> lazy() {
		return a -> new P1<B>() {
			public B _1() {
				return F.this.f(a);
			}
		};
	}

	/**
	 * Promotes this function to map over a product-1.
	 * 
	 * @return This function promoted to map over a product-1.
	 */
	public default F<P1<A>, P1<B>> mapP1() {
		return p -> p.map(this);
	}

	/**
	 * Promotes this function so that it returns its result in an Option.
	 * Kleisli arrow for Option.
	 * 
	 * @return This function promoted to return its result in an Option.
	 */
	public default F<A, Option<B>> optionK() {
		return a -> some(f(a));
	}

	/**
	 * Promotes this function to map over an optional value.
	 * 
	 * @return This function promoted to map over an optional value.
	 */
	public default F<Option<A>, Option<B>> mapOption() {
		return o -> o.map(this);
	}

	/**
	 * Promotes this function so that it returns its result in a List. Kleisli
	 * arrow for List.
	 * 
	 * @return This function promoted to return its result in a List.
	 */
	public default F<A, List<B>> listK() {
		return a -> List.single(f(a));
	}

	/**
	 * Promotes this function to map over a List.
	 * 
	 * @return This function promoted to map over a List.
	 */
	public default F<List<A>, List<B>> mapList() {
		return x -> x.map(this);
	}

	/**
	 * Promotes this function so that it returns its result in a Stream. Kleisli
	 * arrow for Stream.
	 * 
	 * @return This function promoted to return its result in a Stream.
	 */
	public default F<A, Stream<B>> streamK() {
		return a -> Stream.single(f(a));
	}

	/**
	 * Promotes this function to map over a Stream.
	 * 
	 * @return This function promoted to map over a Stream.
	 */
	public default F<Stream<A>, Stream<B>> mapStream() {
		return x -> x.map(this);
	}

	/**
	 * Promotes this function so that it returns its result in a Array. Kleisli
	 * arrow for Array.
	 * 
	 * @return This function promoted to return its result in a Array.
	 */
	public default F<A, Array<B>> arrayK() {
		return a -> Array.single(f(a));
	}

	/**
	 * Promotes this function to map over a Array.
	 * 
	 * @return This function promoted to map over a Array.
	 */
	public default F<Array<A>, Array<B>> mapArray() {
		return x -> x.map(this);
	}

	/**
	 * Returns a function that comaps over a given actor.
	 * 
	 * @return A function that comaps over a given actor.
	 */
	public default F<Actor<B>, Actor<A>> comapActor() {
		return a -> a.comap(this);
	}

	/**
	 * Promotes this function to a concurrent function that returns a Promise of
	 * a value.
	 * 
	 * @param s
	 *            A parallel strategy for concurrent execution.
	 * @return A concurrent function that returns a Promise of a value.
	 */
	public default F<A, Promise<B>> promiseK(final Strategy<Unit> s) {
		return Promise.promise(s, this);
	}

	/**
	 * Promotes this function to map over a Promise.
	 * 
	 * @return This function promoted to map over Promises.
	 */
	public default F<Promise<A>, Promise<B>> mapPromise() {
		return p -> p.fmap(this);
	}

	/**
	 * Promotes this function so that it returns its result on the left side of
	 * an Either. Kleisli arrow for the Either left projection.
	 * 
	 * @return This function promoted to return its result on the left side of
	 *         an Either.
	 */
	public default <C> F<A, Either<B, C>> eitherLeftK() {
		return Either.<B, C> left_().o(this);
	}

	/**
	 * Promotes this function so that it returns its result on the right side of
	 * an Either. Kleisli arrow for the Either right projection.
	 * 
	 * @return This function promoted to return its result on the right side of
	 *         an Either.
	 */
	public default <C> F<A, Either<C, B>> eitherRightK() {
		return Either.<C, B> right_().o(this);
	}

	/**
	 * Promotes this function to map over the left side of an Either.
	 * 
	 * @return This function promoted to map over the left side of an Either.
	 */
	public default <X> F<Either<A, X>, Either<B, X>> mapLeft() {
		return Either.<A, X, B> leftMap_().f(this);
	}

	/**
	 * Promotes this function to map over the right side of an Either.
	 * 
	 * @return This function promoted to map over the right side of an Either.
	 */
	public default <X> F<Either<X, A>, Either<X, B>> mapRight() {
		return Either.<X, A, B> rightMap_().f(this);
	}

	/**
	 * Returns a function that returns the left side of a given Either, or this
	 * function applied to the right side.
	 * 
	 * @return a function that returns the left side of a given Either, or this
	 *         function applied to the right side.
	 */
	public default F<Either<B, A>, B> onLeft() {
		return e -> e.left().on(this);
	}

	/**
	 * Returns a function that returns the right side of a given Either, or this
	 * function applied to the left side.
	 * 
	 * @return a function that returns the right side of a given Either, or this
	 *         function applied to the left side.
	 */
	public default F<Either<A, B>, B> onRight() {
		return e -> e.right().on(this);
	}

	/**
	 * Promotes this function to return its value in an Iterable.
	 * 
	 * @return This function promoted to return its value in an Iterable.
	 */
	public default F<A, IterableW<B>> iterableK() {
		return IterableW.<A, B> arrow().f(this);
	}

	/**
	 * Promotes this function to map over Iterables.
	 * 
	 * @return This function promoted to map over Iterables.
	 */
	public default F<Iterable<A>, IterableW<B>> mapIterable() {
		return IterableW.<A, B> map().f(this)
				.o(IterableW.<A, Iterable<A>> wrap());
	}

	/**
	 * Promotes this function to return its value in a NonEmptyList.
	 * 
	 * @return This function promoted to return its value in a NonEmptyList.
	 */
	public default F<A, NonEmptyList<B>> nelK() {
		return NonEmptyList.<B> nel().o(this);
	}

	/**
	 * Promotes this function to map over a NonEmptyList.
	 * 
	 * @return This function promoted to map over a NonEmptyList.
	 */
	public default F<NonEmptyList<A>, NonEmptyList<B>> mapNel() {
		return l -> l.map(this);
	}

	/**
	 * Promotes this function to return its value in a Set.
	 * 
	 * @param o
	 *            An order for the set.
	 * @return This function promoted to return its value in a Set.
	 */
	public default F<A, Set<B>> setK(final Ord<B> o) {
		return a -> Set.single(o, f(a));
	}

	/**
	 * Promotes this function to map over a Set.
	 * 
	 * @param o
	 *            An order for the resulting set.
	 * @return This function promoted to map over a Set.
	 */
	public default F<Set<A>, Set<B>> mapSet(final Ord<B> o) {
		return s -> s.map(o, this);
	}

	/**
	 * Promotes this function to return its value in a Tree.
	 * 
	 * @return This function promoted to return its value in a Tree.
	 */
	public default F<A, Tree<B>> treeK() {
		return a -> Tree.leaf(f(a));
	}

	/**
	 * Promotes this function to map over a Tree.
	 * 
	 * @return This function promoted to map over a Tree.
	 */
	public default F<Tree<A>, Tree<B>> mapTree() {
		return Tree.<A, B> fmap_().f(this);
	}

	/**
	 * Returns a function that maps this function over a tree and folds it with
	 * the given monoid.
	 * 
	 * @param m
	 *            The monoid with which to fold the mapped tree.
	 * @return a function that maps this function over a tree and folds it with
	 *         the given monoid.
	 */
	public default F<Tree<A>, B> foldMapTree(final Monoid<B> m) {
		return Tree.foldMap_(this, m);
	}

	/**
	 * Promotes this function to return its value in a TreeZipper.
	 * 
	 * @return This function promoted to return its value in a TreeZipper.
	 */
	public default F<A, TreeZipper<B>> treeZipperK() {
		return treeK().andThen(TreeZipper.<B> fromTree());
	}

	/**
	 * Promotes this function to map over a TreeZipper.
	 * 
	 * @return This function promoted to map over a TreeZipper.
	 */
	public default F<TreeZipper<A>, TreeZipper<B>> mapTreeZipper() {
		return z -> z.map(this);
	}

	/**
	 * Promotes this function so that it returns its result on the failure side
	 * of a Validation. Kleisli arrow for the Validation failure projection.
	 * 
	 * @return This function promoted to return its result on the failure side
	 *         of a Validation.
	 */
	public default <C> F<A, Validation<B, C>> failK() {
		return a -> Validation.fail(f(a));
	}

	/**
	 * Promotes this function so that it returns its result on the success side
	 * of an Validation. Kleisli arrow for the Validation success projection.
	 * 
	 * @return This function promoted to return its result on the success side
	 *         of an Validation.
	 */
	public default <C> F<A, Validation<C, B>> successK() {
		return a -> Validation.success(f(a));
	}

	/**
	 * Promotes this function to map over the failure side of a Validation.
	 * 
	 * @return This function promoted to map over the failure side of a
	 *         Validation.
	 */
	public default <X> F<Validation<A, X>, Validation<B, X>> mapFail() {
		return v -> v.f().map(this);
	}

	/**
	 * Promotes this function to map over the success side of a Validation.
	 * 
	 * @return This function promoted to map over the success side of a
	 *         Validation.
	 */
	public default <X> F<Validation<X, A>, Validation<X, B>> mapSuccess() {
		return v -> v.map(this);
	}

	/**
	 * Returns a function that returns the failure side of a given Validation,
	 * or this function applied to the success side.
	 * 
	 * @return a function that returns the failure side of a given Validation,
	 *         or this function applied to the success side.
	 */
	public default F<Validation<B, A>, B> onFail() {
		return v -> v.f().on(this);
	}

	/**
	 * Returns a function that returns the success side of a given Validation,
	 * or this function applied to the failure side.
	 * 
	 * @return a function that returns the success side of a given Validation,
	 *         or this function applied to the failure side.
	 */
	public default F<Validation<A, B>, B> onSuccess() {
		return v -> v.on(this);
	}

	/**
	 * Promotes this function to return its value in a Zipper.
	 * 
	 * @return This function promoted to return its value in a Zipper.
	 */
	public default F<A, Zipper<B>> zipperK() {
		return streamK().andThen(s -> fromStream(s).some());
	}

	/**
	 * Promotes this function to map over a Zipper.
	 * 
	 * @return This function promoted to map over a Zipper.
	 */
	public default F<Zipper<A>, Zipper<B>> mapZipper() {
		return z -> z.map(this);
	}

	/**
	 * Promotes this function to map over an Equal as a contravariant functor.
	 * 
	 * @return This function promoted to map over an Equal as a contravariant
	 *         functor.
	 */
	public default F<Equal<B>, Equal<A>> comapEqual() {
		return e -> e.comap(this);
	}

	/**
	 * Promotes this function to map over a Hash as a contravariant functor.
	 * 
	 * @return This function promoted to map over a Hash as a contravariant
	 *         functor.
	 */
	public default F<Hash<B>, Hash<A>> comapHash() {
		return h -> h.comap(this);
	}

	/**
	 * Promotes this function to map over a Show as a contravariant functor.
	 * 
	 * @return This function promoted to map over a Show as a contravariant
	 *         functor.
	 */
	public default F<Show<B>, Show<A>> comapShow() {
		return s -> s.comap(this);
	}

	/**
	 * Promotes this function to map over the first element of a pair.
	 * 
	 * @return This function promoted to map over the first element of a pair.
	 */
	public default <C> F<P2<A, C>, P2<B, C>> mapFst() {
		return P2.map1_(this);
	}

	/**
	 * Promotes this function to map over the second element of a pair.
	 * 
	 * @return This function promoted to map over the second element of a pair.
	 */
	public default <C> F<P2<C, A>, P2<C, B>> mapSnd() {
		return P2.map2_(this);
	}

	/**
	 * Promotes this function to map over both elements of a pair.
	 * 
	 * @return This function promoted to map over both elements of a pair.
	 */
	public default F<P2<A, A>, P2<B, B>> mapBoth() {
		return aap2 -> P2.map(this, aap2);
	}

	/**
	 * Maps this function over a SynchronousQueue.
	 * 
	 * @param as
	 *            A SynchronousQueue to map this function over.
	 * @return A new SynchronousQueue with this function applied to each
	 *         element.
	 */
	public default SynchronousQueue<B> mapJ(final SynchronousQueue<A> as) {
		final SynchronousQueue<B> bs = new SynchronousQueue<B>();
		bs.addAll(iterableStream(as).map(this).toCollection());
		return bs;
	}

	/**
	 * Maps this function over a PriorityBlockingQueue.
	 * 
	 * @param as
	 *            A PriorityBlockingQueue to map this function over.
	 * @return A new PriorityBlockingQueue with this function applied to each
	 *         element.
	 */
	public default PriorityBlockingQueue<B> mapJ(
			final PriorityBlockingQueue<A> as) {
		return new PriorityBlockingQueue<B>(iterableStream(as).map(this)
				.toCollection());
	}

	/**
	 * Maps this function over a LinkedBlockingQueue.
	 * 
	 * @param as
	 *            A LinkedBlockingQueue to map this function over.
	 * @return A new LinkedBlockingQueue with this function applied to each
	 *         element.
	 */
	public default LinkedBlockingQueue<B> mapJ(final LinkedBlockingQueue<A> as) {
		return new LinkedBlockingQueue<B>(iterableStream(as).map(this)
				.toCollection());
	}

	/**
	 * Maps this function over a CopyOnWriteArraySet.
	 * 
	 * @param as
	 *            A CopyOnWriteArraySet to map this function over.
	 * @return A new CopyOnWriteArraySet with this function applied to each
	 *         element.
	 */
	public default CopyOnWriteArraySet<B> mapJ(final CopyOnWriteArraySet<A> as) {
		return new CopyOnWriteArraySet<B>(iterableStream(as).map(this)
				.toCollection());
	}

	/**
	 * Maps this function over a CopyOnWriteArrayList.
	 * 
	 * @param as
	 *            A CopyOnWriteArrayList to map this function over.
	 * @return A new CopyOnWriteArrayList with this function applied to each
	 *         element.
	 */
	public default CopyOnWriteArrayList<B> mapJ(final CopyOnWriteArrayList<A> as) {
		return new CopyOnWriteArrayList<B>(iterableStream(as).map(this)
				.toCollection());
	}

	/**
	 * Maps this function over a ConcurrentLinkedQueue.
	 * 
	 * @param as
	 *            A ConcurrentLinkedQueue to map this function over.
	 * @return A new ConcurrentLinkedQueue with this function applied to each
	 *         element.
	 */
	public default ConcurrentLinkedQueue<B> mapJ(
			final ConcurrentLinkedQueue<A> as) {
		return new ConcurrentLinkedQueue<B>(iterableStream(as).map(this)
				.toCollection());
	}

	/**
	 * Maps this function over an ArrayBlockingQueue.
	 * 
	 * @param as
	 *            An ArrayBlockingQueue to map this function over.
	 * @return A new ArrayBlockingQueue with this function applied to each
	 *         element.
	 */
	public default ArrayBlockingQueue<B> mapJ(final ArrayBlockingQueue<A> as) {
		final ArrayBlockingQueue<B> bs = new ArrayBlockingQueue<B>(as.size());
		bs.addAll(iterableStream(as).map(this).toCollection());
		return bs;
	}

	/**
	 * Maps this function over a TreeSet.
	 * 
	 * @param as
	 *            A TreeSet to map this function over.
	 * @return A new TreeSet with this function applied to each element.
	 */
	public default TreeSet<B> mapJ(final TreeSet<A> as) {
		return new TreeSet<B>(iterableStream(as).map(this).toCollection());
	}

	/**
	 * Maps this function over a PriorityQueue.
	 * 
	 * @param as
	 *            A PriorityQueue to map this function over.
	 * @return A new PriorityQueue with this function applied to each element.
	 */
	public default PriorityQueue<B> mapJ(final PriorityQueue<A> as) {
		return new PriorityQueue<B>(iterableStream(as).map(this).toCollection());
	}

	/**
	 * Maps this function over a LinkedList.
	 * 
	 * @param as
	 *            A LinkedList to map this function over.
	 * @return A new LinkedList with this function applied to each element.
	 */
	public default LinkedList<B> mapJ(final LinkedList<A> as) {
		return new LinkedList<B>(iterableStream(as).map(this).toCollection());
	}

	/**
	 * Maps this function over an ArrayList.
	 * 
	 * @param as
	 *            An ArrayList to map this function over.
	 * @return A new ArrayList with this function applied to each element.
	 */
	public default ArrayList<B> mapJ(final ArrayList<A> as) {
		return new ArrayList<B>(iterableStream(as).map(this).toCollection());
	}
}
