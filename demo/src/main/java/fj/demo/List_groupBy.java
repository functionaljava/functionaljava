package fj.demo;

import fj.Function;
import fj.Ord;
import fj.data.List;
import fj.data.TreeMap;

import static fj.data.List.list;

public final class List_groupBy {

  public static void main(final String... args) {
    keyDemo();
    keyValueDemo();
    keyValueAccDemo();
  }

  private static void keyDemo() {
    System.out.println("KeyDemo");
    final List<String> words = list("Hello", "World", "how", "are", "your", "doing");
    final TreeMap<Integer, List<String>> lengthMap = words.groupBy(String::length, Ord.intOrd);

    lengthMap.forEach(entry -> System.out.println(String.format("Words with %d chars: %s", entry._1(), entry._2())));
  }

  private static void keyValueDemo() {
    System.out.println("KeyValueDemo");
    final List<Integer> xs = list(1, 2, 3, 4, 5, 6, 7, 8, 9);
    final TreeMap<Integer, List<String>> result = xs.groupBy(x -> x % 3, Integer::toBinaryString, Ord.intOrd);

    result.forEach(entry -> System.out.println(String.format("Numbers with reminder %d are %s", entry._1(), entry._2())));
  }

  private static void keyValueAccDemo() {
    System.out.println("KeyValueAccDemo");
    final List<String> words = list("Hello", "World", "how", "are", "your", "doing");
    final TreeMap<Integer, Integer> lengthCounts =
        words.groupBy(String::length, Function.identity(), 0, (word, sum) -> sum + 1, Ord.intOrd);

    lengthCounts.forEach(entry -> System.out.println(String.format("Words with %d chars: %s", entry._1(), entry._2())));
  }
}
