package fj;

import fj.data.Tree;
import fj.data.TreeZipper;

import org.junit.jupiter.api.Test;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FFunctionsTest {

  @Test
  void testTreeK() {
    final Tree<Integer> t1 = Function.<Integer>identity().treeK().f(1);
    final Tree<Integer> t2 = Function.<Integer>identity().treeK().f(2);
    F<Integer, Integer> f = i -> i + 1;
    F<Integer, Integer> g = i -> i * 1;
    assertThat(f.mapTree().f(t1),
        is(g.mapTree().f(t2)));
  }

  @Test
  void testTreeZipperK() {
    final TreeZipper<Integer> tz1 = Function.<Integer>identity().treeZipperK().f(1);
    final TreeZipper<Integer> tz2 = Function.<Integer>identity().treeZipperK().f(2);
    F<Integer, Integer> f = i -> i + 1;
    F<Integer, Integer> g = i -> i * 1;
    assertThat(f.mapTreeZipper().f(tz1),
        is(g.mapTreeZipper().f(tz2)));
  }
}
