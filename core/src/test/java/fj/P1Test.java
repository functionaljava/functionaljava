package fj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class P1Test {

  @Test
  void bug105() throws Exception {
    final P1<String> p1 = P.weakMemo(() -> "Foo");
    final AtomicInteger nullCounter = new AtomicInteger();
    ExecutorService executorService = Executors.newCachedThreadPool();

    for (int i = 0; i < 10000; i++) {
      executorService.submit(() -> {
        if (p1._1() == null) {
          nullCounter.incrementAndGet();
        }
      });
    }

    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.DAYS);

    Assertions.assertEquals(0, nullCounter.get(), "Race condition in P1.memo()");
  }

  @Test
  void bug122() throws Exception {
    final P1<Integer> p1a = P.lazy(() -> 1);
    final P1<Integer> p1b = P.lazy(() -> 1);

    Assertions.assertTrue(p1a.equals(p1b), p1a + " and " + p1b + " should be equal by Object.equals");
  }
}
