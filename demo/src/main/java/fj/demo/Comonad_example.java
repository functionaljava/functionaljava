package fj.demo;

import fj.F1Functions;
import fj.P;
import static fj.data.List.asString;
import static fj.data.List.fromString;
import fj.data.Stream;
import static fj.data.Stream.join;
import static fj.data.Stream.single;
import fj.data.Zipper;
import static fj.data.Zipper.fromStream;

/**
 * Example of using a Zipper comonad to get all the permutations of a String.
 */

public class Comonad_example {
  public static void main(final String[] args) {
    for (final Stream<Character> p : perms(fromString(args[0]).toStream())) {
      System.out.println(asString(p.toList()));
    }
  }

  public static Stream<Stream<Character>> perms(final Stream<Character> s) {
    Stream<Stream<Character>> r = single(Stream.nil());
    for (final Zipper<Character> z : fromStream(s))
      r = join(z.cobind(zp ->
            perms(zp.lefts().reverse().append(zp.rights())).map(
                F1Functions.o(Stream.<Character>cons().f(zp.focus()), P.p1())
            )
      ).toStream());
    return r;
  }
}
