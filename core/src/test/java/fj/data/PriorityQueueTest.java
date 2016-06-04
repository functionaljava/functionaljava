package fj.data;

import fj.P2;
import org.junit.Test;

import static fj.P.p;
import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.equalTo;
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
        out.println(pq2.toString());
    }

    @Test
    public void empty() {
        PriorityQueue<Integer, Integer> pq = PriorityQueue.<Integer>emptyInt();
        assertThat(pq.isEmpty(), is(true));
    }

    @Test
    public void top() {
        PriorityQueue<Integer, Integer> pq1 = PriorityQueue.emptyInt();
        Option<P2<Integer, Integer>> o = pq1.top();
        assertThat(o, is(Option.none()));
    }

    @Test
    public void dequeue() {
        PriorityQueue<Integer, Integer> pq1 = PriorityQueue.<Integer>emptyInt().enqueue(List.list(
                p(3, 6), p(10, 20), p(4, 8), p(1, 2)
        ));
        out.println(pq1);
        assertThat(pq1.toString(), equalTo("PriorityQueue((3: 6), (10: 20), (4: 8), (1: 2))"));

        PriorityQueue<Integer, Integer> pq2 = PriorityQueue.emptyInt();
        PriorityQueue<Integer, Integer> pq3 = pq2.dequeue();
        out.println(pq3);
        assertThat(pq3.toString(), equalTo("PriorityQueue()"));

        PriorityQueue<Integer, Integer> pq4 = pq1.dequeue();
        out.println(pq4);
        assertThat(pq4.toString(), equalTo("PriorityQueue((3: 6), (4: 8), (1: 2))"));
    }

}
