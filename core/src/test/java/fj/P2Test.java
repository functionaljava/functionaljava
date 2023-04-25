package fj;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class P2Test {

  @Test
  void testToString() {
    String s = P.p(1, 2).toString();
    Assertions.assertTrue(s.equals("(1,2)"));
  }

}
