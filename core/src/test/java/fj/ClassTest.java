package fj;

import fj.data.List;
import fj.data.Natural;
import fj.data.Option;
import fj.data.Tree;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClassTest {
  @Test
  void testInheritance() {
    Class<Natural> c = Class.clas(Natural.class);
    List<Class<? super Natural>> l = c.inheritance();
    assertThat(l.length(), is(3));
  }

  @Test
  void testClassParameters() {
    Class<? extends Option> c = Class.clas(Option.none().getClass());
    Tree<Type> cp = c.classParameters();
    assertThat(cp.length(), is(1));
  }

  @Test
  void testSuperclassParameters() {
    Class<? extends Option> c = Class.clas(Option.none().getClass());
    Tree<Type> cp = c.superclassParameters();
    assertThat(cp.length(), is(2));
  }

  @Test
  void testInterfaceParameters() {
    Class<? extends Option> c = Class.clas(Option.none().getClass());
    List<Tree<Type>> l = c.interfaceParameters();
    assertThat(l.length(), is(0));
  }

  @Test
  void testTypeParameterTree() {
    Class<? extends Option> c = Class.clas(Option.none().getClass());
    Collection<Type> coll = c.classParameters().toCollection();
    for (Type t : coll) {
      assertThat(Class.typeParameterTree(t).toString(), is("Tree(class fj.data.Option$None)"));
    }
  }
}
