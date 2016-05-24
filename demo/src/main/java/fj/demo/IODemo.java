package fj.demo;

import fj.F;
import fj.data.IOFunctions;
import fj.data.LazyString;

import static fj.F1W.lift;
import static fj.data.IOFunctions.interact;
import static fj.data.IOFunctions.runSafe;
import static fj.data.LazyString.lines_;
import static fj.data.LazyString.unlines_;
import static java.lang.System.out;

/**
 * Created by MarkPerry on 13/06/2015.
 */
public class IODemo {

    public static void main(String[] args) {
        IODemo d = new IODemo();
//        d.readFirstShortLine();
//        d.readFirstLine();
//        d.simpleInteract();
        d.getContents();
    }

    /**
     * Reads from standard input until the line length is less than three
     * and prints that last line.
     */
    public final void readFirstShortLine() {
        F<LazyString, LazyString> f = lift(lines_()).andThen(l -> l.filter(s -> s.length() < 3)).andThen(unlines_());
        runSafe(interact(f));
    }

    /**
     * Read a stream of input lazily using interact, in effect reading the first line
     */
    public final void readFirstLine() {
        F<LazyString, LazyString> f = lift(LazyString::lines).andThen(unlines_());
        runSafe(interact(f));
    }

    /**
     * Demonstrate use of interact, just echoing the lazy string.  Reading lines is done
     * lazily, so just the first line is read.
     */
    public final void simpleInteract() {
        runSafe(interact(s -> s));
    }

    /**
     * Demonstrate that getContents returns a lazy string.
     */
    public final void getContents() {
        out.println(runSafe(IOFunctions.getContents()));
    }

}
