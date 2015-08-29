package fj.data;

import org.junit.Assert;
import org.junit.Test;

import static fj.data.Tree.leaf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Created by MarkPerry on 29/08/2015.
 */
public class TreeTest {

    @Test
    public void emptyLength() {
        Tree<Integer> t = leaf(1);
        assertThat(t.length(), equalTo(1));
    }

    @Test
    public void shallowStreamLength() {
        Tree<Integer> t2 = Tree.node(3, Stream.stream(leaf(4), leaf(5)));
        assertThat(t2.length(), equalTo(3));
    }

    @Test
    public void deepStreamLength() {
        Tree<Integer> t3 = Tree.node(4, Stream.stream(leaf(5), Tree.node(6, Stream.stream(leaf(7), leaf(8)))));
        assertThat(t3.length(), equalTo(5));
    }


    @Test
    public void singleIsLeft() {
        Tree<Integer> t = leaf(1);
        assertThat(t.isLeaf(), equalTo(true));
    }

    @Test
    public void shallowStreamIsLeaf() {
        Tree<Integer> t2 = Tree.node(3, Stream.stream(leaf(4), leaf(5)));
        assertThat(t2.isLeaf(), equalTo(false));
    }

    @Test
    public void deepStreamIsLeaf() {
        Tree<Integer> t3 = Tree.node(4, Stream.stream(leaf(5), Tree.node(6, Stream.stream(leaf(7), leaf(8)))));
        assertThat(t3.isLeaf(), equalTo(false));
    }

}
