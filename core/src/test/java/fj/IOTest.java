package fj;

import fj.data.IO;
import fj.data.IOFunctions;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class IOTest {

    @Test
    public void testLift() throws IOException {
        final IO<String> readName = () -> new BufferedReader(new StringReader("foo")).readLine();
        final F<String, IO<String>> upperCaseAndPrint = F1Functions.<String, IO<String>, String>o(this::println).f(String::toUpperCase);
        final IO<String> readAndPrintUpperCasedName = IOFunctions.bind(readName, upperCaseAndPrint);
        assertThat(readAndPrintUpperCasedName.run(), is("FOO"));
    }

    public IO<String> println(final String s) {
        return () -> {
            return s;
        };
    }
}
