package fj;

import fj.data.Array;
import fj.data.DList;
import fj.data.List;
import fj.data.IO;
import fj.data.Natural;
import fj.data.NonEmptyList;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;

import java.math.BigDecimal;
import java.math.BigInteger;

import static fj.F1Functions.dimap;
import static fj.Function.constant;
import static fj.Function.identity;
import static fj.Monoid.*;
import static fj.data.DList.listDList;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 * Implementations must satisfy the law of associativity:
 * <ul>
 * <li><em>Associativity</em>; forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @version %build.number%
 */
public final class Semigroup<A> {

  /**
   * Primitives functions of Semigroup: minimal definition and overridable methods.
   */
  public interface Definition<A> {

    A append(A a1, A a2);

    default F<A, A> prepend(A a) {
      return a2 -> append(a, a2);
    }

    default A sum(A a, F0<Stream<A>> as) {
      return as.f().foldLeft(this::append, a);
    }

    default A multiply1p(int n, A a) {
      if (n <= 0) {
        return a;
      }

      A xTmp = a;
      int yTmp = n;
      A zTmp = a;
      while (true) {
        if ((yTmp & 1) == 1) {
          zTmp = append(xTmp, zTmp);
          if (yTmp == 1) {
            return zTmp;
          }
        }
        xTmp = append(xTmp, xTmp);
        yTmp = (yTmp) >>> 1;
      }
    }

    default Definition<A> dual() {
      return new Definition<A>(){

        @Override
        public A append(A a1, A a2) {
          return Definition.this.append(a2, a1);
        }

        @Override
        public A multiply1p(int n, A a) {
          return Definition.this.multiply1p(n, a);
        }
      };
    }
  }

  /**
   * Primitives functions of Semigroup: alternative minimal definition and overridable methods.
   */
  public interface AltDefinition<A> extends Definition<A> {
    @Override
    F<A, A> prepend(A a);

    @Override
    default A append(A a1, A a2) {
      return prepend(a1).f(a2);
    }
  }

  private final Definition<A> def;

  private Semigroup(final Definition<A> def) {
    this.def = def;
  }

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The of the two given arguments.
   */
  public A sum(final A a1, final A a2) {
    return def.append(a1, a2);
  }

  /**
   * Returns a function that sums the given value according to this semigroup.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this semigroup.
   */
  public F<A, A> sum(final A a1) {
    return def.prepend(a1);
  }

  /**
   * Returns a function that sums according to this semigroup.
   *
   * @return A function that sums according to this semigroup.
   */
  public F<A, F<A, A>> sum() {
    return def::prepend;
  }

  /**
   * Returns a value summed <code>n + 1</code> times (
   * <code>a + a + ... + a</code>) The default definition uses peasant
   * multiplication, exploiting associativity to only require `O(log n)` uses of
   * {@link #sum(Object, Object)}.
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed n + 1 times
   * @return {@code a} summed {@code n} times. If {@code n <= 0}, returns
   * {@code zero()}
   */
  public A multiply1p(int n, A a) {
    return def.multiply1p(n, a);
  }


  /**
   * Sums the given values with left-fold.
   */
  public A sumNel(final NonEmptyList<A> as) {
    return as.foldLeft1(def::append);
  }

  /**
   * Sums the given values with left-fold, shortcutting the computation as early as possible.
   */
  public A sumStream(A a, F0<Stream<A>> as) {
    return def.sum(a, as);
  }

  /**
   * Swaps the arguments when summing.
   */
  public Semigroup<A> dual() {
    return semigroupDef(def.dual());
  }

  /**
   * Lifts the semigroup to obtain a trivial monoid.
   */
  public Monoid<Option<A>> lift() {
    Definition<A> def = this.def;
    return monoidDef(new Monoid.Definition<Option<A>>() {
      @Override
      public Option<A> empty() {
        return none();
      }

      @Override
      public Option<A> append(Option<A> a1, Option<A> a2) {
        return a1.liftM2(a1, def::append).orElse(a1).orElse(a2);
      }

      @Override
      public Option<A> multiply(int n, Option<A> oa) {
        return n > 0 ? oa.map(a -> def.multiply1p(n - 1, a)) : none();
      }

      @Override
      public Option<A> sum(F0<Stream<Option<A>>> oas) {
        Stream<A> as = oas.f().bind(Option::toStream);
        return as.uncons(none(), h -> tail ->  some(def.sum(h, tail::_1)));
      }
    });
  }

