package fj.function;

import fj.F;

import static fj.Function.curry;

/**
 * First-class functions on Characters.
 */
public final class Characters {
  private Characters() {
    throw new UnsupportedOperationException();
  }
  public static final F<Character, String> toString = c -> Character.toString(c);
  public static final F<Character, Boolean> isLowerCase = Character::isLowerCase;
  public static final F<Character, Boolean> isUpperCase = Character::isUpperCase;
  public static final F<Character, Boolean> isTitleCase = Character::isTitleCase;
  public static final F<Character, Boolean> isDigit = Character::isDigit;
  public static final F<Character, Boolean> isDefined = Character::isDefined;
  public static final F<Character, Boolean> isLetter = Character::isLetter;
  public static final F<Character, Boolean> isLetterOrDigit = Character::isLetterOrDigit;
  public static final F<Character, Boolean> isJavaIdentifierStart = Character::isJavaIdentifierStart;
  public static final F<Character, Boolean> isJavaIdentifierPart = Character::isJavaIdentifierPart;
  public static final F<Character, Boolean> isUnicodeIdentifierStart = Character::isUnicodeIdentifierStart;
  public static final F<Character, Boolean> isUnicodeIdentifierPart = Character::isUnicodeIdentifierPart;
  public static final F<Character, Boolean> isIdentifierIgnorable = Character::isIdentifierIgnorable;
  public static final F<Character, Character> toLowerCase = Character::toLowerCase;
  public static final F<Character, Character> toUpperCase = Character::toUpperCase;
  public static final F<Character, Character> toTitleCase = Character::toTitleCase;
  public static final F<Character, F<Integer, Integer>> digit = curry((ch, radix) -> Character.digit(ch, radix));
  public static final F<Character, Integer> getNumericValue = Character::getNumericValue;
  public static final F<Character, Boolean> isSpaceChar = Character::isSpaceChar;
  public static final F<Character, Boolean> isWhitespace = Character::isWhitespace;
  public static final F<Character, Boolean> isISOControl = Character::isISOControl;
  public static final F<Character, Integer> getType = Character::getType;
  public static final F<Character, Byte> getDirectionality = Character::getDirectionality;
  public static final F<Character, Boolean> isMirrored = Character::isMirrored;
  public static final F<Character, Character> reverseBytes = Character::reverseBytes;
  public static final F<Character, Boolean> isNewLine = c -> c == '\n';

}
