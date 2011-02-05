package fj.demo;

import fj.data.List;
import static fj.data.List.list;
import static fj.Ord.intOrd;
import static fj.Show.intShow;
import static fj.Show.listShow;

public final class List_sort {
  public static void main(final String[] args) {
    final List<Integer> a = list(97, 44, 67, 3, 22, 90, 1, 77, 98, 1078, 6, 64, 6, 79, 42);
    final List<Integer> b = a.sort(intOrd);
    listShow(intShow).println(b); // [1,3,6,6,22,42,44,64,67,77,79,90,97,98,1078]
  }
}
