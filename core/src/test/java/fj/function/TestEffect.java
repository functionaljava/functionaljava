package fj.function;

import fj.F;
import org.junit.Test;

/**
 * Created by mperry on 28/08/2014.
 */
public class TestEffect {

	@Test
	public void test1() {
		higher(TestEffect::m1);
	}


	static void higher(Effect1<String> f) {

	}

	static void m1(String s) {

	}

}
