package fj;

import static fj.Function.compose;

import fj.data.*;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;
import fj.data.vector.V5;
import fj.data.vector.V6;
import fj.data.vector.V7;
import fj.data.vector.V8;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Produces a hash code for an object which should attempt uniqueness.
 *
 * @version %build.number%
 */
public final class Hash<A> {
  private final F<A, Integer> f;

  private Hash(final F<A, Integer> f) {
    this.f = f;
  }

  /**
   * Compute the hash of the given value.
   *
   * @param a The value to compute the hash value for.
   * @return The hash value.
   */
  public int hash(final A a) {
    return f.f(a);
  }

  /**
   * Maps the given function across this hash as a contra-variant functor.
   *
   * @param g The function to map.
   * @return A new hash.
   */
  public <B> Hash<B> contramap(final F<B, A> g) {
    return hash(compose(f, g));
  }

  /**
   * Construct a hash with the given hash function.
   *
   * @param f The function to construct the hash with.
   * @return A hash that uses the given function.
   */
  public static <A> Hash<A> hash(final F<A, Integer> f) {
    return new Hash<>(f);
  }

  /**
   * A hash that uses {@link Object#hashCode()}.
   *
   * @return A hash that uses {@link Object#hashCode()}.
   */
  public static <A> Hash<A> anyHash() {
    return hash(Object::hashCode);
  }

  /**
   * A hash instance for the <code>boolean</code> type.
   */
  public static final Hash<Boolean> booleanHash = anyHash();

  /**
   * A hash instance for the <code>byte</code> type.
   */
  public static final Hash<Byte> byteHash = anyHash();

  /**
   * A hash instance for the <code>char</code> type.
   */
  public static final Hash<Character> charHash = anyHash();

  /**
   * A hash instance for the <code>double</code> type.
   */
  public static final Hash<Double> doubleHash = anyHash();

  /**
   * A hash instance for the <code>float</code> type.
   */
  public static final Hash<Float> floatHash = anyHash();

  /**
   * A hash instance for the <code>int</code> type.
   */
  public static final Hash<Integer> intHash = anyHash();

  /**
   * A hash instance for the <code>long</code> type.
   */
  public static final Hash<Long> longHash = anyHash();

  /**
   * A hash instance for the <code>short</code> type.
   */
  public static final Hash<Short> shortHash = anyHash();

  /**
   * A hash instance for the <code>BigInteger</code> type.
   */
  public static final Hash<BigInteger> bigintHash = anyHash();

  /**
   * A hash instance for the <code>BigDecimal</code> type.
   */
  public static final Hash<BigDecimal> bigdecimalHash = anyHash();

  /**
   * A hash instance for the <code>String</code> type.
   */
  public static final Hash<String> stringHash = anyHash();

  /**
   * A hash instance for the {@link StringBuffer} type.
   */
  public static final Hash<StringBuffer> stringBufferHash = hash(sb -> {
      final int p = 419;
      int r = 239;

      for (int i = 0; i < sb.length(); i++)
          r = p * r + sb.charAt(i);

      return r;
  });

  /**
   * A hash instance for the {@link StringBuilder} type.
   */
  public static final Hash<StringBuilder> stringBuilderHash = hash(sb -> {
      final int p = 419;
      int r = 239;

      for (int i = 0; i < sb.length(); i++)
          r = p * r + sb.charAt(i);

      return r;
  });

  /**
   * A hash instance for the {@link Either} type.
   *
   * @param ha Hash the left side of <code>Either</code>.
   * @param hb Hash the right side of <code>Either</code>.
   * @return A hash instance for the {@link Either} type.
   */
  public static <A, B> Hash<Either<A, B>> eitherHash(final Hash<A> ha, final Hash<B> hb) {
    return hash(e -> e.isLeft() ? ha.hash(e.left().value()) : hb.hash(e.right().value()));
  }

  /**
   * A hash instance for the {@link Validation} type.
   *
   * @param ha Hash the failing side of <code>Validation</code>.
   * @param hb Hash the succeeding side of <code>Validation</code>.
   * @return A hash instance for the {@link Validation} type.
   */
  public static <A, B> Hash<Validation<A, B>> validationHash(final Hash<A> ha, final Hash<B> hb) {
    return eitherHash(ha, hb).contramap(Validation.either());
  }

  /**
   * A hash instance for the {@link List} type.
   *
   * @param ha A hash for the elements of the list.
   * @return A hash instance for the {@link List} type.
   */
  public static <A> Hash<List<A>> listHash(final Hash<A> ha) {
    return hash(as -> {
        final int p = 419;
        int r = 239;
        List<A> aas = as;

        while (!aas.isEmpty()) {
            r = p * r + ha.hash(aas.head());
            aas = aas.tail();
        }

        return r;
    });
  }

