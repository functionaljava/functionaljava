package fj;

import static fj.F1Functions.dimap;

import fj.data.Array;
import fj.data.DList;
import fj.data.List;
import fj.data.IO;
import fj.data.Natural;
import fj.data.Option;
import fj.data.Set;
import fj.data.Stream;

import static fj.Function.*;
import static fj.Semigroup.semigroupDef;
import static fj.Unit.unit;
import static fj.data.List.nil;
import static fj.data.Natural.natural;
import static fj.data.Option.none;
import static fj.data.Stream.iterableStream;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must
 * follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall x y z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @version %build.number%
 */
public final class Monoid<A> {


  private final Definition<A> def;

  /**
   * Primitives functions of Monoid: minimal definition and overridable methods.
   */
  public interface Definition<A> extends Semigroup.Definition<A> {

    A empty();


    default A sum(F0<Stream<A>> as) {
      return as.f().foldLeft(this::append, empty());
    }

    @Override
    default A sum(A a, F0<Stream<A>> as) {
      return sum(() -> Stream.cons(a, as));
    }

    default A multiply(int n, A a) {
      return (n <= 0)
          ? empty()
          : Semigroup.Definition.super.multiply1p(n - 1, a);
    }

    @Override
    default A multiply1p(int n, A a) {
      return n == Integer.MAX_VALUE
          ? append(a, multiply(n, a))
          : multiply(n + 1, a);
    }

    default Definition<A> dual() {
      return new Definition<A>(){

        @Override
        public A empty() {
          return Definition.this.empty();
        }

        @Override
        public A append(A a1, A a2) {
          return Definition.this.append(a2, a1);
        }

        @Override
        public A multiply(int n, A a) {
          return Definition.this.multiply(n, a);
        }

        @Override
        public Definition<A> dual() {
          return Definition.this;
        }
      };
    }
  }


  /**
   * Primitives functions of Monoid: alternative minimal definition and overridable methods.
   */
  public interface AltDefinition<A> extends Definition<A> {

    @Override
    F<A, A> prepend(A a);

    @Override
    default A append(A a1, A a2) {
      return prepend(a1).f(a2);
    }
  }

  private Monoid(Definition<A> def) {
    this.def = def;
  }

  /**
   * Composes this monoid with another.
   */
  public <B> Monoid<P2<A,B>>compose(Monoid<B> m) {
    return compose(m, P2.__1(), P2.__2(), P::p);
  }

  /**
   * Returns a semigroup projection of this monoid.
   *
   * @return A semigroup projection of this monoid.
   */
  public Semigroup<A> semigroup() {
    return semigroupDef(def);
  }

  /**
   * Maps the given functions across this monoid as an invariant functor.
   *
   * @param f The covariant map.
   * @param g The contra-variant map.
   * @return A new monoid.
   */
  public <B> Monoid<B> xmap(final F<A, B> f, final F<B, A> g) {
    Monoid.Definition<A> def = this.def;
    B zero = f.f(def.empty());
    return monoidDef(new Definition<B>() {
      @Override
      public B empty() {
        return zero;
      }

      @Override
      public B append(B a1, B a2) {
        return f.f(def.append(g.f(a1), g.f(a2)));
      }

      @Override
      public F<B, B> prepend(B b) {
        return dimap(def.prepend(g.f(b)), g, f);
      }

      @Override
      public B multiply(int n, B b) {
        return f.f(def.multiply(n , g.f(b)));
      }

      @Override
      public B sum(F0<Stream<B>> as) {
        return f.f(def.sum(() -> as.f().map(g)));
      }
    });
  }


