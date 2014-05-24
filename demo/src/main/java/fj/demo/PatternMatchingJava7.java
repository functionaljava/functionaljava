package fj.demo;

import fj.*;
import fj.data.Case;
import fj.data.List;
import fj.data.Stream;

import static fj.P.p;
import static fj.Show.*;
import static fj.data.Case.*;
import static fj.data.List.list;
import static fj.data.Stream.fromString;
import static fj.data.Stream.join;

public class PatternMatchingJava7 {
    public static void main(String[] args) {
        List<Leaf> leafs = makeOrderedLeafList(list(p('x', 8), p('y', 3)));
        leafs.foreach(new Effect<Leaf>() {
            @Override
            public void e(Leaf leaf) {
                leafShow.println(leaf);
            }
        });
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
            return match(codeTree, new F<Unit, List<Case<CodeTree, Stream<Character>>>>() {
                @Override
                public List<Case<CodeTree, Stream<Character>>> f(Unit unit) {
                    return List.<Case<CodeTree, Stream<Character>>>list(
                            when(Fork.class, new F<Fork, Stream<Character>>() {
                                @Override
                                public Stream<Character> f(Fork fork) {
                                    return forkShow.show(fork);
                                }
                            }),
                            when(Leaf.class, new F<Leaf, Stream<Character>>() {
                                @Override
                                public Stream<Character> f(Leaf leaf) {
                                    return leafShow.show(leaf);
                                }
                            })
                    );
                }
            });
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
        return match(tree, new F<Unit, List<Case<CodeTree, List<Character>>>>() {
            @Override
            public List<Case<CodeTree, List<Character>>> f(Unit unit) {
                return List.<Case<CodeTree, List<Character>>>list(
                        when(Fork.class, new F<Fork, Object>() {
                            @Override
                            public List<Character> f(Fork fork) {
                                return chars(fork.left).append(chars(fork.right));
                            }
                        }),
                        when(Leaf.class, new F<Leaf, List<Character>>() {
                            @Override
                            public List<Character> f(Leaf leaf) {
                                return List.<Character>list(leaf.character);
                            }
                        })
                );
            }
        });
    }

    public static CodeTree makeCodeTree(CodeTree left, CodeTree right) {
        return new Fork(left, right, chars(left).append(chars(right)), weight(left) + weight(right));
    }

    public static List<Leaf> makeOrderedLeafList(List<P2<Character, Integer>> freqs) {
        return match(freqs, new F<Unit, List<Case<List<P2<Character, Integer>>, List<Leaf>>>>() {
            @Override
            public List<Case<List<P2<Character, Integer>>, List<Leaf>>> f(Unit unit) {
                return List.<Case<List<P2<Character, Integer>>, List<Leaf>>>list(
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
                );
            }
        });
    }

    public static Integer weight(CodeTree tree) {
        return match(tree, new F<Unit, List<Case<CodeTree, Integer>>>() {
            @Override
            public List<Case<CodeTree, Integer>> f(Unit unit) {
                return List.<Case<CodeTree, Integer>>list(
                        when(Fork.class, new F<Fork, Integer>() {
                            @Override
                            public Integer f(Fork fork) {
                                return weight(fork.left) + weight(fork.right);
                            }
                        }),
                        when(Leaf.class, new F<Leaf, Integer>() {
                            @Override
                            public Integer f(Leaf leaf) {
                                return leaf.weight;
                            }
                        })
                );
            }
        });
    }
}
