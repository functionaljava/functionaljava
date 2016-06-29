package fj.test;

import fj.Equal;
import fj.F;
import fj.F1Functions;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.Function;
import fj.Bottom;

import static fj.Equal.longEqual;
import static fj.Function.compose;
import static fj.P.p;

import fj.P;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;
import fj.data.*;
import fj.LcgRng;
import fj.Ord;

import static fj.data.Either.left;
import static fj.data.Either.right;
import static fj.data.Enumerator.charEnumerator;
import static fj.data.List.asString;
import static fj.data.List.list;
import static fj.data.Option.some;

import fj.data.List;
import fj.data.Set;
import fj.data.TreeMap;
import fj.function.Booleans;
import fj.function.Effect1;
import fj.function.Longs;

import static fj.data.Stream.range;
import static fj.function.Booleans.not;
import static fj.test.Gen.choose;
import static fj.test.Gen.elements;
import static fj.test.Gen.fail;
import static fj.test.Gen.frequency;
import static fj.test.Gen.listOf;
import static fj.test.Gen.oneOf;
import static fj.test.Gen.promote;
import static fj.test.Gen.sized;
import static fj.test.Gen.value;

import static java.lang.Math.abs;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static java.util.Locale.getAvailableLocales;
import static java.util.EnumSet.copyOf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
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
 * Common Gen helper functions.
 *
 * @version %build.number%
 */
public final class Arbitrary {

  /**
   * An arbitrary for functions.
   *
   * @param c The cogen for the function domain.
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Gen<F<A, B>> arbF(final Cogen<A> c, final Gen<B> a) {
    return promote(new F<A, Gen<B>>() {
      public Gen<B> f(final A x) {
        return c.cogen(x, a);
      }
    });
  }

    public static <A, B> Gen<Reader<A, B>> arbReader(Cogen<A> aa, Gen<B> ab) {
        return arbF(aa, ab).map(Reader::unit);
    }

    /**
     * An arbitrary for state.
     */
    public static <S, A> Gen<State<S, A>> arbState(Gen<S> as, Cogen<S> cs, Gen<A> aa) {
        return arbF(cs, arbP2(as, aa)).map(State::unit);
    }

    /**
     * An arbitrary for the LcgRng.
     */
    public static <A> Gen<LcgRng> arbLcgRng() {
        return Arbitrary.arbLong.map(LcgRng::new);
    }

