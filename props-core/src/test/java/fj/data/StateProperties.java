package fj.data;

import fj.F;
import fj.P2;
import fj.Unit;
import fj.test.Arbitrary;
import fj.test.Coarbitrary;
import fj.test.Gen;
import fj.test.Property;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static fj.P.p;
import static fj.data.List.range;
import static fj.test.Arbitrary.arbF;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbP2;
import static fj.test.Arbitrary.arbitrary;
import static fj.test.Coarbitrary.coarbInteger;
import static fj.test.Coarbitrary.coarbP2;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
public final class StateProperties {

  public Property unit() {
    return property(
        arbRunF(coarbInteger, arbInteger, arbInteger),
        arbInteger,
        (runF, initS) -> prop(testUnit(runF, initS)));
  }

  private static <S, A> boolean testUnit(F<S, P2<S, A>> runF, S initS) {
    State<S, A> instance = State.unit(runF);
    P2<S, A> actual = instance.run(initS);
    P2<S, A> expected = runF.f(initS);
    return actual.equals(expected);
  }

  public Property init() {
    return property(
        arbInteger,
        initS -> prop(testInit(initS)));
  }

  private static <S> boolean testInit(S initS) {
    State<S, S> instance = State.init();
    P2<S, S> actual = instance.run(initS);
    P2<S, S> expected = p(initS, initS);
    return actual.equals(expected);
  }

  public Property units() {
    return property(
        arbF(coarbInteger, arbInteger),
        arbInteger,
        (f, initS) -> prop(testUnits(f, initS)));
  }

  private static <S> boolean testUnits(F<S, S> f, S initS) {
    State<S, S> instance = State.units(f);
    P2<S, S> actual = instance.run(initS);
    S expectedS = f.f(initS);
    P2<S, S> expected = p(expectedS, expectedS);
    return actual.equals(expected);
  }

  public Property constant() {
    return property(
        arbInteger,
        arbInteger,
        (a, initS) -> prop(testConstant(a, initS)));
  }

  private static <S, A> boolean testConstant(A a, S initS) {
    State<S, A> instance = State.constant(a);
    P2<S, A> actual = instance.run(initS);
    P2<S, A> expected = p(initS, a);
    return actual.equals(expected);
  }

  public Property staticGets() {
    return property(
        arbF(coarbInteger, arbInteger),
        arbInteger,
        (f, initS) -> prop(testStaticGets(f, initS)));
  }

  private static <S, A> boolean testStaticGets(F<S, A> f, S initS) {
    State<S, A> instance = State.gets(f);
    P2<S, A> actual = instance.run(initS);
    P2<S, A> expected = p(initS, f.f(initS));
    return actual.equals(expected);
  }

  public Property put() {
    return property(
        arbInteger,
        arbInteger,
        (newS, initS) -> prop(testPut(newS, initS)));
  }

  private static <S> boolean testPut(S newS, S initS) {
    State<S, Unit> instance = State.put(newS);
    P2<S, Unit> actual = instance.run(initS);
    P2<S, Unit> expected = p(newS, Unit.unit());
    return actual.equals(expected);
  }

  public Property modify() {
    return property(
        arbF(coarbInteger, arbInteger),
        arbInteger,
        (f, initS) -> prop(testModify(f, initS)));
  }

  private static <S> boolean testModify(F<S, S> f, S initS) {
    State<S, Unit> instance = State.modify(f);
    P2<S, Unit> actual = instance.run(initS);
    P2<S, Unit> expected = p(f.f(initS), Unit.unit());
    return actual.equals(expected);
  }

  public Property staticFlatMap() {
    return property(
        arbRunF(coarbInteger, arbInteger, arbInteger),
        arbF(coarbInteger, arbState(coarbInteger, arbInteger, arbInteger)),
        arbInteger,
        (runF, f, initS) -> prop(testStaticFlatMap(runF, f, initS)));
  }

  private static <S, A, B> boolean testStaticFlatMap(F<S, P2<S, A>> runF, F<A, State<S, B>> f, S initS) {
    State<S, B> instance = State.flatMap(State.unit(runF), f);
    P2<S, B> actual = instance.run(initS);
    P2<S, A> intermediateExpected = runF.f(initS);
    P2<S, B> expected = f.f(intermediateExpected._2()).run(intermediateExpected._1());
    return actual.equals(expected);
  }

  public Property sequence() {
    return property(
        arbList(arbState(coarbInteger, arbInteger, arbInteger)),
        arbInteger,
        (states, initS) -> prop(testSequence(states, initS)));
  }

