package fj.data;

import fj.F;
import fj.P1;
import fj.Unit;
import fj.function.TryEffect0;
import fj.function.Effect0;
import fj.function.Effect1;

import fj.Try;
import fj.TryEffect;
import fj.Effect;
import fj.function.Try0;
import fj.function.Try1;

import java.io.IOException;
import static fj.Unit.unit;
import static fj.data.List.asString;
import static fj.data.List.fromString;

/**
 * Functions that convert between data structure types.
 *
 * @version %build.number%
 */
public final class Conversions {
  private Conversions() {
    throw new UnsupportedOperationException();
  }

  // BEGIN List ->

  /**
   * A function that converts lists to arrays.
   *
   * @return A function that converts lists to arrays.
   */
  public static <A> F<List<A>, Array<A>> List_Array() {
    return List::toArray;
  }

  /**
   * A function that converts lists to streams.
   *
   * @return A function that converts lists to streams.
   */
  public static <A> F<List<A>, Stream<A>> List_Stream() {
    return List::toStream;
  }

  /**
   * A function that converts lists to options.
   *
   * @return A function that converts lists to options.
   */
  public static <A> F<List<A>, Option<A>> List_Option() {
    return List::headOption;
  }

  /**
   * A function that converts lists to eithers.
   *
   * @return A function that converts lists to eithers.
   */
  public static <A, B> F<P1<A>, F<List<B>, Either<A, B>>> List_Either() {
    return a -> bs -> bs.toEither(a);
  }

  /**
   * A function that converts lists to strings.
   */
  public static final F<List<Character>, String> List_String = List::asString;

  /**
   * A function that converts lists to string buffers.
   */
  public static final F<List<Character>, StringBuffer> List_StringBuffer = cs -> new StringBuffer(asString(cs));

  /**
   * A function that converts lists to string builders.
   */
  public static final F<List<Character>, StringBuilder> List_StringBuilder = cs -> new StringBuilder(asString(cs));

  // END List ->

  // BEGIN Array ->

  /**
   * A function that converts arrays to lists.
   *
   * @return A function that converts arrays to lists.
   */
  public static <A> F<Array<A>, List<A>> Array_List() {
    return Array::toList;
  }

  /**
   * A function that converts arrays to streams.
   *
   * @return A function that converts arrays to streams.
   */
  public static <A> F<Array<A>, Stream<A>> Array_Stream() {
    return Array::toStream;
  }

  /**
   * A function that converts arrays to options.
   *
   * @return A function that converts arrays to options.
   */
  public static <A> F<Array<A>, Option<A>> Array_Option() {
    return Array::toOption;
  }

  /**
   * A function that converts arrays to eithers.
   *
   * @return A function that converts arrays to eithers.
   */
  public static <A, B> F<P1<A>, F<Array<B>, Either<A, B>>> Array_Either() {
    return a -> bs -> bs.toEither(a);
  }

  /**
   * A function that converts arrays to strings.
   */
  public static final F<Array<Character>, String> Array_String = cs -> {
      final StringBuilder sb = new StringBuilder(cs.length());
      cs.foreachDoEffect(sb::append);
      return sb.toString();
  };

  /**
   * A function that converts arrays to string buffers.
   */
  public static final F<Array<Character>, StringBuffer> Array_StringBuffer = cs -> {
      final StringBuffer sb = new StringBuffer(cs.length());
      cs.foreachDoEffect(sb::append);
      return sb;
  };

  /**
   * A function that converts arrays to string builders.
   */
  public static final F<Array<Character>, StringBuilder> Array_StringBuilder = cs -> {
      final StringBuilder sb = new StringBuilder(cs.length());
      cs.foreachDoEffect(sb::append);
      return sb;
  };

  // END Array ->

  // BEGIN Stream ->

  /**
   * A function that converts streams to lists.
   *
   * @return A function that converts streams to lists.
   */
  public static <A> F<Stream<A>, List<A>> Stream_List() {
    return Stream::toList;
  }

  /**
   * A function that converts streams to arrays.
   *
   * @return A function that converts streams to arrays.
   */
  public static <A> F<Stream<A>, Array<A>> Stream_Array() {
    return Stream::toArray;
  }

