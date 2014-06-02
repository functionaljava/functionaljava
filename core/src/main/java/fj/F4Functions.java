package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F4Functions {


    static public <A, B, C, D, E> F4<A, B, C, D, Try<E>> toF4(final Try4<A, B, C, D, E> t) {
        return (a, b, c, d) -> {
            try {
                return Try.trySuccess(t.f(a, b, c, d));
            } catch (Exception e) {
                return Try.<E>tryFail(e);
            }
        };
    }

}
