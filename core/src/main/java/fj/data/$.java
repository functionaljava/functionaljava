package fj.data;

import fj.P1;

/**
 * The constant arrow, for attaching a new name to an existing type. For every pair of types A and B, this type
 * is the identity morphism from B to B.
 */
@SuppressWarnings({"UnusedDeclaration"})
public final class $<A, B> extends P1<B> {

  private final B b;

  private $(final B b) {
    this.b = b;
  }

  public static <A, B> $<A, B> _(final B b) {
    return new $<A, B>(b);
  }

  public B _1() {
    return b;
  }
}
