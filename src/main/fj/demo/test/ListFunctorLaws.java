package fj.demo.test;

import fj.Effect;
import fj.F;
import fj.F3;
import fj.Function;
import static fj.Function.compose;
import fj.P2;
import fj.data.List;
import static fj.Equal.listEqual;
import static fj.Equal.stringEqual;
import static fj.test.Arbitrary.arbF;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbLong;
import static fj.test.Arbitrary.arbString;
import fj.test.CheckResult;
import static fj.test.CheckResult.summary;
import static fj.test.Coarbitrary.coarbInteger;
import static fj.test.Coarbitrary.coarbLong;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static fj.test.reflect.Check.check;
import fj.test.reflect.CheckParams;

/*
Checks the two functor laws on List.map. These laws are:
1) The Law of Identity
forall x. map identity x == x

For any list, mapping the identity function (\x -> x) produces the same list.

2) The Law of Composition
forall f. forall g. forall x. map (f . g) x == map f (map g x)

...where (f . g) denotes composition of f with g. That is, \c -> f(g(c)).

Note that to test this second law requires the generation of arbitrary functions.
*/
@SuppressWarnings({"PackageVisibleField"})
@CheckParams(minSuccessful = 1000)
public final class ListFunctorLaws {
  final Property identity = property(arbList(arbString), new F<List<String>, Property>() {
    public Property f(final List<String> x) {
      return prop(listEqual(stringEqual).eq(x, x.map(Function.<String>identity())));
    }
  });

  final Property composition = property(arbF(coarbInteger, arbString), arbF(coarbLong, arbInteger), arbList(arbLong), new F3<F<Integer, String>, F<Long, Integer>, List<Long>, Property>() {
    public Property f(final F<Integer, String> f, final F<Long, Integer> g, final List<Long> x) {
      final List<String> s1 = x.map(compose(f, g));
      final List<String> s2 = x.map(g).map(f);
      return prop(listEqual(stringEqual).eq(s1, s2));
    }
  });

  // identity: OK, passed 1000 tests.
  // composition: OK, passed 1000 tests.
  @SuppressWarnings("unchecked")
  public static void main(final String[] args) {
    check(ListFunctorLaws.class).foreach(new Effect<P2<String, CheckResult>>() {
      public void e(final P2<String, CheckResult> r) {
        System.out.print(r._1() + ": ");
        summary.println(r._2());
      }
    });
  }
}
