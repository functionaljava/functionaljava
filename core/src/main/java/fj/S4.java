package fj;

import fj.data.Option;

/**
 * A sum-type with 4 alternatives.
 */
public final class S4<_1, _2, _3, _4> {

  private final int tag;
  private final Object value;

  private S4(int tag, Object value) {
    this.tag = tag;
    this.value = value;
  }

  public static <_1, _2, _3, _4> S4<_1, _2, _3, _4> _1(_1 value) {
    return new S4<_1, _2, _3, _4>(1, value);
  }

  public static <_1, _2, _3, _4> S4<_1, _2, _3, _4> _2(_2 value) {
    return new S4<_1, _2, _3, _4>(2, value);
  }

  public static <_1, _2, _3, _4> S4<_1, _2, _3, _4> _3(_3 value) {
    return new S4<_1, _2, _3, _4>(3, value);
  }

  public static <_1, _2, _3, _4> S4<_1, _2, _3, _4> _4(_4 value) {
    return new S4<_1, _2, _3, _4>(4, value);
  }

  public Option<_1> _1() {
    return tag == 1 ? Option.some((_1) value) : Option.none();
  }

  public Option<_2> _2() {
    return tag == 2 ? Option.some((_2) value) : Option.none();
  }

  public Option<_3> _3() {
    return tag == 3 ? Option.some((_3) value) : Option.none();
  }

  public Option<_4> _4() {
    return tag == 4 ? Option.some((_4) value) : Option.none();
  }

  public <result, _1, _2, _3, _4> result match(F<_1, result> projection1, F<_2, result> projection2, F<_3, result> projection3, F<_4, result> projection4) {
    switch (tag) {
      case 1:
        return projection1.f((_1) value);
      case 2:
        return projection2.f((_2) value);
      case 3:
        return projection3.f((_3) value);
      case 4:
        return projection4.f((_4) value);
      default:
        throw new Error();
    }
  }

}
