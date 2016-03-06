package fj.demo.concurrent;

import fj.Unit;
import fj.P1;
import fj.control.parallel.Actor;
import static fj.control.parallel.Actor.actor;
import fj.control.parallel.Strategy;
import fj.function.Effect1;

/**
 * Receives Ping messages concurrently and responds with a Pong message.
 */
public class Pong {

  private final Actor<Ping> p;

  public Pong(final Strategy<Unit> s) {
    p = actor(s, new Effect1<Ping>() {
      public void f(final Ping m) {
        m.act(Pong.this);
      }
    });
  }

  // Receive a ping
  public final P1<Unit> act(final Ping ping) {
    return p.act(ping);
  }
}