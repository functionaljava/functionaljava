package fj.test.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import fj.P;
import fj.P3;
import fj.data.List;
import fj.data.Option;
import fj.test.CheckResult;
import fj.test.Property;
import fj.test.reflect.Check;
import fj.test.reflect.CheckParams;

public class PropertyTestRunner extends Runner implements Filterable {
  private final Class<?> clas;
  private final List<P3<Property, Option<CheckParams>, Description>> allTests;
  private volatile List<P3<Property, Option<CheckParams>, Description>> filteredTests;

  public PropertyTestRunner(Class<?> clas) {
    this.clas = clas;
    this.allTests = Check.properties(clas).map(p -> P.p(p._1(), p._3(), Description.createTestDescription(clas, p._2())));
    this.filteredTests = allTests;
  }

  @Override
  public final Description getDescription() {
    Description suite = Description.createSuiteDescription(clas);
    filteredTests.foreachDoEffect(p -> suite.addChild(p._3()));
    return suite;
  }

  @Override
  public final void run(RunNotifier notifier) {
    filteredTests.foreachDoEffect(p -> {
      Description desc = p._3();
      notifier.fireTestStarted(desc);
      CheckResult result = checkProperty(p._1(), p._2());

      try {
          String s = CheckResult.summaryEx.showS(result);
          System.out.println(getLabel(desc) + ": " + s);
      } catch (Throwable t) {
        notifier.fireTestFailure(new Failure(desc, t));
      }

      notifier.fireTestFinished(desc);
    });
  }

    private static String getLabel(Description d) {
        return d.getDisplayName();
    }

  private static CheckResult checkProperty(Property prop, Option<CheckParams> params) {
    for (CheckParams ps : params) {
      return prop.check(ps.minSuccessful(), ps.maxDiscarded(), ps.minSize(), ps.maxSize());
    }

    return prop.check();
  }

  @Override
  public final void filter(Filter filter) throws NoTestsRemainException {
    filteredTests = allTests.filter(p -> filter.shouldRun(p._3()));
    if (filteredTests.isEmpty()) { throw new NoTestsRemainException(); }
  }
}
