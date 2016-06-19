package fj.data.fingertrees;

import fj.*;
import fj.data.Option;
import fj.data.Stream;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;

import static fj.data.fingertrees.FingerTree.mkTree;

/**
 * A digit is a vector of 1-4 elements. Serves as a pointer to the prefix or suffix of a finger tree.
 */
public abstract class Digit<V, A> {
  /**
   * Folds this digit to the right using the given function and the given initial value.
   *
   * @param f A function with which to fold this digit.
   * @param z An initial value to apply at the rightmost end of the fold.
   * @return The right reduction of this digit with the given function and the given initial value.
   */
  public abstract <B> B foldRight(final F<A, F<B, B>> f, final B z);

  /**
   * Folds this digit to the left using the given function and the given initial value.
   *
   * @param f A function with which to fold this digit.
   * @param z An initial value to apply at the leftmost end of the fold.
   * @return The left reduction of this digit with the given function and the given initial value.
   */
  public abstract <B> B foldLeft(final F<B, F<A, B>> f, final B z);

  /**
   * Folds this digit to the right using the given function.
   *
   * @param f A function with which to fold this digit.
   * @return The right reduction of this digit with the given function.
   */
    public final A reduceRight(final F<A, F<A, A>> f) {
        return match(
            One::value,
            two -> {
                final V2<A> v = two.values();
                return f.f(v._1()).f(v._2());
            },
            three -> {
                final V3<A> v = three.values();
                return f.f(v._1()).f(f.f(v._2()).f(v._3()));
            },
            four -> {
                final V4<A> v = four.values();
                return f.f(v._1()).f(f.f(v._2()).f(f.f(v._3()).f(v._4())));
            }
        );
    }

  /**
   * Folds this digit to the right using the given function.
   *
   * @param f A function with which to fold this digit.
   * @return The right reduction of this digit with the given function.
   */
  public final A reduceLeft(final F<A, F<A, A>> f) {
    return match(
        One::value,
        two -> {
            final V2<A> v = two.values();
            return f.f(v._1()).f(v._2());
        },
        three -> {
            final V3<A> v = three.values();
            return f.f(f.f(v._1()).f(v._2())).f(v._3());
        },
        four -> {
            final V4<A> v = four.values();
            return f.f(f.f(f.f(v._1()).f(v._2())).f(v._3())).f(v._4());
        }
    );
  }

  /**
   * Maps a function across the elements of this digit, measuring with the given measurement.
   *
   * @param f A function to map across the elements of this digit.
   * @param m A measuring for the function's domain (destination type).
   * @return A new digit with the same structure as this digit, but with all elements transformed
   *         with the given function and measured with the given measuring.
   */
  public final <B> Digit<V, B> map(final F<A, B> f, final Measured<V, B> m) {
    return match(
        one -> new One<>(m, f.f(one.value())),
        two -> new Two<>(m, two.values().map(f)),
        three -> new Three<>(m, three.values().map(f)),
        four -> new Four<>(m, four.values().map(f))
    );
  }

  /**
   * Structural pattern matching on digits. Applies the function that matches the structure of this digit.
   *
   * @param one   A function to apply to this digit if it's One.
   * @param two   A function to apply to this digit if it's Two.
   * @param three A function to apply to this digit if it's Three.
   * @param four  A function to apply to this digit if it's Four.
   * @return The result of applying the function matching this Digit.
   */
  public abstract <B> B match(final F<One<V, A>, B> one, final F<Two<V, A>, B> two, final F<Three<V, A>, B> three,
                              final F<Four<V, A>, B> four);

  private final Measured<V, A> m;

  Digit(final Measured<V, A> m) {
    this.m = m;
  }

  final Measured<V, A> measured() { return m; }

  /**
   * Returns the sum of the measurements of this digit according to the monoid.
   *
   * @return the sum of the measurements of this digit according to the monoid.
   */
  public final V measure() {
    return foldLeft(Function.curry((v, a) -> m.sum(v, m.measure(a))), m.zero());
  }

  /**
   * Returns the tree representation of this digit.
   * @return the tree representation of this digit. 
   */
    public final FingerTree<V, A> toTree() {
        final MakeTree<V, A> mk = mkTree(m);
        return match(
            one -> mk.single(one.value()),
            two -> mk.deep(mk.one(two.values()._1()), new Empty<>(m.nodeMeasured()), mk.one(two.values()._2())),
            three -> mk.deep(mk.two(three.values()._1(), three.values()._2()), new Empty<>(m.nodeMeasured()), mk.one(three.values()._3())),
            four -> mk.deep(mk.two(four.values()._1(), four.values()._2()), new Empty<>(m.nodeMeasured()), mk.two(four.values()._3(), four.values()._4()))
        );
    }

  final Option<Digit<V, A>> tail() {
    return match(
      one -> Option.none(),
      two -> Option.some(mkTree(m).one(two.values()._2())),
      three -> Option.some(mkTree(m).two(three.values()._2(), three.values()._3())),
      four -> Option.some(mkTree(m).three(four.values()._2(), four.values()._3(), four.values()._4()))
    );
  }

  final Option<Digit<V, A>> init() {
    return match(
      one -> Option.none(),
      two -> Option.some(mkTree(m).one(two.values()._1())),
      three -> Option.some(mkTree(m).two(three.values()._1(), three.values()._2())),
      four -> Option.some(mkTree(m).three(four.values()._1(), four.values()._2(), four.values()._3()))
    );
  }

  abstract P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> split1(final F<V, Boolean> predicate, final V acc);

  public abstract P2<Integer, A> lookup(final F<V, Integer> o, final int i);

    public abstract int length();

    public String toString() {
        return Show.digitShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
    }

    public abstract Stream<A> toStream();


}
