package fj.demo.test;

import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbLinkedList;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.prop;
import static fj.test.Property.property;

import java.util.LinkedList;

/*
For any two linked lists, the size after calling addAll on one with the other is equivalent to
adding the size of each.
*/
public final class JavaLinkedList {
  public static void main(final String[] args) {
    final Property p = property(arbLinkedList(arbInteger), arbLinkedList(arbInteger),
            (x, y) -> {
        final LinkedList<Integer> xy = new LinkedList<>(x);
        xy.addAll(y);
        return prop(xy.size() == x.size() + y.size());
    });
    summary.println(p.check()); // OK, passed 100 tests.
  }
}
