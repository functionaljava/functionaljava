package concurrent;

import fj.Unit;
import fj.P1;
import fj.control.parallel.Actor;
import static fj.control.parallel.Actor.actor;
import fj.control.parallel.Strategy;

/**
 * Pings a Pong actor a given number of times, one at a time, and calls back with its ID when done.
 */
public class Ping {

  private final Pong pong;
  private final Actor<Pong> ping;
  private final Actor<Integer> cb;
  private final Ping self = this;
  private volatile int n;

  public Ping(final Strategy<Unit> s, final int i, final Pong pong, final int id, final Actor<Integer> callback) {
    n = i;
    this.pong = pong;
    cb = callback;
    ping = actor(s, { Pong pong => {
      n--;
      if (n > 0)
        pong.act(self);
      else
        cb.act(id); // Done. Notify caller.
    }});
  }

  // Commence pinging
  public P1<Unit> start() {
    return pong.act(self);
  }

  // Receive a pong
  public P1<Unit> act(final Pong p) {
    return ping.act(p);
  }
}
