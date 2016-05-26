package fj.demo;

import fj.F;
import fj.data.Array;
import static fj.data.Array.array;
import static fj.function.Integers.add;

public final class Array_foldLeft {
    public static void main(final String[] args) {
        final Array<Integer> a = array(97, 44, 67, 3, 22, 90, 1, 77, 98, 1078, 6, 64, 6, 79, 42);
        final int b = a.foldLeft(add, 0);

        // WARNING: In JDK 8, update 20 and 25 (current version) the following code triggers an internal JDK compiler error, likely due to https://bugs.openjdk.java.net/browse/JDK-8062253.   The code below is a workaround for this compiler bug.
        //    final int c = a.foldLeft(i -> (j -> i + j), 0);
        F<Integer, F<Integer, Integer>> add2 = i -> j -> i + j;
        final int c = a.foldLeft(add2, 0);
        System.out.println(b); // 1774
    }
}
