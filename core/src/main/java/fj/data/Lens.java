package fj.data;

import fj.F;
import fj.F2;
import fj.Function;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Unit;

/**
 * A Lens offers a purely function means to access and retrieve a field in a
 * record.
 *
 * The term {@code field} does not necessarily mean an actual field or member
 * of a class. For example, a lens can address membership of a {@code Set}.
 *
 * @param <A> The type of the record
 * @param <B> The type of the field
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class Lens<A, B> {

  /**
   * The getter of the lens.
   *
   * @param a a record
   * @return the value of the field from the record
   */
  public abstract B get(final A a);

  /**
   * The setter of the lens.
   *
   * @param a a record
   * @param b the new value for the field
   * @return the updated record with the field set to the new value
   */
  public abstract A set(final A a, final B b);

  /**
   * Map over the record type of the lens.
   *
   * @param f a function from old record to new record
   * @param g a function from new record to old record
   * @param <X> the type of the new record
   * @return a new Lens with a different record type
   */
  public final <X> Lens<X, B> xmapA(final F<A, X> f, final F<X, A> g) {
    return Lens.lens(x -> get(g.f(x)), (x, b) -> f.f(set(g.f(x), b)));
  }

  /**
   * Map over the field type of the lens.
   *
   * @param <X> the type of the new record
   * @param f a function from old field to new field
   * @param g a function from new field to old field
   * @return a new Lens with a different field type
   */
  public final <X> Lens<A, X> xmapB(final F<B, X> f, final F<X, B> g) {
    return Lens.lens(a -> f.f(get(a)), (a, x) -> set(a, g.f(x)));
  }

  /**
   * @return the getter as a {@code State}.
   */
  public final State<A, B> st() {
    return State.unit(a -> P.p(a, get(a)));
  }

  /**
   * Modify the value viewed through the lens.
   *
   * @param f a function that modifies the field value
   * @param a the record
   * @return the updated record with new field value modified
   */
  public final A mod(final F<B, B> f, final A a) {
    return set(a, f.f(get(a)));
  }

  /**
   * Curried version of {@link #mod(fj.F, A)}.
   *
   * @param f a function that modifies the field value
   * @return a function that modifies the record value
   */
  public final F<A, A> mod(final F<B, B> f) {
    return a -> mod(f, a);
  }

  /**
   * Version of {@link #mod(fj.F, A)}, where the modification returns a
   * {@link fj.data.List}.
   *
   * @param f a function that produces a list for the field value
   * @param a the record
   * @return a list of modified records
   */
  public final List<A> modList(final F<B, List<B>> f, final A a) {
    return f.f(get(a)).map(res -> set(a, res));
  }

  /**
   * Curried version of {@link #modList(fj.F, A)}.
   *
   * @param f a function that produces a list for the field value
   * @return a function that returns a list of modified records
   */
  public final F<A, List<A>> modList(final F<B, List<B>> f) {
    return a -> modList(f, a);
  }

  /**
   * Version of {@link #mod(fj.F, A)}, where the modification returns a
   * {@link fj.data.Option}.
   *
   * @param f a function that may or may not modify the field value
   * @param a the record
   * @return some modified record or none, if nothing was modified
   */
  public final Option<A> modOption(final F<B, Option<B>> f, final A a) {
    return f.f(get(a)).map(res -> set(a, res));
  }

  /**
   * Curried version of {@link #modOption(fj.F, A)}.
   *
   * @param f a function that may or may not modify the field value
   * @return a function that may or may not modify the record value
   */
  public final F<A, Option<A>> modOption(final F<B, Option<B>> f) {
    return a -> modOption(f, a);
  }

  /**
   * Version of {@link #mod(fj.F, A)}, where the modification returns a
   * {@link fj.P2}.
   *
   * @param f a function that modifies the field value together with an
   *          additional value
   * @param a the record
   * @param <C> the additional tuple type
   * @return the modified record together with an additional value
   */
  public final <C> P2<A, C> modPair(final F<B, P2<B, C>> f, final A a) {
    return f.f(get(a)).map1(res -> set(a, res));
  }

  /**
   * Curried version of {@link #modPair(fj.F, A)}.
   *
   * @param f a function that modifies the field value together with an
   *          additional value
   * @param <C> the additional tuple type
   * @return a function that modifies the record value together with an
   *          additional value
   */
  public final <C> F<A, P2<A, C>> modPair(final F<B, P2<B, C>> f) {
    return a -> modPair(f, a);
  }

  /**
   * Version of {@link #mod(fj.F, A)}, where the modification returns a
   * {@link fj.data.Validation}.
   *
   * @param f a function that validates the field value
   * @param a the record
   * @param <E> the error type for the validation
   * @return a validated record
   */
  public final <E> Validation<E, A> modValidation(final F<B, Validation<E, B>> f, final A a) {
    return f.f(get(a)).map(res -> set(a, res));
  }

  /**
   * Curried version of {@link #modValidation(fj.F, A)}.
   *
   * @param f a function that validates the field value
   * @param <E> the error type for the validation
   * @return a function that validates the record value
   */
  public final <E> F<A, Validation<E, A>> modValidation(final F<B, Validation<E, B>> f) {
    return a -> modValidation(f, a);
  }

  /**
   * Modify the portion of the state viewed through the lens and return its new
   * value.
   *
   * @param f a function that modifies the field value
   * @return a state, returning the modified field value
   */
  public final State<A, B> modS(final F<B, B> f) {
    return State.unit(a -> {
      final B b = f.f(get(a));
      return P.p(set(a, b), b);
    });
  }

  /**
   * Modify the portion of the state viewed through the lens and return its old
   * value.
   *
   * @param f a function that modifies the field value
   * @return a state, returning the unmodified field value
   */
  public final State<A, B> modSO(final F<B, B> f) {
    return State.unit(a -> {
      final B b = get(a);
      return P.p(set(a, f.f(b)), b);
    });
  }

  /**
   * Modify the portion of the state viewed through the lens, but do not return
   * its new value.
   *
   * @param f a function that modifies the field value
   * @return a state, discarding the modified field value
   */
  public final State<A, Unit> modS_(final F<B, B> f) {
    return State.unit(a -> P.p(mod(f, a), Unit.unit()));
  }

  /**
   * Set the portion of the state viewed through the lens and return its new
   * value.
   *
   * @param b the new field value
   * @return a state with the new field value
   */
  public final State<A, B> assign(final P1<B> b) {
    return modS(ignore -> b._1());
  }

  /**
   * Set the portion of the state viewed through the lens and return its old
   * value.
   *
   * @param b the new field value
   * @return a state with the old field value
   */
  public final State<A, B> assignOld(final P1<B> b) {
    return modSO(ignore -> b._1());
  }

  /**
   * Contravariantly map a state action through the lens.
   *
   * @param s a state for the field value
   * @param <C> the return type for the given state
   * @return a state for the record value
   */
  public final <C> State<A, C> liftS(final State<B, C> s) {
    return State.unit(a -> modPair(s::run, a));
  }

  /**
   * Map the function {@code f} over the lens as a state action.
   *
   * @param f a function from one field value to another
   * @param <C> the new type of the field value
   * @return a state for the record value
   */
  public final <C> State<A, C> map(final F<B, C> f) {
    return State.unit(a -> P.p(a, f.f(get(a))));
  }

  /**
   * Bind the function {@code f} over the value under the lens, as a state
   * action.
   *
   * @param f a function from the field value to a different record state
   * @param <C> the new type of the field value
   * @return a state for the record value
   */
  public final <C> State<A, C> flatMap(final F<B, State<A, C>> f) {
    return State.unit(a -> f.f(get(a)).run(a));
  }

  /**
   * Sequence the monadic action of looking through the lens to occur before
   * the state action {@code s}.
   *
   * @param s the new state to return
   * @param <C> the new type of the field value
   * @return the new state for the record value
   */
  public final <C> State<A, C> sequence(final P1<State<A, C>> s) {
    return flatMap(ignore -> s._1());
  }

  /**
   * Compose the lens with another one.
   *
   * @param that a lens from another record to this record as its field
   * @param <C> the type of the other record
   * @return a lens from that record to this field
   */
  public final <C> Lens<C, B> compose(final Lens<C, A> that) {
    return lens(
        a -> get(that.get(a)),
        (a, b) -> that.set(a, set(that.get(a), b)));
  }

  /**
   * Compose another lens with this one.
   *
   * @param that a lens from this field as its record to another field
   * @param <C> the type of the other field
   * @return a lens from this record to that field
   */
  public final <C> Lens<A, C> andThen(final Lens<B, C> that) {
    return that.compose(this);
  }

  /**
   * Join two lenses that view a value of the same type.
   *
   * @param that a lens that views the same field type from another record
   * @param <C> the type of the other record
   * @return a lens that can view this field from either record
   */
  public final <C> Lens<Either<A, C>, B> sum(final Lens<C, B> that) {
    return lens(
        ac -> ac.either(this::get, that::get),
        ac -> ac.either(
            a -> b -> Either.left(set(a, b)),
            c -> b -> Either.right(that.set(c, b))));
  }

  /**
   * Pair two disjoint lenses.
   *
   * @param that a lens with its own record and field types
   * @param <C> the type of the other record
   * @param <D> the type of the other field
   * @return a lens from both records to both fields
   */
  public final <C, D> Lens<P2<A, C>, P2<B, D>> product(final Lens<C, D> that) {
    return lens(
        ac -> P.p(get(ac._1()), that.get(ac._2())),
        ac -> bd -> P.p(set(ac._1(), bd._1()), that.set(ac._2(), bd._2())));
  }

  /**
   * Create a lens from a getter and a curried setter.
   *
   * @param get the getter
   * @param set the setter, curried
   * @param <A> the type of the record
   * @param <B> the type of the field
   * @return the lens from the record to the field through the getter and setter
   */
  public static <A, B> Lens<A, B> lens(final F<A, B> get, final F<A, F<B, A>> set) {
    return new DefaultLens<>(get, set);
  }

  /**
   * Create a lens from a getter and a setter.
   *
   * @param get the getter
   * @param set the setter
   * @param <A> the type of the record
   * @param <B> the type of the field
   * @return the lens from the record to the field through the getter and setter
   */
  public static <A, B> Lens<A, B> lens(final F<A, B> get, final F2<A, B, A> set) {
    return lens(get, Function.curry(set));
  }

  /**
   * The identity for a given record.
   *
   * @param <A> the type of the record and the field
   * @return the id lens, that returns always the identical record
   */
  public static <A> Lens<A, A> id() {
    return lens(Function.identity(), a -> Function.identity());
  }

  /**
   * The trivial lens that can retrieve Unit from anything.
   *
   * @param <A> the type of the record
   * @return a lens that produces unit for every record
   */
  public static <A> Lens<A, Unit> trivial() {
    return lens(a -> Unit.unit(), a -> b -> a);
  }

  /**
   * A lens that discards the choice of right or left from disjunction.
   *
   * @param <A> the type of the record and the field
   * @return a lens that discards disjunctions for the same type
   */
  public static <A> Lens<Either<A, A>, A> codiag() {
    return Lens.<A>id().sum(id());
  }

  /**
   * Access the first field of a pair.
   *
   * @param <A> the type for the first element of the pair
   * @param <B> the type for the second element of the pair
   * @return a lens that views the first value of a pair
   */
  public static <A, B> Lens<P2<A, B>, A> first() {
    return lens(P2::_1, ab -> a -> ab.map1(x -> a));
  }

  /**
   * Access the second field of a pair.
   *
   * @param <A> the type for the first element of the pair
   * @param <B> the type for the second element of the pair
   * @return a lens that views the second value of a pair
   */
  public static <A, B> Lens<P2<A, B>, B> second() {
    return lens(P2::_2, ab -> b -> ab.map2(x -> b));
  }

  /**
   * Access the head of a non empty list.
   *
   * @param <A> the type of the list elements
   * @return a lens that views the head of a non empty list
   */
  public static <A> Lens<NonEmptyList<A>, A> nelHead() {
    return lens(
        nel -> nel.head,
        nel -> a -> NonEmptyList.nel(a, nel.tail));
  }

  /**
   * Access the tail of a non empty list.
   *
   * @param <A> the type of the list elements
   * @return a lens that views the tail of a non empty list
   */
  public static <A> Lens<NonEmptyList<A>, List<A>> nelTail() {
    return lens(
        nel -> nel.tail,
        nel -> xs -> NonEmptyList.nel(nel.head, xs));
  }

  /**
   * Access the value at a particular key of a Map.
   *
   * @param k the key of the map
   * @param <K> the type of the map keys
   * @param <V> the type of the map values
   * @return a lens that views the membership for a given key in a map
   */
  public static <K, V> Lens<TreeMap<K, V>, Option<V>> mapValue(final K k) {
    return lens(
        m -> m.get(k),
        m -> o -> o.option(m.delete(k), v -> m.set(k, v)));
  }

  /**
   * Access the value at a particular key of a Map or use a default value.
   *
   * @param k the key of the map
   * @param def the default value, if the key was not in the map
   * @param <K> the type of the map keys
   * @param <V> the type of the map values
   * @return a lens that views the membership for a given key in a map
   */
  public static <K, V> Lens<TreeMap<K, V>, V> mapWithDefault(final K k, final P1<V> def) {
    return lens(
        m -> m.get(k).orSome(def),
        m -> o -> m.set(k, o));
  }

  /**
   * Specify whether a value is in a Set.
   *
   * @param a the member of the set
   * @param <A> the type of the set members
   * @return a lens that views the membership for a element in a set
   */
  public static <A> Lens<Set<A>, Boolean> setMembership(final A a) {
    return lens(
        q -> q.member(a),
        q -> b -> b ? q.insert(a) : q.delete(a));
  }

  private static final class DefaultLens<A, B> extends Lens<A, B> {

    private final F<A, B> get;
    private final F<A, F<B, A>> set;

    private DefaultLens(final F<A, B> get, final F<A, F<B, A>> set) {
      this.get = get;
      this.set = set;
    }

    @Override
    public B get(final A a) {
      return get.f(a);
    }

    @Override
    public A set(final A a, final B b) {
      return set.f(a).f(b);
    }
  }
}
