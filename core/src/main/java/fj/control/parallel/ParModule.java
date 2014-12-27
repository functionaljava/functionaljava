package fj.control.parallel;

import fj.*;

import static fj.P.p;
import static fj.Function.curry;
import static fj.Function.uncurryF2;
import static fj.control.parallel.Promise.liftM2;
import fj.data.Array;
import fj.data.IterableW;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeZipper;
import fj.data.Zipper;
import fj.function.Effect1;

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;

/**
 * A module of higher-order concurrency features.
 */
public final class ParModule {
  private final Strategy<Unit> strategy;

  private ParModule(final Strategy<Unit> strategy) {
    this.strategy = strategy;
  }

  /**
   * Constructor method for ParModule
   *
   * @param u A parallel strategy for the module.
   * @return A ParModule that uses the given strategy for parallelism.
   */
  public static ParModule parModule(final Strategy<Unit> u) {
    return new ParModule(u);
  }

  /**
   * Evaluates the given product concurrently and returns a Promise of the result.
   *
   * @param p A product to evaluate concurrently.
   * @return A Promise of the value of the given product, that can be claimed in the future.
   */
  public <A> Promise<A> promise(final P1<A> p) {
    return Promise.promise(strategy, p);
  }

  /**
   * Returns a function that evaluates a given product concurrently and returns a Promise of the result.
   *
   * @return a function that evaluates a given product concurrently and returns a Promise of the result.
   */
  public <A> F<P1<A>, Promise<A>> promise() {
    return ap1 -> promise(ap1);
  }

  /**
   * Promotes the given function to a concurrent function that returns a Promise.
   *
   * @param f A given function to promote to a concurrent function.
   * @return A function that is applied concurrently when given an argument, yielding a Promise of the result
   *         that can be claimed in the future.
   */
  public <A, B> F<A, Promise<B>> promise(final F<A, B> f) {
    return F1Functions.promiseK(f, strategy);
  }

  /**
   * Returns a function that promotes a given function to a concurrent function that returns a Promise.
   * The pure Kleisli arrow of Promise.
   *
   * @return A higher-order function that takes pure functions to promise-valued functions.
   */
  public <A, B> F<F<A, B>, F<A, Promise<B>>> promisePure() {
    return abf -> promise(abf);
  }

  /**
   * Promotes the given function to a concurrent function that returns a Promise.
   *
   * @param f A given function to promote to a concurrent function.
   * @return A function that is applied concurrently when given an argument, yielding a Promise of the result
   *         that can be claimed in the future.
   */
  public <A, B, C> F2<A, B, Promise<C>> promise(final F2<A, B, C> f) {
    return P2.untuple(F1Functions.promiseK(F2Functions.tuple(f), strategy));
  }


  /**
   * Creates a very fast concurrent effect, as an actor that does not guarantee ordering of its messages.
   * Such an actor is not thread-safe unless the given Effect is.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that does not guarantee ordering of its messages.
   */
  public <A> Actor<A> effect(final Effect1<A> e) {
    return Actor.actor(strategy, e);
  }

  /**
   * A first-class constructor of concurrent effects, as actors that don't guarantee ordering of messages.
   * Such an actor is not thread-safe unless the given Effect is.
   *
   * @return A function that takes an effect and returns a concurrent effect.
   */
  public <A> F<Effect1<A>, Actor<A>> effect() {
    return effect -> effect(effect);
  }

  /**
   * Creates a concurrent actor that is guaranteed to process only one message at a time.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that is guaranteed to process its messages in order.
   */
  public <A> Actor<A> actor(final Effect1<A> e) {
    return Actor.queueActor(strategy, e);
  }

  /**
   * A first-class constructor of actors.
   *
   * @return A function that takes an effect and returns an actor that processes messages in some order.
   */
  public <A> F<Effect1<A>, Actor<A>> actor() {
    return effect -> actor(effect);
  }

  /**
   * List iteration inside a Promise. Traverses a List of Promises yielding a Promise of a List.
   *
   * @param ps A list of promises to sequence.
   * @return A promise of the List of values promised by the list of promises.
   */
  public <A> Promise<List<A>> sequence(final List<Promise<A>> ps) {
    return Promise.sequence(strategy, ps);
  }

  /**
   * A first-class function that traverses a list inside a promise.
   *
   * @return A first-class function that traverses a list inside a promise.
   */
  public <A> F<List<Promise<A>>, Promise<List<A>>> sequenceList() {
    return list -> sequence(list);
  }

