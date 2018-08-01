package fj.data;

import fj.Show;
import org.junit.Test;

import java.util.EnumSet;

import static fj.Show.listShow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by MarkPerry on 14/07/2014.
 */
public class JavaTest {

	@Test
	public void test1() {
		// #33: Fixes ClassCastException
		final List<Colors> colors = Java.<Colors>EnumSet_List().f(EnumSet.allOf(Colors.class));
		assertThat(listShow(Show.<Colors>anyShow()).showS(colors), is("List(red,green,blue)"));
	}

	enum Colors {
		red, green, blue
	}

}
