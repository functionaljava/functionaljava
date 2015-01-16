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

import fj.*;
import fj.data.Iteratee.Input;
import fj.data.Iteratee.IterV;
import fj.function.Try0;

/**
 * IO monad for processing files, with main methods {@link #enumFileLines },
 * {@link #enumFileChars} and {@link #enumFileCharChunks}
 * (the latter one is the fastest as char chunks read from the file are directly passed to the iteratee
 * without indirection in between).
 *
 * @author Martin Grotzke
 *
 */
public class IOFunctions {
  
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static <A> Try0<A, IOException> toTry(IO<A> io) {
        return () -> io.run();
    }

    public static <A> P1<Validation<IOException, A>> p(IO<A> io) {
        return Try.f(toTry(io));
    }

    public static <A> IO<A> io(P1<A> p) {
        return () -> p._1();
    }

    public static <A> IO<A> io(Try0<A, ? extends IOException> t) {
        return () -> t.f();
    }

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
   * lines, consider to use {@link #enumFileCharChunks} or {@link #enumFileChars}
   * as an alternative.
   * 
   * @param f the file to read, must not be <code>null</code>
   * @param encoding the encoding to use, {@link Option#none()} means platform default
   * @param i the iteratee that is fed with lines read from the file
   */
  public static <A> IO<IterV<String, A>> enumFileLines(final File f, final Option<Charset> encoding, final IterV<String, A> i) {
    return bracket(bufferedReader(f, encoding)
      , Function.<BufferedReader, IO<Unit>>vary(closeReader)
      , partialApply2(IOFunctions.<A>lineReader(), i));
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
      , partialApply2(IOFunctions.<A>charChunkReader(), i));
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
      , partialApply2(IOFunctions.<A>charChunkReader2(), i));
  }

  public static IO<BufferedReader> bufferedReader(final File f, final Option<Charset> encoding) {
    return IOFunctions.map(fileReader(f, encoding), new F<Reader, BufferedReader>() {
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

	public static final <A> IO<A> lazy(final P1<A> p) {
		return () -> p._1();
	}

    public static final <A> IO<A> lazy(final F<Unit, A> f) {
        return () -> f.f(Unit.unit());
    }

    public static final <A> SafeIO<A> lazySafe(final F<Unit, A> f) {
        return () -> f.f(Unit.unit());
    }

    public static final <A> SafeIO<A> lazySafe(final P1<A> f) {
        return () -> f._1();
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
                  final F<F<Input<String>, IterV<String, A>>, P1<IterV<String, A>>> cont = F1Functions.lazy(Function.<Input<String>, IterV<String, A>>apply(input));
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
                      F1Functions.lazy(Function.<Input<char[]>, IterV<char[], A>>apply(input));
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

  public static final <A, B> IO<B> map(final IO<A> io, final F<A, B> f) {
    return new IO<B>() {
      @Override
      public B run() throws IOException {
        return f.f(io.run());
      }
    };
  }

  public static final <A, B> IO<B> bind(final IO<A> io, final F<A, IO<B>> f) {
    return new IO<B>() {
      @Override
      public B run() throws IOException {
        return f.f(io.run()).run();
      }
    };
  }

	/**
	 * Evaluate each action in the sequence from left to right, and collect the results.
	 */
	public static <A> IO<List<A>> sequence(List<IO<A>> list) {
		F2<IO<A>, IO<List<A>>, IO<List<A>>> f2 = (io, ioList) ->
				IOFunctions.bind(ioList, (xs) -> map(io, x -> List.cons(x, xs)));
		return list.foldRight(f2, IOFunctions.unit(List.<A>nil()));
	}


	public static <A> IO<Stream<A>> sequence(Stream<IO<A>> stream) {
		F2<IO<Stream<A>>, IO<A>, IO<Stream<A>>> f2 = (ioList, io) ->
			IOFunctions.bind(ioList, (xs) -> map(io, x -> Stream.cons(x, P.lazy(u -> xs))));
		return stream.foldLeft(f2, IOFunctions.unit(Stream.<A>nil()));
	}


	public static <A> IO<A> join(IO<IO<A>> io1) {
		return bind(io1, io2 -> io2);
	}

	public static <A> SafeIO<Validation<IOException, A>> toSafeIO(IO<A> io) {
		return () -> Try.f(() -> io.run())._1();
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

	static <A> IO<Stream<A>> sequenceWhile(final Stream<IO<A>> stream, final F<A, Boolean> f) {
		return new IO<Stream<A>>() {
			@Override
			public Stream<A> run() throws IOException {
				boolean loop = true;
				Stream<IO<A>> input = stream;
				Stream<A> result = Stream.<A>nil();
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
			}
		};
	}

	public static <A, B> IO<B> apply(IO<A> io, IO<F<A, B>> iof) {
		return bind(iof, f -> map(io, a -> f.f(a)));
	}

	public static <A, B, C> IO<C> liftM2(IO<A> ioa, IO<B> iob, F2<A, B, C> f) {
		return bind(ioa, a -> map(iob, b -> f.f(a, b)));
	}

	public static <A> IO<List<A>> replicateM(IO<A> ioa, int n) {
		return sequence(List.replicate(n, ioa));
	}

	public static <A> IO<State<BufferedReader, Validation<IOException, String>>> readerState() {
		return () -> State.unit((BufferedReader r) -> P.p(r, Try.f((BufferedReader r2) -> r2.readLine()).f(r)));
	}

	public static final BufferedReader stdinBufferedReader = new BufferedReader(new InputStreamReader(System.in));

	public static IO<String> stdinReadLine() {
		return () -> stdinBufferedReader.readLine();
	}

	public static IO<Unit> stdoutPrintln(final String s) {
		return () -> {
			System.out.println(s);
			return Unit.unit();
		};
	}

}
