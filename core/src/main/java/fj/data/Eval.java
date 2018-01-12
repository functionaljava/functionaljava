package fj.data;

import fj.F;
import fj.F0;
import fj.P;
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
  public <B> Eval<B> map(final F<A, B> f) {
    return bind(a -> now(f.f(a)));
  }

  /**
   * Alias for {@link #bind(F)}.
   */
  public <B> Eval<B> flatMap(final F<A, Eval<B>> f) {
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
  public <B> Eval<B> bind(final F<A, Eval<B>> f) {
    if (isTrampoline()) {
      return bindTrampoline(f, (TrampolineEval<A>) this);
    } else {
      return bindTrampoline(f, pureTrampoline(this));
    }
  }

  /**
   * True if the current instance is a trampoline instance, false - otherwise.
   */
  boolean isTrampoline() {
    return false;
  }

  /**
   * Represents an eager computation.
   */
  private static final class Now<A> extends Eval<A> {
    private final A a;

    private Now(A a) {
      this.a = a;
    }

    @Override
    public A value() {
      return a;
    }
  }

  /**
   * Represents a lazy computation that is evaluated only once.
   */
  private static final class Later<A> extends Eval<A> {
    private F0<A> producer;
    private A a;

    private Later(F0<A> producer) {
      this.producer = producer;
    }

    @Override
    public A value() {
      if (producer != null) {
        a = producer.f();
        producer = null;
      }
      return a;
    }
  }

  /**
   * Represents a lazy computation that is evaluated every time when it's requested.
   */
  private static final class Always<A> extends Eval<A> {
    private F0<A> supplier;

    private Always(F0<A> supplier) {
      this.supplier = supplier;
    }

    @Override
    public A value() {
      return supplier != null ? supplier.f() : null;
    }
  }

  /**
   * A helper abstraction that allows to perform recursive lazy transformations in O(1) stack space.
   */
  private static abstract class TrampolineEval<A> extends Eval<A> {

    protected abstract Trampoline<A> trampoline();

    @Override
    public A value() {
      return trampoline().run();
    }

    @Override
    boolean isTrampoline() {
      return true;
    }
  }

  private static final class PureTrampolineEval<A> extends TrampolineEval<A> {
    private final Eval<A> start;

    private PureTrampolineEval(Eval<A> start) {
      this.start = start;
    }

    @Override
    protected Trampoline<A> trampoline() {
      return Trampoline.suspend(P.lazy(() -> Trampoline.pure(start.value())));
    }
  }

  private static final class BindTrampolineEval<A, B> extends TrampolineEval<B> {
    private final TrampolineEval<A> next;
    private final F<A, Eval<B>> f;

    private BindTrampolineEval(F<A, Eval<B>> f, TrampolineEval<A> next) {
      this.next = next;
      this.f = f;
    }

    @Override
    protected Trampoline<B> trampoline() {
      return Trampoline.suspend(P.lazy(() -> next.trampoline().map(v -> f.f(v).value())));
    }
  }

  private static <A> TrampolineEval<A> pureTrampoline(Eval<A> start) {
    return new PureTrampolineEval<>(start);
  }

  private static <A, B> TrampolineEval<B> bindTrampoline(F<A, Eval<B>> f, TrampolineEval<A> next) {
    return new BindTrampolineEval<>(f, next);
  }
}
