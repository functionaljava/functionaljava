package fj.demo;

import fj.F;
import fj.data.List;
import static fj.data.List.list;
import static fj.data.List.single;
import static fj.function.Integers.*;
import static fj.Show.intShow;
import static fj.Show.listShow;

public class List_apply {
  public static void main(final String[] a) {
    final List<F<Integer, Integer>> fs = single(subtract.f(2)).cons(multiply.f(2)).cons(add.f(2));
    final List<Integer> three = list(3);
    listShow(intShow).println(three.apply(fs)); // Prints out: <5,6,-1>
  }
}