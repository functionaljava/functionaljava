package fj.test.reflect;

import fj.P2;
import static fj.data.Array.array;

import fj.function.Effect1;
import fj.test.CheckResult;
import static fj.test.CheckResult.summary;
import static fj.test.reflect.Check.check;

import static java.lang.Class.forName;
import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * Checks the properties of a class using a standard random generator, standard check parameters and
 * the given categories. The class name and categories are passed as command line arguments. 
 *
 * @version %build.number%
 */
public final class Main {
  private Main() {
    throw new UnsupportedOperationException();
  }

  /**
   * Check the given class and categories. At least one command line argument (the class name) must be
   * passed or an error message results.
   *
   * @param args The class name as the first argument, then zero or more categories.
   */
  public static void main(final String... args) {
    if(args.length == 0) {
      System.err.println("<class> [category]*");
      //noinspection CallToSystemExit
      exit(441);
    } else {
      try {
        check(forName(args[0]), array(args).toList().tail()).foreachDoEffect(r -> {
            summary.print(r._2());
            out.println(" (" + r._1() + ')');
        });
      } catch(ClassNotFoundException e) {
        e.printStackTrace();
        //noinspection CallToSystemExit
        exit(144);
      }
    }
  }
}
