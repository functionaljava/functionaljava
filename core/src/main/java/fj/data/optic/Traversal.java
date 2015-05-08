package fj.data.optic;

import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.Function;
import fj.Monoid;
import fj.P1;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

public abstract class Traversal<S, A> extends PTraversal<S, S, A, A> {

  /** join two {@link Traversal} with the same target */
  public final <S1> Traversal<Either<S, S1>, A> sum(final Traversal<S1, A> other) {
    final Traversal<S, A> self = this;
    return new Traversal<Either<S, S1>, A>() {

      @Override
      public <C> F<Either<S, S1>, F<C, Either<S, S1>>> modifyFunctionF(final F<A, F<C, A>> f) {
        return ss1 -> ss1.either(
            s -> Function.compose(Either.left_(), self.modifyFunctionF(f).f(s)),
            s1 -> Function.compose(Either.right_(), other.modifyFunctionF(f).f(s1))
            );
      }

      @Override
      public <L> F<Either<S, S1>, Either<L, Either<S, S1>>> modifyEitherF(final F<A, Either<L, A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyEitherF(f).f(s).right().map(Either.left_()),
            s1 -> other.modifyEitherF(f).f(s1).right().map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, IO<Either<S, S1>>> modifyIOF(final F<A, IO<A>> f) {
        return ss1 -> ss1.either(
            s -> IOFunctions.map(self.modifyIOF(f).f(s), Either.left_()),
            s1 -> IOFunctions.map(other.modifyIOF(f).f(s1), Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Trampoline<Either<S, S1>>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyTrampolineF(f).f(s).map(Either.left_()),
            s1 -> other.modifyTrampolineF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Promise<Either<S, S1>>> modifyPromiseF(final F<A, Promise<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyPromiseF(f).f(s).fmap(Either.left_()),
            s1 -> other.modifyPromiseF(f).f(s1).fmap(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, List<Either<S, S1>>> modifyListF(final F<A, List<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyListF(f).f(s).map(Either.left_()),
            s1 -> other.modifyListF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Option<Either<S, S1>>> modifyOptionF(final F<A, Option<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyOptionF(f).f(s).map(Either.left_()),
            s1 -> other.modifyOptionF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, Stream<Either<S, S1>>> modifyStreamF(final F<A, Stream<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyStreamF(f).f(s).map(Either.left_()),
            s1 -> other.modifyStreamF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, P1<Either<S, S1>>> modifyP1F(final F<A, P1<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyP1F(f).f(s).map(Either.left_()),
            s1 -> other.modifyP1F(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S1>, V2<Either<S, S1>>> modifyV2F(final F<A, V2<A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyV2F(f).f(s).map(Either.left_()),
            s1 -> other.modifyV2F(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public <E> F<Either<S, S1>, Validation<E, Either<S, S1>>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return ss1 -> ss1.either(
            s -> self.modifyValidationF(f).f(s).map(Either.left_()),
            s1 -> other.modifyValidationF(f).f(s1).map(Either.right_())
            );
      }

      @Override
      public <M> F<Either<S, S1>, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return ss1 -> ss1.either(
            self.foldMap(monoid, f),
            other.foldMap(monoid, f)
            );
      }

    };
  }

  /***************************************************************/
  /** Compose methods between a {@link Traversal} and another Optics */
  /***************************************************************/

  /** compose a {@link Traversal} with a {@link Setter} */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link Traversal} with a {@link Traversal} */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    final Traversal<S, A> self = this;
    return new Traversal<S, C>() {

      @Override
      public <G> F<S, F<G, S>> modifyFunctionF(final F<C, F<G, C>> f) {
        return self.modifyFunctionF(other.modifyFunctionF(f));
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<C, Either<L, C>> f) {
        return self.modifyEitherF(other.modifyEitherF(f));
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<C, IO<C>> f) {
        return self.modifyIOF(other.modifyIOF(f));
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<C, Trampoline<C>> f) {
        return self.modifyTrampolineF(other.modifyTrampolineF(f));
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<C, Promise<C>> f) {
        return self.modifyPromiseF(other.modifyPromiseF(f));
      }

      @Override
      public F<S, List<S>> modifyListF(final F<C, List<C>> f) {
        return self.modifyListF(other.modifyListF(f));
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<C, Option<C>> f) {
        return self.modifyOptionF(other.modifyOptionF(f));
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<C, Stream<C>> f) {
        return self.modifyStreamF(other.modifyStreamF(f));
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<C, P1<C>> f) {
        return self.modifyP1F(other.modifyP1F(f));
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<C, Validation<E, C>> f) {
        return self.modifyValidationF(other.modifyValidationF(f));
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<C, V2<C>> f) {
        return self.modifyV2F(other.modifyV2F(f));
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<C, M> f) {
        return self.foldMap(monoid, other.foldMap(monoid, f));
      }
    };
  }

  /*********************************************************************/
  /** Transformation methods to view a {@link Traversal} as another Optics */
  /*********************************************************************/

  /** view a {@link Traversal} as a {@link Setter} */
  @Override
  public final Setter<S, A> asSetter() {
    return Setter.setter(this::modify);
  }

  public static <S> Traversal<S, S> id() {
    return Iso.<S> id().asTraversal();
  }

  public static <S> Traversal<Either<S, S>, S> codiagonal() {
    return new Traversal<Either<S, S>, S>() {

      @Override
      public <C> F<Either<S, S>, F<C, Either<S, S>>> modifyFunctionF(final F<S, F<C, S>> f) {
        return s -> s.bimap(f, f).either(
            f1 -> Function.compose(Either.left_(), f1),
            f1 -> Function.compose(Either.right_(), f1)
            );
      }

      @Override
      public <L> F<Either<S, S>, Either<L, Either<S, S>>> modifyEitherF(final F<S, Either<L, S>> f) {
        return s -> s.bimap(f, f).either(
            e -> e.right().map(Either.left_()),
            e -> e.right().map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, IO<Either<S, S>>> modifyIOF(final F<S, IO<S>> f) {
        return s -> s.bimap(f, f).either(
            io -> IOFunctions.map(io, Either.left_()),
            io -> IOFunctions.map(io, Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Trampoline<Either<S, S>>> modifyTrampolineF(final F<S, Trampoline<S>> f) {
        return s -> s.bimap(f, f).either(
            t -> t.map(Either.left_()),
            t -> t.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Promise<Either<S, S>>> modifyPromiseF(final F<S, Promise<S>> f) {
        return s -> s.bimap(f, f).either(
            p -> p.fmap(Either.left_()),
            p -> p.fmap(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, List<Either<S, S>>> modifyListF(final F<S, List<S>> f) {
        return s -> s.bimap(f, f).either(
            l -> l.map(Either.left_()),
            l -> l.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Option<Either<S, S>>> modifyOptionF(final F<S, Option<S>> f) {
        return s -> s.bimap(f, f).either(
            o -> o.map(Either.left_()),
            o -> o.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, Stream<Either<S, S>>> modifyStreamF(final F<S, Stream<S>> f) {
        return s -> s.bimap(f, f).either(
            stream -> stream.map(Either.left_()),
            stream -> stream.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, P1<Either<S, S>>> modifyP1F(final F<S, P1<S>> f) {
        return s -> s.bimap(f, f).either(
            p1 -> p1.map(Either.left_()),
            p1 -> p1.map(Either.right_())
            );
      }

      @Override
      public F<Either<S, S>, V2<Either<S, S>>> modifyV2F(final F<S, V2<S>> f) {
        return s -> s.bimap(f, f).either(
            v2 -> v2.map(Either.left_()),
            v2 -> v2.map(Either.right_())
            );
      }

      @Override
      public <E> F<Either<S, S>, Validation<E, Either<S, S>>> modifyValidationF(final F<S, Validation<E, S>> f) {
        return s -> s.bimap(f, f).either(
            v -> v.map(Either.left_()),
            v -> v.map(Either.right_())
            );
      }

      @Override
      public <M> F<Either<S, S>, M> foldMap(final Monoid<M> monoid, final F<S, M> f) {
        return s -> s.either(f, f);
      }
    };
  }

  public static <S, A> Traversal<S, A> fromPtraversal(final PTraversal<S, S, A, A> pTraversal) {
    return new Traversal<S, A>() {

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
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return pTraversal.modifyValidationF(f);
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return pTraversal.modifyV2F(f);
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return pTraversal.foldMap(monoid, f);
      }
    };
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F2<A, A, S> set) {
    return fromPtraversal(PTraversal.pTraversal(get1, get2, (a1, a2, s) -> set.f(a1, a2)));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F3<A, A, A, S> set) {
    return fromPtraversal(PTraversal.pTraversal(get1, get2, get3, (a1, a2, a3, s) -> set.f(a1, a2, a3)));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4,
      final F4<A, A, A, A, S> set) {
    return fromPtraversal(PTraversal.pTraversal(get1, get2, get3, get4, (a1, a2, a3, a4, s) -> set.f(a1, a2, a3, a4)));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5,
      final F5<A, A, A, A, A, S> set) {
    return fromPtraversal(PTraversal.pTraversal(get1, get2, get3, get4, get5,
        (a1, a2, a3, a4, a5, s) -> set.f(a1, a2, a3, a4, a5)));
  }

  public static <S, A> Traversal<S, A> traversal(final F<S, A> get1, final F<S, A> get2, final F<S, A> get3,
      final F<S, A> get4, final F<S, A> get5, final F<S, A> get6,
      final F6<A, A, A, A, A, A, S> set) {
    return fromPtraversal(PTraversal.pTraversal(get1, get2, get3, get4, get5, get6,
        (a1, a2, a3, a4, a5, a6, s) -> set.f(a1, a2, a3, a4, a5, a6)));
  }

}
