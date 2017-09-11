package fj;

import fj.data.Array;
import fj.data.Either;
import fj.data.LazyString;
import fj.data.List;
import fj.data.Natural;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Seq;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Tree;
import fj.data.TreeMap;
import fj.data.Validation;
import fj.data.Writer;
import fj.data.hamt.BitSet;
import fj.data.hlist.HList;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;
import fj.data.vector.V5;
import fj.data.vector.V6;
import fj.data.vector.V7;
import fj.data.vector.V8;

import java.math.BigDecimal;
import java.math.BigInteger;

import static fj.Function.compose;
import static fj.Function.constant;
import static fj.Function.curry;

/**
 * Tests for equality between two objects.
 *
 * @version %build.number%
 */
public final class Equal<A> {

  /**
   * Primitives functions of Equal: minimal definition and overridable methods.
   */
  public interface Definition<A> {

    F<A, Boolean> equal(A a);

    default boolean equal(A a1, A a2) {
      return equal(a1).f(a2);
    }
  }

  /**
   * Primitives functions of Equal: alternative minimal definition and overridable methods.
   */
  public interface AltDefinition<A> extends Definition<A> {

    @Override
    boolean equal(A a1, A a2);

    @Override
    default F<A, Boolean> equal(A a) {
      return a2 -> equal(a, a2);
    }
  }

  private final Definition<A> def;

  private Equal(final Definition<A> def) {
    this.def = def;
  }

  /**
   * Returns <code>true</code> if the two given arguments are equal, <code>false</code> otherwise.
   *
   * @param a1 An object to test for equality against another.
   * @param a2 An object to test for equality against another.
   * @return <code>true</code> if the two given arguments are equal, <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return def.equal(a1, a2);
  }

  /**
   * Returns <code>true</code> if the two given arguments are not equal, <code>false</code> otherwise.
   *
   * @param a1 An object to test for inequality against another.
   * @param a2 An object to test for inequality against another.
   * @return <code>true</code> if the two given arguments are not equal, <code>false</code> otherwise.
   */
  public boolean notEq(final A a1, final A a2) {
    return !def.equal(a1, a2);
  }

  /**
   * First-class equality check.
   *
   * @return A function that returns <code>true</code> if the two given arguments are equal.
   */
  public F2<A, A, Boolean> eq() {
    return def::equal;
  }

  /**
   * Partially applied equality check.
   *
   * @param a An object to test for equality against another.
   * @return A function that returns <code>true</code> if the given argument equals the argument to this method.
   */
  public F<A, Boolean> eq(final A a) {
    return def.equal(a);
  }

  /**
   * Maps the given function across this equal as a contra-variant functor.
   *
   * @param f The function to map.
   * @return A new equal.
   */
  public <B> Equal<B> contramap(final F<B, A> f) {
    Definition<A> eaDef = def;
    return equalDef(new Definition<B>(){
      @Override
      public F<B, Boolean> equal(B b) {
        return compose(eaDef.equal(f.f(b)), f);
      }

      @Override
      public boolean equal(B b1, B b2) {
        return eaDef.equal(f.f(b1), f.f(b2));
      }
    });
  }

  /**
   * Constructs an equal instance from the given function.
   *
   * Java 8+ users: use {@link #equalDef(Definition)} instead.
   *
   * @param f The function to construct the equal with.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equal(final F<A, F<A, Boolean>> f) {
    return new Equal<>(f::f);
  }


  /**
   * Constructs an equal instance from the given function.
   *
   * Java 8+ users: use {@link #equalDef(AltDefinition)} instead.
   *
   * @param f The function to construct the equal with.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equal(final F2<A, A, Boolean> f) {
    return equalDef(f::f);
  }

  /**
   * Constructs an equal instance from the given definition.
   *
   * @param definition a definition of the equal instance.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equalDef(final Definition<A> definition) {
    return new Equal<>(definition);
  }

  /**
   * Constructs an equal instance from the given (alternative) definition.
   *
   * @param definition a definition of the equal instance.
   * @return An equal instance from the given function.
   */
  public static <A> Equal<A> equalDef(final AltDefinition<A> definition) {
    return new Equal<>(definition);
  }

