package fj.data;

import static fj.Bottom.errorF;
import static fj.Function.constant;
import static fj.Function.partialApply2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;

import fj.F;
import fj.Function;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;
import fj.data.Iteratee.Input;
import fj.data.Iteratee.IterV;

/**
 * IO monad for processing files, with main methods {@link #enumFileLines(File, Option, IterV)},
 * {@link #enumFileChars(File, Option, IterV)} and {@link #enumFileCharChunks(File, Option, IterV)}
 * (the latter one is the fastest as char chunks read from the file are directly passed to the iteratee
 * without indirection in between).
 *
 * @author Martin Grotzke
 *
 * @param <A> the type of the result produced by the wrapped iteratee
 */
public abstract class IO<A> {
  
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

  public static final F<Reader, IO<Unit>> closeReader =
    new F<Reader, IO<Unit>>() {
      @Override
      public IO<Unit> f(final Reader r) {
        return closeReader(r);
      }
    };

  public static IO<Unit> closeReader(final Reader r) {
    return new IO<Unit>() {
      @Override
      public Unit run() throws IOException {
        r.close();
        return Unit.unit();
      }
    };
  }

  /**
   * An IO monad that reads lines from the given file (using a {@link BufferedReader}) and passes
   * lines to the provided iteratee. May not be suitable for files with very long
   * lines, consider to use {@link #enumFileCharChunks(File, IterV)} or {@link #enumFileChars(File, IterV)}
   * as an alternative.
   * 
   * @param f the file to read, must not be <code>null</code>
   * @param encoding the encoding to use, {@link Option#none()} means platform default
   * @param i the iteratee that is fed with lines read from the file
   */
  public static <A> IO<IterV<String, A>> enumFileLines(final File f, final Option<Charset> encoding, final IterV<String, A> i) {
    return bracket(bufferedReader(f, encoding)
      , Function.<BufferedReader, IO<Unit>>vary(closeReader)
      , partialApply2(IO.<A>lineReader(), i));
  }

  /**
   * An IO monad that reads char chunks from the given file and passes them to the given iteratee.
   * 
   * @param f the file to read, must not be <code>null</code>
   * @param encoding the encoding to use, {@link Option#none()} means platform default
   * @param i the iteratee that is fed with char chunks read from the file
   */
  public static <A> IO<IterV<char[], A>> enumFileCharChunks(final File f, final Option<Charset> encoding, final IterV<char[], A> i) {
    return bracket(fileReader(f, encoding)
      , Function.<Reader, IO<Unit>>vary(closeReader)
      , partialApply2(IO.<A>charChunkReader(), i));
  }

  /**
   * An IO monad that reads char chunks from the given file and passes single chars to the given iteratee.
   * 
   * @param f  the file to read, must not be <code>null</code>
   * @param encoding  the encoding to use, {@link Option#none()} means platform default
   * @param i the iteratee that is fed with chars read from the file
   */
  public static <A> IO<IterV<Character, A>> enumFileChars(final File f, final Option<Charset> encoding, final IterV<Character, A> i) {
    return bracket(fileReader(f, encoding)
      , Function.<Reader, IO<Unit>>vary(closeReader)
      , partialApply2(IO.<A>charChunkReader2(), i));
  }

  public static IO<BufferedReader> bufferedReader(final File f, final Option<Charset> encoding) {
    return fileReader(f, encoding).map(new F<Reader, BufferedReader>() {
      @Override
      public BufferedReader f(final Reader a) {
        return new BufferedReader(a);
      }});
  }

  public static IO<Reader> fileReader(final File f, final Option<Charset> encoding) {
    return new IO<Reader>() {
      @Override
      public Reader run() throws IOException {
        final FileInputStream fis = new FileInputStream(f);
        return encoding.isNone() ? new InputStreamReader(fis) : new InputStreamReader(fis, encoding.some());
      }
    };
  }

  public static final <A, B, C> IO<C> bracket(final IO<A> init, final F<A, IO<B>> fin, final F<A, IO<C>> body) {
    return new IO<C>() {
      @Override
      public C run() throws IOException {
        final A a = init.run();
        try {
          return body.f(a).run();
        } catch (final IOException e) {
          throw e;
        } finally {
          fin.f(a);
        }
      }
    };
  }

