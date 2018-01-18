package fj.data;

import fj.F;
import fj.F0;
import fj.P;
import fj.P1;
import fj.control.Trampoline;

/**
 * <code>Eval</code> is an abstraction over different models of evaluation.
 * The data constructors:
 * <ul>
 *   <li><code>Now</code> - the value is evaluated immediately.</li>
 *   <li><code>Later</code> - the value is evaluated only once when it's requested (lazy evaluation).</li>
 *   <li><code>Always</code> - the value is evaluated every time when it's requested.</li>
 * </ul>
 *
 * Both <code>Later</code> and <code>Always</code> are lazy computations, while <code>Now</code> is eager.
 *
 *
 * @version %build.number%
 */
public abstract class Eval<A> {

  /**
   * Constructs an eager evaluation by wrapping the given value.
   *
   * @param a the evaluated value.
   * @return an eval with computed value.
   */
  public static <A> Eval<A> now(A a) {
    return new Now<>(a);
  }

  /**
   * Constructs a lazy evaluation with caching.
   *
   * @param a the supplier that evaluates a value.
   * @return a lazy evaluation.
   */
  public static <A> Eval<A> later(F0<A> a) {
    return new Later<>(a);
  }

  /**
   * Constructs a lazy evaluation without caching.
   *
   * @param a the supplier that evaluates a value.
   * @return a lazy evaluation.
   */
  public static <A> Eval<A> always(F0<A> a) {
    return new Always<>(a);
  }

  /**
   * Constructs a lazy evaluation of an expression that produces <code>Eval<A></code>.
   * This operation is stack-safe and can be used for recursive computations.
   *
   * @param a the supplier that produces an <code>Eval<A></code>.
   * @return a lazily evaluated nested evaluation.
   */
  public static <A> Eval<A> defer(F0<Eval<A>> a) {
    return new DeferEval<>(a);
  }

  /**
   * Evaluates the computation and return its result.
   * Depending on whether the current instance is lazy or eager the
   * computation may or may not happen at this point.
   *
   * @return a result of this computation.
   */
  public abstract A value();

  /**
   * Transforms <code>Eval<A></code> into a <code>Eval<B></code> using
   * the given function.
   *
   * Note: the computation of the given transformation is always lazy,
   * even if it invoked for an eager <code>Now</code> instance. This computation
   * is performed in O(1) stack space.
   *
   * @param f the transformation function.
   * @return a transformed evaluation.
   */
  public final <B> Eval<B> map(final F<A, B> f) {
    return bind(a -> now(f.f(a)));
  }

  /**
   * Alias for {@link #bind(F)}.
   */
  public final <B> Eval<B> flatMap(final F<A, Eval<B>> f) {
    return bind(f);
  }

  /**
   * Transforms <code>Eval<A></code> into a <code>Eval<B></code> using
   * the given function that directly produces <code>Eval<B></code>.
   *
   * Note: the computation of the given transformation is always lazy,
   * even if it invoked for an eager <code>Now</code> instance. This computation
   * is performed in O(1) stack space.
   *
   * @param f the transformation function.
   * @return a transformed evaluation.
   */
  public final <B> Eval<B> bind(final F<A, Eval<B>> f) {
    return new BindTrampolineEval<>(f, asTrampoline());
  }

  /**
   * Transforms the current instance into a trampoline instance.
   */
  abstract TrampolineEval<A> asTrampoline();

  /**
   * Represents an eager computation.
   */
  private static final class Now<A> extends Eval<A> {
    private final A a;

    Now(A a) {
      this.a = a;
    }

    @Override
    public final A value() {
      return a;
    }

    @Override
    final TrampolineEval<A> asTrampoline() {
      return new PureTrampolineEval<>(this);
    }
  }

  /**
   * Represents a lazy computation that is evaluated only once.
   */
  private static final class Later<A> extends Eval<A> {
    private final P1<A> memo;

    Later(F0<A> producer) {
      this.memo = P.hardMemo(producer);
    }

    @Override
    public final A value() {
      return memo._1();
    }

    @Override
    final TrampolineEval<A> asTrampoline() {
      return new PureTrampolineEval<>(this);
    }
  }

  /**
   * Represents a lazy computation that is evaluated every time when it's requested.
   */
  private static final class Always<A> extends Eval<A> {
    private final F0<A> supplier;

    Always(F0<A> supplier) {
      this.supplier = supplier;
    }

    @Override
    public final A value() {
      return supplier.f();
    }

    @Override
    final TrampolineEval<A> asTrampoline() {
      return new PureTrampolineEval<>(this);
    }
  }

  /**
   * A helper abstraction that allows to perform recursive lazy transformations in O(1) stack space.
   */
  private static abstract class TrampolineEval<A> extends Eval<A> {

    protected abstract Trampoline<A> trampoline();

    @Override
    public final A value() {
      return trampoline().run();
    }

    @Override
    final TrampolineEval<A> asTrampoline() {
      return this;
    }
  }

  private static final class PureTrampolineEval<A> extends TrampolineEval<A> {
    private final Eval<A> start;

    PureTrampolineEval(Eval<A> start) {
      this.start = start;
    }

    @Override
    protected final Trampoline<A> trampoline() {
      return Trampoline.suspend(P.lazy(() -> Trampoline.pure(start.value())));
    }
  }

  private static final class BindTrampolineEval<A, B> extends TrampolineEval<B> {
    private final TrampolineEval<A> next;
    private final F<A, Eval<B>> f;

    BindTrampolineEval(F<A, Eval<B>> f, TrampolineEval<A> next) {
      this.next = next;
      this.f = f;
    }

    @Override
    protected final Trampoline<B> trampoline() {
      return Trampoline.suspend(P.lazy(() -> next.trampoline().bind(v -> f.f(v).asTrampoline().trampoline())));
    }
  }

  private static final class DeferEval<A> extends TrampolineEval<A> {
    private final P1<Eval<A>> memo;

    DeferEval(F0<Eval<A>> producer) {
      this.memo = P.hardMemo(producer);
    }

    @Override
    protected final Trampoline<A> trampoline() {
      return Trampoline.suspend(P.lazy(() -> memo._1().asTrampoline().trampoline()));
    }
  }
}
