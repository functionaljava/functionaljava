package fj.function;

import fj.F;
import fj.F2;
import fj.data.Stream;
import static fj.Function.curry;
import static fj.function.Booleans.not;
import static fj.function.Characters.isWhitespace;

/**
 * Curried string functions.
 *
 * @version %build.number%
 */
public final class Strings {
  private Strings() {
    throw new UnsupportedOperationException();
  }

  /**
   * This function checks if a given String contains any non-whitespace character
   * (according to {@link Character#isWhitespace(char)}) and if it's also not
   * <code>null</code> and not empty ("").
   * 
   * @see #isNullOrBlank
   * @see Character#isWhitespace(char)
   * @see Characters#isWhitespace
   */
  public static final F<String, Boolean> isNotNullOrBlank = new F<String, Boolean>() {
    @Override
    public Boolean f(final String a) {
      return isNotNullOrEmpty.f(a) && Stream.fromString(a).find(not(isWhitespace)).isSome();
    }
  };

  /**
   * This function checks if a given String is whitespace (according to {@link Character#isWhitespace(char)}),
   * empty ("") or <code>null</code>.
   * 
   * @see #isNotNullOrBlank
   * @see Character#isWhitespace(char)
   * @see Characters#isWhitespace
   */
  public static final F<String, Boolean> isNullOrBlank = new F<String, Boolean>() {
    @Override
    public Boolean f(final String a) {
      return isNullOrEmpty.f(a) || Stream.fromString(a).find(not(isWhitespace)).isNone();
    }
  };

  /**
   * This function checks if a given String is neither <code>null</code> nor empty.
   * 
   * @see #isNullOrEmpty
   */
  public static final F<String, Boolean> isNotNullOrEmpty = new F<String, Boolean>() {
    @Override
    public Boolean f(final String a) {
      return a != null && a.length() > 0;
    }
  };

  /**
   * This function checks if a given String is <code>null</code> or empty ({@link String#isEmpty()}).
   * 
   * @see #isNotNullOrEmpty
   */
  public static final F<String, Boolean> isNullOrEmpty = new F<String, Boolean>() {
    @Override
    public Boolean f(final String a) {
      return a == null || a.length() == 0;
    }
  };

  /**
   * A curried version of {@link String#isEmpty()}.
   */
  public static final F<String, Boolean> isEmpty = new F<String, Boolean>() {
    public Boolean f(final String s) {
      return s.length() == 0;
    }
  };

  /**
   * A curried version of {@link String#length()}.
   */
  public static final F<String, Integer> length = new F<String, Integer>() {
    public Integer f(final String s) {
      return s.length();
    }
  };

  /**
   * A curried version of {@link String#contains(CharSequence)}.
   * The function returns true if the second argument contains the first.
   */
  public static final F<String, F<String, Boolean>> contains = curry(new F2<String, String, Boolean>() {
    public Boolean f(final String s1, final String s2) {
      return s2.contains(s1);
    }
  });

  /**
   * A curried version of {@link String#matches(String)}.
   * The function returns true if the second argument matches the first.
   */
  public static final F<String, F<String, Boolean>> matches = curry(new F2<String, String, Boolean>() {
    public Boolean f(final String s1, final String s2) {
      return s2.matches(s1);
    }
  });

}
