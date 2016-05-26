package fj.data;

import fj.F;
import fj.Unit;

import java.io.IOException;

/**
 * Created by MarkPerry on 9/06/2015.
 */
public final class IOW<A> implements IO<A> {

    private final IO<A> io;

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

    public SafeIO<Validation<IOException, A>> safe() {
        return IOFunctions.toSafeValidation(io);
    }

    public <B> IOW<B> map(F<A, B> f) { return lift(IOFunctions.map(io, f)); }

    public <B> IOW<B> bind(F<A, IO<B>> f) { return lift(IOFunctions.bind(io, f)); }

    public <B> IOW<B> append(IO<B> iob) { return lift(IOFunctions.append(io, iob)); }

    public static IOW<LazyString> getContents() {
        return lift(() -> IOFunctions.getContents().run());
    }

    public static IOW<Unit> interact(F<LazyString, LazyString> f) {
        return lift(() -> IOFunctions.interact(f).run());
    }
}
