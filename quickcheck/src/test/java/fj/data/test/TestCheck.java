package fj.data.test;

import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static fj.test.Property.prop;
import static fj.test.Property.property;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCheck {

  @Test
  @Timeout(5000)
  void testExceptionsThrownFromGeneratorsArePropagated() {
    Gen<Integer> failingGen = Gen.value(0).map((i) -> {
      throw new RuntimeException("test failure");
    });

    Property p = property(failingGen, (Integer i) -> prop(i == 0));

    CheckResult res = p.check(
        1, /*minSuccessful*/
        0, /*maxDiscarded*/
        0, /*minSize*/
        1 /*maxSize*/
    );
    assertTrue(res.isGenException(), "Exception not propagated!");
  }
}
