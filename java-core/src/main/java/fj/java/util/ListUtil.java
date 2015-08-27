package fj.java.util;

import fj.F;
import java.util.List;

/**
 * Created by MarkPerry on 28/08/2015.
 */
public class ListUtil {

    public static <A, B> List<B> map(List<A> list, F<A, B> f) {
        return fj.data.List.list(list).map(f).toJavaList();
    }
}
