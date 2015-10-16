package fj;

/**
 * The unit type which has only one value.
 *
 * @version %build.number%
 */
public final class Unit {
  private static final Unit u = new Unit();

  private Unit() {

  }

  /**
   * The only value of the unit type.
   *
   * @return The only value of the unit type.
   */
  public static Unit unit() {
    return u;
  }

  @Override
  public String toString() {
    return "unit";
  }

}
