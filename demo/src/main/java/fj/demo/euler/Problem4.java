package fj.demo.euler;

import static fj.Function.flip;
import fj.data.Stream;
import static fj.data.Stream.iterate;
import static fj.function.Integers.multiply;
import static fj.function.Integers.subtract;
import static fj.Equal.charEqual;
import static fj.Equal.streamEqual;
import static fj.Ord.intOrd;
import static fj.Show.intShow;

/**
 * Find the largest palindrome made from the product of two 3-digit numbers.
 */
public class Problem4 {
  public static void main(final String[] a) {
    final Stream<Integer> tdl = iterate(flip(subtract).f(1), 999).takeWhile(intOrd.isGreaterThan(99));
    intShow.println(tdl.tails().bind(tdl.zipWith(multiply)).filter(i -> {
        final Stream<Character> s = intShow.show(i);
        return streamEqual(charEqual).eq(s.reverse().take(3), s.take(3));
    }).foldLeft1(intOrd::max));
  }
}
