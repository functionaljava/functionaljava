package fj.demo;

import static fj.data.hlist.HList.HCons;
import static fj.data.hlist.HList.HNil;
import static fj.data.hlist.HList.HAppend.append;
import static fj.data.hlist.HList.HAppend;
import static fj.data.hlist.HList.nil;

/**
 * Append two heterogeneous lists
 */
public class HList_append {
  public static void main(final String[] args) {
    // The two lists
    final HCons<String, HCons<Integer, HCons<Boolean, HNil>>> a =
      nil().extend(true).extend(3).extend("Foo");
    final HCons<Double, HCons<String, HCons<Integer[], HNil>>> b =
      nil().extend(new Integer[]{1, 2}).extend("Bar").extend(4.0);

    // A lot of type annotation
    final HAppend<HNil, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
      HCons<Double, HCons<String, HCons<Integer[], HNil>>>> zero = append();
    final HAppend<HCons<Boolean, HNil>, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
      HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>> one = append(zero);
    final HAppend<HCons<Integer, HCons<Boolean, HNil>>, HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
      HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>> two = append(one);
    final HAppend<HCons<String, HCons<Integer, HCons<Boolean, HNil>>>,
      HCons<Double, HCons<String, HCons<Integer[], HNil>>>,
      HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>>>
      three = append(two);

    // And all of that lets us append one list to the other.
    final HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HCons<String, HCons<Integer[], HNil>>>>>>
      x = three.append(a, b);

    // And we can access the components of the concatenated list in a type-safe manner
    System.out.println(x.head()); // Foo
    System.out.println(x.tail().tail().tail().tail().head()); // Bar
  }
}
