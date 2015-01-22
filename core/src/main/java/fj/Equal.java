package fj;

import static fj.Function.curry;

import fj.data.*;
import fj.data.hlist.HList;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;
import fj.data.vector.V5;
import fj.data.vector.V6;
import fj.data.vector.V7;
import fj.data.vector.V8;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * Tests for equality between two objects.
 *
 * @version %build.number%
 */
public final class Equal<A> {
  private final F<A, F<A, Boolean>> f;

  private Equal(final F<A, F<A, Boolean>> f) {
    this.f = f;
  }

  /**
   * Returns <code>true</code> if the two given arguments are equal, <code>false</code> otherwise.
   *
   * @param a1 An object to test for equality against another.
   * @param a2 An object to test for equality against another.
   * @return <code>true</code> if the two given arguments are equal, <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return f.f(a1).f(a2);
  }

  /**
   * First-class equality check.
   *
   * @return A function that returns <code>true</code> if the two given arguments are equal.
   */
  public F2<A, A, Boolean> eq() {
    return (a, a1) -> eq(a, a1);
  }

  /**
   * Partially applied equality check.
   *
   * @param a An object to test for equality against another.
   * @return A function that returns <code>true</code> if the given argument equals the argument to this method.
   */
  public F<A, Boolean> eq(final A a) {
    return a1 -> eq(a, a1);
  }

  /**
   * Maps the given function across this equal as a contra-variant functor.
   *
   * @param f The function to map.
   * @return A new equal.
   */
  public <B> Equal<B> comap(final F<B, A> f) {
    return equal(F1Functions.o(F1Functions.o(F1Functions.<B, A, Boolean>andThen(f), this.f), f));
  }

  /**
   * Constructs an equal instance from the given function.
   *
   * @param f The function to construct the equal with.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equal(final F<A, F<A, Boolean>> f) {
    return new Equal<A>(f);
  }

  /**
   * Returns an equal instance that uses the {@link Object#equals(Object)} method to test for
   * equality.
   *
   * @return An equal instance that uses the {@link Object#equals(Object)} method to test for
   *         equality.
   */
  public static <A> Equal<A> anyEqual() {
    return equal(a1 -> a2 -> a1.equals(a2));
  }

  /**
   * An equal instance for the <code>boolean</code> type.
   */
  public static final Equal<Boolean> booleanEqual = anyEqual();

  /**
   * An equal instance for the <code>byte</code> type.
   */
  public static final Equal<Byte> byteEqual = anyEqual();

  /**
   * An equal instance for the <code>char</code> type.
   */
  public static final Equal<Character> charEqual = anyEqual();

  /**
   * An equal instance for the <code>double</code> type.
   */
  public static final Equal<Double> doubleEqual = anyEqual();

  /**
   * An equal instance for the <code>float</code> type.
   */
  public static final Equal<Float> floatEqual = anyEqual();

  /**
   * An equal instance for the <code>int</code> type.
   */
  public static final Equal<Integer> intEqual = anyEqual();

  /**
   * An equal instance for the <code>BigInteger</code> type.
   */
  public static final Equal<BigInteger> bigintEqual = anyEqual();

  /**
   * An equal instance for the <code>BigDecimal</code> type.
   */
  public static final Equal<BigDecimal> bigdecimalEqual = anyEqual();

  /**
   * An equal instance for the <code>long</code> type.
   */
  public static final Equal<Long> longEqual = anyEqual();

  /**
   * An equal instance for the <code>short</code> type.
   */
  public static final Equal<Short> shortEqual = anyEqual();

  /**
   * An equal instance for the {@link String} type.
   */
  public static final Equal<String> stringEqual = anyEqual();

  /**
   * An equal instance for the {@link StringBuffer} type.
   */
  public static final Equal<StringBuffer> stringBufferEqual =
      equal(sb1 -> sb2 -> {
        if (sb1.length() == sb2.length()) {
          for (int i = 0; i < sb1.length(); i++)
            if (sb1.charAt(i) != sb2.charAt(i))
              return false;
          return true;
        } else
          return false;
      });

