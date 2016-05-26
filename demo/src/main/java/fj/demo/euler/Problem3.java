package fj.demo.euler;

import fj.P1;
import static fj.data.Enumerator.naturalEnumerator;
import fj.data.Natural;
import static fj.data.Natural.ZERO;
import static fj.data.Natural.natural;
import fj.data.Stream;
import static fj.data.Stream.*;
import fj.data.vector.V2;
import static fj.Ord.naturalOrd;
import static fj.Show.naturalShow;

/**
 * Find the largest prime factor of a composite number.
 */
public class Problem3 {
  // An infinite stream of all the primes.
  public static final Stream<Natural> primes = cons(natural(2).some(), () -> forever(naturalEnumerator, natural(3).some(), 2).filter(n -> primeFactors(n).length() == 1));

  //Finds factors of a given number.
  public static Stream<Natural> factor(final Natural n, final Natural p, final P1<Stream<Natural>> ps) {
    Stream<Natural> ns = cons(p, ps);
    Stream<Natural> ret = nil();
    while (ns.isNotEmpty() && ret.isEmpty()) {
      final Natural h = ns.head();
      final P1<Stream<Natural>> t = ns.tail();
      if (naturalOrd.isGreaterThan(h.multiply(h), n))
        ret = single(n);
      else {
        final V2<Natural> dm = n.divmod(h);
        if (naturalOrd.eq(dm._2(), ZERO))
          ret = cons(h, () -> factor(dm._1(), h, t));
        else ns = ns.tail()._1();
      }
    }
    return ret;
  }

  // Finds the prime factors of a given number.
  public static Stream<Natural> primeFactors(final Natural n) {return factor(n, natural(2).some(), primes.tail());}

  public static void main(final String[] args) {
    naturalShow.println(primeFactors(natural(600851475143L).some()).last());
  }
}
