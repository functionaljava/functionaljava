package fj;

import fj.data.Array;
import fj.data.Either;
import fj.data.List;
import fj.data.Natural;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;
import fj.data.Validation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import static fj.Function.apply;
import static fj.Function.compose;
import static fj.Function.curry;
import static fj.Semigroup.semigroup;
import static fj.Semigroup.semigroupDef;

/**
 * Tests for ordering between two objects.
 *
 * @version %build.number%
 */
public final class Ord<A> {

  /**
   * Primitives functions of Ord: minimal definition and overridable methods.
   */
  public interface Definition<A> extends Equal.Definition<A>, Semigroup.Definition<A> {

    F<A, Ordering> compare(A a);

    default Ordering compare(A a1, A a2) {
      return compare(a1).f(a2);
    }

    // equal:
    @Override
    default boolean equal(A a1, A a2) {
      return compare(a1, a2) == Ordering.EQ;
    }

    @Override
    default F<A, Boolean> equal(A a) {
      return compose(o -> o == Ordering.EQ, compare(a));
    }

    // max semigroup:
    @Override
    default A append(A a1, A a2) {
      return compare(a1, a2) == Ordering.GT ? a1 : a2;
    }

    @Override
    default A multiply1p(int n, A a) {
      return a;
    }

    @Override
    default F<A, A> prepend(A a1) {
      return apply((a2, o) -> o == Ordering.GT ? a1 : a2, compare(a1));
    }

    @Override
    default Definition<A> dual() {
      return new Definition<A>() {
        @Override
        public F<A, Ordering> compare(A a) {
          return compose(Ordering::reverse, Definition.this.compare(a));
        }

        @Override
        public Ordering compare(A a1, A a2) {
          return Definition.this.compare(a2, a1);
        }

        @Override
        public Definition<A> dual() {
          return Definition.this;
        }
      };
    }
  }

  /**
   * Primitives functions of Ord: alternative minimal definition and overridable methods.
   */
  public interface AltDefinition<A> extends Definition<A> {

    Ordering compare(A a1, A a2);

    default F<A, Ordering> compare(A a1) {
      return a2 -> compare(a1, a2);
    }

    @Override
    default F<A, A> prepend(A a1) {
      return a2 -> append(a1, a2);
    }

  }


  private final Definition<A> def;

  private Ord(final Definition<A> def) {
    this.def = def;
    this.max = a1 -> apply((a2, o) -> o == Ordering.GT ? a1 : a2, def.compare(a1));
    this.min = a1 -> apply((a2, o) -> o == Ordering.LT ? a1 : a2, def.compare(a1));
  }

  /**
   * First-class ordering.
   *
   * @return A function that returns an ordering for its arguments.
   */
  public F<A, F<A, Ordering>> compare() {
    return def::compare;
  }

  /**
   * Returns an ordering for the given arguments.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return An ordering for the given arguments.
   */
  public Ordering compare(final A a1, final A a2) {
    return def.compare(a1, a2);
  }

  /**
   * Returns <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for equality to another.
   * @param a2 An instance to compare for equality to another.
   * @return <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return def.compare(a1, a2) == Ordering.EQ;
  }

  /**
   * Returns an <code>Equal</code> for this order.
   *
   * @return An <code>Equal</code> for this order.
   */
  public Equal<A> equal() {
    return Equal.equalDef(def);
  }

  /**
   * Maps the given function across this ord as a contra-variant functor.
   *
   * @param f The function to map.
   * @return A new ord.
   */
  public <B> Ord<B> contramap(final F<B, A> f) {
    Definition<A> selfDef = def;
    return ordDef(new Definition<B>() {
      @Override
      public F<B, Ordering> compare(B b) {
        return compose(selfDef.compare(f.f(b)), f);
      }

      @Override
      public Ordering compare(B b1, B b2) {
        return selfDef.compare(f.f(b1), f.f(b2));
      }
    });
  }

  /**
   * Returns <code>true</code> if the first given argument is less than the second given argument,
   * <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is less than the second given argument,
   *         <code>false</code> otherwise.
   */
  public boolean isLessThan(final A a1, final A a2) {
    return def.compare(a1, a2) == Ordering.LT;
  }

    /**
     * Returns <code>true</code> if the first given argument is less than or equal to the second given argument,
     * <code>false</code> otherwise.
     *
     * @param a1 An instance to compare for ordering to another.
     * @param a2 An instance to compare for ordering to another.
     * @return <code>true</code> if the first given argument is less than or equal to the second given argument,
     *         <code>false</code> otherwise.
     */
    public boolean isLessThanOrEqualTo(final A a1, final A a2) {
        return def.compare(a1, a2) != Ordering.GT;
    }

