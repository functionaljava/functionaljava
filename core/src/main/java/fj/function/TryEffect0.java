package fj.function;

import fj.F0;
import fj.P;
import fj.P1;
import fj.Unit;
import fj.data.Option;

import static fj.Unit.unit;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 * Created by mperry on 28/08/2014.
 */
public interface TryEffect0<Z extends Exception> {

	void f() throws Z;

	@SuppressWarnings("unchecked")
	default F0<Option<Z>> toF0() {
		return () -> {
			try {
				f();
				return none();
			} catch (Exception e) {
				return some((Z) e);
			}
		};
	}

	@SuppressWarnings("unchecked")
	default Try0<Unit, Z> toTry0() {
		return () -> {
			try {
				f();
				return unit();
			} catch (Exception e) {
				throw ((Z) e);
			}
		};
	}

	default Effect0 toEffect0() {
		return () -> {
			try {
				f();
			} catch (Exception e) {
			}
		};
	}

	default P1<Unit> toP1() {
		return P.lazy(() -> {
			toEffect0().f();
			return Unit.unit();
		});
	}

}
