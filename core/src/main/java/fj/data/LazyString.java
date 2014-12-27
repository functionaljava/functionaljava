package fj.data;

import fj.Equal;
import fj.F;
import fj.F2;
import static fj.Function.compose;
import static fj.Function.curry;
import static fj.P.p;
import fj.P1;
import fj.P2;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Stream.join;
import static fj.function.Booleans.or;
import static fj.function.Characters.isSpaceChar;
import static fj.Equal.charEqual;
import static fj.Equal.streamEqual;

import java.util.regex.Pattern;

/**
 * A lazy (non-evaluated) immutable character string.
 */
public final class LazyString implements CharSequence {
  private final Stream<Character> s;

  private LazyString(final Stream<Character> s) {
    this.s = s;
  }

  /**
   * Constructs a lazy string from a String.
   *
   * @param s A string from which to construct a lazy string.
   * @return A lazy string with the characters from the given string.
   */
  public static LazyString str(final String s) {
    return new LazyString(Stream.unfold(new F<P2<String, Integer>, Option<P2<Character, P2<String, Integer>>>>() {
      public Option<P2<Character, P2<String, Integer>>> f(final P2<String, Integer> o) {
        final String s = o._1();
        final int n = o._2();
        final Option<P2<Character, P2<String, Integer>>> none = none();
        return s.length() <= n ? none : some(p(s.charAt(n), p(s, n + 1)));
      }
    }, p(s, 0)));
  }

  /**
   * The empty string.
   */
  public static final LazyString empty = str("");

  /**
   * Constructs a lazy string from a stream of characters.
   *
   * @param s A stream of characters.
   * @return A lazy string with the characters from the given stream.
   */
  public static LazyString fromStream(final Stream<Character> s) {
    return new LazyString(s);
  }

  /**
   * Gives a stream representation of this lazy string.
   *
   * @return A stream representation of this lazy string.
   */
  public Stream<Character> toStream() {
    return s;
  }

  /**
   * The length of the lazy string. Note that this operation is O(n).
   *
   * @return The length of this lazy string.
   */
  public int length() {
    return s.length();
  }

  /**
   * Returns the caracter at the specified index.
   *
   * @param index The index for the character to be returned.
   * @return The character at the specified index.
   */
  public char charAt(final int index) {
    return s.index(index);
  }

  /**
   * Gets the specified subsequence of this lazy string.
   * This operation does not fail for indexes that are out of bounds. If the start index is past the end
   * of this lazy string, then the resulting character sequence will be empty. If the end index is past the
   * end of this lazy string, then the resulting character sequence will be truncated.
   *
   * @param start The character index of this lazy string at which to start the subsequence.
   * @param end   The character index of this lazy string at which to end the subsequence.
   * @return A character sequence containing the specified character subsequence.
   */
  public CharSequence subSequence(final int start, final int end) {
    return fromStream(s.drop(start).take(end - start));
  }

  /**
   * Returns the String representation of this lazy string.
   *
   * @return The String representation of this lazy string.
   */
  public String toString() {
    return new StringBuilder(this).toString();
  }

  /**
   * Appends the given lazy string to the end of this lazy string.
   *
   * @param cs A lazy string to append to this one.
   * @return A new lazy string that is the concatenation of this string and the given string.
   */
  public LazyString append(final LazyString cs) {
    return fromStream(s.append(cs.s));
  }

  /**
   * Appends the given String to the end of this lazy string.
   *
   * @param s A String to append to this lazy string.
   * @return A new lazy string that is the concatenation of this lazy string and the given string.
   */
  public LazyString append(final String s) {
    return append(str(s));
  }

  /**
   * Returns true if the given lazy string is a substring of this lazy string.
   *
   * @param cs A substring to find in this lazy string.
   * @return True if the given string is a substring of this string, otherwise False.
   */
  public boolean contains(final LazyString cs) {
    return or(s.tails().map(compose(startsWith().f(cs), fromStream)));
  }

  /**
   * Returns true if the given lazy string is a suffix of this lazy string.
   *
   * @param cs A string to find at the end of this lazy string.
   * @return True if the given string is a suffix of this lazy string, otherwise False.
   */
  public boolean endsWith(final LazyString cs) {
    return reverse().startsWith(cs.reverse());
  }

  /**
   * Returns true if the given lazy string is a prefix of this lazy string.
   *
   * @param cs A string to find at the start of this lazy string.
   * @return True if the given string is a prefix of this lazy string, otherwise False.
   */
  public boolean startsWith(final LazyString cs) {
    return cs.isEmpty() || !isEmpty() && charEqual.eq(head(), cs.head()) && tail().startsWith(cs.tail());
  }


