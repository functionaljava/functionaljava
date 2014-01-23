package fj.function;

import static fj.Function.curry;
import fj.F;

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
	 * This function checks if a given String is neither <code>null</code> nor
	 * empty.
	 */
	public static final F<String, Boolean> isNotNullOrEmpty = a -> a != null
			&& a.length() > 0;

	/**
	 * A curried version of {@link String#isEmpty()}.
	 */
	public static final F<String, Boolean> isEmpty = s -> s.length() == 0;

	/**
	 * A curried version of {@link String#length()}.
	 */
	public static final F<String, Integer> length = s -> s.length();

	/**
	 * A curried version of {@link String#contains(CharSequence)}. The function
	 * returns true if the second argument contains the first.
	 */
	public static final F<String, F<String, Boolean>> contains = curry((s1, s2) -> s2
			.contains(s1));

	/**
	 * A curried version of {@link String#matches(String)}. The function returns
	 * true if the second argument matches the first.
	 */
	public static final F<String, F<String, Boolean>> matches = curry((s1, s2) -> s2
			.matches(s1));

}
