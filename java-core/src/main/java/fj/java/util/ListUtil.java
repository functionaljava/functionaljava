package fj.java.util;

import fj.F;
import fj.F2;

import java.util.List;

/**
 * Created by MarkPerry on 28/08/2015.
 */
public class ListUtil {

    public static <A, B> List<B> map(List<A> list, F<A, B> f) {
        return fj.data.List.list(list).map(f).toJavaList();
    }

    public static<A> List<A> filter(List<A> list, F<A, Boolean> f) {
        return fj.data.List.list(list).filter(f).toJavaList();
    }

    public static <A, B> B fold(List<A> list, F2<B, A, B> f, B b) {
        return fj.data.List.list(list).foldLeft(f, b);
    }

    public static <A, B> List<B> flatMap(List<A> list, F<A, List<B>> f) {
        return fj.data.List.list(list).bind(a -> fj.data.List.list(f.f(a))).toJavaList();
    }

    public static <A, B> List<B> bind(List<A> list, F<A, List<B>> f) {
        return flatMap(list, f);
    }

}
