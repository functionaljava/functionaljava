package fj;

import fj.control.parallel.Promise;
import fj.data.*;

import static fj.P.p;
import static fj.data.IterableW.wrap;
import static fj.data.Set.iterableSet;
import static fj.data.Tree.node;
import static fj.data.TreeZipper.treeZipper;
import static fj.data.Zipper.zipper;

/**
 * Created by MarkPerry on 22/01/2015.
 */
public abstract class F2W<A, B, C> implements F2<A, B, C> {

    /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument.
     */
    public F1W<B, C> f(final A a) {
        return F1W.lift(F2Functions.f(this, a));
    }

    /**
     * Curries this wrapped function to a wrapped function of arity-1 that returns another wrapped function.
     *
     * @return a wrapped function of arity-1 that returns another wrapped function.
     */
    public F1W<A, F<B, C>> curry() {
        return F1W.lift(F2Functions.curry(this));
    }

    /**
     * Flips the arguments of this function.
     *
     * @return A new function with the arguments of this function flipped.
     */
    public F2W<B, A, C> flip() {
        return lift(F2Functions.flip(this));
    }

    /**
     * Uncurries this function to a function on tuples.
     *
     * @return A new function that calls this function with the elements of a given tuple.
     */
    public F1W<P2<A, B>, C> tuple() {
        return F1W.lift(F2Functions.tuple(this));
    }

    /**
     * Promotes this function to a function on Arrays.
     *
     * @return This function promoted to transform Arrays.
     */
    public F2W<Array<A>, Array<B>, Array<C>> arrayM() {
        return lift(F2Functions.arrayM(this));
    }

    /**
     * Promotes this function to a function on Promises.
     *
     * @return This function promoted to transform Promises.
     */
    public F2W<Promise<A>, Promise<B>, Promise<C>> promiseM() {
        return lift(F2Functions.promiseM(this));
    }

    /**
     * Promotes this function to a function on Iterables.
     *
     * @return This function promoted to transform Iterables.
     */
    public F2W<Iterable<A>, Iterable<B>, IterableW<C>> iterableM() {
        return lift(F2Functions.iterableM(this));
    }

    /**
     * Promotes this function to a function on Lists.
     *
     * @return This function promoted to transform Lists.
     */
    public F2W<List<A>, List<B>, List<C>> listM() {
        return lift(F2Functions.listM(this));
    }

    /**
     * Promotes this function to a function on non-empty lists.
     *
     * @return This function promoted to transform non-empty lists.
     */
    public F2W<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> nelM() {
        return lift(F2Functions.nelM(this));
    }

    /**
     * Promotes this function to a function on Options.
     *
     * @return This function promoted to transform Options.
     */
    public F2W<Option<A>, Option<B>, Option<C>> optionM() {
        return lift(F2Functions.optionM(this));
    }

    /**
     * Promotes this function to a function on Sets.
     *
     * @param o An ordering for the result of the promoted function.
     * @return This function promoted to transform Sets.
     */
    public F2W<Set<A>, Set<B>, Set<C>> setM(final Ord<C> o) {
        return lift(F2Functions.setM(this, o));
    }

    /**
     * Promotes this function to a function on Streams.
     *
     * @return This function promoted to transform Streams.
     */
    public F2W<Stream<A>, Stream<B>, Stream<C>> streamM() {
        return lift(F2Functions.streamM(this));
    }

    /**
     * Promotes this function to a function on Trees.
     *
     * @return This function promoted to transform Trees.
     */
    public F2W<Tree<A>, Tree<B>, Tree<C>> treeM() {
        return lift(F2Functions.treeM(this));
    }

    /**
     * Promotes this function to zip two arrays, applying the function lock-step over both Arrays.
     *
     * @return A function that zips two arrays with this function.
     */
    public F2W<Array<A>, Array<B>, Array<C>> zipArrayM() {
        return lift(F2Functions.zipArrayM(this));
    }

    /**
     * Promotes this function to zip two iterables, applying the function lock-step over both iterables.
     *
     * @return A function that zips two iterables with this function.
     */
    public F2W<Iterable<A>, Iterable<B>, Iterable<C>> zipIterableM() {
        return lift(F2Functions.zipIterableM(this));
    }

    /**
     * Promotes this function to zip two lists, applying the function lock-step over both lists.
     *
     * @return A function that zips two lists with this function.
     */
    public F2W<List<A>, List<B>, List<C>> zipListM() {
        return lift(F2Functions.zipListM(this));
    }


    /**
     * Promotes this function to zip two streams, applying the function lock-step over both streams.
     *
     * @return A function that zips two streams with this function.
     */
    public F2W<Stream<A>, Stream<B>, Stream<C>> zipStreamM() {
        return lift(F2Functions.zipStreamM(this));
    }

    /**
     * Promotes this function to zip two non-empty lists, applying the function lock-step over both lists.
     *
     * @return A function that zips two non-empty lists with this function.
     */
    public F2W<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> zipNelM() {
        return lift(F2Functions.zipNelM(this));
    }

    /**
     * Promotes this function to zip two sets, applying the function lock-step over both sets.
     *
     * @param o An ordering for the resulting set.
     * @return A function that zips two sets with this function.
     */
    public F2W<Set<A>, Set<B>, Set<C>> zipSetM(final Ord<C> o) {
        return lift(F2Functions.zipSetM(this, o));
    }

    /**
     * Promotes this function to zip two trees, applying the function lock-step over both trees.
     * The structure of the resulting tree is the structural intersection of the two trees.
     *
     * @return A function that zips two trees with this function.
     */
    public F2W<Tree<A>, Tree<B>, Tree<C>> zipTreeM() {
        return lift(F2Functions.zipTreeM(this));
    }

    /**
     * Promotes this function to zip two zippers, applying the function lock-step over both zippers in both directions.
     * The structure of the resulting zipper is the structural intersection of the two zippers.
     *
     * @return A function that zips two zippers with this function.
     */
    public F2W<Zipper<A>, Zipper<B>, Zipper<C>> zipZipperM() {
        return lift(F2Functions.zipZipperM(this));
    }

    /**
     * Promotes this function to zip two TreeZippers, applying the function lock-step over both zippers in all directions.
     * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
     *
     * @return A function that zips two TreeZippers with this function.
     */
    public F2W<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>> zipTreeZipperM() {
        return lift(F2Functions.zipTreeZipperM(this));
    }

    public <Z> F2W<Z, B, C> contramapFirst(F<Z, A> f) {
        return lift(F2Functions.contramapFirst(this, f));
    }

    public <Z> F2W<A, Z, C> contramapSecond(F<Z, B> f) {
        return lift(F2Functions.contramapSecond(this, f));
    }

    public <X, Y> F2W<X, Y, C> contramap(F<X, A> f, F<Y, B> g) {
        return lift(F2Functions.contramap(this, f, g));
    }

    public <Z> F2W<A, B, Z> map(F<C, Z> f) {
        return lift(F2Functions.map(this, f));
    }


    public static class F2WFunc<A, B, C> extends F2W<A, B, C> {
        final F2<A, B, C> func;
        public F2WFunc(F2<A, B, C> f) {
            func = f;
        }

        @Override
        public C f(A a, B b) {
            return func.f(a, b);
        }
    }

    /**
     * Lifts the function into the fully featured function wrapper
     */
    public static <A, B, C> F2W<A, B, C> lift(final F2<A, B, C> f) {
        return new F2WFunc<>(f);
    }



}