  public static final <A> IO<A> unit(final A a) {
    return new IO<A>() {
      @Override
      public A run() throws IOException {
        return a;
      }
    };
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

    return new F<BufferedReader, F<IterV<String, A>, IO<IterV<String, A>>>>() {
      @Override
      public F<IterV<String, A>, IO<IterV<String, A>>> f(final BufferedReader r) {
        return new F<IterV<String, A>, IO<IterV<String, A>>>() {
          final F<P2<A, Input<String>>, P1<IterV<String, A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

          @Override
          public IO<IterV<String, A>> f(final IterV<String, A> it) {
            // use loop instead of recursion because of missing TCO
            return new IO<Iteratee.IterV<String, A>>() {
              @Override
              public IterV<String, A> run() throws IOException {
                IterV<String, A> i = it;
                while (!isDone.f(i)) {
                  final String s = r.readLine();
                  if (s == null) { return i; }
                  final Input<String> input = Input.<String>el(s);
                  final F<F<Input<String>, IterV<String, A>>, P1<IterV<String, A>>> cont = Function.<Input<String>, IterV<String, A>>apply(input).lazy();
                  i = i.fold(done, cont)._1();
                }
                return i;
              }
            };
          }
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

    return new F<Reader, F<IterV<char[], A>, IO<IterV<char[], A>>>>() {
      @Override
      public F<IterV<char[], A>, IO<IterV<char[], A>>> f(final Reader r) {
        return new F<IterV<char[], A>, IO<IterV<char[], A>>>() {
          final F<P2<A, Input<char[]>>, P1<IterV<char[], A>>> done = errorF("iteratee is done"); //$NON-NLS-1$

          @Override
          public IO<IterV<char[], A>> f(final IterV<char[], A> it) {
            // use loop instead of recursion because of missing TCO
            return new IO<Iteratee.IterV<char[], A>>() {
              @Override
              public IterV<char[], A> run() throws IOException {
                
                IterV<char[], A> i = it;
                while (!isDone.f(i)) {
                  char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                  final int numRead = r.read(buffer);
                  if (numRead == -1) { return i; }
                  if(numRead < buffer.length) {
                    buffer = Arrays.copyOfRange(buffer, 0, numRead);
                  }
                  final Input<char[]> input = Input.<char[]>el(buffer);
                  final F<F<Input<char[]>, IterV<char[], A>>, P1<IterV<char[], A>>> cont =
                      Function.<Input<char[]>, IterV<char[], A>>apply(input).lazy();
                  i = i.fold(done, cont)._1();
                }
                return i;
              }
            };
          }
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

    return new F<Reader, F<IterV<Character, A>, IO<IterV<Character, A>>>>() {
      @Override
      public F<IterV<Character, A>, IO<IterV<Character, A>>> f(final Reader r) {
        return new F<IterV<Character, A>, IO<IterV<Character, A>>>() {
          final F<P2<A, Input<Character>>, IterV<Character, A>> done = errorF("iteratee is done"); //$NON-NLS-1$

          @Override
          public IO<IterV<Character, A>> f(final IterV<Character, A> it) {
            // use loop instead of recursion because of missing TCO
            return new IO<Iteratee.IterV<Character, A>>() {
              @Override
              public IterV<Character, A> run() throws IOException {
                
                IterV<Character, A> i = it;
                while (!isDone.f(i)) {
                  char[] buffer = new char[DEFAULT_BUFFER_SIZE];
                  final int numRead = r.read(buffer);
                  if (numRead == -1) { return i; }
                  if(numRead < buffer.length) {
                    buffer = Arrays.copyOfRange(buffer, 0, numRead);
                  }
                  for(int c = 0; c < buffer.length; c++) {
                    final Input<Character> input = Input.el(buffer[c]);
                    final F<F<Input<Character>, IterV<Character, A>>, IterV<Character, A>> cont =
                        Function.<Input<Character>, IterV<Character, A>>apply(input);
                    i = i.fold(done, cont);
                  }
                }
                return i;
              }
            };
          }
        };
      }
    };
  }

  public abstract A run() throws IOException;

  public final <B> IO<B> map(final F<A, B> f) {
    return new IO<B>() {
      @Override
      public B run() throws IOException {
        return f.f(IO.this.run());
      }
    };
  }

  public final <B> IO<B> bind(final F<A, IO<B>> f) {
    return new IO<B>() {
      @Override
      public B run() throws IOException {
        return f.f(IO.this.run()).run();
      }
    };
  }
}
