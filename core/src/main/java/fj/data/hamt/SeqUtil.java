package fj.data.hamt;

import fj.F;
import fj.P2;
import fj.data.Seq;


/**
 * Created by maperr on 6/06/2016.
 */
public class SeqUtil {

    public static <A> Seq<A> filter(Seq<A> s, F<A, Boolean> f) {
        return s.foldLeft((Seq<A> acc, A a) -> f.f(a) ? acc.snoc(a) : acc, Seq.<A>empty());
    }

    public static <A> Seq<A> insert(Seq<A> s, int index, A a) {
        P2<Seq<A>, Seq<A>> p2 = s.split(index);
        return p2._1().append(Seq.single(a)).append(p2._2());
    }

}
