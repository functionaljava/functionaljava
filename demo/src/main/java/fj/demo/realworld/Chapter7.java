package fj.demo.realworld;

import fj.data.LazyString;
import fj.data.Stream;

import static fj.data.IOFunctions.*;

/**
 * Created by MarkPerry on 11/06/2015.
 *
 * Examples from Chapter 7 of Real World Haskell, http://book.realworldhaskell.org/.
 */
public class Chapter7 {

    public static void main(String[] args) {
        interactToUpper();
    }

    /**
     * Lazy interact to upper, shows the first lazy string line.
     */
    public static void interactToUpper() {
        runSafe(interact(ls -> {
            Stream<String> stream = ls.lines().map((LazyString ls2) -> ls2.eval().toUpperCase());
            return LazyString.unlines(stream.map(s -> LazyString.str(s)));
        }));
    }

}
