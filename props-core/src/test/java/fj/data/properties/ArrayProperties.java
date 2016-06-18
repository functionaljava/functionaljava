package fj.data.properties;

import fj.*;
import fj.data.Array;
import fj.data.Either;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fj.Equal.arrayEqual;
import static fj.Equal.intEqual;
import static fj.Function.compose;
import static fj.Function.identity;
import static fj.data.Array.array;
import static fj.data.Array.empty;
import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class ArrayProperties {

  private static final Equal<Array<Integer>> eq = arrayEqual(intEqual);

  private static final Gen<P2<Array<Integer>, Integer>> arbArrayWithIndex = arbArray(arbInteger)
    .filter(Array::isNotEmpty)
    .bind(array -> Gen.choose(0, array.length() - 1).map(i -> P.p(array, i)));

  public Property isEmpty() {
    return property(arbArray(arbInteger), array -> prop(array.isEmpty() != array.isNotEmpty()));
  }

  public Property isNotEmpty() {
    return property(arbArray(arbInteger), array -> prop(array.length() > 0 == array.isNotEmpty()));
  }

  public Property toOption() {
    return property(arbArray(arbInteger), array ->
      prop(array.toOption().isNone() || intEqual.eq(array.toOption().some(), array.get(0))));
  }

  public Property toEither() {
    return property(arbArray(arbInteger), arbP1(arbInteger), (array, n) -> {
      final Either<Integer, Integer> e = array.toEither(n);
      return prop(e.isLeft() && intEqual.eq(e.left().value(), n._1()) ||
        intEqual.eq(e.right().value(), array.get(0)));
    });
  }

  public Property mapId() {
    return property(arbArray(arbInteger), array -> prop(eq.eq(array.map(identity()), array)));
  }

  public Property mapCompose() {
    final F<Integer, Integer> f = x -> x + 3;
    final F<Integer, Integer> g = x -> x * 4;
    return property(arbArray(arbInteger), array ->
      prop(eq.eq(array.map(compose(f, g)), array.map(g).map(f))));
  }

  public Property foreachDoEffect() {
    return property(arbArray(arbInteger), array -> {
      int[] acc = {0};
      array.foreachDoEffect(x -> acc[0] += x);

      int acc2 = 0;
      for (int x : array) { acc2 += x; }

      return prop(intEqual.eq(acc[0], acc2));
    });
  }

  public Property filter() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbArray(arbInteger), array -> prop(array.filter(predicate).forall(predicate)));
  }

  public Property filterLength() {
    final F<Integer, Boolean> predicate = (x -> x % 2 == 0);
    return property(arbArray(arbInteger), array -> prop(array.filter(predicate).length() <= array.length()));
  }

  public Property bindLeftIdentity() {
    final F<Integer, Array<Integer>> f = (i -> array(-i));
    return property(arbArray(arbInteger), arbInteger, (array, i) ->
      prop(eq.eq(array(i).bind(f), f.f(i))));
  }

  public Property bindRightIdentity() {
    return property(arbArray(arbInteger), array -> prop(eq.eq(array.bind(Array::array), array)));
  }

  public Property bindAssociativity() {
    final F<Integer, Array<Integer>> f = x -> array(x + 3);
    final F<Integer, Array<Integer>> g = x -> array(x * 4);
    return property(arbArray(arbInteger), array ->
      prop(eq.eq(array.bind(f).bind(g), array.bind(i -> f.f(i).bind(g)))));
  }

  public Property foldRight() {
    return property(arbArray(arbInteger), array ->
      prop(eq.eq(array.foldRight((i, s) -> array(i).append(s), empty()), array)));
  }

  public Property foldLeft() {
    return property(arbArray(arbInteger), array ->
      prop(eq.eq(array.foldLeft((s, i) -> array(i).append(s), empty()),
        array.reverse().foldRight((i, s) -> array(i).append(s), empty()))));
  }

  public Property scans() {
    return property(arbArray(arbInteger), arbInteger, (array, z) -> {
      final F<Integer, F<Integer, Integer>> add = x -> y -> x + y;
      final Array<Integer> left = array.scanLeft(add, z);
      final Array<Integer> right = array.reverse().scanRight(add, z).reverse();
      return prop(eq.eq(left, right));
    });
  }

  public Property scans1() {
    return property(arbArray(arbInteger), array ->
      implies(array.isNotEmpty(), () -> {
        final F<Integer, F<Integer, Integer>> add = x -> y -> x + y;
        final Array<Integer> left = array.scanLeft1(add);
        final Array<Integer> right = array.reverse().scanRight1(add).reverse();
        return prop(eq.eq(left, right));
      }));
  }

  @CheckParams(maxSize = 100)
  public Property sequence() {
    return property(arbArray(arbInteger), arbArray(arbInteger), (array1, array2) ->
      prop(eq.eq(array1.sequence(array2), array1.bind(__ -> array2))));
  }

  public Property reverseIdentity() {
    return property(arbArray(arbInteger), array ->
      prop(eq.eq(array.reverse().reverse(), array)));
  }

  public Property reverse() {
    return property(arbArray(arbInteger), arbArray(arbInteger), (array1, array2) ->
      prop(eq.eq(array1.append(array2).reverse(), array2.reverse().append(array1.reverse()))));
  }

  @CheckParams(minSize = 1)
  public Property reverseIndex() {
    return property(arbArrayWithIndex, p -> {
      final Array<Integer> array = p._1();
      final Integer i = p._2();
      return prop(intEqual.eq(array.reverse().get(i), array.get(array.length() - i - 1)));
    });
  }

  public Property appendLeftIdentity() {
    return property(arbArray(arbInteger), array -> prop(eq.eq(Array.<Integer> empty().append(array), array)));
  }

  public Property appendRightIdentity() {
    return property(arbArray(arbInteger), array -> prop(eq.eq(array.append(empty()), array)));
  }

  public Property appendAssociativity() {
    return property(arbArray(arbInteger), arbArray(arbInteger), arbArray(arbInteger), (array1, array2, array3) ->
      prop(eq.eq(array1.append(array2).append(array3), array1.append(array2.append(array3)))));
  }

  public Property appendLength() {
    return property(arbArray(arbInteger), arbArray(arbInteger), (array1, array2) ->
      prop(array1.append(array2).length() == array1.length() + array2.length()));
  }

  public Property arrayLength() {
    return property(arbArray(arbInteger), array -> prop(array.length() == array.array().length));
  }

  @CheckParams(minSize = 1)
  public Property index() {
    return property(arbArrayWithIndex, p -> {
      final Array<Integer> array = p._1();
      final Integer i = p._2();
      return prop(intEqual.eq(array.get(i), array.array(Integer[].class)[i]));
    });
  }

  public Property forallExists() {
    return property(arbArray(arbInteger), array ->
      prop(array.forall(x -> x % 2 == 0) == !array.exists(x -> x % 2 != 0)));
  }

  public Property find() {
    return property(arbArray(arbInteger), array -> prop(array.find(x -> x % 2 == 0).forall(x -> x % 2 == 0)));
  }

  @CheckParams(maxSize = 500)
  public Property join() {
    return property(arbArray(arbArray(arbInteger)), (Array<Array<Integer>> array) ->
      prop(eq.eq(array.foldRight(Array::append, empty()), Array.join(array))));
  }
}
