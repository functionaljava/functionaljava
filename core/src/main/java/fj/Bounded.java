package fj;

/**
 * The Bounded class is used to name the upper and lower limits of a type.
 * Ord is not a superclass of Bounded since types that are not totally ordered may also have upper and lower bounds.
 */
public final class Bounded<A> {

    private final Definition<A> def;

    /**
     * Minimal definition of Bounded
     */
    public interface Definition<A> {
        A min();

        A max();
    }

    private Bounded(Definition<A> definition) {
        this.def = definition;
    }

    public A min() {
        return def.min();
    }

    public A max() {
        return def.max();
    }

    public static <A> Bounded<A> boundedDef(Definition<A> def) {
        return new Bounded<>(def);
    }

    public static <A> Bounded<A> bounded(A min, A max) {
        return boundedDef(new Definition<A>() {
            @Override
            public A min() {
                return min;
            }

            @Override
            public A max() {
                return max;
            }
        });
    }

}
