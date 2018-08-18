package fj.data;

import org.junit.Test;

import static fj.data.Option.none;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TreeZipperTest {
    @Test
    public void testDelete() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz = TreeZipper.fromTree(t);
        assertThat(tz.delete(), is(none()));
    }

    @Test
    public void testDeleteForest() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz = TreeZipper.fromForest(Stream.single(t)).some();
        assertThat(tz.delete(), is(none()));

    }

    @Test
    public void testHash() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz1 = TreeZipper.fromForest(Stream.single(t)).some();
        final TreeZipper<Integer> tz2 = TreeZipper.fromForest(Stream.single(t)).some();
        assertThat(tz1.hashCode(), is(tz2.hashCode()));
    }
}