  /**
   * An equal instance for the {@link StringBuilder} type.
   */
  public static final Equal<StringBuilder> stringBuilderEqual =
      equal(sb1 -> sb2 -> {
        if (sb1.length() == sb2.length()) {
          for (int i = 0; i < sb1.length(); i++)
            if (sb1.charAt(i) != sb2.charAt(i))
              return false;
          return true;
        } else
          return false;
      });

  /**
   * An equal instance for the {@link Either} type.
   *
   * @param ea Equality across the left side of {@link Either}.
   * @param eb Equality across the right side of {@link Either}.
   * @return An equal instance for the {@link Either} type.
   */
  public static <A, B> Equal<Either<A, B>> eitherEqual(final Equal<A> ea, final Equal<B> eb) {
    return equal(e1 -> e2 -> e1.isLeft() && e2.isLeft() && ea.f.f(e1.left().value()).f(e2.left().value()) ||
           e1.isRight() && e2.isRight() && eb.f.f(e1.right().value()).f(e2.right().value()));
  }

  /**
   * An equal instance for the {@link Validation} type.
   *
   * @param ea Equality across the failing side of {@link Validation}.
   * @param eb Equality across the succeeding side of {@link Validation}.
   * @return An equal instance for the {@link Validation} type.
   */
  public static <A, B> Equal<Validation<A, B>> validationEqual(final Equal<A> ea, final Equal<B> eb) {
    return eitherEqual(ea, eb).comap(Validation.<A, B>either());
  }

  /**
   * An equal instance for the {@link List} type.
   *
   * @param ea Equality across the elements of the list.
   * @return An equal instance for the {@link List} type.
   */
  public static <A> Equal<List<A>> listEqual(final Equal<A> ea) {
    return equal(a1 -> a2 -> {
      List<A> x1 = a1;
      List<A> x2 = a2;

      while (x1.isNotEmpty() && x2.isNotEmpty()) {
        if (!ea.eq(x1.head(), x2.head()))
          return false;

        x1 = x1.tail();
        x2 = x2.tail();
      }

      return x1.isEmpty() && x2.isEmpty();
    });
  }

  /**
   * An equal instance for the {@link NonEmptyList} type.
   *
   * @param ea Equality across the elements of the non-empty list.
   * @return An equal instance for the {@link NonEmptyList} type.
   */
  public static <A> Equal<NonEmptyList<A>> nonEmptyListEqual(final Equal<A> ea) {
    return listEqual(ea).comap(NonEmptyList.<A>toList_());
  }

  /**
   * An equal instance for the {@link Option} type.
   *
   * @param ea Equality across the element of the option.
   * @return An equal instance for the {@link Option} type.
   */
  public static <A> Equal<Option<A>> optionEqual(final Equal<A> ea) {
    return equal(o1 -> o2 -> o1.isNone() && o2.isNone() ||
           o1.isSome() && o2.isSome() && ea.f.f(o1.some()).f(o2.some()));
  }

  public static <A> Equal<Seq<A>> seqEqual(final Equal<A> e) {
    return equal(s1 -> s2 -> streamEqual(e).eq(s1.toStream(), s2.toStream()));
  }

  /**
   * An equal instance for the {@link Stream} type.
   *
   * @param ea Equality across the elements of the stream.
   * @return An equal instance for the {@link Stream} type.
   */
  public static <A> Equal<Stream<A>> streamEqual(final Equal<A> ea) {
    return equal(a1 -> a2 -> {
      Stream<A> x1 = a1;
      Stream<A> x2 = a2;

      while (x1.isNotEmpty() && x2.isNotEmpty()) {
        if (!ea.eq(x1.head(), x2.head()))
          return false;

        x1 = x1.tail()._1();
        x2 = x2.tail()._1();
      }

      return x1.isEmpty() && x2.isEmpty();
    });
  }

  /**
   * An equal instance for the {@link Array} type.
   *
   * @param ea Equality across the elements of the array.
   * @return An equal instance for the {@link Array} type.
   */
  public static <A> Equal<Array<A>> arrayEqual(final Equal<A> ea) {
    return equal(a1 -> a2 -> {
      if (a1.length() == a2.length()) {
        for (int i = 0; i < a1.length(); i++) {
          if (!ea.eq(a1.get(i), a2.get(i)))
            return false;
        }
        return true;
      } else
        return false;
    });
  }