  private static <S, A> boolean testSequence(List<State<S, A>> states, S initS) {
    State<S, List<A>> instance = State.sequence(states);
    P2<S, List<A>> actual = instance.run(initS);

    S expectedFinalS = initS;
    List<A> expectedAs = List.nil();
    List<State<S, A>> remainingStates = states;
    while (remainingStates.isNotEmpty()) {
      P2<S, A> nextResult = remainingStates.head().run(expectedFinalS);
      expectedFinalS = nextResult._1();
      expectedAs = List.cons(nextResult._2(), expectedAs);
      remainingStates = remainingStates.tail();
    }
    expectedAs = expectedAs.reverse();

    P2<S, List<A>> expected = p(expectedFinalS, expectedAs);
    return actual.equals(expected);
  }

  public Property traverse() {
    return property(
        arbList(arbInteger),
        arbF(coarbInteger, arbState(coarbInteger, arbInteger, arbInteger)),
        arbInteger,
        (as, f, initS) -> prop(testTraverse(as, f, initS)));

  }

  private static <S, A, B> boolean testTraverse(List<A> as, F<A, State<S, B>> f, S initS) {
    State<S, List<B>> instance = State.traverse(as, f);
    P2<S, List<B>> actual = instance.run(initS);

    S expectedFinalS = initS;
    List<B> expectedFinalBs = List.nil();
    List<A> currAs = as;
    while (currAs.isNotEmpty()) {
      P2<S, B> nextStateAndB = f.f(currAs.head()).run(expectedFinalS);
      expectedFinalS = nextStateAndB._1();
      expectedFinalBs = List.cons(nextStateAndB._2(), expectedFinalBs);
      currAs = currAs.tail();
    }
    expectedFinalBs = expectedFinalBs.reverse();
    P2<S, List<B>> expected = p(expectedFinalS, expectedFinalBs);

    return actual.equals(expected);
  }

  public Property run() {
    return property(
        arbRunF(coarbInteger, arbInteger, arbInteger),
        arbInteger,
        (runF, initS) -> prop(testRun(runF, initS)));
  }

  private static <S, A> boolean testRun(F<S, P2<S, A>> runF, S initS) {
    State<S, A> instance = State.unit(runF);
    P2<S, A> actual = instance.run(initS);
    P2<S, A> expected = runF.f(initS);
    return actual.equals(expected);
  }

  public Property eval() {
    return property(
        arbRunF(coarbInteger, arbInteger, arbInteger),
        arbInteger,
        (runF, initS) -> prop(testEval(runF, initS)));
  }

  private static <S, A> boolean testEval(F<S, P2<S, A>> runF, S initS) {
    State<S, A> instance = State.unit(runF);
    A actual = instance.eval(initS);
    A expected = runF.f(initS)._2();
    return actual.equals(expected);
  }

  public Property exec() {
    return property(
        arbRunF(coarbInteger, arbInteger, arbUnit),
        arbInteger,
        (runF, initS) -> prop(testExec(runF, initS)));
  }

  private static <S> boolean testExec(F<S, P2<S, Unit>> runF, S initS) {
    State<S, Unit> instance = State.unit(runF);
    S actual = instance.exec(initS);
    S expected = runF.f(initS)._1();
    return actual.equals(expected);
  }

  public Property getsProperty() {
    return property(
        arbState(coarbInteger, arbInteger, arbInteger),
        arbInteger,
        (state, initS) -> prop(testGets(state, initS)));
  }

  private static <S, A> boolean testGets(State<S, A> state, S initS) {
    State<S, S> instance = state.gets();
    P2<S, S> actual = instance.run(initS);
    P2<S, S> expected = p(state.run(initS)._1(), state.run(initS)._1());
    return actual.equals(expected);
  }

  public Property map() {
    return property(
        arbState(coarbInteger, arbInteger, arbInteger),
        arbF(coarbInteger, arbInteger),
        arbInteger,
        (state, f, initS) -> prop(testMap(state, f, initS)));
  }

  private static <S, A, B> boolean testMap(State<S, A> state, F<A, B> f, S initS) {
    State<S, B> instance = state.map(f);
    P2<S, B> actual = instance.run(initS);
    P2<S, B> expected = state.run(initS).map2(f);
    return actual.equals(expected);
  }

  public Property mapState() {
    return property(
        arbState(coarbInteger, arbInteger, arbInteger),
        arbMapStateF(coarbInteger, coarbInteger, arbInteger, arbInteger),
        arbInteger,
        (state, f, initS) -> prop(testMapState(state, f, initS)));
  }

  private static <S, A, B> boolean testMapState(State<S, A> state, F<P2<S, A>, P2<S, B>> f, S initS) {
    State<S, B> instance = state.mapState(f);
    P2<S, B> actual = instance.run(initS);
    P2<S, B> expected = f.f(state.run(initS));
    return actual.equals(expected);
  }

  public Property withs() {
    return property(
        arbState(coarbInteger, arbInteger, arbInteger),
        arbF(coarbInteger, arbInteger),
        arbInteger,
        (state, f, initS) -> prop(testWiths(state, f, initS)));
  }

  private static <S, A> boolean testWiths(State<S, A> state, F<S, S> f, S initS) {
    State<S, A> instance = state.withs(f);
    P2<S, A> actual = instance.run(initS);
    P2<S, A> expected = state.run(f.f(initS));
    return actual.equals(expected);
  }

