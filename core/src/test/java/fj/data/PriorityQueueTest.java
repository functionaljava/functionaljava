package fj.data;

import fj.Equal;
import fj.Monoid;
import org.junit.Assert;
import org.junit.Test;

import static fj.P.p;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by MarkPerry on 1 Jun 16.
 */
public class PriorityQueueTest {

    @Test
    public void test1() {
        PriorityQueue<Integer, Integer> pq = PriorityQueue.<Integer>emptyInt();
        PriorityQueue<Integer, Integer> pq2 = pq.enqueue(List.list(p(1, 1)));
        System.out.println(pq2.toString());
    }

    @Test
    public void empty() {
        PriorityQueue<Integer, Integer> pq = PriorityQueue.<Integer>emptyInt();
        assertThat(pq.isEmpty(), is(true));
    }

}
