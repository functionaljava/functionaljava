package fj.demo;

import fj.F;
import fj.P2;
import fj.data.Writer;

import static fj.Monoid.stringMonoid;

public class WriterDemo_Halver {

    public static void main(String args[]) {
        testWriter();
    }

    static F<Integer, Writer<String, Integer>> half() {
        return x -> Writer.unit(x / 2, stringMonoid).tell("I just2 halved " + x + "!");
    }

    static void testWriter() {
        Integer init = 32;
        P2<String, Integer> p1 = half().f(init).flatMap(half()).flatMap(half()).run();
        System.out.println(p1);
        System.out.println(half().map(w -> w.flatMap(half()).flatMap(half()).run()).f(init));
    }

}
