package fj.demo;

import fj.data.Array;
import static fj.data.Array.array;
import static fj.function.Integers.add;

public final class Array_foldLeft {
  public static void main(final String[] args) {
    final Array<Integer> a = array(97, 44, 67, 3, 22, 90, 1, 77, 98, 1078, 6, 64, 6, 79, 42);
    final int b = a.foldLeft(add, 0);
    System.out.println(b); // 1774
  }
}
