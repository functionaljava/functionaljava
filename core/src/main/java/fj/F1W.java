package fj;

import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.*;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

/**
 * Created by MarkPerry on 22/01/2015.
 */
public abstract class F1W<A, B> implements F<A, B> {

    /**
     * Function composition
     *
     * @param g A function to compose with this one.
     * @return The composed function such that this function is applied last.
     */
    public <C> F<C, B> o(final F<C, A> g) {
        return F1Functions.o(this, g);
    }

    /**
     * First-class function composition
     *
     * @return A function that composes this function with another.
     */
    public <C> F<F<C, A>, F<C, B>> o() {
        return F1Functions.o(this);
    }

    /**
     * Function composition flipped.
     *
     * @param g A function with which to compose this one.
     * @return The composed function such that this function is applied first.
     */
    @SuppressWarnings({"unchecked"})
    public <C> F<A, C> andThen(final F<B, C> g) {
        return F1Functions.andThen(this, g);
    }

    /**
     * First-class composition flipped.
     *
     * @return A function that invokes this function and then a given function on the result.
     */
    public <C> F<F<B, C>, F<A, C>> andThen() {
        return F1Functions.andThen(this);
    }

    /**
     * Binds a given function across this function (Reader Monad).
     *
     * @param g A function that takes the return value of this function as an argument, yielding a new function.
     * @return A function that invokes this function on its argument and then the given function on the result.
     */
    public <C> F<A, C> bind(final F<B, F<A, C>> g) {
        return F1Functions.bind(this, g);
    }


    /**
     * First-class function binding.
     *
     * @return A function that binds another function across this function.
     */
    public <C> F<F<B, F<A, C>>, F<A, C>> bind() {
        return F1Functions.bind(this);
    }

    /**
     * Function application in an environment (Applicative Functor).
     *
     * @param g A function with the same argument type as this function, yielding a function that takes the return
     *          value of this function.
     * @return A new function that invokes the given function on its argument, yielding a new function that is then
     *         applied to the result of applying this function to the argument.
     */
    public <C> F<A, C> apply(final F<A, F<B, C>> g) {
        return F1Functions.apply(this, g);
    }


    /**
     * First-class function application in an environment.
     *
     * @return A function that applies a given function within the environment of this function.
     */
    public <C> F<F<A, F<B, C>>, F<A, C>> apply() {
        return F1Functions.apply(this);
    }

    /**
     * Applies this function over the arguments of another function.
     *
     * @param g The function over whose arguments to apply this function.
     * @return A new function that invokes this function on its arguments before invoking the given function.
     */
    public <C> F<A, F<A, C>> on(final F<B, F<B, C>> g) {
        return F1Functions.on(this, g);
    }


    /**
     * Applies this function over the arguments of another function.
     *
     * @return A function that applies this function over the arguments of another function.
     */
    public <C> F<F<B, F<B, C>>, F<A, F<A, C>>> on() {
        return F1Functions.on(this);
    }

    /**
     * Promotes this function so that it returns its result in a product-1. Kleisli arrow for P1.
     *
     * @return This function promoted to return its result in a product-1.
     */
    public F<A, P1<B>> lazy() {
        return F1Functions.lazy(this);
    }


    /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument to return a lazy value.
     */
    public P1<B> lazy(final A a) {
        return F1Functions.f(this, a);
    }

    /**
     * Promotes this function to map over a product-1.
     *
     * @return This function promoted to map over a product-1.
     */
    public F<P1<A>, P1<B>> mapP1() {
        return F1Functions.mapP1(this);
    }

    /**
     * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
     *
     * @return This function promoted to return its result in an Option.
     */
    public F<A, Option<B>> optionK() {
        return F1Functions.optionK(this);
    }


    /**
     * Promotes this function to map over an optional value.
     *
     * @return This function promoted to map over an optional value.
     */
    public F<Option<A>, Option<B>> mapOption() {
        return F1Functions.mapOption(this);
    }

    /**
     * Promotes this function so that it returns its result in a List. Kleisli arrow for List.
     *
     * @return This function promoted to return its result in a List.
     */
    public F<A, List<B>> listK() {
        return F1Functions.listK(this);
    }

    /**
     * Promotes this function to map over a List.
     *
     * @return This function promoted to map over a List.
     */
    public F<List<A>, List<B>> mapList() {
        return F1Functions.mapList(this);
    }

