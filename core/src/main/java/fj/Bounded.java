package fj;

public class Bounded<A> {

    public interface Definition<A> {
        A min();

        A max();
    }
}