  /**
   * An equal instance for the {@link Tree} type.
   *
   * @param ea Equality across the elements of the tree.
   * @return An equal instance for the {@link Tree} type.
   */
  public static <A> Equal<Tree<A>> treeEqual(final Equal<A> ea) {
    return Equal.<Tree<A>>equal(curry((t1, t2) -> ea.eq(t1.root(), t2.root()) && p1Equal(streamEqual(Equal.<A>treeEqual(ea))).eq(t2.subForest(), t1.subForest())));
  }

  /**
   * An equal instance for a product-1.
   *
   * @param ea Equality across the first element of the product.
   * @return An equal instance for a product-1.
   */
  public static <A> Equal<P1<A>> p1Equal(final Equal<A> ea) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()));
  }

  /**
   * An equal instance for a product-2.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @return An equal instance for a product-2.
   */
  public static <A, B> Equal<P2<A, B>> p2Equal(final Equal<A> ea, final Equal<B> eb) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()));
  }

  /**
   * An equal instance for a product-3.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @return An equal instance for a product-3.
   */
  public static <A, B, C> Equal<P3<A, B, C>> p3Equal(final Equal<A> ea, final Equal<B> eb, final Equal<C> ec) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()));
  }

  /**
   * An equal instance for a product-4.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @param ed Equality across the fourth element of the product.
   * @return An equal instance for a product-4.
   */
  public static <A, B, C, D> Equal<P4<A, B, C, D>> p4Equal(final Equal<A> ea, final Equal<B> eb, final Equal<C> ec,
                                                           final Equal<D> ed) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()) &&
           ed.eq(p1._4(), p2._4()));
  }

  /**
   * An equal instance for a product-5.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @param ed Equality across the fourth element of the product.
   * @param ee Equality across the fifth element of the product.
   * @return An equal instance for a product-5.
   */
  public static <A, B, C, D, E> Equal<P5<A, B, C, D, E>> p5Equal(final Equal<A> ea, final Equal<B> eb,
                                                                 final Equal<C> ec, final Equal<D> ed,
                                                                 final Equal<E> ee) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()) &&
           ed.eq(p1._4(), p2._4()) && ee.eq(p1._5(), p2._5()));
  }

  /**
   * An equal instance for a product-6.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @param ed Equality across the fourth element of the product.
   * @param ee Equality across the fifth element of the product.
   * @param ef Equality across the sixth element of the product.
   * @return An equal instance for a product-6.
   */
  public static <A, B, C, D, E, F$> Equal<P6<A, B, C, D, E, F$>> p6Equal(final Equal<A> ea, final Equal<B> eb,
                                                                         final Equal<C> ec, final Equal<D> ed,
                                                                         final Equal<E> ee, final Equal<F$> ef) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()) &&
           ed.eq(p1._4(), p2._4()) && ee.eq(p1._5(), p2._5()) && ef.eq(p1._6(), p2._6()));
  }

  /**
   * An equal instance for a product-7.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @param ed Equality across the fourth element of the product.
   * @param ee Equality across the fifth element of the product.
   * @param ef Equality across the sixth element of the product.
   * @param eg Equality across the seventh element of the product.
   * @return An equal instance for a product-7.
   */
  public static <A, B, C, D, E, F$, G> Equal<P7<A, B, C, D, E, F$, G>> p7Equal(final Equal<A> ea, final Equal<B> eb,
                                                                               final Equal<C> ec, final Equal<D> ed,
                                                                               final Equal<E> ee, final Equal<F$> ef,
                                                                               final Equal<G> eg) {
    return equal(p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()) &&
           ed.eq(p1._4(), p2._4()) && ee.eq(p1._5(), p2._5()) && ef.eq(p1._6(), p2._6()) &&
           eg.eq(p1._7(), p2._7()));
  }

  /**
   * An equal instance for a product-8.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @param ec Equality across the third element of the product.
   * @param ed Equality across the fourth element of the product.
   * @param ee Equality across the fifth element of the product.
   * @param ef Equality across the sixth element of the product.
   * @param eg Equality across the seventh element of the product.
   * @param eh Equality across the eighth element of the product.
   * @return An equal instance for a product-8.
   */
  public static <A, B, C, D, E, F$, G, H> Equal<P8<A, B, C, D, E, F$, G, H>> p8Equal(final Equal<A> ea,
                                                                                     final Equal<B> eb,
                                                                                     final Equal<C> ec,
                                                                                     final Equal<D> ed,
                                                                                     final Equal<E> ee,
                                                                                     final Equal<F$> ef,
                                                                                     final Equal<G> eg,
                                                                                     final Equal<H> eh) {
    return equal(
            p1 -> p2 -> ea.eq(p1._1(), p2._1()) && eb.eq(p1._2(), p2._2()) && ec.eq(p1._3(), p2._3()) &&
                   ed.eq(p1._4(), p2._4()) && ee.eq(p1._5(), p2._5()) && ef.eq(p1._6(), p2._6()) &&
                   eg.eq(p1._7(), p2._7()) && eh.eq(p1._8(), p2._8()));
  }

  /**
   * An equal instance for a vector-2.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-2.
   */
  public static <A> Equal<V2<A>> v2Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V2.<A>toStream_());
  }

  /**
   * An equal instance for a vector-3.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-3.
   */
  public static <A> Equal<V3<A>> v3Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V3.<A>toStream_());
  }

  /**
   * An equal instance for a vector-4.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-4.
   */
  public static <A> Equal<V4<A>> v4Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V4.<A>toStream_());
  }

  /**
   * An equal instance for a vector-5.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-5.
   */
  public static <A> Equal<V5<A>> v5Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V5.<A>toStream_());
  }

  /**
   * An equal instance for a vector-6.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-6.
   */
  public static <A> Equal<V6<A>> v6Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V6.<A>toStream_());
  }

  /**
   * An equal instance for a vector-7.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-7.
   */
  public static <A> Equal<V7<A>> v7Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V7.<A>toStream_());
  }

  /**
   * An equal instance for a vector-8.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-8.
   */
  public static <A> Equal<V8<A>> v8Equal(final Equal<A> ea) {
    return streamEqual(ea).comap(V8.<A>toStream_());
  }

  /**
   * An equal instance for lazy strings.
   */
  public static final Equal<LazyString> eq = streamEqual(charEqual).comap(LazyString.toStream);

  /**
   * An equal instance for the empty heterogeneous list.
   */
  public static final Equal<HList.HNil> hListEqual = anyEqual();

  /**
   * An equal instance for heterogeneous lists.
   *
   * @param e Equality for the first element of the list.
   * @param l Equality for the rest of the list.
   * @return an equal instance for a heterogeneous list.
   */
  public static <E, L extends HList<L>> Equal<HList.HCons<E, L>> hListEqual(final Equal<E> e, final Equal<L> l) {
    return equal(curry((HList.HCons<E, L> c1, HList.HCons<E, L> c2) -> e.eq(c1.head(), c2.head()) && l.eq(c1.tail(), c2.tail())));
  }

  /**
   * Equal instance for sets.
   *
   * @param e Equality for the set elements.
   * @return An equal instance for sets.
   */
  public static <A> Equal<Set<A>> setEqual(final Equal<A> e) {
    return equal(curry((Set<A> a, Set<A> b) -> streamEqual(e).eq(a.toStream(), b.toStream())));
  }

  public static <K, V> Equal<TreeMap<K, V>> treeMapEqual(Equal<K> k, Equal<V> v) {
    return equal(t1 -> t2 -> Equal.streamEqual(p2Equal(k, v)).eq(t1.toStream(), t2.toStream()));
  }

  public static <A, B> Equal<Writer<A, B>> writerEqual(Equal<A> eq1, Equal<B> eq2) {
    return equal(w1 -> w2 -> p2Equal(eq1, eq2).eq(w1.run(), w2.run()));
  }

  /**
   * @return Returns none if no equality can be determined by checking the nullity and reference values, else the equality
   */
  public static Option<Boolean> shallowEqualsO(Object o1, Object o2) {
    if (o1 == null && o2 == null) {
      return Option.some(true);
    } else if (o1 == o2) {
      return Option.some(true);
    } else if (o1 != null && o2 != null) {
      java.lang.Class<?> c = o1.getClass();
      return c.isInstance(o2) ? Option.none() : Option.some(false);
    } else {
      return Option.some(false);
    }
  }

}