    /**
     * Promotes this function so that it returns its result in a Stream. Kleisli arrow for Stream.
     *
     * @return This function promoted to return its result in a Stream.
     */
    public F<A, Stream<B>> streamK() {
        return F1Functions.streamK(this);
    }

    /**
     * Promotes this function to map over a Stream.
     *
     * @return This function promoted to map over a Stream.
     */
    public F<Stream<A>, Stream<B>> mapStream() {
        return F1Functions.mapStream(this);
    }

    /**
     * Promotes this function so that it returns its result in a Array. Kleisli arrow for Array.
     *
     * @return This function promoted to return its result in a Array.
     */
    public F<A, Array<B>> arrayK() {
        return F1Functions.arrayK(this);

    }

    /**
     * Promotes this function to map over a Array.
     *
     * @return This function promoted to map over a Array.
     */
    public F<Array<A>, Array<B>> mapArray() {
        return F1Functions.mapArray(this);
    }

    /**
     * Returns a function that comaps over a given actor.
     *
     * @return A function that comaps over a given actor.
     */
    public F<Actor<B>, Actor<A>> comapActor() {
        return F1Functions.comapActor(this);
    }

    /**
     * Promotes this function to a concurrent function that returns a Promise of a value.
     *
     * @param s A parallel strategy for concurrent execution.
     * @return A concurrent function that returns a Promise of a value.
     */
    public F<A, Promise<B>> promiseK(final Strategy<Unit> s) {
        return F1Functions.promiseK(this, s);
    }

    /**
     * Promotes this function to map over a Promise.
     *
     * @return This function promoted to map over Promises.
     */
    public F<Promise<A>, Promise<B>> mapPromise() {
        return F1Functions.mapPromise(this);
    }

    /**
     * Promotes this function so that it returns its result on the left side of an Either.
     * Kleisli arrow for the Either left projection.
     *
     * @return This function promoted to return its result on the left side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    public <C> F<A, Either<B, C>> eitherLeftK() {
        return F1Functions.eitherLeftK(this);
    }

    /**
     * Promotes this function so that it returns its result on the right side of an Either.
     * Kleisli arrow for the Either right projection.
     *
     * @return This function promoted to return its result on the right side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    public <C> F<A, Either<C, B>> eitherRightK() {
        return F1Functions.eitherRightK(this);
    }

    /**
     * Promotes this function to map over the left side of an Either.
     *
     * @return This function promoted to map over the left side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    public <X> F<Either<A, X>, Either<B, X>> mapLeft() {
        return F1Functions.mapLeft(this);
    }

    /**
     * Promotes this function to map over the right side of an Either.
     *
     * @return This function promoted to map over the right side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    public <X> F<Either<X, A>, Either<X, B>> mapRight() {
        return F1Functions.mapRight(this);
    }

    /**
     * Returns a function that returns the left side of a given Either, or this function applied to the right side.
     *
     * @return a function that returns the left side of a given Either, or this function applied to the right side.
     */
    public F<Either<B, A>, B> onLeft() {
        return F1Functions.onLeft(this);
    }

    /**
     * Returns a function that returns the right side of a given Either, or this function applied to the left side.
     *
     * @return a function that returns the right side of a given Either, or this function applied to the left side.
     */
    public F<Either<A, B>, B> onRight() {
        return F1Functions.onRight(this);
    }

    /**
     * Promotes this function to return its value in an Iterable.
     *
     * @return This function promoted to return its value in an Iterable.
     */
    @SuppressWarnings({"unchecked"})
    public F<A, IterableW<B>> iterableK() {
        return F1Functions.iterableK(this);
    }

    /**
     * Promotes this function to map over Iterables.
     *
     * @return This function promoted to map over Iterables.
     */
    @SuppressWarnings({"unchecked"})
    public F<Iterable<A>, IterableW<B>> mapIterable() {
        return F1Functions.mapIterable(this);
    }

    /**
     * Promotes this function to return its value in a NonEmptyList.
     *
     * @return This function promoted to return its value in a NonEmptyList.
     */
    @SuppressWarnings({"unchecked"})
    public F<A, NonEmptyList<B>> nelK() {
        return F1Functions.nelK(this);
    }

    /**
     * Promotes this function to map over a NonEmptyList.
     *
     * @return This function promoted to map over a NonEmptyList.
     */
    public F<NonEmptyList<A>, NonEmptyList<B>> mapNel() {
        return F1Functions.mapNel(this);
    }

