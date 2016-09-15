package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Semigroup;
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

/**
 * A {@link PIso} defines an isomorphism between types S, A and B, T:
 *
 * <pre>
 *              get                           reverse.get
 *     -------------------->             -------------------->
 *   S                       A         T                       B
 *     <--------------------             <--------------------
 *       reverse.reverseGet                   reverseGet
 * </pre>
 *
 * In addition, if f and g forms an isomorphism between `A` and `B`, i.e. if `f . g = id` and `g . f = id`, then a {@link PIso}
 * defines an isomorphism between `S` and `T`:
 *
 * <pre>
 *     S           T                                   S           T
 *     |           |                                   |           |
 *     |           |                                   |           |
 * get |           | reverseGet     reverse.reverseGet |           | reverse.get
 *     |           |                                   |           |
 *     |     f     |                                   |     g     |
 *     A --------> B                                   A <-------- B
 * </pre>
 *
 * A {@link PIso} is also a valid {@link Getter}, {@link Fold}, {@link PLens}, {@link PPrism}, {@link POptional},
 * {@link PTraversal} and {@link PSetter}
 *
 * @param <S> the source of a {@link PIso}
 * @param <T> the modified source of a {@link PIso}
 * @param <A> the target of a {@link PIso}
 * @param <B> the modified target of a {@link PIso}
 */
public abstract class PIso<S, T, A, B> {

  PIso() {
    super();
  }

  /** get the target of a {@link PIso} */
  public abstract A get(S s);

  /** get the modified source of a {@link PIso} */
  public abstract T reverseGet(B b);

