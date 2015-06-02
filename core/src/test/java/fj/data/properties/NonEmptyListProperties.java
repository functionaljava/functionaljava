package fj.data.properties;

import fj.runner.PropertyTestRunner;
import fj.test.Property;
import org.junit.runner.RunWith;

import static fj.Function.identity;
import static fj.data.NonEmptyList.nel;
import static fj.test.Arbitrary.arbInteger;
import static fj.test.Arbitrary.arbNonEmptyList;
import static fj.test.Property.prop;
import static fj.test.Property.property;

/**
 * Created by Zheka Kozlov on 02.06.2015.
 */
@RunWith(PropertyTestRunner.class)
public class NonEmptyListProperties {

  public Property consHead() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(list.cons(n).head().equals(n)));
  }

  public Property consLength() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(list.cons(n).length() == list.length() + 1));
  }

  public Property positiveLength() {
    return property(arbNonEmptyList(arbInteger), list -> prop(list.length() > 0));
  }

  public Property appendLength() {
    return property(arbNonEmptyList(arbInteger), arbNonEmptyList(arbInteger), (list1, list2) ->
      prop(list1.append(list2).length() == list1.length() + list2.length()));
  }

  public Property appendSingle() {
    return property(arbNonEmptyList(arbInteger), arbInteger, (list, n) -> prop(nel(n).append(list).equals(list.cons(n))));
  }

  public Property tailLength() {
    return property(arbNonEmptyList(arbInteger), list -> prop(list.length() == 1 + list.tail().length()));
  }

  public Property mapId() {
    return property(arbNonEmptyList(arbInteger), list -> prop(list.map(identity()).equals(list)));
  }
}
