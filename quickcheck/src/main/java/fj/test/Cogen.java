package fj.test;

import fj.*;

import static fj.Function.curry;
import static fj.P.p;

import fj.data.*;

import static fj.data.Array.array;
import static fj.data.Array.iterableArray;
import static fj.data.List.fromString;
import static fj.data.List.nil;

import static fj.test.Variant.variant;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Float.floatToRawIntBits;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Transforms a type and a generator to produce a new generator. This function is used to generate
 * {@link Gen gen} functions.
 *
 * @version %build.number%
 */
public abstract class Cogen<A> {
  /**
   * Transforms the given value and generator to a new generator with a high probability of being
   * independent.
   *
   * @param a The value to produce the generator from.
   * @param g The generator to produce the new generator from.
   * @return A new generator with a high probability of being independent.
   */
  public abstract <B> Gen<B> cogen(A a, Gen<B> g);


  /**
   * A curried version of {@link #cogen(Object, Gen)}.
   *
   * @param a The value to produce the generator from.
   * @return A curried version of {@link #cogen(Object, Gen)}.
   */
  public final <B> F<Gen<B>, Gen<B>> cogen(final A a) {
    return g -> cogen(a, g);
  }

  /**
   * Composes the given function with this cogen to produce a new cogen.
   *
   * @param f The function to compose.
   * @return A new cogen composed with the given function.
   */
  public final <B> Cogen<B> compose(final F<B, A> f) {
    return new Cogen<B>() {
      public <X> Gen<X> cogen(final B b, final Gen<X> g) {
        return Cogen.this.cogen(f.f(b), g);
      }
    };
  }

  /**
   * Contra-maps this cogen using the given function.
   *
   * @param f The function to co-map with.
   * @return A contra-mapped cogen.
   */
  public final <B> Cogen<B> contramap(final F<B, A> f) {
    return new Cogen<B>() {
      public <X> Gen<X> cogen(final B b, final Gen<X> g) {
        return Cogen.this.cogen(f.f(b), g);
      }
    };
  }

  /**
   * A cogen for a function.
   *
   * @param a A gen for the domain of the function.
   * @param c A cogen for the codomain of the function.
   * @return A cogen for a function.
   */
  public static <A, B> Cogen<F<A, B>> cogenF(final Gen<A> a, final Cogen<B> c) {
    return new Cogen<F<A, B>>() {
      public <X> Gen<X> cogen(final F<A, B> f, final Gen<X> g) {
        return a.bind(a1 -> c.cogen(f.f(a1), g));
      }
    };
  }

