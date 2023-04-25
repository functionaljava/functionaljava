package fj.data;

import fj.F0;
import fj.F2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

public class EvalTest {

  @Test
  void testNow() {
    Eval<Integer> eval = Eval.now(1);
    Assertions.assertEquals(eval.value().intValue(), 1);
    Assertions.assertEquals(eval.map(a -> a.toString()).value(), "1");
    Assertions.assertEquals(eval.bind(a -> Eval.now(a * 3)).value().intValue(), 3);
  }

  @Test
  void testLater() {
    InvocationTrackingF<Integer> tracker = new InvocationTrackingF<>(1);
    Eval<Integer> eval = Eval.later(tracker);

    Assertions.assertEquals(tracker.getInvocationCounter(), 0);
    Assertions.assertEquals(eval.value().intValue(), 1);
    Assertions.assertEquals(tracker.getInvocationCounter(), 1);
    eval.value();
    Assertions.assertEquals(tracker.getInvocationCounter(), 1);
  }

  @Test
  void testAlways() {
    InvocationTrackingF<Integer> tracker = new InvocationTrackingF<>(1);
    Eval<Integer> eval = Eval.always(tracker);

    Assertions.assertEquals(tracker.getInvocationCounter(), 0);
    Assertions.assertEquals(eval.value().intValue(), 1);
    Assertions.assertEquals(tracker.getInvocationCounter(), 1);
    eval.value();
    eval.value();
    Assertions.assertEquals(tracker.getInvocationCounter(), 3);
  }

  @Test
  void testDefer() {
    // Make sure that a recursive computation is actually stack-safe.
    int targetValue = 200000;
    Iterator<Integer> it = Enumerator.intEnumerator.toStream(0).iterator();
    Eval<Boolean> result = foldRight(it, (v, acc) -> v == targetValue ? Eval.now(true) : acc, false);
    Assertions.assertTrue(result.value());
  }

  private static <A, T> Eval<A> foldRight(Iterator<T> iterator,
      F2<T, Eval<A>, Eval<A>> f,
      A zero) {
    if (!iterator.hasNext()) {
      return Eval.now(zero);
    }
    return f.f(iterator.next(), Eval.defer(() -> foldRight(iterator, f, zero)));
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