  /**
   * Returns <code>true</code> if the first given argument is greater than the second given
   * argument, <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is greater than the second given
   *         argument, <code>false</code> otherwise.
   */
  public boolean isGreaterThan(final A a1, final A a2) {
    return def.compare(a1, a2) == Ordering.GT;
  }

  /**
   * Returns a function that returns true if its argument is less than the argument to this method.
   *
   * @param a A value to compare against.
   * @return A function that returns true if its argument is less than the argument to this method.
   */
  public F<A, Boolean> isLessThan(final A a) {
    return compose(o -> o == Ordering.GT, def.compare(a));
  }

  /**
   * Returns a function that returns true if its argument is greater than than the argument to this method.
   *
   * @param a A value to compare against.
   * @return A function that returns true if its argument is greater than the argument to this method.
   */
  public F<A, Boolean> isGreaterThan(final A a) {
    return compose(o -> o == Ordering.LT, def.compare(a));
  }

  /**
   * Returns the greater of its two arguments.
   *
   * @param a1 A value to compare with another.
   * @param a2 A value to compare with another.
   * @return The greater of the two values.
   */
  public A max(final A a1, final A a2) {
    return def.append(a1, a2);
  }


  /**
   * Returns the lesser of its two arguments.
   *
   * @param a1 A value to compare with another.
   * @param a2 A value to compare with another.
   * @return The lesser of the two values.
   */
  public A min(final A a1, final A a2) {
    return isLessThan(a1, a2) ? a1 : a2;
  }

  /**
   * A function that returns the greater of its two arguments.
   *
   */
  public final F<A, F<A, A>> max;

  /**
   * A function that returns the lesser of its two arguments.
   */
  public final F<A, F<A, A>> min;

  public final Semigroup<A> minSemigroup() {
      return semigroupDef(def.dual());
  }

  public final Monoid<A> minMonoid(A zero) {
    return Monoid.monoidDef(def.dual(), zero);
  }

  public final Semigroup<A> maxSemigroup() {
      return semigroupDef(def);
  }

  public final Monoid<A> maxMonoid(A zero) {
    return Monoid.monoidDef(def, zero);
  }

  public final Ord<A> reverse() {
    return ordDef(def.dual());
  }

  /**
   * Returns an order instance that uses the given equality test and ordering function.
   *
   * Java 8+ users: use {@link #ordDef(Definition)} instead.
   *
   * @param f The order function.
   * @return An order instance.
   */
  public static <A> Ord<A> ord(final F<A, F<A, Ordering>> f) {
    return new Ord<>(f::f);
  }

  /**
   * Returns an order instance that uses the given equality test and ordering function.
   *
   * Java 8+ users: use {@link #ordDef(AltDefinition)} instead.
   *
   * @param f The order function.
   * @return An order instance.
   */
  public static <A> Ord<A> ord(final F2<A, A, Ordering> f) {
    return ordDef(f::f);
  }

  /**
   * Returns an order instance that uses the given minimal equality test and ordering definition.
   *
   * @param def The order definition.
   * @return An order instance.
   */
  public static <A> Ord<A> ordDef(final Definition<A> def) {
    return new Ord<>(def);
  }

  /**
   * Returns an order instance that uses the given minimal equality test and ordering definition.
   *
   * @param def The order definition.
   * @return An order instance.
   */
  public static <A> Ord<A> ordDef(final AltDefinition<A> def) {
    return new Ord<>(def);
  }


  /**
   * An order instance for the <code>boolean</code> type.
   */
  public static final Ord<Boolean> booleanOrd = comparableOrd();

  /**
   * An order instance for the <code>byte</code> type.
   */
  public static final Ord<Byte> byteOrd = comparableOrd();

  /**
   * An order instance for the <code>char</code> type.
   */
  public static final Ord<Character> charOrd = comparableOrd();

  /**
   * An order instance for the <code>double</code> type.
   */
  public static final Ord<Double> doubleOrd = comparableOrd();

  /**
   * An order instance for the <code>float</code> type.
   */
  public static final Ord<Float> floatOrd = comparableOrd();

  /**
   * An order instance for the <code>int</code> type.
   */
  public static final Ord<Integer> intOrd = comparableOrd();

  /**
   * An order instance for the <code>BigInteger</code> type.
   */
  public static final Ord<BigInteger> bigintOrd = comparableOrd();

  /**
   * An order instance for the <code>BigDecimal</code> type.
   */
  public static final Ord<BigDecimal> bigdecimalOrd = comparableOrd();

  /**
   * An order instance for the <code>long</code> type.
   */
  public static final Ord<Long> longOrd = comparableOrd();

  /**
   * An order instance for the <code>short</code> type.
   */
  public static final Ord<Short> shortOrd = comparableOrd();

