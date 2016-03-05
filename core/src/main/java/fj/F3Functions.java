package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public final class F3Functions {


  private F3Functions() {
  }

  /**
     * Partial application.
     *
     * @param a The <code>A</code> to which to apply this function.
     * @return The function partially applied to the given argument.
     */
  public static <A, B, C, D> F2<B, C, D> f(final F3<A, B, C, D> f, final A a) {
        return (b, c) -> f.f(a, b, c);
    }

}
