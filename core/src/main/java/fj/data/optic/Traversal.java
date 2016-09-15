package fj.data.optic;

import fj.F;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.Monoid;
import fj.P1;
import fj.Semigroup;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

public final class Traversal<S, A> extends PTraversal<S, S, A, A> {

  final PTraversal<S, S, A, A> pTraversal;

  public Traversal(final PTraversal<S, S, A, A> pTraversal) {
    this.pTraversal = pTraversal;
  }

  @Override
  public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
    return pTraversal.modifyFunctionF(f);
  }

  @Override
  public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
    return pTraversal.modifyEitherF(f);
  }

  @Override
  public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
    return pTraversal.modifyIOF(f);
  }

  @Override
  public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
    return pTraversal.modifyTrampolineF(f);
  }

  @Override
  public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
    return pTraversal.modifyPromiseF(f);
  }

  @Override
  public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
    return pTraversal.modifyListF(f);
  }

  @Override
  public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
    return pTraversal.modifyOptionF(f);
  }

  @Override
  public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
    return pTraversal.modifyStreamF(f);
  }

  @Override
  public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
    return pTraversal.modifyP1F(f);
  }

  @Override
  public <E> F<S, Validation<E, S>> modifyValidationF(Semigroup<E> s, final F<A, Validation<E, A>> f) {
    return pTraversal.modifyValidationF(s, f);
  }

  @Override
  public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
    return pTraversal.modifyV2F(f);
  }

  @Override
  public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
    return pTraversal.foldMap(monoid, f);
  }

  /** join two {@link Traversal} with the same target */
  public <S1> Traversal<Either<S, S1>, A> sum(final Traversal<S1, A> other) {
    return new Traversal<>(pTraversal.sum(other.pTraversal));
  }

  /***************************************************************/
  /** Compose methods between a {@link Traversal} and another Optics */
  /***************************************************************/

  /** compose a {@link Traversal} with a {@link Setter} */
  public <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return new Setter<>(pTraversal.composeSetter(other.pSetter));
  }

  /** compose a {@link Traversal} with a {@link Traversal} */
  public <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return new Traversal<>(pTraversal.composeTraversal(other.pTraversal));
  }

  /*********************************************************************/
  /** Transformation methods to view a {@link Traversal} as another Optics */
  /*********************************************************************/

  /** view a {@link Traversal} as a {@link Setter} */
  @Override
  public Setter<S, A> asSetter() {
    return new Setter<>(pTraversal.asSetter());
  }

  public static <S> Traversal<S, S> id() {
    return new Traversal<>(PTraversal.pId());
  }

  public static <S> Traversal<Either<S, S>, S> codiagonal() {
    return new Traversal<>(PTraversal.pCodiagonal());
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F3<A, A, S, S> set) {
    return new Traversal<>(PTraversal.pTraversal(get1, get2, set));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F4<A, A, A, S, S> set) {
    return new Traversal<>(PTraversal.pTraversal(get1, get2, get3, set));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F5<A, A, A, A, S, S> set) {
    return new Traversal<>(PTraversal.pTraversal(get1, get2, get3, get4, set));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5, final F6<A, A, A, A, A, S, S> set) {
    return new Traversal<>(PTraversal.pTraversal(get1, get2, get3, get4, get5, set));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5, final F<S, A> get6, final F7<A, A, A, A, A, A, S, S> set) {
    return new Traversal<>(PTraversal.pTraversal(get1, get2, get3, get4, get5, get6,
        set));
  }

}