    /**
     * Promotes this function to return its value in a Set.
     *
     * @param o An order for the set.
     * @return This function promoted to return its value in a Set.
     */
    public F<A, Set<B>> setK(final Ord<B> o) {
        return F1Functions.setK(this, o);
    }

    /**
     * Promotes this function to map over a Set.
     *
     * @param o An order for the resulting set.
     * @return This function promoted to map over a Set.
     */
    public F<Set<A>, Set<B>> mapSet(final Ord<B> o) {
        return F1Functions.mapSet(this, o);
    }

    /**
     * Promotes this function to return its value in a Tree.
     *
     * @return This function promoted to return its value in a Tree.
     */
    public F<A, Tree<B>> treeK() {
        return F1Functions.treeK(this);
    }

    /**
     * Promotes this function to map over a Tree.
     *
     * @return This function promoted to map over a Tree.
     */
    @SuppressWarnings({"unchecked"})
    public F<Tree<A>, Tree<B>> mapTree() {
        return F1Functions.mapTree(this);
    }

    /**
     * Returns a function that maps this function over a tree and folds it with the given monoid.
     *
     * @param m The monoid with which to fold the mapped tree.
     * @return a function that maps this function over a tree and folds it with the given monoid.
     */
    public F<Tree<A>, B> foldMapTree(final Monoid<B> m) {
        return F1Functions.foldMapTree(this, m);
    }

    /**
     * Promotes this function to return its value in a TreeZipper.
     *
     * @return This function promoted to return its value in a TreeZipper.
     */
    public F<A, TreeZipper<B>> treeZipperK() {
        return F1Functions.treeZipperK(this);
    }

    /**
     * Promotes this function to map over a TreeZipper.
     *
     * @return This function promoted to map over a TreeZipper.
     */
    public F<TreeZipper<A>, TreeZipper<B>> mapTreeZipper() {
        return F1Functions.mapTreeZipper(this);
    }

    /**
     * Promotes this function so that it returns its result on the failure side of a Validation.
     * Kleisli arrow for the Validation failure projection.
     *
     * @return This function promoted to return its result on the failure side of a Validation.
     */
    public <C> F<A, Validation<B, C>> failK() {
        return F1Functions.failK(this);
    }

    /**
     * Promotes this function so that it returns its result on the success side of an Validation.
     * Kleisli arrow for the Validation success projection.
     *
     * @return This function promoted to return its result on the success side of an Validation.
     */
    public <C> F<A, Validation<C, B>> successK() {
        return F1Functions.successK(this);
    }

    /**
     * Promotes this function to map over the failure side of a Validation.
     *
     * @return This function promoted to map over the failure side of a Validation.
     */
    public <X> F<Validation<A, X>, Validation<B, X>> mapFail() {
        return F1Functions.mapFail(this);
    }

    /**
     * Promotes this function to map over the success side of a Validation.
     *
     * @return This function promoted to map over the success side of a Validation.
     */
    public <X> F<Validation<X, A>, Validation<X, B>> mapSuccess() {
        return F1Functions.mapSuccess(this);
    }

    /**
     * Returns a function that returns the failure side of a given Validation,
     * or this function applied to the success side.
     *
     * @return a function that returns the failure side of a given Validation,
     *         or this function applied to the success side.
     */
    public F<Validation<B, A>, B> onFail() {
        return F1Functions.onFail(this);
    }

    /**
     * Returns a function that returns the success side of a given Validation,
     * or this function applied to the failure side.
     *
     * @return a function that returns the success side of a given Validation,
     *         or this function applied to the failure side.
     */
    public F<Validation<A, B>, B> onSuccess() {
        return F1Functions.onSuccess(this);
    }

    /**
     * Promotes this function to return its value in a Zipper.
     *
     * @return This function promoted to return its value in a Zipper.
     */
    public F<A, Zipper<B>> zipperK() {
        return F1Functions.zipperK(this);
    }

    /**
     * Promotes this function to map over a Zipper.
     *
     * @return This function promoted to map over a Zipper.
     */
    public F<Zipper<A>, Zipper<B>> mapZipper() {
        return F1Functions.mapZipper(this);
    }

    /**
     * Promotes this function to map over an Equal as a contravariant functor.
     *
     * @return This function promoted to map over an Equal as a contravariant functor.
     */
    public F<Equal<B>, Equal<A>> comapEqual() {
        return F1Functions.comapEqual(this);
    }

