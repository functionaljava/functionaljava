package fj;


/**
 * A product-1. Also, the identity monad.
 *
 * @version %build.number%
 */
public interface P1<A> {
  /**
   * Access the first element of the product.
   *
   * @return The first element of the product.
   */
  public abstract A _1();
}
