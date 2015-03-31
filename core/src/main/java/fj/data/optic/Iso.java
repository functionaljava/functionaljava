package fj.data.optic;

import fj.F;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.data.Either;
import fj.data.IO;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/** {@link PIso} when S = T and A = B */
public abstract class Iso<S, A> extends PIso<S, S, A, A> {

  private Iso() {
    super();
  }

  @Override
  public abstract Iso<A, S> reverse();

  /** pair two disjoint {@link Iso} */
  public <S1, A1> Iso<P2<S, S1>, P2<A, A1>> product(final Iso<S1, A1> other) {
    return iso(
        ss1 -> P.p(get(ss1._1()), other.get(ss1._2())),
        bb1 -> P.p(reverseGet(bb1._1()), other.reverseGet(bb1._2())));
  }

  @Override
  public <C> Iso<P2<S, C>, P2<A, C>> first() {
    return iso(
        sc -> P.p(get(sc._1()), sc._2()),
        bc -> P.p(reverseGet(bc._1()), bc._2()));
  }

  @Override
  public <C> Iso<P2<C, S>, P2<C, A>> second() {
    return iso(
        cs -> P.p(cs._1(), get(cs._2())),
        cb -> P.p(cb._1(), reverseGet(cb._2())));
  }

  /**********************************************************/
  /** Compose methods between an {@link Iso} and another Optics */
  /**********************************************************/

  /** compose an {@link Iso} with a {@link Setter} */
  public final <C> Setter<S, C> composeSetter(final Setter<A, C> other) {
    return asSetter().composeSetter(other);
  }

  /** compose an {@link Iso} with a {@link Traversal} */
  public final <C> Traversal<S, C> composeTraversal(final Traversal<A, C> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose an {@link Iso} with a {@link Optional} */
  public final <C> Optional<S, C> composeOptional(final Optional<A, C> other) {
    return asOptional().composeOptional(other);
  }

  /** compose an {@link Iso} with a {@link Prism} */
  public final <C> Prism<S, C> composePrism(final Prism<A, C> other) {
    return asPrism().composePrism(other);
  }

  /** compose an {@link Iso} with a {@link Lens} */
  public final <C> Lens<S, C> composeLens(final Lens<A, C> other) {
    return asLens().composeLens(other);
  }

  /** compose an {@link Iso} with an {@link Iso} */
  public final <C> Iso<S, C> composeIso(final Iso<A, C> other) {
    final Iso<S, A> self = this;
    return new Iso<S, C>() {

      @Override
      public C get(final S s) {
        return other.get(self.get(s));
      }

      @Override
      public S reverseGet(final C c) {
        return self.reverseGet(other.reverseGet(c));
      }

      @Override
      public Iso<C, S> reverse() {
        final Iso<S, C> composeSelf = this;
        return new Iso<C, S>() {

          @Override
          public S get(final C c) {
            return self.reverseGet(other.reverseGet(c));
          }

          @Override
          public C reverseGet(final S s) {
            return other.get(self.get(s));
          }

          @Override
          public Iso<S, C> reverse() {
            return composeSelf;
          }
        };
      }

    };
  }

  /****************************************************************/
  /** Transformation methods to view an {@link Iso} as another Optics */
  /****************************************************************/

  /** view an {@link Iso} as a {@link Setter} */
  @Override
  public final Setter<S, A> asSetter() {
    final Iso<S, A> self = this;
    return new Setter<S, A>() {
      @Override
      public F<S, S> modify(final F<A, A> f) {
        return self.modify(f);
      }

      @Override
      public F<S, S> set(final A a) {
        return self.set(a);
      }
    };
  }

  /** view an {@link Iso} as a {@link Traversal} */
  @Override
  public final Traversal<S, A> asTraversal() {
    final Iso<S, A> self = this;
    return new Traversal<S, A>() {

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> f.f(self.get(s));
      }

    };
  }

  /** view an {@link Iso} as a {@link Optional} */
  @Override
  public final Optional<S, A> asOptional() {
    final Iso<S, A> self = this;
    return new Optional<S, A>() {
      @Override
      public Either<S, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public F<S, S> set(final A b) {
        return self.set(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }

      @Override
      public F<S, S> modify(final F<A, A> f) {
        return self.modify(f);
      }
    };
  }

  /** view an {@link Iso} as a {@link Prism} */
  @Override
  public final Prism<S, A> asPrism() {
    final Iso<S, A> self = this;
    return new Prism<S, A>() {
      @Override
      public Either<S, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public S reverseGet(final A b) {
        return self.reverseGet(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }
    };
  }

  /** view an {@link Iso} as a {@link Lens} */
  @Override
  public final Lens<S, A> asLens() {
    final Iso<S, A> self = this;
    return new Lens<S, A>() {
      @Override
      public A get(final S s) {
        return self.get(s);
      }

      @Override
      public F<S, S> set(final A b) {
        return self.set(b);
      }

      @Override
      public F<S, S> modify(final F<A, A> f) {
        return self.modify(f);
      }

      @Override
      public <C> F<S, F<C, S>> modifyFunctionF(final F<A, F<C, A>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, S>> modifyEitherF(final F<A, Either<L, A>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<S>> modifyIOF(final F<A, IO<A>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<S>> modifyTrampolineF(final F<A, Trampoline<A>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<S>> modifyPromiseF(final F<A, Promise<A>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<S>> modifyListF(final F<A, List<A>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<S>> modifyOptionF(final F<A, Option<A>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<S>> modifyStreamF(final F<A, Stream<A>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<S>> modifyP1F(final F<A, P1<A>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, S>> modifyValidationF(final F<A, Validation<E, A>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<S>> modifyV2F(final F<A, V2<A>> f) {
        return self.modifyV2F(f);
      }
    };
  }

  /** create an {@link Iso} using a pair of functions: one to get the target and one to get the source. */
  public static <S, A> Iso<S, A> iso(final F<S, A> get, final F<A, S> reverseGet) {
    return new Iso<S, A>() {

      @Override
      public A get(final S s) {
        return get.f(s);
      }

      @Override
      public S reverseGet(final A a) {
        return reverseGet.f(a);
      }

      @Override
      public Iso<A, S> reverse() {
        final Iso<S, A> self = this;
        return new Iso<A, S>() {
          @Override
          public S get(final A a) {
            return reverseGet.f(a);
          }

          @Override
          public A reverseGet(final S s) {
            return get.f(s);
          }

          @Override
          public Iso<S, A> reverse() {
            return self;
          }

        };
      }
    };
  }

  /**
   * create an {@link Iso} between any type and itself. id is the zero element of optics composition, for all optics o of type O
   * (e.g. Lens, Iso, Prism, ...):
   *
   * <pre>
   *  o composeIso Iso.id == o
   *  Iso.id composeO o == o
   * </pre>
   *
   * (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  public static <S> Iso<S, S> id() {
    return new Iso<S, S>() {

      @Override
      public S get(final S s) {
        return s;
      }

      @Override
      public S reverseGet(final S s) {
        return s;
      }

      @Override
      public Iso<S, S> reverse() {
        return this;
      }

    };
  }

}