  /**
   * A function that converts streams to options.
   *
   * @return A function that converts streams to options.
   */
  public static <A> F<Stream<A>, Option<A>> Stream_Option() {
    return Stream::toOption;
  }

  /**
   * A function that converts streams to eithers.
   *
   * @return A function that converts streams to eithers.
   */
  public static <A, B> F<P1<A>, F<Stream<B>, Either<A, B>>> Stream_Either() {
    return a -> bs -> bs.toEither(a);
  }

  /**
   * A function that converts streams to strings.
   */
  public static final F<Stream<Character>, String> Stream_String = cs -> {
      final StringBuilder sb = new StringBuilder();
      cs.foreachDoEffect(sb::append);
      return sb.toString();
    };

  /**
   * A function that converts streams to string buffers.
   */
  public static final F<Stream<Character>, StringBuffer> Stream_StringBuffer = cs -> {
      final StringBuffer sb = new StringBuffer();
      cs.foreachDoEffect(sb::append);
      return sb;
  };

  /**
   * A function that converts streams to string builders.
   */
  public static final F<Stream<Character>, StringBuilder> Stream_StringBuilder = cs -> {
      final StringBuilder sb = new StringBuilder();
      cs.foreachDoEffect(sb::append);
      return sb;
  };

  // END Stream ->

  // BEGIN Option ->

  /**
   * A function that converts options to lists.
   *
   * @return A function that converts options to lists.
   */
  public static <A> F<Option<A>, List<A>> Option_List() {
    return Option::toList;
  }

  /**
   * A function that converts options to arrays.
   *
   * @return A function that converts options to arrays.
   */
  public static <A> F<Option<A>, Array<A>> Option_Array() {
    return Option::toArray;
  }

  /**
   * A function that converts options to streams.
   *
   * @return A function that converts options to streams.
   */
  public static <A> F<Option<A>, Stream<A>> Option_Stream() {
    return Option::toStream;
  }

  /**
   * A function that converts options to eithers.
   *
   * @return A function that converts options to eithers.
   */
  public static <A, B> F<P1<A>, F<Option<B>, Either<A, B>>> Option_Either() {
    return a -> o -> o.toEither(a);
  }

  /**
   * A function that converts options to strings.
   */
  public static final F<Option<Character>, String> Option_String = o -> asString(o.toList());

  /**
   * A function that converts options to string buffers.
   */
  public static final F<Option<Character>, StringBuffer> Option_StringBuffer = o -> new StringBuffer(asString(o.toList()));

  /**
   * A function that converts options to string builders.
   */
  public static final F<Option<Character>, StringBuilder> Option_StringBuilder = o -> new StringBuilder(asString(o.toList()));

  // END Option ->

    // BEGIN Effect

    public static F<Effect0, P1<Unit>> Effect0_P1() {
        return Conversions::Effect0_P1;
    }

    public static P1<Unit> Effect0_P1(Effect0 e) {
        return Effect.f(e);
    }

    public static <A> F<A, Unit> Effect1_F(Effect1<A> e) {
        return Effect.f(e);
    }

    public static <A> F<Effect1<A>, F<A, Unit>> Effect1_F() {
        return Conversions::Effect1_F;
    }

    public static IO<Unit> Effect_IO(Effect0 e) {
        return () ->{
            e.f();
            return unit();
        };
    }

    public static F<Effect0, IO<Unit>> Effect_IO() {
        return Conversions::Effect_IO;
    }

    public static SafeIO<Unit> Effect_SafeIO(Effect0 e) {
        return () -> {
            e.f();
            return unit();
        };
    }

    public static F<Effect0, SafeIO<Unit>> Effect_SafeIO() {
        return Conversions::Effect_SafeIO;
    }

    // END Effect

  // BEGIN Either ->

  /**
   * A function that converts eithers to lists.
   *
   * @return A function that converts eithers to lists.
   */
  public static <A, B> F<Either<A, B>, List<A>> Either_ListA() {
    return e -> e.left().toList();
  }

  /**
   * A function that converts eithers to lists.
   *
   * @return A function that converts eithers to lists.
   */
  public static <A, B> F<Either<A, B>, List<B>> Either_ListB() {
    return e -> e.right().toList();
  }

