package fj.data.optic;

import fj.F;
import fj.Function;
import fj.Monoid;
import fj.P;
import fj.P1;
import fj.Semigroup;
import fj.control.Trampoline;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.Either;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.data.Validation;
import fj.data.vector.V2;

/**
 * A {@link PPrism} can be seen as a pair of functions: - `getOrModify: S => T \/ A` - `reverseGet : B => T`
 *
 * A {@link PPrism} could also be defined as a weaker {@link PIso} where get can fail.
 *
 * Typically a {@link PPrism} or {@link Prism} encodes the relation between a Sum or CoProduct type (e.g. sealed trait) and one
 * of it is element.
 *
 * {@link PPrism} stands for Polymorphic Prism as it set and modify methods change a type `A` to `B` and `S` to `T`.
 * {@link Prism} is a {@link PPrism} where the type of target cannot be modified.
 *
 * A {@link PPrism} is also a valid {@link Fold}, {@link POptional}, {@link PTraversal} and {@link PSetter}
 *
 * @param <S> the source of a {@link PPrism}
 * @param <T> the modified source of a {@link PPrism}
 * @param <A> the target of a {@link PPrism}
 * @param <B> the modified target of a {@link PPrism}
 */
public abstract class PPrism<S, T, A, B> {

  PPrism() {
    super();
  }

  /** get the target of a {@link PPrism} or modify the source in case there is no target */

  public abstract Either<T, A> getOrModify(S s);

  /** get the modified source of a {@link PPrism} */
  public abstract T reverseGet(B b);