  /**
   * Returns an equal instance that uses the {@link Object#equals(Object)} method to test for
   * equality.
   *
   * @return An equal instance that uses the {@link Object#equals(Object)} method to test for
   *         equality.
   */
  public static <A> Equal<A> anyEqual() {
    return equalDef(new Definition<A>() {
      @Override
      public F<A, Boolean> equal(A a) {
        return a::equals;
      }

      @Override
      public boolean equal(A a1, A a2) {
        return a1.equals(a2);
      }
    });
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
   * An equal instance for the <code>Natural</code> type.
   */
  public static final Equal<Natural> naturalEqual = bigintEqual.contramap(Natural::bigIntegerValue);

  /**
   * An equal instance for the {@link String} type.
   */
  public static final Equal<String> stringEqual = anyEqual();

  /**
   * An equal instance for the {@link StringBuffer} type.
   */
  public static final Equal<StringBuffer> stringBufferEqual =
      equalDef((sb1, sb2) -> {
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
      equalDef((sb1, sb2) -> {
        if (sb1.length() == sb2.length()) {
          for (int i = 0; i < sb1.length(); i++)
            if (sb1.charAt(i) != sb2.charAt(i))
              return false;
          return true;
        } else
          return false;
      });

  /**
   * An equal instance for the {@link BitSet} type.
   */
  public static final Equal<BitSet> bitSetSequal = equalDef((bs1, bs2) -> bs1.longValue() == bs2.longValue());

  /**
   * An equal instance for the {@link Either} type.
   *
   * @param ea Equality across the left side of {@link Either}.
   * @param eb Equality across the right side of {@link Either}.
   * @return An equal instance for the {@link Either} type.
   */
  public static <A, B> Equal<Either<A, B>> eitherEqual(final Equal<A> ea, final Equal<B> eb) {
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    return equalDef(e1 -> e1.either(
        a1 -> Either.either_(eaDef.equal(a1), (B __) -> false),
        b1 -> Either.either_((A __)-> false, ebDef.equal(b1))
    ));
  }

  /**
   * An equal instance for the {@link Validation} type.
   *
   * @param ea Equality across the failing side of {@link Validation}.
   * @param eb Equality across the succeeding side of {@link Validation}.
   * @return An equal instance for the {@link Validation} type.
   */
  public static <A, B> Equal<Validation<A, B>> validationEqual(final Equal<A> ea, final Equal<B> eb) {
    return eitherEqual(ea, eb).contramap(Validation.either());
  }

  /**
   * An equal instance for the {@link List} type.
   *
   * @param ea Equality across the elements of the list.
   * @return An equal instance for the {@link List} type.
   */
  public static <A> Equal<List<A>> listEqual(final Equal<A> ea) {
    Definition<A> eaDef = ea.def;
    return equalDef((a1, a2) -> {
      List<A> x1 = a1;
      List<A> x2 = a2;

      while (x1.isNotEmpty() && x2.isNotEmpty()) {
        if (!eaDef.equal(x1.head(), x2.head()))
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
    return listEqual(ea).contramap(NonEmptyList.toList_());
  }

  /**
   * An equal instance for the {@link Option} type.
   *
   * @param ea Equality across the element of the option.
   * @return An equal instance for the {@link Option} type.
   */
  public static <A> Equal<Option<A>> optionEqual(final Equal<A> ea) {
    Definition<A> eaDef = ea.def;
    return equalDef(o1 -> o1.option(
        Option.isNone_(),
        a1 -> Option.option_(false, eaDef.equal(a1))
    ));
  }

  public static <A> Equal<Seq<A>> seqEqual(final Equal<A> e) {
    return streamEqual(e).contramap(Seq::toStream);
  }

  /**
   * An equal instance for the {@link Stream} type.
   *
   * @param ea Equality across the elements of the stream.
   * @return An equal instance for the {@link Stream} type.
   */
  public static <A> Equal<Stream<A>> streamEqual(final Equal<A> ea) {
    Definition<A> eaDef = ea.def;
    return equalDef((a1, a2) -> {
      Stream<A> x1 = a1;
      Stream<A> x2 = a2;

      while (x1.isNotEmpty() && x2.isNotEmpty()) {
        if (!eaDef.equal(x1.head(), x2.head()))
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
    Definition<A> eaDef = ea.def;
    return equalDef((a1, a2) -> {
      if (a1.length() == a2.length()) {
        for (int i = 0; i < a1.length(); i++) {
          if (!eaDef.equal(a1.get(i), a2.get(i)))
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
    Definition<A> eaDef = ea.def;
    return equalDef(new AltDefinition<Tree<A>>() {

      final Definition<P1<Stream<Tree<A>>>> subForestEqDef = p1Equal(streamEqual(equalDef(this))).def;

      @Override
      public boolean equal(Tree<A> t1, Tree<A> t2) {
        return eaDef.equal(t1.root(), t2.root())
            && subForestEqDef.equal(t1.subForest(), t2.subForest());

      }
    });
  }

  /**
   * An equal instance for a product-1.
   *
   * @param ea Equality across the first element of the product.
   * @return An equal instance for a product-1.
   */
  public static <A> Equal<P1<A>> p1Equal(final Equal<A> ea) {
    return ea.contramap(P1.__1());
  }

  /**
   * An equal instance for a product-2.
   *
   * @param ea Equality across the first element of the product.
   * @param eb Equality across the second element of the product.
   * @return An equal instance for a product-2.
   */
  public static <A, B> Equal<P2<A, B>> p2Equal(final Equal<A> ea, final Equal<B> eb) {
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    return equalDef((p1, p2)-> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    return equalDef((p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    Definition<D> edDef = ed.def;
    return equalDef((p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()) &&
        edDef.equal(p1._4(), p2._4()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    Definition<D> edDef = ed.def;
    Definition<E> eeDef = ee.def;
    return equalDef((p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()) &&
        edDef.equal(p1._4(), p2._4()) && eeDef.equal(p1._5(), p2._5()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    Definition<D> edDef = ed.def;
    Definition<E> eeDef = ee.def;
    Definition<F$> efDef = ef.def;
    return equalDef((p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()) &&
        edDef.equal(p1._4(), p2._4()) && eeDef.equal(p1._5(), p2._5()) && efDef.equal(p1._6(), p2._6()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    Definition<D> edDef = ed.def;
    Definition<E> eeDef = ee.def;
    Definition<F$> efDef = ef.def;
    Definition<G> egDef = eg.def;
    return equalDef((p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()) &&
        edDef.equal(p1._4(), p2._4()) && eeDef.equal(p1._5(), p2._5()) && efDef.equal(p1._6(), p2._6()) &&
        egDef.equal(p1._7(), p2._7()));
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
    Definition<A> eaDef = ea.def;
    Definition<B> ebDef = eb.def;
    Definition<C> ecDef = ec.def;
    Definition<D> edDef = ed.def;
    Definition<E> eeDef = ee.def;
    Definition<F$> efDef = ef.def;
    Definition<G> egDef = eg.def;
    Definition<H> ehDef = eh.def;
    return equalDef(
        (p1, p2) -> eaDef.equal(p1._1(), p2._1()) && ebDef.equal(p1._2(), p2._2()) && ecDef.equal(p1._3(), p2._3()) &&
            edDef.equal(p1._4(), p2._4()) && eeDef.equal(p1._5(), p2._5()) && efDef.equal(p1._6(), p2._6()) &&
            egDef.equal(p1._7(), p2._7()) && ehDef.equal(p1._8(), p2._8()));
  }

  /**
   * An equal instance for a vector-2.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-2.
   */
  public static <A> Equal<V2<A>> v2Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V2.toStream_());
  }

  /**
   * An equal instance for a vector-3.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-3.
   */
  public static <A> Equal<V3<A>> v3Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V3.toStream_());
  }

  /**
   * An equal instance for a vector-4.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-4.
   */
  public static <A> Equal<V4<A>> v4Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V4.toStream_());
  }

  /**
   * An equal instance for a vector-5.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-5.
   */
  public static <A> Equal<V5<A>> v5Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V5.toStream_());
  }

  /**
   * An equal instance for a vector-6.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-6.
   */
  public static <A> Equal<V6<A>> v6Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V6.toStream_());
  }

  /**
   * An equal instance for a vector-7.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-7.
   */
  public static <A> Equal<V7<A>> v7Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V7.toStream_());
  }

  /**
   * An equal instance for a vector-8.
   *
   * @param ea Equality across the elements of the vector.
   * @return An equal instance for a vector-8.
   */
  public static <A> Equal<V8<A>> v8Equal(final Equal<A> ea) {
    return streamEqual(ea).contramap(V8.toStream_());
  }

  /**
   * An equal instance for lazy strings.
   */
  public static final Equal<LazyString> eq = streamEqual(charEqual).contramap(LazyString::toStream);

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
    Definition<E> eDef = e.def;
    Definition<L> lDef = l.def;
    return equalDef((c1, c2) -> eDef.equal(c1.head(), c2.head()) && lDef.equal(c1.tail(), c2.tail()));
  }

  /**
   * Equal instance for sets.
   *
   * @param e Equality for the set elements.
   * @return An equal instance for sets.
   */
  public static <A> Equal<Set<A>> setEqual(final Equal<A> e) {
    return streamEqual(e).contramap(Set::toStream);
  }

  public static <K, V> Equal<TreeMap<K, V>> treeMapEqual(Equal<K> k, Equal<V> v) {
    return streamEqual(p2Equal(k, v)).contramap(TreeMap::toStream);
  }

  public static <A, B> Equal<Writer<A, B>> writerEqual(Equal<A> eq1, Equal<B> eq2) {
    return p2Equal(eq1, eq2).contramap(Writer::run);
  }
  
  /**
   * Helper method to implement {@link Object#equals(Object)} correctly. DO NOT USE it for any other purpose.
   *
   * @param clazz the class in which the {@link Object#equals(Object)} is implemented
   * @param self a reference to 'this'
   * @param other the other object of the comparison
   * @param equal an equal instance for the type of self (that use {@link #anyEqual()} if generic type).
   * @return true if self and other are equal
   */
  @SuppressWarnings("unchecked")
  public static <A> boolean equals0(final java.lang.Class<? super A> clazz, final A self, final Object other, final Equal<A> equal) {
    return self == other || clazz.isInstance(other) && equal.eq(self, (A) other);
  }
  
  /**
   * Helper method to implement {@link Object#equals(Object)} correctly. DO NOT USE it for any other purpose.
   *
   * @param clazz the class in which the {@link Object#equals(Object)} is implemented
   * @param self a reference to 'this'
   * @param other the other object of the comparison
   * @param equal a lazy equal instance for the type (that use {@link #anyEqual()} if generic type)..
   * @return true if self and other are equal
   */
  @SuppressWarnings("unchecked")
  public static <A> boolean equals0(final java.lang.Class<? super A> clazz, final A self, final Object other, final F0<Equal<A>> equal) {
    return self == other || clazz.isInstance(other) && equal.f().eq(self, (A) other);
  }

}
