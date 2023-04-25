package fj.data;

import fj.F;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static fj.data.List.list;
import static fj.data.Option.some;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class List_Traverse_Tests {

  @Test
  void shouldTraverseListWithGivenFunction() {
    List<String> strings = list("some1", "some2", "some3", "not_some", "  ");
    F<String, Option<String>> f = s -> {
      if (s.startsWith("some"))
        return some(s);
      else
        return Option.none();
    };

    Option<List<String>> optStr = strings.traverseOption(f);
    Assertions.assertEquals(Option.none(), optStr, "optStr should be none");
  }

  @Test
  void shouldTraverseListWithGivenFunction2() {
    List<String> strings = list("some1", "some2", "some3");
    F<String, Option<String>> f = s -> {
      if (s.startsWith("some"))
        return some(s);
      else
        return Option.none();
    };

    Option<List<String>> optStr = strings.traverseOption(f);
    Assertions.assertEquals(optStr.isSome(), true, "optStr should be some");
    assertThat(optStr.some(), is(List.list("some1", "some2", "some3")));
  }

}
