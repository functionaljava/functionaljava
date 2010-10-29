/**
 * A wrapper around the <code>fj.test</code> package that uses annotations for configuring properties to
 * check. The properties are found using {@link java.lang.reflect Java Reflection}. All annotations
 * are optional and a property is eligible for checking by default. A property is any of the
 * following member descriptions, unless the member or enclosing class is annotated with
 * {@link NoCheck}.
 *
<ul>
  <li>a static field of type {@link Property}.</li>
  <li>a static zero argument method that returns {@link Property}.</li>
  <li>a non-static field of type {@link Property} in a class with a zero-argument constructor.</li>
  <li>a non-static no-argument method that returns {@link Property} in a class with a no-argument
      constructor.</li>
</ul>
 *
 * <p>
 * A property may be in zero or more categories by annotating the member or enclosing class with
 * {@link Category}. The property is in the set of categories that make up the
 * union of its member and enclosing class category annotation.
 * </p>
 * <p>
 * When a property is checked, it uses default configuration values, which may be overridden by
 * annotating the member or the enclosing class with the {@link CheckParams}
 * annotation. The values used are first those specified on the member; or if the annotation does
 * not exist, then the enclosing class (default values otherwise). 
 * </p>
 * <p>
 * A property can have a name associated with it by annotating the member with the
 * {@link Name} annotation. The name is a {@link String} that is used
 * only for reporting in check results. If the {@link Name} annotation does not
 * appear on a property member, then the field or method name is used by default. 
 * </p>
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision: 388 $</li>
 *          <li>$LastChangedDate: 2010-06-04 21:53:24 +1000 (Fri, 04 Jun 2010) $</li>
 *          <li>$LastChangedBy: tonymorris $</li>
 *          </ul>
 */
package fj.test.reflect;
