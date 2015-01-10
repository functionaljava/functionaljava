package fj;

/**
 * Represents the bottom _|_ value.
 *
 * @version %build.number%
 */
public final class Bottom {
  private Bottom() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an error to represent undefinedness in a computation.
   *
   * @return An error to represent undefinedness in a computation.
   */
  public static Error undefined() {
    return error("undefined");
  }

  /**
   * Returns an error to represent undefinedness in a computation with early failure using the given
   * message.
   *
   * @param s The message to fail with.
   * @return An error to represent undefinedness in a computation with early failure using the given
   *         message.
   */
  public static Error error(final String s) {
    throw new Error(s);
  }

  /**
   * Provides a thunk that throws an error using the given message when evaluated.
   *
   * @param s The message to fail with.
   * @return A thunk that throws an error using the given message when evaluated.
   */
  public static <A> P1<A> error_(final String s) {
    return new P1<A>() {
      @Override public A _1() {
        throw new Error(s);
      }
    };
  }

  /**
   * Provides a function that throws an error using the given message, ignoring its argument.
   *
   * @param s The message to fail with.
   * @return A function that throws an error using the given message, ignoring its argument.
   */
  public static <A, B> F<A, B> errorF(final String s) {
    return a -> {
        throw new Error(s);
    };
  }

  /**
   * Represents a deconstruction failure that was non-exhaustive.
   *
   * @param a  The value being deconstructed.
   * @param sa The rendering for the value being deconstructed.
   * @return A deconstruction failure that was non-exhaustive.
   */
  public static <A> Error decons(final A a, final Show<A> sa) {
    return error("Deconstruction failure on type " + a.getClass() + " with value " + sa.show(a).toString());
  }

  /**
   * Represents a deconstruction failure that was non-exhaustive.
   *
   * @param c The type being deconstructed.
   * @return A deconstruction failure that was non-exhaustive.
   */
  @SuppressWarnings({"UnnecessaryFullyQualifiedName"})
  public static <A> Error decons(final java.lang.Class<A> c) {
    return error("Deconstruction failure on type " + c);
  }

  /**
   * A function that returns the <code>toString</code> for a throwable.
   *
   * @return A function that returns the <code>toString</code> for a throwable.
   */
  public static <T extends Throwable> F<T, String> eToString() {
    return t -> t.toString();
  }

  /**
   * A function that returns the <code>getMessage</code> for a throwable.
   *
   * @return A function that returns the <code>getMessage</code> for a throwable.
   */
  public static <T extends Throwable> F<T, String> eMessage() {
    return t -> t.getMessage();
  }
}
