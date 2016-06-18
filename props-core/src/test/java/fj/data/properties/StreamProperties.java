package fj.data.properties;

import fj.*;
import fj.data.Either;
import fj.data.Stream;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.Equal.intEqual;
import static fj.Equal.p1Equal;
import static fj.Equal.streamEqual;
import static fj.Function.compose;
import static fj.Function.identity;
import static fj.Ord.intOrd;
import static fj.data.Stream.nil;
import static fj.data.Stream.single;
import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.Math.abs;

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class StreamProperties {

  private static final Equal<Stream<Integer>> eq = streamEqual(intEqual);

  public Property isEmpty() {
    return property(arbStream(arbInteger), stream -> prop(stream.isEmpty() != stream.isNotEmpty()));
  }

  public Property isNotEmpty() {
    return property(arbStream(arbInteger), stream ->
      prop(stream.length() > 0 == stream.isNotEmpty()));
  }

  public Property orHead() {
    return property(arbStream(arbInteger), arbInteger, (stream, n) ->
      implies(stream.isNotEmpty(), () -> prop(intEqual.eq(stream.orHead(() -> n), stream.head()))));
  }

  public Property orTail() {
    final Equal<P1<Stream<Integer>>> eq = p1Equal(streamEqual(intEqual));
    return property(arbStream(arbInteger), arbP1(arbStream(arbInteger)), (stream, stream2) ->
      implies(stream.isNotEmpty(), () -> prop(eq.eq(stream.orTail(stream2), stream.tail()))));
  }

  public Property bindStackOverflow() {
    return property(arbInteger, n -> {
      final Stream<Integer> stream = Stream.range(1, abs(n));
      final Stream<Integer> bound = stream.bind(Stream::single);
      return prop(stream.zip(bound).forall(p2 -> intEqual.eq(p2._1(), p2._2())));
    });
  }

  public Property toOption() {
    return property(arbStream(arbInteger), stream ->
      prop(stream.toOption().isNone() || intEqual.eq(stream.toOption().some(), stream.head())));
  }

  public Property toEither() {
    return property(arbStream(arbInteger), arbP1(arbInteger), (stream, n) -> {
      final Either<Integer, Integer> e = stream.toEither(n);
      return prop(e.isLeft() && intEqual.eq(e.left().value(), n._1()) ||
        intEqual.eq(e.right().value(), stream.head()));
    });
  }

  public Property consHead() {
    return property(arbStream(arbInteger), arbInteger, (stream, n) ->
      prop(intEqual.eq(stream.cons(n).head(), n)));
  }

  public Property consLength() {
    return property(arbStream(arbInteger), arbInteger, (stream, n) ->
      prop(stream.cons(n).length() == stream.length() + 1));
  }

  public Property mapId() {
    return property(arbStream(arbInteger), stream -> prop(eq.eq(stream.map(identity()), stream)));
  }

  public Property mapCompose() {
    final F<Integer, Integer> f = x -> x + 3;
    final F<Integer, Integer> g = x -> x * 4;
    return property(arbStream(arbInteger), stream ->
      prop(eq.eq(stream.map(compose(f, g)), stream.map(g).map(f))));
  }

  public Property foreachDoEffect() {
    return property(arbStream(arbInteger), stream -> {
      int[] acc = {0};
      stream.foreachDoEffect(x -> acc[0] += x);

      int acc2 = 0;
      for (int x : stream) { acc2 += x; }

      return prop(intEqual.eq(acc[0], acc2));
    });
  }

  public Property filter() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbStream(arbInteger), stream -> prop(stream.filter(predicate).forall(predicate)));
  }

  public Property filterLength() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbStream(arbInteger), stream -> prop(stream.filter(predicate).length() <= stream.length()));
  }

  public Property bindLeftIdentity() {
    final F<Integer, Stream<Integer>> f = (i -> single(-i));
    return property(arbStream(arbInteger), arbInteger, (stream, i) ->
      prop(eq.eq(single(i).bind(f), f.f(i))));
  }

  public Property bindRightIdentity() {
    return property(arbStream(arbInteger), stream -> prop(eq.eq(stream.bind(Stream::single), stream)));
  }

  public Property bindAssociativity() {
    final F<Integer, Stream<Integer>> f = x -> single(x + 3);
    final F<Integer, Stream<Integer>> g = x -> single(x * 4);
    return property(arbStream(arbInteger), stream ->
      prop(eq.eq(stream.bind(f).bind(g), stream.bind(i -> f.f(i).bind(g)))));
  }

  @CheckParams(maxSize = 100)
  public Property sequence() {
    return property(arbStream(arbInteger), arbStream(arbInteger), (stream1, stream2) ->
      prop(eq.eq(stream1.sequence(stream2), stream1.bind(__ -> stream2))));
  }

  public Property append() {
    return property(arbStream(arbInteger), arbInteger, (stream, i) ->
      prop(eq.eq(single(i).append(stream), stream.cons(i))));
  }

  public Property foldRight() {
    return property(arbStream(arbInteger), stream ->
      prop(eq.eq(stream.foldRight((i, s) -> single(i).append(s), nil()), stream)));
  }

  public Property foldLeft() {
    return property(arbStream(arbInteger), stream ->
      prop(eq.eq(stream.foldLeft((s, i) -> single(i).append(s), nil()),
        stream.reverse().foldRight((i, s) -> single(i).append(s), nil()))));
  }

  public Property tailLength() {
    return property(arbStream(arbInteger), stream ->
      implies(stream.isNotEmpty(), () -> prop(stream.tail()._1().length() == stream.length() - 1)));
  }

  public Property reverseIdentity() {
    return property(arbStream(arbInteger), stream ->
      prop(eq.eq(stream.reverse().reverse(), stream)));
  }

  public Property reverse() {
    return property(arbStream(arbInteger), arbStream(arbInteger), (stream1, stream2) ->
      prop(eq.eq(stream1.append(stream2).reverse(), stream2.reverse().append(stream1.reverse()))));
  }

  @CheckParams(minSize = 2, maxSize = 10000)
  public Property indexTail() {
    final Gen<P2<Stream<Integer>, Integer>> gen = arbStream(arbInteger)
      .filter(stream -> stream.length() > 1)
      .bind(stream -> Gen.choose(1, stream.length() - 1).map(i -> P.p(stream, i)));

    return property(gen, pair -> {
      final Stream<Integer> stream = pair._1();
      final int i = pair._2();
      return prop(intEqual.eq(stream.index(i), stream.tail()._1().index(i - 1)));
    });
  }

  public Property forallExists() {
    return property(arbStream(arbInteger), stream ->
      prop(stream.forall(x -> x % 2 == 0) == !stream.exists(x -> x % 2 != 0)));
  }

  public Property find() {
    return property(arbStream(arbInteger), stream -> prop(stream.find(x -> x % 2 == 0).forall(x -> x % 2 == 0)));
  }

  @CheckParams(maxSize = 500)
  public Property join() {
    return property(arbStream(arbStream(arbInteger)), (Stream<Stream<Integer>> stream) ->
      prop(eq.eq(stream.foldRight((Stream<Integer> i, P1<Stream<Integer>> s) -> i.append(s._1()), nil()),
        Stream.join(stream))));
  }

  @CheckParams(maxSize = 1000)
  public Property sort() {
    return property(arbStream(arbInteger), (Stream<Integer> stream) ->
      prop(eq.eq(stream.sort(intOrd), stream.toList().sort(intOrd).toStream())));
  }
}