  /**
   * An order instance for the {@link Ordering} type.
   */
  public static final Ord<Ordering> orderingOrd = ordDef((o1, o2) -> o1 == o2 ?
         Ordering.EQ :
         o1 == Ordering.LT ?
         Ordering.LT :
         o2 == Ordering.LT ?
         Ordering.GT :
         o1 == Ordering.EQ ?
         Ordering.LT :
         Ordering.GT);

  /**
   * An order instance for the {@link String} type.
   */
  public static final Ord<String> stringOrd = comparableOrd();

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Ord<StringBuffer> stringBufferOrd = stringOrd.contramap(StringBuffer::toString);

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Ord<StringBuilder> stringBuilderOrd = stringOrd.contramap(StringBuilder::toString);

  /**
   * An order instance for the {@link Option} type.
   *
   * @param oa Order across the element of the option.
   * @return An order instance for the {@link Option} type.
   */
  public static <A> Ord<Option<A>> optionOrd(final Ord<A> oa) {
    Definition<A> oaDef = oa.def;
    return ordDef((o1, o2) -> o1.isNone() ?
            o2.isNone() ?
                    Ordering.EQ :
                    Ordering.LT :
            o2.isNone() ?
                    Ordering.GT :
                oaDef.compare(o1.some()).f(o2.some()));
  }

  /**
   * An order instance for the {@link Either} type.
   *
   * @param oa Order across the left side of {@link Either}.
   * @param ob Order across the right side of {@link Either}.
   * @return An order instance for the {@link Either} type.
   */
  public static <A, B> Ord<Either<A, B>> eitherOrd(final Ord<A> oa, final Ord<B> ob) {
    Definition<A> oaDef = oa.def;
    Definition<B> obDef = ob.def;
    return ordDef((e1, e2) -> e1.isLeft() ?
            e2.isLeft() ?
                oaDef.compare(e1.left().value()).f(e2.left().value()) :
                Ordering.LT :
            e2.isLeft() ?
                Ordering.GT :
                obDef.compare(e1.right().value()).f(e2.right().value()));
  }

  /**
   * An order instance for the {@link Validation} type.
   *
   * @param oa Order across the failing side of {@link Validation}.
   * @param ob Order across the succeeding side of {@link Validation}.
   * @return An order instance for the {@link Validation} type.
   */
  public static <A, B> Ord<Validation<A, B>> validationOrd(final Ord<A> oa, final Ord<B> ob) {
    return eitherOrd(oa, ob).contramap(Validation.either());
  }

  /**
   * An order instance for the {@link List} type.
   *
   * @param oa Order across the elements of the list.
   * @return An order instance for the {@link List} type.
   */
  public static <A> Ord<List<A>> listOrd(final Ord<A> oa) {
    return ordDef((l1, l2) -> {
      List<A> x1 = l1;
      List<A> x2 = l2;

      while (x1.isNotEmpty() && x2.isNotEmpty()) {
        final Ordering o = oa.compare(x1.head(), x2.head());
        if (o == Ordering.LT || o == Ordering.GT) {
          return o;
        }
        x1 = x1.tail();
        x2 = x2.tail();
      }

      if (x1.isEmpty() && x2.isEmpty()) {
        return Ordering.EQ;
      } else if (x1.isEmpty()) {
        return Ordering.LT;
      } else {
        return Ordering.GT;
      }
    });
  }

  /**
   * An order instance for the {@link NonEmptyList} type.
   *
   * @param oa Order across the elements of the non-empty list.
   * @return An order instance for the {@link NonEmptyList} type.
   */
  public static <A> Ord<NonEmptyList<A>> nonEmptyListOrd(final Ord<A> oa) {
    return listOrd(oa).contramap(NonEmptyList.toList_());
  }

  /**
   * An order instance for the {@link Stream} type.
   *
   * @param oa Order across the elements of the stream.
   * @return An order instance for the {@link Stream} type.
   */
  public static <A> Ord<Stream<A>> streamOrd(final Ord<A> oa) {
    return ordDef((s1, s2) -> {
        if (s1.isEmpty())
            return s2.isEmpty() ? Ordering.EQ : Ordering.LT;
        else if (s2.isEmpty())
            return s1.isEmpty() ? Ordering.EQ : Ordering.GT;
        else {
            final Ordering c = oa.compare(s1.head(), s2.head());
          // FIXME: not stack safe
            return c == Ordering.EQ ? streamOrd(oa).def.compare(s1.tail()._1()).f(s2.tail()._1()) : c;
        }
    });
  }

  /**
   * An order instance for the {@link Array} type.
   *
   * @param oa Order across the elements of the array.
   * @return An order instance for the {@link Array} type.
   */
  public static <A> Ord<Array<A>> arrayOrd(final Ord<A> oa) {
    return ordDef((a1, a2) -> {
        int i = 0;
        //noinspection ForLoopWithMissingComponent
        for (; i < a1.length() && i < a2.length(); i++) {
            final Ordering c = oa.compare(a1.get(i), a2.get(i));
            if (c == Ordering.GT || c == Ordering.LT)
                return c;
        }
        return i == a1.length() ?
                i == a2.length() ?
                        Ordering.EQ :
                        Ordering.LT :
                i == a1.length() ?
                        Ordering.EQ :
                        Ordering.GT;
    });
  }

