package fj.data.fingertrees;

import fj.*;
import fj.data.Option;
import fj.data.Stream;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;

import static fj.Function.constant;
import static fj.data.List.list;
import static fj.Function.flip;
import static fj.data.Stream.nil;

/**
 * A finger tree with 1-4-digits on the left and right, and a finger tree of 2-3-nodes in the middle.
 */
public final class Deep<V, A> extends FingerTree<V, A> {
  private final V v;
  private final Digit<V, A> prefix;
  private final FingerTree<V, Node<V, A>> middle;
  private final Digit<V, A> suffix;

  Deep(final Measured<V, A> m, final V v, final Digit<V, A> prefix,
       final FingerTree<V, Node<V, A>> middle,
       final Digit<V, A> suffix) {
    super(m);
    this.v = v;
    this.prefix = prefix;
    this.middle = middle;
    this.suffix = suffix;
  }

  /**
   * Returns the first few elements of this tree.
   *
   * @return the first few elements of this tree.
   */
  public Digit<V, A> prefix() {
    return prefix;
  }

  /**
   * Returns a finger tree of the inner nodes of this tree.
   *
   * @return a finger tree of the inner nodes of this tree.
   */
  public FingerTree<V, Node<V, A>> middle() {
    return middle;
  }

  /**
   * Returns the last few elements of this tree.
   *
   * @return the last few elements of this tree.
   */
  public Digit<V, A> suffix() {
    return suffix;
  }

