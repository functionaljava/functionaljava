package fj.data;

import fj.F;
import fj.P2;
import fj.Unit;
import fj.control.Trampoline;

import static fj.P.lazy;
import static fj.P.p;
import static fj.control.Trampoline.suspend;
import static fj.data.List.cons;

/**
 * Created by MarkPerry on 7/07/2014.
 */
public final class State<S, A> {

  public static <S, A> State<S, A> unit(F<S, P2<S, A>> runF) {
    return new State<>(s -> Trampoline.pure(runF.f(s)));
  }

  public static <S> State<S, S> init() {
    return unit(s -> dup(s));
  }

  public static <S> State<S, S> units(F<S, S> f) {
    return unit(s -> dup(f.f(s)));
  }

  private static <S> P2<S, S> dup(S s) {
    return p(s, s);
  }

  public static <S, A> State<S, A> constant(A a) {
    return unit(s -> p(s, a));
  }

  public static <S, A> State<S, A> gets(F<S, A> f) {
    return unit(s -> p(s, f.f(s)));
  }

  public static <S> State<S, Unit> put(S s) {
    return unit(ignoredS -> p(s, Unit.unit()));
  }

  public static <S> State<S, Unit> modify(F<S, S> f) {
    return unit(s -> p(f.f(s), Unit.unit()));
  }

  public static <S, A, B> State<S, B> flatMap(State<S, A> ts, F<A, State<S, B>> f) {
    return ts.flatMap(f);
  }

  /**
   * Evaluate each action in the sequence from left to right, and collect the results.
   */
  public static <S, A> State<S, List<A>> sequence(List<State<S, A>> list) {
    return list
        .foldLeft(
            (acc, ts) -> acc.flatMap(as -> ts.map(a -> cons(a, as))),
            State.<S, List<A>>constant(List.nil()))
        .map(as -> as.reverse());
  }

  /**
   * Map each element of a structure to an action, evaluate these actions from left to right
   * and collect the results.
   */
  public static <S, A, B> State<S, List<B>> traverse(List<A> list, F<A, State<S, B>> f) {
    return list
        .foldLeft(
            (acc, a) -> acc.flatMap(bs -> f.f(a).map(b -> cons(b, bs))),
            State.<S, List<B>>constant(List.nil()))
        .map(bs -> bs.reverse());
  }

  private static <S, A> State<S, A> suspended(F<S, Trampoline<P2<S, A>>> runF) {
    return new State<>(s -> suspend(lazy(() -> runF.f(s))));
  }

  private final F<S, Trampoline<P2<S, A>>> runF;

  private State(F<S, Trampoline<P2<S, A>>> runF) {
    this.runF = runF;
  }

  public P2<S, A> run(S s) {
    return runF.f(s).run();
  }

  public A eval(S s) {
    return run(s)._2();
  }

  public S exec(S s) {
    return run(s)._1();
  }

  public State<S, S> gets() {
    return mapState(result -> p(result._1(), result._1()));
  }

  public <B> State<S, B> map(F<A, B> f) {
    return mapState(result -> p(result._1(), f.f(result._2())));
  }

  public <B> State<S, B> mapState(F<P2<S, A>, P2<S, B>> f) {
    return suspended(s -> runF.f(s).map(result -> f.f(result)));
  }

  public State<S, A> withs(F<S, S> f) {
    return suspended(s -> runF.f(f.f(s)));
  }

  public <B> State<S, B> flatMap(F<A, State<S, B>> f) {
    return suspended(s -> runF.f(s).bind(result -> Trampoline.pure(f.f(result._2()).run(result._1()))));
  }

}