  /**
   * A hash instance for the {@link NonEmptyList} type.
   *
   * @param ha A hash for the elements of the non-empty list.
   * @return A hash instance for the {@link NonEmptyList} type.
   */
  public static <A> Hash<NonEmptyList<A>> nonEmptyListHash(final Hash<A> ha) {
    return listHash(ha).contramap(NonEmptyList.toList_());
  }

  /**
   * A hash instance for the {@link Option} type.
   *
   * @param ha A hash for the element of the optional value.
   * @return A hash instance for the {@link Option} type.
   */
  public static <A> Hash<Option<A>> optionHash(final Hash<A> ha) {
    return hash(o -> o.isNone() ? 0 : ha.hash(o.some()));
  }

    public static <A> Hash<Seq<A>> seqHash(final Hash<A> h) {
        return hash(s -> streamHash(h).hash(s.toStream()));
    }

    public static <A> Hash<Set<A>> setHash(final Hash<A> h) {
        return hash(s -> streamHash(h).hash(s.toStream()));
    }

  /**
   * A hash instance for the {@link Stream} type.
   *
   * @param ha A hash for the elements of the stream.
   * @return A hash instance for the {@link Stream} type.
   */
  public static <A> Hash<Stream<A>> streamHash(final Hash<A> ha) {
    return hash(as -> {
        final int p = 419;
        int r = 239;
        Stream<A> aas = as;

        while (!aas.isEmpty()) {
            r = p * r + ha.hash(aas.head());
            aas = aas.tail()._1();
        }

        return r;
    });
  }

  /**
   * A hash instance for the {@link Array} type.
   *
   * @param ha A hash for the elements of the array.
   * @return A hash instance for the {@link Array} type.
   */
  public static <A> Hash<Array<A>> arrayHash(final Hash<A> ha) {
    return hash(as -> {
        final int p = 419;
        int r = 239;

        for (int i = 0; i < as.length(); i++) {
            r = p * r + ha.hash(as.get(i));
        }

        return r;
    });
  }

  /**
   * A hash instance for the {@link Tree} type.
   *
   * @param ha A hash for the elements of the tree.
   * @return A hash instance for the {@link Tree} type.
   */
  public static <A> Hash<Tree<A>> treeHash(final Hash<A> ha) {
    return streamHash(ha).contramap(Tree.flatten_());
  }

    public static <K, V> Hash<TreeMap<K, V>> treeMapHash(final Hash<K> h, final Hash<V> v) {
        return hash(t -> streamHash(p2Hash(h, v)).hash(t.toStream()));
    }

  /**
   * A hash instance for a product-1.
   *
   * @param ha A hash for the first element of the product.
   * @return A hash instance for a product-1.
   */
  public static <A> Hash<P1<A>> p1Hash(final Hash<A> ha) {
    return ha.contramap(P1.__1());
  }

  /**
   * A hash instance for a product-2.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @return A hash instance for a product-2.
   */
  public static <A, B> Hash<P2<A, B>> p2Hash(final Hash<A> ha, final Hash<B> hb) {
    return hash(p2 -> {
        final int p = 419;
        int r = 239;

        r = p * r + ha.hash(p2._1());
        r = p * r + hb.hash(p2._2());

        return r;
    });
  }

  /**
   * A hash instance for a product-3.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @return A hash instance for a product-3.
   */
  public static <A, B, C> Hash<P3<A, B, C>> p3Hash(final Hash<A> ha, final Hash<B> hb, final Hash<C> hc) {
    return hash(p3 -> {
        final int p = 419;
        int r = 239;

        r = p * r + ha.hash(p3._1());
        r = p * r + hb.hash(p3._2());
        r = p * r + hc.hash(p3._3());

        return r;
    });
  }

  /**
   * A hash instance for a product-4.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @param hd A hash for the fourth element of the product.
   * @return A hash instance for a product-4.
   */
  public static <A, B, C, D> Hash<P4<A, B, C, D>> p4Hash(final Hash<A> ha, final Hash<B> hb, final Hash<C> hc,
                                                         final Hash<D> hd) {
    return hash(p4 -> {
      final int p = 419;
      int r = 239;

      r = p * r + ha.hash(p4._1());
      r = p * r + hb.hash(p4._2());
      r = p * r + hc.hash(p4._3());
      r = p * r + hd.hash(p4._4());

      return r;
    });
  }

  /**
   * A hash instance for a product-5.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @param hd A hash for the fourth element of the product.
   * @param he A hash for the fifth element of the product.
   * @return A hash instance for a product-5.
   */
  public static <A, B, C, D, E> Hash<P5<A, B, C, D, E>> p5Hash(final Hash<A> ha, final Hash<B> hb, final Hash<C> hc,
                                                               final Hash<D> hd, final Hash<E> he) {
    return hash(p5 -> {
      final int p = 419;
      int r = 239;

      r = p * r + ha.hash(p5._1());
      r = p * r + hb.hash(p5._2());
      r = p * r + hc.hash(p5._3());
      r = p * r + hd.hash(p5._4());
      r = p * r + he.hash(p5._5());

      return r;
    });
  }