  /**
   * First-class prefix check.
   *
   * @return A function that yields true if the first argument is a prefix of the second.
   */
  public static F<LazyString, F<LazyString, Boolean>> startsWith() {
    return curry((needle, haystack) -> haystack.startsWith(needle));
  }

  /**
   * Returns the first character of this string.
   *
   * @return The first character of this string, or error if the string is empty.
   */
  public char head() {
    return s.head();
  }

  /**
   * Returns all but the first character of this string.
   *
   * @return All but the first character of this string, or error if the string is empty.
   */
  public LazyString tail() {
    return fromStream(s.tail()._1());
  }

  /**
   * Checks if this string is empty.
   *
   * @return True if there are no characters in this string, otherwise False.
   */
  public boolean isEmpty() {
    return s.isEmpty();
  }

  /**
   * Returns the reverse of this string.
   *
   * @return the reverse of this string.
   */
  public LazyString reverse() {
    return fromStream(s.reverse());
  }

  /**
   * Returns the first index of the given character in this lazy string, if present.
   *
   * @param c A character to find in this lazy string.
   * @return The first index of the given character in this lazy string, or None if the character is not present.
   */
  public Option<Integer> indexOf(final char c) {
    return s.indexOf(Equal.charEqual.eq(c));
  }

  /**
   * Returns the first index of the given substring in this lazy string, if present.
   *
   * @param cs A substring to find in this lazy string.
   * @return The first index of the given substring in this lazy string, or None if there is no such substring.
   */
  public Option<Integer> indexOf(final LazyString cs) {
    return s.substreams().indexOf(eqS.eq(cs.s));
  }

  /**
   * Regular expression pattern matching.
   *
   * @param regex A regular expression to match this lazy string.
   * @return True if this string mathches the given regular expression, otherwise False.
   */
  public boolean matches(final String regex) {
    return Pattern.matches(regex, this);
  }

  /**
   * Splits this lazy string by characters matching the given predicate.
   *
   * @param p A predicate that matches characters to be considered delimiters.
   * @return A stream of the substrings in this lazy string, when separated by the given predicate.
   */
  public Stream<LazyString> split(final F<Character, Boolean> p) {
    final Stream<Character> findIt = s.dropWhile(p);
    final P2<Stream<Character>, Stream<Character>> ws = findIt.split(p);
    return findIt.isEmpty() ? Stream.<LazyString>nil()
                            : Stream.cons(fromStream(ws._1()), new P1<Stream<LazyString>>() {
                              public Stream<LazyString> _1() {
                                return fromStream(ws._2()).split(p);
                              }
                            });
  }

  /**
   * Splits this lazy string by the given delimiter character.
   *
   * @param c A delimiter character at which to split.
   * @return A stream of substrings of this lazy string, when separated by the given delimiter.
   */
  public Stream<LazyString> split(final char c) {
    return split(charEqual.eq(c));
  }

  /**
   * Splits this lazy string into words by spaces.
   *
   * @return A stream of the words in this lazy string, when split by spaces.
   */
  public Stream<LazyString> words() {
    return split(isSpaceChar);
  }

  /**
   * Splits this lazy string into lines.
   *
   * @return A stream of the lines in this lazy string, when split by newlines.
   */
  public Stream<LazyString> lines() {
    return split('\n');
  }

  /**
   * Joins the given stream of lazy strings into one, separated by newlines.
   *
   * @param str A stream of lazy strings to join by newlines.
   * @return A new lazy string, consisting of the given strings separated by newlines.
   */
  public static LazyString unlines(final Stream<LazyString> str) {
    return fromStream(join(str.intersperse(str("\n")).map(toStream)));
  }

  /**
   * Joins the given stream of lazy strings into one, separated by spaces.
   *
   * @param str A stream of lazy strings to join by spaces.
   * @return A new lazy string, consisting of the given strings with spaces in between.
   */
  public static LazyString unwords(final Stream<LazyString> str) {
    return fromStream(join(str.intersperse(str(" ")).map(toStream)));
  }

  /**
   * First-class conversion from lazy strings to streams.
   */
  public static final F<LazyString, Stream<Character>> toStream =
          string -> string.toStream();

  /**
   * First-class conversion from lazy strings to String.
   */
  public static final F<LazyString, String> toString =
          string -> string.toString();

  /**
   * First-class conversion from character streams to lazy strings.
   */
  public static final F<Stream<Character>, LazyString> fromStream =
          s -> fromStream(s);

  private static final Equal<Stream<Character>> eqS = streamEqual(charEqual);

}
