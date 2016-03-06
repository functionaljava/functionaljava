package fj.test;

import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.Function;
import fj.Bottom;

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
import fj.function.Effect1;

import static fj.data.Stream.range;
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
 * The type used to generate arbitrary values of the given type parameter (<code>A</code>). Common
 * arbitrary implementations are provided.
 *
 * @version %build.number%
 */
public final class Arbitrary<A> {
  /**
   * The generator associated with this arbitrary.
   */
  @SuppressWarnings("PublicField")
  public final Gen<A> gen;

  private Arbitrary(final Gen<A> gen) {
    this.gen = gen;
  }

  /**
   * Constructs and arbitrary with the given generator.
   *
   * @param g The generator to construct an arbitrary with.
   * @return A new arbitrary that uses the given generator.
   */
  public static <A> Arbitrary<A> arbitrary(final Gen<A> g) {
    return new Arbitrary<>(g);
  }

  /**
   * An arbitrary for functions.
   *
   * @param c The coarbitrary for the function domain.
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Arbitrary<F<A, B>> arbF(final Coarbitrary<A> c, final Arbitrary<B> a) {
    return arbitrary(promote(new F<A, Gen<B>>() {
      public Gen<B> f(final A x) {
        return c.coarbitrary(x, a.gen);
      }
    }));
  }

    public static <A, B> Arbitrary<Reader<A, B>> arbReader(Coarbitrary<A> aa, Arbitrary<B> ab) {
        return arbitrary(arbF(aa, ab).gen.map(Reader::unit));
    }

    /**
     * An arbitrary for state.
     */
    public static <S, A> Arbitrary<State<S, A>> arbState(Arbitrary<S> as, Coarbitrary<S> cs, Arbitrary<A> aa) {
        return arbitrary(arbF(cs, arbP2(as, aa)).gen.map(State::unit));
    }

    /**
     * An arbitrary for the LcgRng.
     */
    public static <A> Arbitrary<LcgRng> arbLcgRng() {
        return arbitrary(Arbitrary.arbLong.gen.map(LcgRng::new));
    }

  /**
   * An arbitrary for functions.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for functions.
   */
  public static <A, B> Arbitrary<F<A, B>> arbFInvariant(final Arbitrary<B> a) {
    return arbitrary(a.gen.map(Function.constant()));
  }

  /**
   * An arbitrary for function-2.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Arbitrary<F2<A, B, C>> arbF2(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                       final Arbitrary<C> a) {
    return arbitrary(arbF(ca, arbF(cb, a)).gen.map(Function.uncurryF2()));
  }

  /**
   * An arbitrary for function-2.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-2.
   */
  public static <A, B, C> Arbitrary<F2<A, B, C>> arbF2Invariant(final Arbitrary<C> a) {
    return arbitrary(a.gen.map(
        compose(Function.uncurryF2(), compose(Function.constant(), Function.constant()))));
  }

  /**
   * An arbitrary for function-3.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Arbitrary<F3<A, B, C, D>> arbF3(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                             final Coarbitrary<C> cc, final Arbitrary<D> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, a))).gen.map(Function.uncurryF3()));
  }

  /**
   * An arbitrary for function-3.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-3.
   */
  public static <A, B, C, D> Arbitrary<F3<A, B, C, D>> arbF3Invariant(final Arbitrary<D> a) {
    return arbitrary(a.gen.map(compose(Function.uncurryF3(), compose(Function.constant(),
                                                                                 compose(
                                                                                     Function.constant(),
                                                                                     Function.constant())))));
  }

  /**
   * An arbitrary for function-4.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Arbitrary<F4<A, B, C, D, E>> arbF4(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                                   final Coarbitrary<C> cc, final Coarbitrary<D> cd,
                                                                   final Arbitrary<E> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, a)))).gen.map(Function.uncurryF4()));
  }

  /**
   * An arbitrary for function-4.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-4.
   */
  public static <A, B, C, D, E> Arbitrary<F4<A, B, C, D, E>> arbF4Invariant(final Arbitrary<E> a) {
    return arbitrary(a.gen.map(compose(Function.uncurryF4(),
                                       compose(Function.constant(),
                                               compose(Function.constant(),
                                                       compose(Function.constant(),
                                                               Function.constant()))))));
  }