  /**
   * An arbitrary for functions.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Gen<F<A, B>> arbFInvariant(final Gen<B> a) {
    return a.map(Function.constant());
  }

  /**
   * An arbitrary for function-2.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Gen<F2<A, B, C>> arbF2(final Cogen<A> ca, final Cogen<B> cb,
                                                 final Gen<C> a) {
    return arbF(ca, arbF(cb, a)).map(Function.uncurryF2());
  }

  /**
   * An arbitrary for function-2.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Gen<F2<A, B, C>> arbF2Invariant(final Gen<C> a) {
    return a.map(compose(Function.uncurryF2(), compose(Function.constant(), Function.constant())));
  }

  /**
   * An arbitrary for function-3.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Gen<F3<A, B, C, D>> arbF3(final Cogen<A> ca, final Cogen<B> cb,
                                                       final Cogen<C> cc, final Gen<D> a) {
    return arbF(ca, arbF(cb, arbF(cc, a))).map(Function.uncurryF3());
  }

  /**
   * An arbitrary for function-3.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Gen<F3<A, B, C, D>> arbF3Invariant(final Gen<D> a) {
    return a.map(compose(Function.uncurryF3(), compose(Function.constant(),
                                                                                 compose(
                                                                                     Function.constant(),
                                                                                     Function.constant()))));
  }

  /**
   * An arbitrary for function-4.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param cd A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Gen<F4<A, B, C, D, E>> arbF4(final Cogen<A> ca, final Cogen<B> cb,
                                                             final Cogen<C> cc, final Cogen<D> cd,
                                                             final Gen<E> a) {
    return arbF(ca, arbF(cb, arbF(cc, arbF(cd, a)))).map(Function.uncurryF4());
  }

  /**
   * An arbitrary for function-4.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Gen<F4<A, B, C, D, E>> arbF4Invariant(final Gen<E> a) {
    return a.map(compose(Function.uncurryF4(),
                                       compose(Function.constant(),
                                               compose(Function.constant(),
                                                       compose(Function.constant(),
                                                               Function.constant())))));
  }

  /**
   * An arbitrary for function-5.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param cd A cogen for the part of the domain of the function.
   * @param ce A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Gen<F5<A, B, C, D, E, F$>> arbF5(final Cogen<A> ca,
                                                                     final Cogen<B> cb,
                                                                     final Cogen<C> cc,
                                                                     final Cogen<D> cd,
                                                                     final Cogen<E> ce,
                                                                     final Gen<F$> a) {
    return arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, a))))).map(Function.uncurryF5());
  }

  /**
   * An arbitrary for function-5.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Gen<F5<A, B, C, D, E, F$>> arbF5Invariant(final Gen<F$> a) {
    return a.map(compose(Function.uncurryF5(),
                                       compose(Function.constant(),
                                               compose(Function.constant(),
                                                       compose(Function.constant(),
                                                               compose(Function.constant(),
                                                                       Function.constant()))))));
  }

  /**
   * An arbitrary for function-6.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param cd A cogen for the part of the domain of the function.
   * @param ce A cogen for the part of the domain of the function.
   * @param cf A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Gen<F6<A, B, C, D, E, F$, G>> arbF6(final Cogen<A> ca,
                                                                           final Cogen<B> cb,
                                                                           final Cogen<C> cc,
                                                                           final Cogen<D> cd,
                                                                           final Cogen<E> ce,
                                                                           final Cogen<F$> cf,
                                                                           final Gen<G> a) {
    return arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, a)))))).map(
        Function.uncurryF6());
  }

  /**
   * An arbitrary for function-6.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Gen<F6<A, B, C, D, E, F$, G>> arbF6Invariant(final Gen<G> a) {
    return a.map(compose(Function.<A, B, C, D, E, F$, G>uncurryF6(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, G>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, G>>>>constant(),
                    compose(Function.<D, F<E, F<F$, G>>>constant(),
                        compose(Function.<E, F<F$, G>>constant(),
                            Function.<F$, G>constant())))))));
  }

  /**
   * An arbitrary for function-7.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param cd A cogen for the part of the domain of the function.
   * @param ce A cogen for the part of the domain of the function.
   * @param cf A cogen for the part of the domain of the function.
   * @param cg A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Gen<F7<A, B, C, D, E, F$, G, H>> arbF7(final Cogen<A> ca,
                                                                                 final Cogen<B> cb,
                                                                                 final Cogen<C> cc,
                                                                                 final Cogen<D> cd,
                                                                                 final Cogen<E> ce,
                                                                                 final Cogen<F$> cf,
                                                                                 final Cogen<G> cg,
                                                                                 final Gen<H> a) {
    return arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, a))))))).map(
        Function.uncurryF7());
  }

  /**
   * An arbitrary for function-7.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Gen<F7<A, B, C, D, E, F$, G, H>> arbF7Invariant(final Gen<H> a) {
    return a.map(compose(Function.<A, B, C, D, E, F$, G, H>uncurryF7(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, F<G, H>>>>>constant(),
                    compose(Function.<D, F<E, F<F$, F<G, H>>>>constant(),
                        compose(Function.<E, F<F$, F<G, H>>>constant(),
                            compose(Function.<F$, F<G, H>>constant(),
                                Function.<G, H>constant()))))))));
  }

  /**
   * An arbitrary for function-8.
   *
   * @param ca A cogen for the part of the domain of the function.
   * @param cb A cogen for the part of the domain of the function.
   * @param cc A cogen for the part of the domain of the function.
   * @param cd A cogen for the part of the domain of the function.
   * @param ce A cogen for the part of the domain of the function.
   * @param cf A cogen for the part of the domain of the function.
   * @param cg A cogen for the part of the domain of the function.
   * @param ch A cogen for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Gen<F8<A, B, C, D, E, F$, G, H, I>> arbF8(final Cogen<A> ca,
                                                                                       final Cogen<B> cb,
                                                                                       final Cogen<C> cc,
                                                                                       final Cogen<D> cd,
                                                                                       final Cogen<E> ce,
                                                                                       final Cogen<F$> cf,
                                                                                       final Cogen<G> cg,
                                                                                       final Cogen<H> ch,
                                                                                       final Gen<I> a) {
    return arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, arbF(ch, a)))))))).map(
        Function.uncurryF8());
  }

  /**
   * An arbitrary for function-8.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Gen<F8<A, B, C, D, E, F$, G, H, I>> arbF8Invariant(
      final Gen<I> a) {
    return a.map(compose(Function.<A, B, C, D, E, F$, G, H, I>uncurryF8(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>constant(),
                    compose(
                        Function.<D, F<E, F<F$, F<G, F<H, I>>>>>constant(),
                        compose(Function.<E, F<F$, F<G, F<H, I>>>>constant(),
                            compose(
                                Function.<F$, F<G, F<H, I>>>constant(),
                                compose(Function.<G, F<H, I>>constant(),
                                    Function.<H, I>constant())))))))));
  }

  /**
   * An arbitrary implementation for boolean values.
   */
  public static final Gen<Boolean> arbBoolean = elements(true, false);

  /**
   * An arbitrary implementation for integer values.
   */
  public static final Gen<Integer> arbInteger = sized(i -> choose(-i, i));

  /**
   * An arbitrary implementation for integer values that checks boundary values <code>(0, 1, -1,
   * max, min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link
   * #arbInteger} the remainder of the time (93%).
   */
  public static final Gen<Integer> arbIntegerBoundaries = sized(new F<Integer, Gen<Integer>>() {
    @SuppressWarnings("unchecked")
    public Gen<Integer> f(final Integer i) {
      return frequency(list(p(1, value(0)),
                            p(1, value(1)),
                            p(1, value(-1)),
                            p(1, value(Integer.MAX_VALUE)),
                            p(1, value(Integer.MIN_VALUE)),
                            p(1, value(Integer.MAX_VALUE - 1)),
                            p(1, value(Integer.MIN_VALUE + 1)),
                            p(93, arbInteger)));
    }
  });

