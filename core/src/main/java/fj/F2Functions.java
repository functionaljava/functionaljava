package fj;

import fj.control.parallel.Promise;
import fj.data.*;
import fj.function.Try2;

import static fj.P.p;
import static fj.data.IterableW.wrap;
import static fj.data.Set.iterableSet;
import static fj.data.Tree.node;
import static fj.data.TreeZipper.treeZipper;
import static fj.data.Zipper.zipper;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F2Functions {


    /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument.
     */
    static public <A, B, C> F<B, C> f(final F2<A, B, C> f, final A a) {
        return new F<B, C>() {
            public C f(final B b) {
                return f.f(a, b);
            }
        };
    }

    /**
     * Curries this wrapped function to a wrapped function of arity-1 that returns another wrapped function.
     *
     * @return a wrapped function of arity-1 that returns another wrapped function.
     */
    static public <A, B, C> F<A, F<B, C>> curry(final F2<A, B, C> f) {
        return new F<A, F<B, C>>() {
            public F<B, C> f(final A a) {
                return new F<B, C>() {
                    public C f(final B b) {
                        return f.f(a, b);
                    }
                };
            }
        };
    }

    /**
     * Flips the arguments of this function.
     *
     * @return A new function with the arguments of this function flipped.
     */
    static public <A, B, C> F2<B, A, C> flip(final F2<A, B, C> f) {
        return new F2<B, A, C>() {
            public C f(final B b, final A a) {
                return f.f(a, b);
            }
        };
    }

    /**
     * Uncurries this function to a function on tuples.
     *
     * @return A new function that calls this function with the elements of a given tuple.
     */
    static public <A, B, C> F<P2<A, B>, C> tuple(final F2<A, B, C> f) {
        return new F<P2<A, B>, C>() {
            public C f(final P2<A, B> p) {
                return f.f(p._1(), p._2());
            }
        };
    }

    /**
     * Promotes this function to a function on Arrays.
     *
     * @return This function promoted to transform Arrays.
     */
    static public <A, B, C> F2<Array<A>, Array<B>, Array<C>> arrayM(final F2<A, B, C> f) {
        return new F2<Array<A>, Array<B>, Array<C>>() {
            public Array<C> f(final Array<A> a, final Array<B> b) {
                return a.bind(b, curry(f));
            }
        };
    }

    /**
     * Promotes this function to a function on Promises.
     *
     * @return This function promoted to transform Promises.
     */
    static public <A, B, C> F2<Promise<A>, Promise<B>, Promise<C>> promiseM(final F2<A, B, C> f) {
        return new F2<Promise<A>, Promise<B>, Promise<C>>() {
            public Promise<C> f(final Promise<A> a, final Promise<B> b) {
                return a.bind(b, curry(f));
            }
        };
    }

    /**
     * Promotes this function to a function on Iterables.
     *
     * @return This function promoted to transform Iterables.
     */
    static public <A, B, C> F2<Iterable<A>, Iterable<B>, IterableW<C>> iterableM(final F2<A, B, C> f) {
        return new F2<Iterable<A>, Iterable<B>, IterableW<C>>() {
            public IterableW<C> f(final Iterable<A> a, final Iterable<B> b) {
                return IterableW.liftM2(curry(f)).f(a).f(b);
            }
        };
    }

    /**
     * Promotes this function to a function on Lists.
     *
     * @return This function promoted to transform Lists.
     */
    static public <A, B, C> F2<List<A>, List<B>, List<C>> listM(final F2<A, B, C> f) {
        return new F2<List<A>, List<B>, List<C>>() {
            public List<C> f(final List<A> a, final List<B> b) {
                return List.liftM2(curry(f)).f(a).f(b);
            }
        };
    }

    /**
     * Promotes this function to a function on non-empty lists.
     *
     * @return This function promoted to transform non-empty lists.
     */
    static public <A, B, C> F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> nelM(final F2<A, B, C> f) {
        return new F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>>() {
            public NonEmptyList<C> f(final NonEmptyList<A> as, final NonEmptyList<B> bs) {
                return NonEmptyList.fromList(as.toList().bind(bs.toList(), f)).some();
            }
        };
    }

    /**
     * Promotes this function to a function on Options.
     *
     * @return This function promoted to transform Options.
     */
    static public <A, B, C> F2<Option<A>, Option<B>, Option<C>> optionM(final F2<A, B, C> f) {
        return new F2<Option<A>, Option<B>, Option<C>>() {
            public Option<C> f(final Option<A> a, final Option<B> b) {
                return Option.liftM2(curry(f)).f(a).f(b);
            }
        };
    }

    /**
     * Promotes this function to a function on Sets.
     *
     * @param o An ordering for the result of the promoted function.
     * @return This function promoted to transform Sets.
     */
    static public <A, B, C> F2<Set<A>, Set<B>, Set<C>> setM(final F2<A, B, C> f, final Ord<C> o) {
        return new F2<Set<A>, Set<B>, Set<C>>() {
            public Set<C> f(final Set<A> as, final Set<B> bs) {
                Set<C> cs = Set.empty(o);
                for (final A a : as)
                    for (final B b : bs)
                        cs = cs.insert(f.f(a, b));
                return cs;
            }
        };
    }

    /**
     * Promotes this function to a function on Streams.
     *
     * @return This function promoted to transform Streams.
     */
    static public <A, B, C> F2<Stream<A>, Stream<B>, Stream<C>> streamM(final F2<A, B, C> f) {
        return new F2<Stream<A>, Stream<B>, Stream<C>>() {
            public Stream<C> f(final Stream<A> as, final Stream<B> bs) {
                return as.bind(bs, f);
            }
        };
    }

    /**
     * Promotes this function to a function on Trees.
     *
     * @return This function promoted to transform Trees.
     */
    static public <A, B, C> F2<Tree<A>, Tree<B>, Tree<C>> treeM(final F2<A, B, C> f) {
        return new F2<Tree<A>, Tree<B>, Tree<C>>() {
            public Tree<C> f(final Tree<A> as, final Tree<B> bs) {
                final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
                return node(f.f(as.root(), bs.root()), new P1<Stream<Tree<C>>>() {
                    public Stream<Tree<C>> _1() {
                        return streamM(self).f(as.subForest()._1(), bs.subForest()._1());
                    }
                });
            }
        };
    }

    /**
     * Promotes this function to zip two arrays, applying the function lock-step over both Arrays.
     *
     * @return A function that zips two arrays with this function.
     */
    static public <A, B, C> F2<Array<A>, Array<B>, Array<C>> zipArrayM(final F2<A, B, C> f) {
        return new F2<Array<A>, Array<B>, Array<C>>() {
            public Array<C> f(final Array<A> as, final Array<B> bs) {
                return as.zipWith(bs, f);
            }
        };
    }

    /**
     * Promotes this function to zip two iterables, applying the function lock-step over both iterables.
     *
     * @return A function that zips two iterables with this function.
     */
    static public <A, B, C> F2<Iterable<A>, Iterable<B>, Iterable<C>> zipIterableM(final F2<A, B, C> f) {
        return new F2<Iterable<A>, Iterable<B>, Iterable<C>>() {
            public Iterable<C> f(final Iterable<A> as, final Iterable<B> bs) {
                return wrap(as).zipWith(bs, f);
            }
        };
    }

    /**
     * Promotes this function to zip two lists, applying the function lock-step over both lists.
     *
     * @return A function that zips two lists with this function.
     */
    static public <A, B, C> F2<List<A>, List<B>, List<C>> zipListM(final F2<A, B, C> f) {
        return new F2<List<A>, List<B>, List<C>>() {
            public List<C> f(final List<A> as, final List<B> bs) {
                return as.zipWith(bs, f);
            }
        };
    }


    /**
     * Promotes this function to zip two streams, applying the function lock-step over both streams.
     *
     * @return A function that zips two streams with this function.
     */
    static public <A, B, C> F2<Stream<A>, Stream<B>, Stream<C>> zipStreamM(final F2<A, B, C> f) {
        return new F2<Stream<A>, Stream<B>, Stream<C>>() {
            public Stream<C> f(final Stream<A> as, final Stream<B> bs) {
                return as.zipWith(bs, f);
            }
        };
    }

    /**
     * Promotes this function to zip two non-empty lists, applying the function lock-step over both lists.
     *
     * @return A function that zips two non-empty lists with this function.
     */
    static public <A, B, C> F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> zipNelM(final F2<A, B, C> f) {
        return new F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>>() {
            public NonEmptyList<C> f(final NonEmptyList<A> as, final NonEmptyList<B> bs) {
                return NonEmptyList.fromList(as.toList().zipWith(bs.toList(), f)).some();
            }
        };
    }

    /**
     * Promotes this function to zip two sets, applying the function lock-step over both sets.
     *
     * @param o An ordering for the resulting set.
     * @return A function that zips two sets with this function.
     */
    static public <A, B, C> F2<Set<A>, Set<B>, Set<C>> zipSetM(final F2<A, B, C> f, final Ord<C> o) {
        return new F2<Set<A>, Set<B>, Set<C>>() {
            public Set<C> f(final Set<A> as, final Set<B> bs) {
                return iterableSet(o, as.toStream().zipWith(bs.toStream(), f));
            }
        };
    }

    /**
     * Promotes this function to zip two trees, applying the function lock-step over both trees.
     * The structure of the resulting tree is the structural intersection of the two trees.
     *
     * @return A function that zips two trees with this function.
     */
    static public <A, B, C> F2<Tree<A>, Tree<B>, Tree<C>> zipTreeM(final F2<A, B, C> f) {
        return new F2<Tree<A>, Tree<B>, Tree<C>>() {
            public Tree<C> f(final Tree<A> ta, final Tree<B> tb) {
                final F2<Tree<A>, Tree<B>, Tree<C>> self = this;
                return node(f.f(ta.root(), tb.root()), new P1<Stream<Tree<C>>>() {
                    public Stream<Tree<C>> _1() {
                        return zipStreamM(self).f(ta.subForest()._1(), tb.subForest()._1());
                    }
                });
            }
        };
    }

    /**
     * Promotes this function to zip two zippers, applying the function lock-step over both zippers in both directions.
     * The structure of the resulting zipper is the structural intersection of the two zippers.
     *
     * @return A function that zips two zippers with this function.
     */
    static public <A, B, C> F2<Zipper<A>, Zipper<B>, Zipper<C>> zipZipperM(final F2<A, B, C> f) {
        return new F2<Zipper<A>, Zipper<B>, Zipper<C>>() {
            @SuppressWarnings({"unchecked"})
            public Zipper<C> f(final Zipper<A> ta, final Zipper<B> tb) {
                final F2<Stream<A>, Stream<B>, Stream<C>> sf = zipStreamM(f);
                return zipper(sf.f(ta.lefts(), tb.lefts()), f.f(ta.focus(), tb.focus()), sf.f(ta.rights(), tb.rights()));
            }
        };
    }

    /**
     * Promotes this function to zip two TreeZippers, applying the function lock-step over both zippers in all directions.
     * The structure of the resulting TreeZipper is the structural intersection of the two TreeZippers.
     *
     * @return A function that zips two TreeZippers with this function.
     */
    static public <A, B, C> F2<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>> zipTreeZipperM(final F2<A, B, C> f) {
        return new F2<TreeZipper<A>, TreeZipper<B>, TreeZipper<C>>() {
            @SuppressWarnings({"unchecked"})
            public TreeZipper<C> f(final TreeZipper<A> ta, final TreeZipper<B> tb) {
                final F2<Stream<Tree<A>>, Stream<Tree<B>>, Stream<Tree<C>>> sf = zipStreamM(treeM(f));
                final
                F2<Stream<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>>,
                        Stream<P3<Stream<Tree<B>>, B, Stream<Tree<B>>>>,
                        Stream<P3<Stream<Tree<C>>, C, Stream<Tree<C>>>>>
                        pf =
                        zipStreamM(new F2<P3<Stream<Tree<A>>, A, Stream<Tree<A>>>,
                                P3<Stream<Tree<B>>, B, Stream<Tree<B>>>,
                                P3<Stream<Tree<C>>, C, Stream<Tree<C>>>>() {
                            public P3<Stream<Tree<C>>, C, Stream<Tree<C>>> f(final P3<Stream<Tree<A>>, A, Stream<Tree<A>>> pa,
                                                                             final P3<Stream<Tree<B>>, B, Stream<Tree<B>>> pb) {
                                return p(zipStreamM(treeM(f)).f(pa._1(), pb._1()), f.f(pa._2(), pb._2()),
                                        zipStreamM(treeM(f)).f(pa._3(), pb._3()));
                            }
                        });
                return treeZipper(treeM(f).f(ta.p()._1(), tb.p()._1()), sf.f(ta.lefts(), tb.lefts()),
                        sf.f(ta.rights(), tb.rights()), pf.f(ta.p()._4(), tb.p()._4()));
            }
        };
    }

    static public <A, B, C, Z> F2<Z, B, C> contramapFirst(F2<A, B, C> target, F<Z, A> f) {
        return (z, b) -> target.f(f.f(z), b);
    }

    static public <A, B, C, Z> F2<A, Z, C> contramapSecond(F2<A, B, C> target, F<Z, B> f) {
        return (a, z) -> target.f(a, f.f(z));
    }

    static public <A, B, C, X, Y> F2<X, Y, C> contramap(F2<A, B, C> target, F<X, A> f, F<Y, B> g) {
        return contramapSecond(contramapFirst(target, f), g);
    }

    static public <A, B, C, Z> F2<A, B, Z> map(F2<A, B, C> target, F<C, Z> f) {
        return (a, b) -> f.f(target.f(a, b));
    }

}
