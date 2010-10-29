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

import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import static fj.data.Zipper.fromStream;

/**
 * A transformation or function from <code>A</code> to <code>B</code>. This type can be represented
 * using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 412 $</li>
 *          <li>$LastChangedDate: 2010-06-06 16:11:52 +1000 (Sun, 06 Jun 2010) $</li>
 *          </ul>
 */
public abstract class F<A, B> {
  /**
   * Transform <code>A</code> to <code>B</code>.
   *
   * @param a The <code>A</code> to transform.
   * @return The result of the transformation.
   */
  public abstract B f(A a);


  /**
   * Function composition
   *
   * @param g A function to compose with this one.
   * @return The composed function such that this function is applied last.
   */
  public final <C> F<C, B> o(final F<C, A> g) {
    return new F<C, B>() {
      public B f(final C c) {
        return F.this.f(g.f(c));
      }
    };
  }

  /**
   * First-class function composition
   *
   * @return A function that composes this function with another.
   */
  public final <C> F<F<C, A>, F<C, B>> o() {
    return new F<F<C, A>, F<C, B>>() {
      public F<C, B> f(final F<C, A> g) {
        return F.this.o(g);
      }
    };
  }

  /**
   * Function composition flipped.
   *
   * @param g A function with which to compose this one.
   * @return The composed function such that this function is applied first.
   */
  @SuppressWarnings({"unchecked"})
  public final <C> F<A, C> andThen(final F<B, C> g) {
    return g.o(this);
  }

  /**
   * First-class composition flipped.
   *
   * @return A function that invokes this function and then a given function on the result.
   */
  public final <C> F<F<B, C>, F<A, C>> andThen() {
    return new F<F<B, C>, F<A, C>>() {
      public F<A, C> f(final F<B, C> g) {
        return F.this.andThen(g);
      }
    };
  }

  /**
   * Binds a given function across this function (Reader Monad).
   *
   * @param g A function that takes the return value of this function as an argument, yielding a new function.
   * @return A function that invokes this function on its argument and then the given function on the result.
   */
  public final <C> F<A, C> bind(final F<B, F<A, C>> g) {
    return new F<A, C>() {
      @SuppressWarnings({"unchecked"})
      public C f(final A a) {
        return g.f(F.this.f(a)).f(a);
      }
    };
  }

  /**
   * First-class function binding.
   *
   * @return A function that binds another function across this function.
   */
  public final <C> F<F<B, F<A, C>>, F<A, C>> bind() {
    return new F<F<B, F<A, C>>, F<A, C>>() {
      public F<A, C> f(final F<B, F<A, C>> g) {
        return F.this.bind(g);
      }
    };
  }

  /**
   * Function application in an environment (Applicative Functor).
   *
   * @param g A function with the same argument type as this function, yielding a function that takes the return
   *          value of this function.
   * @return A new function that invokes the given function on its argument, yielding a new function that is then
   *         applied to the result of applying this function to the argument.
   */
  public final <C> F<A, C> apply(final F<A, F<B, C>> g) {
    return new F<A, C>() {
      @SuppressWarnings({"unchecked"})
      public C f(final A a) {
        return g.f(a).f(F.this.f(a));
      }
    };
  }

  /**
   * First-class function application in an environment.
   *
   * @return A function that applies a given function within the environment of this function.
   */
  public final <C> F<F<A, F<B, C>>, F<A, C>> apply() {
    return new F<F<A, F<B, C>>, F<A, C>>() {
      public F<A, C> f(final F<A, F<B, C>> g) {
        return F.this.apply(g);
      }
    };
  }

  /**
   * Applies this function over the arguments of another function.
   *
   * @param g The function over whose arguments to apply this function.
   * @return A new function that invokes this function on its arguments before invoking the given function.
   */
  public final <C> F<A, F<A, C>> on(final F<B, F<B, C>> g) {
    return new F<A, F<A, C>>() {
      public F<A, C> f(final A a1) {
        return new F<A, C>() {
          @SuppressWarnings({"unchecked"})
          public C f(final A a2) {
            return g.f(F.this.f(a1)).f(F.this.f(a2));
          }
        };
      }
    };
  }