  /**
   * An arbitrary implementation for long values.
   */
  public static final Gen<Long> arbLong =
      arbInteger.bind(arbInteger, i1 -> i2 -> (long) i1 << 32L & i2);

  /**
   * An arbitrary implementation for long values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbLong}
   * the remainder of the time (93%).
   */
  public static final Gen<Long> arbLongBoundaries = sized(new F<Integer, Gen<Long>>() {
    @SuppressWarnings("unchecked")
    public Gen<Long> f(final Integer i) {
      return frequency(list(p(1, value(0L)),
                            p(1, value(1L)),
                            p(1, value(-1L)),
                            p(1, value(Long.MAX_VALUE)),
                            p(1, value(Long.MIN_VALUE)),
                            p(1, value(Long.MAX_VALUE - 1L)),
                            p(1, value(Long.MIN_VALUE + 1L)),
                            p(93, arbLong)));
    }
  });

  /**
   * An arbitrary implementation for byte values.
   */
  public static final Gen<Byte> arbByte = arbInteger.map(i -> (byte) i.intValue());

  /**
   * An arbitrary implementation for byte values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbByte}
   * the remainder of the time (93%).
   */
  public static final Gen<Byte> arbByteBoundaries = sized(new F<Integer, Gen<Byte>>() {
    @SuppressWarnings("unchecked")
    public Gen<Byte> f(final Integer i) {
      return frequency(list(p(1, value((byte) 0)),
                            p(1, value((byte) 1)),
                            p(1, value((byte) -1)),
                            p(1, value(Byte.MAX_VALUE)),
                            p(1, value(Byte.MIN_VALUE)),
                            p(1, value((byte) (Byte.MAX_VALUE - 1))),
                            p(1, value((byte) (Byte.MIN_VALUE + 1))),
                            p(93, arbByte)));
    }
  });

  /**
   * An arbitrary implementation for short values.
   */
  public static final Gen<Short> arbShort = arbInteger.map(i -> (short) i.intValue());

  /**
   * An arbitrary implementation for short values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbShort}
   * the remainder of the time (93%).
   */
  public static final Gen<Short> arbShortBoundaries = sized(new F<Integer, Gen<Short>>() {
    @SuppressWarnings("unchecked")
    public Gen<Short> f(final Integer i) {
      return frequency(list(p(1, value((short) 0)),
                            p(1, value((short) 1)),
                            p(1, value((short) -1)),
                            p(1, value(Short.MAX_VALUE)),
                            p(1, value(Short.MIN_VALUE)),
                            p(1, value((short) (Short.MAX_VALUE - 1))),
                            p(1, value((short) (Short.MIN_VALUE + 1))),
                            p(93, arbShort)));
    }
  });

  /**
   * An arbitrary implementation for character values.
   */
  public static final Gen<Character> arbCharacter = choose(0, 65536).map(i -> (char) i.intValue());

  /**
   * An arbitrary implementation for character values that checks boundary values <code>(max, min,
   * max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbCharacter}
   * the remainder of the time (96%).
   */
  public static final Gen<Character> arbCharacterBoundaries = sized(new F<Integer, Gen<Character>>() {
    @SuppressWarnings("unchecked")
    public Gen<Character> f(final Integer i) {
      return frequency(list(p(1, value(Character.MIN_VALUE)),
                            p(1, value((char) (Character.MIN_VALUE + 1))),
                            p(1, value(Character.MAX_VALUE)),
                            p(1, value((char) (Character.MAX_VALUE - 1))),
                            p(95, arbCharacter)));
    }
  });

  /**
   * An arbitrary implementation for double values.
   */
  public static final Gen<Double> arbDouble = sized(i -> choose((double) -i, i));

  /**
   * An arbitrary implementation for double values that checks boundary values <code>(0, 1, -1, max,
   * min, min (normal), NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then
   * generates from {@link #arbDouble} the remainder of the time (91%).
   */
  public static final Gen<Double> arbDoubleBoundaries = sized(new F<Integer, Gen<Double>>() {
    @SuppressWarnings("unchecked")
    public Gen<Double> f(final Integer i) {
      return frequency(list(p(1, value(0D)),
                            p(1, value(1D)),
                            p(1, value(-1D)),
                            p(1, value(Double.MAX_VALUE)),
                            p(1, value(Double.MIN_VALUE)),
                            p(1, value(Double.NaN)),
                            p(1, value(Double.NEGATIVE_INFINITY)),
                            p(1, value(Double.POSITIVE_INFINITY)),
                            p(1, value(Double.MAX_VALUE - 1D)),
                            p(91, arbDouble)));
    }
  });

  /**
   * An arbitrary implementation for float values.
   */
  public static final Gen<Float> arbFloat = arbDouble.map(d -> (float) d.doubleValue());