  /**
   * A cogen for a function-2.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-2.
   */
  public static <A, B, C> Cogen<F2<A, B, C>> cogenF2(final Gen<A> aa, final Gen<B> ab,
                                                     final Cogen<C> c) {
    return new Cogen<F2<A, B, C>>() {
      public <X> Gen<X> cogen(final F2<A, B, C> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, c)).cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-3.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-3.
   */
  public static <A, B, C, D> Cogen<F3<A, B, C, D>> cogenF3(final Gen<A> aa, final Gen<B> ab,
                                                           final Gen<C> ac, final Cogen<D> c) {
    return new Cogen<F3<A, B, C, D>>() {
      public <X> Gen<X> cogen(final F3<A, B, C, D> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, c))).cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-4.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param ad A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-4.
   */
  public static <A, B, C, D, E> Cogen<F4<A, B, C, D, E>> cogenF4(final Gen<A> aa, final Gen<B> ab,
                                                                 final Gen<C> ac, final Gen<D> ad,
                                                                 final Cogen<E> c) {
    return new Cogen<F4<A, B, C, D, E>>() {
      public <X> Gen<X> cogen(final F4<A, B, C, D, E> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, cogenF(ad, c)))).cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-5.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param ad A gen for part of the domain of the function.
   * @param ae A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-5.
   */
  public static <A, B, C, D, E, F$> Cogen<F5<A, B, C, D, E, F$>> cogenF5(final Gen<A> aa,
                                                                         final Gen<B> ab,
                                                                         final Gen<C> ac,
                                                                         final Gen<D> ad,
                                                                         final Gen<E> ae,
                                                                         final Cogen<F$> c) {
    return new Cogen<F5<A, B, C, D, E, F$>>() {
      public <X> Gen<X> cogen(final F5<A, B, C, D, E, F$> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, cogenF(ad, cogenF(ae, c))))).cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-6.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param ad A gen for part of the domain of the function.
   * @param ae A gen for part of the domain of the function.
   * @param af A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-6.
   */
  public static <A, B, C, D, E, F$, G> Cogen<F6<A, B, C, D, E, F$, G>> cogenF6(final Gen<A> aa,
                                                                               final Gen<B> ab,
                                                                               final Gen<C> ac,
                                                                               final Gen<D> ad,
                                                                               final Gen<E> ae,
                                                                               final Gen<F$> af,
                                                                               final Cogen<G> c) {
    return new Cogen<F6<A, B, C, D, E, F$, G>>() {
      public <X> Gen<X> cogen(final F6<A, B, C, D, E, F$, G> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, cogenF(ad, cogenF(ae, cogenF(af, c)))))).cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-7.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param ad A gen for part of the domain of the function.
   * @param ae A gen for part of the domain of the function.
   * @param af A gen for part of the domain of the function.
   * @param ag A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Cogen<F7<A, B, C, D, E, F$, G, H>> cogenF7(final Gen<A> aa,
                                                                                     final Gen<B> ab,
                                                                                     final Gen<C> ac,
                                                                                     final Gen<D> ad,
                                                                                     final Gen<E> ae,
                                                                                     final Gen<F$> af,
                                                                                     final Gen<G> ag,
                                                                                     final Cogen<H> c) {
    return new Cogen<F7<A, B, C, D, E, F$, G, H>>() {
      public <X> Gen<X> cogen(final F7<A, B, C, D, E, F$, G, H> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, cogenF(ad, cogenF(ae, cogenF(af, cogenF(ag, c)))))))
            .cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for a function-8.
   *
   * @param aa A gen for part of the domain of the function.
   * @param ab A gen for part of the domain of the function.
   * @param ac A gen for part of the domain of the function.
   * @param ad A gen for part of the domain of the function.
   * @param ae A gen for part of the domain of the function.
   * @param af A gen for part of the domain of the function.
   * @param ag A gen for part of the domain of the function.
   * @param ah A gen for part of the domain of the function.
   * @param c  A cogen for the codomain of the function.
   * @return A cogen for a function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Cogen<F8<A, B, C, D, E, F$, G, H, I>> cogenF8(final Gen<A> aa,
                                                                                           final Gen<B> ab,
                                                                                           final Gen<C> ac,
                                                                                           final Gen<D> ad,
                                                                                           final Gen<E> ae,
                                                                                           final Gen<F$> af,
                                                                                           final Gen<G> ag,
                                                                                           final Gen<H> ah,
                                                                                           final Cogen<I> c) {
    return new Cogen<F8<A, B, C, D, E, F$, G, H, I>>() {
      public <X> Gen<X> cogen(final F8<A, B, C, D, E, F$, G, H, I> f, final Gen<X> g) {
        return cogenF(aa, cogenF(ab, cogenF(ac, cogenF(ad, cogenF(ae, cogenF(af, cogenF(ag, cogenF(ah, c))))))))
            .cogen(curry(f), g);
      }
    };
  }

  /**
   * A cogen for booleans.
   */
  public static final Cogen<Boolean> cogenBoolean = new Cogen<Boolean>() {
    public <B> Gen<B> cogen(final Boolean b, final Gen<B> g) {
      return variant(b ? 0 : 1, g);
    }
  };

  /**
   * A cogen for integers.
   */
  public static final Cogen<Integer> cogenInteger = new Cogen<Integer>() {
    public <B> Gen<B> cogen(final Integer i, final Gen<B> g) {
      return variant(i >= 0 ? 2 * i : -2 * i + 1, g);
    }
  };

  /**
   * A cogen for bytes.
   */
  public static final Cogen<Byte> cogenByte = new Cogen<Byte>() {
    public <B> Gen<B> cogen(final Byte b, final Gen<B> g) {
      return variant(b >= 0 ? 2 * b : -2 * b + 1, g);
    }
  };

  /**
   * A cogen for shorts.
   */
  public static final Cogen<Short> cogenShort = new Cogen<Short>() {
    public <B> Gen<B> cogen(final Short s, final Gen<B> g) {
      return variant(s >= 0 ? 2 * s : -2 * s + 1, g);
    }
  };

  /**
   * A cogen for longs.
   */
  public static final Cogen<Long> cogenLong = new Cogen<Long>() {
    public <B> Gen<B> cogen(final Long l, final Gen<B> g) {
      return variant(l >= 0L ? 2L * l : -2L * l + 1L, g);
    }
  };

  /**
   * A cogen for characters.
   */
  public static final Cogen<Character> cogenCharacter = new Cogen<Character>() {
    public <B> Gen<B> cogen(final Character c, final Gen<B> g) {
      return variant(c << 1, g);
    }
  };

  /**
   * A cogen for floats.
   */
  public static final Cogen<Float> cogenFloat = new Cogen<Float>() {
    public <B> Gen<B> cogen(final Float f, final Gen<B> g) {
      return cogenInteger.cogen(floatToRawIntBits(f), g);
    }
  };

  /**
   * A cogen for doubles.
   */
  public static final Cogen<Double> cogenDouble = new Cogen<Double>() {
    public <B> Gen<B> cogen(final Double d, final Gen<B> g) {
      return cogenLong.cogen(doubleToRawLongBits(d), g);
    }
  };

  /**
   * A cogen for the optional value.
   *
   * @param ca A cogen for the type of the optional value.
   * @return A cogen for the optional value.
   */
  public static <A> Cogen<Option<A>> cogenOption(final Cogen<A> ca) {
    return new Cogen<Option<A>>() {
      public <B> Gen<B> cogen(final Option<A> o, final Gen<B> g) {
        return o.isNone() ? variant(0, g) : variant(1, ca.cogen(o.some(), g));
      }
    };
  }

  /**
   * A cogen for the disjoint union.
   *
   * @param ca A cogen for one side of the disjoint union.
   * @param cb A cogen for one side of the disjoint union.
   * @return A cogen for the disjoint union.
   */
  public static <A, B> Cogen<Either<A, B>> cogenEither(final Cogen<A> ca, final Cogen<B> cb) {
    return new Cogen<Either<A, B>>() {
      public <X> Gen<X> cogen(final Either<A, B> e, final Gen<X> g) {
        return e.isLeft() ?
            variant(0, ca.cogen(e.left().value(), g)) :
            variant(1, cb.cogen(e.right().value(), g));
      }
    };
  }

  /**
   * A cogen for lists.
   *
   * @param ca A cogen for the elements of the list.
   * @return A cogen for lists.
   */
  public static <A> Cogen<List<A>> cogenList(final Cogen<A> ca) {
    return new Cogen<List<A>>() {
      public <B> Gen<B> cogen(final List<A> as, final Gen<B> g) {
        return as.isEmpty() ?
            variant(0, g) :
            variant(1, ca.cogen(as.head(), cogen(as.tail(), g)));
      }
    };
  }

  /**
   * A cogen for strings.
   */
  public static final Cogen<String> cogenString = new Cogen<String>() {
    public <B> Gen<B> cogen(final String s, final Gen<B> g) {
      return cogenList(cogenCharacter).cogen(fromString(s), g);
    }
  };

  /**
   * A cogen for string buffers.
   */
  public static final Cogen<StringBuffer> cogenStringBuffer = new Cogen<StringBuffer>() {
    public <B> Gen<B> cogen(final StringBuffer s, final Gen<B> g) {
      return cogenString.cogen(s.toString(), g);
    }
  };

  /**
   * A cogen for string builders.
   */
  public static final Cogen<StringBuilder> cogenStringBuilder = new Cogen<StringBuilder>() {
    public <B> Gen<B> cogen(final StringBuilder s, final Gen<B> g) {
      return cogenString.cogen(s.toString(), g);
    }
  };

  /**
   * A cogen for streams.
   *
   * @param ca A cogen for the elements of the stream.
   * @return A cogen for streams.
   */
  public static <A> Cogen<Stream<A>> cogenStream(final Cogen<A> ca) {
    return new Cogen<Stream<A>>() {
      public <B> Gen<B> cogen(final Stream<A> as, final Gen<B> g) {
        return as.isEmpty() ?
            variant(0, g) :
            variant(1, ca.cogen(as.head(), cogen(as.tail()._1(), g)));
      }
    };
  }

  /**
   * A cogen for the provided LcgRng
   * @return A cogen for the provided LcgRng.
   */
  public static Cogen<LcgRng> cogenLcgRng() {
    return new Cogen<LcgRng>() {
      @Override
      public <B> Gen<B> cogen(LcgRng rng, Gen<B> g) {
        long i = rng.getSeed();
        return variant(i >= 0 ? 2 * i : -2 * i + 1, g);
      }
    };
  }

  /**
   * A cogen for state.
   */
  public static <S, A> Cogen<State<S, A>> cogenState(Gen<S> as, F2<S, A, Long> f) {
    return new Cogen<State<S, A>>() {
      @Override
      public <B> Gen<B> cogen(State<S, A> s1, Gen<B> g) {
        return as.bind(r -> {
          P2<S, A> p = s1.run(r);
          return variant(f.f(p._1(), p._2()), g);
        });
      }
    };
  }

  /**
   * A cogen for arrays.
   *
   * @param ca A cogen for the elements of the array.
   * @return A cogen for arrays.
   */
  public static <A> Cogen<Array<A>> cogenArray(final Cogen<A> ca) {
    return new Cogen<Array<A>>() {
      public <B> Gen<B> cogen(final Array<A> as, final Gen<B> g) {
        return cogenList(ca).cogen(as.toList(), g);
      }
    };
  }

  /**
   * A cogen for throwables.
   *
   * @param cs A cogen for the throwable message.
   * @return A cogen for throwables.
   */
  public static Cogen<Throwable> cogenThrowable(final Cogen<String> cs) {
    return cs.contramap(new F<Throwable, String>() {
      public String f(final Throwable t) {
        return t.getMessage();
      }
    });
  }

  /**
   * A cogen for throwables.
   */
  public static final Cogen<Throwable> cogenThrowable =
      cogenThrowable(cogenString);

  // BEGIN java.util

  /**
   * A cogen for array lists.
   *
   * @param ca A cogen for the elements of the array list.
   * @return A cogen for array lists.
   */
  public static <A> Cogen<ArrayList<A>> cogenArrayList(final Cogen<A> ca) {
    return new Cogen<ArrayList<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final ArrayList<A> as, final Gen<B> g) {
        return cogenArray(ca).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for bit sets.
   */
  public static final Cogen<BitSet> cogenBitSet = new Cogen<BitSet>() {
    public <B> Gen<B> cogen(final BitSet s, final Gen<B> g) {
      List<Boolean> x = nil();

      for (int i = 0; i < s.size(); i++) {
        x = x.snoc(s.get(i));
      }

      return cogenList(cogenBoolean).cogen(x, g);
    }
  };

  /**
   * A cogen for calendars.
   */
  public static final Cogen<Calendar> cogenCalendar = new Cogen<Calendar>() {
    public <B> Gen<B> cogen(final Calendar c, final Gen<B> g) {
      return cogenLong.cogen(c.getTime().getTime(), g);
    }
  };

  /**
   * A cogen for dates.
   */
  public static final Cogen<Date> cogenDate = new Cogen<Date>() {
    public <B> Gen<B> cogen(final Date d, final Gen<B> g) {
      return cogenLong.cogen(d.getTime(), g);
    }
  };

  /**
   * A cogen for enum maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for enum maps.
   */
  public static <K extends Enum<K>, V> Cogen<EnumMap<K, V>> cogenEnumMap(final Cogen<K> ck,
                                                                         final Cogen<V> cv) {
    return new Cogen<EnumMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final EnumMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for enum sets.
   *
   * @param c A cogen for the elements of the enum set.
   * @return A cogen for enum sets.
   */
  public static <A extends Enum<A>> Cogen<EnumSet<A>> cogenEnumSet(final Cogen<A> c) {
    return new Cogen<EnumSet<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final EnumSet<A> as, final Gen<B> g) {
        return cogenHashSet(c).cogen(new HashSet<>(as), g);
      }
    };
  }

  /**
   * A cogen for gregorian calendars.
   */
  public static final Cogen<GregorianCalendar> cogenGregorianCalendar = new Cogen<GregorianCalendar>() {
    public <B> Gen<B> cogen(final GregorianCalendar c, final Gen<B> g) {
      return cogenLong.cogen(c.getTime().getTime(), g);
    }
  };

  /**
   * A cogen for hash maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for hash maps.
   */
  public static <K, V> Cogen<HashMap<K, V>> cogenHashMap(final Cogen<K> ck, final Cogen<V> cv) {
    return new Cogen<HashMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final HashMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for hash sets.
   *
   * @param c A cogen for the elements of the hash set.
   * @return A cogen for hash sets.
   */
  public static <A> Cogen<HashSet<A>> cogenHashSet(final Cogen<A> c) {
    return new Cogen<HashSet<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final HashSet<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for hash tables.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for hash tables.
   */
  public static <K, V> Cogen<Hashtable<K, V>> cogenHashtable(final Cogen<K> ck, final Cogen<V> cv) {
    return new Cogen<Hashtable<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final Hashtable<K, V> h, final Gen<B> g) {
        List<P2<K, V>> x = nil();

        for (final Map.Entry<K, V> entry : h.entrySet()) {
          x = x.snoc(p(entry.getKey(), entry.getValue()));
        }

        return cogenList(cogenP2(ck, cv)).cogen(x, g);
      }
    };
  }

  /**
   * A cogen for identity hash maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for identity hash maps.
   */
  public static <K, V> Cogen<IdentityHashMap<K, V>> cogenIdentityHashMap(final Cogen<K> ck,
                                                                         final Cogen<V> cv) {
    return new Cogen<IdentityHashMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final IdentityHashMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for linked hash maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for linked hash maps.
   */
  public static <K, V> Cogen<LinkedHashMap<K, V>> cogenLinkedHashMap(final Cogen<K> ck,
                                                                     final Cogen<V> cv) {
    return new Cogen<LinkedHashMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final LinkedHashMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for linked hash sets.
   *
   * @param c A cogen for the elements of the linked hash set.
   * @return A cogen for linked hash sets.
   */
  public static <A> Cogen<LinkedHashSet<A>> cogenLinkedHashSet(final Cogen<A> c) {
    return new Cogen<LinkedHashSet<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final LinkedHashSet<A> as, final Gen<B> g) {
        return cogenHashSet(c).cogen(new HashSet<>(as), g);
      }
    };
  }

  /**
   * A cogen for linked lists.
   *
   * @param c A cogen for the elements of the linked list.
   * @return A cogen for linked lists.
   */
  public static <A> Cogen<LinkedList<A>> cogenLinkedList(final Cogen<A> c) {
    return new Cogen<LinkedList<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final LinkedList<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for priority queues.
   *
   * @param c A cogen for the elements of the priority queue.
   * @return A cogen for priority queues.
   */
  public static <A> Cogen<PriorityQueue<A>> cogenPriorityQueue(final Cogen<A> c) {
    return new Cogen<PriorityQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final PriorityQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for properties.
   */
  public static final Cogen<Properties> cogenProperties = new Cogen<Properties>() {
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public <B> Gen<B> cogen(final Properties p, final Gen<B> g) {
      final Hashtable<String, String> t = new Hashtable<>();

      for (final Object s : p.keySet()) {
        t.put((String) s, p.getProperty((String) s));
      }

      return cogenHashtable(cogenString, cogenString).cogen(t, g);
    }
  };

  /**
   * A cogen for stacks.
   *
   * @param c A cogen for the elements of the stack.
   * @return A cogen for stacks.
   */
  public static <A> Cogen<Stack<A>> cogenStack(final Cogen<A> c) {
    return new Cogen<Stack<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final Stack<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for tree maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for tree maps.
   */
  public static <K, V> Cogen<TreeMap<K, V>> cogenTreeMap(final Cogen<K> ck, final Cogen<V> cv) {
    return new Cogen<TreeMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final TreeMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for tree sets.
   *
   * @param c A cogen for the elements of the tree set.
   * @return A cogen for tree sets.
   */
  public static <A> Cogen<TreeSet<A>> cogenTreeSet(final Cogen<A> c) {
    return new Cogen<TreeSet<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final TreeSet<A> as, final Gen<B> g) {
        return cogenHashSet(c).cogen(new HashSet<>(as), g);
      }
    };
  }

  /**
   * A cogen for vectors.
   *
   * @param c A cogen for the elements of the vector.
   * @return A cogen for vectors.
   */
  public static <A> Cogen<Vector<A>> cogenVector(final Cogen<A> c) {
    return new Cogen<Vector<A>>() {
      @SuppressWarnings({"unchecked", "UseOfObsoleteCollectionType"})
      public <B> Gen<B> cogen(final Vector<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for weak hash maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for weak hash maps.
   */
  public static <K, V> Cogen<WeakHashMap<K, V>> cogenWeakHashMap(final Cogen<K> ck,
                                                                 final Cogen<V> cv) {
    return new Cogen<WeakHashMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final WeakHashMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  // END java.util

  // BEGIN java.util.concurrent

  /**
   * A cogen for array blocking queues.
   *
   * @param c A cogen for the elements of the array blocking queue.
   * @return A cogen for array blocking queues.
   */
  public static <A> Cogen<ArrayBlockingQueue<A>> cogenArrayBlockingQueue(final Cogen<A> c) {
    return new Cogen<ArrayBlockingQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final ArrayBlockingQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for concurrent hash maps.
   *
   * @param ck A cogen for the map keys.
   * @param cv A cogen for the map values.
   * @return A cogen for concurrent hash maps.
   */
  public static <K, V> Cogen<ConcurrentHashMap<K, V>> cogenConcurrentHashMap(final Cogen<K> ck,
                                                                             final Cogen<V> cv) {
    return new Cogen<ConcurrentHashMap<K, V>>() {
      @SuppressWarnings("UseOfObsoleteCollectionType")
      public <B> Gen<B> cogen(final ConcurrentHashMap<K, V> m, final Gen<B> g) {
        return cogenHashtable(ck, cv).cogen(new Hashtable<>(m), g);
      }
    };
  }

  /**
   * A cogen for concurrent linked queues.
   *
   * @param c A cogen for the elements of the concurrent linked queue.
   * @return A cogen for concurrent linked queues.
   */
  public static <A> Cogen<ConcurrentLinkedQueue<A>> cogenConcurrentLinkedQueue(final Cogen<A> c) {
    return new Cogen<ConcurrentLinkedQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final ConcurrentLinkedQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for copy-on-write array lists.
   *
   * @param c A cogen for the elements of the copy-on-write array list.
   * @return A cogen for copy-on-write array lists.
   */
  public static <A> Cogen<CopyOnWriteArrayList<A>> cogenCopyOnWriteArrayList(final Cogen<A> c) {
    return new Cogen<CopyOnWriteArrayList<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final CopyOnWriteArrayList<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for copy-on-write array sets.
   *
   * @param c A cogen for the elements of the copy-on-write array set.
   * @return A cogen for copy-on-write array sets.
   */
  public static <A> Cogen<CopyOnWriteArraySet<A>> cogenCopyOnWriteArraySet(final Cogen<A> c) {
    return new Cogen<CopyOnWriteArraySet<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final CopyOnWriteArraySet<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for delay queues.
   *
   * @param c A cogen for the elements of the delay queue.
   * @return A cogen for delay queues.
   */
  public static <A extends Delayed> Cogen<DelayQueue<A>> cogenDelayQueue(final Cogen<A> c) {
    return new Cogen<DelayQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final DelayQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for linked blocking queues.
   *
   * @param c A cogen for the elements of the linked blocking queue.
   * @return A cogen for linked blocking queues.
   */
  public static <A> Cogen<LinkedBlockingQueue<A>> cogenLinkedBlockingQueue(final Cogen<A> c) {
    return new Cogen<LinkedBlockingQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final LinkedBlockingQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for priority blocking queues.
   *
   * @param c A cogen for the elements of the priority blocking queue.
   * @return A cogen for priority blocking queues.
   */
  public static <A> Cogen<PriorityBlockingQueue<A>> cogenPriorityBlockingQueue(final Cogen<A> c) {
    return new Cogen<PriorityBlockingQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final PriorityBlockingQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  /**
   * A cogen for synchronous queues.
   *
   * @param c A cogen for the elements of the synchronous queue.
   * @return A cogen for synchronous queues.
   */
  public static <A> Cogen<SynchronousQueue<A>> cogenSynchronousQueue(final Cogen<A> c) {
    return new Cogen<SynchronousQueue<A>>() {
      @SuppressWarnings("unchecked")
      public <B> Gen<B> cogen(final SynchronousQueue<A> as, final Gen<B> g) {
        return cogenArray(c).cogen(iterableArray(as), g);
      }
    };
  }

  // END java.util.concurrent

  // BEGIN java.sql

  public static final Cogen<java.sql.Date> cogenSQLDate = new Cogen<java.sql.Date>() {
    public <B> Gen<B> cogen(final java.sql.Date d, final Gen<B> g) {
      return cogenLong.cogen(d.getTime(), g);
    }
  };

  public static final Cogen<Timestamp> cogenTimestamp = new Cogen<Timestamp>() {
    public <B> Gen<B> cogen(final Timestamp t, final Gen<B> g) {
      return cogenLong.cogen(t.getTime(), g);
    }
  };

  public static final Cogen<Time> cogenTime = new Cogen<Time>() {
    public <B> Gen<B> cogen(final Time t, final Gen<B> g) {
      return cogenLong.cogen(t.getTime(), g);
    }
  };

  // END java.sql

  // BEGIN java.math

  public static final Cogen<BigInteger> cogenBigInteger = new Cogen<BigInteger>() {
    public <B> Gen<B> cogen(final BigInteger i, final Gen<B> g) {
      return variant((i.compareTo(BigInteger.ZERO) >= 0 ?
          i.multiply(BigInteger.valueOf(2L)) :
          i.multiply(BigInteger.valueOf(-2L).add(BigInteger.ONE))).longValue(), g);
    }
  };

  public static final Cogen<BigDecimal> cogenBigDecimal = new Cogen<BigDecimal>() {
    public <B> Gen<B> cogen(final BigDecimal d, final Gen<B> g) {
      return variant((d.compareTo(BigDecimal.ZERO) >= 0 ?
          d.multiply(BigDecimal.valueOf(2L)) :
          d.multiply(BigDecimal.valueOf(-2L).add(BigDecimal.ONE))).longValue(), g);
    }
  };

  // END java.math

  /**
   * A cogen for product-1 values.
   *
   * @param ca A cogen for one of the types over which the product-1 is defined.
   * @return A cogen for product-1 values.
   */
  public static <A> Cogen<P1<A>> cogenP1(final Cogen<A> ca) {
    return new Cogen<P1<A>>() {
      public <B> Gen<B> cogen(final P1<A> p, final Gen<B> g) {
        return ca.cogen(p._1(), g);
      }
    };
  }

  /**
   * A cogen for product-2 values.
   *
   * @param ca A cogen for one of the types over which the product-2 is defined.
   * @param cb A cogen for one of the types over which the product-2 is defined.
   * @return A cogen for product-2 values.
   */
  public static <A, B> Cogen<P2<A, B>> cogenP2(final Cogen<A> ca, final Cogen<B> cb) {
    return new Cogen<P2<A, B>>() {
      public <X> Gen<X> cogen(final P2<A, B> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(), g));
      }
    };
  }

  /**
   * A cogen for product-3 values.
   *
   * @param ca A cogen for one of the types over which the product-3 is defined.
   * @param cb A cogen for one of the types over which the product-3 is defined.
   * @param cc A cogen for one of the types over which the product-3 is defined.
   * @return A cogen for product-3 values.
   */
  public static <A, B, C> Cogen<P3<A, B, C>> cogenP3(final Cogen<A> ca, final Cogen<B> cb,
                                                     final Cogen<C> cc) {
    return new Cogen<P3<A, B, C>>() {
      public <X> Gen<X> cogen(final P3<A, B, C> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(), cc.cogen(p._3(), g)));
      }
    };
  }

  /**
   * A cogen for product-4 values.
   *
   * @param ca A cogen for one of the types over which the product-4 is defined.
   * @param cb A cogen for one of the types over which the product-4 is defined.
   * @param cc A cogen for one of the types over which the product-4 is defined.
   * @param cd A cogen for one of the types over which the product-4 is defined.
   * @return A cogen for product-4 values.
   */
  public static <A, B, C, D> Cogen<P4<A, B, C, D>> cogenP4(final Cogen<A> ca, final Cogen<B> cb,
                                                           final Cogen<C> cc, final Cogen<D> cd) {
    return new Cogen<P4<A, B, C, D>>() {
      public <X> Gen<X> cogen(final P4<A, B, C, D> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(), cc.cogen(p._3(), cd.cogen(p._4(), g))));
      }
    };
  }

  /**
   * A cogen for product-5 values.
   *
   * @param ca A cogen for one of the types over which the product-5 is defined.
   * @param cb A cogen for one of the types over which the product-5 is defined.
   * @param cc A cogen for one of the types over which the product-5 is defined.
   * @param cd A cogen for one of the types over which the product-5 is defined.
   * @param ce A cogen for one of the types over which the product-5 is defined.
   * @return A cogen for product-5 values.
   */
  public static <A, B, C, D, E> Cogen<P5<A, B, C, D, E>> cogenP5(final Cogen<A> ca, final Cogen<B> cb,
                                                                 final Cogen<C> cc, final Cogen<D> cd,
                                                                 final Cogen<E> ce) {
    return new Cogen<P5<A, B, C, D, E>>() {
      public <X> Gen<X> cogen(final P5<A, B, C, D, E> p, final Gen<X> g) {
        return ca.cogen(p._1(),
            cb.cogen(p._2(), cc.cogen(p._3(), cd.cogen(p._4(), ce.cogen(p._5(), g)))));
      }
    };
  }

  /**
   * A cogen for product-6 values.
   *
   * @param ca A cogen for one of the types over which the product-6 is defined.
   * @param cb A cogen for one of the types over which the product-6 is defined.
   * @param cc A cogen for one of the types over which the product-6 is defined.
   * @param cd A cogen for one of the types over which the product-6 is defined.
   * @param ce A cogen for one of the types over which the product-6 is defined.
   * @param cf A cogen for one of the types over which the product-6 is defined.
   * @return A cogen for product-6 values.
   */
  public static <A, B, C, D, E, F$> Cogen<P6<A, B, C, D, E, F$>> cogenP6(final Cogen<A> ca,
                                                                         final Cogen<B> cb,
                                                                         final Cogen<C> cc,
                                                                         final Cogen<D> cd,
                                                                         final Cogen<E> ce,
                                                                         final Cogen<F$> cf) {
    return new Cogen<P6<A, B, C, D, E, F$>>() {
      public <X> Gen<X> cogen(final P6<A, B, C, D, E, F$> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(),
            cc.cogen(p._3(), cd.cogen(p._4(), ce.cogen(p._5(), cf.cogen(p._6(), g))))));
      }
    };
  }

  /**
   * A cogen for product-7 values.
   *
   * @param ca A cogen for one of the types over which the product-7 is defined.
   * @param cb A cogen for one of the types over which the product-7 is defined.
   * @param cc A cogen for one of the types over which the product-7 is defined.
   * @param cd A cogen for one of the types over which the product-7 is defined.
   * @param ce A cogen for one of the types over which the product-7 is defined.
   * @param cf A cogen for one of the types over which the product-7 is defined.
   * @param cg A cogen for one of the types over which the product-7 is defined.
   * @return A cogen for product-7 values.
   */
  public static <A, B, C, D, E, F$, G> Cogen<P7<A, B, C, D, E, F$, G>> cogenP7(final Cogen<A> ca,
                                                                               final Cogen<B> cb,
                                                                               final Cogen<C> cc,
                                                                               final Cogen<D> cd,
                                                                               final Cogen<E> ce,
                                                                               final Cogen<F$> cf,
                                                                               final Cogen<G> cg) {
    return new Cogen<P7<A, B, C, D, E, F$, G>>() {
      public <X> Gen<X> cogen(final P7<A, B, C, D, E, F$, G> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(), cc.cogen(p._3(),
            cd.cogen(p._4(), ce.cogen(p._5(), cf.cogen(p._6(), cg.cogen(p._7(), g)))))));
      }
    };
  }

  /**
   * A cogen for product-8 values.
   *
   * @param ca A cogen for one of the types over which the product-8 is defined.
   * @param cb A cogen for one of the types over which the product-8 is defined.
   * @param cc A cogen for one of the types over which the product-8 is defined.
   * @param cd A cogen for one of the types over which the product-8 is defined.
   * @param ce A cogen for one of the types over which the product-8 is defined.
   * @param cf A cogen for one of the types over which the product-8 is defined.
   * @param cg A cogen for one of the types over which the product-8 is defined.
   * @param ch A cogen for one of the types over which the product-8 is defined.
   * @return A cogen for product-8 values.
   */
  public static <A, B, C, D, E, F$, G, H> Cogen<P8<A, B, C, D, E, F$, G, H>> cogenP8(final Cogen<A> ca,
                                                                                     final Cogen<B> cb,
                                                                                     final Cogen<C> cc,
                                                                                     final Cogen<D> cd,
                                                                                     final Cogen<E> ce,
                                                                                     final Cogen<F$> cf,
                                                                                     final Cogen<G> cg,
                                                                                     final Cogen<H> ch) {
    return new Cogen<P8<A, B, C, D, E, F$, G, H>>() {
      public <X> Gen<X> cogen(final P8<A, B, C, D, E, F$, G, H> p, final Gen<X> g) {
        return ca.cogen(p._1(), cb.cogen(p._2(), cc.cogen(p._3(), cd.cogen(p._4(),
            ce.cogen(p._5(), cf.cogen(p._6(), cg.cogen(p._7(), ch.cogen(p._8(), g))))))));
      }
    };
  }
}