  /**
   * An arbitrary for function-5.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Arbitrary<F5<A, B, C, D, E, F$>> arbF5(final Coarbitrary<A> ca,
                                                                           final Coarbitrary<B> cb,
                                                                           final Coarbitrary<C> cc,
                                                                           final Coarbitrary<D> cd,
                                                                           final Coarbitrary<E> ce,
                                                                           final Arbitrary<F$> a) {
    return arbitrary(
        arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, a))))).gen.map(Function.uncurryF5()));
  }

  /**
   * An arbitrary for function-5.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-5.
   */
  public static <A, B, C, D, E, F$> Arbitrary<F5<A, B, C, D, E, F$>> arbF5Invariant(final Arbitrary<F$> a) {
    return arbitrary(a.gen.map(compose(Function.uncurryF5(),
                                       compose(Function.constant(),
                                               compose(Function.constant(),
                                                       compose(Function.constant(),
                                                               compose(Function.constant(),
                                                                       Function.constant())))))));
  }

  /**
   * An arbitrary for function-6.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Arbitrary<F6<A, B, C, D, E, F$, G>> arbF6(final Coarbitrary<A> ca,
                                                                                 final Coarbitrary<B> cb,
                                                                                 final Coarbitrary<C> cc,
                                                                                 final Coarbitrary<D> cd,
                                                                                 final Coarbitrary<E> ce,
                                                                                 final Coarbitrary<F$> cf,
                                                                                 final Arbitrary<G> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, a)))))).gen.map(
        Function.uncurryF6()));
  }

  /**
   * An arbitrary for function-6.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-6.
   */
  public static <A, B, C, D, E, F$, G> Arbitrary<F6<A, B, C, D, E, F$, G>> arbF6Invariant(final Arbitrary<G> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G>uncurryF6(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, G>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, G>>>>constant(),
                    compose(Function.<D, F<E, F<F$, G>>>constant(),
                        compose(Function.<E, F<F$, G>>constant(),
                            Function.<F$, G>constant()))))))));
  }

  /**
   * An arbitrary for function-7.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param cg A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Arbitrary<F7<A, B, C, D, E, F$, G, H>> arbF7(final Coarbitrary<A> ca,
                                                                                       final Coarbitrary<B> cb,
                                                                                       final Coarbitrary<C> cc,
                                                                                       final Coarbitrary<D> cd,
                                                                                       final Coarbitrary<E> ce,
                                                                                       final Coarbitrary<F$> cf,
                                                                                       final Coarbitrary<G> cg,
                                                                                       final Arbitrary<H> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, a))))))).gen.map(
        Function.uncurryF7()));
  }

  /**
   * An arbitrary for function-7.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Arbitrary<F7<A, B, C, D, E, F$, G, H>> arbF7Invariant(final Arbitrary<H> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G, H>uncurryF7(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, F<G, H>>>>>constant(),
                    compose(Function.<D, F<E, F<F$, F<G, H>>>>constant(),
                        compose(Function.<E, F<F$, F<G, H>>>constant(),
                            compose(Function.<F$, F<G, H>>constant(),
                                Function.<G, H>constant())))))))));
  }

  /**
   * An arbitrary for function-8.
   *
   * @param ca A coarbitrary for the part of the domain of the function.
   * @param cb A coarbitrary for the part of the domain of the function.
   * @param cc A coarbitrary for the part of the domain of the function.
   * @param cd A coarbitrary for the part of the domain of the function.
   * @param ce A coarbitrary for the part of the domain of the function.
   * @param cf A coarbitrary for the part of the domain of the function.
   * @param cg A coarbitrary for the part of the domain of the function.
   * @param ch A coarbitrary for the part of the domain of the function.
   * @param a  An arbitrary for the codomain of the function.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Arbitrary<F8<A, B, C, D, E, F$, G, H, I>> arbF8(final Coarbitrary<A> ca,
                                                                                             final Coarbitrary<B> cb,
                                                                                             final Coarbitrary<C> cc,
                                                                                             final Coarbitrary<D> cd,
                                                                                             final Coarbitrary<E> ce,
                                                                                             final Coarbitrary<F$> cf,
                                                                                             final Coarbitrary<G> cg,
                                                                                             final Coarbitrary<H> ch,
                                                                                             final Arbitrary<I> a) {
    return arbitrary(arbF(ca, arbF(cb, arbF(cc, arbF(cd, arbF(ce, arbF(cf, arbF(cg, arbF(ch, a)))))))).gen.map(
        Function.uncurryF8()));
  }

  /**
   * An arbitrary for function-8.
   *
   * @param a The arbitrary for the function codomain.
   * @return An arbitrary for function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Arbitrary<F8<A, B, C, D, E, F$, G, H, I>> arbF8Invariant(
      final Arbitrary<I> a) {
    return arbitrary(a.gen.map(compose(Function.<A, B, C, D, E, F$, G, H, I>uncurryF8(),
        compose(Function.<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>constant(),
            compose(Function.<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>constant(),
                compose(Function.<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>constant(),
                    compose(
                        Function.<D, F<E, F<F$, F<G, F<H, I>>>>>constant(),
                        compose(Function.<E, F<F$, F<G, F<H, I>>>>constant(),
                            compose(
                                Function.<F$, F<G, F<H, I>>>constant(),
                                compose(Function.<G, F<H, I>>constant(),
                                    Function.<H, I>constant()))))))))));
  }

  /**
   * An arbitrary implementation for boolean values.
   */
  public static final Arbitrary<Boolean> arbBoolean = arbitrary(elements(true, false));

  /**
   * An arbitrary implementation for integer values.
   */
  public static final Arbitrary<Integer> arbInteger = arbitrary(sized(i -> choose(-i, i)));

  /**
   * An arbitrary implementation for integer values that checks boundary values <code>(0, 1, -1,
   * max, min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link
   * #arbInteger} the remainder of the time (93%).
   */
  public static final Arbitrary<Integer> arbIntegerBoundaries = arbitrary(sized(new F<Integer, Gen<Integer>>() {
    @SuppressWarnings("unchecked")
    public Gen<Integer> f(final Integer i) {
      return frequency(list(p(1, value(0)),
                            p(1, value(1)),
                            p(1, value(-1)),
                            p(1, value(Integer.MAX_VALUE)),
                            p(1, value(Integer.MIN_VALUE)),
                            p(1, value(Integer.MAX_VALUE - 1)),
                            p(1, value(Integer.MIN_VALUE + 1)),
                            p(93, arbInteger.gen)));
    }
  }));

  /**
   * An arbitrary implementation for long values.
   */
  public static final Arbitrary<Long> arbLong =
      arbitrary(arbInteger.gen.bind(arbInteger.gen, i1 -> i2 -> (long) i1 << 32L & i2));

  /**
   * An arbitrary implementation for long values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbLong}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Long> arbLongBoundaries = arbitrary(sized(new F<Integer, Gen<Long>>() {
    @SuppressWarnings("unchecked")
    public Gen<Long> f(final Integer i) {
      return frequency(list(p(1, value(0L)),
                            p(1, value(1L)),
                            p(1, value(-1L)),
                            p(1, value(Long.MAX_VALUE)),
                            p(1, value(Long.MIN_VALUE)),
                            p(1, value(Long.MAX_VALUE - 1L)),
                            p(1, value(Long.MIN_VALUE + 1L)),
                            p(93, arbLong.gen)));
    }
  }));

  /**
   * An arbitrary implementation for byte values.
   */
  public static final Arbitrary<Byte> arbByte = arbitrary(arbInteger.gen.map(i -> (byte) i.intValue()));

  /**
   * An arbitrary implementation for byte values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbByte}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Byte> arbByteBoundaries = arbitrary(sized(new F<Integer, Gen<Byte>>() {
    @SuppressWarnings("unchecked")
    public Gen<Byte> f(final Integer i) {
      return frequency(list(p(1, value((byte) 0)),
                            p(1, value((byte) 1)),
                            p(1, value((byte) -1)),
                            p(1, value(Byte.MAX_VALUE)),
                            p(1, value(Byte.MIN_VALUE)),
                            p(1, value((byte) (Byte.MAX_VALUE - 1))),
                            p(1, value((byte) (Byte.MIN_VALUE + 1))),
                            p(93, arbByte.gen)));
    }
  }));

  /**
   * An arbitrary implementation for short values.
   */
  public static final Arbitrary<Short> arbShort = arbitrary(arbInteger.gen.map(i -> (short) i.intValue()));

  /**
   * An arbitrary implementation for short values that checks boundary values <code>(0, 1, -1, max,
   * min, max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbShort}
   * the remainder of the time (93%).
   */
  public static final Arbitrary<Short> arbShortBoundaries = arbitrary(sized(new F<Integer, Gen<Short>>() {
    @SuppressWarnings("unchecked")
    public Gen<Short> f(final Integer i) {
      return frequency(list(p(1, value((short) 0)),
                            p(1, value((short) 1)),
                            p(1, value((short) -1)),
                            p(1, value(Short.MAX_VALUE)),
                            p(1, value(Short.MIN_VALUE)),
                            p(1, value((short) (Short.MAX_VALUE - 1))),
                            p(1, value((short) (Short.MIN_VALUE + 1))),
                            p(93, arbShort.gen)));
    }
  }));

  /**
   * An arbitrary implementation for character values.
   */
  public static final Arbitrary<Character> arbCharacter = arbitrary(choose(0, 65536).map(i -> (char) i.intValue()));

  /**
   * An arbitrary implementation for character values that checks boundary values <code>(max, min,
   * max - 1, min + 1)</code> with a frequency of 1% each then generates from {@link #arbCharacter}
   * the remainder of the time (96%).
   */
  public static final Arbitrary<Character> arbCharacterBoundaries = arbitrary(sized(new F<Integer, Gen<Character>>() {
    @SuppressWarnings("unchecked")
    public Gen<Character> f(final Integer i) {
      return frequency(list(p(1, value(Character.MIN_VALUE)),
                            p(1, value((char) (Character.MIN_VALUE + 1))),
                            p(1, value(Character.MAX_VALUE)),
                            p(1, value((char) (Character.MAX_VALUE - 1))),
                            p(95, arbCharacter.gen)));
    }
  }));

  /**
   * An arbitrary implementation for double values.
   */
  public static final Arbitrary<Double> arbDouble = arbitrary(sized(i -> choose((double) -i, i)));

  /**
   * An arbitrary implementation for double values that checks boundary values <code>(0, 1, -1, max,
   * min, min (normal), NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then
   * generates from {@link #arbDouble} the remainder of the time (91%).
   */
  public static final Arbitrary<Double> arbDoubleBoundaries = arbitrary(sized(new F<Integer, Gen<Double>>() {
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
                            p(91, arbDouble.gen)));
    }
  }));

  /**
   * An arbitrary implementation for float values.
   */
  public static final Arbitrary<Float> arbFloat = arbitrary(arbDouble.gen.map(d -> (float) d.doubleValue()));

  /**
   * An arbitrary implementation for float values that checks boundary values <code>(0, 1, -1, max,
   * min, NaN, -infinity, infinity, max - 1)</code> with a frequency of 1% each then generates from
   * {@link #arbFloat} the remainder of the time (91%).
   */
  public static final Arbitrary<Float> arbFloatBoundaries = arbitrary(sized(new F<Integer, Gen<Float>>() {
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
                            p(91, arbFloat.gen)));
    }
  }));

  /**
   * An arbitrary implementation for string values.
   */
  public static final Arbitrary<String> arbString =
      arbitrary(arbList(arbCharacter).gen.map(List::asString));

  /**
   * An arbitrary implementation for string values with characters in the US-ASCII range.
   */
  public static final Arbitrary<String> arbUSASCIIString =
      arbitrary(arbList(arbCharacter).gen.map(cs -> asString(cs.map(c -> (char) (c % 128)))));

  /**
   * An arbitrary implementation for string values with alpha-numeric characters.
   */
  public static final Arbitrary<String> arbAlphaNumString =
      arbitrary(arbList(arbitrary(elements(range(charEnumerator, 'a', 'z').append(
          range(charEnumerator, 'A', 'Z')).append(
          range(charEnumerator, '0', '9')).toArray().array(Character[].class)))).gen.map(asString()));

  /**
   * An arbitrary implementation for string buffer values.
   */
  public static final Arbitrary<StringBuffer> arbStringBuffer =
      arbitrary(arbString.gen.map(StringBuffer::new));

  /**
   * An arbitrary implementation for string builder values.
   */
  public static final Arbitrary<StringBuilder> arbStringBuilder =
      arbitrary(arbString.gen.map(StringBuilder::new));

  /**
   * Returns an arbitrary implementation for generators.
   *
   * @param aa an arbitrary implementation for the type over which the generator is defined.
   * @return An arbitrary implementation for generators.
   */
  public static <A> Arbitrary<Gen<A>> arbGen(final Arbitrary<A> aa) {
    return arbitrary(sized(i -> {
      if (i == 0)
        return fail();
      else
        return aa.gen.map(Gen::value).resize(i - 1);
    }));
  }

  /**
   * Returns an arbitrary implementation for optional values.
   *
   * @param aa an arbitrary implementation for the type over which the optional value is defined.
   * @return An arbitrary implementation for optional values.
   */
  public static <A> Arbitrary<Option<A>> arbOption(final Arbitrary<A> aa) {
    return arbitrary(sized(i -> i == 0 ?
           value(Option.none()) :
           aa.gen.map(Option::some).resize(i - 1)));
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
  public static <A, B> Arbitrary<Either<A, B>> arbEither(final Arbitrary<A> aa, final Arbitrary<B> ab) {
    final Gen<Either<A, B>> left = aa.gen.map(Either::left);
    final Gen<Either<A, B>> right = ab.gen.map(Either::right);
    return arbitrary(oneOf(list(left, right)));
  }

  /**
   * Returns an arbitrary implementation for lists.
   *
   * @param aa An arbitrary implementation for the type over which the list is defined.
   * @return An arbitrary implementation for lists.
   */
  public static <A> Arbitrary<List<A>> arbList(final Arbitrary<A> aa) {
    return arbitrary(listOf(aa.gen));
  }

	/**
	 * Returns an arbitrary list of integers.
	 */
	public static <A> Arbitrary<List<Integer>> arbListInteger() {
		return arbitrary(listOf(arbInteger.gen));
	}

	/**
	 * Returns an arbitrary list of strings.
	 */
	public static <A> Arbitrary<List<String>> arbListString() {
		return arbitrary(listOf(arbString.gen));
	}

	/**
	 * Returns an arbitrary list of booleans.
	 */
	public static <A> Arbitrary<List<Boolean>> arbListBoolean() {
		return arbitrary(listOf(arbBoolean.gen));
	}

	/**
	 * Returns an arbitrary list of doubles.
	 */
	public static <A> Arbitrary<List<Double>> arbListDouble() {
		return arbitrary(listOf(arbDouble.gen));
	}

	public static <A> Arbitrary<NonEmptyList<A>> arbNonEmptyList(final Arbitrary<A> aa) {
    return arbitrary(Gen.listOf1(aa.gen).map(list -> NonEmptyList.fromList(list).some()));
  }

    /**
     * Returns an arbitrary Validation for the given arbitrary parameters.
     */
    public static <A, B> Arbitrary<Validation<A, B>> arbValidation(final Arbitrary<A> aa, final Arbitrary<B> ab) {
        return arbitrary(arbBoolean.gen.bind(bool -> bool ? ab.gen.map(Validation::<A, B>success) : aa.gen.map(Validation::<A, B>fail)));
    }

  /**
   * Returns an arbitrary implementation for streams.
   *
   * @param aa An arbitrary implementation for the type over which the stream is defined.
   * @return An arbitrary implementation for streams.
   */
  public static <A> Arbitrary<Stream<A>> arbStream(final Arbitrary<A> aa) {
    return arbitrary(arbList(aa).gen.map(List::toStream));
  }

  /**
   * Returns an arbitrary implementation for arrays.
   *
   * @param aa An arbitrary implementation for the type over which the array is defined.
   * @return An arbitrary implementation for arrays.
   */
  public static <A> Arbitrary<Array<A>> arbArray(final Arbitrary<A> aa) {
    return arbitrary(arbList(aa).gen.map(List<A>::toArray));
  }

  /**
   * Returns an arbitrary implementation for sequences.
   *
   * @param aa An arbitrary implementation for the type over which the sequence is defined.
   * @return An arbitrary implementation for sequences.
   */
  @SuppressWarnings("unchecked")
  public static <A> Arbitrary<Seq<A>> arbSeq(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(Seq::iterableSeq));
  }

	public static <A> Arbitrary<Set<A>> arbSet(Ord<A> ord, final Arbitrary<A> aa) {
		return arbitrary(arbList(aa).gen.map(list -> Set.iterableSet(ord, list)));
	}

    public static <A> Arbitrary<Set<A>> arbSet(Ord<A> ord, final Arbitrary<A> aa, int max) {
        Gen<Set<A>> g = choose(0, max).bind(i -> Gen.sequenceN(i, aa.gen)).map(list -> Set.iterableSet(ord, list));
        return arbitrary(g);
    }


    /**
   * Returns an arbitrary implementation for throwables.
   *
   * @param as An arbitrary used for the throwable message.
   * @return An arbitrary implementation for throwables.
   */
  public static Arbitrary<Throwable> arbThrowable(final Arbitrary<String> as) {
    return arbitrary(as.gen.map(Throwable::new));
  }

  /**
   * An arbitrary implementation for throwables.
   */
  public static final Arbitrary<Throwable> arbThrowable = arbThrowable(arbString);

  // BEGIN java.util

  /**
   * Returns an arbitrary implementation for array lists.
   *
   * @param aa An arbitrary implementation for the type over which the array list is defined.
   * @return An arbitrary implementation for array lists.
   */
  public static <A> Arbitrary<ArrayList<A>> arbArrayList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(Array::toJavaList));
  }

  /**
   * An arbitrary implementation for bit sets.
   */
  public static final Arbitrary<BitSet> arbBitSet =
      arbitrary(arbList(arbBoolean).gen.map(bs -> {
        final BitSet s = new BitSet(bs.length());
        bs.zipIndex().foreachDoEffect(bi -> s.set(bi._2(), bi._1()));
        return s;
      }));

  /**
   * An arbitrary implementation for calendars.
   */
  public static final Arbitrary<Calendar> arbCalendar = arbitrary(arbLong.gen.map(i -> {
    final Calendar c = Calendar.getInstance();
    c.setTimeInMillis(i);
    return c;
  }));

  /**
   * An arbitrary implementation for dates.
   */
  public static final Arbitrary<Date> arbDate = arbitrary(arbLong.gen.map(Date::new));

  /**
   * Returns an arbitrary implementation for a Java enumeration.
   *
   * @param clazz The type of enum to return an arbtrary of.
   * @return An arbitrary for instances of the supplied enum type.
   */
  public static <A extends Enum<A>> Arbitrary<A> arbEnumValue(final Class<A> clazz) {
    return arbitrary(elements(clazz.getEnumConstants()));
  }

  /**
   * Returns an arbitrary implementation for enum maps.
   *
   * @param ak An arbitrary implementation for the type over which the enum map's keys are defined.
   * @param av An arbitrary implementation for the type over which the enum map's values are
   *           defined.
   * @return An arbitrary implementation for enum maps.
   */
  public static <K extends Enum<K>, V> Arbitrary<EnumMap<K, V>> arbEnumMap(final Arbitrary<K> ak,
                                                                           final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(EnumMap::new));
  }

  /**
   * Returns an arbitrary implementation for enum sets.
   *
   * @param aa An arbitrary implementation for the type over which the enum set is defined.
   * @return An arbitrary implementation for enum sets.
   */
  public static <A extends Enum<A>> Arbitrary<EnumSet<A>> arbEnumSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> copyOf(a.asJavaList())));
  }

  /**
   * An arbitrary implementation for gregorian calendars.
   */
  public static final Arbitrary<GregorianCalendar> arbGregorianCalendar =
      arbitrary(arbLong.gen.map(i -> {
        final GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(i);
        return c;
      }));

  /**
   * Returns an arbitrary implementation for hash maps.
   *
   * @param ak An arbitrary implementation for the type over which the hash map's keys are defined.
   * @param av An arbitrary implementation for the type over which the hash map's values are
   *           defined.
   * @return An arbitrary implementation for hash maps.
   */
  public static <K, V> Arbitrary<HashMap<K, V>> arbHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(HashMap::new));
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Arbitrary<HashSet<A>> arbHashSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new HashSet<>(a.asJavaList())));
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
  public static <K, V> Arbitrary<Hashtable<K, V>> arbHashtable(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbList(ak).gen.bind(arbList(av).gen, ks -> vs -> {
      final Hashtable<K, V> t = new Hashtable<>();

      ks.zip(vs).foreachDoEffect(kv -> t.put(kv._1(), kv._2()));

      return t;
    }));
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
  public static <K, V> Arbitrary<IdentityHashMap<K, V>> arbIdentityHashMap(final Arbitrary<K> ak,
                                                                           final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(IdentityHashMap::new));
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
  public static <K, V> Arbitrary<LinkedHashMap<K, V>> arbLinkedHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(LinkedHashMap::new));
  }

  /**
   * Returns an arbitrary implementation for hash sets.
   *
   * @param aa An arbitrary implementation for the type over which the hash set is defined.
   * @return An arbitrary implementation for hash sets.
   */
  public static <A> Arbitrary<LinkedHashSet<A>> arbLinkedHashSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedHashSet<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for linked lists.
   *
   * @param aa An arbitrary implementation for the type over which the linked list is defined.
   * @return An arbitrary implementation for linked lists.
   */
  public static <A> Arbitrary<LinkedList<A>> arbLinkedList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedList<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for priority queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority queue is defined.
   * @return An arbitrary implementation for priority queues.
   */
  public static <A> Arbitrary<PriorityQueue<A>> arbPriorityQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new PriorityQueue<>(a.asJavaList())));
  }

  /**
   * An arbitrary implementation for properties.
   */
  public static final Arbitrary<Properties> arbProperties =
      arbitrary(arbHashtable(arbString, arbString).gen.map(ht -> {
        final Properties p = new Properties();

        for (final Map.Entry<String, String> entry : ht.entrySet()) {
          p.setProperty(entry.getKey(), entry.getValue());
        }

        return p;
      }));

  /**
   * Returns an arbitrary implementation for stacks.
   *
   * @param aa An arbitrary implementation for the type over which the stack is defined.
   * @return An arbitrary implementation for stacks.
   */
  public static <A> Arbitrary<Stack<A>> arbStack(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> {
      final Stack<A> s = new Stack<>();
      s.addAll(a.asJavaList());
      return s;
    }));
  }

  /**
   * Returns an arbitrary implementation for java.util tree maps.
   *
   * @param ak An arbitrary implementation for the type over which the tree map's keys are defined.
   * @param av An arbitrary implementation for the type over which the tree map's values are
   *           defined.
   * @return An arbitrary implementation for tree maps.
   */
  public static <K, V> Arbitrary<java.util.TreeMap<K, V>> arbJavaTreeMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(java.util.TreeMap::new));
  }

    /**
     * Returns an arbitrary implementation for tree maps.
     */
    public static <K, V> Arbitrary<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Arbitrary<List<P2<K, V>>> al) {
        return arbitrary(al.gen.map(list -> fj.data.TreeMap.iterableTreeMap(ord, list)));
    }

    /**
     * Returns an arbitrary implementation for tree maps.
     */
    public static <K, V> Arbitrary<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Arbitrary<K> ak, Arbitrary<V> av) {
        return arbTreeMap(ord, arbList(arbP2(ak, av)));
    }

    /**
     * Returns an arbitrary implementation for tree maps where the map size is the given arbitrary integer.
     */
    public static <K, V> Arbitrary<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Arbitrary<K> ak, Arbitrary<V> av, Arbitrary<Integer> ai) {
        Gen<List<P2<K, V>>> gl2 = ai.gen.bind(i -> {
            if (i < 0) {
                throw Bottom.error("Undefined: arbitrary natural is negative (" + i + ")");
            }
            return Gen.sequenceN(Math.max(i, 0), arbP2(ak, av).gen);
        });
        return arbTreeMap(ord, arbitrary(gl2));
    }

    /**
     * Returns an arbitrary implementation for tree maps where the size is less than or equal to the max size.
     */
    public static <K, V> Arbitrary<fj.data.TreeMap<K, V>> arbTreeMap(Ord<K> ord, Arbitrary<K> ak, Arbitrary<V> av, int maxSize) {
        if (maxSize < 0) {
          throw Bottom.error("Undefined: arbitrary natural is negative (" + maxSize + ")");
        }
        return arbTreeMap(ord, ak, av, arbitrary(choose(0, maxSize)));
    }

  /**
   * Returns an arbitrary implementation for tree sets.
   *
   * @param aa An arbitrary implementation for the type over which the tree set is defined.
   * @return An arbitrary implementation for tree sets.
   */
  public static <A> Arbitrary<TreeSet<A>> arbTreeSet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new TreeSet<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for vectors.
   *
   * @param aa An arbitrary implementation for the type over which the vector is defined.
   * @return An arbitrary implementation for vectors.
   */
  @SuppressWarnings("UseOfObsoleteCollectionType")
  public static <A> Arbitrary<Vector<A>> arbVector(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new Vector<>(a.asJavaList())));
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
  public static <K, V> Arbitrary<WeakHashMap<K, V>> arbWeakHashMap(final Arbitrary<K> ak, final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(WeakHashMap::new));
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
  public static <A> Arbitrary<ArrayBlockingQueue<A>> arbArrayBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.bind(arbInteger.gen, arbBoolean.gen,
        a -> capacity -> fair -> new ArrayBlockingQueue<A>(a.length() + abs(capacity),
                                         fair, a.asJavaList())));
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
  public static <K, V> Arbitrary<ConcurrentHashMap<K, V>> arbConcurrentHashMap(final Arbitrary<K> ak,
                                                                               final Arbitrary<V> av) {
    return arbitrary(arbHashtable(ak, av).gen.map(ConcurrentHashMap::new));
  }

  /**
   * Returns an arbitrary implementation for concurrent linked queues.
   *
   * @param aa An arbitrary implementation for the type over which the concurrent linked queue is
   *           defined.
   * @return An arbitrary implementation for concurrent linked queues.
   */
  public static <A> Arbitrary<ConcurrentLinkedQueue<A>> arbConcurrentLinkedQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new ConcurrentLinkedQueue<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array lists.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array list is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array lists.
   */
  public static <A> Arbitrary<CopyOnWriteArrayList<A>> arbCopyOnWriteArrayList(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new CopyOnWriteArrayList<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for copy-on-write array sets.
   *
   * @param aa An arbitrary implementation for the type over which the copy-on-write array set is
   *           defined.
   * @return An arbitrary implementation for copy-on-write array sets.
   */
  public static <A> Arbitrary<CopyOnWriteArraySet<A>> arbCopyOnWriteArraySet(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new CopyOnWriteArraySet<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for delay queues.
   *
   * @param aa An arbitrary implementation for the type over which the delay queue is defined.
   * @return An arbitrary implementation for delay queues.
   */
  public static <A extends Delayed> Arbitrary<DelayQueue<A>> arbDelayQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new DelayQueue<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for linked blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the linked blocking queue is
   *           defined.
   * @return An arbitrary implementation for linked blocking queues.
   */
  public static <A> Arbitrary<LinkedBlockingQueue<A>> arbLinkedBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new LinkedBlockingQueue<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Arbitrary<PriorityBlockingQueue<A>> arbPriorityBlockingQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.map(a -> new PriorityBlockingQueue<>(a.asJavaList())));
  }

  /**
   * Returns an arbitrary implementation for priority blocking queues.
   *
   * @param aa An arbitrary implementation for the type over which the priority blocking queue is
   *           defined.
   * @return An arbitrary implementation for priority blocking queues.
   */
  public static <A> Arbitrary<SynchronousQueue<A>> arbSynchronousQueue(final Arbitrary<A> aa) {
    return arbitrary(arbArray(aa).gen.bind(arbBoolean.gen, a -> fair -> {
      final SynchronousQueue<A> q = new SynchronousQueue<>(fair);
      q.addAll(a.asJavaList());
      return q;
    }));
  }

  // END java.util.concurrent

  // BEGIN java.sql

  /**
   * An arbitrary implementation for SQL dates.
   */
  public static final Arbitrary<java.sql.Date> arbSQLDate = arbitrary(arbLong.gen.map(java.sql.Date::new));

  /**
   * An arbitrary implementation for SQL times.
   */
  public static final Arbitrary<Time> arbTime = arbitrary(arbLong.gen.map(Time::new));

  /**
   * An arbitrary implementation for SQL time stamps.
   */
  public static final Arbitrary<Timestamp> arbTimestamp = arbitrary(arbLong.gen.map(Timestamp::new));

  // END java.sql

  // BEGIN java.math

  /**
   * An arbitrary implementation for big integers.
   */
  public static final Arbitrary<BigInteger> arbBigInteger =
      arbitrary(arbArray(arbByte).gen.bind(arbByte.gen, a -> b -> {
        final byte[] x = new byte[a.length() + 1];

        for (int i = 0; i < a.array().length; i++) {
          x[i] = a.get(i);
        }

        x[a.length()] = b;

        return new BigInteger(x);
      }));

  /**
   * An arbitrary implementation for big decimals.
   */
  public static final Arbitrary<BigDecimal> arbBigDecimal =
      arbitrary(arbBigInteger.gen.map(BigDecimal::new));

  // END java.math

  /**
   * An arbitrary implementation for locales.
   */
  public static final Arbitrary<Locale> arbLocale = arbitrary(elements(getAvailableLocales()));

  /**
   * Returns an arbitrary implementation for product-1 values.
   *
   * @param aa An arbitrary implementation for the type over which the product-1 is defined.
   * @return An arbitrary implementation for product-1 values.
   */
  public static <A> Arbitrary<P1<A>> arbP1(final Arbitrary<A> aa) {
    return arbitrary(aa.gen.map(P::p));
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
  public static <A, B> Arbitrary<P2<A, B>> arbP2(final Arbitrary<A> aa, final Arbitrary<B> ab) {
    return arbitrary(aa.gen.bind(ab.gen, a -> b -> p(a, b)));
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
  public static <A, B, C> Arbitrary<P3<A, B, C>> arbP3(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                       final Arbitrary<C> ac) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, a -> b -> c -> p(a, b, c)));
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
  public static <A, B, C, D> Arbitrary<P4<A, B, C, D>> arbP4(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                             final Arbitrary<C> ac, final Arbitrary<D> ad) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, a -> b -> c -> d -> p(a, b, c, d)));
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
  public static <A, B, C, D, E> Arbitrary<P5<A, B, C, D, E>> arbP5(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                   final Arbitrary<C> ac, final Arbitrary<D> ad,
                                                                   final Arbitrary<E> ae) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, a -> b -> c -> d -> e -> p(a, b, c, d, e)));
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
  public static <A, B, C, D, E, F$> Arbitrary<P6<A, B, C, D, E, F$>> arbP6(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                           final Arbitrary<C> ac, final Arbitrary<D> ad,
                                                                           final Arbitrary<E> ae,
                                                                           final Arbitrary<F$> af) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen,
        a -> b -> c -> d -> e -> f -> p(a, b, c, d, e, f)));
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
  public static <A, B, C, D, E, F$, G> Arbitrary<P7<A, B, C, D, E, F$, G>> arbP7(final Arbitrary<A> aa,
                                                                                 final Arbitrary<B> ab,
                                                                                 final Arbitrary<C> ac,
                                                                                 final Arbitrary<D> ad,
                                                                                 final Arbitrary<E> ae,
                                                                                 final Arbitrary<F$> af,
                                                                                 final Arbitrary<G> ag) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen, ag.gen,
        a -> b -> c -> d -> e -> f -> g -> p(a, b, c, d, e, f, g)));
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
  public static <A, B, C, D, E, F$, G, H> Arbitrary<P8<A, B, C, D, E, F$, G, H>> arbP8(final Arbitrary<A> aa,
                                                                                       final Arbitrary<B> ab,
                                                                                       final Arbitrary<C> ac,
                                                                                       final Arbitrary<D> ad,
                                                                                       final Arbitrary<E> ae,
                                                                                       final Arbitrary<F$> af,
                                                                                       final Arbitrary<G> ag,
                                                                                       final Arbitrary<H> ah) {
    return arbitrary(aa.gen.bind(ab.gen, ac.gen, ad.gen, ae.gen, af.gen, ag.gen, ah.gen,
        a -> b -> c -> d -> e -> f -> g -> h -> p(a, b, c, d, e, f, g, h)));
  }
}
