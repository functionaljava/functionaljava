package fj.data;

import static fj.Bottom.errorF;
import static fj.Function.constant;
import static fj.Function.partialApply2;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import fj.*;
import fj.data.Iteratee.Input;
import fj.data.Iteratee.IterV;
import fj.function.Try0;
import fj.function.Try1;

/**
 * IO monad for processing files, with main methods {@link #enumFileLines },
 * {@link #enumFileChars} and {@link #enumFileCharChunks}
 * (the latter one is the fastest as char chunks read from the file are directly passed to the iteratee
 * without indirection in between).
 *
 * @author Martin Grotzke
 */
public final class IOFunctions {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private IOFunctions() {
    }

    public static <A> Try0<A, IOException> toTry(IO<A> io) {
        return io::run;
    }

    public static <A> P1<Validation<IOException, A>> p(IO<A> io) {
        return Try.f(toTry(io));
    }

    public static <A> IO<A> fromF(F0<A> p) {
        return p::f;
    }

    public static <A> IO<A> fromTry(Try0<A, ? extends IOException> t) {
        return t::f;
    }

    public static final F<Reader, IO<Unit>> closeReader = IOFunctions::closeReader;

    /**
     * Convert io to a SafeIO, throwing any IOException wrapped inside a RuntimeException
     * @param io
     */
    public static <A> SafeIO<A> toSafe(IO<A> io) {
        return () -> {
            try {
                return io.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Run io, rethrowing any IOException wrapped in a RuntimeException
     * @param io
     */
    public static <A> A runSafe(IO<A> io) {
        return toSafe(io).run();
    }

    public static IO<Unit> closeReader(final Reader r) {
        return () -> {
            r.close();
            return Unit.unit();
        };
    }

    /**
     * An IO monad that reads lines from the given file (using a {@link BufferedReader}) and passes
     * lines to the provided iteratee. May not be suitable for files with very long
     * lines, consider to use {@link #enumFileCharChunks} or {@link #enumFileChars}
     * as an alternative.
     *
     * @param f        the file to read, must not be <code>null</code>
     * @param encoding the encoding to use, {@link Option#none()} means platform default
     * @param i        the iteratee that is fed with lines read from the file
     */
    public static <A> IO<IterV<String, A>> enumFileLines(final File f, final Option<Charset> encoding, final IterV<String, A> i) {
        return bracket(bufferedReader(f, encoding)
                , Function.vary(closeReader)
                , partialApply2(IOFunctions.lineReader(), i));
    }

    /**
     * An IO monad that reads char chunks from the given file and passes them to the given iteratee.
     *
     * @param f        the file to read, must not be <code>null</code>
     * @param encoding the encoding to use, {@link Option#none()} means platform default
     * @param i        the iteratee that is fed with char chunks read from the file
     */
    public static <A> IO<IterV<char[], A>> enumFileCharChunks(final File f, final Option<Charset> encoding, final IterV<char[], A> i) {
        return bracket(fileReader(f, encoding)
                , Function.vary(closeReader)
                , partialApply2(IOFunctions.charChunkReader(), i));
    }

    /**
     * An IO monad that reads char chunks from the given file and passes single chars to the given iteratee.
     *
     * @param f        the file to read, must not be <code>null</code>
     * @param encoding the encoding to use, {@link Option#none()} means platform default
     * @param i        the iteratee that is fed with chars read from the file
     */
    public static <A> IO<IterV<Character, A>> enumFileChars(final File f, final Option<Charset> encoding, final IterV<Character, A> i) {
        return bracket(fileReader(f, encoding)
                , Function.vary(closeReader)
                , partialApply2(IOFunctions.charChunkReader2(), i));
    }

    public static IO<BufferedReader> bufferedReader(final File f, final Option<Charset> encoding) {
        return map(fileReader(f, encoding), BufferedReader::new);
    }

    public static IO<Reader> fileReader(final File f, final Option<Charset> encoding) {
        return () -> {
            final FileInputStream fis = new FileInputStream(f);
            return encoding.isNone() ? new InputStreamReader(fis) : new InputStreamReader(fis, encoding.some());
        };
    }

    public static <A, B, C> IO<C> bracket(final IO<A> init, final F<A, IO<B>> fin, final F<A, IO<C>> body) {
        return () -> {
            final A a = init.run();
            try(Closeable finAsCloseable = fin.f(a)::run) {
                return body.f(a).run();
            }
        };
    }

    public static <A> IO<A> unit(final A a) {
        return () -> a;
    }

    public static final IO<Unit> ioUnit = unit(Unit.unit());

    public static <A> IO<A> lazy(final F0<A> p) {
        return fromF(p);
    }

    public static <A> IO<A> lazy(final F<Unit, A> f) {
        return () -> f.f(Unit.unit());
    }

    public static <A> SafeIO<A> lazySafe(final F<Unit, A> f) {
        return () -> f.f(Unit.unit());
    }

    public static <A> SafeIO<A> lazySafe(final F0<A> f) {
        return f::f;
    }

    /**
     * A function that feeds an iteratee with lines read from a {@link BufferedReader}.
     */
    public static <A> F<BufferedReader, F<IterV<String, A>, IO<IterV<String, A>>>> lineReader() {
        final F<IterV<String, A>, Boolean> isDone =
                new F<Iteratee.IterV<String, A>, Boolean>() {
                    final F<P2<A, Input<String>>, P1<Boolean>> done = constant(P.p(true));
                    final F<F<Input<String>, IterV<String, A>>, P1<Boolean>> cont = constant(P.p(false));

                    @Override
                    public Boolean f(final IterV<String, A> i) {
                        return i.fold(done, cont)._1();
                    }
                };

        return r -> new F<IterV<String, A>, IO<IterV<String, A>>>() {
            final F<P2<A, Input<String>>, P1<IterV<String, A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

            @Override
            public IO<IterV<String, A>> f(final IterV<String, A> it) {
                // use loop instead of recursion because of missing TCO
                return () -> {
                    IterV<String, A> i = it;
                    while (!isDone.f(i)) {
                        final String s = r.readLine();
                        if (s == null) {
                            return i;
                        }
                        final Input<String> input = Input.el(s);
                        final F<F<Input<String>, IterV<String, A>>, P1<IterV<String, A>>> cont = F1Functions.lazy(Function.apply(input));
                        i = i.fold(done, cont)._1();
                    }
                    return i;
                };
            }
        };
    }

    /**
     * A function that feeds an iteratee with character chunks read from a {@link Reader}
     * (char[] of size {@link #DEFAULT_BUFFER_SIZE}).
     */
    public static <A> F<Reader, F<IterV<char[], A>, IO<IterV<char[], A>>>> charChunkReader() {
        final F<IterV<char[], A>, Boolean> isDone =
                new F<Iteratee.IterV<char[], A>, Boolean>() {
                    final F<P2<A, Input<char[]>>, P1<Boolean>> done = constant(P.p(true));
                    final F<F<Input<char[]>, IterV<char[], A>>, P1<Boolean>> cont = constant(P.p(false));

                    @Override
                    public Boolean f(final IterV<char[], A> i) {
                        return i.fold(done, cont)._1();
                    }
                };

        return r -> new F<IterV<char[], A>, IO<IterV<char[], A>>>() {
            final F<P2<A, Input<char[]>>, P1<IterV<char[], A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

            @Override
            public IO<IterV<char[], A>> f(final IterV<char[], A> it) {
                // use loop instead of recursion because of missing TCO
                return () -> {

                    IterV<char[], A> i = it;
                    while (!isDone.f(i)) {
                        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                        final int numRead = r.read(buffer);
                        if (numRead == -1) {
                            return i;
                        }
                        if (numRead < buffer.length) {
                            buffer = Arrays.copyOfRange(buffer, 0, numRead);
                        }
                        final Input<char[]> input = Input.el(buffer);
                        final F<F<Input<char[]>, IterV<char[], A>>, P1<IterV<char[], A>>> cont =
                                F1Functions.lazy(Function.apply(input));
                        i = i.fold(done, cont)._1();
                    }
                    return i;
                };
            }
        };
    }

    /**
     * A function that feeds an iteratee with characters read from a {@link Reader}
     * (chars are read in chunks of size {@link #DEFAULT_BUFFER_SIZE}).
     */
    public static <A> F<Reader, F<IterV<Character, A>, IO<IterV<Character, A>>>> charChunkReader2() {
        final F<IterV<Character, A>, Boolean> isDone =
                new F<Iteratee.IterV<Character, A>, Boolean>() {
                    final F<P2<A, Input<Character>>, P1<Boolean>> done = constant(P.p(true));
                    final F<F<Input<Character>, IterV<Character, A>>, P1<Boolean>> cont = constant(P.p(false));

                    @Override
                    public Boolean f(final IterV<Character, A> i) {
                        return i.fold(done, cont)._1();
                    }
                };

        return r -> new F<IterV<Character, A>, IO<IterV<Character, A>>>() {
            final F<P2<A, Input<Character>>, IterV<Character, A>> done = errorF("iteratee is done"); //$NON-NLS-1$

            @Override
            public IO<IterV<Character, A>> f(final IterV<Character, A> it) {
                // use loop instead of recursion because of missing TCO
                return () -> {

                    IterV<Character, A> i = it;
                    while (!isDone.f(i)) {
                        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                        final int numRead = r.read(buffer);
                        if (numRead == -1) {
                            return i;
                        }
                        if (numRead < buffer.length) {
                            buffer = Arrays.copyOfRange(buffer, 0, numRead);
                        }
                        for (char c : buffer) {
                            final Input<Character> input = Input.el(c);
                            final F<F<Input<Character>, IterV<Character, A>>, IterV<Character, A>> cont =
                                Function.apply(input);
                            i = i.fold(done, cont);
                        }
                    }
                    return i;
                };
            }
        };
    }

    public static <A, B> IO<B> map(final IO<A> io, final F<A, B> f) {
        return () -> f.f(io.run());
    }

    public static <A, B> IO<B> as(final IO<A> io, final B b) {
        return map(io, ignored -> b);
    }

    public static <A> IO<Unit> voided(final IO <A> io) {
        return as(io, Unit.unit());
    }

    public static <A, B> IO<B> bind(final IO<A> io, final F<A, IO<B>> f) {
        return () -> f.f(io.run()).run();
    }

    public static IO<Unit> when(final Boolean b, final IO<Unit> io) {
        return b ? io : ioUnit;
    }

    public static IO<Unit> unless(final Boolean b, final IO<Unit> io) {
        return when(!b, io);
    }

    /**
     * Evaluate each action in the sequence from left to right, and collect the results.
     */
    public static <A> IO<List<A>> sequence(List<IO<A>> list) {
        F2<IO<A>, IO<List<A>>, IO<List<A>>> f2 = (io, ioList) ->
                bind(ioList, (xs) -> map(io, x -> List.cons(x, xs)));
        return list.foldRight(f2, unit(List.nil()));
    }


    public static <A> IO<Stream<A>> sequence(Stream<IO<A>> stream) {
        F2<IO<Stream<A>>, IO<A>, IO<Stream<A>>> f2 = (ioList, io) ->
                bind(ioList, (xs) -> map(io, x -> Stream.cons(x, () -> xs)));
        return stream.foldLeft(f2, unit(Stream.nil()));
    }


    public static <A> IO<A> join(IO<IO<A>> io1) {
        return bind(io1, io2 -> io2);
    }

    public static <A> SafeIO<Validation<IOException, A>> toSafeValidation(IO<A> io) {
        return () -> Try.f(io::run)._1();
    }

    public static <A, B> IO<B> append(final IO<A> io1, final IO<B> io2) {
        return () -> {
            io1.run();
            return io2.run();
        };
    }

    public static <A, B> IO<A> left(final IO<A> io1, final IO<B> io2) {
        return () -> {
            A a = io1.run();
            io2.run();
            return a;
        };
    }

    public static <A, B> IO<B> flatMap(final IO<A> io, final F<A, IO<B>> f) {
        return bind(io, f);
    }

    /**
     * Read lines from stdin until condition is not met, transforming each line and printing
     * the result to stdout.
     * @param condition Read lines until a line does not satisfy condition
     * @param transform Function to change line value
     */
    public static IO<Unit> interactWhile(F<String, Boolean> condition, F<String, String> transform) {
        Stream<IO<String>> s1 = Stream.repeat(stdinReadLine());
        IO<Stream<String>> io = sequenceWhile(s1, condition);
        return () -> runSafe(io).foreach(s -> runSafe(stdoutPrintln(transform.f(s))));
    }

    public static <A> IO<Stream<A>> sequenceWhileEager(final Stream<IO<A>> stream, final F<A, Boolean> f) {
        return () -> {
            boolean loop = true;
            Stream<IO<A>> input = stream;
            Stream<A> result = Stream.nil();
            while (loop) {
                if (input.isEmpty()) {
                    loop = false;
                } else {
                    A a = input.head().run();
                    if (!f.f(a)) {
                        loop = false;
                    } else {
                        input = input.tail()._1();
                        result = result.cons(a);
                    }
                }
            }
            return result.reverse();
        };
    }

    public static <A> IO<Stream<A>> sequenceWhile(final Stream<IO<A>> stream, final F<A, Boolean> f) {
        return () -> {
            if (stream.isEmpty()) {
                return Stream.nil();
            } else {
                IO<A> io = stream.head();
                A a = io.run();
                if (!f.f(a)) {
                    return Stream.nil();
                } else {
                    IO<Stream<A>> io2 = sequenceWhile(stream.tail()._1(), f);
                    SafeIO<Stream<A>> s3 = toSafe(io2::run);
                    return Stream.cons(a, s3::run);
                }
            }
        };
    }

    public static <A, B> IO<B> apply(IO<A> io, IO<F<A, B>> iof) {
        return bind(iof, f -> map(io, f));
    }

    public static <A, B, C> IO<C> liftM2(IO<A> ioa, IO<B> iob, F2<A, B, C> f) {
        return bind(ioa, a -> map(iob, b -> f.f(a, b)));
    }

    public static <A> IO<List<A>> replicateM(IO<A> ioa, int n) {
        return sequence(List.replicate(n, ioa));
    }

    public static <A> IO<State<BufferedReader, Validation<IOException, String>>> readerState() {
        return () -> State.unit((BufferedReader r) -> P.p(r, Try.f((Try1<BufferedReader, String, IOException>) BufferedReader::readLine).f(r)));
    }

    public static final BufferedReader stdinBufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static IO<String> stdinReadLine() {
        return stdinBufferedReader::readLine;
    }

    public static IO<Unit> stdoutPrintln(final String s) {
        return () -> {
            System.out.println(s);
            return Unit.unit();
        };
    }

    public static IO<Unit> stdoutPrint(final String s) {
        return () -> {
            System.out.print(s);
            return Unit.unit();
        };
    }

    public static IO<LazyString> getContents() {
        Stream<IO<Integer>> s = Stream.repeat(() -> stdinBufferedReader.read());
        return map(sequenceWhile(s, i -> i != -1), s2 -> LazyString.fromStream(s2.map(i -> (char) i.intValue())));
    }

    public static IO<Unit> interact(F<LazyString, LazyString> f) {
        return bind(getContents(), ls1 -> {
            LazyString ls2 = f.f(ls1);
            return stdoutPrintln(ls2.toString());
        });
    }

}
