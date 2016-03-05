package fj.demo.test;

import fj.P;
import fj.control.parallel.Strategy;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.propertyP;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
Checks the commutative property of addition with a large set of parallel tests.
*/
public final class AdditionCommutesParallel {
  public static void main(final String[] args) {
    final ExecutorService pool = Executors.newFixedThreadPool(8);
    final Strategy<Property> s = Strategy.executorStrategy(pool);
    final Property p = propertyP(arbInteger, arbInteger, (a, b) -> s.par(P.lazy(() -> prop(a + b == b + a))));
    summary.println(p.check(1000000, 5000000, 0, 100)); // OK, passed 1000000 tests.
    pool.shutdown();
  }
}
