package fj.function;

import org.junit.Test;

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
