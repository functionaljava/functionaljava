package fj.test.reflect;

import fj.Class;
import static fj.Class.clas;
import fj.F;
import fj.Function;
import fj.P;
import static fj.P.p;
import fj.P2;
import fj.P3;
import fj.data.Array;
import static fj.data.Array.array;
import fj.data.List;
import static fj.data.List.join;
import static fj.data.List.list;
import fj.data.Option;
import static fj.data.Option.fromNull;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.Option.somes;
import fj.test.CheckResult;
import fj.test.Property;
import fj.test.Rand;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isStatic;

/**
 * Functions for checking properties in a class that are found reflectively and according to various
 * annotations.
 * 
 * @version %build.number%
 */
public final class Check {
  private Check() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the results and names of checking the properties on the given classes using a
   * {@link Rand#standard standard random generator}.
   *
   * @param c The classes to check the properties of.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given classes using a
   * {@link Rand#standard standard random generator}.
   */
  public static <T> List<P2<String, CheckResult>> check(final List<java.lang.Class<T>> c, final String... categories) {
    return check(c, Rand.standard, categories);
  }

  /**
   * Returns the results and names of checking the properties on the given classes using a
   * {@link Rand#standard standard random generator}.
   *
   * @param c The classes to check the properties of.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given classes using a
   * {@link Rand#standard standard random generator}.
   */
  public static <T> List<P2<String, CheckResult>> check(final List<java.lang.Class<T>> c, final List<String> categories) {
    return check(c, Rand.standard, categories.toArray().array(String[].class));
  }

  /**
   * Returns the results and names of checking the properties on the given classes.
   *
   * @param c The classes to check the properties of.
   * @param r The random generator to use to check the properties on the given classes.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given classes.
   */
  public static <T> List<P2<String, CheckResult>> check(final List<java.lang.Class<T>> c, final Rand r, final String... categories) {
    return join(c.map(c1 -> check(c1, r, categories)));
  }

  /**
   * Returns the results and names of checking the properties on the given classes.
   *
   * @param c The classes to check the properties of.
   * @param r The random generator to use to check the properties on the given classes.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given classes.
   */
  public static <T> List<P2<String, CheckResult>> check(final List<java.lang.Class<T>> c, final Rand r, final List<String> categories) {
    return check(c, r, categories.toArray().array(String[].class));
  }

  /**
   * Returns the results and names of checking the properties on the given class using a
   * {@link Rand#standard standard random generator}.
   *
   * @param c The class to check the properties of.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given class using a
   * {@link Rand#standard standard random generator}.
   */
  public static <T> List<P2<String, CheckResult>> check(final java.lang.Class<T> c, final String... categories) {
    return check(c, Rand.standard, categories);
  }

  /**
   * Returns the results and names of checking the properties on the given class using a
   * {@link Rand#standard standard random generator}.
   *
   * @param c The class to check the properties of.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results and names of checking the properties on the given class using a
   * {@link Rand#standard standard random generator}.
   */
  public static <T> List<P2<String, CheckResult>> check(final java.lang.Class<T> c, final List<String> categories) {
    return check(c, Rand.standard, categories.toArray().array(String[].class));
  }

  /**
   * Returns the results and names of checking the properties on the given class.
   *
   * @param c The class to check the properties of.
   * @param r The random generator to use to check the properties on the given class.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results of checking the properties on the given class.
   */
  public static <T> List<P2<String, CheckResult>> check(final java.lang.Class<T> c, final Rand r, final String... categories) {
    return join(clas(c).inheritance().map(c1 -> properties(c1.clas(), categories))).map(p -> {
      if(p._3().isSome()) {
        final CheckParams ps = p._3().some();
        return p(p._2(), p._1().check(r, ps.minSuccessful(), ps.maxDiscarded(), ps.minSize(), ps.maxSize()));
      } else
        return p(p._2(), p._1().check(r));
    });
  }

  /**
   * Returns the results and names of checking the properties on the given class.
   *
   * @param c The class to check the properties of.
   * @param r The random generator to use to check the properties on the given class.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return The results of checking the properties on the given class.
   */
  public static <T> List<P2<String, CheckResult>> check(final java.lang.Class<T> c, final Rand r, final List<String> categories) {
    return check(c, r, categories.toArray().array(String[].class));
  }

  /**
   * Returns all properties, their name and possible check parameters in a given class that are
   * found reflectively and according to various annotations. For example, properties or their
   * enclosing class that are annotated with {@link NoCheck} are not considered. The name of a
   * property is specified by the {@link Name annotation} or if this annotation is not present, the
   * name of the method or field that represents the property.
   *
   * @param c The class to look for properties on.
   * @param categories The categories of properties to return. If no categories are specified, all
   * candidate properties are returned, otherwise, only those properties in the given categories are
   * returned (properties in no category are omitted in this latter case).
   * @return All properties, their name and possible check parameters in a given class that are
   * found reflectively and according to various annotations.
   */
  public static <U, T extends U> List<P3<Property, String, Option<CheckParams>>> properties(final java.lang.Class<T> c, final String... categories) {
    //noinspection ClassEscapesDefinedScope
    final Array<P3<Property, String, Option<CheckParams>>> propFields = properties(array(c.getDeclaredFields()).map((F<Field, PropertyMember>) f -> new PropertyMember() {
      public java.lang.Class<?> type() {
        return f.getType();
      }

      public AnnotatedElement element() {
        return f;
      }

      public String name() {
        return f.getName();
      }

      public int modifiers() {
        return f.getModifiers();
      }

      public <X> Property invoke(final X x) throws IllegalAccessException {
        f.setAccessible(true);
        return (Property)f.get(x);
      }

      public boolean isProperty() {
        return true;
      }
    }), c, categories);

    //noinspection ClassEscapesDefinedScope
    final Array<P3<Property, String, Option<CheckParams>>> propMethods = properties(array(c.getDeclaredMethods()).map((F<Method, PropertyMember>) m -> {
      //noinspection ProhibitedExceptionDeclared
      return new PropertyMember() {
        public java.lang.Class<?> type() {
          return m.getReturnType();
        }

        public AnnotatedElement element() {
          return m;
        }

        public String name() {
          return m.getName();
        }

        public int modifiers() {
          return m.getModifiers();
        }

        public <X> Property invoke(final X x) throws Exception {
          m.setAccessible(true);
          return (Property)m.invoke(x);
        }

        public boolean isProperty() {
          return m.getParameterTypes().length == 0;
        }
      };
    }), c, categories);

    return propFields.append(propMethods).toList();
  }

  private interface PropertyMember {
    java.lang.Class<?> type();
    AnnotatedElement element();
    String name();
    int modifiers();
    @SuppressWarnings("ProhibitedExceptionDeclared")
    <X> Property invoke(X x) throws Exception;
    boolean isProperty();
  }

  private static <T> Array<P3<Property, String, Option<CheckParams>>> properties(final Array<PropertyMember> ms, final java.lang.Class<T> declaringClass, final String... categories) {
    final Option<T> t = emptyCtor(declaringClass).map(ctor -> {
      try {
        ctor.setAccessible(true);
        return ctor.newInstance();
      } catch(Exception e) {
        throw new Error(e.getMessage(), e);
      }
    });

    final F<AnnotatedElement, F<String, Boolean>> p = e -> s -> {
      final F<Category, Boolean> p1 = c -> array(c.value()).exists(cs -> cs.equals(s));

      @SuppressWarnings("unchecked")
      final List<Boolean> bss = somes(list(fromNull(e.getAnnotation(Category.class)).map(p1),
        fromNull(declaringClass.getAnnotation(Category.class)).map(p1)));
      return bss.exists(Function.identity());
    };

    final F<Name, String> nameS = Name::value;

    return ms.filter(m -> {
      //noinspection ObjectEquality
      return m.isProperty() &&
          m.type() == Property.class &&
          !m.element().isAnnotationPresent(NoCheck.class) &&
          !declaringClass.isAnnotationPresent(NoCheck.class) &&
          (categories.length == 0 || array(categories).exists(p.f(m.element()))) &&
          (t.isSome() || isStatic(m.modifiers()));
    }).map(m -> {
      try {
        final Option<CheckParams> params = fromNull(m.element().getAnnotation(CheckParams.class)).orElse(fromNull(declaringClass.getAnnotation(CheckParams.class)));
        final String name = fromNull(m.element().getAnnotation(Name.class)).map(nameS).orSome(m.name());
        return p(m.invoke(t.orSome(P.p(null))), name, params);
      } catch(Exception e) {
        throw new Error(e.getMessage(), e);
      }
    });
  }

  private static <T> Option<Constructor<T>> emptyCtor(final java.lang.Class<T> c) {
    Option<Constructor<T>> ctor;

    //noinspection UnusedCatchParameter
    try {
      ctor = some(c.getDeclaredConstructor());
    } catch(NoSuchMethodException e) {
      ctor = none();
    }
    return ctor;
  }
}
