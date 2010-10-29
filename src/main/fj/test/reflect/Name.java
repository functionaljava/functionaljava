package fj.test.reflect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The name of a property to be used in reporting.
 * 
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 5 $</li>
 *          <li>$LastChangedDate: 2008-12-06 16:49:43 +1000 (Sat, 06 Dec 2008) $</li>
 *          <li>$LastChangedBy: tonymorris $</li>
 *          </ul>
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Name {
  /**
   * The name of a property to be used in reporting.
   *
   * @return The name of a property to be used in reporting.
   */
  String value();
}
