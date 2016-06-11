package fj.data.properties;


import fj.P;
import fj.P2;
import fj.data.Seq;
import fj.test.reflect.CheckParams;
import fj.test.runner.PropertyTestRunner;
import fj.test.Gen;
import fj.test.Property;
import org.junit.runner.RunWith;

import static fj.Function.identity;
import static fj.test.Arbitrary.*;
import static fj.test.Property.implies;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
@CheckParams(maxSize = 10000)
public class SeqProperties {

  private static final Gen<P2<Seq<Integer>, Integer>> arbSeqWithIndex = arbSeq(arbInteger)
    .filter(Seq::isNotEmpty)
    .bind(seq -> Gen.choose(0, seq.length() - 1).map(i -> P.p(seq, i)));

  public Property consHead() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(seq.cons(n).head().equals(n)));
  }

  public Property consLength() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(seq.cons(n).length() == seq.length() + 1));
  }

  public Property snocLast() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(seq.snoc(n).last().equals(n)));
  }

  public Property snocLength() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(seq.snoc(n).length() == seq.length() + 1));
  }

  public Property appendEmptyLeft() {
    return property(arbSeq(arbInteger), seq -> prop(Seq.<Integer>empty().append(seq).equals(seq)));
  }

  public Property appendEmptyRight() {
    return property(arbSeq(arbInteger), seq -> prop(seq.append(Seq.empty()).equals(seq)));
  }

  public Property appendLength() {
    return property(arbSeq(arbInteger), arbSeq(arbInteger), (seq1, seq2) ->
      prop(seq1.append(seq2).length() == seq1.length() + seq2.length()));
  }

  public Property consNotEmpty() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(!seq.cons(n).isEmpty()));
  }

  public Property snocNotEmpty() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(!seq.snoc(n).isEmpty()));
  }

  public Property appendSingleLeft() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(Seq.single(n).append(seq).equals(seq.cons(n))));
  }

  public Property appendSingleRight() {
    return property(arbSeq(arbInteger), arbInteger, (seq, n) -> prop(seq.append(Seq.single(n)).equals(seq.snoc(n))));
  }

  public Property splitLength() {
    return property(arbSeq(arbInteger), arbInteger, (seq, i) -> prop(seq.length() == seq.split(i)._1().length() + seq.split(i)._2().length()));
  }

  public Property tailLength() {
    return property(arbSeq(arbInteger), seq ->
      implies(!seq.isEmpty(), () -> prop(seq.length() == 1 + seq.tail().length())));
  }

  public Property initLength() {
    return property(arbSeq(arbInteger), seq ->
      implies(!seq.isEmpty(), () -> prop(seq.length() == seq.init().length() + 1)));
  }

  public Property mapId() {
    return property(arbSeq(arbInteger), seq -> prop(seq.map(identity()).equals(seq)));
  }

  @CheckParams(minSize = 1)
  public Property updateAndIndex() {
    return property(arbSeqWithIndex, arbInteger, (pair, n) -> {
      final Seq<Integer> seq = pair._1();
      final int index = pair._2();
      return prop(seq.update(index, n).index(index).equals(n));
    });
  }

  @CheckParams(minSize = 1)
  public Property delete() {
    return property(arbSeqWithIndex, arbInteger, (pair, n) -> {
      final Seq<Integer> seq = pair._1();
      final int index = pair._2();
      return prop(seq.delete(index).length() == seq.length() - 1);
    });
  }

  public Property foldLeft() {
    return property(arbSeq(Gen.value(1)), seq ->
      prop(seq.foldLeft((acc, i) -> acc + i, 0) == seq.length()));
  }

  public Property foldRight() {
    return property(arbSeq(Gen.value(1)), seq ->
      prop(seq.foldRight((i, acc) -> acc + i, 0) == seq.length()));
  }

    public Property length() {
        return property(arbList(arbInteger), list ->
            prop(Seq.iterableSeq(list).length() == list.length())
        );
    }

}
