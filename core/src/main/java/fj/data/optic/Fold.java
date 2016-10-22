package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;

/**
 * A {@link Fold} can be seen as a {@link Getter} with many targets or a weaker {@link PTraversal} which cannot modify its
 * target.
 *
 * {@link Fold} is on the top of the Optic hierarchy which means that {@link Getter}, {@link PTraversal}, {@link POptional},
 * {@link PLens}, {@link PPrism} and {@link PIso} are valid {@link Fold}
 *
 * @param <S> the source of a {@link Fold}
 * @param <A> the target of a {@link Fold}
 */
public abstract class Fold<S, A> {

  /**
   * map each target to a {@link Monoid} and combine the results underlying representation of {@link Fold}, all {@link Fold}
   * methods are defined in terms of foldMap
   */
  public abstract <M> F<S, M> foldMap(Monoid<M> m, F<A, M> f);

  /** combine all targets using a target's {@link Monoid} */
  public final F<S, A> fold(final Monoid<A> m) {
    return foldMap(m, Function.identity());
  }

  /**
   * get all the targets of a {@link Fold} TODO: Shall it return a Stream as there might be an infinite number of targets?
   */
  public final List<A> getAll(final S s) {
    return foldMap(Monoid.listMonoid(), List::single).f(s);
  }

  /** find the first target of a {@link Fold} matching the predicate */
  public final F<S, Option<A>> find(final F<A, Boolean> p) {
    return foldMap(Monoid.firstOptionMonoid(), a -> p.f(a) ? Option.some(a) : Option.none());
  }

  /** get the first target of a {@link Fold} */
  public final Option<A> headOption(final S s) {
    return find(Function.constant(Boolean.TRUE)).f(s);
  }

  /** check if at least one target satisfies the predicate */
  public final F<S, Boolean> exist(final F<A, Boolean> p) {
    return foldMap(Monoid.disjunctionMonoid, p);
  }

  /** check if all targets satisfy the predicate */
  public final F<S, Boolean> all(final F<A, Boolean> p) {
    return foldMap(Monoid.conjunctionMonoid, p);
  }

  /** join two {@link Fold} with the same target */
  public final <S1> Fold<Either<S, S1>, A> sum(final Fold<S1, A> other) {
    return new Fold<Either<S, S1>, A>() {
      @Override
      public <B> F<Either<S, S1>, B> foldMap(final Monoid<B> m, final F<A, B> f) {
        return s -> s.either(Fold.this.foldMap(m, f), other.foldMap(m, f));
      }
    };
  }

  /**********************************************************/
  /** Compose methods between a {@link Fold} and another Optics */
  /**********************************************************/

  /** compose a {@link Fold} with a {@link Fold} */
  public final <B> Fold<S, B> composeFold(final Fold<A, B> other) {
    return new Fold<S, B>() {
      @Override
      public <C> F<S, C> foldMap(final Monoid<C> m, final F<B, C> f) {
        return Fold.this.foldMap(m, other.<C> foldMap(m, f));
      }
    };
  }

  /** compose a {@link Fold} with a {@link Getter} */
  public final <C> Fold<S, C> composeGetter(final Getter<A, C> other) {
    return composeFold(other.asFold());
  }

  /** compose a {@link Fold} with a {@link POptional} */
  public final <B, C, D> Fold<S, C> composeOptional(final POptional<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /** compose a {@link Fold} with a {@link PPrism} */
  public final <B, C, D> Fold<S, C> composePrism(final PPrism<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /** compose a {@link Fold} with a {@link PLens} */
  public final <B, C, D> Fold<S, C> composeLens(final PLens<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  /** compose a {@link Fold} with a {@link PIso} */
  public final <B, C, D> Fold<S, C> composeIso(final PIso<A, B, C, D> other) {
    return composeFold(other.asFold());
  }

  public static <A> Fold<A, A> id() {
    return PIso.<A, A> pId().asFold();
  }

  public static <A> Fold<Either<A, A>, A> codiagonal() {
    return new Fold<Either<A, A>, A>() {
      @Override
      public <B> F<Either<A, A>, B> foldMap(final Monoid<B> m, final F<A, B> f) {
        return e -> e.either(f, f);
      }
    };
  }

}
