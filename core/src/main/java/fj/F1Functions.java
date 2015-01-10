package fj;

import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.*;
import fj.function.Try1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.*;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F1Functions {


    /**
     * Function composition
     *
     * @param g A function to compose with this one.
     * @return The composed function such that this function is applied last.
     */
    static public <A, B, C> F<C, B> o(final F<A, B> f, final F<C, A> g) {
        return c -> f.f(g.f(c));
    }

    /**
     * First-class function composition
     *
     * @return A function that composes this function with another.
     */
    static public <A, B,C> F<F<C, A>, F<C, B>> o(final F<A, B> f) {
        return g -> o(f, g);
    }

    /**
     * Function composition flipped.
     *
     * @param g A function with which to compose this one.
     * @return The composed function such that this function is applied first.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B, C> F<A, C> andThen(final F<A, B> f, final F<B, C> g) {
        return o(g, f);
    }

    /**
     * First-class composition flipped.
     *
     * @return A function that invokes this function and then a given function on the result.
     */
    static public <A, B, C> F<F<B, C>, F<A, C>> andThen(final F<A, B> f) {
        return g -> andThen(f, g);
    }

    /**
     * Binds a given function across this function (Reader Monad).
     *
     * @param g A function that takes the return value of this function as an argument, yielding a new function.
     * @return A function that invokes this function on its argument and then the given function on the result.
     */
    static public <A, B, C> F<A, C> bind(final F<A, B> f, final F<B, F<A, C>> g) {
        return a ->  g.f(f.f(a)).f(a);
    }

    /**
     * First-class function binding.
     *
     * @return A function that binds another function across this function.
     */
    static public <A, B, C> F<F<B, F<A, C>>, F<A, C>> bind(final F<A, B> f) {
        return g -> bind(f, g);
    }

    /**
     * Function application in an environment (Applicative Functor).
     *
     * @param g A function with the same argument type as this function, yielding a function that takes the return
     *          value of this function.
     * @return A new function that invokes the given function on its argument, yielding a new function that is then
     *         applied to the result of applying this function to the argument.
     */
    static public <A, B, C> F<A, C> apply(final F<A, B> f, final F<A, F<B, C>> g) {
        return a -> g.f(a).f(f.f(a));
    }

    /**
     * First-class function application in an environment.
     *
     * @return A function that applies a given function within the environment of this function.
     */
    static public <A, B, C> F<F<A, F<B, C>>, F<A, C>> apply(final F<A, B> f) {
        return g -> apply(f, g);
    }

    /**
     * Applies this function over the arguments of another function.
     *
     * @param g The function over whose arguments to apply this function.
     * @return A new function that invokes this function on its arguments before invoking the given function.
     */
    static public <A, B, C> F<A, F<A, C>> on(final F<A, B> f, final F<B, F<B, C>> g) {
        return a1 -> a2 -> g.f(f.f(a1)).f(f.f(a2));
    }



    /**
     * Applies this function over the arguments of another function.
     *
     * @return A function that applies this function over the arguments of another function.
     */
    static public <A, B, C> F<F<B, F<B, C>>, F<A, F<A, C>>> on(final F<A, B> f) {
        return g -> on(f, g);
    }

    /**
     * Promotes this function so that it returns its result in a product-1. Kleisli arrow for P1.
     *
     * @return This function promoted to return its result in a product-1.
     */
    static public <A, B> F<A, P1<B>> lazy(final F<A, B> f) {
       return a -> P.lazy(u -> f.f(a));
    }

    /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument to return a lazy value.
     */
    static public <A, B> P1<B> f(final F<A, B> f, final A a) {
        return P.lazy(u -> f.f(a));
    }

    /**
     * Promotes this function to map over a product-1.
     *
     * @return This function promoted to map over a product-1.
     */
    static public <A, B> F<P1<A>, P1<B>> mapP1(final F<A, B> f) {
        return p -> p.map(f);
    }

    /**
     * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
     *
     * @return This function promoted to return its result in an Option.
     */
    static public <A, B> F<A, Option<B>> optionK(final F<A, B> f) {
        return a -> some(f.f(a));
    }

    /**
     * Promotes this function to map over an optional value.
     *
     * @return This function promoted to map over an optional value.
     */
    static public <A, B> F<Option<A>, Option<B>> mapOption(final F<A, B> f) {
        return o -> o.map(f);
    }

    /**
     * Promotes this function so that it returns its result in a List. Kleisli arrow for List.
     *
     * @return This function promoted to return its result in a List.
     */
    static public <A, B> F<A, List<B>> listK(final F<A, B> f) {
        return a -> List.single(f.f(a));
    }

    /**
     * Promotes this function to map over a List.
     *
     * @return This function promoted to map over a List.
     */
    static public <A, B> F<List<A>, List<B>> mapList(final F<A, B> f) {
        return x -> x.map(f);
    }

    /**
     * Promotes this function so that it returns its result in a Stream. Kleisli arrow for Stream.
     *
     * @return This function promoted to return its result in a Stream.
     */
    static public <A, B> F<A, Stream<B>> streamK(final F<A, B> f) {
        return a -> Stream.single(f.f(a));
    }

    /**
     * Promotes this function to map over a Stream.
     *
     * @return This function promoted to map over a Stream.
     */
    static public <A, B> F<Stream<A>, Stream<B>> mapStream(final F<A, B> f) {
        return x -> x.map(f);
    }

    /**
     * Promotes this function so that it returns its result in a Array. Kleisli arrow for Array.
     *
     * @return This function promoted to return its result in a Array.
     */
    static public <A, B> F<A, Array<B>> arrayK(final F<A, B> f) {
        return a -> Array.single(f.f(a));

    }

    /**
     * Promotes this function to map over a Array.
     *
     * @return This function promoted to map over a Array.
     */
    static public <A, B> F<Array<A>, Array<B>> mapArray(final F<A, B> f) {
        return x -> x.map(f);
    }

    /**
     * Returns a function that comaps over a given actor.
     *
     * @return A function that comaps over a given actor.
     */
    static public <A, B> F<Actor<B>, Actor<A>> comapActor(final F<A, B> f) {
        return a -> a.comap(f);
    }

    /**
     * Promotes this function to a concurrent function that returns a Promise of a value.
     *
     * @param s A parallel strategy for concurrent execution.
     * @return A concurrent function that returns a Promise of a value.
     */
    static public <A, B> F<A, Promise<B>> promiseK(final F<A, B> f, final Strategy<Unit> s) {
        return Promise.promise(s, f);
    }

    /**
     * Promotes this function to map over a Promise.
     *
     * @return This function promoted to map over Promises.
     */
    static public <A, B> F<Promise<A>, Promise<B>> mapPromise(final F<A, B> f) {
        return p -> p.fmap(f);
    }

    /**
     * Promotes this function so that it returns its result on the left side of an Either.
     * Kleisli arrow for the Either left projection.
     *
     * @return This function promoted to return its result on the left side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B, C> F<A, Either<B, C>> eitherLeftK(final F<A, B> f) {
        return o(Either.<B, C>left_(), f);
    }

    /**
     * Promotes this function so that it returns its result on the right side of an Either.
     * Kleisli arrow for the Either right projection.
     *
     * @return This function promoted to return its result on the right side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B, C> F<A, Either<C, B>> eitherRightK(final F<A, B> f) {
        return o(Either.<C, B>right_(), f);
    }

    /**
     * Promotes this function to map over the left side of an Either.
     *
     * @return This function promoted to map over the left side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B, X> F<Either<A, X>, Either<B, X>> mapLeft(final F<A, B> f) {
        return Either.<A, X, B>leftMap_().f(f);
    }

    /**
     * Promotes this function to map over the right side of an Either.
     *
     * @return This function promoted to map over the right side of an Either.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B, X> F<Either<X, A>, Either<X, B>> mapRight(final F<A, B> f) {
        return Either.<X, A, B>rightMap_().f(f);
    }

    /**
     * Returns a function that returns the left side of a given Either, or this function applied to the right side.
     *
     * @return a function that returns the left side of a given Either, or this function applied to the right side.
     */
    static public <A, B> F<Either<B, A>, B> onLeft(final F<A, B> f) {
        return e -> e.left().on(f);
    }

    /**
     * Returns a function that returns the right side of a given Either, or this function applied to the left side.
     *
     * @return a function that returns the right side of a given Either, or this function applied to the left side.
     */
    static public <A, B> F<Either<A, B>, B> onRight(final F<A, B> f) {
        return e -> e.right().on(f);
    }

    /**
     * Promotes this function to return its value in an Iterable.
     *
     * @return This function promoted to return its value in an Iterable.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B> F<A, IterableW<B>> iterableK(final F<A, B> f) {
        return IterableW.<A, B>arrow().f(f);
    }

    /**
     * Promotes this function to map over Iterables.
     *
     * @return This function promoted to map over Iterables.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B> F<Iterable<A>, IterableW<B>> mapIterable(final F<A, B> f) {
        return F1Functions.o(IterableW.<A, B>map().f(f), IterableW.<A, Iterable<A>>wrap());
    }

    /**
     * Promotes this function to return its value in a NonEmptyList.
     *
     * @return This function promoted to return its value in a NonEmptyList.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B> F<A, NonEmptyList<B>> nelK(final F<A, B> f) {
        return o(NonEmptyList.<B>nel(), f);
    }

    /**
     * Promotes this function to map over a NonEmptyList.
     *
     * @return This function promoted to map over a NonEmptyList.
     */
    static public <A, B> F<NonEmptyList<A>, NonEmptyList<B>> mapNel(final F<A, B> f) {
        return list -> list.map(f);
    }

    /**
     * Promotes this function to return its value in a Set.
     *
     * @param o An order for the set.
     * @return This function promoted to return its value in a Set.
     */
    static public <A, B> F<A, Set<B>> setK(final F<A, B> f, final Ord<B> o
    ) {
        return a -> Set.single(o, f.f(a));
    }

    /**
     * Promotes this function to map over a Set.
     *
     * @param o An order for the resulting set.
     * @return This function promoted to map over a Set.
     */
    static public <A, B> F<Set<A>, Set<B>> mapSet(final F<A, B> f, final Ord<B> o) {
        return s -> s.map(o, f);
    }

    /**
     * Promotes this function to return its value in a Tree.
     *
     * @return This function promoted to return its value in a Tree.
     */
    static public <A, B> F<A, Tree<B>> treeK(final F<A, B> f) {
        return a -> Tree.leaf(f.f(a));
    }

    /**
     * Promotes this function to map over a Tree.
     *
     * @return This function promoted to map over a Tree.
     */
    @SuppressWarnings({"unchecked"})
    static public <A, B> F<Tree<A>, Tree<B>> mapTree(final F<A, B> f) {
        return Tree.<A, B>fmap_().f(f);
    }

    /**
     * Returns a function that maps this function over a tree and folds it with the given monoid.
     *
     * @param m The monoid with which to fold the mapped tree.
     * @return a function that maps this function over a tree and folds it with the given monoid.
     */
    static public <A, B> F<Tree<A>, B> foldMapTree(final F<A, B> f, final Monoid<B> m) {
        return Tree.foldMap_(f, m);
    }

    /**
     * Promotes this function to return its value in a TreeZipper.
     *
     * @return This function promoted to return its value in a TreeZipper.
     */
    static public <A, B> F<A, TreeZipper<B>> treeZipperK(final F<A, B> f) {
        return andThen(treeK(f), TreeZipper.<B>fromTree());
    }

    /**
     * Promotes this function to map over a TreeZipper.
     *
     * @return This function promoted to map over a TreeZipper.
     */
    static public <A, B> F<TreeZipper<A>, TreeZipper<B>> mapTreeZipper(final F<A, B> f) {
        return (z) -> z.map(f);
    }

    /**
     * Promotes this function so that it returns its result on the failure side of a Validation.
     * Kleisli arrow for the Validation failure projection.
     *
     * @return This function promoted to return its result on the failure side of a Validation.
     */
    static public <A, B, C> F<A, Validation<B, C>> failK(final F<A, B> f) {
        return a -> Validation.fail(f.f(a));

    }

    /**
     * Promotes this function so that it returns its result on the success side of an Validation.
     * Kleisli arrow for the Validation success projection.
     *
     * @return This function promoted to return its result on the success side of an Validation.
     */
    static public <A, B, C> F<A, Validation<C, B>> successK(final F<A, B> f) {
        return a -> Validation.success(f.f(a));
    }

    /**
     * Promotes this function to map over the failure side of a Validation.
     *
     * @return This function promoted to map over the failure side of a Validation.
     */
    static public <A, B, X> F<Validation<A, X>, Validation<B, X>> mapFail(final F<A, B> f) {
        return v -> v.f().map(f);
    }

    /**
     * Promotes this function to map over the success side of a Validation.
     *
     * @return This function promoted to map over the success side of a Validation.
     */
    static public <A, B, X> F<Validation<X, A>, Validation<X, B>> mapSuccess(final F<A, B> f) {
        return v -> v.map(f);
    }

    /**
     * Returns a function that returns the failure side of a given Validation,
     * or this function applied to the success side.
     *
     * @return a function that returns the failure side of a given Validation,
     *         or this function applied to the success side.
     */
    static public <A, B> F<Validation<B, A>, B> onFail(final F<A, B> f) {
        return v -> v.f().on(f);
    }

    /**
     * Returns a function that returns the success side of a given Validation,
     * or this function applied to the failure side.
     *
     * @return a function that returns the success side of a given Validation,
     *         or this function applied to the failure side.
     */
    static public <A, B> F<Validation<A, B>, B> onSuccess(final F<A, B> f) {
        return v -> v.on(f);
    }

    /**
     * Promotes this function to return its value in a Zipper.
     *
     * @return This function promoted to return its value in a Zipper.
     */
    static public <A, B> F<A, Zipper<B>> zipperK(final F<A, B> f) {
        return andThen(streamK(f), s -> fromStream(s).some());
    }

    /**
     * Promotes this function to map over a Zipper.
     *
     * @return This function promoted to map over a Zipper.
     */
    static public <A, B> F<Zipper<A>, Zipper<B>> mapZipper(final F<A, B> f) {
        return z -> z.map(f);
    }

    /**
     * Promotes this function to map over an Equal as a contravariant functor.
     *
     * @return This function promoted to map over an Equal as a contravariant functor.
     */
    static public <A, B> F<Equal<B>, Equal<A>> comapEqual(final F<A, B> f) {
        return e -> e.comap(f);
    }

    /**
     * Promotes this function to map over a Hash as a contravariant functor.
     *
     * @return This function promoted to map over a Hash as a contravariant functor.
     */
    static public <A, B> F<Hash<B>, Hash<A>> comapHash(final F<A, B> f) {
        return h -> h.comap(f);
    }

    /**
     * Promotes this function to map over a Show as a contravariant functor.
     *
     * @return This function promoted to map over a Show as a contravariant functor.
     */
    static public <A, B> F<Show<B>, Show<A>> comapShow(final F<A, B> f) {
        return s -> s.comap(f);
    }

    /**
     * Promotes this function to map over the first element of a pair.
     *
     * @return This function promoted to map over the first element of a pair.
     */
    static public <A, B, C> F<P2<A, C>, P2<B, C>> mapFst(final F<A, B> f) {
        return P2.map1_(f);
    }

    /**
     * Promotes this function to map over the second element of a pair.
     *
     * @return This function promoted to map over the second element of a pair.
     */
    static public <A, B, C> F<P2<C, A>, P2<C, B>> mapSnd(final F<A, B> f) {
        return P2.map2_(f);
    }

    /**
     * Promotes this function to map over both elements of a pair.
     *
     * @return This function promoted to map over both elements of a pair.
     */
    static public <A, B> F<P2<A, A>, P2<B, B>> mapBoth(final F<A, B> f) {
        return p2 -> P2.map(f, p2);
    }

    /**
     * Maps this function over a SynchronousQueue.
     *
     * @param as A SynchronousQueue to map this function over.
     * @return A new SynchronousQueue with this function applied to each element.
     */
    static public <A, B> SynchronousQueue<B> mapJ(final F<A, B> f, final SynchronousQueue<A> as) {
        final SynchronousQueue<B> bs = new SynchronousQueue<B>();
        bs.addAll(iterableStream(as).map(f).toCollection());
        return bs;
    }


    /**
     * Maps this function over a PriorityBlockingQueue.
     *
     * @param as A PriorityBlockingQueue to map this function over.
     * @return A new PriorityBlockingQueue with this function applied to each element.
     */
    static public <A, B> PriorityBlockingQueue<B> mapJ(final F<A, B> f, final PriorityBlockingQueue<A> as) {
        return new PriorityBlockingQueue<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a LinkedBlockingQueue.
     *
     * @param as A LinkedBlockingQueue to map this function over.
     * @return A new LinkedBlockingQueue with this function applied to each element.
     */
    static public <A, B> LinkedBlockingQueue<B> mapJ(final F<A, B> f, final LinkedBlockingQueue<A> as) {
        return new LinkedBlockingQueue<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a CopyOnWriteArraySet.
     *
     * @param as A CopyOnWriteArraySet to map this function over.
     * @return A new CopyOnWriteArraySet with this function applied to each element.
     */
    static public <A, B> CopyOnWriteArraySet<B> mapJ(final F<A, B> f, final CopyOnWriteArraySet<A> as) {
        return new CopyOnWriteArraySet<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a CopyOnWriteArrayList.
     *
     * @param as A CopyOnWriteArrayList to map this function over.
     * @return A new CopyOnWriteArrayList with this function applied to each element.
     */
    static public <A, B> CopyOnWriteArrayList<B> mapJ(final F<A, B> f, final CopyOnWriteArrayList<A> as) {
        return new CopyOnWriteArrayList<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a ConcurrentLinkedQueue.
     *
     * @param as A ConcurrentLinkedQueue to map this function over.
     * @return A new ConcurrentLinkedQueue with this function applied to each element.
     */
    static public <A, B> ConcurrentLinkedQueue<B> mapJ(final F<A, B> f, final ConcurrentLinkedQueue<A> as) {
        return new ConcurrentLinkedQueue<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over an ArrayBlockingQueue.
     *
     * @param as An ArrayBlockingQueue to map this function over.
     * @return A new ArrayBlockingQueue with this function applied to each element.
     */
    static public <A, B> ArrayBlockingQueue<B> mapJ(final F<A, B> f, final ArrayBlockingQueue<A> as) {
        final ArrayBlockingQueue<B> bs = new ArrayBlockingQueue<B>(as.size());
        bs.addAll(iterableStream(as).map(f).toCollection());
        return bs;
    }


    /**
     * Maps this function over a TreeSet.
     *
     * @param as A TreeSet to map this function over.
     * @return A new TreeSet with this function applied to each element.
     */
    static public <A, B> TreeSet<B> mapJ(final F<A, B> f, final TreeSet<A> as) {
        return new TreeSet<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a PriorityQueue.
     *
     * @param as A PriorityQueue to map this function over.
     * @return A new PriorityQueue with this function applied to each element.
     */
    static public <A, B> PriorityQueue<B> mapJ(final F<A, B> f, final PriorityQueue<A> as) {
        return new PriorityQueue<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over a LinkedList.
     *
     * @param as A LinkedList to map this function over.
     * @return A new LinkedList with this function applied to each element.
     */
    static public <A, B> LinkedList<B> mapJ(final F<A, B> f, final LinkedList<A> as) {
        return new LinkedList<B>(iterableStream(as).map(f).toCollection());
    }

    /**
     * Maps this function over an ArrayList.
     *
     * @param as An ArrayList to map this function over.
     * @return A new ArrayList with this function applied to each element.
     */
    static public <A, B> ArrayList<B> mapJ(final F<A, B> f, final ArrayList<A> as) {
        return new ArrayList<B>(iterableStream(as).map(f).toCollection());
    }

    static public <A, B, C> F<A, C> map(F<A, B> target, F<B, C> f) {
        return andThen(target, f);
    }

    static public <A, B, C> F<C, B> contramap(F<A, B> target, F<C, A> f) {
        return andThen(f, target);
    }

}
