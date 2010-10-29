package concurrent;

import static fj.Bottom.error;
import fj.F;
import fj.Function;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;
import static fj.P1.fmap;
import fj.data.List;
import static fj.data.List.range;
import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import static fj.control.parallel.Promise.join;
import static fj.control.parallel.Promise.promise;
import fj.control.parallel.Strategy;
import static fj.control.parallel.Actor.actor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parallel Fibonacci numbers.
 * Based on a Haskell example by Don Stewart.
 * Author: Runar
 */
public class Fibs {

  private static final int CUTOFF = 35;

  public static void main(final String[] args) throws Exception {
    if (args.length < 1)
      throw error("This program takes an argument: number_of_threads");

    final int threads = Integer.parseInt(args[0]);
    final ExecutorService pool = Executors.newFixedThreadPool(threads);
    final Strategy<Unit> su = Strategy.executorStrategy(pool);
    final Strategy<Promise<Integer>> spi = Strategy.executorStrategy(pool);

    // This actor performs output and detects the termination condition.
    final Actor<List<Integer>> out = actor(su, { List<Integer> fs => {
      for (P2<Integer, Integer> p : fs.zipIndex()) {
        System.out.println(MessageFormat.format("n={0} => {1}", p._2(), p._1()));
      }
      pool.shutdown();       
    }});

    // A parallel recursive Fibonacci function
    final F<Integer, Promise<Integer>> fib = { Integer n => (n < CUTOFF) ?
        promise(su, P.p(seqFib(n))) :
        fib.f(n - 1).bind(join(su, P1.curry(fib).f(n - 2)), { int a => { int b => a + b }} ) };

    System.out.println("Calculating Fibonacci sequence in parallel...");

    join(su, fmap(Promise.<Integer>sequence(su)).f(spi.parMap(fib).f(range(0, 46)))).to(out);
  }

  // The sequential verison of the recursive Fibonacci function
  public static int seqFib(final int n) {
    return n < 2 ? n : seqFib(n - 1) + seqFib(n - 2);
  }
}
