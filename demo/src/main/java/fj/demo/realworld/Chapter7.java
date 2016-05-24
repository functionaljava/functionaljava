package fj.demo.realworld;

import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.LazyString;
import fj.data.Stream;

import static fj.data.IOFunctions.*;

/**
 * Created by MarkPerry on 11/06/2015.
 *
 * Examples from Chapter 7 of Real World Haskell, http://book.realworldhaskell.org/.
 *
 * Currently just ch07/toupper-lazy4.hs.
 */
public class Chapter7 {

    public static void main(String[] args) {
//        toUpperLazy();
//        toUpperByLine();
        toUpperInteract();
    }

    /**
     * Lazy interact to upper, shows the first lazy string line.
     */
    public static void toUpperLazy() {
        runSafe(interact(ls -> {
            Stream<String> stream = ls.lines().map((LazyString ls2) -> ls2.eval().toUpperCase());
            return LazyString.unlines(stream.map(LazyString::str));
        }));
    }

    /**
     * Read each line, convert to uppercase and print on stdout, until an empty line
     */
    public static void toUpperByLine() {
        Stream<IO<String>> s1 = Stream.repeat(stdinReadLine());
        IO<Stream<String>> io = sequenceWhile(s1, s -> s.trim().length() > 0);
        runSafe(io).foreachDoEffect(s -> runSafe(stdoutPrintln(s.toUpperCase())));
    }

    /**
     * Read from stdin each line, whilst each line is not empty, print
     * uppercase line to stdout
     */
    public static void toUpperInteract() {
        runSafe(interactWhile(s -> s.trim().length() > 0, String::toUpperCase));
    }

}