  public <B, C> Monoid<C> compose(Monoid<B> mb, final F<C, A> a, final F<C, B> b, final F2<A, B, C> c) {
    Definition<A> maDef = this.def;
    Definition<B> mbDef = mb.def;
    C empty = c.f(maDef.empty(), mbDef.empty());
    return monoidDef(new Definition<C>() {

      @Override
      public C empty() {
        return empty;
      }

      @Override
      public C append(C c1, C c2) {
        return c.f(maDef.append(a.f(c1), a.f(c2)), mbDef.append(b.f(c1), b.f(c2)));
      }

      @Override
      public F<C, C> prepend(C c1) {
        F<A, A> prependA = maDef.prepend(a.f(c1));
        F<B, B> prependB = mbDef.prepend(b.f(c1));
        return c2 -> c.f(prependA.f(a.f(c2)), prependB.f(b.f(c2)));
      }

      @Override
      public C multiply(int n, C c1) {
        return c.f(maDef.multiply(n, a.f(c1)), mbDef.multiply(n, b.f(c1)));
      }

      @Override
      public C sum(F0<Stream<C>> cs) {
        return c.f(maDef.sum(() -> cs.f().map(a)), mbDef.sum(() -> cs.f().map(b)));
      }
    });
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
   * Returns a function that sums the given value according to this monoid.
   *
   * @param a1 The value to sum.
   * @return A function that sums the given value according to this monoid.
   */
  public F<A, A> sum(final A a1) {
    return def.prepend(a1);
  }

  /**
   * Returns a function that sums according to this monoid.
   *
   * @return A function that sums according to this monoid.
   */
  public F<A, F<A, A>> sum() {
    return def::prepend;
  }

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  public A zero() {
    return def.empty();
  }

  /**
   * Returns a value summed <code>n</code> times (<code>a + a + ... + a</code>).
   * The default definition uses peasant multiplication, exploiting
   * associativity to only require `O(log n)` uses of
   * {@link #sum(Object, Object)}.
   *
   * @param n multiplier
   * @param a the value to be reapeatly summed
   * @return {@code a} summed {@code n} times. If {@code n <= 0}, returns
   * {@code zero()}
   */
  public A multiply(final int n, final A a) {
    return def.multiply(n, a);
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumRight(final List<A> as) {
    return as.foldRight(def::append, def.empty());
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumRight(final Stream<A> as) {
    return as.foldRight1(def::append, def.empty());
  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final List<A> as) {
    return as.foldLeft(def::append, def.empty());
  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final Stream<A> as) {
    return def.sum(() -> as);
  }

  /**
   * Returns a function that sums the given values with left-fold.
   *
   * @return a function that sums the given values with left-fold.
   */
  public F<List<A>, A> sumLeft() {
    return this::sumLeft;
  }

  /**
   * Returns a function that sums the given values with right-fold.
   *
   * @return a function that sums the given values with right-fold.
   */
  public F<List<A>, A> sumRight() {
    return this::sumRight;
  }

  /**
   * Returns a function that sums the given values with left-fold.
   *
   * @return a function that sums the given values with left-fold.
   */
  public F<Stream<A>, A> sumLeftS() {
    return this::sumLeft;
  }

  /**
   * Intersperses the given value between each two elements of the iterable, and sums the result.
   *
   * @param as An iterable of values to sum.
   * @param a  The value to intersperse between values of the given iterable.
   * @return The sum of the given values and the interspersed value.
   */
  public A join(final Iterable<A> as, final A a) {
    final Stream<A> s = iterableStream(as);
    F<A, A> prependA = def.prepend(a);
    return s.isEmpty()
           ? def.empty()
           : s.foldLeft1((a1, a2) -> def.append(a1, prependA.f(a2)));
  }

  /**
   * Swaps the arguments when summing.
   */
  public Monoid<A> dual() {
    return monoidDef(def.dual());
  }

  /**
   * Constructs a monoid from the given definition, which must follow the monoidal
   * laws.
   *
   * @param def  The definition for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoidDef(Definition<A> def) {
    return new Monoid<>(def);
  }

  /**
   * Constructs a monoid from the given definition, which must follow the monoidal
   * laws.
   *
   * @param def  The definition for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoidDef(AltDefinition<A> def) {
    return new Monoid<>(def);
  }

  /**
   * Constructs a monoid from the given semigroup definition and zero value, which must follow the monoidal laws.
   *
   * @param s    The semigroup definition for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoidDef(final Semigroup.Definition<A> s, final A zero) {
    return new Monoid<>(new Monoid.Definition<A>() {
      @Override
      public A empty() {
        return zero;
      }

      @Override
      public A sum(F0<Stream<A>> as) {
        return s.sum(zero, as);
      }

      @Override
      public A sum(A a, F0<Stream<A>> as) {
        return s.sum(a, as);
      }

      @Override
      public A multiply(int n, A a) {
        return (n <= 0)
            ? zero
            : s.multiply1p(n - 1, a);
      }

      @Override
      public A multiply1p(int n, A a) {
        return s.multiply1p(n, a);
      }

      @Override
      public A append(A a1, A a2) {
        return s.append(a1, a2);
      }

      @Override
      public F<A, A> prepend(A a) {
        return s.prepend(a);
      }
    });
  }

  /**
   * Constructs a monoid from the given semigroup definition and zero value, which must follow the monoidal laws.
   *
   * @param s    The semigroup definition for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoidDef(final Semigroup.AltDefinition<A> s, final A zero) {
    return monoidDef((Semigroup.Definition<A>) s, zero);
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal
   * laws.
   * Java 8+ users: use {@link #monoidDef(Semigroup.Definition, Object)} instead.
   *
   * @param sum  The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final F<A, F<A, A>> sum, final A zero) {
    return new Monoid<>(new AltDefinition<A>() {
      @Override
      public F<A, A> prepend(A a) {
        return sum.f(a);
      }

      @Override
      public A empty() {
        return zero;
      }
    });
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal
   * laws.
   *
   * Java 8+ users: use {@link #monoidDef(Semigroup.Definition, Object)} instead.
   *
   * @param sum  The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final F2<A, A, A> sum, final A zero) {
    return new Monoid<>(new Definition<A>() {
      @Override
      public A empty() {
        return zero;
      }

      @Override
      public A append(A a1, A a2) {
        return sum.f(a1, a2);
      }
    });
  }

  /**
   * Constructs a monoid from the given semigroup and zero value, which must follow the monoidal laws.
   * @deprecated since 4.7. Use {@link #monoidDef(Semigroup.Definition, Object)} or {@link Semigroup#monoid(Object)} instead.
   *
   * @param s    The semigroup for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  @Deprecated
  public static <A> Monoid<A> monoid(final Semigroup<A> s, final A zero) {
    return s.monoid(zero);
  }


  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intAdditionMonoid = monoidDef(new Definition<Integer>() {
    @Override
    public Integer empty() {
      return 0;
    }

    @Override
    public Integer append(Integer a1, Integer a2) {
      return a1 + a2;
    }

    @Override
    public Integer multiply(int n, Integer i) {
      return n <= 0 ? 0 : n * i;
    }
  });

  /**
   * A monoid that multiplies integers.
   */
  public static final Monoid<Integer> intMultiplicationMonoid = monoidDef(new Definition<Integer>() {
    @Override
    public Integer empty() {
      return 1;
    }

    @Override
    public Integer append(Integer i1, Integer i2) {
      return i1 * i2;
    }

    @Override
    public Integer sum(F0<Stream<Integer>> as) {
      int x = 1;
      for (Stream<Integer> xs = as.f(); x != 0 && !xs.isEmpty(); xs = xs.tail()._1()) {
        x *= xs.head();
      }
      return x;
    }

    @Override
    public Integer multiply(int n, Integer integer) {
      return n <= 0 ? 1 : (int) StrictMath.pow(integer.doubleValue(), n);
    }
  });

  /**
   * @deprecated Since 4.7. Due to rounding errors, addition of doubles does not comply with monoid laws
   */
  @Deprecated
  public static final Monoid<Double> doubleAdditionMonoid = monoidDef((d1, d2) -> d1 + d2, 0.0);

  /**
   * @deprecated Since 4.7. Due to rounding errors, multiplication of doubles does not comply with monoid laws
   */
  @Deprecated
  public static final Monoid<Double> doubleMultiplicationMonoid = monoidDef((d1, d2) -> d1 * d2, 1.0);

  /**
   * A monoid that adds big integers.
   */
  public static final Monoid<BigInteger> bigintAdditionMonoid = monoidDef(new Definition<BigInteger>() {
    @Override
    public BigInteger empty() {
      return BigInteger.ZERO;
    }

    @Override
    public BigInteger append(BigInteger a1, BigInteger a2) {
      return a1.add(a2);
    }

    @Override
    public BigInteger multiply(int n, BigInteger a) {
      return n <= 0 ? BigInteger.ZERO : a.multiply(BigInteger.valueOf(n));
    }
  });

  /**
   * A monoid that multiplies big integers.
   */
  public static final Monoid<BigInteger> bigintMultiplicationMonoid = monoidDef(new Definition<BigInteger>() {
    @Override
    public BigInteger empty() {
      return BigInteger.ONE;
    }

    @Override
    public BigInteger append(BigInteger a1, BigInteger a2) {
      return a1.multiply(a2);
    }

    @Override
    public BigInteger multiply(int n, BigInteger a) {
      return n <= 0 ? BigInteger.ONE : a.pow(n);
    }

  });

  /**
   * A monoid that adds big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalAdditionMonoid =
      monoidDef(new Definition<BigDecimal>() {
        @Override
        public BigDecimal empty() {
          return BigDecimal.ZERO;
        }

        @Override
        public BigDecimal append(BigDecimal a1, BigDecimal a2) {
          return a1.add(a2);
        }

        @Override
        public BigDecimal multiply(int n, BigDecimal a) {
          return n <= 0 ? BigDecimal.ZERO : a.multiply(BigDecimal.valueOf(n));
        }
      });

  /**
   * A monoid that multiplies big decimals.
   */
  public static final Monoid<BigDecimal> bigdecimalMultiplicationMonoid =
      monoidDef(new Definition<BigDecimal>() {
        @Override
        public BigDecimal empty() {
          return BigDecimal.ONE;
        }

        @Override
        public BigDecimal append(BigDecimal a1, BigDecimal a2) {
          return a1.multiply(a2);
        }

        @Override
        public BigDecimal multiply(int n, BigDecimal decimal) {
          return n <= 0 ? BigDecimal.ONE : decimal.pow(n);
        }
      });



  /**
   * A monoid that adds natural numbers.
   */
  public static final Monoid<Natural> naturalAdditionMonoid =
      monoidDef(new Definition<Natural>() {
        @Override
        public Natural empty() {
          return Natural.ZERO;
        }

        @Override
        public Natural append(Natural a1, Natural a2) {
          return a1.add(a2);
        }

        @Override
        public Natural multiply(int n, Natural a) {
          return natural(n).map(positiveN -> a.multiply(positiveN)).orSome(Natural.ZERO);
        }
      });

  /**
   * A monoid that multiplies natural numbers.
   */
  public static final Monoid<Natural> naturalMultiplicationMonoid =
      monoidDef(new Definition<Natural>() {
        @Override
        public Natural empty() {
          return Natural.ONE;
        }

        @Override
        public Natural append(Natural a1, Natural a2) {
          return a1.multiply(a2);
        }
      });

  /**
   * A monoid that adds longs.
   */
  public static final Monoid<Long> longAdditionMonoid = monoidDef(new Definition<Long>() {
    @Override
    public Long empty() {
      return 0L;
    }

    @Override
    public Long append(Long a1, Long a2) {
      return a1 + a2;
    }

    @Override
    public Long multiply(int n, Long a) {
      return n <= 0 ? 0L : n * a;
    }
  });

  /**
   * A monoid that multiplies longs.
   */
  public static final Monoid<Long> longMultiplicationMonoid = monoidDef(new Definition<Long>() {
    @Override
    public Long empty() {
      return 1L;
    }

    @Override
    public Long append(Long i1, Long i2) {
      return i1 * i2;
    }

    @Override
    public Long sum(F0<Stream<Long>> as) {
      long x = 1L;
      for (Stream<Long> xs = as.f(); x != 0L && !xs.isEmpty(); xs = xs.tail()._1()) {
        x *= xs.head();
      }
      return x;
    }

    @Override
    public Long multiply(int n, Long l) {
      return n <= 0 ? 1L : (long) StrictMath.pow(l.doubleValue(), n);
    }
  });

  /**
   * A monoid that ORs booleans.
   */
  public static final Monoid<Boolean> disjunctionMonoid = monoidDef(new Definition<Boolean>() {
    @Override
    public Boolean empty() {
      return false;
    }

    @Override
    public Boolean append(Boolean a1, Boolean a2) {
      return a1 | a2;
    }

    @Override
    public Boolean sum(F0<Stream<Boolean>> as) {
      return as.f().filter(identity()).isNotEmpty();
    }

    @Override
    public Boolean multiply(int n, Boolean a) {
      return n <= 0 ? false : a;
    }
  });

  /**
   * A monoid that XORs booleans.
   */
  public static final Monoid<Boolean> exclusiveDisjunctionMonoid = monoidDef(new Definition<Boolean>() {
    @Override
    public Boolean empty() {
      return false;
    }

    @Override
    public Boolean append(Boolean a1, Boolean a2) {
      return a1 ^ a2;
    }

    @Override
    public Boolean multiply(int n, Boolean a) {
      return a && (n == 1);
    }
  });

  /**
   * A monoid that ANDs booleans.
   */
  public static final Monoid<Boolean> conjunctionMonoid = monoidDef(new Definition<Boolean>() {
    @Override
    public Boolean empty() {
      return true;
    }

    @Override
    public Boolean append(Boolean a1, Boolean a2) {
      return a1 & a2;
    }

    @Override
    public Boolean multiply(int n, Boolean a) {
      return a;
    }

    @Override
    public Boolean sum(F0<Stream<Boolean>> as) {
      return as.f().filter(a -> !a).isEmpty();
    }
  });

  /**
   * A monoid that appends strings.
   */
  public static final Monoid<String> stringMonoid = monoidDef(new Definition<String>() {
    @Override
    public String empty() {
      return "";
    }

    @Override
    public String append(String a1, String a2) {
      return a1.concat(a2);
    }

    @Override
    public String sum(F0<Stream<String>> as) {
      StringBuilder sb = new StringBuilder();
      as.f().foreachDoEffect(sb::append);
      return sb.toString();
    }
  });

  /**
   * A monoid that appends string buffers.
   */
  public static final Monoid<StringBuffer> stringBufferMonoid = monoidDef((s1, s2) -> new StringBuffer(s1).append(s2),  new StringBuffer(0));

  /**
   * A monoid that appends string builders.
   */
  public static final Monoid<StringBuilder> stringBuilderMonoid = monoidDef((s1, s2) -> new StringBuilder(s1).append(s2),  new StringBuilder(0));

  /**
   * A monoid for functions.
   *
   * @param mb The monoid for the function codomain.
   * @return A monoid for functions.
   */
  public static <A, B> Monoid<F<A, B>> functionMonoid(final Monoid<B> mb) {
    Definition<B> mbDef = mb.def;
    return monoidDef(new Definition<F<A, B>>() {
      @Override
      public F<A, B> empty() {
        return __ -> mbDef.empty();
      }

      @Override
      public F<A, B> append(F<A, B> a1, F<A, B> a2) {
        return a -> mbDef.append(a1.f(a), a2.f(a));
      }
    });
  }

  /**
   * A monoid for lists.
   *
   * @return A monoid for lists.
   */
  public static <A> Monoid<List<A>> listMonoid() {
    return monoidDef(new Definition<List<A>>() {
      @Override
      public List<A> empty() {
        return nil();
      }

      @Override
      public List<A> append(List<A> a1, List<A> a2) {
        return a1.append(a2);
      }

      @Override
      public List<A> sum(F0<Stream<List<A>>> as) {
        return as.f().map(DList::listDList).foldLeft(DList::append, DList.<A>nil()).run();
      }
    });
  }

  /**
   * A monoid for options.
   * @deprecated since 4.7. Use {@link #firstOptionMonoid()}.
   *
   * @return A monoid for options.
   */
  @Deprecated
  public static <A> Monoid<Option<A>> optionMonoid() {
    return firstOptionMonoid();
  }

  /**
   * A monoid for options that take the first available value.
   *
   * @return A monoid for options that take the first available value.
   */
  public static <A> Monoid<Option<A>> firstOptionMonoid() {
    return monoidDef(new Definition<Option<A>>() {
      @Override
      public Option<A> empty() {
        return none();
      }

      @Override
      public Option<A> append(Option<A> a1, Option<A> a2) {
        return a1.orElse(a2);
      }

      @Override
      public F<Option<A>, Option<A>> prepend(Option<A> a1) {
        return a1.isSome() ? __ -> a1 : identity();
      }

      @Override
      public Option<A> multiply(int n, Option<A> as) {
        return as;
      }

      @Override
      public Option<A> sum(F0<Stream<Option<A>>> as) {
        return as.f().filter(Option.isSome_()).orHead(Option::none);
      }
    });
  }

  /**
   * A monoid for options that take the last available value.
   *
   * @return A monoid for options that take the last available value.
   */
  public static <A> Monoid<Option<A>> lastOptionMonoid() {
    return monoidDef(new Definition<Option<A>>() {
      @Override
      public Option<A> empty() {
        return none();
      }

      @Override
      public Option<A> append(Option<A> a1, Option<A> a2) {
        return a2.orElse(a1);
      }

      @Override
      public F<Option<A>, Option<A>> prepend(Option<A> a1) {
        return a1.isNone() ? identity() : a2 -> a2.orElse(a1);
      }

      @Override
      public Option<A> multiply(int n, Option<A> as) {
        return as;
      }
    });
  }

  /**
   * A monoid for streams.
   *
   * @return A monoid for streams.
   */
  public static <A> Monoid<Stream<A>> streamMonoid() {
    return monoidDef(new Definition<Stream<A>>() {
      @Override
      public Stream<A> empty() {
        return Stream.nil();
      }

      @Override
      public Stream<A> append(Stream<A> a1, Stream<A> a2) {
        return a1.append(a2);
      }

      @Override
      public Stream<A> sum(F0<Stream<Stream<A>>> as) {
        return Stream.join(as.f());
      }
    });
  }

  /**
   * A monoid for arrays.
   *
   * @return A monoid for arrays.
   */
  @SuppressWarnings("unchecked")
  public static <A> Monoid<Array<A>> arrayMonoid() {
    return monoidDef(new Definition<Array<A>>() {
      @Override
      public Array<A> empty() {
        return Array.empty();
      }

      @Override
      public Array<A> append(Array<A> a1, Array<A> a2) {
        return a1.append(a2);
      }
    });
  }

  /**
   * A monoid for IO values.
   */
  public static <A> Monoid<IO<A>> ioMonoid(final Monoid<A> ma) {
    Definition<A> maDef = ma.def;
    return monoidDef(new Definition<IO<A>>() {
      @Override
      public IO<A> empty() {
        return () -> maDef.empty();
      }

      @Override
      public IO<A> append(IO<A> a1, IO<A> a2) {
        return () -> maDef.append(a1.run(), a2.run());
      }
    });
  }

  /**
   * A monoid for the maximum of two integers.
   */
  public static final Monoid<Integer> intMaxMonoid = monoidDef(new Definition<Integer>() {
    @Override
    public Integer empty() {
      return Integer.MIN_VALUE;
    }

    @Override
    public Integer append(Integer a1, Integer a2) {
      return Math.max(a1, a2);
    }

    @Override
    public Integer multiply(int n, Integer a) {
      return a;
    }
  });

  /**
   * A monoid for the minimum of two integers.
   */
  public static final Monoid<Integer> intMinMonoid = monoidDef(new Definition<Integer>() {
    @Override
    public Integer empty() {
      return Integer.MAX_VALUE;
    }

    @Override
    public Integer append(Integer a1, Integer a2) {
      return Math.min(a1, a2);
    }

    @Override
    public Integer multiply(int n, Integer a) {
      return a;
    }
  });

  /**
   * A monoid for the Unit value.
   */
  public static final Monoid<Unit> unitMonoid = monoidDef(new Definition<Unit>() {
    @Override
    public Unit empty() {
      return unit();
    }

    @Override
    public Unit append(Unit a1, Unit a2) {
      return unit();
    }
  });

  /**
   * A monoid for sets.
   *
   * @param o An order for set elements.
   * @return A monoid for sets whose elements have the given order.
   */
  public static <A> Monoid<Set<A>> setMonoid(final Ord<A> o) {
    return monoidDef(new Definition<Set<A>>() {
      @Override
      public Set<A> empty() {
        return Set.empty(o);
      }

      @Override
      public Set<A> append(Set<A> a1, Set<A> a2) {
        return a1.union(a2);
      }
    });
  }

  /**
   * A monoid for the maximum of elements with ordering o.
   * @deprecated since 4.7. Use {@link Ord#maxMonoid(Object)}
   *
   * @param o An ordering of elements.
   * @param zero The minimum element.
   */
  @Deprecated
  public static <A> Monoid<A> ordMaxMonoid(final Ord<A> o, final A zero) {
    return o.maxMonoid(zero);
  }

}