  /**
   * Maps the given functions across this monoid as an invariant functor.
   *
   * @param f The covariant map.
   * @param g The contra-variant map.
   * @return A new monoid.
   */
  public <B> Semigroup<B> xmap(final F<A, B> f, final F<B, A> g) {
    Definition<A> def = this.def;
    return semigroupDef(new Definition<B>() {

      @Override
      public B append(B a1, B a2) {
        return f.f(def.append(g.f(a1), g.f(a2)));
      }

      @Override
      public F<B, B> prepend(B b) {
        return dimap(def.prepend(g.f(b)), g, f);
      }

      @Override
      public B multiply1p(int n, B b) {
        return f.f(def.multiply1p(n , g.f(b)));
      }

      @Override
      public B sum(B b, F0<Stream<B>> bs) {
        return f.f(def.sum(g.f(b), () -> bs.f().map(g)));
      }
    });
  }

  public <B, C> Semigroup<C> compose(Semigroup<B> sb, final F<C, B> b, final F<C, A> a, final F2<A, B, C> c) {
    Definition<A> saDef = this.def;
    Definition<B> sbDef = sb.def;
    return semigroupDef(new Definition<C>() {

      @Override
      public C append(C c1, C c2) {
        return c.f(saDef.append(a.f(c1), a.f(c2)), sbDef.append(b.f(c1), b.f(c2)));
      }

      @Override
      public F<C, C> prepend(C c1) {
        F<A, A> prependA = saDef.prepend(a.f(c1));
        F<B, B> prependB = sbDef.prepend(b.f(c1));
        return c2 -> c.f(prependA.f(a.f(c2)), prependB.f(b.f(c2)));
      }

      @Override
      public C multiply1p(int n, C c1) {
        return c.f(saDef.multiply1p(n, a.f(c1)), sbDef.multiply1p(n, b.f(c1)));
      }

      @Override
      public C sum(C c1, F0<Stream<C>> cs) {
        return c.f(saDef.sum(a.f(c1), () -> cs.f().map(a)), sbDef.sum(b.f(c1), () -> cs.f().map(b)));
      }
    });
  }

  /**
   * Constructs a monoid from this semigroup and a zero value, which must follow the monoidal laws.
   *
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public Monoid<A> monoid(A zero) {
    return monoidDef(this.def, zero);
  }

  /**
   * Constructs a semigroup from the given definition.
   *
   * @param def The definition to construct this semigroup with.
   * @return A semigroup from the given definition.
   */
  public static <A> Semigroup<A> semigroupDef(final Definition<A> def) {
    return new Semigroup<>(def);
  }

  /**
   * Constructs a semigroup from the given definition.
   *
   * @param def The definition to construct this semigroup with.
   * @return A semigroup from the given definition.
   */
  public static <A> Semigroup<A> semigroupDef(final AltDefinition<A> def) {
    return new Semigroup<>(def);
  }

  /**
   * Constructs a semigroup from the given function.
   * Java 8+ users: use {@link #semigroupDef(AltDefinition)} instead.
   *
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final F<A, F<A, A>> sum) {
    return semigroupDef(sum::f);
  }

  /**
   * Constructs a semigroup from the given function.
   * Java 8+ users: use {@link #semigroupDef(Definition)} instead.
   *
   * @param sum The function to construct this semigroup with.
   * @return A semigroup from the given function.
   */
  public static <A> Semigroup<A> semigroup(final F2<A, A, A> sum) {
    return new Semigroup<>(sum::f);
  }


  /**
   * A semigroup that adds integers.
   */
  public static final Semigroup<Integer> intAdditionSemigroup = intAdditionMonoid.semigroup();

  /**
   * @deprecated Since 4.7. Due to rounding errors, addition of doubles does not comply with monoid laws
   */
  @Deprecated
  public static final Semigroup<Double> doubleAdditionSemigroup = semigroupDef((d1, d2) -> d1 + d2);

  /**
   * A semigroup that multiplies integers.
   */
  public static final Semigroup<Integer> intMultiplicationSemigroup = intMultiplicationMonoid.semigroup();

  /**
   * @deprecated Since 4.7. Due to rounding errors, addition of doubles does not comply with monoid laws
   */
  @Deprecated
  public static final Semigroup<Double> doubleMultiplicationSemigroup = semigroupDef((d1, d2) -> d1 * d2);

  /**
   * A semigroup that yields the maximum of integers.
   */
  public static final Semigroup<Integer> intMaximumSemigroup = intMaxMonoid.semigroup();

  /**
   * A semigroup that yields the minimum of integers.
   */
  public static final Semigroup<Integer> intMinimumSemigroup = intMinMonoid.semigroup();

  /**
   * A semigroup that adds big integers.
   */
  public static final Semigroup<BigInteger> bigintAdditionSemigroup = bigintAdditionMonoid.semigroup();

