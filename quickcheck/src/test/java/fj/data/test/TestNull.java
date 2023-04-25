package fj.data.test;

import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;

import org.junit.jupiter.api.Test;

import static fj.test.Property.prop;
import static fj.test.Property.property;

public class TestNull {

  @Test
  void testShowNullParameters() {
    Property p = property(Gen.value(null), (Integer i) -> prop(i != null));
    CheckResult.summary.println(p.check());
  }

}
