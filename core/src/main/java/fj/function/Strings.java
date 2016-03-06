package fj.function;

import fj.F;
import fj.data.List;
import fj.data.Stream;

import java.util.regex.Pattern;

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

  private static final Pattern lineSeparatorPattern = Pattern.compile("\\r?\\n");

  public static final String lineSeparator = System.getProperty("line.separator");

    /**
     * This function checks if a given String is neither <code>null</code> nor empty.
     *
     * @see #isNullOrEmpty
     */
    public static final F<String, Boolean> isNotNullOrEmpty = a -> a != null && a.length() > 0;

    /**
     * This function checks if a given String is <code>null</code> or empty ({@link String#isEmpty()}).
     *
     * @see #isNotNullOrEmpty
     */
    public static final F<String, Boolean> isNullOrEmpty = a -> a == null || a.length() == 0;

    /**
   * This function checks if a given String contains any non-whitespace character
   * (according to {@link Character#isWhitespace(char)}) and if it's also not
   * <code>null</code> and not empty ("").
   * 
   * @see #isNullOrBlank
   * @see Character#isWhitespace(char)
   * @see Characters#isWhitespace
   */
  public static final F<String, Boolean> isNotNullOrBlank = a -> isNotNullOrEmpty.f(a) && Stream.fromString(a).find(not(isWhitespace)).isSome();
  /**
   * This function checks if a given String is whitespace (according to {@link Character#isWhitespace(char)}),
   * empty ("") or <code>null</code>.
   * 
   * @see #isNotNullOrBlank
   * @see Character#isWhitespace(char)
   * @see Characters#isWhitespace
   */
  public static final F<String, Boolean> isNullOrBlank = a -> isNullOrEmpty.f(a) || Stream.fromString(a).find(not(isWhitespace)).isNone();

  /**
   * A curried version of {@link String#isEmpty()}.
   */
  public static final F<String, Boolean> isEmpty = s -> s.length() == 0;

  /**
   * A curried version of {@link String#length()}.
   */
  public static final F<String, Integer> length = String::length;

  /**
   * A curried version of {@link String#contains(CharSequence)}.
   * The function returns true if the second argument contains the first.
   */
  public static final F<String, F<String, Boolean>> contains = curry((s1, s2) -> s2.contains(s1));

  /**
   * A curried version of {@link String#matches(String)}.
   * The function returns true if the second argument matches the first.
   */
  public static final F<String, F<String, Boolean>> matches = curry((s1, s2) -> s2.matches(s1));

  public static List<String> lines(String s) {
    return List.list(lineSeparatorPattern.split(s));
  }

  public static F<String, List<String>> lines() {
    return Strings::lines;
  }

  public static String unlines(List<String> list) {
    StringBuilder sb = new StringBuilder();
    list.intersperse(lineSeparator).foreachDoEffect(sb::append);
    return sb.toString();
  }

  public static F<List<String>, String> unlines() {
    return Strings::unlines;
  }

}