  /**
   * An arbitrary implementation for float values that checks boundary values <code>(0, 1, -1, max,
   * min, NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then generates from
   * {@link #arbFloat} the remainder of the time (91%).
   */
  public static final Gen<Float> arbFloatBoundaries = sized(new F<Integer, Gen<Float>>() {
    @SuppressWarnings("unchecked")
    public Gen<Float> f(final Integer i) {
      return frequency(list(p(1, value(0F)),
                            p(1, value(1F)),
                            p(1, value(-1F)),
                            p(1, value(Float.MAX_VALUE)),
                            p(1, value(Float.MIN_VALUE)),
                            p(1, value(Float.NaN)),
                            p(1, value(Float.NEGATIVE_INFINITY)),
                            p(1, value(Float.POSITIVE_INFINITY)),
                            p(1, value(Float.MAX_VALUE - 1F)),
                            p(91, arbFloat)));
    }
  });

  /**
   * An arbitrary implementation for string values.
   */
  public static final Gen<String> arbString =
      arbList(arbCharacter).map(List::asString);

  /**
   * An arbitrary implementation for string values with characters in the US-ASCII range.
   */
  public static final Gen<String> arbUSASCIIString =
      arbList(arbCharacter).map(cs -> asString(cs.map(c -> (char) (c % 128))));

  /**
   * An arbitrary implementation for string values with alpha-numeric characters.
   */
  public static final Gen<String> arbAlphaNumString =
      arbList(elements(range(charEnumerator, 'a', 'z').append(
          range(charEnumerator, 'A', 'Z')).append(
          range(charEnumerator, '0', '9')).toArray().array(Character[].class))).map(asString());

  /**
   * An arbitrary implementation for string buffer values.
   */
  public static final Gen<StringBuffer> arbStringBuffer =
      arbString.map(StringBuffer::new);

  /**
   * An arbitrary implementation for string builder values.
   */
  public static final Gen<StringBuilder> arbStringBuilder =
      arbString.map(StringBuilder::new);

  /**
   * Returns an arbitrary implementation for generators.
   *
   * @param aa an arbitrary implementation for the type over which the generator is defined.
   * @return An arbitrary implementation for generators.
   */
  public static <A> Gen<Gen<A>> arbGen(final Gen<A> aa) {
    return sized(i -> {
      if (i == 0)
        return fail();
      else
        return aa.map(Gen::value).resize(i - 1);
    });
  }

  /**
   * Returns an arbitrary implementation for optional values.
   *
   * @param aa an arbitrary implementation for the type over which the optional value is defined.
   * @return An arbitrary implementation for optional values.
   */
  public static <A> Gen<Option<A>> arbOption(final Gen<A> aa) {
    return sized(i -> i == 0 ?
           value(Option.none()) :
           aa.map(Option::some).resize(i - 1));
  }

  /**
   * Returns an arbitrary implementation for the disjoint union.
   *
   * @param aa An arbitrary implementation for the type over which one side of the disjoint union is
   *           defined.
   * @param ab An arbitrary implementation for the type over which one side of the disjoint union is
   *           defined.
   * @return An arbitrary implementation for the disjoint union.
   */
  @SuppressWarnings("unchecked")
  public static <A, B> Gen<Either<A, B>> arbEither(final Gen<A> aa, final Gen<B> ab) {
    final Gen<Either<A, B>> left = aa.map(Either::left);
    final Gen<Either<A, B>> right = ab.map(Either::right);
    return oneOf(list(left, right));
  }

  /**
   * Returns an arbitrary implementation for lists.
   *
   * @param aa An arbitrary implementation for the type over which the list is defined.
   * @return An arbitrary implementation for lists.
   */
  public static <A> Gen<List<A>> arbList(final Gen<A> aa) {
    return listOf(aa);
  }

	/**
	 * Returns an arbitrary list of integers.
	 */
	public static <A> Gen<List<Integer>> arbListInteger() {
		return listOf(arbInteger);
	}

	/**
	 * Returns an arbitrary list of strings.
	 */
	public static <A> Gen<List<String>> arbListString() {
		return listOf(arbString);
	}

	/**
	 * Returns an arbitrary list of booleans.
	 */
	public static <A> Gen<List<Boolean>> arbListBoolean() {
		return listOf(arbBoolean);
	}

	/**
	 * Returns an arbitrary list of doubles.
	 */
	public static <A> Gen<List<Double>> arbListDouble() {
		return listOf(arbDouble);
	}

	public static <A> Gen<NonEmptyList<A>> arbNonEmptyList(final Gen<A> aa) {
    return Gen.listOf1(aa).map(list -> NonEmptyList.fromList(list).some());
  }

    /**
     * Returns an arbitrary Validation for the given arbitrary parameters.
     */
    public static <A, B> Gen<Validation<A, B>> arbValidation(final Gen<A> aa, final Gen<B> ab) {
        return arbBoolean.bind(bool -> bool ? ab.map(Validation::<A, B>success) : aa.map(Validation::<A, B>fail));
    }

