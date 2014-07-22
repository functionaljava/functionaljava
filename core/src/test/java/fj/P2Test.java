package fj;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by MarkPerry on 22/07/2014.
 */
public class P2Test {

	@Test
	public void testToString() {
		String s = P.p(1, 2).toString();
		Assert.assertTrue(s.equals("(1,2)"));
	}

}
