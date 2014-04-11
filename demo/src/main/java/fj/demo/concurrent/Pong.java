package fj.demo.concurrent;

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

  public Pong(final Strategy<Unit> s) {
    p = actor(s, new Effect<Ping>() {
      public void e(final Ping m) {
        m.act(Pong.this);
      }
    });
  }

  // Receive a ping
  public P1<Unit> act(final Ping ping) {
    return p.act(ping);
  }
}