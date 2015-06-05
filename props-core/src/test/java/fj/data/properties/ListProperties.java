package fj.data.properties;

import fj.P;
import fj.P2;
import fj.data.List;
import fj.test.runner.PropertyTestRunner;
import fj.test.Gen;
import fj.test.Property;
import org.junit.runner.RunWith;

import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static fj.Equal.intEqual;

/**
 * Created by Zheka Kozlov on 02.06.2015.
 */
@RunWith(PropertyTestRunner.class)
public class ListProperties {

  public Property isPrefixOf() {
    final Gen<P2<List<Integer>, Integer>> gen = arbList(arbInteger).gen.bind(list ->
      Gen.choose(0, list.length()).map(i -> P.p(list, i)));

    return property(arbitrary(gen), pair -> prop(pair._1().take(pair._2()).isPrefixOf(intEqual, pair._1())));
  }

  public Property isSuffixOf() {
    final Gen<P2<List<Integer>, Integer>> gen = arbList(arbInteger).gen.bind(list ->
      Gen.choose(0, list.length()).map(i -> P.p(list, i)));

    return property(arbitrary(gen), pair -> prop(pair._1().drop(pair._2()).isSuffixOf(intEqual, pair._1())));
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
}
