package fj.data;

import fj.Unit;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class IOFunctionsTest {

  @Test
  public void bracket_happy_path() throws Exception {
    AtomicBoolean closed = new AtomicBoolean();
    Reader reader = new StringReader("Read OK") {
      @Override
      public void close() {
        super.close();
        closed.set(true);
      }
    };

    IO<String> bracketed = IOFunctions.bracket(
        () -> reader,
        IOFunctions.closeReader,
        r -> () -> new BufferedReader(r).readLine()
    );

    Assert.assertThat(bracketed.run(), Is.is("Read OK"));
    Assert.assertThat(closed.get(), Is.is(true));
  }

  @Test
  public void bracket_exception_path() throws Exception {
    AtomicBoolean closed = new AtomicBoolean();
    Reader reader = new StringReader("Read OK") {
      @Override
      public void close() {
        super.close();
        closed.set(true);
        throw new IllegalStateException("Should be suppressed");
      }
    };

    IO<String> bracketed = IOFunctions.bracket(
        () -> reader,
        IOFunctions.closeReader,
        r -> () -> {throw new IllegalArgumentException("OoO");}
    );

    try {
      bracketed.run();
      fail("Exception expected");
    } catch (IllegalArgumentException e) {
      Assert.assertThat(e.getMessage(), Is.is("OoO"));
    }
    Assert.assertThat(closed.get(), Is.is(true));
  }

}