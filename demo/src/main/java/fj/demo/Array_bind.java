package fj.demo;

import fj.data.Array;
import static fj.data.Array.array;
import static fj.Show.arrayShow;
import static fj.Show.intShow;

public final class Array_bind {
  public static void main(final String[] args) {
    final Array<Integer> a = array(97, 44, 67, 3, 22, 90, 1, 77, 98, 1078, 6, 64, 6, 79, 42);
    final Array<Integer> b = a.bind(i -> array(500, i));
    arrayShow(intShow).println(b);
    // {500,97,500,44,500,67,500,3,500,22,500,90,500,1,500,77,500,98,500,1078,500,6,500,64,500,6,500,79,500,42}
  }
}
