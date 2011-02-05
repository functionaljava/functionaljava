package fj.demo;

import static fj.data.Enumerator.naturalEnumerator;
import static fj.data.Natural.natural;
import static fj.data.Stream.forever;
import static fj.Show.naturalShow;
import static fj.Show.unlineShow;

/**
 * Produces natural numbers forever.
 */
public class Stream_Test {
  public static void main(final String[] args) {
    unlineShow(naturalShow).println(forever(naturalEnumerator, natural(3).some(), 2));
  }
}
