package fj.data;

import fj.Unit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnitTest {

  @Test
  void objectMethods() {
    Assertions.assertTrue(Unit.unit().equals(Unit.unit()));
    Assertions.assertFalse(Unit.unit().equals(3));
    Assertions.assertTrue(Unit.unit().toString().equals("unit"));
  }

}
