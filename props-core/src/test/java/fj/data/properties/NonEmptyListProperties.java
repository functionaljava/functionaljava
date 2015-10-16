package fj.data.properties;

import fj.Equal;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.NonEmptyList;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import fj.test.Property;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

import static fj.Equal.intEqual;
import static fj.Equal.listEqual;
import static fj.Equal.nonEmptyListEqual;
import static fj.Function.identity;
import static fj.data.NonEmptyList.nel;
import static fj.data.NonEmptyList.unzip;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbNonEmptyList;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static java.lang.Math.min;

/**
 * Created by Zheka Kozlov on 02.06.2015.
 */
@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class NonEmptyListProperties {

  private static final Equal<NonEmptyList<Integer>> eq = nonEmptyListEqual(intEqual);
  private static final Equal<List<Integer>> listEq = listEqual(intEqual);

  public Property consHead() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(intEqual.eq(list.cons(n).head(), n)));
  }

  public Property consLength() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(list.cons(n).length() == list.length() + 1));
  }

  public Property positiveLength() {
    return property(arbNonEmptyList(arbInteger), list -> prop(list.length() > 0));
  }

  public Property appendLength() {
    return property(arbNonEmptyList(arbInteger), arbNonEmptyList(arbInteger), (list1, list2) ->
      prop(list1.append(list2).length() == list1.length() + list2.length()));
  }

  public Property appendSingle() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(eq.eq(nel(n).append(list), list.cons(n))));
  }

  public Property tailLength() {
    return property(arbNonEmptyList(arbInteger), list -> prop(list.length() == 1 + list.tail().length()));
  }

  public Property mapId() {
    return property(arbNonEmptyList(arbInteger), list -> prop(eq.eq(list.map(identity()), list)));
  }

  public Property reverse() {
    return property(arbNonEmptyList(arbInteger), list ->
      prop(listEq.eq(list.reverse().toList(), list.tail().reverse().snoc(list.head()))));
  }

  public Property doubleReverse() {
    return property(arbNonEmptyList(arbInteger), list -> prop(eq.eq(list.reverse().reverse(), list)));
  }

  public Property sort() {
    return property(arbNonEmptyList(arbInteger), list -> {
      java.util.List<Integer> javaList = list.sort(Ord.intOrd).toList().toJavaList();
      java.util.List<Integer> copy = new ArrayList<>(javaList);
      Collections.sort(copy);
      return prop(javaList.equals(copy));
    });
  }

  public Property intersperseLength() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) ->
      prop(list.intersperse(n).length() == 2 * list.length() - 1));
  }

  public Property zip() {
    return property(arbNonEmptyList(arbInteger), arbNonEmptyList(arbInteger), (list1, list2) -> {
      final int size = min(list1.length(), list2.length());
      final NonEmptyList<P2<Integer, Integer>> zipped = list1.zip(list2);
      return prop(listEq.eq(zipped.map(P2::_1).toList(), list1.toList().take(size)))
        .and(prop(listEq.eq(zipped.map(P2::_2).toList(), list2.toList().take(size))));
    });
  }

  public Property zipUnzip() {
    return property(arbNonEmptyList(arbInteger), arbNonEmptyList(arbInteger), (list1, list2) -> {
      final P2<NonEmptyList<Integer>, NonEmptyList<Integer>> unzipped = unzip(list1.zip(list2));
      final int size = min(list1.length(), list2.length());
      return prop(listEq.eq(unzipped._1().toList(), list1.toList().take(size)))
        .and(prop(listEq.eq(unzipped._2().toList(), list2.toList().take(size))));
    });

  }
}