  /**
   * An order instance for the {@link Set} type.
   *
   * @param oa Order across the elements of the set.
   * @return An order instance for the {@link Set} type.
   */
  public static <A> Ord<Set<A>> setOrd(final Ord<A> oa) {
    return streamOrd(oa).contramap(Set::toStream);
  }

  /**
   * An order instance for the {@link Unit} type.
   */
  public static final Ord<Unit> unitOrd = ordDef((u1, u2) -> Ordering.EQ);

  /**
   * An order instance for a product-1.
   *
   * @param oa Order across the produced type.
   * @return An order instance for a product-1.
   */
  public static <A> Ord<P1<A>> p1Ord(final Ord<A> oa) {
    return oa.contramap(P1.__1());
  }


  /**
   * An order instance for a product-2, with the first factor considered most significant.
   *
   * @param oa An order instance for the first factor.
   * @param ob An order instance for the second factor.
   * @return An order instance for a product-2, with the first factor considered most significant.
   */
  public static <A, B> Ord<P2<A, B>> p2Ord(final Ord<A> oa, final Ord<B> ob) {
    return ordDef((a, b) -> oa.eq(a._1(), b._1()) ? ob.compare(a._2(), b._2()) : oa.compare(a._1(), b._1()));
  }

    public static <A, B> Ord<P2<A, B>> p2Ord1(Ord<A> oa) {
        return ordDef((p1, p2) -> oa.compare(p1._1(), p2._1()));
    }

    public static <A, B> Ord<P2<A, B>> p2Ord2(Ord<B> ob) {
        return ordDef((p1, p2) -> ob.compare(p1._2(), p2._2()));
    }

  /**
   * An order instance for a product-3, with the first factor considered most significant.
   *
   * @param oa An order instance for the first factor.
   * @param ob An order instance for the second factor.
   * @param oc An order instance for the third factor.
   * @return An order instance for a product-3, with the first factor considered most significant.
   */
  public static <A, B, C> Ord<P3<A, B, C>> p3Ord(final Ord<A> oa, final Ord<B> ob, final Ord<C> oc) {
    return ordDef((a, b) -> oa.eq(a._1(), b._1()) ?
           p2Ord(ob, oc).compare(P.p(a._2(), a._3()), P.p(b._2(), b._3()))
                                 : oa.compare(a._1(), b._1()));
  }

  /**
   * An order instance for the <code>Natural</code> type.
   */
  public static final Ord<Natural> naturalOrd = bigintOrd.contramap(Natural.bigIntegerValue);


  /**
   * An order instance for the <code>Comparable</code> interface.
   *
   * @return An order instance for the <code>Comparable</code> interface.
   */
  public static <A extends Comparable<A>> Ord<A> comparableOrd() {
    return ordDef((a1, a2) -> Ordering.fromInt(a1.compareTo(a2)));
  }

  /**
   * An order instance that uses {@link Object#hashCode()} for computing the order and equality,
   * thus objects returning the same hashCode are considered to be equals.
   * This is not safe and therefore this method is deprecated.
   *
   * @return An order instance that is based on {@link Object#hashCode()}.
   *
   * @deprecated As of release 4.7.
   */
  @Deprecated
  public static <A> Ord<A> hashOrd() {
    return ordDef(a -> {
      int aHash = a.hashCode();
      return a2 -> Ordering.fromInt(Integer.valueOf(aHash).compareTo(a2.hashCode()));
    });
  }

  /**
   * An order instance that uses {@link Object#hashCode()} and {@link Object#equals} for computing
   * the order and equality. First the hashCode is compared, if this is equal, objects are compared
   * using {@link Object#equals}.
   * WARNING: This ordering violate antisymmetry on hash collisions.
   *
   * @return An order instance that is based on {@link Object#hashCode()} and {@link Object#equals}.
   *
   * @deprecated As of release 4.7.
   */
  @Deprecated
  public static <A> Ord<A> hashEqualsOrd() {
    return ordDef(a -> {
      int aHash = a.hashCode();
      return a2 -> {
        final int a2Hash = a2.hashCode();
        return aHash < a2Hash ? Ordering.LT : aHash == a2Hash && a.equals(a2) ? Ordering.EQ : Ordering.GT;
      };
    });
  }

  class OrdComparator implements Comparator<A> {
	@Override
    public final int compare(A o1, A o2) {
	    return Ord.this.compare(o1, o2).toInt();
    }
  }

  public Comparator<A> toComparator() {
	  return new OrdComparator();
  }
}