  /**
   * Applies this function over the arguments of another function.
   *
   * @return A function that applies this function over the arguments of another function.
   */
  public final <C> F<F<B, F<B, C>>, F<A, F<A, C>>> on() {
    return new F<F<B, F<B, C>>, F<A, F<A, C>>>() {
      public F<A, F<A, C>> f(final F<B, F<B, C>> g) {
        return F.this.on(g);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result in a product-1. Kleisli arrow for P1.
   *
   * @return This function promoted to return its result in a product-1.
   */
  public final F<A, P1<B>> lazy() {
    return new F<A, P1<B>>() {
      public P1<B> f(final A a) {
        return new P1<B>() {
          public B _1() {
            return F.this.f(a);
          }
        };
      }
    };
  }

  /**
   * Promotes this function to map over a product-1.
   *
   * @return This function promoted to map over a product-1.
   */
  public final F<P1<A>, P1<B>> mapP1() {
    return new F<P1<A>, P1<B>>() {
      public P1<B> f(final P1<A> p) {
        return p.map(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
   *
   * @return This function promoted to return its result in an Option.
   */
  public final F<A, Option<B>> optionK() {
    return new F<A, Option<B>>() {
      public Option<B> f(final A a) {
        return some(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over an optional value.
   *
   * @return This function promoted to map over an optional value.
   */
  public final F<Option<A>, Option<B>> mapOption() {
    return new F<Option<A>, Option<B>>() {
      public Option<B> f(final Option<A> o) {
        return o.map(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result in a List. Kleisli arrow for List.
   *
   * @return This function promoted to return its result in a List.
   */
  public final F<A, List<B>> listK() {
    return new F<A, List<B>>() {
      public List<B> f(final A a) {
        return List.single(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over a List.
   *
   * @return This function promoted to map over a List.
   */
  public final F<List<A>, List<B>> mapList() {
    return new F<List<A>, List<B>>() {
      public List<B> f(final List<A> x) {
        return x.map(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result in a Stream. Kleisli arrow for Stream.
   *
   * @return This function promoted to return its result in a Stream.
   */
  public final F<A, Stream<B>> streamK() {
    return new F<A, Stream<B>>() {
      public Stream<B> f(final A a) {
        return Stream.single(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over a Stream.
   *
   * @return This function promoted to map over a Stream.
   */
  public final F<Stream<A>, Stream<B>> mapStream() {
    return new F<Stream<A>, Stream<B>>() {
      public Stream<B> f(final Stream<A> x) {
        return x.map(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result in a Array. Kleisli arrow for Array.
   *
   * @return This function promoted to return its result in a Array.
   */
  public final F<A, Array<B>> arrayK() {
    return new F<A, Array<B>>() {
      public Array<B> f(final A a) {
        return Array.single(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over a Array.
   *
   * @return This function promoted to map over a Array.
   */
  public final F<Array<A>, Array<B>> mapArray() {
    return new F<Array<A>, Array<B>>() {
      public Array<B> f(final Array<A> x) {
        return x.map(F.this);
      }
    };
  }

  /**
   * Returns a function that comaps over a given actor.
   *
   * @return A function that comaps over a given actor.
   */
  public final F<Actor<B>, Actor<A>> comapActor() {
    return new F<Actor<B>, Actor<A>>() {
      public Actor<A> f(final Actor<B> a) {
        return a.comap(F.this);
      }
    };
  }

  /**
   * Promotes this function to a concurrent function that returns a Promise of a value.
   *
   * @param s A parallel strategy for concurrent execution.
   * @return A concurrent function that returns a Promise of a value.
   */
  public final F<A, Promise<B>> promiseK(final Strategy<Unit> s) {
    return Promise.promise(s, this);
  }

  /**
   * Promotes this function to map over a Promise.
   *
   * @return This function promoted to map over Promises.
   */
  public final F<Promise<A>, Promise<B>> mapPromise() {
    return new F<Promise<A>, Promise<B>>() {
      public Promise<B> f(final Promise<A> p) {
        return p.fmap(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result on the left side of an Either.
   * Kleisli arrow for the Either left projection.
   *
   * @return This function promoted to return its result on the left side of an Either.
   */
  @SuppressWarnings({"unchecked"})
  public final <C> F<A, Either<B, C>> eitherLeftK() {
    return Either.<B, C>left_().o(F.this);
  }

  /**
   * Promotes this function so that it returns its result on the right side of an Either.
   * Kleisli arrow for the Either right projection.
   *
   * @return This function promoted to return its result on the right side of an Either.
   */
  @SuppressWarnings({"unchecked"})
  public final <C> F<A, Either<C, B>> eitherRightK() {
    return Either.<C, B>right_().o(F.this);
  }

  /**
   * Promotes this function to map over the left side of an Either.
   *
   * @return This function promoted to map over the left side of an Either.
   */
  @SuppressWarnings({"unchecked"})
  public final <X> F<Either<A, X>, Either<B, X>> mapLeft() {
    return Either.<A, X, B>leftMap_().f(F.this);
  }

  /**
   * Promotes this function to map over the right side of an Either.
   *
   * @return This function promoted to map over the right side of an Either.
   */
  @SuppressWarnings({"unchecked"})
  public final <X> F<Either<X, A>, Either<X, B>> mapRight() {
    return Either.<X, A, B>rightMap_().f(F.this);
  }

  /**
   * Returns a function that returns the left side of a given Either, or this function applied to the right side.
   *
   * @return a function that returns the left side of a given Either, or this function applied to the right side.
   */
  public final F<Either<B, A>, B> onLeft() {
    return new F<Either<B, A>, B>() {
      public B f(final Either<B, A> either) {
        return either.left().on(F.this);
      }
    };
  }

  /**
   * Returns a function that returns the right side of a given Either, or this function applied to the left side.
   *
   * @return a function that returns the right side of a given Either, or this function applied to the left side.
   */
  public final F<Either<A, B>, B> onRight() {
    return new F<Either<A, B>, B>() {
      public B f(final Either<A, B> either) {
        return either.right().on(F.this);
      }
    };
  }

  /**
   * Promotes this function to return its value in an Iterable.
   *
   * @return This function promoted to return its value in an Iterable.
   */
  @SuppressWarnings({"unchecked"})
  public final F<A, IterableW<B>> iterableK() {
    return IterableW.<A, B>arrow().f(F.this);
  }

  /**
   * Promotes this function to map over Iterables.
   *
   * @return This function promoted to map over Iterables.
   */
  @SuppressWarnings({"unchecked"})
  public final F<Iterable<A>, IterableW<B>> mapIterable() {
    return IterableW.<A, B>map().f(F.this).o(IterableW.<A, Iterable<A>>wrap());
  }

  /**
   * Promotes this function to return its value in a NonEmptyList.
   *
   * @return This function promoted to return its value in a NonEmptyList.
   */
  @SuppressWarnings({"unchecked"})
  public final F<A, NonEmptyList<B>> nelK() {
    return NonEmptyList.<B>nel().o(F.this);
  }

  /**
   * Promotes this function to map over a NonEmptyList.
   *
   * @return This function promoted to map over a NonEmptyList.
   */
  public final F<NonEmptyList<A>, NonEmptyList<B>> mapNel() {
    return new F<NonEmptyList<A>, NonEmptyList<B>>() {
      public NonEmptyList<B> f(final NonEmptyList<A> list) {
        return list.map(F.this);
      }
    };
  }

  /**
   * Promotes this function to return its value in a Set.
   *
   * @param o An order for the set.
   * @return This function promoted to return its value in a Set.
   */
  public final F<A, Set<B>> setK(final Ord<B> o) {
    return new F<A, Set<B>>() {
      public Set<B> f(final A a) {
        return Set.single(o, F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over a Set.
   *
   * @param o An order for the resulting set.
   * @return This function promoted to map over a Set.
   */
  public final F<Set<A>, Set<B>> mapSet(final Ord<B> o) {
    return new F<Set<A>, Set<B>>() {
      public Set<B> f(final Set<A> set) {
        return set.map(o, F.this);
      }
    };
  }

  /**
   * Promotes this function to return its value in a Tree.
   *
   * @return This function promoted to return its value in a Tree.
   */
  public final F<A, Tree<B>> treeK() {
    return new F<A, Tree<B>>() {
      public Tree<B> f(final A a) {
        return Tree.leaf(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over a Tree.
   *
   * @return This function promoted to map over a Tree.
   */
  @SuppressWarnings({"unchecked"})
  public final F<Tree<A>, Tree<B>> mapTree() {
    return Tree.<A, B>fmap_().f(F.this);
  }

  /**
   * Returns a function that maps this function over a tree and folds it with the given monoid.
   *
   * @param m The monoid with which to fold the mapped tree.
   * @return a function that maps this function over a tree and folds it with the given monoid.
   */
  public final F<Tree<A>, B> foldMapTree(final Monoid<B> m) {
    return Tree.foldMap_(F.this, m);
  }

  /**
   * Promotes this function to return its value in a TreeZipper.
   *
   * @return This function promoted to return its value in a TreeZipper.
   */
  public final F<A, TreeZipper<B>> treeZipperK() {
    return treeK().andThen(TreeZipper.<B>fromTree());
  }

  /**
   * Promotes this function to map over a TreeZipper.
   *
   * @return This function promoted to map over a TreeZipper.
   */
  public final F<TreeZipper<A>, TreeZipper<B>> mapTreeZipper() {
    return new F<TreeZipper<A>, TreeZipper<B>>() {
      public TreeZipper<B> f(final TreeZipper<A> zipper) {
        return zipper.map(F.this);
      }
    };
  }

  /**
   * Promotes this function so that it returns its result on the failure side of a Validation.
   * Kleisli arrow for the Validation failure projection.
   *
   * @return This function promoted to return its result on the failure side of a Validation.
   */
  public final <C> F<A, Validation<B, C>> failK() {
    return new F<A, Validation<B, C>>() {
      public Validation<B, C> f(final A a) {
        return Validation.fail(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function so that it returns its result on the success side of an Validation.
   * Kleisli arrow for the Validation success projection.
   *
   * @return This function promoted to return its result on the success side of an Validation.
   */
  public final <C> F<A, Validation<C, B>> successK() {
    return new F<A, Validation<C, B>>() {
      public Validation<C, B> f(final A a) {
        return Validation.success(F.this.f(a));
      }
    };
  }

  /**
   * Promotes this function to map over the failure side of a Validation.
   *
   * @return This function promoted to map over the failure side of a Validation.
   */
  public final <X> F<Validation<A, X>, Validation<B, X>> mapFail() {
    return new F<Validation<A, X>, Validation<B, X>>() {
      public Validation<B, X> f(final Validation<A, X> validation) {
        return validation.f().map(F.this);
      }
    };
  }

  /**
   * Promotes this function to map over the success side of a Validation.
   *
   * @return This function promoted to map over the success side of a Validation.
   */
  public final <X> F<Validation<X, A>, Validation<X, B>> mapSuccess() {
    return new F<Validation<X, A>, Validation<X, B>>() {
      public Validation<X, B> f(final Validation<X, A> validation) {
        return validation.map(F.this);
      }
    };
  }

  /**
   * Returns a function that returns the failure side of a given Validation,
   * or this function applied to the success side.
   *
   * @return a function that returns the failure side of a given Validation,
   *         or this function applied to the success side.
   */
  public final F<Validation<B, A>, B> onFail() {
    return new F<Validation<B, A>, B>() {
      public B f(final Validation<B, A> v) {
        return v.f().on(F.this);
      }
    };
  }

  /**
   * Returns a function that returns the success side of a given Validation,
   * or this function applied to the failure side.
   *
   * @return a function that returns the success side of a given Validation,
   *         or this function applied to the failure side.
   */
  public final F<Validation<A, B>, B> onSuccess() {
    return new F<Validation<A, B>, B>() {
      public B f(final Validation<A, B> v) {
        return v.on(F.this);
      }
    };
  }

  /**
   * Promotes this function to return its value in a Zipper.
   *
   * @return This function promoted to return its value in a Zipper.
   */
  public final F<A, Zipper<B>> zipperK() {
    return streamK().andThen(new F<Stream<B>, Zipper<B>>() {
      public Zipper<B> f(final Stream<B> stream) {
        return fromStream(stream).some();
      }
    });
  }

  /**
   * Promotes this function to map over a Zipper.
   *
   * @return This function promoted to map over a Zipper.
   */
  public final F<Zipper<A>, Zipper<B>> mapZipper() {
    return new F<Zipper<A>, Zipper<B>>() {
      public Zipper<B> f(final Zipper<A> zipper) {
        return zipper.map(F.this);
      }
    };
  }

  /**
   * Promotes this function to map over an Equal as a contravariant functor.
   *
   * @return This function promoted to map over an Equal as a contravariant functor.
   */
  public final F<Equal<B>, Equal<A>> comapEqual() {
    return new F<Equal<B>, Equal<A>>() {
      public Equal<A> f(final Equal<B> equal) {
        return equal.comap(F.this);
      }
    };
  }

  /**
   * Promotes this function to map over a Hash as a contravariant functor.
   *
   * @return This function promoted to map over a Hash as a contravariant functor.
   */
  public final F<Hash<B>, Hash<A>> comapHash() {
    return new F<Hash<B>, Hash<A>>() {
      public Hash<A> f(final Hash<B> hash) {
        return hash.comap(F.this);
      }
    };
  }

  /**
   * Promotes this function to map over a Show as a contravariant functor.
   *
   * @return This function promoted to map over a Show as a contravariant functor.
   */
  public final F<Show<B>, Show<A>> comapShow() {
    return new F<Show<B>, Show<A>>() {
      public Show<A> f(final Show<B> s) {
        return s.comap(F.this);
      }
    };
  }

  /**
   * Promotes this function to map over the first element of a pair.
   *
   * @return This function promoted to map over the first element of a pair.
   */
  public final <C> F<P2<A, C>, P2<B, C>> mapFst() {
    return P2.map1_(F.this);
  }

  /**
   * Promotes this function to map over the second element of a pair.
   *
   * @return This function promoted to map over the second element of a pair.
   */
  public final <C> F<P2<C, A>, P2<C, B>> mapSnd() {
    return P2.map2_(F.this);
  }

  /**
   * Promotes this function to map over both elements of a pair.
   *
   * @return This function promoted to map over both elements of a pair.
   */
  public final F<P2<A, A>, P2<B, B>> mapBoth() {
    return new F<P2<A, A>, P2<B, B>>() {
      public P2<B, B> f(final P2<A, A> aap2) {
        return P2.map(F.this, aap2);
      }
    };
  }

  /**
   * Maps this function over a SynchronousQueue.
   *
   * @param as A SynchronousQueue to map this function over.
   * @return A new SynchronousQueue with this function applied to each element.
   */
  public final SynchronousQueue<B> mapJ(final SynchronousQueue<A> as) {
    final SynchronousQueue<B> bs = new SynchronousQueue<B>();
    bs.addAll(iterableStream(as).map(this).toCollection());
    return bs;
  }


  /**
   * Maps this function over a PriorityBlockingQueue.
   *
   * @param as A PriorityBlockingQueue to map this function over.
   * @return A new PriorityBlockingQueue with this function applied to each element.
   */
  public final PriorityBlockingQueue<B> mapJ(final PriorityBlockingQueue<A> as) {
    return new PriorityBlockingQueue<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a LinkedBlockingQueue.
   *
   * @param as A LinkedBlockingQueue to map this function over.
   * @return A new LinkedBlockingQueue with this function applied to each element.
   */
  public final LinkedBlockingQueue<B> mapJ(final LinkedBlockingQueue<A> as) {
    return new LinkedBlockingQueue<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a CopyOnWriteArraySet.
   *
   * @param as A CopyOnWriteArraySet to map this function over.
   * @return A new CopyOnWriteArraySet with this function applied to each element.
   */
  public final CopyOnWriteArraySet<B> mapJ(final CopyOnWriteArraySet<A> as) {
    return new CopyOnWriteArraySet<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a CopyOnWriteArrayList.
   *
   * @param as A CopyOnWriteArrayList to map this function over.
   * @return A new CopyOnWriteArrayList with this function applied to each element.
   */
  public final CopyOnWriteArrayList<B> mapJ(final CopyOnWriteArrayList<A> as) {
    return new CopyOnWriteArrayList<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a ConcurrentLinkedQueue.
   *
   * @param as A ConcurrentLinkedQueue to map this function over.
   * @return A new ConcurrentLinkedQueue with this function applied to each element.
   */
  public final ConcurrentLinkedQueue<B> mapJ(final ConcurrentLinkedQueue<A> as) {
    return new ConcurrentLinkedQueue<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over an ArrayBlockingQueue.
   *
   * @param as An ArrayBlockingQueue to map this function over.
   * @return A new ArrayBlockingQueue with this function applied to each element.
   */
  public final ArrayBlockingQueue<B> mapJ(final ArrayBlockingQueue<A> as) {
    final ArrayBlockingQueue<B> bs = new ArrayBlockingQueue<B>(as.size());
    bs.addAll(iterableStream(as).map(this).toCollection());
    return bs;
  }


  /**
   * Maps this function over a TreeSet.
   *
   * @param as A TreeSet to map this function over.
   * @return A new TreeSet with this function applied to each element.
   */
  public final TreeSet<B> mapJ(final TreeSet<A> as) {
    return new TreeSet<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a PriorityQueue.
   *
   * @param as A PriorityQueue to map this function over.
   * @return A new PriorityQueue with this function applied to each element.
   */
  public final PriorityQueue<B> mapJ(final PriorityQueue<A> as) {
    return new PriorityQueue<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over a LinkedList.
   *
   * @param as A LinkedList to map this function over.
   * @return A new LinkedList with this function applied to each element.
   */
  public final LinkedList<B> mapJ(final LinkedList<A> as) {
    return new LinkedList<B>(iterableStream(as).map(this).toCollection());
  }

  /**
   * Maps this function over an ArrayList.
   *
   * @param as An ArrayList to map this function over.
   * @return A new ArrayList with this function applied to each element.
   */
  public final ArrayList<B> mapJ(final ArrayList<A> as) {
    return new ArrayList<B>(iterableStream(as).map(this).toCollection());
  }
}
