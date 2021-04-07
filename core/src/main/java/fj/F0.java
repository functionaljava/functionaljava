package fj;

import java.util.function.Supplier;

/**
 * Created by MarkPerry on 21/01/2015.
 */
@FunctionalInterface
public interface F0<A> extends Supplier<A> {

    A f();

    default A get() {
        return f();
    }

}
