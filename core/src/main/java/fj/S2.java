package fj;

import fj.data.Option;

/**
 * A sum-type with 2 alternatives.
 */
public final class S2<_1, _2> {

  private final int tag;
  private final Object value;

  private S2(int tag, Object value) {
    this.tag = tag;
    this.value = value;
  }

  public static <_1, _2> S2<_1, _2> _1(_1 value) {
    return new S2<_1, _2>(1, value);
  }

  public static <_1, _2> S2<_1, _2> _2(_2 value) {
    return new S2<_1, _2>(2, value);
  }

  public Option<_1> _1() {
    return tag == 1 ? Option.some((_1) value) : Option.none();
  }

  public Option<_2> _2() {
    return tag == 2 ? Option.some((_2) value) : Option.none();
  }

  public <result, _1, _2> result match(F<_1, result> projection1, F<_2, result> projection2) {
    switch (tag) {
      case 1:
        return projection1.f((_1) value);
      case 2:
        return projection2.f((_2) value);
      default:
        throw new Error();
    }
  }

}
