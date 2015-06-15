package fj.demo;

import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.List;
import fj.data.Stream;

import static fj.data.Enumerator.naturalEnumerator;
import static fj.data.IOFunctions.runSafe;
import static fj.data.IOFunctions.stdinReadLine;
import static fj.data.Natural.natural;
import static fj.data.Stream.forever;
import static fj.Show.naturalShow;
import static fj.Show.unlineShow;
import static java.lang.System.out;

public class Stream_Test {

  public static void main(final String[] args) {
    sequenceWhile();
//    foreverNaturals();
  }

  /**
   * Produces natural numbers forever.
   */
  static void foreverNaturals() {
    unlineShow(naturalShow).println(forever(naturalEnumerator, natural(3).some(), 2));
  }

  /**
   * Reads lines from standard input until a line's length is less than two or three lines
   * have been read.
   */
  static void sequenceWhile() {
    Stream<IO<String>> s = Stream.repeat(stdinReadLine());
    IO<Stream<String>> io = IOFunctions.sequenceWhile(s, s2 -> s2.length() > 1);
    List<String> list = runSafe(io).take(3).toList();
    out.println("list: " + list + " size: " + list.length());
  }

}