  /**
   * A semigroup that multiplies big integers.
   */
  public static final Semigroup<BigInteger> bigintMultiplicationSemigroup =
      semigroup(BigInteger::multiply);

  /**
   * A semigroup that yields the maximum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMaximumSemigroup = Ord.bigintOrd.maxSemigroup();

  /**
   * A semigroup that yields the minimum of big integers.
   */
  public static final Semigroup<BigInteger> bigintMinimumSemigroup = Ord.bigintOrd.minSemigroup();

  /**
   * A semigroup that adds big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalAdditionSemigroup = bigdecimalAdditionMonoid.semigroup();

  /**
   * A semigroup that multiplies big decimals.
   */
  public static final Semigroup<BigDecimal> bigdecimalMultiplicationSemigroup = bigdecimalMultiplicationMonoid.semigroup();

  /**
   * A semigroup that yields the maximum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMaximumSemigroup = Ord.bigdecimalOrd.maxSemigroup();

  /**
   * A semigroup that yields the minimum of big decimals.
   */
  public static final Semigroup<BigDecimal> bigDecimalMinimumSemigroup = Ord.bigdecimalOrd.minSemigroup();

  /**
   * A semigroup that multiplies natural numbers.
   */
  public static final Semigroup<Natural> naturalMultiplicationSemigroup = naturalMultiplicationMonoid.semigroup();

  /**
   * A semigroup that adds natural numbers.
   */
  public static final Semigroup<Natural> naturalAdditionSemigroup = naturalAdditionMonoid.semigroup();

  /**
   * A semigroup that yields the maximum of natural numbers.
   */
  public static final Semigroup<Natural> naturalMaximumSemigroup = Ord.naturalOrd.maxSemigroup();

  /**
   * A semigroup that yields the minimum of natural numbers.
   */
  public static final Semigroup<Natural> naturalMinimumSemigroup = Ord.naturalOrd.minSemigroup();

  /**
   * A semigroup that adds longs.
   */
  public static final Semigroup<Long> longAdditionSemigroup = longAdditionMonoid.semigroup();

  /**
   * A semigroup that multiplies longs.
   */
  public static final Semigroup<Long> longMultiplicationSemigroup = longMultiplicationMonoid.semigroup();

  /**
   * A semigroup that yields the maximum of longs.
   */
  public static final Semigroup<Long> longMaximumSemigroup = Ord.longOrd.maxSemigroup();

  /**
   * A semigroup that yields the minimum of longs.
   */
  public static final Semigroup<Long> longMinimumSemigroup = Ord.longOrd.minSemigroup();

  /**
   * A semigroup that ORs booleans.
   */
  public static final Semigroup<Boolean> disjunctionSemigroup = disjunctionMonoid.semigroup();

  /**
   * A semigroup that XORs booleans.
   */
  public static final Semigroup<Boolean> exclusiveDisjunctionSemiGroup = exclusiveDisjunctionMonoid.semigroup();

  /**
   * A semigroup that ANDs booleans.
   */
  public static final Semigroup<Boolean> conjunctionSemigroup = conjunctionMonoid.semigroup();

  /**
   * A semigroup that appends strings.
   */
  public static final Semigroup<String> stringSemigroup = stringMonoid.semigroup();

  /**
   * A semigroup that appends string buffers.
   */
  public static final Semigroup<StringBuffer> stringBufferSemigroup = stringBufferMonoid.semigroup();

  /**
   * A semigroup that appends string builders.
   */
  public static final Semigroup<StringBuilder> stringBuilderSemigroup = stringBuilderMonoid.semigroup();

  /**
   * A semigroup which always uses the "first" (left-hand side) value.
   */
  public static <A> Semigroup<A> firstSemigroup() {
      return semigroupDef(new Definition<A>() {
        @Override
        public A append(A a1, A a2) {
          return a1;
        }

        @Override
        public F<A, A> prepend(A a) {
          return constant(a);
        }

        @Override
        public A multiply1p(int n, A a) {
          return a;
        }

        @Override
        public A sum(A a, F0<Stream<A>> as) {
          return a;
        }
      });
  }

  /**
   * A semigroup which always uses the "last" (right-hand side) value.
   */
  public static <A> Semigroup<A> lastSemigroup() {
    return semigroupDef(new Definition<A>() {
      @Override
      public A append(A a1, A a2) {
        return a2;
      }

      @Override
      public F<A, A> prepend(A a) {
        return identity();
      }

      @Override
      public A multiply1p(int n, A a) {
        return a;
      }
    });
  }

