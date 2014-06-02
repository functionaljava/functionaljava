package fj;

/**
 * Created by MarkPerry on 6/04/2014.
 */
public class F3Functions {



    static public <A, B, C, D> F3<A, B, C, Try<D>> toF3(final Try3<A, B, C, D> t) {
        return (a, b, c) -> {
            try {
                return Try.trySuccess(t.f(a, b, c));
            } catch (Exception e) {
                return Try.<D>tryFail(e);
            }
        };
    }

}
