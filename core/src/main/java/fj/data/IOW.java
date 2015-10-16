package fj.data;

import fj.F;
import fj.Unit;

import java.io.IOException;

/**
 * Created by MarkPerry on 9/06/2015.
 */
public class IOW<A> implements IO<A> {

    private IO<A> io;

    private IOW(IO<A> in) {
        io = in;
    }

    public static <A> IOW<A> lift(IO<A> io) {
        return new IOW<>(io);
    }

    @Override
    public A run() throws IOException {
        return io.run();
    }

    public <B> IOW<B> map(F<A, B> f) {
        return lift(() -> f.f(io.run()));
    }

    public <B> IOW<B> bind(F<A, IO<B>> f) throws IOException {
        return lift(f.f(io.run()));
    }

    public <B> IOW<B> append(IO<B> iob) {
        return lift(() -> {
            io.run();
            return iob.run();
        });
    }

    public IOW<LazyString> getContents() {
        return lift(() -> IOFunctions.getContents().run());
    }

    public IOW<Unit> interact(F<LazyString, LazyString> f) {
        return IOW.lift(() -> IOFunctions.interact(f).run());
    }
}