  /**
   * Returns an arbitrary implementation for streams.
   *
   * @param aa An arbitrary implementation for the type over which the stream is defined.
   * @return An arbitrary implementation for streams.
   */
  public static <A> Gen<Stream<A>> arbStream(final Gen<A> aa) {
    return arbList(aa).map(List::toStream);
  }

  /**
   * Returns an arbitrary implementation for arrays.
   *
   * @param aa An arbitrary implementation for the type over which the array is defined.
   * @return An arbitrary implementation for arrays.
   */
  public static <A> Gen<Array<A>> arbArray(final Gen<A> aa) {
    return arbList(aa).map(List<A>::toArray);
  }

  /**
   * Returns an arbitrary implementation for sequences.
   *
   * @param aa An arbitrary implementation for the type over which the sequence is defined.
   * @return An arbitrary implementation for sequences.
   */
  @SuppressWarnings("unchecked")
  public static <A> Gen<Seq<A>> arbSeq(final Gen<A> aa) {
    return arbArray(aa).map(Seq::iterableSeq);
  }

	public static <A> Gen<Set<A>> arbSet(Ord<A> ord, final Gen<A> aa) {
		return arbList(aa).map(list -> Set.iterableSet(ord, list));
	}

    public static <A> Gen<Set<A>> arbSet(Ord<A> ord, final Gen<A> aa, int max) {
        return choose(0, max).bind(i -> Gen.sequenceN(i, aa)).map(list -> Set.iterableSet(ord, list));
    }


    /**
   * Returns an arbitrary implementation for throwables.
   *
   * @param as An arbitrary used for the throwable message.
   * @return An arbitrary implementation for throwables.
   */
  public static Gen<Throwable> arbThrowable(final Gen<String> as) {
    return as.map(Throwable::new);
  }

  /**
   * An arbitrary implementation for throwables.
   */
  public static final Gen<Throwable> arbThrowable = arbThrowable(arbString);

  // BEGIN java.util

  /**
   * Returns an arbitrary implementation for array lists.
   *
   * @param aa An arbitrary implementation for the type over which the array list is defined.
   * @return An arbitrary implementation for array lists.
   */
  public static <A> Gen<ArrayList<A>> arbArrayList(final Gen<A> aa) {
    return arbArray(aa).map(Array::toJavaList);
  }

  /**
   * An arbitrary implementation for bit sets.
   */
  public static final Gen<BitSet> arbBitSet =
      arbList(arbBoolean).map(bs -> {
        final BitSet s = new BitSet(bs.length());
        bs.zipIndex().foreachDoEffect(bi -> s.set(bi._2(), bi._1()));
        return s;
      });

  /**
   * An arbitrary implementation for calendars.
   */
  public static final Gen<Calendar> arbCalendar = arbLong.map(i -> {
    final Calendar c = Calendar.getInstance();
    c.setTimeInMillis(i);
    return c;
  });

  /**
   * An arbitrary implementation for dates.
   */
  public static final Gen<Date> arbDate = arbLong.map(Date::new);

  /**
   * Returns an arbitrary implementation for a Java enumeration.
   *
   * @param clazz The type of enum to return an arbitrary of.
   * @return An arbitrary for instances of the supplied enum type.
   */
  public static <A extends Enum<A>> Gen<A> arbEnumValue(final Class<A> clazz) {
    return elements(clazz.getEnumConstants());
  }

  /**
   * Returns an arbitrary implementation for enum maps.
   *
   * @param ak An arbitrary implementation for the type over which the enum map's keys are defined.
   * @param av An arbitrary implementation for the type over which the enum map's values are
   *           defined.
   * @return An arbitrary implementation for enum maps.
   */
  public static <K extends Enum<K>, V> Gen<EnumMap<K, V>> arbEnumMap(final Gen<K> ak,
                                                                           final Gen<V> av) {
    return arbHashtable(ak, av).map(EnumMap::new);
  }

  /**
   * Returns an arbitrary implementation for enum sets.
   *
   * @param aa An arbitrary implementation for the type over which the enum set is defined.
   * @return An arbitrary implementation for enum sets.
   */
  public static <A extends Enum<A>> Gen<EnumSet<A>> arbEnumSet(final Gen<A> aa) {
    return arbArray(aa).map(a -> copyOf(a.asJavaList()));
  }

  /**
   * An arbitrary implementation for gregorian calendars.
   */
  public static final Gen<GregorianCalendar> arbGregorianCalendar =
      arbLong.map(i -> {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(i);
        return c;
      });