  /**
   * A function that converts eithers to arrays.
   *
   * @return A function that converts eithers to arrays.
   */
  public static <A, B> F<Either<A, B>, Array<A>> Either_ArrayA() {
    return e -> e.left().toArray();
  }

  /**
   * A function that converts eithers to arrays.
   *
   * @return A function that converts eithers to arrays.
   */
  public static <A, B> F<Either<A, B>, Array<B>> Either_ArrayB() {
    return e -> e.right().toArray();
  }

  /**
   * A function that converts eithers to streams.
   *
   * @return A function that converts eithers to streams.
   */
  public static <A, B> F<Either<A, B>, Stream<A>> Either_StreamA() {
    return e -> e.left().toStream();
  }

  /**
   * A function that converts eithers to streams.
   *
   * @return A function that converts eithers to streams.
   */
  public static <A, B> F<Either<A, B>, Stream<B>> Either_StreamB() {
    return e -> e.right().toStream();
  }

  /**
   * A function that converts eithers to options.
   *
   * @return A function that converts eithers to options.
   */
  public static <A, B> F<Either<A, B>, Option<A>> Either_OptionA() {
    return e -> e.left().toOption();
  }

  /**
   * A function that converts eithers to options.
   *
   * @return A function that converts eithers to options.
   */
  public static <A, B> F<Either<A, B>, Option<B>> Either_OptionB() {
    return e -> e.right().toOption();
  }

  /**
   * A function that converts eithers to strings.
   *
   * @return A function that converts eithers to strings.
   */
  public static <B> F<Either<Character, B>, String> Either_StringA() {
    return e -> asString(e.left().toList());
  }

  /**
   * A function that converts eithers to strings.
   *
   * @return A function that converts eithers to strings.
   */
  public static <A> F<Either<A, Character>, String> Either_StringB() {
    return e -> asString(e.right().toList());
  }

  /**
   * A function that converts eithers to string buffers.
   *
   * @return A function that converts eithers to string buffers.
   */
  public static <B> F<Either<Character, B>, StringBuffer> Either_StringBufferA() {
    return e -> new StringBuffer(asString(e.left().toList()));
  }

  /**
   * A function that converts eithers to string buffers.
   *
   * @return A function that converts eithers to string buffers.
   */
  public static <A> F<Either<A, Character>, StringBuffer> Either_StringBufferB() {
    return e -> new StringBuffer(asString(e.right().toList()));
  }

  /**
   * A function that converts eithers to string builders.
   *
   * @return A function that converts eithers to string builders.
   */
  public static <B> F<Either<Character, B>, StringBuilder> Either_StringBuilderA() {
    return e -> new StringBuilder(asString(e.left().toList()));
  }

  /**
   * A function that converts eithers to string builders.
   *
   * @return A function that converts eithers to string builders.
   */
  public static <A> F<Either<A, Character>, StringBuilder> Either_StringBuilderB() {
    return e -> new StringBuilder(asString(e.right().toList()));
  }

  // END Either ->

    // BEGIN F

    public static <A> SafeIO<A> F_SafeIO(F<Unit, A> f) {
        return () -> f.f(unit());
    }

    public static <A> F<F<Unit, A>, SafeIO<A>> F_SafeIO() {
        return Conversions::F_SafeIO;
    }

    // END F

  // BEGIN String ->

  /**
   * A function that converts strings to lists.
   */
  public static final F<String, List<Character>> String_List = List::fromString;

  /**
   * A function that converts strings to arrays.
   */
  public static final F<String, Array<Character>> String_Array = s -> fromString(s).toArray();

  /**
   * A function that converts strings to options.
   */
  public static final F<String, Option<Character>> String_Option = s -> fromString(s).headOption();

  /**
   * A function that converts string to eithers.
   *
   * @return A function that converts string to eithers.
   */
  public static <A> F<P1<A>, F<String, Either<A, Character>>> String_Either() {
    return a -> s -> fromString(s).toEither(a);
  }

  /**
   * A function that converts strings to streams.
   */
  public static final F<String, Stream<Character>> String_Stream = s -> fromString(s).toStream();

  /**
   * A function that converts strings to string buffers.
   */
  public static final F<String, StringBuffer> String_StringBuffer = StringBuffer::new;

