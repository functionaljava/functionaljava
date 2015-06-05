package fj.test.runner;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import fj.P3;
import fj.data.Option;
import fj.test.CheckResult;
import fj.test.Property;
import fj.test.reflect.Check;
import fj.test.reflect.CheckParams;

public class PropertyTestRunner extends Runner {
  private final Class<?> clas;

  public PropertyTestRunner(Class<?> clas) {
    this.clas = clas;
  }

  @Override
  public Description getDescription() {
    Description suite = Description.createSuiteDescription(clas);
    for (P3<Property, String, Option<CheckParams>> p : Check.properties(clas)) {
      suite.addChild(Description.createTestDescription(clas, p._2()));
    }
    return suite;
  }

  @Override
  public void run(RunNotifier notifier) {
    for (P3<Property, String, Option<CheckParams>> p : Check.properties(clas)) {
      Description desc = Description.createTestDescription(clas, p._2());
      notifier.fireTestStarted(desc);
      CheckResult result = checkProperty(p._1(), p._3());

      try {
        CheckResult.summaryEx.showS(result);
      } catch (Throwable t) {
        notifier.fireTestFailure(new Failure(desc, t));
      }

      notifier.fireTestFinished(desc);
    }
  }

  private static CheckResult checkProperty(Property prop, Option<CheckParams> params) {
    for (CheckParams ps : params) {
      return prop.check(ps.minSuccessful(), ps.maxDiscarded(), ps.minSize(), ps.maxSize());
    }

    return prop.check();
  }
}