  /** reverse a {@link PIso}: the source becomes the target and the target becomes the source */
  public abstract PIso<B, A, T, S> reverse();

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
    return s -> Function.compose(this::reverseGet, f.f(get(s)));
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
    return s -> f.f(get(s)).right().map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
    return s -> IOFunctions.map(f.f(get(s)), this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
    return s -> f.f(get(s)).fmap(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, List<T>> modifyListF(final F<A, List<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with an Applicative function */
  public final F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
    return s -> f.f(get(s)).map(this::reverseGet);
  }

  /** modify polymorphically the target of a {@link PIso} with a function */
  public final F<S, T> modify(final F<A, B> f) {
    return s -> reverseGet(f.f(get(s)));
  }

  /** set polymorphically the target of a {@link PIso} with a value */
  public final F<S, T> set(final B b) {
    return Function.constant(reverseGet(b));
  }

  /** pair two disjoint {@link PIso} */
  public final <S1, T1, A1, B1> PIso<P2<S, S1>, P2<T, T1>, P2<A, A1>, P2<B, B1>> product(final PIso<S1, T1, A1, B1> other) {
    return pIso(
        ss1 -> P.p(get(ss1._1()), other.get(ss1._2())),
        bb1 -> P.p(reverseGet(bb1._1()), other.reverseGet(bb1._2())));
  }

  public <C> PIso<P2<S, C>, P2<T, C>, P2<A, C>, P2<B, C>> first() {
    return pIso(
        sc -> P.p(get(sc._1()), sc._2()),
        bc -> P.p(reverseGet(bc._1()), bc._2()));
  }

  public <C> PIso<P2<C, S>, P2<C, T>, P2<C, A>, P2<C, B>> second() {
    return pIso(
        cs -> P.p(cs._1(), get(cs._2())),
        cb -> P.p(cb._1(), reverseGet(cb._2())));
  }

  /**********************************************************/
  /** Compose methods between a {@link PIso} and another Optics */
  /**********************************************************/

  /** compose a {@link PIso} with a {@link Fold} */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /** compose a {@link PIso} with a {@link Getter} */
  public final <C> Getter<S, C> composeGetter(final Getter<A, C> other) {
    return asGetter().composeGetter(other);
  }

  /** compose a {@link PIso} with a {@link PSetter} */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link PIso} with a {@link PTraversal} */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link PIso} with a {@link POptional} */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return asOptional().composeOptional(other);
  }

  /** compose a {@link PIso} with a {@link PPrism} */
  public final <C, D> PPrism<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return asPrism().composePrism(other);
  }

  /** compose a {@link PIso} with a {@link PLens} */
  public final <C, D> PLens<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return asLens().composeLens(other);
  }

  /** compose a {@link PIso} with a {@link PIso} */
  public final <C, D> PIso<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    final PIso<S, T, A, B> self = this;
    return new PIso<S, T, C, D>() {
      @Override
      public C get(final S s) {
        return other.get(self.get(s));
      }

      @Override
      public T reverseGet(final D d) {
        return self.reverseGet(other.reverseGet(d));
      }

      @Override
      public PIso<D, C, T, S> reverse() {
        final PIso<S, T, C, D> composeSelf = this;
        return new PIso<D, C, T, S>() {
          @Override
          public T get(final D d) {
            return self.reverseGet(other.reverseGet(d));
          }

          @Override
          public C reverseGet(final S s) {
            return other.get(self.get(s));
          }

          @Override
          public PIso<S, T, C, D> reverse() {
            return composeSelf;
          }
        };
      }
    };
  }

  /****************************************************************/
  /** Transformation methods to view a {@link PIso} as another Optics */
  /****************************************************************/

  /** view a {@link PIso} as a {@link Fold} */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override
      public <M> F<S, M> foldMap(final Monoid<M> m, final F<A, M> f) {
        return s -> f.f(PIso.this.get(s));
      }
    };
  }

  /** view a {@link PIso} as a {@link Getter} */
  public final Getter<S, A> asGetter() {
    return new Getter<S, A>() {
      @Override
      public A get(final S s) {
        return PIso.this.get(s);
      }
    };
  }

  /** view a {@link PIso} as a {@link Setter} */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override
      public F<S, T> modify(final F<A, B> f) {
        return PIso.this.modify(f);
      }

      @Override
      public F<S, T> set(final B b) {
        return PIso.this.set(b);
      }
    };
  }

  /** view a {@link PIso} as a {@link PTraversal} */
  public PTraversal<S, T, A, B> asTraversal() {
    final PIso<S, T, A, B> self = this;
    return new PTraversal<S, T, A, B>() {

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(Semigroup<E> s, final F<A, Validation<E, B>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> f.f(self.get(s));
      }

    };
  }

  /** view a {@link PIso} as a {@link POptional} */
  public POptional<S, T, A, B> asOptional() {
    final PIso<S, T, A, B> self = this;
    return new POptional<S, T, A, B>() {
      @Override
      public Either<T, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return self.modifyV2F(f);
      }

      @Override
      public F<S, T> set(final B b) {
        return self.set(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return self.modify(f);
      }
    };
  }

  /** view a {@link PIso} as a {@link PPrism} */
  public PPrism<S, T, A, B> asPrism() {
    final PIso<S, T, A, B> self = this;
    return new PPrism<S, T, A, B>() {
      @Override
      public Either<T, A> getOrModify(final S s) {
        return Either.right(self.get(s));
      }

      @Override
      public T reverseGet(final B b) {
        return self.reverseGet(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return Option.some(self.get(s));
      }
    };
  }

  /** view a {@link PIso} as a {@link PLens} */
  public PLens<S, T, A, B> asLens() {
    final PIso<S, T, A, B> self = this;
    return new PLens<S, T, A, B>() {
      @Override
      public A get(final S s) {
        return self.get(s);
      }

      @Override
      public F<S, T> set(final B b) {
        return self.set(b);
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return self.modify(f);
      }

      @Override
      public <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
        return self.modifyFunctionF(f);
      }

      @Override
      public <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
        return self.modifyEitherF(f);
      }

      @Override
      public F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
        return self.modifyIOF(f);
      }

      @Override
      public F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
        return self.modifyTrampolineF(f);
      }

      @Override
      public F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
        return self.modifyPromiseF(f);
      }

      @Override
      public F<S, List<T>> modifyListF(final F<A, List<B>> f) {
        return self.modifyListF(f);
      }

      @Override
      public F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
        return self.modifyOptionF(f);
      }

      @Override
      public F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
        return self.modifyStreamF(f);
      }

      @Override
      public F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
        return self.modifyP1F(f);
      }

      @Override
      public <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
        return self.modifyValidationF(f);
      }

      @Override
      public F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
        return self.modifyV2F(f);
      }
    };
  }

  /** create a {@link PIso} using a pair of functions: one to get the target and one to get the source. */
  public static <S, T, A, B> PIso<S, T, A, B> pIso(final F<S, A> get, final F<B, T> reverseGet) {
    return new PIso<S, T, A, B>() {

      @Override
      public A get(final S s) {
        return get.f(s);
      }

      @Override
      public T reverseGet(final B b) {
        return reverseGet.f(b);
      }

      @Override
      public PIso<B, A, T, S> reverse() {
        final PIso<S, T, A, B> self = this;
        return new PIso<B, A, T, S>() {
          @Override
          public T get(final B b) {
            return reverseGet.f(b);
          }

          @Override
          public A reverseGet(final S s) {
            return get.f(s);
          }

          @Override
          public PIso<S, T, A, B> reverse() {
            return self;
          }
        };
      }

    };
  }

  /**
   * create a {@link PIso} between any type and itself. id is the zero element of optics composition, for all optics o of type O
   * (e.g. Lens, Iso, Prism, ...):
   *
   * <pre>
   *  o composeIso Iso.id == o
   *  Iso.id composeO o == o
   * </pre>
   *
   * (replace composeO by composeLens, composeIso, composePrism, ...)
   */
  public static <S, T> PIso<S, T, S, T> pId() {
    return new PIso<S, T, S, T>() {

      @Override
      public S get(final S s) {
        return s;
      }

      @Override
      public T reverseGet(final T t) {
        return t;
      }

      @Override
      public PIso<T, S, T, S> reverse() {
        final PIso<S, T, S, T> self = this;
        return new PIso<T, S, T, S>() {
          @Override
          public T get(final T t) {
            return t;
          }

          @Override
          public S reverseGet(final S s) {
            return s;
          }

          @Override
          public PIso<S, T, S, T> reverse() {
            return self;
          }
        };
      }
    };
  }

}
