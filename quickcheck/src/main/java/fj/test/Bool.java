package fj.test;

import fj.F0;

import static fj.test.Property.prop;

/**
 * A boolean wrapper that works well with properties.
 *
 * @version %build.number%
 */
public final class Bool {
  private final boolean b;

  private static final Bool t = new Bool(true);
  private static final Bool f = new Bool(false);

  private Bool(final boolean b) {
    this.b = b;
  }

  /**
   * Returns <code>true</code> if this value is true, <code>false</code> otherwise.
   *
   * @return <code>true</code> if this value is true, <code>false</code> otherwise.
   */
  public boolean is() {
    return b;
  }

  /**
   * Returns <code>false</code> if this value is true, <code>true</code> otherwise.
   *
   * @return <code>false</code> if this value is true, <code>true</code> otherwise.
   */
  public boolean isNot() {
    return !b;
  }

  /**
   * Returns a property that produces a result only if this value is true. The result will be taken
   * from the given property.
   *
   * @param p The property to return if this value is true.
   * @return a property that produces a result only if this value is true.
   */
  public Property implies(final F0<Property> p) {
    return Property.implies(b, p);
  }

  /**
   * Returns a property that produces a result only if this value is true. The result will be taken
   * from the given property.
   *
   * @param p The property to return if this value is true.
   * @return a property that produces a result only if this value is true.
   */
  public Property implies(final Property p) {
    return Property.implies(b, () -> p);
  }

  /**
   * Returns a property that produces a result only if this value is true.
   *
   * @param c The value to construct a property with to return if this value is true.
   * @return a property that produces a result only if this value is true.
   */
  public Property implies(final Bool c) {
    return implies(prop(c.b));
  }

  /**
   * Returns a property that produces a result only if this value is true.
   *
   * @param c The value to construct a property with to return if this value is true.
   * @return a property that produces a result only if this value is true.
   */
  public Property implies(final boolean c) {
    return Property.implies(b, () -> prop(c));
  }

  /**
   * Construct a <code>Bool</code> from the given value.
   *
   * @param b The value to construct a <code>Bool</code> with.
   * @return A <code>Bool</code> from the given value.
   */
  public static Bool bool(final boolean b) {
    return b ? t : f;
  }
}
