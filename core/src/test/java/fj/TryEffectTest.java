package fj;

import fj.data.Validation;
import fj.function.TryEffect0;
import fj.function.TryEffect1;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TryEffectTest {

    @Test
    public void testTryEffect0Success() {
        F<TryEffect0<TryEffectException>, Validation<TryEffectException, Unit>> f = TryEffect.f(TryEffect0::f);
        Validation<TryEffectException, Unit> v = f.f(new AlwaysSucceed0());
        assertThat(v.isSuccess(), is(true));
        assertThat(v.success(), is(Unit.unit()));
    }

    @Test
    public void testTryEffect0Fail() {
        F<TryEffect0<TryEffectException>, Validation<TryEffectException, Unit>> f = TryEffect.f(TryEffect0::f);
        Validation<TryEffectException, Unit> v = f.f(new AlwaysFail0());
        assertThat(v.isFail(), is(true));
        assertThat(v.fail(), is(new TryEffectException()));
    }

    @Test
    public void testTryEffect1Success() {
        F2<TryEffect1<Integer, TryEffectException>, Integer, Validation<TryEffectException, Unit>> f =
                TryEffect.f(TryEffect1<Integer, TryEffectException>::f);
        Validation<TryEffectException, Unit> v = f.f(new AlwaysSucceed1(), 1);
        assertThat(v.isSuccess(), is(true));
        assertThat(v.success(), is(Unit.unit()));
    }

    @Test
    public void testTryEffect1Fail() {
        F2<TryEffect1<Integer, TryEffectException>, Integer, Validation<TryEffectException, Unit>> f =
                TryEffect.f(TryEffect1<Integer, TryEffectException>::f);
        Validation<TryEffectException, Unit> v = f.f(new AlwaysFail1(), 1);
        assertThat(v.isFail(), is(true));
        assertThat(v.fail(), is(new TryEffectException()));
    }

    class AlwaysSucceed0 implements TryEffect0<TryEffectException> {
        @Override
        public void f() throws TryEffectException {
            // SUCCESS
        }
    }

    class AlwaysSucceed1 implements TryEffect1<Integer, TryEffectException> {
        @Override
        public void f(Integer i) throws TryEffectException {
            // SUCCESS;
        }
    }

    class AlwaysFail0 implements TryEffect0<TryEffectException> {
        @Override
        public void f() throws TryEffectException {
            throw new TryEffectException();
        }
    }

    class AlwaysFail1 implements TryEffect1<Integer, TryEffectException> {
        @Override
        public void f(Integer i) throws TryEffectException {
            throw new TryEffectException();
        }
    }

    class TryEffectException extends Exception {
        @Override
        public boolean equals (Object obj) {
            return (obj instanceof TryEffectException);
        }
    }
}
