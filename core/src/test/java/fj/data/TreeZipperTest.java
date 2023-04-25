package fj.data;

import org.junit.jupiter.api.Test;

import static fj.data.Option.none;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TreeZipperTest {
  @Test
  void testDelete() {
    final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
    final TreeZipper<Integer> tz = TreeZipper.fromTree(t);
    assertThat(tz.delete(), is(none()));
  }

  @Test
  void testDeleteForest() {
    final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
    final TreeZipper<Integer> tz = TreeZipper.fromForest(Stream.single(t)).some();
    assertThat(tz.delete(), is(none()));

  }

  @Test
  void testHash() {
    final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
    final TreeZipper<Integer> tz1 = TreeZipper.fromForest(Stream.single(t)).some();
    final TreeZipper<Integer> tz2 = TreeZipper.fromForest(Stream.single(t)).some();
    assertThat(tz1.hashCode(), is(tz2.hashCode()));
  }
}