  /**
   * Returns an arbitrary implementation for hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the hash map's keys are defined.
   * @param av An arbitrary implementation for the type over which the hash map's values are
   *           defined.
   * @return An arbitrary implementation for hash maps.
   */
  public static <K, V> Gen<HashMap<K, V>> arbHashMap(final Gen<K> ak, final Gen<V> av) {
    return arbHashtable(ak, av).map(HashMap::new);
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Gen<HashSet<A>> arbHashSet(final Gen<A> aa) {
    return arbArray(aa).map(a -> new HashSet<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for hash tables.
   *
   * @param ak An arbitrary implementation for the type over which the hash table's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the hash table's values are
   *           defined.
   * @return An arbitrary implementation for hash tables.
   */
  public static <K, V> Gen<Hashtable<K, V>> arbHashtable(final Gen<K> ak, final Gen<V> av) {
    return arbList(ak).bind(arbList(av), ks -> vs -> {
      final Hashtable<K, V> t = new Hashtable<>();

      ks.zip(vs).foreachDoEffect(kv -> t.put(kv._1(), kv._2()));

      return t;
    });
  }

  /**
   * Returns an arbitrary implementation for identity hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the identity hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the identity hash map's values
   *           are defined.
   * @return An arbitrary implementation for identity hash maps.
   */
  public static <K, V> Gen<IdentityHashMap<K, V>> arbIdentityHashMap(final Gen<K> ak,
                                                                           final Gen<V> av) {
    return arbHashtable(ak, av).map(IdentityHashMap::new);
  }

  /**
   * Returns an arbitrary implementation for linked hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the linked hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the linked hash map's values are
   *           defined.
   * @return An arbitrary implementation for linked hash maps.
   */
  public static <K, V> Gen<LinkedHashMap<K, V>> arbLinkedHashMap(final Gen<K> ak, final Gen<V> av) {
    return arbHashtable(ak, av).map(LinkedHashMap::new);
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Gen<LinkedHashSet<A>> arbLinkedHashSet(final Gen<A> aa) {
    return arbArray(aa).map(a -> new LinkedHashSet<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for linked lists.
   *
   * @param aa An arbitrary implementation for the type over which the linked list is defined.
   * @return An arbitrary implementation for linked lists.
   */
  public static <A> Gen<LinkedList<A>> arbLinkedList(final Gen<A> aa) {
    return arbArray(aa).map(a -> new LinkedList<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for priority queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority queue is defined.
   * @return An arbitrary implementation for priority queues.
   */
  public static <A> Gen<PriorityQueue<A>> arbPriorityQueue(final Gen<A> aa) {
    return arbArray(aa).map(a -> new PriorityQueue<>(a.asJavaList()));
  }

  /**
   * An arbitrary implementation for properties.
   */
  public static final Gen<Properties> arbProperties =
      arbHashtable(arbString, arbString).map(ht -> {
        final Properties p = new Properties();

        for (final Map.Entry<String, String> entry : ht.entrySet()) {
          p.setProperty(entry.getKey(), entry.getValue());
        }

        return p;
      });

  /**
   * Returns an arbitrary implementation for stacks.
   *
   * @param aa An arbitrary implementation for the type over which the stack is defined.
   * @return An arbitrary implementation for stacks.
   */
  public static <A> Gen<Stack<A>> arbStack(final Gen<A> aa) {
    return arbArray(aa).map(a -> {
      final Stack<A> s = new Stack<>();
      s.addAll(a.asJavaList());
      return s;
    });
  }

  /**
   * Returns an arbitrary implementation for java.util tree maps.
   *
   * @param ak An arbitrary implementation for the type over which the tree map's keys are defined.
   * @param av An arbitrary implementation for the type over which the tree map's values are
   *           defined.
   * @return An arbitrary implementation for tree maps.
   */
  public static <K, V> Gen<java.util.TreeMap<K, V>> arbJavaTreeMap(final Gen<K> ak, final Gen<V> av) {
    return arbHashtable(ak, av).map(java.util.TreeMap::new);
  }

    /**
     * Returns an arbitrary implementation for tree maps.
     */
    public static <K, V> Gen<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Gen<List<P2<K, V>>> al) {
        return al.map(list -> fj.data.TreeMap.iterableTreeMap(ord, list));
    }

    /**
     * Returns an arbitrary implementation for tree maps.
     */
    public static <K, V> Gen<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Gen<K> ak, Gen<V> av) {
        return arbTreeMap(ord, arbList(arbP2(ak, av)));
    }

    /**
     * Returns an arbitrary implementation for tree maps where the map size is the given arbitrary integer.
     */
    public static <K, V> Gen<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Gen<K> ak, Gen<V> av, Gen<Integer> ai) {
        Gen<List<P2<K, V>>> gl2 = ai.bind(i -> {
            if (i < 0) {
                throw Bottom.error("Undefined: arbitrary natural is negative (" + i + ")");
            }
            return Gen.sequenceN(Math.max(i, 0), arbP2(ak, av));
        });
        return arbTreeMap(ord, gl2);
    }

    /**
     * Returns an arbitrary implementation for tree maps where the size is less than or equal to the max size.
     */
    public static <K, V> Gen<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Gen<K> ak, Gen<V> av, int maxSize) {
        if (maxSize < 0) {
          throw Bottom.error("Undefined: arbitrary natural is negative (" + maxSize + ")");
        }
        return arbTreeMap(ord, ak, av, choose(0, maxSize));
    }

  /**
   * Returns an arbitrary implementation for tree sets.
   *
   * @param aa An arbitrary implementation for the type over which the tree set is defined.
   * @return An arbitrary implementation for tree sets.
   */
  public static <A> Gen<TreeSet<A>> arbTreeSet(final Gen<A> aa) {
    return arbArray(aa).map(a -> new TreeSet<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for vectors.
   *
   * @param aa An arbitrary implementation for the type over which the vector is defined.
   * @return An arbitrary implementation for vectors.
   */
  @SuppressWarnings("UseOfObsoleteCollectionType")
  public static <A> Gen<Vector<A>> arbVector(final Gen<A> aa) {
    return arbArray(aa).map(a -> new Vector<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for weak hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the weak hash map's keys are
   *           defined.
   * @param av An arbitrary implementation for the type over which the weak hash map's values are
   *           defined.
   * @return An arbitrary implementation for weak hash maps.
   */
  public static <K, V> Gen<WeakHashMap<K, V>> arbWeakHashMap(final Gen<K> ak, final Gen<V> av) {
    return arbHashtable(ak, av).map(WeakHashMap::new);
  }

  // END java.util

  // BEGIN java.util.concurrent

  /**
   * Returns an arbitrary implementation for array blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the array blocking queue is
   *           defined.
   * @return An arbitrary implementation for array blocking queues.
   */
  public static <A> Gen<ArrayBlockingQueue<A>> arbArrayBlockingQueue(final Gen<A> aa) {
    return arbArray(aa).bind(arbInteger, arbBoolean,
        a -> capacity -> fair -> new ArrayBlockingQueue<A>(a.length() + abs(capacity),
                                         fair, a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for concurrent hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the concurrent hash map's keys
   *           are defined.
   * @param av An arbitrary implementation for the type over which the concurrent hash map's values
   *           are defined.
   * @return An arbitrary implementation for concurrent hash maps.
   */
  public static <K, V> Gen<ConcurrentHashMap<K, V>> arbConcurrentHashMap(final Gen<K> ak,
                                                                               final Gen<V> av) {
    return arbHashtable(ak, av).map(ConcurrentHashMap::new);
  }

  /**
   * Returns an arbitrary implementation for concurrent linked queues.
   *
   * @param aa An arbitrary implementation for the type over which the concurrent linked queue is
   *           defined.
   * @return An arbitrary implementation for concurrent linked queues.
   */
  public static <A> Gen<ConcurrentLinkedQueue<A>> arbConcurrentLinkedQueue(final Gen<A> aa) {
    return arbArray(aa).map(a -> new ConcurrentLinkedQueue<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array lists.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array list is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array lists.
   */
  public static <A> Gen<CopyOnWriteArrayList<A>> arbCopyOnWriteArrayList(final Gen<A> aa) {
    return arbArray(aa).map(a -> new CopyOnWriteArrayList<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array sets.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array set is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array sets.
   */
  public static <A> Gen<CopyOnWriteArraySet<A>> arbCopyOnWriteArraySet(final Gen<A> aa) {
    return arbArray(aa).map(a -> new CopyOnWriteArraySet<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for delay queues.
   *
   * @param aa An arbitrary implementation for the type over which the delay queue is defined.
   * @return An arbitrary implementation for delay queues.
   */
  public static <A extends Delayed> Gen<DelayQueue<A>> arbDelayQueue(final Gen<A> aa) {
    return arbArray(aa).map(a -> new DelayQueue<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for linked blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the linked blocking queue is
   *           defined.
   * @return An arbitrary implementation for linked blocking queues.
   */
  public static <A> Gen<LinkedBlockingQueue<A>> arbLinkedBlockingQueue(final Gen<A> aa) {
    return arbArray(aa).map(a -> new LinkedBlockingQueue<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Gen<PriorityBlockingQueue<A>> arbPriorityBlockingQueue(final Gen<A> aa) {
    return arbArray(aa).map(a -> new PriorityBlockingQueue<>(a.asJavaList()));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Gen<SynchronousQueue<A>> arbSynchronousQueue(final Gen<A> aa) {
    return arbArray(aa).bind(arbBoolean, a -> fair -> {
      final SynchronousQueue<A> q = new SynchronousQueue<>(fair);
      q.addAll(a.asJavaList());
      return q;
    });
  }

  // END java.util.concurrent

  // BEGIN java.sql

  /**
   * An arbitrary implementation for SQL dates.
   */
  public static final Gen<java.sql.Date> arbSQLDate = arbLong.map(java.sql.Date::new);

  /**
   * An arbitrary implementation for SQL times.
   */
  public static final Gen<Time> arbTime = arbLong.map(Time::new);

  /**
   * An arbitrary implementation for SQL time stamps.
   */
  public static final Gen<Timestamp> arbTimestamp = arbLong.map(Timestamp::new);

  // END java.sql

  // BEGIN java.math

  /**
   * An arbitrary implementation for big integers.
   */
  public static final Gen<BigInteger> arbBigInteger =
      arbArray(arbByte).bind(arbByte, a -> b -> {
        final byte[] x = new byte[a.length() + 1];

        for (int i = 0; i < a.array().length; i++) {
          x[i] = a.get(i);
        }

        x[a.length()] = b;

        return new BigInteger(x);
      });

  /**
   * An arbitrary implementation for big decimals.
   */
  public static final Gen<BigDecimal> arbBigDecimal =
      arbBigInteger.map(BigDecimal::new);

  // END java.math

  /**
   * An arbitrary implementation for naturals.
   */
  public static final Gen<Natural> arbNatural = arbBigInteger.map(BigInteger::abs).map(Natural::natural).map(o -> o.some());

  /**
   * An arbitrary implementation for locales.
   */
  public static final Gen<Locale> arbLocale = elements(getAvailableLocales());

  /**
   * Returns an arbitrary implementation for product-1 values.
   *
   * @param aa An arbitrary implementation for the type over which the product-1 is defined.
   * @return An arbitrary implementation for product-1 values.
   */
  public static <A> Gen<P1<A>> arbP1(final Gen<A> aa) {
    return aa.map(P::p);
  }

  /**
   * Returns an arbitrary implementation for product-2 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-2 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-2 is
   *           defined.
   * @return An arbitrary implementation for product-2 values.
   */
  public static <A, B> Gen<P2<A, B>> arbP2(final Gen<A> aa, final Gen<B> ab) {
    return aa.bind(ab, a -> b -> p(a, b));
  }

  /**
   * Returns an arbitrary implementation for product-3 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-3 is
   *           defined.
   * @return An arbitrary implementation for product-3 values.
   */
  public static <A, B, C> Gen<P3<A, B, C>> arbP3(final Gen<A> aa, final Gen<B> ab,
                                                 final Gen<C> ac) {
    return aa.bind(ab, ac, a -> b -> c -> p(a, b, c));
  }

  /**
   * Returns an arbitrary implementation for product-4 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-4 is
   *           defined.
   * @return An arbitrary implementation for product-4 values.
   */
  public static <A, B, C, D> Gen<P4<A, B, C, D>> arbP4(final Gen<A> aa, final Gen<B> ab,
                                                       final Gen<C> ac, final Gen<D> ad) {
    return aa.bind(ab, ac, ad, a -> b -> c -> d -> p(a, b, c, d));
  }

  /**
   * Returns an arbitrary implementation for product-5 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-5 is
   *           defined.
   * @return An arbitrary implementation for product-5 values.
   */
  public static <A, B, C, D, E> Gen<P5<A, B, C, D, E>> arbP5(final Gen<A> aa, final Gen<B> ab,
                                                             final Gen<C> ac, final Gen<D> ad,
                                                             final Gen<E> ae) {
    return aa.bind(ab, ac, ad, ae, a -> b -> c -> d -> e -> p(a, b, c, d, e));
  }

  /**
   * Returns an arbitrary implementation for product-6 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-6 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @return An arbitrary implementation for product-6 values.
   */
  public static <A, B, C, D, E, F$> Gen<P6<A, B, C, D, E, F$>> arbP6(final Gen<A> aa, final Gen<B> ab,
                                                                     final Gen<C> ac, final Gen<D> ad,
                                                                     final Gen<E> ae,
                                                                     final Gen<F$> af) {
    return aa.bind(ab, ac, ad, ae, af,
        a -> b -> c -> d -> e -> f -> p(a, b, c, d, e, f));
  }

  /**
   * Returns an arbitrary implementation for product-7 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-7 is
   *           defined.
   * @param ag An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @return An arbitrary implementation for product-7 values.
   */
  public static <A, B, C, D, E, F$, G> Gen<P7<A, B, C, D, E, F$, G>> arbP7(final Gen<A> aa,
                                                                           final Gen<B> ab,
                                                                           final Gen<C> ac,
                                                                           final Gen<D> ad,
                                                                           final Gen<E> ae,
                                                                           final Gen<F$> af,
                                                                           final Gen<G> ag) {
    return aa.bind(ab, ac, ad, ae, af, ag,
        a -> b -> c -> d -> e -> f -> g -> p(a, b, c, d, e, f, g));
  }

  /**
   * Returns an arbitrary implementation for product-8 values.
   *
   * @param aa An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ab An Arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ac An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ad An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ae An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param af An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ag An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @param ah An arbitrary implementation for one of the types over which the product-8 is
   *           defined.
   * @return An arbitrary implementation for product-8 values.
   */
  public static <A, B, C, D, E, F$, G, H> Gen<P8<A, B, C, D, E, F$, G, H>> arbP8(final Gen<A> aa,
                                                                                 final Gen<B> ab,
                                                                                 final Gen<C> ac,
                                                                                 final Gen<D> ad,
                                                                                 final Gen<E> ae,
                                                                                 final Gen<F$> af,
                                                                                 final Gen<G> ag,
                                                                                 final Gen<H> ah) {
    return aa.bind(ab, ac, ad, ae, af, ag, ah,
        a -> b -> c -> d -> e -> f -> g -> h -> p(a, b, c, d, e, f, g, h));
  }

}
