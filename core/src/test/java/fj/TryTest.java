package fj;

import fj.data.Validation;
import fj.function.Try0;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TryTest {

    @Test
    public void testTrySuccess() {
        F<Try0<Integer, TryException>, Validation<TryException, Integer>> f =
                Try.f(Try0::f);
        Validation<TryException, Integer> v = f.f(new AlwaysSucceed());
        assertThat(v.isSuccess(), is(true));
        assertThat(v.success(), is(99));
    }

    @Test
    public void testTryFail() {
        F<Try0<Integer, TryException>, Validation<TryException, Integer>> f =
                Try.f(Try0::f);
        Validation<TryException, Integer> v = f.f(new AlwaysFail());
        assertThat(v.isFail(), is(true));
        assertThat(v.fail(), is(new TryException()));
    }

    class AlwaysSucceed implements Try0<Integer, TryException> {
        @Override
        public Integer f() throws TryException {
            return 99;
        }
    }

    class AlwaysFail implements Try0<Integer, TryException> {
        @Override
        public Integer f() throws TryException {
            throw new TryException();
        }
    }

    class TryException extends Exception {
        @Override
        public boolean equals (Object obj) {
            return (obj instanceof TryException);
        }
    }
}
