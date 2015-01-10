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

import static fj.Function.curry;

/**
 * Tests for ordering between two objects.
 *
 * @version %build.number%
 */
public final class Ord<A> {
  private final F<A, F<A, Ordering>> f;

  private Ord(final F<A, F<A, Ordering>> f) {
    this.f = f;
  }

  /**
   * First-class ordering.
   *
   * @return A function that returns an ordering for its arguments.
   */
  public F<A, F<A, Ordering>> compare() {
    return f;
  }

  /**
   * Returns an ordering for the given arguments.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return An ordering for the given arguments.
   */
  public Ordering compare(final A a1, final A a2) {
    return f.f(a1).f(a2);
  }

  /**
   * Returns <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for equality to another.
   * @param a2 An instance to compare for equality to another.
   * @return <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return compare(a1, a2) == Ordering.EQ;
  }

  /**
   * Returns an <code>Equal</code> for this order.
   *
   * @return An <code>Equal</code> for this order.
   */
  public Equal<A> equal() {
    return Equal.equal(curry(this::eq));
  }

  /**
   * Maps the given function across this ord as a contra-variant functor.
   *
   * @param f The function to map.
   * @return A new ord.
   */
  public <B> Ord<B> comap(final F<B, A> f) {
    return ord(F1Functions.o(F1Functions.o(F1Functions.<B, A, Ordering>andThen(f), this.f), f));
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
    return compare(a1, a2) == Ordering.LT;
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
    return compare(a1, a2) == Ordering.GT;
  }

  /**
   * Returns a function that returns true if its argument is less than the argument to this method.
   *
   * @param a A value to compare against.
   * @return A function that returns true if its argument is less than the argument to this method.
   */
  public F<A, Boolean> isLessThan(final A a) {
    return a2 -> compare(a2, a) == Ordering.LT;
  }

  /**
   * Returns a function that returns true if its argument is greater than than the argument to this method.
   *
   * @param a A value to compare against.
   * @return A function that returns true if its argument is greater than the argument to this method.
   */
  public F<A, Boolean> isGreaterThan(final A a) {
    return a2 -> compare(a2, a) == Ordering.GT;
  }

