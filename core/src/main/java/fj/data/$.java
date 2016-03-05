package fj.data;

import fj.P1;

/**
 * The constant arrow, for attaching a new name to an existing type. For every pair of types A and B, this type
 * is the identity morphism from B to B.
 */
@SuppressWarnings("UnusedDeclaration")
public final class $<A, B> extends P1<B> {

  private final B b;

  private $(final B b) {
    this.b = b;
  }

  /**
   * Returns a function that given an argument, returns a function that ignores its argument.
   * @deprecated  JDK 8 warns '_' may not be supported after SE 8.  As of release 4.4, use {@link #constant} (note the synonym {@link #__}).
   * @return A function that given an argument, returns a function that ignores its argument.
   */
  @Deprecated
  public static <A, B> $<A, B> _(final B b) {
    return constant(b);
  }

	public static <A, B> $<A, B> __(final B b) {
		return constant(b);
	}

	public static <A, B> $<A, B> constant(final B b) {
		return new $<>(b);
	}


	public B _1() {
    return b;
  }
}
