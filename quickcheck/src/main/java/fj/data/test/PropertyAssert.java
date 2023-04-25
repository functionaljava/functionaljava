package fj.data.test;

import fj.Unit;
import fj.test.CheckResult;
import fj.test.Property;

import org.junit.jupiter.api.Assertions;

public final class PropertyAssert {

  private PropertyAssert() {
  }

  public static Unit assertResult(Property p) {
    CheckResult cr = p.check();
    CheckResult.summary.println(cr);
    Assertions.assertTrue(cr.isExhausted() || cr.isPassed() || cr.isProven());
    return Unit.unit();
  }

}
