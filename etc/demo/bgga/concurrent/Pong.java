package concurrent;

import fj.Effect;
import fj.Unit;
import fj.P1;
import fj.control.parallel.Actor;
import static fj.control.parallel.Actor.actor;
import fj.control.parallel.Strategy;

/**
 * Receives Ping messages concurrently and responds with a Pong message.
 */
public class Pong {

  private final Actor<Ping> p;
  private final Pong self = this;

  public Pong(final Strategy<Unit> s) {
    p = actor(s, { Ping m => m.act(self); });
  }

  // Receive a ping
  public P1<Unit> act(final Ping ping) {
    return p.act(ping);
  }
}
