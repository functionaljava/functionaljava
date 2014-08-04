package fj.demo;

import fj.P;
import fj.data.State;

/**
 * Created by mperry on 4/08/2014.
 */
public class StateDemo_Greeter {

    public static void main(String args[]) {
        stateDemo();
    }

    static void stateDemo() {
        State<String, String> st1 = State.<String>init().flatMap(s -> State.unit(s2 -> P.p("Batman", "Hello " + s)));
        System.out.println(st1.run("Robin"));
    }

}
