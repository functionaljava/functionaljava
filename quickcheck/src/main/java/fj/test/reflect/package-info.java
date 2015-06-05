/**
 * A wrapper around the <code>fj.test</code> package that uses annotations for configuring properties to
 * check. The properties are found using reflection. All annotations
 * are optional and a property is eligible for checking by default. A property is any of the
 * following member descriptions, unless the member or enclosing class is annotated with
 * {@link fj.test.reflect.NoCheck}.
 *
<ul>
  <li>a static field of type {@link fj.test.Property}.</li>
  <li>a static zero argument method that returns {@link fj.test.Property}.</li>
  <li>a non-static field of type {@link fj.test.Property} in a class with a zero-argument constructor.</li>
  <li>a non-static no-argument method that returns {@link fj.test.Property} in a class with a no-argument
      constructor.</li>
</ul>
 *
 * <p>
 * A property may be in zero or more categories by annotating the member or enclosing class with
 * {@link fj.test.reflect.Category}. The property is in the set of categories that make up the
 * union of its member and enclosing class category annotation.
 * </p>
 * <p>
 * When a property is checked, it uses default configuration values, which may be overridden by
 * annotating the member or the enclosing class with the {@link fj.test.reflect.CheckParams}
 * annotation. The values used are first those specified on the member; or if the annotation does
 * not exist, then the enclosing class (default values otherwise). 
 * </p>
 * <p>
 * A property can have a name associated with it by annotating the member with the
 * {@link fj.test.reflect.Name} annotation. The name is a {@link java.lang.String} that is used
 * only for reporting in check results. If the {@link fj.test.reflect.Name} annotation does not
 * appear on a property member, then the field or method name is used by default. 
 * </p>
 *
 * @version %build.number%
 */
package fj.test.reflect;
