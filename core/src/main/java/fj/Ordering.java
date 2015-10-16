package fj;

/**
 * The comparison of two instances of a type may have one of three orderings; less than, equal or
 * greater than.
 *
 * @version %build.number%
 */
public enum Ordering {
  /**
   * Less than.
   */
  LT,

  /**
   * Equal.
   */
  EQ,

  /**
   * Greater than.
   */
  GT;

  public int toInt() { return ordinal() - 1 ; }

  public Ordering reverse() {
    switch (this) {
      case LT: return GT;
      case GT: return LT;
    }
    return EQ;
  }

  public static Ordering fromInt(int cmp) {
    return cmp == 0 ? EQ : cmp > 0 ? GT : LT;
  }
}
