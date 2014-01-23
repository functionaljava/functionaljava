package fj;

/**
 * Functions across products.
 * 
 * @version %build.number%
 */
public final class P {
	private P() {
		throw new UnsupportedOperationException();
	}

	/**
	 * A function that puts an element in a product-1.
	 * 
	 * @return A function that puts an element in a product-1.
	 */
	public static <A> F<A, P1<A>> p1() {
		return P::p;
	}

	/**
	 * A function that puts an element in a product-1.
	 * 
	 * @param a
	 *            The element.
	 * @return The product-1.
	 */
	public static <A> P1<A> p(final A a) {
		return () -> a;
	}

	/**
	 * A function that puts an element in a product-2.
	 * 
	 * @return A function that puts an element in a product-2.
	 */
	public static <A, B> F<A, F<B, P2<A, B>>> p2() {
		return a -> b -> p(a, b);
	}

	/**
	 * A function that puts elements in a product-2.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @return The product-2.
	 */
	public static <A, B> P2<A, B> p(final A a, final B b) {
		return new P2<A, B>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}
		};
	}

	/**
	 * A function that puts an element in a product-3.
	 * 
	 * @return A function that puts an element in a product-3.
	 */
	public static <A, B, C> F<A, F<B, F<C, P3<A, B, C>>>> p3() {
		return a -> b -> c -> p(a, b, c);
	}

	/**
	 * A function that puts elements in a product-3.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @return The product-3.
	 */
	public static <A, B, C> P3<A, B, C> p(final A a, final B b, final C c) {
		return new P3<A, B, C>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}
		};
	}

	/**
	 * A function that puts an element in a product-4.
	 * 
	 * @return A function that puts an element in a product-4.
	 */
	public static <A, B, C, D> F<A, F<B, F<C, F<D, P4<A, B, C, D>>>>> p4() {
		return a -> b -> c -> d -> p(a, b, c, d);
	}

	/**
	 * A function that puts elements in a product-4.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @param d
	 *            An element.
	 * @return The product-4.
	 */
	public static <A, B, C, D> P4<A, B, C, D> p(final A a, final B b,
			final C c, final D d) {
		return new P4<A, B, C, D>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}

			public D _4() {
				return d;
			}
		};
	}

	/**
	 * A function that puts an element in a product-5.
	 * 
	 * @return A function that puts an element in a product-5.
	 */
	public static <A, B, C, D, E> F<A, F<B, F<C, F<D, F<E, P5<A, B, C, D, E>>>>>> p5() {
		return a -> b -> c -> d -> e -> p(a, b, c, d, e);
	}

	/**
	 * A function that puts elements in a product-5.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @param d
	 *            An element.
	 * @param e
	 *            An element.
	 * @return The product-5.
	 */
	public static <A, B, C, D, E> P5<A, B, C, D, E> p(final A a, final B b,
			final C c, final D d, final E e) {
		return new P5<A, B, C, D, E>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}

			public D _4() {
				return d;
			}

			public E _5() {
				return e;
			}
		};
	}

	/**
	 * A function that puts an element in a product-6.
	 * 
	 * @return A function that puts an element in a product-6.
	 */
	public static <A, B, C, D, E, F$> F<A, F<B, F<C, F<D, F<E, F<F$, P6<A, B, C, D, E, F$>>>>>>> p6() {
		return a -> b -> c -> d -> e -> f -> p(a, b, c, d, e, f);
	}

	/**
	 * A function that puts elements in a product-6.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @param d
	 *            An element.
	 * @param e
	 *            An element.
	 * @param f
	 *            An element.
	 * @return The product-6.
	 */
	public static <A, B, C, D, E, F$> P6<A, B, C, D, E, F$> p(final A a,
			final B b, final C c, final D d, final E e, final F$ f) {
		return new P6<A, B, C, D, E, F$>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}

			public D _4() {
				return d;
			}

			public E _5() {
				return e;
			}

			public F$ _6() {
				return f;
			}
		};
	}

	/**
	 * A function that puts an element in a product-7.
	 * 
	 * @return A function that puts an element in a product-7.
	 */
	public static <A, B, C, D, E, F$, G> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, P7<A, B, C, D, E, F$, G>>>>>>>> p7() {
		return a -> b -> c -> d -> e -> f -> g -> p(a, b, c, d, e, f, g);
	}

	/**
	 * A function that puts elements in a product-7.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @param d
	 *            An element.
	 * @param e
	 *            An element.
	 * @param f
	 *            An element.
	 * @param g
	 *            An element.
	 * @return The product-7.
	 */
	public static <A, B, C, D, E, F$, G> P7<A, B, C, D, E, F$, G> p(final A a,
			final B b, final C c, final D d, final E e, final F$ f, final G g) {
		return new P7<A, B, C, D, E, F$, G>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}

			public D _4() {
				return d;
			}

			public E _5() {
				return e;
			}

			public F$ _6() {
				return f;
			}

			public G _7() {
				return g;
			}
		};
	}

	/**
	 * A function that puts an element in a product-8.
	 * 
	 * @return A function that puts an element in a product-8.
	 */
	public static <A, B, C, D, E, F$, G, H> F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, P8<A, B, C, D, E, F$, G, H>>>>>>>>> p8() {
		return a -> b -> c -> d -> e -> f -> g -> h -> p(a, b, c, d, e, f, g, h);
	}

	/**
	 * A function that puts elements in a product-8.
	 * 
	 * @param a
	 *            An element.
	 * @param b
	 *            An element.
	 * @param c
	 *            An element.
	 * @param d
	 *            An element.
	 * @param e
	 *            An element.
	 * @param f
	 *            An element.
	 * @param g
	 *            An element.
	 * @param h
	 *            An element.
	 * @return The product-8.
	 */
	public static <A, B, C, D, E, F$, G, H> P8<A, B, C, D, E, F$, G, H> p(
			final A a, final B b, final C c, final D d, final E e, final F$ f,
			final G g, final H h) {
		return new P8<A, B, C, D, E, F$, G, H>() {
			public A _1() {
				return a;
			}

			public B _2() {
				return b;
			}

			public C _3() {
				return c;
			}

			public D _4() {
				return d;
			}

			public E _5() {
				return e;
			}

			public F$ _6() {
				return f;
			}

			public G _7() {
				return g;
			}

			public H _8() {
				return h;
			}
		};
	}
}