  /**
   * A semigroup for functions.
   *
   * @param sb The smeigroup for the codomain.
   * @return A semigroup for functions.
   */
  public static <A, B> Semigroup<F<A, B>> functionSemigroup(final Semigroup<B> sb) {
    Definition<B> sbDef = sb.def;
    return semigroupDef((a1, a2) -> a -> sbDef.append(a1.f(a), a2.f(a)));
  }

  /**
   * A semigroup for lists.
   *
   * @return A semigroup for lists.
   */
  public static <A> Semigroup<List<A>> listSemigroup() {
    return Monoid.<A>listMonoid().semigroup();
  }

  /**
   * A semigroup for non-empty lists.
   *
   * @return A semigroup for non-empty lists.
   */
  public static <A> Semigroup<NonEmptyList<A>> nonEmptyListSemigroup() {

    return semigroupDef(new Definition<NonEmptyList<A>>() {
      @Override
      public NonEmptyList<A> append(NonEmptyList<A> a1, NonEmptyList<A> a2) {
        return a1.append(a2);
      }

      @Override
      public NonEmptyList<A> sum(NonEmptyList<A> nea, F0<Stream<NonEmptyList<A>>> neas) {
        List<A> tail = neas.f().map(nel -> listDList(nel.toList())).foldLeft(DList::append, DList.<A>nil()).run();
        return nea.append(tail);
      }
    });
  }

  /**
   * A semigroup for optional values.
   * @deprecated since 4.7. Use {@link #firstOptionSemigroup()}.
   *
   ** @return A semigroup for optional values.
   */
  public static <A> Semigroup<Option<A>> optionSemigroup() {
    return firstOptionSemigroup();
  }

  /**
   * A semigroup for optional values that take the first available value.
   *
   * @return A semigroup for optional values that take the first available value.
   */
  public static <A> Semigroup<Option<A>> firstOptionSemigroup() {
    return Monoid.<A>firstOptionMonoid().semigroup();
  }

  /**
   * A semigroup for optional values that take the last available value.
   *
   * @return A semigroup for optional values that take the last available value.
   */
  public static <A> Semigroup<Option<A>> lastOptionSemigroup() {
    return Monoid.<A>lastOptionMonoid().semigroup();
  }

  /**
   * A semigroup for streams.
   *
   * @return A semigroup for streams.
   */
  public static <A> Semigroup<Stream<A>> streamSemigroup() {
    return Monoid.<A>streamMonoid().semigroup();
  }

  /**
   * A semigroup for arrays.
   *
   * @return A semigroup for arrays.
   */
  public static <A> Semigroup<Array<A>> arraySemigroup() {
    return Monoid.<A>arrayMonoid().semigroup();
  }

  /**
   * A lazy semigroup for unary products.
   *
   * @param sa A semigroup for the product's type.
   * @return A semigroup for unary products.
   */
  public static <A> Semigroup<P1<A>> p1Semigroup(final Semigroup<A> sa) {
    Definition<A> def = sa.def;
    return semigroupDef(new Definition<P1<A>>() {
      @Override
      public P1<A> append(P1<A> a1, P1<A> a2) {
        return P.lazy(() -> def.append(a1._1(), a2._1()));
      }

      @Override
      public P1<A> multiply1p(int n, P1<A> ap1) {
        return P.lazy(() -> def.multiply1p(n, ap1._1()));
      }

      @Override
      public P1<A> sum(P1<A> ap1, F0<Stream<P1<A>>> as) {
        return P.lazy(() -> def.sum(ap1._1(), () -> as.f().map(P1.__1())));
      }
    });
  }

  /**
   * A lazy semigroup for binary products.
   *
   * @param sa A semigroup for the product's first type.
   * @param sb A semigroup for the product's second type.
   * @return A semigroup for binary products.
   */
  public static <A, B> Semigroup<P2<A, B>> p2Semigroup(final Semigroup<A> sa, final Semigroup<B> sb) {
    return semigroupDef((a1, a2) -> P.lazy(() -> sa.sum(a1._1(), a2._1()), () -> sb.sum(a1._2(), a2._2())));
  }

  /**
   * A semigroup for IO values.
   */
  public static <A> Semigroup<IO<A>> ioSemigroup(final Semigroup <A> sa) {
    Definition<A> def = sa.def;
    return semigroupDef((a1, a2) -> () -> def.append(a1.run(), a2.run()));
  }

  /**
   * A semigroup for the Unit value.
   */
  public static final Semigroup<Unit> unitSemigroup = unitMonoid.semigroup();

  /**
   * A semigroup for sets.
   *
   * @return a semigroup for sets.
   */
  public static <A> Semigroup<Set<A>> setSemigroup() {
    return semigroupDef(Set::union);
  }

}
