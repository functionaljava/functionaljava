package test;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;

import fj.test.reflect.Category;
import static fj.test.reflect.Check.check;
import fj.test.reflect.CheckParams;
import fj.test.reflect.Name;
import fj.test.reflect.NoCheck;

/*
  Performs four test runs of these properties using various test parameters and categories.

  A test property is any of the following:
    - a static field of type fj.test.Property.
    - a static no-argument method that returns fj.test.Property.
    - a non-static field of type fj.test.Property in a class with a no-argument constructor.
    - a non-static no-argument method that returns fj.test.Property in a class with a no-argument
      constructor.

  Any property annotated with fj.test.reflect.NoCheck will not be checked. A property's categories
  are the union of the fj.testn.reflect.Category values of the enclosing class and itself. Default
  check parameters can be overridden with the fj.testn.reflect.CheckParams annotation. A property
  can have a name associated with it, that is first taken from its fj.test.reflect.Name
  annotation and if it doesn't exist, then the same annotation on the enclosing class.
*/
@SuppressWarnings({"PackageVisibleField", "MethodMayBeStatic"})
@CheckParams(minSuccessful = 500)
@Category("fj.test.reflect demo")
public final class Reflect {
  @Name("Integer Addition Commutes")
  @Category("Passes for demo")
  @CheckParams(minSuccessful = 20)
  Property p1 = property(arbInteger, arbInteger, {int a, int b => prop(a + b == b + a)});

  @Name("Natural Integer Addition yields Natural Integer")
  @Category("Fails for demo")
  @CheckParams(minSuccessful = 1618034, maxDiscarded = 1346269)
  Property p2 = property(arbIntegerBoundaries, arbIntegerBoundaries, shrinkInteger, shrinkInteger,
        {int a, int b => bool(a > 0 && b > 0).implies(a + b > 0)});

  @Category("Passes for demo")
  Property p3 = property(arbStringBuilder,
      {StringBuilder sb => prop(stringBuilderEqual.eq(sb, sb.reverse().reverse()))});

  @Category("Passes for demo")
  Property p4 = property(arbCharacter,
      {char c => prop(stringBuilderEqual.eq(new StringBuilder().append(c), new StringBuilder().append(c).reverse()))});

  @Category("Passes for demo")
  @CheckParams(minSuccessful = 750, maxSize = 40)
  Property p5 = property(arbStringBuilder, arbStringBuilder,
      {StringBuilder x, StringBuilder y =>
        // copy the string builders before performing updates on x and y.
        final StringBuilder xx = new StringBuilder(x);
        final StringBuilder yy = new StringBuilder(y);
        prop(stringBuilderEqual.eq(x.append(y).reverse(), yy.reverse().append(xx.reverse())))});

  @Name("Triangulation")
  @Category("Fails for demo")
  Property p6 = property(arbInteger, {int a => prop(Triangulation.isPositive(a) == (a != 0 && !Triangulation.isNegative(a)))});

  @NoCheck
  Property leave() {
    throw error("this should not be executed");
  }

  /*
  OK, passed 20 tests. (Integer Addition Commutes)
  Falsified after 1 passed test with arguments: [2147483647,1] (Natural Integer Addition yields Natural Integer)
  OK, passed 500 tests.
  OK, passed 500 tests.
  OK, passed 750 tests.
  Falsified after 0 passed tests with arguments: 0 (Triangulation)
  --------------------------------------------------------------------------------
  OK, passed 20 tests. (Integer Addition Commutes)
  OK, passed 500 tests.
  OK, passed 500 tests.
  OK, passed 750 tests.
  --------------------------------------------------------------------------------
  Falsified after 2 passed tests with arguments: [2147483647,2147483647] (Natural Integer Addition yields Natural Integer)
  Falsified after 0 passed tests with arguments: 0 (Triangulation)
  --------------------------------------------------------------------------------
  OK, passed 20 tests. (Integer Addition Commutes)
  Falsified after 0 passed tests with arguments: [1,2147483647] (Natural Integer Addition yields Natural Integer)
  OK, passed 500 tests.
  OK, passed 500 tests.
  OK, passed 750 tests.
  Falsified after 0 passed tests with arguments: 0 (Triangulation)
  --------------------------------------------------------------------------------
  */  
  @SuppressWarnings("unchecked")
  public static void main(final String[] args) {
    printResults(check(list(Reflect.class)));
    // execute only those in the given category
    printResults(check(list(Reflect.class), "Passes for demo"));
    printResults(check(list(Reflect.class), "Fails for demo"));
    printResults(check(list(Reflect.class), "fj.test.reflect demo"));
  }

  private static void printResults(final List<P2<String,CheckResult>> results) {
    results.foreach({P2<String,CheckResult> result =>
        summary.print(result._2());
        out.println(" (" + result._1() + ')');
        });
    out.println("--------------------------------------------------------------------------------");
  }
}