  /** get the target of a {@link PPrism} or nothing if there is no target */
  public abstract Option<A> getOption(final S s);

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final <C> F<S, F<C, T>> modifyFunctionF(final F<A, F<C, B>> f) {
    return s -> getOrModify(s).either(
        Function.constant(),
        a -> Function.compose(this::reverseGet, f.f(a))
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final <L> F<S, Either<L, T>> modifyEitherF(final F<A, Either<L, B>> f) {
    return s -> getOrModify(s).either(
        Either.right_(),
        t -> f.f(t).right().map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, IO<T>> modifyIOF(final F<A, IO<B>> f) {
    return s -> getOrModify(s).either(
        IOFunctions::unit,
        t -> IOFunctions.map(f.f(t), this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, Trampoline<T>> modifyTrampolineF(final F<A, Trampoline<B>> f) {
    return s -> getOrModify(s).either(
        Trampoline.pure(),
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, Promise<T>> modifyPromiseF(final F<A, Promise<B>> f) {
    return s -> getOrModify(s).either(
        t -> Promise.promise(Strategy.idStrategy(), P.p(t)),
        t -> f.f(t).fmap(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, List<T>> modifyListF(final F<A, List<B>> f) {
    return s -> getOrModify(s).either(
        List::single,
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, Option<T>> modifyOptionF(final F<A, Option<B>> f) {
    return s -> getOrModify(s).either(
        Option.some_(),
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, Stream<T>> modifyStreamF(final F<A, Stream<B>> f) {
    return s -> getOrModify(s).either(
        Stream.single(),
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, P1<T>> modifyP1F(final F<A, P1<B>> f) {
    return s -> getOrModify(s).either(
        P.p1(),
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final <E> F<S, Validation<E, T>> modifyValidationF(final F<A, Validation<E, B>> f) {
    return s -> getOrModify(s).either(
        Validation::<E, T>success,
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with an Applicative function */
  public final F<S, V2<T>> modifyV2F(final F<A, V2<B>> f) {
    return s -> getOrModify(s).either(
        t -> V2.p(P.p(t, t)),
        t -> f.f(t).map(this::reverseGet)
        );
  }

  /** modify polymorphically the target of a {@link PPrism} with a function */
  public final F<S, T> modify(final F<A, B> f) {
    return s -> getOrModify(s).either(Function.identity(), a -> reverseGet(f.f(a)));
  }

  /** modify polymorphically the target of a {@link PPrism} with a function. return empty if the {@link PPrism} is not matching */
  public final F<S, Option<T>> modifyOption(final F<A, B> f) {
    return s -> getOption(s).map(a -> reverseGet(f.f(a)));
  }

  /** set polymorphically the target of a {@link PPrism} with a value */
  public final F<S, T> set(final B b) {
    return modify(Function.constant(b));
  }

  /** set polymorphically the target of a {@link PPrism} with a value. return empty if the {@link PPrism} is not matching */
  public final F<S, Option<T>> setOption(final B b) {
    return modifyOption(Function.constant(b));
  }

  /** check if a {@link PPrism} has a target */
  public final boolean isMatching(final S s) {
    return getOption(s).isSome();
  }

  /** create a {@link Getter} from the modified target to the modified source of a {@link PPrism} */
  public final Getter<B, T> re() {
    return Getter.getter(this::reverseGet);
  }

  /************************************************************/
  /** Compose methods between a {@link PPrism} and another Optics */
  /************************************************************/

  /** compose a {@link PPrism} with a {@link Fold} */
  public final <C> Fold<S, C> composeFold(final Fold<A, C> other) {
    return asFold().composeFold(other);
  }

  /** compose a {@link PPrism} with a {@link Getter} */
  public final <C> Fold<S, C> composeGetter(final Getter<A, C> other) {
    return asFold().composeGetter(other);
  }

  /** compose a {@link PPrism} with a {@link PSetter} */
  public final <C, D> PSetter<S, T, C, D> composeSetter(final PSetter<A, B, C, D> other) {
    return asSetter().composeSetter(other);
  }

  /** compose a {@link PPrism} with a {@link PTraversal} */
  public final <C, D> PTraversal<S, T, C, D> composeTraversal(final PTraversal<A, B, C, D> other) {
    return asTraversal().composeTraversal(other);
  }

  /** compose a {@link PPrism} with a {@link POptional} */
  public final <C, D> POptional<S, T, C, D> composeOptional(final POptional<A, B, C, D> other) {
    return asOptional().composeOptional(other);
  }

  /** compose a {@link PPrism} with a {@link PLens} */
  public final <C, D> POptional<S, T, C, D> composeLens(final PLens<A, B, C, D> other) {
    return asOptional().composeOptional(other.asOptional());
  }

  /** compose a {@link PPrism} with a {@link PPrism} */
  public final <C, D> PPrism<S, T, C, D> composePrism(final PPrism<A, B, C, D> other) {
    return new PPrism<S, T, C, D>() {

      @Override
      public Either<T, C> getOrModify(final S s) {
        return PPrism.this.getOrModify(s).right()
            .bind(a -> other.getOrModify(a).bimap(b -> PPrism.this.set(b).f(s), Function.identity()));
      }

      @Override
      public T reverseGet(final D d) {
        return PPrism.this.reverseGet(other.reverseGet(d));
      }

      @Override
      public Option<C> getOption(final S s) {
        return PPrism.this.getOption(s).bind(other::getOption);
      }
    };
  }

  /** compose a {@link PPrism} with a {@link PIso} */
  public final <C, D> PPrism<S, T, C, D> composeIso(final PIso<A, B, C, D> other) {
    return composePrism(other.asPrism());
  }

  /******************************************************************/
  /** Transformation methods to view a {@link PPrism} as another Optics */
  /******************************************************************/

  /** view a {@link PPrism} as a {@link Fold} */
  public final Fold<S, A> asFold() {
    return new Fold<S, A>() {
      @Override
      public <M> F<S, M> foldMap(final Monoid<M> monoid, final F<A, M> f) {
        return s -> getOption(s).map(f).orSome(monoid.zero());
      }
    };
  }

  /** view a {@link PPrism} as a {@link Setter} */
  public PSetter<S, T, A, B> asSetter() {
    return new PSetter<S, T, A, B>() {
      @Override
      public F<S, T> modify(final F<A, B> f) {
        return PPrism.this.modify(f);
      }

      @Override
      public F<S, T> set(final B b) {
        return PPrism.this.set(b);
      }
    };
  }

  /** view a {@link PPrism} as a {@link PTraversal} */
  public PTraversal<S, T, A, B> asTraversal() {
    final PPrism<S, T, A, B> self = this;
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
        return s -> getOption(s).map(f).orSome(monoid.zero());
      }

    };
  }

  /** view a {@link PPrism} as a {@link POptional} */
  public POptional<S, T, A, B> asOptional() {
    final PPrism<S, T, A, B> self = this;
    return new POptional<S, T, A, B>() {

      @Override
      public Either<T, A> getOrModify(final S s) {
        return self.getOrModify(s);
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
        return self.getOption(s);
      }

      @Override
      public F<S, T> modify(final F<A, B> f) {
        return self.modify(f);
      }

    };
  }

  public static <S, T> PPrism<S, T, S, T> pId() {
    return PIso.<S, T> pId().asPrism();
  }

  /** create a {@link PPrism} using the canonical functions: getOrModify and reverseGet */
  public static <S, T, A, B> PPrism<S, T, A, B> pPrism(final F<S, Either<T, A>> getOrModify, final F<B, T> reverseGet) {
    return new PPrism<S, T, A, B>() {

      @Override
      public Either<T, A> getOrModify(final S s) {
        return getOrModify.f(s);
      }

      @Override
      public T reverseGet(final B b) {
        return reverseGet.f(b);
      }

      @Override
      public Option<A> getOption(final S s) {
        return getOrModify.f(s).right().toOption();
      }
    };
  }

}