  /**
   * Stream iteration inside a Promise. Traverses a Stream of Promises yielding a Promise of a Stream.
   *
   * @param ps A Stream of promises to sequence.
   * @return A promise of the Stream of values promised by the Stream of promises.
   */
  public <A> Promise<Stream<A>> sequence(final Stream<Promise<A>> ps) {
    return Promise.sequence(strategy, ps);
  }

  /**
   * A first-class function that traverses a stream inside a promise.
   *
   * @return A first-class function that traverses a stream inside a promise.
   */
  public <A> F<Stream<Promise<A>>, Promise<Stream<A>>> sequenceStream() {
    return stream -> sequence(stream);
  }

  /**
   * Traverses a product-1 inside a promise.
   *
   * @param p A product-1 of a promised value.
   * @return A promise of a product of the value promised by the argument.
   */
  public <A> Promise<P1<A>> sequence(final P1<Promise<A>> p) {
    return Promise.sequence(strategy, p);
  }

  /**
   * Takes a Promise-valued function and applies it to each element
   * in the given List, yielding a promise of a List of results.
   *
   * @param as A list to map across.
   * @param f  A promise-valued function to map across the list.
   * @return A Promise of a new list with the given function applied to each element.
   */
  public <A, B> Promise<List<B>> mapM(final List<A> as, final F<A, Promise<B>> f) {
    return sequence(as.map(f));
  }

  /**
   * First-class function that maps a concurrent function over a List inside a promise.
   *
   * @return a function that maps a concurrent function over a List inside a promise.
   */
  public <A, B> F<F<A, Promise<B>>, F<List<A>, Promise<List<B>>>> mapList() {
    return curry((f, list) -> mapM(list, f));
  }

  /**
   * Takes a Promise-valued function and applies it to each element
   * in the given Stream, yielding a promise of a Stream of results.
   *
   * @param as A Stream to map across.
   * @param f  A promise-valued function to map across the Stream.
   * @return A Promise of a new Stream with the given function applied to each element.
   */
  public <A, B> Promise<Stream<B>> mapM(final Stream<A> as, final F<A, Promise<B>> f) {
    return sequence(as.map(f));
  }

  /**
   * First-class function that maps a concurrent function over a Stream inside a promise.
   *
   * @return a function that maps a concurrent function over a Stream inside a promise.
   */
  public <A, B> F<F<A, Promise<B>>, F<Stream<A>, Promise<Stream<B>>>> mapStream() {
    return curry((f, stream) -> mapM(stream, f));
  }

  /**
   * Maps a concurrent function over a Product-1 inside a Promise.
   *
   * @param a A product-1 across which to map.
   * @param f A concurrent function to map over the product inside a promise.
   * @return A promised product of the result of mapping the given function over the given product.
   */
  public <A, B> Promise<P1<B>> mapM(final P1<A> a, final F<A, Promise<B>> f) {
    return sequence(a.map(f));
  }

  /**
   * Maps across a list in parallel.
   *
   * @param as A list to map across in parallel.
   * @param f  A function to map across the given list.
   * @return A Promise of a new list with the given function applied to each element.
   */
  public <A, B> Promise<List<B>> parMap(final List<A> as, final F<A, B> f) {
    return mapM(as, promise(f));
  }

  /**
   * A first-class function that maps another function across a list in parallel.
   *
   * @return A function that maps another function across a list in parallel.
   */
  public <A, B> F<F<A, B>, F<List<A>, Promise<List<B>>>> parMapList() {
    return curry((abf, list) -> parMap(list, abf));
  }

  /**
   * Maps across a nonempty list in parallel.
   *
   * @param as A NonEmptyList to map across in parallel.
   * @param f  A function to map across the given NonEmptyList.
   * @return A Promise of a new NonEmptyList with the given function applied to each element.
   */
  public <A, B> Promise<NonEmptyList<B>> parMap(final NonEmptyList<A> as, final F<A, B> f) {
    return mapM(as.toList(), promise(f)).fmap((F<List<B>, NonEmptyList<B>>) list -> NonEmptyList.fromList(list).some());
  }

  /**
   * Maps across a Stream in parallel.
   *
   * @param as A Stream to map across in parallel.
   * @param f  A function to map across the given Stream.
   * @return A Promise of a new Stream with the given function applied to each element.
   */
  public <A, B> Promise<Stream<B>> parMap(final Stream<A> as, final F<A, B> f) {
    return mapM(as, promise(f));
  }

  /**
   * A first-class function that maps another function across a stream in parallel.
   *
   * @return A function that maps another function across a stream in parallel.
   */
  public <A, B> F<F<A, B>, F<Stream<A>, Promise<Stream<B>>>> parMapStream() {
    return curry((abf, stream) -> parMap(stream, abf));
  }

