package fj.function;

import org.junit.jupiter.api.Test;

public class TestEffect {

  @Test
  void test1() {
    higher(TestEffect::m1);
  }


  static void higher(Effect1<String> f) {

  }

  static void m1(String s) {

  }

}
