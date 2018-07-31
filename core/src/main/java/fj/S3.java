package fj;

import fj.data.Option;

/**
 * A sum-type with 3 alternatives.
 */
public final class S3<_1, _2, _3> {

  private final int tag;
  private final Object value;

  private S3(int tag, Object value) {
    this.tag = tag;
    this.value = value;
  }

  public static <_1, _2, _3> S3<_1, _2, _3> _1(_1 value) {
    return new S3<_1, _2, _3>(1, value);
  }

  public static <_1, _2, _3> S3<_1, _2, _3> _2(_2 value) {
    return new S3<_1, _2, _3>(2, value);
  }

  public static <_1, _2, _3> S3<_1, _2, _3> _3(_3 value) {
    return new S3<_1, _2, _3>(3, value);
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

  public <result, _1, _2, _3> result match(F<_1, result> projection1, F<_2, result> projection2, F<_3, result> projection3) {
    switch (tag) {
      case 1:
        return projection1.f((_1) value);
      case 2:
        return projection2.f((_2) value);
      case 3:
        return projection3.f((_3) value);
      default:
        throw new Error();
    }
  }

}
