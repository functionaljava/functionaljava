package fj.data.fingertrees;

import fj.F;
import fj.Function;
import fj.P2;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;
import static fj.data.List.list;
import static fj.Function.flip;

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
    return prefix.foldRight(aff, middle.foldRight(flip(Node.<V, A, B>foldRight_(aff)), suffix.foldRight(aff, z)));
  }

  @Override public A reduceRight(final F<A, F<A, A>> aff) {
    return prefix.foldRight(aff, middle.foldRight(flip(Node.<V, A, A>foldRight_(aff)), suffix.reduceRight(aff)));
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return suffix.foldLeft(bff, middle.foldLeft(Node.<V, A, B>foldLeft_(bff), prefix.foldLeft(bff, z)));
  }

  @Override public A reduceLeft(final F<A, F<A, A>> aff) {
    return suffix.foldLeft(aff, middle.foldLeft(Node.<V, A, A>foldLeft_(aff), prefix.reduceLeft(aff)));
  }

  @Override public <B> FingerTree<V, B> map(final F<A, B> abf, final Measured<V, B> m) {
    return new Deep<V, B>(m, v, prefix.map(abf, m), middle.map(Node.<V, A, B>liftM(abf, m), m.nodeMeasured()),
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
    return prefix.match(new F<One<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final One<V, A> one) {
        return new Deep<V, A>(m, measure, mk.two(a, one.value()), middle, suffix);
      }
    }, new F<Two<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Two<V, A> two) {
        return new Deep<V, A>(m, measure, mk.three(a, two.values()._1(), two.values()._2()), middle, suffix);
      }
    }, new F<Three<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Three<V, A> three) {
        return new Deep<V, A>(m, measure, mk.four(a, three.values()._1(), three.values()._2(),
                                                  three.values()._3()), middle, suffix);
      }
    }, new F<Four<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Four<V, A> four) {
        return new Deep<V, A>(m, measure, mk.two(a, four.values()._1()),
                              middle.cons(mk.node3(four.values()._2(), four.values()._3(), four.values()._4())),
                              suffix);
      }
    });
  }

  public FingerTree<V, A> snoc(final A a) {
    final Measured<V, A> m = measured();
    final V measure = m.sum(m.measure(a), v);
    final MakeTree<V, A> mk = mkTree(m);
    return suffix.match(new F<One<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final One<V, A> one) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.two(one.value(), a));
      }
    }, new F<Two<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Two<V, A> two) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.three(two.values()._1(), two.values()._2(), a));
      }
    }, new F<Three<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Three<V, A> three) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.four(three.values()._1(), three.values()._2(),
                                                                  three.values()._3(), a));
      }
    }, new F<Four<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Four<V, A> four) {
        return new Deep<V, A>(m, measure, prefix,
                              middle.snoc(mk.node3(four.values()._1(), four.values()._2(), four.values()._3())),
                              mk.two(four.values()._4(), a));
      }
    });
  }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    final Measured<V, A> m = measured();
    return t.match(Function.<Empty<V, A>, FingerTree<V, A>>constant(t), new F<Single<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Single<V, A> single) {
        return t.snoc(single.value());
      }
    }, new F<Deep<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Deep<V, A> deep) {
        return new Deep<V, A>(m, m.sum(measure(), deep.measure()), prefix,
                              addDigits0(m, middle, suffix, deep.prefix, deep.middle), deep.suffix);
      }
    });
  }

  @SuppressWarnings({"ReturnOfNull", "IfStatementWithIdenticalBranches"})
  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    final int spr = o.f(prefix.measure());
    final int spm = o.f(middle.measure());
    if (i < spr)
        return null; // TODO
      //return prefix.lookup(o, i);
    if (i < spm) {
      return null; // TODO
      /* final P2<Integer, Node<V, A>> p = middle.lookup(o, i - spr);
      return p._2().lookup(o, p._1()); */
    }
    return null; // TODO suffix.lookup(i - spm);
  }

  private static <V, A> FingerTree<V, Node<V, A>> addDigits0(final Measured<V, A> m, final FingerTree<V, Node<V, A>> m1,
                                                             final Digit<V, A> s1, final Digit<V, A> p2,
                                                             final FingerTree<V, Node<V, A>> m2) {
    final MakeTree<V, A> mk = mkTree(m);
    return s1.match(new F<One<V, A>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final One<V, A> one1) {
        return p2.match(new F<One<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final One<V, A> one2) {
            return append1(m, m1, mk.node2(one1.value(), one2.value()), m2);
          }
        }, new F<Two<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Two<V, A> two2) {
            final V2<A> vs = two2.values();
            return append1(m, m1, mk.node3(one1.value(), vs._1(), vs._2()), m2);
          }
        }, new F<Three<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Three<V, A> three) {
            final V3<A> vs = three.values();
            return append2(m, m1, mk.node2(one1.value(), vs._1()), mk.node2(vs._2(), vs._3()), m2);
          }
        }, new F<Four<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Four<V, A> four) {
            final V4<A> vs = four.values();
            return append2(m, m1, mk.node3(one1.value(), vs._1(), vs._2()), mk.node2(vs._3(), vs._4()), m2);
          }
        });
      }
    }, new F<Two<V, A>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Two<V, A> two1) {
        final V2<A> v1 = two1.values();
        return p2.match(new F<One<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final One<V, A> one) {
            return append1(m, m1, mk.node3(v1._1(), v1._2(), one.value()), m2);
          }
        }, new F<Two<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Two<V, A> two2) {
            final V2<A> v2 = two2.values();
            return append2(m, m1, mk.node2(v1._1(), v1._2()), mk.node2(v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Three<V, A> three) {
            final V3<A> v2 = three.values();
            return append2(m, m1, mk.node3(v1._1(), v1._2(), v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Four<V, A> four) {
            final V4<A> v2 = four.values();
            return append2(m, m1, mk.node3(v1._1(), v1._2(), v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Three<V, A>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Three<V, A> three1) {
        final V3<A> v1 = three1.values();
        return p2.match(new F<One<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final One<V, A> one) {
            return append2(m, m1, mk.node2(v1._1(), v1._2()), mk.node2(v1._3(), one.value()), m2);
          }
        }, new F<Two<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Two<V, A> two) {
            final V2<A> v2 = two.values();
            return append2(m, m1, mk.node3(v1), mk.node2(v2), m2);
          }
        }, new F<Three<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Three<V, A> three2) {
            return append2(m, m1, mk.node3(v1), mk.node3(three2.values()), m2);
          }
        }, new F<Four<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Four<V, A> four) {
            return append3(m, m1, mk.node3(v1), mk.node2(four.values()._1(), four.values()._2()),
                           mk.node2(four.values()._3(), four.values()._4()), m2);
          }
        });
      }
    }, new F<Four<V, A>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Four<V, A> four1) {
        final V4<A> v1 = four1.values();
        return p2.match(new F<One<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final One<V, A> one) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), one.value()), m2);
          }
        }, new F<Two<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Two<V, A> two) {
            final V2<A> v2 = two.values();
            return append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Three<V, A> three) {
            final V3<A> v2 = three.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), v2._1()),
                           mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, A>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Four<V, A> four2) {
            final V4<A> v2 = four2.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, A>> append1(final Measured<V, A> m, final FingerTree<V, Node<V, A>> xs,
                                                          final Node<V, A> a, final FingerTree<V, Node<V, A>> ys) {
    return xs.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
        return ys.cons(a);
      }
    }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
        return ys.cons(a).cons(single.value());
      }
    }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep1) {
        return ys.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
            return xs.snoc(a);
          }
        }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
            return xs.snoc(a).snoc(single.value());
          }
        }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep2) {
            final Measured<V, Node<V, A>> nm = m.nodeMeasured();
            return new Deep<V, Node<V, A>>(nm, m.sum(m.sum(deep1.v, nm.measure(a)), deep2.v), deep1.prefix,
                                           addDigits1(nm, deep1.middle, deep1.suffix, a, deep2.prefix, deep2.middle),
                                           deep2.suffix);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits1(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> x, final Node<V, A> n,
                                                                      final Digit<V, Node<V, A>> y,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return x.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one1) {
        return y.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one2) {
            return append1(m, m1, mk.node3(one1.value(), n, one2.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append2(m, m1, mk.node2(one1.value(), n), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append2(m, m1, mk.node3(one1.value(), n, v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append2(m, m1, mk.node3(one1.value(), n, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two1) {
        final V2<Node<V, A>> v1 = two1.values();
        return y.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node2(v1), mk.node2(n, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node3(three.values()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n), mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()),
                           m2);
          }
        });
      }
    }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
        final V3<Node<V, A>> v1 = three.values();
        return y.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node3(v1), mk.node2(n, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            final V2<Node<V, A>> v2 = two.values();
            return append2(m, m1, mk.node3(v1), mk.node3(n, v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(v1), mk.node2(n, v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1), mk.node3(n, v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
        final V4<Node<V, A>> v1 = four.values();
        return y.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), n), mk.node2(two.values()),
                           m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, v2._1()),
                           mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n, v2._1()),
                           mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, A>> append2(final Measured<V, A> m, final FingerTree<V, Node<V, A>> t1,
                                                          final Node<V, A> n1, final Node<V, A> n2,
                                                          final FingerTree<V, Node<V, A>> t2) {
    return t1.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
        return t2.cons(n2).cons(n1);
      }
    }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
        return t2.cons(n2).cons(n1).cons(single.value());
      }
    }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep) {
        return t2.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
            return deep.snoc(n1).snoc(n2);
          }
        }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
            return deep.snoc(n1).snoc(n2).snoc(single.value());
          }
        }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep2) {
            return new Deep<V, Node<V, A>>(m.nodeMeasured(),
                                           m.sum(m.sum(m.sum(deep.measure(), n1.measure()), n2.measure()),
                                                 deep2.measure()), deep.prefix,
                                           addDigits2(m.nodeMeasured(), deep.middle, deep.suffix, n1, n2, deep2.prefix,
                                                      deep2.middle), deep2.suffix);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits2(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one2) {
            return append2(m, m1, mk.node2(one.value(), n1), mk.node2(n2, one2.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(three.values()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()),
                           m2);
          }
        });
      }
    }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
        final V2<Node<V, A>> v1 = two.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two2) {
            final V2<Node<V, A>> v2 = two2.values();
            return append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, v2._1()), mk.node2(v2._2(), v2._3()),
                           m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
        final V3<Node<V, A>> v1 = three.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node3(v1), mk.node3(n1, n2, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(v1), mk.node2(n1, n2), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three2) {
            final V3<Node<V, A>> v2 = three2.values();
            return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
        final V4<Node<V, A>> v1 = four.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node2(v1._4(), n1), mk.node2(n2, one.value()),
                           m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(three.values()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four2) {
            final V4<Node<V, A>> v2 = four2.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node2(v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    });
  }

  @SuppressWarnings("unchecked")
  private static <V, A> FingerTree<V, Node<V, A>> append3(final Measured<V, A> m, final FingerTree<V, Node<V, A>> t1,
                                                          final Node<V, A> n1, final Node<V, A> n2, final Node<V, A> n3,
                                                          final FingerTree<V, Node<V, A>> t2) {
    final Measured<V, Node<V, A>> nm = m.nodeMeasured();
    return t1.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
        return t2.cons(n3).cons(n2).cons(n1);
      }
    }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
        return t2.cons(n3).cons(n2).cons(n1).cons(single.value());
      }
    }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep) {
        return t2.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
            return deep.snoc(n1).snoc(n2).snoc(n3);
          }
        }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
            return deep.snoc(n1).snoc(n2).snoc(n3).snoc(single.value());
          }
        }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep2) {
            return new Deep<V, Node<V, A>>(nm, nm.monoid().sumLeft(
                list(deep.v, n1.measure(), n2.measure(), n3.measure(), deep2.v)), deep.prefix,
                                           addDigits3(nm, deep.middle, deep.suffix, n1, n2, n3, deep2.prefix,
                                                      deep2.middle), deep2.suffix);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits3(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Node<V, A> n3,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one2) {
            return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, one2.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            final V2<Node<V, A>> v2 = two.values();
            return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
        final V2<Node<V, A>> v1 = two.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append2(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, n3), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, v2._1()), mk.node2(v2._2(), v2._3()),
                           m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, v2._1()),
                           mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(three.values()), mk.node2(n1, n2), mk.node2(n3, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three2) {
            return append3(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node3(three2.values()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append4(m, m1, mk.node3(three.values()), mk.node3(n1, n2, n3), mk.node2(v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
        final V4<Node<V, A>> v1 = four.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node2(n3, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            final V2<Node<V, A>> v2 = two.values();
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(n3, v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2), mk.node2(n3, v2._1()),
                           mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four2) {
            final V4<Node<V, A>> v2 = four2.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(n3, v2._1(), v2._2()), mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
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
    return t1.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
        return t2.cons(n4).cons(n3).cons(n2).cons(n1);
      }
    }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
        return t2.cons(n4).cons(n3).cons(n2).cons(n1).cons(single.value());
      }
    }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
      public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep) {
        return t2.match(new F<Empty<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Empty<V, Node<V, A>> empty) {
            return t1.snoc(n1).snoc(n2).snoc(n3).snoc(n4);
          }
        }, new F<Single<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Single<V, Node<V, A>> single) {
            return t1.snoc(n1).snoc(n2).snoc(n3).snoc(n4).snoc(single.value());
          }
        }, new F<Deep<V, Node<V, A>>, FingerTree<V, Node<V, A>>>() {
          public FingerTree<V, Node<V, A>> f(final Deep<V, Node<V, A>> deep2) {
            return new Deep<V, Node<V, A>>(nm, m.monoid().sumLeft(
                list(deep.v, n1.measure(), n2.measure(), n3.measure(), n4.measure(), deep2.v)), deep.prefix,
                                           addDigits4(nm, deep.middle, deep.suffix, n1, n2, n3, n4, deep2.prefix,
                                                      deep2.middle), deep2.suffix);
          }
        });
      }
    });
  }

  private static <V, A> FingerTree<V, Node<V, Node<V, A>>> addDigits4(final Measured<V, Node<V, A>> m,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m1,
                                                                      final Digit<V, Node<V, A>> suffix,
                                                                      final Node<V, A> n1, final Node<V, A> n2,
                                                                      final Node<V, A> n3, final Node<V, A> n4,
                                                                      final Digit<V, Node<V, A>> prefix,
                                                                      final FingerTree<V, Node<V, Node<V, A>>> m2) {
    final MakeTree<V, Node<V, A>> mk = mkTree(m);
    return suffix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one2) {
            return append2(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, one2.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node2(n3, n4), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, v2._1()), mk.node2(v2._2(), v2._3()),
                           m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append3(m, m1, mk.node3(one.value(), n1, n2), mk.node3(n3, n4, v2._1()),
                           mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
        final V2<Node<V, A>> v1 = two.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node2(n2, n3), mk.node2(n4, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two2) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node2(two2.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node3(three.values()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), n1), mk.node3(n2, n3, n4), mk.node2(v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
        final V3<Node<V, A>> v1 = three.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node2(n4, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            final V2<Node<V, A>> v2 = two.values();
            return append3(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node3(n4, v2._1(), v2._2()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append4(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node2(n4, v2._1()), mk.node2(v2._2(), v2._3()),
                           m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append4(m, m1, mk.node3(v1), mk.node3(n1, n2, n3), mk.node3(n4, v2._1(), v2._2()),
                           mk.node2(v2._3(), v2._4()), m2);
          }
        });
      }
    }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
      public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
        final V4<Node<V, A>> v1 = four.values();
        return prefix.match(new F<One<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final One<V, Node<V, A>> one) {
            return append3(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(n3, n4, one.value()), m2);
          }
        }, new F<Two<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Two<V, Node<V, A>> two) {
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node2(n3, n4), mk.node2(two.values()), m2);
          }
        }, new F<Three<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Three<V, Node<V, A>> three) {
            final V3<Node<V, A>> v2 = three.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(n3, n4, v2._1()), mk.node2(v2._2(), v2._3()), m2);
          }
        }, new F<Four<V, Node<V, A>>, FingerTree<V, Node<V, Node<V, A>>>>() {
          public FingerTree<V, Node<V, Node<V, A>>> f(final Four<V, Node<V, A>> four) {
            final V4<Node<V, A>> v2 = four.values();
            return append4(m, m1, mk.node3(v1._1(), v1._2(), v1._3()), mk.node3(v1._4(), n1, n2),
                           mk.node3(n3, n4, v2._1()), mk.node3(v2._2(), v2._3(), v2._4()), m2);
          }
        });
      }
    });
  }
}
