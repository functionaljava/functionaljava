package fj.control.parallel;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import fj.Effect;
import fj.F;
import fj.Unit;
import fj.P1;
import fj.function.Effect1;

/**
 * Light weight actors for Java. Concurrency is controlled by a parallel Strategy.
 * The Strategy serves as the Actor's execution engine, and as its mailbox.
 * <p/>
 * Given some effect, the Actor performs the effect on its messages using its Strategy, transforming them
 * into instances of fj.P1. The P1 represents a possibly running computation which is executing the effect.
 * <p/>
 * <b>NOTE:</b> A value of this type may generally process more than one message at a time, depending on its Strategy.
 * An actor is not thread-safe unless either its Effect imposes an order on incoming messages or its Strategy is
 * single-threaded.
 *
 * A queue actor which imposes an order on its messages is provided by the {@link #queueActor} static method.
 */
public final class Actor<A> {

  private final Strategy<Unit> s;
  private final F<A, P1<Unit>> f;

  /**
   * An Actor equipped with a queue and which is guaranteed to process one message at a time.
   * With respect to an enqueueing actor or thread, this actor will process messages in the same order
   * as they are sent.
   */
  public static <T> Actor<T> queueActor(final Strategy<Unit> s, final Effect1<T> ea) {
    return actor(Strategy.<Unit>seqStrategy(), new Effect1<T>() {

      // Lock to ensure the actor only acts on one message at a time
      AtomicBoolean suspended = new AtomicBoolean(true);

      // Queue to hold pending messages
      ConcurrentLinkedQueue<T> mbox = new ConcurrentLinkedQueue<T>();

      // Product so the actor can use its strategy (to act on messages in other threads,
      // to handle exceptions, etc.)
      P1<Unit> processor = new P1<Unit>() {
        @Override public Unit _1() {
          // get next item from queue
          T a = mbox.poll();
          // if there is one, process it
          if (a != null) {
            ea.f(a);
            // try again, in case there are more messages
            s.par(this);
          } else {
            // clear the lock
            suspended.set(true);
            // work again, in case someone else queued up a message while we were holding the lock
            work();
          }
          return Unit.unit();
        }
      };
      
      // Effect's body -- queues up a message and tries to unsuspend the actor
      @Override public void f(T a) {
        mbox.offer(a);
        work();
      }

      // If there are pending messages, use the strategy to run the processor
      protected void work() {
        if (!mbox.isEmpty() && suspended.compareAndSet(true, false)) {
          s.par(processor);
        }
      }
    });
  };
  
  private Actor(final Strategy<Unit> s, final F<A, P1<Unit>> e) {
    this.s = s;
    f = a -> s.par(e.f(a));
  }

  /**
   * Creates a new Actor that uses the given parallelization strategy and has the given side-effect.
   *
   * @param s The parallelization strategy to use for the new Actor.
   * @param e The side-effect to apply to messages passed to the Actor.
   * @return A new actor that uses the given parallelization strategy and has the given side-effect.
   */
  public static <A> Actor<A> actor(final Strategy<Unit> s, final Effect1<A> e) {
    return new Actor<A>(s, P1.curry(Effect.f(e)));
  }

  /**
   * Creates a new Actor that uses the given parallelization strategy and has the given side-effect.
   *
   * @param s The parallelization strategy to use for the new Actor.
   * @param e The function projection of a side-effect to apply to messages passed to the Actor.
   * @return A new actor that uses the given parallelization strategy and has the given side-effect.
   */
  public static <A> Actor<A> actor(final Strategy<Unit> s, final F<A, P1<Unit>> e) {
    return new Actor<A>(s, e);
  }

  /**
   * Pass a message to this actor, applying its side-effect to the message. The side-effect is applied in a concurrent
   * computation, resulting in a product referencing that computation.
   *
   * @param a The message to send to this actor.
   * @return A unit-product that represents the action running concurrently.
   */
  public P1<Unit> act(final A a) {
    return f.f(a);
  }

  /**
   * Contravariant functor pattern. Creates a new actor whose message is transformed by the given function
   * before being passed to this actor.
   *
   * @param f The function to use for the transformation
   * @return A new actor which passes its messages through the given function, to this actor.
   */
  public <B> Actor<B> comap(final F<B, A> f) {
    return actor(s, (B b) -> act(f.f(b)));
  }

  /**
   * Transforms this actor to an actor on promises.
   *
   * @return A new actor, equivalent to this actor, that acts on promises.
   */
  public Actor<Promise<A>> promise() {
    return actor(s, (Promise<A> b) -> b.to(Actor.this));
  }

}