  public Property flatMap() {
    return property(
        arbState(coarbInteger, arbInteger, arbInteger),
        arbF(coarbInteger, arbState(coarbInteger, arbInteger, arbInteger)),
        arbInteger,
        (state, f, initS) -> prop(testFlatMap(state, f, initS)));
  }

  private static <S, A, B> boolean testFlatMap(State<S, A> state, F<A, State<S, B>> f, S initS) {
    State<S, B> instance = state.flatMap(f);
    P2<S, B> actual = instance.run(initS);
    P2<S, B> expected = f.f(state.run(initS)._2()).run(state.run(initS)._1());
    return actual.equals(expected);
  }

  @CheckParams(minSuccessful = 1)
  public Property getsStackSafety() {
    return property(
        arbHugeState(
            arbState(coarbInteger, arbInteger, arbInteger),
            currState -> arbitrary(Gen.value(currState.gets()))),
        arbInteger,
        (instance, initS) -> prop(testNoStackOverflow(instance, initS)));
  }

  @CheckParams(minSuccessful = 1)
  public Property mapStackSafety() {
    return property(
        arbHugeState(
            arbState(coarbInteger, arbInteger, arbInteger),
            currState -> arbitrary(Gen.gen(s -> r -> currState.map(arbF(coarbInteger, arbInteger).gen.gen(s, r))))),
        arbInteger,
        (instance, initS) -> prop(testNoStackOverflow(instance, initS)));
  }

  @CheckParams(minSuccessful = 1)
  public Property mapStateStackSafety() {
    return property(
        arbHugeState(
            arbState(coarbInteger, arbInteger, arbInteger),
            currState -> arbitrary(Gen.gen(s -> r ->
                currState.mapState(arbMapStateF(coarbInteger, coarbInteger, arbInteger, arbInteger).gen.gen(s, r))))),
        arbInteger,
        (instance, initS) -> prop(testNoStackOverflow(instance, initS)));
  }

  @CheckParams(minSuccessful = 1)
  public Property withsStackSafety() {
    return property(
        arbHugeState(
            arbState(coarbInteger, arbInteger, arbInteger),
            currState -> arbitrary(Gen.gen(s -> r -> currState.withs(arbF(coarbInteger, arbInteger).gen.gen(s, r))))),
        arbInteger,
        (instance, initS) -> prop(testNoStackOverflow(instance, initS)));
  }

  @CheckParams(minSuccessful = 1)
  public Property flatMapStackSafety() {
    return property(
        arbHugeState(
            arbState(coarbInteger, arbInteger, arbInteger),
            currState -> arbitrary(Gen.gen(s -> r ->
                currState.flatMap(arbF(coarbInteger, arbState(coarbInteger, arbInteger, arbInteger)).gen.gen(s, r))))),
        arbInteger,
        (instance, initS) -> prop(testNoStackOverflow(instance, initS)));
  }

  private static <S, A> boolean testNoStackOverflow(State<S, A> instance, S initS) {
    instance.run(initS);
    return true;
  }

  private static final Arbitrary<Unit> arbUnit = arbitrary(Gen.value(Unit.unit()));

  private static <S, A> Arbitrary<F<S, P2<S, A>>> arbRunF(
      Coarbitrary<S> coarbInitS,
      Arbitrary<S> arbNextS,
      Arbitrary<A> arbValue) {

    return arbF(coarbInitS, arbP2(arbNextS, arbValue));
  }

  private static <S, A> Arbitrary<F<P2<S, A>, P2<S, A>>> arbMapStateF(
      Coarbitrary<S> coarbInitS,
      Coarbitrary<A> coarbInitValue,
      Arbitrary<S> arbNextS,
      Arbitrary<A> arbNextValue) {

    return arbF(coarbP2(coarbInitS, coarbInitValue), arbP2(arbNextS, arbNextValue));
  }

  private static <S, A> Arbitrary<State<S, A>> arbState(
      Coarbitrary<S> coarbInitS,
      Arbitrary<S> arbNextS,
      Arbitrary<A> arbValue) {

    Arbitrary<F<S, P2<S, A>>> arbRunF = arbRunF(coarbInitS, arbNextS, arbValue);
    return arbitrary(Gen.gen(s -> r -> State.unit(arbRunF.gen.gen(s, r))));
  }

  private static final int HUGE_SIZE = 10000;

  private static <S, A> Arbitrary<State<S, A>> arbHugeState(
      Arbitrary<State<S, A>> arbInitState,
      F<State<S, A>, Arbitrary<State<S, A>>> nextArbStateF) {

    return arbitrary(Gen.gen(s -> r -> range(0, HUGE_SIZE).foldLeft(
        (acc, x) -> nextArbStateF.f(acc).gen.gen(s, r),
        arbInitState.gen.gen(s, r))));
  }

}
