package fj.demo;

import fj.*;
import fj.data.Case;
import fj.data.List;
import fj.data.Stream;

import static fj.P.p;
import static fj.Show.charShow;
import static fj.Show.intShow;
import static fj.data.Case.*;
import static fj.data.List.cons;
import static fj.data.List.list;
import static fj.data.Stream.fromString;
import static fj.data.Stream.join;

public class PatternMatchingJava8 {
    public static void main(String[] args) {
        List<Leaf> leafs = makeOrderedLeafList(list(p('x', 8), p('y', 3)));
        leafs.foreach(leafShow::println);
        CodeTree codeTree = makeCodeTree(leafs.head(), leafs.tail().head());
        codeTreeShow.println(codeTree);
    }

    static abstract class CodeTree {

    }

    static final class Fork extends CodeTree {
        public final CodeTree left;
        public final CodeTree right;
        public final List<Character> chars;
        public final Integer weight;

        public Fork(CodeTree left, CodeTree right, List<Character> chars, Integer weight) {
            this.left = left;
            this.right = right;
            this.chars = chars;
            this.weight = weight;
        }
    }

    static final class Leaf extends CodeTree {
        public final Character character;
        public final Integer weight;

        public Leaf(Character character, Integer weight) {
            this.character = character;
            this.weight = weight;
        }
    }

    public static final Show<CodeTree> codeTreeShow = Show.show(new F<CodeTree, Stream<Character>>() {
        @Override
        public Stream<Character> f(CodeTree codeTree) {
            return match(codeTree, unit -> list(
                    when(Fork.class, fork -> forkShow.show(fork)),
                    when(Leaf.class, leaf -> leafShow.show(leaf))
            ));
        }
    });
    public static final Show<Leaf> leafShow = Show.show(new F<Leaf, Stream<Character>>() {
        @Override
        public Stream<Character> f(Leaf leaf) {
            return Stream.fromString("Leaf('").
                    append(charShow.show(leaf.character)).
                    append(Stream.fromString("', ")).
                    append(intShow.show(leaf.weight)).
                    append(Stream.fromString(")"));
        }
    });
    public static final Show<Fork> forkShow = Show.show(new F<Fork, Stream<Character>>() {
        @Override
        public Stream<Character> f(Fork fork) {
            return Stream.fromString("Fork(").
                    append(codeTreeShow.show(fork.left)).
                    append(Stream.fromString(", ")).
                    append(codeTreeShow.show(fork.right)).
                    append(Stream.fromString(", ")).
                    append(join(fork.chars.
                            toStream().
                            map(charShow.show_()).
                            intersperse(fromString("', '")).
                            cons(fromString("List('")).
                            snoc(p(fromString("')"))))).
                    append(Stream.fromString(", ")).
                    append(intShow.show(fork.weight)).
                    append(Stream.fromString(")"));
        }
    });

    public static List<Character> chars(CodeTree tree) {
        return (List<Character>) match(tree, unit -> List.<Case<CodeTree, List<Character>>>list(
                when(Fork.class, fork -> chars(fork.left).append(chars(fork.right))),
                when(Leaf.class, leaf -> List.<Character>list(leaf.character))
        ));
    }

    public static CodeTree makeCodeTree(CodeTree left, CodeTree right) {
        return new Fork(left, right, chars(left).append(chars(right)), weight(left) + weight(right));
    }

    public static List<Leaf> makeOrderedLeafList(List<P2<Character, Integer>> freqs) {
        return (List<Leaf>) match(freqs, unit -> List.<Case<List<P2<Character, Integer>>, List<Leaf>>>list(
                when((F<List, Boolean>) List::isEmpty, ys -> List.<Leaf>nil()),
                otherwise((List<P2<Character, Integer>> ys) ->
                        cons(new Leaf(ys.head()._1(), ys.head()._2()), makeOrderedLeafList(ys.tail())))
        ));
    }

    public static Integer weight(CodeTree tree) {
        return (Integer) match(tree, unit -> List.<Case<CodeTree, Integer>>list(
                when(Fork.class, fork -> weight(fork.left) + weight(fork.right)),
                when(Leaf.class, leaf -> leaf.weight)
        ));
    }
}