  /**
   * Maps across an Iterable in parallel.
   *
   * @param as An Iterable to map across in parallel.
   * @param f  A function to map across the given Iterable.
   * @return A Promise of a new Iterable with the given function applied to each element.
   */
  public <A, B> Promise<Iterable<B>> parMap(final Iterable<A> as, final F<A, B> f) {
    return parMap(iterableStream(as), f)
        .fmap(Function.<Stream<B>, Iterable<B>>vary(Function.<Stream<B>>identity()));
  }

  /**
   * A first-class function that maps another function across an iterable in parallel.
   *
   * @return A function that maps another function across an iterable in parallel.
   */
  public <A, B> F<F<A, B>, F<Iterable<A>, Promise<Iterable<B>>>> parMapIterable() {
    return curry((abf, iterable) -> parMap(iterable, abf));
  }

  /**
   * Maps across an Array in parallel.
   *
   * @param as An array to map across in parallel.
   * @param f  A function to map across the given Array.
   * @return A Promise of a new Array with the given function applied to each element.
   */
  public <A, B> Promise<Array<B>> parMap(final Array<A> as, final F<A, B> f) {
    return parMap(as.toStream(), f).fmap(stream -> stream.toArray());
  }

  /**
   * A first-class function that maps another function across an array in parallel.
   *
   * @return A function that maps another function across an array in parallel.
   */
  public <A, B> F<F<A, B>, F<Array<A>, Promise<Array<B>>>> parMapArray() {
    return curry((abf, array) -> parMap(array, abf));
  }

  /**
   * Maps a function across a Zipper in parallel.
   *
   * @param za A Zipper to map across in parallel.
   * @param f  A function to map across the given Zipper.
   * @return A promise of a new Zipper with the given function applied to each element.
   */
  public <A, B> Promise<Zipper<B>> parMap(final Zipper<A> za, final F<A, B> f) {
    return parMap(za.rights(), f)
        .apply(promise(f).f(za.focus()).apply(parMap(za.lefts(), f).fmap(curry(Zipper.<B>zipper()))));
  }

  /**
   * Maps a function across a Tree in parallel.
   *
   * @param ta A Tree to map across in parallel.
   * @param f  A function to map across the given Tree.
   * @return A promise of a new Tree with the given function applied to each element.
   */
  public <A, B> Promise<Tree<B>> parMap(final Tree<A> ta, final F<A, B> f) {
    return mapM(ta.subForest(), this.<Tree<A>, Tree<B>>mapStream().f(this.<A, B>parMapTree().f(f)))
        .apply(promise(f).f(ta.root()).fmap(Tree.<B>node()));
  }

  /**
   * A first-class function that maps across a Tree in parallel.
   *
   * @return A function that maps a given function across a Tree in parallel.
   */
  public <A, B> F<F<A, B>, F<Tree<A>, Promise<Tree<B>>>> parMapTree() {
    return curry((abf, tree) -> parMap(tree, abf));
  }

  /**
   * Maps a function across a TreeZipper in parallel.
   *
   * @param za A TreeZipper to map across in parallel.
   * @param f  A function to map across the given TreeZipper.
   * @return A promise of a new TreeZipper with the given function applied to each element of the tree.
   */
  public <A, B> Promise<TreeZipper<B>> parMap(final TreeZipper<A> za, final F<A, B> f) {
    final F<Tree<A>, Tree<B>> tf = Tree.<A, B>fmap_().f(f);
    final P4<Tree<A>, Stream<Tree<A>>, Stream<Tree<A>>, Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>> p = za.p();
    return mapM(p._4(),
            p3 -> parMap(p3._3(), tf).apply(promise(f).f(p3._2()).apply(
                parMap(p3._1(), tf).fmap(P.<Stream<Tree<B>>, B, Stream<Tree<B>>>p3())))).apply(parMap(za.rights(), tf).apply(
        parMap(za.lefts(), tf).apply(parMap(p._1(), f).fmap(TreeZipper.<B>treeZipper()))));
  }

  /**
   * Binds a list-valued function across a list in parallel, concatenating the results into a new list.
   *
   * @param as A list to bind across in parallel.
   * @param f  A function to bind across the given list in parallel.
   * @return A promise of a new List with the given function bound across its elements.
   */
  public <A, B> Promise<List<B>> parFlatMap(final List<A> as, final F<A, List<B>> f) {
    return parFoldMap(as, f, Monoid.<B>listMonoid());
  }

