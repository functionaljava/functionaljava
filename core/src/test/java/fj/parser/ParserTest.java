package fj.parser;

import fj.F;
import fj.F0;
import fj.data.Stream;
import fj.data.Validation;
import org.junit.Test;

import static fj.parser.Result.result;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ParserTest {
    @Test
    public void testParserFail() {
        final Parser<String, String, Exception> fail = Parser.fail(new ParseException());
        assertThat(fail.parse("").fail(), is(new ParseException()));
    }

    @Test
    public void testParserValue() {
        final Parser<String, String, Exception> p = Parser.parser(s -> s.isEmpty() ?
                Validation.fail(new ParseException()) :
                Validation.success(result(s.substring(1), s.substring(0, 1)))
        );
        final Result<String, String> r = p.parse("abc").success();
        assertThat(r.value(), is("a"));
        assertThat(r.rest(), is("bc"));
    }

    @Test
    public void testParserBind() {
        final Parser<String, String, Exception> p = Parser.value("a");
        final Parser<String, String, Exception> fail = Parser.fail(new ParseException());
        assertThat(p.bind(o -> fail).parse("aaaa").fail(), is(new ParseException()));
    }

    @Test
    public void testParserStream() {
        Stream<Character> s = Stream.fromString("abc");
        Result<Stream<Character>, Character> r = Parser.CharsParser.character('a').parse(s).success();
        assertThat(r, is(Result.result(Stream.fromString("bc"), 'a')));
    }

    class ParseException extends Exception {
        @Override
        public boolean equals (Object obj) {
            return (obj instanceof ParseException);
        }
    }

}
