package fj.test;

import fj.F;
import static fj.test.Gen.gen;

import java.util.HashMap;

/**
 * A memoised generator variant. Stores generators that have already been computed for the given arguments.
 *
 * @version %build.number%
 */
public final class Variant {
  private static final HashMap<LongGen, Gen<?>> variantMemo = new HashMap<LongGen, Gen<?>>();

  private static final class LongGen {
    private final long n;
    private final Gen<?> gen;

    LongGen(final long n, final Gen<?> gen) {
      this.n = n;
      this.gen = gen;
    }

    public boolean equals(final Object o) {
      return o != null &&
             o.getClass() == LongGen.class &&
             n == ((LongGen)o).n &&
             gen == ((LongGen)o).gen;
    }

    public int hashCode() {
      final int p = 419;
      int result = 239;
      result = p * result + (int) (n ^ n >>> 32);
      result = p * result + gen.hashCode();
      return result;
    }
  }

  private Variant() {
    throw new UnsupportedOperationException();
  }

  /**
   * Produces a generator that is independent of the given generator using the given value.
   *
   * @param n The value to produce the new generator from.
   * @param g The generator to produce the new generator from.
   * @return A generator that is independent of the given generator using the given value.
   */
  public static <A> Gen<A> variant(final long n, final Gen<A> g) {
    final LongGen p = new LongGen(n, g);
    final Gen<?> gx = variantMemo.get(p);
    if(gx == null) {
      final Gen<A> t = gen(i -> r -> g.gen(i, r.reseed(n)));
      variantMemo.put(p, t);
      return t;
    } else return gen(i -> r -> (A)gx.gen(i, r));
  }

  /**
   * A curried version of {@link #variant(long, Gen)}.
   *
   * @param n The value to produce the new generator from.
   * @return A curried version of {@link #variant(long, Gen)}.
   */
  public static <A> F<Gen<A>, Gen<A>> variant(final long n) {
    return g -> variant(n, g);
  }
}

