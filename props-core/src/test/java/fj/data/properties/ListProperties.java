package fj.data.properties;

import fj.*;
import fj.data.List;
import fj.data.Stream;
import fj.data.TreeMap;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import fj.test.Gen;
import fj.test.Property;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

import static fj.Equal.listEqual;
import static fj.Equal.p2Equal;
import static fj.Function.compose;
import static fj.Function.identity;
import static fj.P.p;
import static fj.data.List.nil;
import static fj.data.List.single;
import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static fj.Equal.intEqual;
import static fj.Monoid.intAdditionMonoid;
import static fj.Ord.booleanOrd;
import static fj.Ord.intOrd;

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class ListProperties {

  private static final Equal<List<Integer>> eq = listEqual(intEqual);

  private static final Gen<P2<List<Integer>, Integer>> arbListWithIndex = arbList(arbInteger)
    .filter(List::isNotEmpty)
    .bind(list -> Gen.choose(0, list.length() - 1).map(i -> p(list, i)));

  public Property isEmpty() {
    return property(arbList(arbInteger), list -> prop(list.isEmpty() != list.isNotEmpty()));
  }

  public Property isNotEmpty() {
    return property(arbList(arbInteger), list -> prop(list.length() > 0 == list.isNotEmpty()));
  }

  public Property orHead() {
    return property(arbList(arbInteger), arbInteger, (list, n) ->
      implies(list.isNotEmpty(), () -> prop(intEqual.eq(list.orHead(() -> n), list.head()))));
  }

  public Property orTail() {
    return property(arbList(arbInteger), arbP1(arbList(arbInteger)), (list, list2) ->
      implies(list.isNotEmpty(), () -> prop(eq.eq(list.orTail(list2), list.tail()))));
  }

  public Property toOption() {
    return property(arbList(arbInteger), list ->
      prop(list.headOption().isNone() || intEqual.eq(list.headOption().some(), list.head())));
  }

  public Property consHead() {
    return property(arbList(arbInteger), arbInteger, (list, n) -> prop(intEqual.eq(list.cons(n).head(), n)));
  }

  public Property consLength() {
    return property(arbList(arbInteger), arbInteger, (list, n) -> prop(list.cons(n).length() == list.length() + 1));
  }

  public Property mapId() {
    return property(arbList(arbInteger), list -> prop(eq.eq(list.map(identity()), list)));
  }

  public Property mapCompose() {
    final F<Integer, Integer> f = x -> x + 3;
    final F<Integer, Integer> g = x -> x * 4;
    return property(arbList(arbInteger), list ->
      prop(eq.eq(list.map(compose(f, g)), list.map(g).map(f))));
  }

  public Property foreachDoEffect() {
    return property(arbList(arbInteger), list -> {
      int[] acc = {0};
      list.foreachDoEffect(x -> acc[0] += x);

      int acc2 = 0;
      for (int x : list) { acc2 += x; }

      return prop(intEqual.eq(acc[0], acc2));
    });
  }

  public Property filter() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbList(arbInteger), list -> prop(list.filter(predicate).forall(predicate)));
  }

  public Property filterLength() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbList(arbInteger), list -> prop(list.filter(predicate).length() <= list.length()));
  }

  public Property bindLeftIdentity() {
    final F<Integer, List<Integer>> f = (i -> single(-i));
    return property(arbList(arbInteger), arbInteger, (list, i) ->
      prop(eq.eq(single(i).bind(f), f.f(i))));
  }

  public Property bindRightIdentity() {
    return property(arbList(arbInteger), list -> prop(eq.eq(list.bind(List::list), list)));
  }

  public Property bindAssociativity() {
    final F<Integer, List<Integer>> f = x -> single(x + 3);
    final F<Integer, List<Integer>> g = x -> single(x * 4);
    return property(arbList(arbInteger), list ->
      prop(eq.eq(list.bind(f).bind(g), list.bind(i -> f.f(i).bind(g)))));
  }

  public Property foldRight() {
    return property(arbList(arbInteger), list ->
      prop(eq.eq(list.foldRight((i, s) -> single(i).append(s), nil()), list)));
  }

  public Property foldLeft() {
    return property(arbList(arbInteger), list ->
      prop(eq.eq(list.foldLeft((s, i) -> single(i).append(s), nil()),
        list.reverse().foldRight((i, s) -> single(i).append(s), nil()))));
  }

  public Property tailLength() {
    return property(arbList(arbInteger), list ->
      implies(list.isNotEmpty(), () -> prop(list.tail().length() == list.length() - 1)));
  }

  public Property reverseIdentity() {
    return property(arbList(arbInteger), list -> prop(eq.eq(list.reverse().reverse(), list)));
  }

  public Property reverse() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      prop(eq.eq(list1.append(list2).reverse(), list2.reverse().append(list1.reverse()))));
  }

  @CheckParams(maxSize = 100)
  public Property sequence() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      prop(eq.eq(list1.sequence(list2), list1.bind(__ -> list2))));
  }

  public Property appendLeftIdentity() {
    return property(arbList(arbInteger), list -> prop(eq.eq(List.<Integer> nil().append(list), list)));
  }

  public Property appendRightIdentity() {
    return property(arbList(arbInteger), list -> prop(eq.eq(list.append(nil()), list)));
  }

  public Property appendAssociativity() {
    return property(arbList(arbInteger), arbList(arbInteger), arbList(arbInteger), (list1, list2, list3) ->
      prop(eq.eq(list1.append(list2).append(list3), list1.append(list2.append(list3)))));
  }

  public Property appendLength() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      prop(list1.append(list2).length() == list1.length() + list2.length()));
  }

  @CheckParams(minSize = 2, maxSize = 10000)
  public Property indexTail() {
    final Gen<P2<List<Integer>, Integer>> gen = arbList(arbInteger)
      .filter(list -> list.length() > 1)
      .bind(list -> Gen.choose(1, list.length() - 1).map(i -> p(list, i)));

    return property(gen, pair -> {
      final List<Integer> list = pair._1();
      final int i = pair._2();
      return prop(intEqual.eq(list.index(i), list.tail().index(i - 1)));
    });
  }

  public Property snoc() {
    return property(arbList(arbInteger), arbInteger, (list, n) -> prop(eq.eq(list.snoc(n), list.append(single(n)))));
  }

  public Property take() {
    return property(arbList(arbInteger), arbInteger, (list, n) -> prop(list.take(n).length() <= list.length()));
  }

  public Property drop() {
    return property(arbList(arbInteger), arbInteger, (list, n) -> prop(list.drop(n).length() <= list.length()));
  }

  public Property splitAt() {
    return property(arbList(arbInteger), arbInteger, (list, n) ->
      prop(p2Equal(eq, eq).eq(list.splitAt(n), p(list.take(n), list.drop(n)))));
  }

  @CheckParams(minSize = 1, maxSize = 2000)
  public Property partition() {
    return property(arbListWithIndex, p -> implies(p._2() > 0, () -> {
      final List<Integer> list = p._1();
      final Integer i = p._2();
      final List<List<Integer>> partition = list.partition(i);
      return prop(eq.eq(list, List.join(partition))).and(prop(partition.forall(part -> part.length() <= i)));
    }));
  }

  @CheckParams(minSize = 1, maxSize = 2000)
  public Property tails() {
    return property(arbList(arbInteger), list -> implies(list.isNotEmpty(), () ->
      prop(list.tails().length() == list.length() + 1 &&
        List.join(list.inits()).length() == Stream.range(1, list.length() + 1).foldLeft((acc, i) -> acc + i, 0))));
  }

  @CheckParams(minSize = 1, maxSize = 2000)
  public Property inits() {
    return property(arbList(arbInteger), list -> implies(list.isNotEmpty(), () ->
      prop(list.inits().length() == list.length() + 1 &&
        List.join(list.tails()).length() == Stream.range(1, list.length() + 1).foldLeft((acc, i) -> acc + i, 0))));
  }

  public Property sort() {
    return property(arbList(arbInteger), list -> {
      java.util.List<Integer> javaList = list.sort(intOrd).toJavaList();
      java.util.List<Integer> copy = new ArrayList<>(javaList);
      Collections.sort(copy);
      return prop(javaList.equals(copy));
    });
  }

  public Property forallExists() {
    return property(arbList(arbInteger), list ->
      prop(list.forall(x -> x % 2 == 0) == !list.exists(x -> x % 2 != 0)));
  }

  public Property find() {
    return property(arbList(arbInteger), list -> prop(list.find(x -> x % 2 == 0).forall(x -> x % 2 == 0)));
  }

  @CheckParams(maxSize = 500)
  public Property join() {
    return property(arbList(arbList(arbInteger)), (List<List<Integer>> lists) ->
      prop(eq.eq(lists.foldLeft(List::append, nil()), List.join(lists))));
  }

  @CheckParams(maxSize = 2000)
  public Property nub() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      prop(eq.eq(list1.append(list2).nub(), list1.nub().append(list2.nub()).nub())));
  }

  public Property groupBy() {
    return property(arbList(arbInteger), list -> {
      final TreeMap<Boolean, List<Integer>> map = list.groupBy(i -> i % 2 == 0, Ord.booleanOrd);
      final List<Integer> list1 = map.get(true).orSome(nil());
      final List<Integer> list2 = map.get(false).orSome(nil());
      return prop(list.length() == list1.length() + list2.length())
        .and(prop(list1.forall(i -> i % 2 == 0)))
        .and(prop(list2.forall(i -> i % 2 != 0)))
        .and(prop(list.map(i -> i % 2 == 0).nub().length() == map.size()));
    });
  }

  public Property groupByMonoid() {
    return property(arbList(arbInteger), list -> {
      final TreeMap<Boolean, Integer> map = list.groupBy(i -> i % 2 == 0, identity(), intAdditionMonoid, booleanOrd);
      final int sum1 = map.get(true).orSome(0);
      final int sum2 = map.get(false).orSome(0);
      return prop(list.filter(i -> i % 2 == 0).foldLeft((acc, i) -> acc + i, 0) == sum1)
        .and(prop(list.filter(i -> i % 2 != 0).foldLeft((acc, i) -> acc + i, 0) == sum2));
    });
  }

  public Property isPrefixOf() {
    final Gen<P2<List<Integer>, Integer>> gen = arbList(arbInteger).bind(list ->
      Gen.choose(0, list.length()).map(i -> p(list, i)));

    return property(gen, pair -> prop(pair._1().take(pair._2()).isPrefixOf(intEqual, pair._1())));
  }

  public Property isSuffixOf() {
    final Gen<P2<List<Integer>, Integer>> gen = arbList(arbInteger).bind(list ->
      Gen.choose(0, list.length()).map(i -> p(list, i)));

    return property(gen, pair -> prop(pair._1().drop(pair._2()).isSuffixOf(intEqual, pair._1())));
  }

  public Property isPrefixOfShorter() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      implies(list1.length() > list2.length(), () -> prop(!list1.isPrefixOf(intEqual, list2))));
  }

  public Property isSuffixOfShorter() {
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      implies(list1.length() > list2.length(), () -> prop(!list1.isSuffixOf(intEqual, list2))));
  }

  public Property isPrefixOfDifferentHeads() {
    return property(arbList(arbInteger), arbList(arbInteger), arbInteger, arbInteger, (list1, list2, h1, h2) ->
      implies(intEqual.notEq(h1, h2), () -> prop(!list1.cons(h1).isPrefixOf(intEqual, list2.cons(h2)))));
  }

  public Property isSuffixOfDifferentHeads() {
    return property(arbList(arbInteger), arbList(arbInteger), arbInteger, arbInteger, (list1, list2, h1, h2) ->
      implies(intEqual.notEq(h1, h2), () -> prop(!list1.snoc(h1).isSuffixOf(intEqual, list2.snoc(h2)))));
  }

  public Property listOrdEqual() {
    return property(arbList(arbInteger), list -> prop(Ord.listOrd(Ord.intOrd).equal().eq(list, list)));
  }

  public Property listOrdReverse() {
    final Ord<List<Integer>> ord = Ord.listOrd(Ord.intOrd);
    return property(arbList(arbInteger), arbList(arbInteger), (list1, list2) ->
      prop(ord.compare(list1, list2) == ord.reverse().compare(list1, list2).reverse()));
  }

}