  /**
   * A hash instance for a product-6.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @param hd A hash for the fourth element of the product.
   * @param he A hash for the fifth element of the product.
   * @param hf A hash for the sixth element of the product.
   * @return A hash instance for a product-6.
   */
  public static <A, B, C, D, E, F$> Hash<P6<A, B, C, D, E, F$>> p6Hash(final Hash<A> ha, final Hash<B> hb,
                                                                       final Hash<C> hc, final Hash<D> hd,
                                                                       final Hash<E> he, final Hash<F$> hf) {
    return hash(p6 -> {
      final int p = 419;
      int r = 239;

      r = p * r + ha.hash(p6._1());
      r = p * r + hb.hash(p6._2());
      r = p * r + hc.hash(p6._3());
      r = p * r + hd.hash(p6._4());
      r = p * r + he.hash(p6._5());
      r = p * r + hf.hash(p6._6());

      return r;
    });
  }

  /**
   * A hash instance for a product-7.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @param hd A hash for the fourth element of the product.
   * @param he A hash for the fifth element of the product.
   * @param hf A hash for the sixth element of the product.
   * @param hg A hash for the seventh element of the product.
   * @return A hash instance for a product-7.
   */
  public static <A, B, C, D, E, F$, G> Hash<P7<A, B, C, D, E, F$, G>> p7Hash(final Hash<A> ha, final Hash<B> hb,
                                                                             final Hash<C> hc, final Hash<D> hd,
                                                                             final Hash<E> he, final Hash<F$> hf,
                                                                             final Hash<G> hg) {
    return hash(p7 -> {
      final int p = 419;
      int r = 239;

      r = p * r + ha.hash(p7._1());
      r = p * r + hb.hash(p7._2());
      r = p * r + hc.hash(p7._3());
      r = p * r + hd.hash(p7._4());
      r = p * r + he.hash(p7._5());
      r = p * r + hf.hash(p7._6());
      r = p * r + hg.hash(p7._7());

      return r;
    });
  }

  /**
   * A hash instance for a product-8.
   *
   * @param ha A hash for the first element of the product.
   * @param hb A hash for the second element of the product.
   * @param hc A hash for the third element of the product.
   * @param hd A hash for the fourth element of the product.
   * @param he A hash for the fifth element of the product.
   * @param hf A hash for the sixth element of the product.
   * @param hg A hash for the seventh element of the product.
   * @param hh A hash for the eighth element of the product.
   * @return A hash instance for a product-8.
   */
  public static <A, B, C, D, E, F$, G, H> Hash<P8<A, B, C, D, E, F$, G, H>> p8Hash(final Hash<A> ha, final Hash<B> hb,
                                                                                   final Hash<C> hc, final Hash<D> hd,
                                                                                   final Hash<E> he, final Hash<F$> hf,
                                                                                   final Hash<G> hg, final Hash<H> hh) {
    return hash(p8 -> {
      final int p = 419;
      int r = 239;

      r = p * r + ha.hash(p8._1());
      r = p * r + hb.hash(p8._2());
      r = p * r + hc.hash(p8._3());
      r = p * r + hd.hash(p8._4());
      r = p * r + he.hash(p8._5());
      r = p * r + hf.hash(p8._6());
      r = p * r + hg.hash(p8._7());
      r = p * r + hh.hash(p8._8());

      return r;
    });
  }

  /**
   * A hash instance for a vector-2.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-2.
   */
  public static <A> Hash<V2<A>> v2Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V2.toStream_());
  }

  /**
   * A hash instance for a vector-3.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-3.
   */
  public static <A> Hash<V3<A>> v3Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V3.toStream_());
  }

  /**
   * A hash instance for a vector-4.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-4.
   */
  public static <A> Hash<V4<A>> v4Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V4.toStream_());
  }

  /**
   * A hash instance for a vector-5.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-5.
   */
  public static <A> Hash<V5<A>> v5Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V5.toStream_());
  }

  /**
   * A hash instance for a vector-6.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-6.
   */
  public static <A> Hash<V6<A>> v6Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V6.toStream_());
  }

  /**
   * A hash instance for a vector-7.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-7.
   */
  public static <A> Hash<V7<A>> v7Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V7.toStream_());
  }

  /**
   * A hash instance for a vector-8.
   *
   * @param ea A hash for the elements of the vector.
   * @return A hash instance for a vector-8.
   */
  public static <A> Hash<V8<A>> v8Hash(final Hash<A> ea) {
    return streamHash(ea).contramap(V8.toStream_());
  }
}
