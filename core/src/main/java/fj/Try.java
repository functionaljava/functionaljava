package fj;

import fj.data.Either;
import fj.data.Validation;

/**
 * Created by MarkPerry on 2/06/2014.
 */
public class Try<A> extends Validation<Exception, A> {

    public static <B, C> Try<C> toTry(Validation<B, C> v) {
        return toTry(v.toEither());
    }

    public static <B, C> Try<C> toTry(Either<B, C> e) {
        return new Try(e);
    }

    public Try(Either<Exception, A> e) {
        super(e);
    }

    public static <B> Try<B> trySuccess(B b) {
        return toTry(Validation.success(b));
    }

    public static <B> Try<B> tryFail(Exception e) {
        return toTry(Validation.fail(e));
   }


}