  /**
   * A function that converts strings to string builders.
   */
  public static final F<String, StringBuilder> String_StringBuilder = StringBuilder::new;

  // END String ->

  // BEGIN StringBuffer ->

  /**
   * A function that converts string buffers to lists.
   */
  public static final F<StringBuffer, List<Character>> StringBuffer_List = s -> fromString(s.toString());

  /**
   * A function that converts string buffers to arrays.
   */
  public static final F<StringBuffer, Array<Character>> StringBuffer_Array = s -> fromString(s.toString()).toArray();

  /**
   * A function that converts string buffers to streams.
   */
  public static final F<StringBuffer, Stream<Character>> StringBuffer_Stream = s -> fromString(s.toString()).toStream();

  /**
   * A function that converts string buffers to options.
   */
  public static final F<StringBuffer, Option<Character>> StringBuffer_Option = s -> fromString(s.toString()).headOption();

  /**
   * A function that converts string buffers to eithers.
   *
   * @return A function that converts string buffers to eithers.
   */
  public static <A> F<P1<A>, F<StringBuffer, Either<A, Character>>> StringBuffer_Either() {
    return a -> s -> fromString(s.toString()).toEither(a);
  }

  /**
   * A function that converts string buffers to strings.
   */
  public static final F<StringBuffer, String> StringBuffer_String = StringBuffer::toString;

  /**
   * A function that converts string buffers to string builders.
   */
  public static final F<StringBuffer, StringBuilder> StringBuffer_StringBuilder = StringBuilder::new;

  // END StringBuffer ->

  // BEGIN StringBuilder ->

  /**
   * A function that converts string builders to lists.
   */
  public static final F<StringBuilder, List<Character>> StringBuilder_List = s -> fromString(s.toString());

  /**
   * A function that converts string builders to arrays.
   */
  public static final F<StringBuilder, Array<Character>> StringBuilder_Array = s -> fromString(s.toString()).toArray();

  /**
   * A function that converts string builders to streams.
   */
  public static final F<StringBuilder, Stream<Character>> StringBuilder_Stream = s -> fromString(s.toString()).toStream();

  /**
   * A function that converts string builders to options.
   */
  public static final F<StringBuilder, Option<Character>> StringBuilder_Option = s -> fromString(s.toString()).headOption();

  /**
   * A function that converts string builders to eithers.
   *
   * @return A function that converts string builders to eithers.
   */
  public static <A> F<P1<A>, F<StringBuilder, Either<A, Character>>> StringBuilder_Either() {
    return a -> s -> fromString(s.toString()).toEither(a);
  }

  /**
   * A function that converts string builders to strings.
   */
  public static final F<StringBuilder, String> StringBuilder_String = StringBuilder::toString;

  /**
   * A function that converts string builders to string buffers.
   */
  public static final F<StringBuilder, StringBuffer> StringBuilder_StringBuffer = StringBuffer::new;

  // END StringBuilder ->


    // BEGIN Try

    public static <A, B, Z extends Exception> SafeIO<Validation<Z, A>> Try_SafeIO(Try0<A, Z> t) {
        return F_SafeIO(u -> Try.f(t)._1());
    }

    public static <A, B, Z extends Exception> F<Try0<A, Z>, SafeIO<Validation<Z, A>>> Try_SafeIO() {
        return Conversions::Try_SafeIO;
    }

    public static <A, B, Z extends IOException> IO<A> Try_IO(Try0<A, Z> t) {
        return t::f;
    }

    public static <A, B, Z extends IOException> F<Try0<A, Z>, IO<A>> Try_IO() {
        return Conversions::Try_IO;
    }

    public static <A, B, Z extends IOException> F<A, Validation<Z, B>> Try_F(Try1<A, B, Z> t) {
        return Try.f(t);
    }

    public static <A, B, Z extends IOException> F<Try1<A, B, Z>, F<A, Validation<Z, B>>> Try_F() {
        return Conversions::Try_F;
    }

    // END Try

    // BEGIN TryEffect

    public static <E extends Exception> P1<Validation<E, Unit>> TryEffect_P(final TryEffect0<E> t) {
        return TryEffect.f(t);
    }


    // END TryEffect

}
