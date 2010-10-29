package fj;

/**
 * The unit type which has only one value.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 5 $</li>
 *          <li>$LastChangedDate: 2008-12-06 16:49:43 +1000 (Sat, 06 Dec 2008) $</li>
 *          </ul>
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
}
