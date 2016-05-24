package fj.demo.test;

import static fj.Bottom.error;

import fj.P2;
import fj.data.List;
import static fj.data.List.list;
import static fj.Equal.stringBuilderEqual;
import static fj.test.Arbitrary.arbCharacter;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbIntegerBoundaries;
import static fj.test.Arbitrary.arbStringBuilder;
import static fj.test.Bool.bool;

import fj.function.Effect1;
import fj.test.CheckResult;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;
import static fj.test.Shrink.shrinkInteger;
import fj.test.reflect.Category;
import static fj.test.reflect.Check.check;
import fj.test.reflect.CheckParams;
import fj.test.reflect.Name;
import fj.test.reflect.NoCheck;

import static java.lang.System.out;

/*
  Performs four test runs of these properties using various test parameters and categories.

  A test property is any of the following:
    - a static field of type reductio.Property.
    - a static no-argument method that returns reductio.Property.
    - a non-static field of type reductio.Property in a class with a no-argument constructor.
    - a non-static no-argument method that returns reductio.Property in a class with a no-argument
      constructor.

  Any property annotated with reductio.reflect.NoCheck will not be checked. A property's categories
  are the union of the reduction.reflect.Category values of the enclosing class and itself. Default
  check parameters can be overridden with the reduction.reflect.CheckParams annotation. A property
  can have a name associated with it, that is first taken from its reductio.reflect.Name
  annotation and if it doesn't exist, then the same annotation on the enclosing class.   
*/
@SuppressWarnings({"PackageVisibleField", "MethodMayBeStatic"})
@CheckParams(minSuccessful = 500)
@Category("reductio.reflect demo")
public final class Reflect {
  @Name("Integer Addition Commutes")
  @Category("Passes for demo")
  @CheckParams(minSuccessful = 20)  
  final Property p1 = property(arbInteger, arbInteger, (a, b) -> prop(a + b == b + a));

  @Name("Natural Integer Addition yields Natural Integer")
  @Category("Fails for demo")
  final Property p2 = property(arbIntegerBoundaries, arbIntegerBoundaries, shrinkInteger, shrinkInteger, (a, b) -> bool(a > 0 && b > 0).implies(a + b > 0));

  @Category("Passes for demo")
  final Property p3 = property(arbStringBuilder, sb -> prop(stringBuilderEqual.eq(sb, sb.reverse().reverse())));

  @Category("Passes for demo")
  final Property p4 = property(arbCharacter, c -> prop(stringBuilderEqual.eq(new StringBuilder().append(c), new StringBuilder().append(c).reverse())));

  @Category("Passes for demo")
  @CheckParams(minSuccessful = 750, maxSize = 40)
  final Property p5 = property(arbStringBuilder, arbStringBuilder, (x, y) -> {
      // copy the string builders before performing updates on x and y.
      final StringBuilder xx = new StringBuilder(x);
      final StringBuilder yy = new StringBuilder(y);
      return prop(stringBuilderEqual.eq(x.append(y).reverse(), yy.reverse().append(xx.reverse())));
  });

  @Name("Triangulation")
  @Category("Fails for demo")
  final Property p6 = property(arbInteger, a -> prop(Triangulation.isPositive(a) == (a != 0 && !Triangulation.isNegative(a))));

  @NoCheck
  Property leave() {
    throw error("this should not be executed");
  }

  @SuppressWarnings("UnusedDeclaration")
  Property leave(final int i) {
    throw error("this should not be executed");
  }

  /*
  OK, passed 20 tests. (Integer Addition Commutes)
  Falsified after 22 passed tests with arguments: [2147483646,13] (Natural Integer Addition yields Natural Integer)
  OK, passed 500 tests. (p3)
  OK, passed 500 tests. (p4)
  OK, passed 750 tests. (p5)
  Falsified after 0 passed tests with argument: 0 (Triangulation)
  --------------------------------------------------------------------------------
  OK, passed 20 tests. (Integer Addition Commutes)
  OK, passed 500 tests. (p3)
  OK, passed 500 tests. (p4)
  OK, passed 750 tests. (p5)
  --------------------------------------------------------------------------------
  Falsified after 0 passed tests with arguments: [2,2147483646] (Natural Integer Addition yields Natural Integer)
  Falsified after 0 passed tests with argument: 0 (Triangulation)
  --------------------------------------------------------------------------------
  OK, passed 20 tests. (Integer Addition Commutes)
  Falsified after 0 passed tests with arguments: [2147483647,1] (Natural Integer Addition yields Natural Integer)
  OK, passed 500 tests. (p3)
  OK, passed 500 tests. (p4)
  OK, passed 750 tests. (p5)
  Falsified after 0 passed tests with arguments: 0 (Triangulation)
  --------------------------------------------------------------------------------
  */
  @SuppressWarnings("unchecked")
  public static void main(final String[] args) {
    printResults(check(list(Reflect.class)));
    // execute only those in the given category
    printResults(check(list(Reflect.class), "Passes for demo"));
    printResults(check(list(Reflect.class), "Fails for demo"));
    printResults(check(list(Reflect.class), "reductio.reflect demo"));
  }

  private static void printResults(final List<P2<String,CheckResult>> results) {
    results.foreachDoEffect(result -> {
        summary.print(result._2());
        out.println(" (" + result._1() + ')');
    });
    out.println("--------------------------------------------------------------------------------");
  }
}
