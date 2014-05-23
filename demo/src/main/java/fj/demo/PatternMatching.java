package fj.demo;

import fj.F;
import fj.P2;
import fj.Show;
import fj.Unit;
import fj.data.List;
import fj.data.Stream;

import static fj.P.p;
import static fj.data.Case.*;
import static fj.data.List.list;
import static fj.demo.PatternMatching.Leaf.show;

public class PatternMatching {
    static final class Leaf {
        private final Character character;
        private final Integer weight;

        public Leaf(Character character, Integer weight) {
            this.character = character;
            this.weight = weight;
        }

        /*
            Java 7 syntax
         */
        public static Show<Leaf> show() {
            return Show.show(new F<Leaf, Stream<Character>>() {
                @Override
                public Stream<Character> f(Leaf leaf) {
                    return Stream.fromString("Leaf('" + leaf.character + "', " + leaf.weight + ")");
                }
            });
        }
/*
        Java 8 syntax
        public static Show<Leaf> show() {
            return Show.show(leaf -> Stream.fromString("Leaf('" + leaf.character + "', " + leaf.weight + ")"));
         }
*/
    }


/*
    Java 8 syntax

    public static void main(String[] args) {
        List<Leaf> leafs = makeOrderedLeafList(list(p('A', 8), p('B', 3)));
        leafs.foreach(leaf -> show().println(leaf));
    }

    public static List<Leaf> makeOrderedLeafList(List<P2<Character, Integer>> freqs) {
        return match(freqs, unit -> list(
                when((List xs) -> xs.isEmpty(), ys ->
                        List.<Leaf>nil()),
                otherwise((List<P2<Character, Integer>> ys) ->
                        cons(new Leaf(ys.head()._1(), ys.head()._2()), makeOrderedLeafList(ys.tail())))
        ));
    }
*/
    /*
        Java 7 syntax
     */
    public static void main(String[] args) {
        List<Leaf> leafs = makeOrderedLeafList(list(p('A', 8), p('B', 3)));
        leafs.foreach(new F<Leaf, Unit>() {
            @Override
            public Unit f(Leaf leaf) {
                show().println(leaf);
                return null;
            }
        });
    }

    public static List<Leaf> makeOrderedLeafList(List<P2<Character, Integer>> freqs) {
        return match(freqs, unit -> list(
                when(new F<List, Boolean>() {
                    @Override
                    public Boolean f(List xs) {
                        return xs.isEmpty();
                    }
                }, new F<List, List<Leaf>>() {
                    @Override
                    public List<Leaf> f(List ys) {
                        return List.<Leaf>nil();
                    }
                }),
                otherwise(new F<List<P2<Character, Integer>>, List<Leaf>>() {
                    @Override
                    public List<Leaf> f(List<P2<Character, Integer>> ys) {
                        return List.<Leaf>cons(new Leaf(ys.head()._1(), ys.head()._2()), makeOrderedLeafList(ys.tail()));
                    }
                })
        ));
    }

}
