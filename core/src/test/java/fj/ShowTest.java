package fj;

import fj.data.Array;

import org.junit.jupiter.api.Test;

import static fj.data.Array.array;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShowTest {
  @Test
  void arrayShow() {
    Array<Integer> a = array(3, 5, 7);
    String s = Show.arrayShow(Show.intShow).showS(a);
    assertTrue(s.equals("Array(3,5,7)"));
  }
}