  /**
   * Binds a Stream-valued function across a Stream in parallel, concatenating the results into a new Stream.
   *
   * @param as A Stream to bind across in parallel.
   * @param f  A function to bind across the given Stream in parallel.
   * @return A promise of a new Stream with the given function bound across its elements.
   */
  public <A, B> Promise<Stream<B>> parFlatMap(final Stream<A> as, final F<A, Stream<B>> f) {
    return parFoldMap(as, f, Monoid.<B>streamMonoid());
  }

  /**
   * Binds an Array-valued function across an Array in parallel, concatenating the results into a new Array.
   *
   * @param as An Array to bind across in parallel.
   * @param f  A function to bind across the given Array in parallel.
   * @return A promise of a new Array with the given function bound across its elements.
   */
  public <A, B> Promise<Array<B>> parFlatMap(final Array<A> as, final F<A, Array<B>> f) {
    return parMap(as, f).fmap(Array.<B>join());
  }

  /**
   * Binds an Iterable-valued function across an Iterable in parallel, concatenating the results into a new Iterable.
   *
   * @param as A Iterable to bind across in parallel.
   * @param f  A function to bind across the given Iterable in parallel.
   * @return A promise of a new Iterable with the given function bound across its elements.
   */
  public <A, B> Promise<Iterable<B>> parFlatMap(final Iterable<A> as, final F<A, Iterable<B>> f) {
    return parMap(as, f).fmap(IterableW.<B, Iterable<B>>join())
        .fmap(Function.<IterableW<B>, Iterable<B>>vary(Function.<Iterable<B>>identity()));
  }

  /**
   * Zips two lists together with a given function, in parallel.
   *
   * @param as A list to zip with another in parallel.
   * @param bs A list to zip with another in parallel.
   * @param f  A function with which to zip two lists in parallel.
   * @return A Promise of a new list with the results of applying the given function across the two lists in lockstep.
   */
  public <A, B, C> Promise<List<C>> parZipWith(final List<A> as, final List<B> bs, final F<A, F<B, C>> f) {
    return sequence(as.<B, Promise<C>>zipWith(bs, promise(uncurryF2(f))));
  }

  /**
   * Zips two streams together with a given function, in parallel.
   *
   * @param as A stream to zip with another in parallel.
   * @param bs A stream to zip with another in parallel.
   * @param f  A function with which to zip two streams in parallel.
   * @return A Promise of a new stream with the results of applying the given function across the two streams, stepwise.
   */
  public <A, B, C> Promise<Stream<C>> parZipWith(final Stream<A> as, final Stream<B> bs, final F<A, F<B, C>> f) {
    return sequence(as.<B, Promise<C>>zipWith(bs, promise(uncurryF2(f))));
  }

  /**
   * Zips two arrays together with a given function, in parallel.
   *
   * @param as An array to zip with another in parallel.
   * @param bs An array to zip with another in parallel.
   * @param f  A function with which to zip two arrays in parallel.
   * @return A Promise of a new array with the results of applying the given function across the two arrays, stepwise.
   */
  public <A, B, C> Promise<Array<C>> parZipWith(final Array<A> as, final Array<B> bs, final F<A, F<B, C>> f) {
    return parZipWith(as.toStream(), bs.toStream(), f).fmap(new F<Stream<C>, Array<C>>() {
      public Array<C> f(final Stream<C> stream) {
        return stream.toArray();
      }
    });
  }

  /**
   * Zips two iterables together with a given function, in parallel.
   *
   * @param as An iterable to zip with another in parallel.
   * @param bs An iterable to zip with another in parallel.
   * @param f  A function with which to zip two iterables in parallel.
   * @return A Promise of a new iterable with the results of applying the given function across the two iterables, stepwise.
   */
  public <A, B, C> Promise<Iterable<C>> parZipWith(final Iterable<A> as, final Iterable<B> bs, final F<A, F<B, C>> f) {
    return parZipWith(iterableStream(as), iterableStream(bs), f).fmap(
        Function.<Stream<C>, Iterable<C>>vary(Function.<Iterable<C>>identity()));
  }

