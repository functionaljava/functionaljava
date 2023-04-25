package fj.data;

import org.junit.jupiter.api.Test;

import static fj.P.p;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StateTest {

  @Test
  void testBind() {
    assertEquals(p(2, "one"), state().run(1));
    assertEquals(p(3, "two"), state().run(2));
    assertEquals(p(4, "three"), state().run(3));
    assertEquals(p(2, "?"), state().bind(state -> State.constant("?")).run(1));
    assertEquals(p(3, "?"), state().bind(state -> State.constant("?")).run(2));
    assertEquals(p(4, "?"), state().bind(state -> State.constant("?")).run(3));
  }

  @Test
  void testFlatMap() {
    assertEquals(p(2, "one"), state().run(1));
    assertEquals(p(3, "two"), state().run(2));
    assertEquals(p(4, "three"), state().run(3));
    assertEquals(p(2, "?"), state().flatMap(state -> State.constant("?")).run(1));
    assertEquals(p(3, "?"), state().flatMap(state -> State.constant("?")).run(2));
    assertEquals(p(4, "?"), state().flatMap(state -> State.constant("?")).run(3));
  }

  private static final State<Integer, String> state() {
    return State.<Integer, String>unit(i -> p(i + 1, toLapine(i)));
  }

  private static String toLapine(
      final int i) {
    return i == 1 ?
        "one" :
        i == 2 ?
            "two" :
            i == 3 ?
                "three" :
                i == 4 ?
                    "four" : "hrair";
  }
}
