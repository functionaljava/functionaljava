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
	public static final F<Character, Boolean> isLowerCase = Character::isLowerCase;
	public static final F<Character, Boolean> isUpperCase = Character::isUpperCase;
	public static final F<Character, Boolean> isTitleCase = Character::isTitleCase;
	public static final F<Character, Boolean> isDigit = Character::isDigit;
	public static final F<Character, Boolean> isDefined = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isDefined(ch);
		}
	};
	public static final F<Character, Boolean> isLetter = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isLetter(ch);
		}
	};
	public static final F<Character, Boolean> isLetterOrDigit = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isLetterOrDigit(ch);
		}
	};
	public static final F<Character, Boolean> isJavaIdentifierStart = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isJavaIdentifierStart(ch);
		}
	};
	public static final F<Character, Boolean> isJavaIdentifierPart = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isJavaIdentifierPart(ch);
		}
	};
	public static final F<Character, Boolean> isUnicodeIdentifierStart = Character::isUnicodeIdentifierStart;

	public static final F<Character, Boolean> isUnicodeIdentifierPart = Character::isUnicodeIdentifierPart;

	public static final F<Character, Boolean> isIdentifierIgnorable = Character::isIdentifierIgnorable;
	public static final F<Character, Character> toLowerCase = Character::toLowerCase;
	public static final F<Character, Character> toUpperCase = Character::toUpperCase;
	public static final F<Character, Character> toTitleCase = Character::toTitleCase;
	public static final F<Character, F<Integer, Integer>> digit = curry((ch,
			radix) -> Character.digit(ch, radix));
	public static final F<Character, Integer> getNumericValue = new F<Character, Integer>() {
		public Integer f(final Character ch) {
			return Character.getNumericValue(ch);
		}
	};
	public static final F<Character, Boolean> isSpaceChar = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isSpaceChar(ch);
		}
	};
	public static final F<Character, Boolean> isWhitespace = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isWhitespace(ch);
		}
	};
	public static final F<Character, Boolean> isISOControl = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isISOControl(ch);
		}
	};
	public static final F<Character, Integer> getType = new F<Character, Integer>() {
		public Integer f(final Character ch) {
			return Character.getType(ch);
		}
	};
	public static final F<Character, Byte> getDirectionality = new F<Character, Byte>() {
		public Byte f(final Character ch) {
			return Character.getDirectionality(ch);
		}
	};
	public static final F<Character, Boolean> isMirrored = new F<Character, Boolean>() {
		public Boolean f(final Character ch) {
			return Character.isMirrored(ch);
		}
	};
	public static final F<Character, Character> reverseBytes = new F<Character, Character>() {
		public Character f(final Character ch) {
			return Character.reverseBytes(ch);
		}
	};
	public static final F<Character, Boolean> isNewLine = new F<Character, Boolean>() {
		public Boolean f(final Character c) {
			return c == '\n';
		}
	};
}
