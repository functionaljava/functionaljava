package fj.demo.concurrent;

import static fj.Bottom.error;

import fj.F;
import fj.P;
import fj.P2;
import fj.Unit;
import fj.data.List;
import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.function.Effect1;

import static fj.data.List.range;
import static fj.function.Integers.add;
import static fj.control.parallel.Promise.join;
import static fj.control.parallel.Promise.promise;
import static fj.control.parallel.Actor.actor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Parallel Fibonacci numbers.
 * Based on a Haskell example by Don Stewart.
 * Author: Runar
 */
public class Fibs
  {private static final int CUTOFF = 35;

   public static void main(final String[] args)
     {if (args.length < 1)
         throw error("This program takes an argument: number_of_threads");

      final int threads = Integer.parseInt(args[0]);
      final ExecutorService pool = Executors.newFixedThreadPool(threads);
      final Strategy<Unit> su = Strategy.executorStrategy(pool);
      final Strategy<Promise<Integer>> spi = Strategy.executorStrategy(pool);

      // This actor performs output and detects the termination condition.
      final Actor<List<Integer>> out = actor(su, new Effect1<List<Integer>>()
        {public void f(final List<Integer> fs)
          {for (final P2<Integer, Integer> p : fs.zipIndex())
               {System.out.println(MessageFormat.format("n={0} => {1}", p._2(), p._1()));}
           pool.shutdown();}});

      // A parallel recursive Fibonacci function
      final F<Integer, Promise<Integer>> fib = new F<Integer, Promise<Integer>>()
        {public Promise<Integer> f(final Integer n)
          {return n < CUTOFF ? promise(su, P.p(seqFib(n))) : f(n - 1).bind(f(n - 2), add);}};

      System.out.println("Calculating Fibonacci sequence in parallel...");
      join(su, spi.parMap(fib, range(0, 46)).map(Promise.sequence(su))).to(out);}

  // The sequential version of the recursive Fibonacci function
  public static int seqFib(final int n)
    {return n < 2 ? n : seqFib(n - 1) + seqFib(n - 2);}}

