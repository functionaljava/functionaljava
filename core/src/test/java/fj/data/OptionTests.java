package fj.data;

import fj.F;
import org.junit.Assert;
import org.junit.Test;

import static fj.data.List.list;
import static fj.data.Option.none;
import static fj.data.Option.some;

/**
 * Created by amar on 28/12/14.
 */
public class OptionTests {

    @Test
    public void ShouldTraverseListWithGivenFunction(){
        List<String> strings = list("some1", "some2", "some3", "not_some", "  ");
        F<String, Option<String>> f = s -> {
            if(s.startsWith("some"))
                return some(s);
            else
                return Option.none();
        };

        Option<List<String>> optStr = Option.traverse(strings, f);
        Assert.assertEquals("optStr should be none", Option.none(), optStr);
    }

    @Test
    public void ShouldTraverseListWithGivenFunction2(){
        List<String> strings = list("some1", "some2", "some3");
        F<String, Option<String>> f = s -> {
            if(s.startsWith("some"))
                return some(s);
            else
                return Option.none();
        };

        Option<List<String>> optStr = Option.traverse(strings, f);
        Assert.assertEquals("optStr should be some", optStr.isSome(), true);
        Assert.assertArrayEquals(optStr.some().toArray().array(), new String[]{"some1", "some2", "some3"});
    }

    @Test
    public void ShouldSequenceListOfOptionalContexts(){
        List<Option<String>> strings = list(some("some1"), some("some2"), some("some3"));


        Option<List<String>> optStr = Option.sequence(strings);
        Assert.assertEquals("should be some", optStr.isSome(), true);
        Assert.assertArrayEquals(optStr.some().toArray().array(), new String[]{"some1", "some2", "some3"});
    }

    @Test
    public void ShouldSequenceListOfOptionalContexts2(){
        List<Option<String>> strings = list(some("some1"), some("some2"), none());


        Option<List<String>> optStr = Option.sequence(strings);
        Assert.assertEquals("should be some", optStr.isNone(), true);
    }
}