  /**
   * Returns the greater of its two arguments.
   *
   * @param a1 A value to compare with another.
   * @param a2 A value to compare with another.
   * @return The greater of the two values.
   */
  public A max(final A a1, final A a2) {
    return isGreaterThan(a1, a2) ? a1 : a2;
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
   */
  public final F<A, F<A, A>> max = curry((a, a1) -> max(a, a1));

  /**
   * A function that returns the lesser of its two arguments.
   */
  public final F<A, F<A, A>> min = curry((a, a1) -> min(a, a1));

  /**
   * Returns an order instance that uses the given equality test and ordering function.
   *
   * @param f The order function.
   * @return An order instance.
   */
  public static <A> Ord<A> ord(final F<A, F<A, Ordering>> f) {
    return new Ord<A>(f);
  }

  /**
   * An order instance for the <code>boolean</code> type.
   */
  public static final Ord<Boolean> booleanOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>byte</code> type.
   */
  public static final Ord<Byte> byteOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>char</code> type.
   */
  public static final Ord<Character> charOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>double</code> type.
   */
  public static final Ord<Double> doubleOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>float</code> type.
   */
  public static final Ord<Float> floatOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>int</code> type.
   */
  public static final Ord<Integer> intOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>BigInteger</code> type.
   */
  public static final Ord<BigInteger> bigintOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>BigDecimal</code> type.
   */
  public static final Ord<BigDecimal> bigdecimalOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>long</code> type.
   */
  public static final Ord<Long> longOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the <code>short</code> type.
   */
  public static final Ord<Short> shortOrd = ord(
          a1 -> a2 -> {
            final int x = a1.compareTo(a2);
            return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the {@link Ordering} type.
   */
  public static final Ord<Ordering> orderingOrd = Ord.<Ordering>ord(curry((o1, o2) -> o1 == o2 ?
         Ordering.EQ :
         o1 == Ordering.LT ?
         Ordering.LT :
         o2 == Ordering.LT ?
         Ordering.GT :
         o1 == Ordering.EQ ?
         Ordering.LT :
         Ordering.GT));

  /**
   * An order instance for the {@link String} type.
   */
  public static final Ord<String> stringOrd = ord(
          a1 -> a2 -> {
              final int x = a1.compareTo(a2);
              return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
          });

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Ord<StringBuffer> stringBufferOrd =
      ord(a1 -> a2 -> stringOrd.compare(a1.toString(), a2.toString()));

  /**
   * An order instance for the {@link StringBuffer} type.
   */
  public static final Ord<StringBuilder> stringBuilderOrd =
      ord(a1 -> a2 -> stringOrd.compare(a1.toString(), a2.toString()));

  /**
   * An order instance for the {@link Option} type.
   *
   * @param oa Order across the element of the option.
   * @return An order instance for the {@link Option} type.
   */
  public static <A> Ord<Option<A>> optionOrd(final Ord<A> oa) {
    return ord(o1 -> o2 -> o1.isNone() ?
            o2.isNone() ?
                    Ordering.EQ :
                    Ordering.LT :
            o2.isNone() ?
                    Ordering.GT :
                    oa.f.f(o1.some()).f(o2.some()));
  }

  /**
   * An order instance for the {@link Either} type.
   *
   * @param oa Order across the left side of {@link Either}.
   * @param ob Order across the right side of {@link Either}.
   * @return An order instance for the {@link Either} type.
   */
  public static <A, B> Ord<Either<A, B>> eitherOrd(final Ord<A> oa, final Ord<B> ob) {
    return ord(e1 -> e2 -> e1.isLeft() ?
            e2.isLeft() ?
                    oa.f.f(e1.left().value()).f(e2.left().value()) :
                    Ordering.LT :
            e2.isLeft() ?
                    Ordering.GT :
                    ob.f.f(e1.right().value()).f(e2.right().value()));
  }

  /**
   * An order instance for the {@link Validation} type.
   *
   * @param oa Order across the failing side of {@link Validation}.
   * @param ob Order across the succeeding side of {@link Validation}.
   * @return An order instance for the {@link Validation} type.
   */
  public static <A, B> Ord<Validation<A, B>> validationOrd(final Ord<A> oa, final Ord<B> ob) {
    return eitherOrd(oa, ob).comap(Validation.<A, B>either());
  }

  /**
   * An order instance for the {@link List} type.
   *
   * @param oa Order across the elements of the list.
   * @return An order instance for the {@link List} type.
   */
  public static <A> Ord<List<A>> listOrd(final Ord<A> oa) {
    return ord(l1 -> l2 -> {
        if (l1.isEmpty())
            return l2.isEmpty() ? Ordering.EQ : Ordering.LT;
        else if (l2.isEmpty())
            return l1.isEmpty() ? Ordering.EQ : Ordering.GT;
        else {
            final Ordering c = oa.compare(l1.head(), l2.head());
            return c == Ordering.EQ ? listOrd(oa).f.f(l1.tail()).f(l2.tail()) : c;
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
    return listOrd(oa).comap(NonEmptyList.<A>toList_());
  }

  /**
   * An order instance for the {@link Stream} type.
   *
   * @param oa Order across the elements of the stream.
   * @return An order instance for the {@link Stream} type.
   */
  public static <A> Ord<Stream<A>> streamOrd(final Ord<A> oa) {
    return ord(s1 -> s2 -> {
        if (s1.isEmpty())
            return s2.isEmpty() ? Ordering.EQ : Ordering.LT;
        else if (s2.isEmpty())
            return s1.isEmpty() ? Ordering.EQ : Ordering.GT;
        else {
            final Ordering c = oa.compare(s1.head(), s2.head());
            return c == Ordering.EQ ? streamOrd(oa).f.f(s1.tail()._1()).f(s2.tail()._1()) : c;
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
    return ord(a1 -> a2 -> {
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
    return streamOrd(oa).comap(as -> as.toStream());
  }

  /**
   * An order instance for the {@link Unit} type.
   */
  public static final Ord<Unit> unitOrd = ord(curry((Unit u1, Unit u2) -> Ordering.EQ));

  /**
   * An order instance for a product-1.
   *
   * @param oa Order across the produced type.
   * @return An order instance for a product-1.
   */
  public static <A> Ord<P1<A>> p1Ord(final Ord<A> oa) {
    return oa.comap(P1.<A>__1());
  }


  /**
   * An order instance for a product-2, with the first factor considered most significant.
   *
   * @param oa An order instance for the first factor.
   * @param ob An order instance for the second factor.
   * @return An order instance for a product-2, with the first factor considered most significant.
   */
  public static <A, B> Ord<P2<A, B>> p2Ord(final Ord<A> oa, final Ord<B> ob) {
    return ord(curry((P2<A, B> a, P2<A, B> b) -> oa.eq(a._1(), b._1()) ? ob.compare(a._2(), b._2()) : oa.compare(a._1(), b._1())));
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
    return ord(curry((P3<A, B, C> a, P3<A, B, C> b) -> oa.eq(a._1(), b._1()) ?
           p2Ord(ob, oc).compare(P.p(a._2(), a._3()), P.p(b._2(), b._3()))
                                 : oa.compare(a._1(), b._1())));
  }

  /**
   * An order instance for the <code>Natural</code> type.
   */
  public static final Ord<Natural> naturalOrd = bigintOrd.comap(Natural.bigIntegerValue);


  /**
   * An order instance for the <code>Comparable</code> interface.
   *
   * @return An order instance for the <code>Comparable</code> interface.
   */
  public static <A extends Comparable<A>> Ord<A> comparableOrd() {

    return ord(a1 -> a2 -> Ordering.fromInt(a1.compareTo(a2)));
  }

  /**
   * An order instance that uses {@link Object#hashCode()} for computing the order and equality,
   * thus objects returning the same hashCode are considered to be equals (check {@link #hashEqualsOrd()}
   * for an additional check on {@link Object#equals(Object)}).
   *
   * @return An order instance that is based on {@link Object#hashCode()}.
   * @see #hashEqualsOrd()
   */
  public static <A> Ord<A> hashOrd() {
    return ord(a -> a2 -> {
        final int x = a.hashCode() - a2.hashCode();
        return x < 0 ? Ordering.LT : x == 0 ? Ordering.EQ : Ordering.GT;
    });
  }

  /**
   * An order instance that uses {@link Object#hashCode()} and {@link Object#equals} for computing
   * the order and equality. First the hashCode is compared, if this is equal, objects are compared
   * using {@link Object#equals}.
   *
   * @return An order instance that is based on {@link Object#hashCode()} and {@link Object#equals}.
   */
  public static <A> Ord<A> hashEqualsOrd() {
    return ord(a -> a2 -> {
        final int x = a.hashCode() - a2.hashCode();
        return x < 0 ? Ordering.LT : x == 0 && a.equals(a2) ? Ordering.EQ : Ordering.GT;
    });
  }

}
