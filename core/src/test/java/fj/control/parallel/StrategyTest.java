package fj.control.parallel;

import fj.Ord;
import fj.P;
import fj.P1;
import fj.Unit;
import fj.data.Enumerator;
import fj.data.Java;
import fj.data.List;
import fj.data.Stream;
import org.junit.Test;

import java.util.concurrent.*;

import static fj.control.parallel.Callables.callable;
import static fj.control.parallel.Strategy.*;
import static fj.data.Stream.range;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StrategyTest {

    @Test
    public void testStrategySeq() {
        final Stream<Integer> s = range(Enumerator.intEnumerator, 99, -99, -1);
        assertThat(s.sort(Ord.intOrd, seqStrategy()), is(s.sort(Ord.intOrd)));
    }

    @Test
    public void testStrategyThread() {
        final Stream<Integer> s = range(Enumerator.intEnumerator, 99, -99, -1);
        assertThat(s.sort(Ord.intOrd, simpleThreadStrategy()), is(s.sort(Ord.intOrd)));
    }

    @Test
    public void testStrategyExecutor() {
        final Stream<Integer> s = range(Enumerator.intEnumerator, 99, -99, -1);
        final ExecutorService es = Executors.newFixedThreadPool(10);
        assertThat(s.sort(Ord.intOrd, executorStrategy(es)), is(s.sort(Ord.intOrd)));
    }

    @Test
    public void testStrategyCompletion() {
        final Stream<Integer> s = range(Enumerator.intEnumerator, 99, -99, -1);
        final ExecutorService es = Executors.newFixedThreadPool(10);
        final CompletionService<Unit> cs = new ExecutorCompletionService<>(es);
        assertThat(s.sort(Ord.intOrd, completionStrategy(cs)), is(s.sort(Ord.intOrd)));
    }

    @Test
    public void testStrategyMergeAll() {
        final List<Integer> l = List.range(0, 100);
        final List<P1<Integer>> p1s = mergeAll(l.map(x -> future(x)));
        assertThat(P1.sequence(p1s)._1(), is(l));
    }

    public static <A> Future<A> future(A a) {
        FutureTask<A> ft = new FutureTask<>(() -> a);
        new Thread(ft).start();
        return ft;
    }

    @Test
    public void testStrategyCallables() throws Exception {
        final Strategy<Callable<Integer>> s = strategy(c -> c);
        final Strategy<Callable<Integer>> cs = callableStrategy(s);
        assertThat(callableStrategy(s).par(P.p(callable(1)))._1().call(), is(1));
    }
}
