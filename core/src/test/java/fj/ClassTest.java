package fj;

import fj.data.List;
import fj.data.Natural;
import fj.data.Option;
import fj.data.Tree;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassTest {
    @Test
    public void testInheritance() {
        Class<Natural> c = Class.clas(Natural.class);
        List<Class<? super Natural>> l = c.inheritance();
        assertThat(l.length(), is(3));
    }

    @Test
    public void testClassParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Tree<Type> cp = c.classParameters();
        assertThat(cp.length(), is(1));
    }

    @Test
    public void testSuperclassParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Tree<Type> cp = c.superclassParameters();
        assertThat(cp.length(), is(2));
    }

    @Test
    public void testInterfaceParameters() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        List<Tree<Type>> l =c.interfaceParameters();
        assertThat(l.length(), is(0));
    }

    @Test
    public void testTypeParameterTree() {
        Class<? extends Option> c = Class.clas(Option.none().getClass());
        Collection<Type> coll = c.classParameters().toCollection();
        for (Type t: coll) {
            assertThat(Class.typeParameterTree(t).toString(), is("Tree(class fj.data.Option$None)"));
        }
    }
}