    /**
     * Promotes this function to map over a Hash as a contravariant functor.
     *
     * @return This function promoted to map over a Hash as a contravariant functor.
     */
    public F<Hash<B>, Hash<A>> comapHash() {
        return F1Functions.comapHash(this);
    }

    /**
     * Promotes this function to map over a Show as a contravariant functor.
     *
     * @return This function promoted to map over a Show as a contravariant functor.
     */
    public F<Show<B>, Show<A>> comapShow() {
        return F1Functions.comapShow(this);
    }

    /**
     * Promotes this function to map over the first element of a pair.
     *
     * @return This function promoted to map over the first element of a pair.
     */
    public <C> F<P2<A, C>, P2<B, C>> mapFst() {
        return F1Functions.mapFst(this);
    }

    /**
     * Promotes this function to map over the second element of a pair.
     *
     * @return This function promoted to map over the second element of a pair.
     */
    public <C> F<P2<C, A>, P2<C, B>> mapSnd() {
        return F1Functions.mapSnd(this);
    }

    /**
     * Promotes this function to map over both elements of a pair.
     *
     * @return This function promoted to map over both elements of a pair.
     */
    public F<P2<A, A>, P2<B, B>> mapBoth() {
        return F1Functions.mapBoth(this);
    }

    /**
     * Maps this function over a SynchronousQueue.
     *
     * @param as A SynchronousQueue to map this function over.
     * @return A new SynchronousQueue with this function applied to each element.
     */
    public SynchronousQueue<B> mapJ(final SynchronousQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }


    /**
     * Maps this function over a PriorityBlockingQueue.
     *
     * @param as A PriorityBlockingQueue to map this function over.
     * @return A new PriorityBlockingQueue with this function applied to each element.
     */
    public PriorityBlockingQueue<B> mapJ(final PriorityBlockingQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a LinkedBlockingQueue.
     *
     * @param as A LinkedBlockingQueue to map this function over.
     * @return A new LinkedBlockingQueue with this function applied to each element.
     */
    public LinkedBlockingQueue<B> mapJ(final LinkedBlockingQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a CopyOnWriteArraySet.
     *
     * @param as A CopyOnWriteArraySet to map this function over.
     * @return A new CopyOnWriteArraySet with this function applied to each element.
     */
    public CopyOnWriteArraySet<B> mapJ(final CopyOnWriteArraySet<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a CopyOnWriteArrayList.
     *
     * @param as A CopyOnWriteArrayList to map this function over.
     * @return A new CopyOnWriteArrayList with this function applied to each element.
     */
    public CopyOnWriteArrayList<B> mapJ(final CopyOnWriteArrayList<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a ConcurrentLinkedQueue.
     *
     * @param as A ConcurrentLinkedQueue to map this function over.
     * @return A new ConcurrentLinkedQueue with this function applied to each element.
     */
    public ConcurrentLinkedQueue<B> mapJ(final ConcurrentLinkedQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over an ArrayBlockingQueue.
     *
     * @param as An ArrayBlockingQueue to map this function over.
     * @return A new ArrayBlockingQueue with this function applied to each element.
     */
    public ArrayBlockingQueue<B> mapJ(final ArrayBlockingQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }


    /**
     * Maps this function over a TreeSet.
     *
     * @param as A TreeSet to map this function over.
     * @return A new TreeSet with this function applied to each element.
     */
    public TreeSet<B> mapJ(final TreeSet<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a PriorityQueue.
     *
     * @param as A PriorityQueue to map this function over.
     * @return A new PriorityQueue with this function applied to each element.
     */
    public PriorityQueue<B> mapJ(final PriorityQueue<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over a LinkedList.
     *
     * @param as A LinkedList to map this function over.
     * @return A new LinkedList with this function applied to each element.
     */
    public LinkedList<B> mapJ(final LinkedList<A> as) {
        return F1Functions.mapJ(this, as);
    }

    /**
     * Maps this function over an ArrayList.
     *
     * @param as An ArrayList to map this function over.
     * @return A new ArrayList with this function applied to each element.
     */
    public ArrayList<B> mapJ(final ArrayList<A> as) {
        return F1Functions.mapJ(this, as);
    }

    public <C> F<A, C> map(F<B, C> f) {
        return F1Functions.map(this, f);
    }

    public <C> F<C, B> contramap(F<C, A> f) {
        return F1Functions.contramap(this, f);
    }

}
