package fj.demo;

import fj.data.Set;
import static fj.data.Set.empty;
import fj.Ord;
import static fj.Show.intShow;
import static fj.Show.listShow;

public final class Set_map {
  public static void main(final String[] args) {
    final Set<Integer> a = empty(Ord.intOrd).insert(1).insert(2).insert(3).insert(4).insert(5).insert(6);
    final Set<Integer> b = a.map(Ord.intOrd, i -> i / 2);
    listShow(intShow).println(b.toList()); // [3,2,1,0]
  }
}
