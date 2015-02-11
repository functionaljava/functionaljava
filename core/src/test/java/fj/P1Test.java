package fj;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class P1Test {

  @Test
  public void bug105() throws Exception {
    final P1<String> p1 = P.p("Foo").memo();
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

    org.junit.Assert.assertEquals("Race condition in P1.memo()", 0, nullCounter.get());
  }
}