  /**
   * Maps with the given function across the given stream in parallel, while folding with
   * the given monoid.
   *
   * @param as     A stream to map over and reduce.
   * @param map    The function to map over the given stream.
   * @param reduce The monoid with which to sum the results.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Stream<A> as, final F<A, B> map, final Monoid<B> reduce) {
    return as.isEmpty() ? promise(p(reduce.zero())) : as.map(promise(map)).foldLeft1(liftM2(reduce.sum()));
  }

  /**
   * Maps with the given function across chunks of the given stream in parallel, while folding with
   * the given monoid. The stream is split into chunks according to the given chunking function,
   * the given map function is mapped over all chunks simultaneously, but over each chunk sequentially.
   * All chunks are summed concurrently and the sums are then summed sequentially.
   *
   * @param as       A stream to chunk, then map over and reduce.
   * @param map      The function to map over the given stream.
   * @param reduce   The monoid with which to sum the results.
   * @param chunking A function describing how the stream should be split into chunks. Should return the first chunk
   *                 and the rest of the stream.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Stream<A> as, final F<A, B> map, final Monoid<B> reduce,
                                      final F<Stream<A>, P2<Stream<A>, Stream<A>>> chunking) {
    return parMap(Stream.unfold(stream -> stream.isEmpty() ? Option.<P2<Stream<A>, Stream<A>>>none() : some(chunking.f(stream)), as), Stream.<A, B>map_().f(map)).bind(new F<Stream<Stream<B>>, Promise<B>>() {
      public Promise<B> f(final Stream<Stream<B>> stream) {
        return parMap(stream, reduce.sumLeftS()).fmap(reduce.sumLeftS());
      }
    });
  }

  /**
   * Maps with the given function across chunks of the given Iterable in parallel, while folding with
   * the given monoid. The Iterable is split into chunks according to the given chunking function,
   * the given map function is mapped over all chunks simultaneously, but over each chunk sequentially.
   * All chunks are summed concurrently and the sums are then summed sequentially.
   *
   * @param as       An Iterable to chunk, then map over and reduce.
   * @param map      The function to map over the given Iterable.
   * @param reduce   The monoid with which to sum the results.
   * @param chunking A function describing how the Iterable should be split into chunks. Should return the first chunk
   *                 and the rest of the Iterable.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Iterable<A> as, final F<A, B> map, final Monoid<B> reduce,
                                      final F<Iterable<A>, P2<Iterable<A>, Iterable<A>>> chunking) {
    return parFoldMap(iterableStream(as), map, reduce, (Stream<A> stream) -> {
      final F<Iterable<A>, Stream<A>> is = iterable -> iterableStream(iterable);
      return chunking.f(stream).map1(is).map2(is);
    });
  }

  /**
   * Maps with the given function across the given iterable in parallel, while folding with
   * the given monoid.
   *
   * @param as     An Iterable to map over and reduce.
   * @param map    The function to map over the given Iterable.
   * @param reduce The Monoid with which to sum the results.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Iterable<A> as, final F<A, B> map, final Monoid<B> reduce) {
    return parFoldMap(iterableStream(as), map, reduce);
  }


  /**
   * Maps the given function across all positions of the given zipper in parallel.
   *
   * @param za A zipper to extend the given function across.
   * @param f  A function to extend across the given zipper.
   * @return A promise of a new zipper of the results of applying the given function to all positions of the given
   *         zipper.
   */
  public <A, B> Promise<Zipper<B>> parExtend(final Zipper<A> za, final F<Zipper<A>, B> f) {
    return parMap(za.positions(), f);
  }

  /**
   * Maps the given function across all subtrees of the given Tree in parallel.
   *
   * @param ta A tree to extend the given function across.
   * @param f  A function to extend across the given Tree.
   * @return A promise of a new Tree of the results of applying the given function to all subtrees of the given Tree.
   */
  public <A, B> Promise<Tree<B>> parExtend(final Tree<A> ta, final F<Tree<A>, B> f) {
    return parMap(ta.cojoin(), f);
  }

  /**
   * Maps the given function across all positions of the given TreeZipper in parallel.
   *
   * @param za A TreeZipper to extend the given function across.
   * @param f  A function to extend across the given TreeZipper.
   * @return A promise of a new TreeZipper of the results of applying the given function to all positions of the
   *         given TreeZipper.
   */
  public <A, B> Promise<TreeZipper<B>> parExtend(final TreeZipper<A> za, final F<TreeZipper<A>, B> f) {
    return parMap(za.positions(), f);
  }

  /**
   * Maps the given function across all sublists of the given NonEmptyList in parallel.
   *
   * @param as A NonEmptyList to extend the given function across.
   * @param f  A function to extend across the given NonEmptyList
   * @return A promise of a new NonEmptyList of the results of applying the given function to all sublists of the
   *         given NonEmptyList.
   */
  public <A, B> Promise<NonEmptyList<B>> parExtend(final NonEmptyList<A> as, final F<NonEmptyList<A>, B> f) {
    return parMap(as.tails(), f);
  }

}