  @Override public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return prefix.foldRight(aff, middle.foldRight(flip(Node.foldRight_(aff)), suffix.foldRight(aff, z)));
  }

  @Override public A reduceRight(final F<A, F<A, A>> aff) {
    return prefix.foldRight(aff, middle.foldRight(flip(Node.foldRight_(aff)), suffix.reduceRight(aff)));
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return suffix.foldLeft(bff, middle.foldLeft(Node.foldLeft_(bff), prefix.foldLeft(bff, z)));
  }

  @Override public A reduceLeft(final F<A, F<A, A>> aff) {
    return suffix.foldLeft(aff, middle.foldLeft(Node.foldLeft_(aff), prefix.reduceLeft(aff)));
  }

  @Override public <B> FingerTree<V, B> map(final F<A, B> abf, final Measured<V, B> m) {
    return new Deep<>(m, v, prefix.map(abf, m), middle.map(Node.liftM(abf, m), m.nodeMeasured()),
        suffix.map(abf, m));
  }

  /**
   * Returns the sum of the measurements of this tree's elements, according to the monoid.
   *
   * @return the sum of the measurements of this tree's elements, according to the monoid.
   */
  public V measure() {
    return v;
  }

  /**
   * Pattern matching on the tree. Matches the function on the Deep tree.
   */
  @Override public <B> B match(final F<Empty<V, A>, B> empty, final F<Single<V, A>, B> single,
                               final F<Deep<V, A>, B> deep) {
    return deep.f(this);
  }

  @Override public FingerTree<V, A> cons(final A a) {
    final Measured<V, A> m = measured();
    final V measure = m.sum(m.measure(a), v);
    final MakeTree<V, A> mk = mkTree(m);

    return prefix.match(
      one -> new Deep<>(m, measure, mk.two(a, one.value()), middle, suffix),
      two -> new Deep<>(m, measure, mk.three(a, two.values()._1(), two.values()._2()), middle, suffix),
      three -> new Deep<>(m, measure, mk.four(a, three.values()._1(), three.values()._2(), three.values()._3()), middle, suffix),
      four -> new Deep<>(m, measure, mk.two(a, four.values()._1()), middle.cons(mk.node3(four.values()._2(), four.values()._3(), four.values()._4())), suffix));
  }

  public FingerTree<V, A> snoc(final A a) {
    final Measured<V, A> m = measured();
    final V measure = m.sum(m.measure(a), v);
    final MakeTree<V, A> mk = mkTree(m);

    return suffix.match(
      one -> new Deep<>(m, measure, prefix, middle, mk.two(one.value(), a)),
      two -> new Deep<>(m, measure, prefix, middle, mk.three(two.values()._1(), two.values()._2(), a)),
      three -> new Deep<>(m, measure, prefix, middle, mk.four(three.values()._1(), three.values()._2(), three.values()._3(), a)),
      four -> new Deep<>(m, measure, prefix, middle.snoc(mk.node3(four.values()._1(), four.values()._2(), four.values()._3())), mk.two(four.values()._4(), a)));
  }

  @Override public A head() {
    return prefix.match(
      One::value,
      two -> two.values()._1(),
      three -> three.values()._1(),
      four -> four.values()._1());
  }

  @Override public A last() {
    return suffix.match(
      One::value,
      two -> two.values()._2(),
      three -> three.values()._3(),
      four -> four.values()._4());
  }

  private static <V, A> FingerTree<V, A> deepL(final Measured<V, A> measured, final Option<Digit<V, A>> lOpt, final FingerTree<V, Node<V, A>> m, final Digit<V, A> r) {
    return lOpt.option(
      P.lazy(() -> m.isEmpty() ? r.toTree() : mkTree(measured).deep(m.head().toDigit(), m.tail(), r)),
      (F<Digit<V, A>, FingerTree<V, A>>) l -> mkTree(measured).deep(l, m, r)
    );
  }

  private static <V, A> FingerTree<V, A> deepR(final Measured<V, A> measured, final Option<Digit<V, A>> rOpt, final FingerTree<V, Node<V, A>> m, final Digit<V, A> l) {
    return rOpt.option(
      P.lazy(() -> m.isEmpty() ? l.toTree() : mkTree(measured).deep(l, m.init(), m.last().toDigit())),
      (F<Digit<V, A>, FingerTree<V, A>>) r -> mkTree(measured).deep(l, m, r)
    );
  }

  @Override public FingerTree<V, A> tail() { return deepL(measured(), prefix.tail(), middle, suffix); }

  @Override public FingerTree<V, A> init() { return deepR(measured(), suffix.init(), middle, prefix); }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    final Measured<V, A> m = measured();
    return t.match(
      constant(this),
      single -> snoc(single.value()),
      deep -> new Deep<>(m, m.sum(measure(), deep.measure()), prefix,
        addDigits0(m, middle, suffix, deep.prefix, deep.middle), deep.suffix));
  }

  @Override P3<FingerTree<V, A>, A, FingerTree<V, A>> split1(final F<V, Boolean> predicate, final V acc) {
    final Measured<V, A> m = measured();
    final V accL = m.sum(acc, prefix.measure());
    if (predicate.f(accL)) {
      final P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> lxr = prefix.split1(predicate, acc);
      return P.p(lxr._1().option(new Empty<>(m), Digit::toTree), lxr._2(), deepL(m, lxr._3(), middle, suffix));
    } else {
      final V accM = m.sum(accL, middle.measure());
      if (predicate.f(accM)) {
        final P3<FingerTree<V, Node<V, A>>, Node<V, A>, FingerTree<V, Node<V, A>>> mlXsMr = middle.split1(predicate, accL);
        final P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> lxr = mlXsMr._2().split1(predicate, m.sum(accL, mlXsMr._1().measure()));
        return P.p(deepR(m, lxr._1(), mlXsMr._1(), prefix), lxr._2(), deepL(m, lxr._3(), mlXsMr._3(), suffix));
      } else {
        final P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> lxr = suffix.split1(predicate, accM);
        return P.p(deepR(m, lxr._1(), middle, prefix), lxr._2(), lxr._3().option(new Empty<>(m), Digit::toTree));
      }
    }
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    final int spr = o.f(prefix.measure());
    if (i < spr) {
      return prefix.lookup(o, i);
    } else {
      final int spm = spr + o.f(middle.measure());
      if (i < spm) {
        final P2<Integer, Node<V, A>> p = middle.lookup(o, i - spr);
        return p._2().lookup(o, p._1());
      } else {
        return suffix.lookup(o, i - spm);
      }
    }
  }

    @Override
    public int length() {
        int midSize = middle.foldLeft((acc, n) -> acc + n.length(), 0);
        return prefix.length() + midSize + suffix.length();
    }

    private static <V, A> FingerTree<V, Node<V, A>> addDigits0(
            final Measured<V, A> m, final FingerTree<V, Node<V, A>> m1,
            final Digit<V, A> s1, final Digit<V, A> p2,
            final FingerTree<V, Node<V, A>> m2) {

        final MakeTree<V, A> mk = mkTree(m);
        return s1.match(
            one1 -> p2.match(
                one2 -> append1(m, m1, mk.node2(one1.value(), one2.value()), m2),
                two2 -> {
                    final V2<A> vs = two2.values();
                    return append1(m, m1, mk.node3(one1.value(), vs._1(), vs._2()), m2);
                },
                three -> {
                    final V3<A> vs = three.values();
                    return append2(m, m1, mk.node2(one1.value(), vs._1()), mk.node2(vs._2(), vs._3()), m2);
                },
                four -> {
                    final V4<A> vs = four.values();
                    return append2(m, m1, mk.node3(one1.value(), vs._1(), vs._2()), mk.node2(vs._3(), vs._4()), m2);
                }
            ),
            two1 -> {
                final V2<A> v1 = two1.values();
                return p2.match(
                    one -> append1(m, m1, mk.node3(v1._1(), v1._2(), one.value()), m2),
                    two2 -> {
                        final V2<A> v2 = two2.values();
                        return append2(m, m1, mk.node2(v1._1(), v1._2()), mk.node2(v2._1(), v2._2()), m2);
                    },
                    three -> {
                        final V3<A> v2 = three.values();
                        return append2(m, m1, mk.node3(v1._1(), v1._2(), v2._1()), mk.node2(v2._2(), v2._3()), m2);
                    },
                    four -> {
                        final V4<A> v2 = four.values();
                        return append2(m, m1, mk.node3(v1._1(), v1._2(), v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
                    }
                );
            },
            three1 -> {
                final V3<A> v1 = three1.values();
                return p2.match(
                    one -> append2(m, m1, mk.node2(v1._1(), v1._2()), mk.node2(v1._3(), one.value()), m2),
                    two -> {
                        final V2<A> v2 = two.values();
                        return append2(m, m1, mk.node3(v1), mk.node2(v2), m2);
                    },
                    three2 -> append2(m, m1, mk.node3(v1), mk.node3(three2.values()), m2),
                    four -> append3(m, m1, mk.node3(v1),
                        mk.node2(four.values()._1(), four.values()._2()),
                        mk.node2(four.values()._3(), four.values()._4()), m2
                    )
                );
            },
            four1 -> {
                final V4<A> v1 = four1.values();
                return p2.match(
                    one -> append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), one.value()), m2),
                    two -> {
                        final V2<A> v2 = two.values();
                        return append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), v2._1(), v2._2()), m2);
                    },
                    three -> {
                        final V3<A> v2 = three.values();
                        return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), v2._1()), mk.node2(v2._2(), v2._3()), m2);
                    },
                    four2 -> {
                        final V4<A> v2 = four2.values();
                        return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
                    }
                );
            }
        );
    }

  private static <V, A> FingerTree<V, Node<V, A>> append1(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs,
                                                          final Node<V, A> a, final FingerTree<V, Node<V, A>> ys) {
    return xs.match(empty -> ys.cons(a), single -> ys.cons(a).cons(single.value()), deep1 -> ys.match(empty -> xs.snoc(a), single -> xs.snoc(a).snoc(single.value()), deep2 -> {
      final Measured<V, Node<V, A>> nm = m.nodeMeasured();
      return new Deep<>(nm, m.sum(m.sum(deep1.v, nm.measure(a)), deep2.v), deep1.prefix,
          addDigits1(nm, deep1.middle, deep1.suffix, a, deep2.prefix, deep2.middle),
          deep2.suffix);
    }));
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits1(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> x, final Node<V, A> n,
                                                                      final Digit<V, Node<V, A>> y,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return x.match(one1 -> y.match(one2 -> append1(m, m1, mk.node3(one1.value(), n, one2.value()), m2), two -> append2(m, m1, mk.node2(one1.value(), n), mk.node2(two.values()), m2), three -> {
      final V3<Node<V, A>> v2 = three.values();
      return append2(m, m1, mk.node3(one1.value(), n, v2._1()), mk.node2(v2._2(), v2._3()), m2);
    }, four -> {
      final V4<Node<V, A>> v2 = four.values();
      return append2(m, m1, mk.node3(one1.value(), n, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
    }), two1 -> {
      final V2<Node<V, A>> v1 = two1.values();
      return y.match(one -> append2(m, m1, mk.node2(v1), mk.node2(n, one.value()), m2), two -> append2(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node2(two.values()), m2), three -> append2(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node3(three.values()), m2), four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()),
                       m2);
      });
    }, three -> {
      final V3<Node<V, A>> v1 = three.values();
      return y.match(one -> append2(m, m1, mk.node3(v1), mk.node2(n, one.value()), m2), two -> {
        final V2<Node<V, A>> v2 = two.values();
        return append2(m, m1, mk.node3(v1), mk.node3(n, v2._1(), v2._2()), m2);
      }, three1 -> {
        final V3<Node<V, A>> v2 = three1.values();
        return append3(m, m1, mk.node3(v1), mk.node2(n, v2._1()), mk.node2(v2._2(), v2._3()), m2);
      }, four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append3(m, m1, mk.node3(v1), mk.node3(n, v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
      });
    }, four -> {
      final V4<Node<V, A>> v1 = four.values();
      return y.match(one -> append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, one.value()), m2), two -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), n), mk.node2(two.values()),
                     m2), three -> {
        final V3<Node<V, A>> v2 = three.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, v2._1()),
                       mk.node2(v2._2(), v2._3()), m2);
      }, four1 -> {
        final V4<Node<V, A>> v2 = four1.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, v2._1()),
                       mk.node3(v2._2(), v2._3(), v2._4()), m2);
      });
    });
  }

  private static <V, A> FingerTree<V, Node<V, A>> append2(final Measured<V, A> m, final FingerTree<V, Node<V, A>> t1,
                                                          final Node<V, A> n1, final Node<V, A> n2,
                                                          final FingerTree<V, Node<V, A>> t2) {
    return t1.match(empty -> t2.cons(n2).cons(n1), single -> t2.cons(n2).cons(n1).cons(single.value()), deep -> t2.match(empty -> deep.snoc(n1).snoc(n2), single -> deep.snoc(n1).snoc(n2).snoc(single.value()), deep2 -> new Deep<>(m.nodeMeasured(),
        m.sum(m.sum(m.sum(deep.measure(), n1.measure()), n2.measure()),
            deep2.measure()), deep.prefix,
        addDigits2(m.nodeMeasured(), deep.middle, deep.suffix, n1, n2, deep2.prefix,
            deep2.middle), deep2.suffix)));
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits2(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(one -> prefix.match(one2 -> append2(m, m1, mk.node2(one.value(), n1), mk.node2(n2, one2.value()), m2), two -> append2(m, m1, mk.node3(one.value(), n1, n2), mk.node2(two.values()), m2), three -> append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(three.values()), m2), four -> {
      final V4<Node<V, A>> v2 = four.values();
      return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()),
                     m2);
    }), two -> {
      final V2<Node<V, A>> v1 = two.values();
      return prefix.match(one -> append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, one.value()), m2), two2 -> {
        final V2<Node<V, A>> v2 = two2.values();
        return append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, v2._1(), v2._2()), m2);
      }, three -> {
        final V3<Node<V, A>> v2 = three.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, v2._1()), mk.node2(v2._2(), v2._3()),
                       m2);
      }, four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, v2._1(), v2._2()),
                       mk.node2(v2._3(), v2._4()), m2);
      });
    }, three -> {
      final V3<Node<V, A>> v1 = three.values();
      return prefix.match(one -> append2(m, m1, mk.node3(v1), mk.node3(n1, n2, one.value()), m2), two -> append3(m, m1, mk.node3(v1), mk.node2(n1, n2), mk.node2(two.values()), m2), three2 -> {
        final V3<Node<V, A>> v2 = three2.values();
        return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, v2._1()), mk.node2(v2._2(), v2._3()), m2);
      }, four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
      });
    }, four -> {
      final V4<Node<V, A>> v1 = four.values();
      return prefix.match(one -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), n1), mk.node2(n2, one.value()),
                     m2), two -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                     mk.node2(two.values()), m2), three -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                     mk.node3(three.values()), m2), four2 -> {
        final V4<Node<V, A>> v2 = four2.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                       mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
      });
    });
  }

  @SuppressWarnings("unchecked")
  private static <V, A> FingerTree<V, Node<V, A>> append3(final Measured<V, A> m, final FingerTree<V, Node<V, A>> t1,
                                                          final Node<V, A> n1, final Node<V, A> n2, final Node<V, A> n3,
                                                          final FingerTree<V, Node<V, A>> t2) {
    final Measured<V, Node<V, A>> nm = m.nodeMeasured();
    return t1.match(empty -> t2.cons(n3).cons(n2).cons(n1), single -> t2.cons(n3).cons(n2).cons(n1).cons(single.value()), deep -> t2.match(empty -> deep.snoc(n1).snoc(n2).snoc(n3), single -> deep.snoc(n1).snoc(n2).snoc(n3).snoc(single.value()), deep2 -> new Deep<>(nm, nm.monoid().sumLeft(
        list(deep.v, n1.measure(), n2.measure(), n3.measure(), deep2.v)), deep.prefix,
        addDigits3(nm, deep.middle, deep.suffix, n1, n2, n3, deep2.prefix,
            deep2.middle), deep2.suffix)));
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits3(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Node<V, A> n3,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(one -> prefix.match(one2 -> append2(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, one2.value()), m2), two -> {
      final V2<Node<V, A>> v2 = two.values();
      return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, v2._1(), v2._2()), m2);
    }, three -> {
      final V3<Node<V, A>> v2 = three.values();
      return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, v2._1()), mk.node2(v2._2(), v2._3()), m2);
    }, four -> {
      final V4<Node<V, A>> v2 = four.values();
      return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, v2._1(), v2._2()),
                     mk.node2(v2._3(), v2._4()), m2);
    }), two -> {
      final V2<Node<V, A>> v1 = two.values();
      return prefix.match(one -> append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, one.value()), m2), two1 -> append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, n3), mk.node2(two1.values()), m2), three -> {
        final V3<Node<V, A>> v2 = three.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, v2._1()), mk.node2(v2._2(), v2._3()),
                       m2);
      }, four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, v2._1()),
                       mk.node3(v2._2(), v2._3(), v2._4()), m2);
      });
    }, three -> prefix.match(one -> append3(m, m1, mk.node3(three.values()), mk.node2(n1, n2), mk.node2(n3, one.value()), m2), two -> append3(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node2(two.values()), m2), three2 -> append3(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node3(three2.values()), m2), four -> {
      final V4<Node<V, A>> v2 = four.values();
      return append4(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node2(v2._1(), v2._2()),
                     mk.node2(v2._3(), v2._4()), m2);
    }), four -> {
      final V4<Node<V, A>> v1 = four.values();
      return prefix.match(one -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                     mk.node2(n3, one.value()), m2), two -> {
        final V2<Node<V, A>> v2 = two.values();
        return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                       mk.node3(n3, v2._1(), v2._2()), m2);
      }, three -> {
        final V3<Node<V, A>> v2 = three.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2), mk.node2(n3, v2._1()),
                       mk.node2(v2._2(), v2._3()), m2);
      }, four2 -> {
        final V4<Node<V, A>> v2 = four2.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                       mk.node3(n3, v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
      });
    });
  }

  @SuppressWarnings("unchecked")  
  private static <V, A> FingerTree<V, Node<V, A>> append4(final Measured<V, A> m,
                                                          final FingerTree<V, Node<V, A>> t1,
                                                          final Node<V, A> n1,
                                                          final Node<V, A> n2,
                                                          final Node<V, A> n3,
                                                          final Node<V, A> n4,
                                                          final FingerTree<V, Node<V, A>> t2) {
    final Measured<V, Node<V, A>> nm = m.nodeMeasured();
    return t1.match(empty -> t2.cons(n4).cons(n3).cons(n2).cons(n1), single -> t2.cons(n4).cons(n3).cons(n2).cons(n1).cons(single.value()), deep -> t2.match(empty -> t1.snoc(n1).snoc(n2).snoc(n3).snoc(n4), single -> t1.snoc(n1).snoc(n2).snoc(n3).snoc(n4).snoc(single.value()), deep2 -> new Deep<>(nm, m.monoid().sumLeft(
        list(deep.v, n1.measure(), n2.measure(), n3.measure(), n4.measure(), deep2.v)), deep.prefix,
        addDigits4(nm, deep.middle, deep.suffix, n1, n2, n3, n4, deep2.prefix,
            deep2.middle), deep2.suffix)));
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits4(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Node<V, A> n3, final Node<V, A> n4,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(one -> prefix.match(one2 -> append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, one2.value()), m2), two -> append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, n4), mk.node2(two.values()), m2), three -> {
      final V3<Node<V, A>> v2 = three.values();
      return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, v2._1()), mk.node2(v2._2(), v2._3()),
                     m2);
    }, four -> {
      final V4<Node<V, A>> v2 = four.values();
      return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, v2._1()),
                     mk.node3(v2._2(), v2._3(), v2._4()), m2);
    }), two -> {
      final V2<Node<V, A>> v1 = two.values();
      return prefix.match(one -> append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, n3), mk.node2(n4, one.value()), m2), two2 -> append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node2(two2.values()), m2), three -> append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node3(three.values()), m2), four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node2(v2._1(), v2._2()),
                       mk.node2(v2._3(), v2._4()), m2);
      });
    }, three -> {
      final V3<Node<V, A>> v1 = three.values();
      return prefix.match(one -> append3(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node2(n4, one.value()), m2), two -> {
        final V2<Node<V, A>> v2 = two.values();
        return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node3(n4, v2._1(), v2._2()), m2);
      }, three1 -> {
        final V3<Node<V, A>> v2 = three1.values();
        return append4(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node2(n4, v2._1()), mk.node2(v2._2(), v2._3()),
                       m2);
      }, four -> {
        final V4<Node<V, A>> v2 = four.values();
        return append4(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node3(n4, v2._1(), v2._2()),
                       mk.node2(v2._3(), v2._4()), m2);
      });
    }, four -> {
      final V4<Node<V, A>> v1 = four.values();
      return prefix.match(one -> append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                     mk.node3(n3, n4, one.value()), m2), two -> append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                     mk.node2(n3, n4), mk.node2(two.values()), m2), three -> {
        final V3<Node<V, A>> v2 = three.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                       mk.node3(n3, n4, v2._1()), mk.node2(v2._2(), v2._3()), m2);
      }, four1 -> {
        final V4<Node<V, A>> v2 = four1.values();
        return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                       mk.node3(n3, n4, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
      });
    });
  }

  public String toString() {
    return Show.fingerTreeShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
  }

  public Stream<A> toStream() {
    return prefix().toStream().append(() ->
            middle().match(
                    e -> Stream.<A>nil(),
                    s -> s.value().toStream(),
                    d -> d.toStream().bind(p -> p.toStream())
            )
    ).append(() -> suffix.toStream());
  }

}
