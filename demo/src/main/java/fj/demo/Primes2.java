package fj.demo;

import fj.F1Functions;

import static fj.data.Enumerator.naturalEnumerator;

import fj.Show;
import fj.data.Natural;
import static fj.data.Natural.*;
import fj.data.Stream;
import static fj.data.Stream.*;
import static fj.Ord.naturalOrd;
import static fj.Show.naturalShow;
import static fj.Show.streamShow;

import java.math.BigInteger;

/**
 * Prints all primes less than n
 */
public class Primes2 {
  // Finds primes in a given stream.
  public static Stream<Natural> sieve(final Stream<Natural> xs) {
    return cons(xs.head(), () -> sieve(xs.tail()._1().removeAll(F1Functions.o(naturalOrd.equal().eq(ZERO), mod.f(xs.head())))));
  }

  // A stream of all primes less than n.
  public static Stream<Natural> primes(final Natural n) {
    return sieve(forever(naturalEnumerator, natural(2).some())).takeWhile(naturalOrd.isLessThan(n));
  }

  public static void main(final String[] a) {
    final Natural n = natural(new BigInteger(a[0])).some();
    final Show<Stream<Natural>> s = streamShow(naturalShow);

    s.println(primes(n));
  }
}
