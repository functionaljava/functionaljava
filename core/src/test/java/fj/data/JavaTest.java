package fj.data;

import fj.Show;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

import static fj.Show.listShow;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JavaTest {

  @Test
  void test1() {
    // #33: Fixes ClassCastException
    final List<Colors> colors = Java.<Colors>EnumSet_List().f(EnumSet.allOf(Colors.class));
    assertThat(listShow(Show.<Colors>anyShow()).showS(colors), is("List(red,green,blue)"));
  }

  enum Colors {
    
    red, green, blue
  }

}
