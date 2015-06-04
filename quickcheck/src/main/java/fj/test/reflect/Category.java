package fj.test.reflect;

import fj.test.Property;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the categories of a {@link Property property}, which are the union of
 * categories specified on the enclosing class and the categories specified on the method or field
 * that make up the property.
 *
 * @version %build.number%
 */
@Documented
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Category {
  /**
   * The categories of the property.
   *
   * @return The categories of the property.
   */
  String[] value();
}
