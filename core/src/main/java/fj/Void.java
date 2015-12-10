package fj;

/**
 * A logically uninhabited data type.
 */
public abstract class Void {

  private Void() {
    throw new IllegalStateException("Void cannot be instantiated");
  }

  /**
   * Since Void values logically don't exist, this witnesses the logical reasoning tool of "ex falso quodlibet".
   */
  public abstract <X> X absurd();
}
