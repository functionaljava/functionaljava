package fj.data;

import java.util.stream.Collector;

public final class Collectors {

  private Collectors() {
  }

  public static <A> Collector<A, List.Buffer<A>, List<A>> toList() {
    return Collector.of(
        List.Buffer::new,
        List.Buffer::snoc,
        (acc1, acc2) -> acc1.append(acc2.toList()),
        List.Buffer::toList
    );
  }

  public static <A> Collector<A, List.Buffer<A>, Array<A>> toArray() {
    return Collector.of(
        List.Buffer::new,
        List.Buffer::snoc,
        (acc1, acc2) -> acc1.append(acc2.toList()),
        (buf) -> buf.toList().toArray()
    );
  }

  public static <A> Collector<A, List.Buffer<A>, Stream<A>> toStream() {
    return Collector.of(
        List.Buffer::new,
        List.Buffer::snoc,
        (acc1, acc2) -> acc1.append(acc2.toList()),
        (buf) -> buf.toList().toStream()
    );
  }
}
