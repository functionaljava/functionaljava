package fj.demo.concurrent;

import static fj.Bottom.error;
import fj.Unit;
import fj.control.parallel.Strategy;
import fj.control.parallel.Actor;
import fj.function.Effect1;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Programming with concurrent side-effects.
 * Example of parallel synchronous messaging using Actors in Functional Java.
 * Author: Runar
 */
@SuppressWarnings("ArithmeticOnVolatileField")
public class PingPong {
  private final int actors;
  private final int pings;
  private final Strategy<Unit> s;
  private final Actor<Integer> callback;
  private volatile int done;

  public PingPong(final ExecutorService pool, final int actors, final int pings) {
    this.actors = actors;
    this.pings = pings;
    s = Strategy.executorStrategy(pool);

    // This actor gives feedback to the user that work is being done
    // and also terminates the program when all work has been completed.
    callback = Actor.queueActor(s, i -> {
      done++;
      if (done >= actors) {
        System.out.println("All done.");
        pool.shutdown();
      } else if (actors < 10 || done % (actors / 10) == 0)
        System.out.println(MessageFormat.format("{0} actors done ({1} total pongs).", done, pings * done));
    });
  }

  public static void main(final String[] args) {
    if (args.length < 3)
      throw error("This program takes three arguments: number_of_actors, pings_per_actor, degree_of_parallelism");

    final int actors = Integer.parseInt(args[0]);
    final int pings = Integer.parseInt(args[1]);
    final int threads = Integer.parseInt(args[2]);

    new PingPong(Executors.newFixedThreadPool(threads), actors, pings).start();
  }

  public final void start() {
    // We will use one Pong actor...
    final Pong pong = new Pong(s);

    // ...and an awful lot of Ping actors.
    for (int i = 1; i <= actors; i++) {
      new Ping(s, pings, pong, i, callback).start();
      if (actors < 10 || i % (actors / 10) == 0)
        System.out.println(MessageFormat.format("{0} actors started.", i));
    }
  }
}
