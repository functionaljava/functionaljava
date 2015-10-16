package fj.function;

import fj.F;
import fj.F2;

import static fj.Function.curry;

/**
 * First-class functions on Characters.
 */
public final class Characters {
  private Characters() {
    throw new UnsupportedOperationException();
  }
  public static final F<Character, String> toString = c -> Character.toString(c);
  public static final F<Character, Boolean> isLowerCase = ch -> Character.isLowerCase(ch);
  public static final F<Character, Boolean> isUpperCase = ch -> Character.isUpperCase(ch);
  public static final F<Character, Boolean> isTitleCase = ch -> Character.isTitleCase(ch);
  public static final F<Character, Boolean> isDigit = ch -> Character.isDigit(ch);
  public static final F<Character, Boolean> isDefined = ch -> Character.isDefined(ch);
  public static final F<Character, Boolean> isLetter = ch -> Character.isLetter(ch);
  public static final F<Character, Boolean> isLetterOrDigit = ch -> Character.isLetterOrDigit(ch);
  public static final F<Character, Boolean> isJavaIdentifierStart = ch -> Character.isJavaIdentifierStart(ch);
  public static final F<Character, Boolean> isJavaIdentifierPart = ch -> Character.isJavaIdentifierPart(ch);
  public static final F<Character, Boolean> isUnicodeIdentifierStart = ch -> Character.isUnicodeIdentifierStart(ch);
  public static final F<Character, Boolean> isUnicodeIdentifierPart = ch -> Character.isUnicodeIdentifierPart(ch);
  public static final F<Character, Boolean> isIdentifierIgnorable = ch -> Character.isIdentifierIgnorable(ch);
  public static final F<Character, Character> toLowerCase = ch -> Character.toLowerCase(ch);
  public static final F<Character, Character> toUpperCase = ch -> Character.toUpperCase(ch);
  public static final F<Character, Character> toTitleCase = ch -> Character.toTitleCase(ch);
  public static final F<Character, F<Integer, Integer>> digit = curry((ch, radix) -> Character.digit(ch, radix));
  public static final F<Character, Integer> getNumericValue = ch -> Character.getNumericValue(ch);
  public static final F<Character, Boolean> isSpaceChar = ch -> Character.isSpaceChar(ch);
  public static final F<Character, Boolean> isWhitespace = ch -> Character.isWhitespace(ch);
  public static final F<Character, Boolean> isISOControl = ch -> Character.isISOControl(ch);
  public static final F<Character, Integer> getType = ch -> Character.getType(ch);
  public static final F<Character, Byte> getDirectionality = ch -> Character.getDirectionality(ch);
  public static final F<Character, Boolean> isMirrored = ch -> Character.isMirrored(ch);
  public static final F<Character, Character> reverseBytes = ch -> Character.reverseBytes(ch);
  public static final F<Character, Boolean> isNewLine = c -> c == '\n';

}
