package fj.data;

import fj.F0;
import org.junit.Assert;
import org.junit.Test;

public class EvalTest {

  @Test
  public void testNow() {
    Eval<Integer> eval = Eval.now(1);
    Assert.assertEquals(eval.value().intValue(), 1);
    Assert.assertEquals(eval.map(a -> a.toString()).value(), "1");
    Assert.assertEquals(eval.bind(a -> Eval.now(a * 3)).value().intValue(), 3);
  }

  @Test
  public void testLater() {
    InvocationTrackingF<Integer> tracker = new InvocationTrackingF<>(1);
    Eval<Integer> eval = Eval.later(tracker);

    Assert.assertEquals(tracker.getInvocationCounter(), 0);
    Assert.assertEquals(eval.value().intValue(), 1);
    Assert.assertEquals(tracker.getInvocationCounter(), 1);
    eval.value();
    Assert.assertEquals(tracker.getInvocationCounter(), 1);
  }

  @Test
  public void testAlways() {
    InvocationTrackingF<Integer> tracker = new InvocationTrackingF<>(1);
    Eval<Integer> eval = Eval.always(tracker);

    Assert.assertEquals(tracker.getInvocationCounter(), 0);
    Assert.assertEquals(eval.value().intValue(), 1);
    Assert.assertEquals(tracker.getInvocationCounter(), 1);
    eval.value();
    eval.value();
    Assert.assertEquals(tracker.getInvocationCounter(), 3);
  }

  @Test
  public void testDefer() {
    // Make sure that a recursive computation is actually stack-safe.
    Assert.assertEquals(even(200000).value(), "done");
  }

  private static Eval<String> even(int n) {
    return Eval.now(n <= 0).flatMap(b -> b ? Eval.now("done") : Eval.defer(() -> odd(n - 1)));
  }

  private static Eval<String> odd(int n) {
    return Eval.defer(() -> even(n - 1));
  }

  private static class InvocationTrackingF<A> implements F0<A> {

    private final A value;
    private int invocationCounter;

    public InvocationTrackingF(A value) {
      this.value = value;
      this.invocationCounter = 0;
    }

    @Override
    public A f() {
      invocationCounter++;
      return value;
    }

    public int getInvocationCounter() {
      return invocationCounter;
    }
  }
}
