package fj.test.reflect;

import fj.P1;
import fj.test.Property;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the check parameters on a {@link Property} property with typical defaults.
 *
 * @version %build.number%
 */
@Documented
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CheckParams {
  /**
   * The minimum number of successful tests before a result is reached.
   *
   * @return The minimum number of successful tests before a result is reached.
   */
  int minSuccessful() default 100;

  /**
   * The maximum number of tests discarded because they did not satisfy pre-conditions
   * (i.e. {@link Property#implies(boolean, P1)}).
   *
   * @return The maximum number of tests discarded because they did not satisfy pre-conditions
   * (i.e. {@link Property#implies(boolean, P1)}).
   */
  int maxDiscarded() default 500;

  /**
   * The minimum size to use for checking.
   *
   * @return The minimum size to use for checking.
   */
  int minSize() default 0;

  /**
   * The maximum size to use for checking.
   *
   * @return The maximum size to use for checking.
   */
  int maxSize() default 100;
}
