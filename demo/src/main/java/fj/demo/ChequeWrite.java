package fj.demo;

import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.function.Characters;
import fj.function.Effect1;

import static fj.Equal.charEqual;
import static fj.Equal.listEqual;
import static fj.P.p;
import static fj.Show.stringShow;
import static fj.data.List.asString;
import static fj.data.List.fromString;
import static fj.data.List.list;
import static fj.data.List.single;
import static fj.data.List.unfold;
import static fj.data.Option.some;
import static fj.data.Stream.stream;

public final class ChequeWrite {
  private ChequeWrite() {}

  static List<Integer> toZero(final int from) {
    return unfold(i -> i < 0 ? Option.none() : some(p(i, i - 1)), from);
  }

  static int signum(final int i) {
    return i == 0 ? 0 : i < 0 ? -1 : 1;
  }

  static List<Character> show(final char c) {
    return stringShow
        .show(list("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine").index(c - '0'))
        .toList();
  }

  static List<Character> show(final List<Character> cs) {
    if (cs.isEmpty())
      return List.nil();
    else {
      final char d1 = cs.head();
      final List<Character> d1r = cs.tail();

      if (d1r.isEmpty())
        return show(d1);
      else {
        final char d2 = d1r.head();
        final List<Character> d2r = d1r.tail();

        return d2r.isEmpty()
               ? d1 == '0'
                 ? show(d2)
                 : d1 == '1'
                   ? stringShow.showl(
                     list("ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
                          "eighteen", "nineteen").index(d2 - '0'))
                   : stringShow.showl(
                       list("twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety").index(
                           d1 - '0' - 2))
                       .append(d2 == '0' ? List.nil() : show(d2).cons('-'))
               : d1 == '0' && d2 == '0' && d2r.head() == '0'
                 ? List.nil()
                 : d1 == '0'
                   ? show(list(d2, d2r.head()))
                   : d2 == '0' && d2r.head() == '0'
                     ? show(d1).append(stringShow.showl(" hundred"))
                     : show(d1).append(stringShow.showl(" hundred and ")).append(show(list(d2, d2r.head())));
      }
    }
  }

  static <A> List<P2<List<A>, Integer>> split(final List<A> as) {
    final int len = as.length();

    final List<List<A>> ds = as.zip(toZero(len - 1)).foldRight((ki, z) ->
        ki._2() % 3 == 0 ? z.conss(single(ki._1())) : z.tail().conss(z.head().cons(ki._1())), List.nil()
    );
    return ds.zip(toZero(len / 3 + signum(len % 3) - 1));
  }

  static List<Character> illion(final int i) {
    return stringShow.show(
        stream("thousand", "million", "billion", "trillion", "quadrillion", "quintillion", "sextillion", "septillion",
               "octillion", "nonillion", "decillion", "undecillion", "duodecillion", "tredecillion",
               "quattuordecillion", "quindecillion", "sexdecillion", "septendecillion", "octodecillion",
               "novemdecillion", "vigintillion")
            .append(Stream.repeat("<unsupported ?illion>")).index(i)).toList();
  }

  static boolean and(final List<Character> cs) {
    return cs.length() == 3 && cs.head() == '0' && (cs.tail().head() != '0' || cs.tail().tail().head() != '0');
  }

  static boolean existsNotZero(final List<Character> cs) {
    return cs.exists(c -> c != '0');
  }

  static boolean eq(final List<Character> a, final List<Character> b) {
    return listEqual(charEqual).eq(a, b);
  }

  static final F<List<Character>, List<Character>> dollars = cs -> {
      if (cs.isEmpty())
        return fromString("zero dollars");
      else {
        final List.Buffer<List<Character>> x = new List.Buffer<>();

        final List<P2<List<Character>, Integer>> k = split(cs);
        final int c = k.head()._2();

        k.foreachDoEffect(z -> {
            final List<Character> w = z._1();
            final int i = z._2();

            if (i == 0 && c > 0 && and(w))
                x.snoc(fromString("and"));

            if (existsNotZero(w)) {
                x.snoc(show(w));
                if (i != 0)
                    x.snoc(illion(i - 1));
            }
        });

        x.snoc(fromString(eq(cs, list('1')) ? "dollar" : "dollars"));

        return fromString(" ").intercalate(x.toList());
      }

  };

  static final F<List<Character>, List<Character>> cents = a -> {
      final int n = a.length();
      return n == 0
             ? fromString("zero cents")
             : show(list(a.head(), n == 1 ? '0' : a.tail().head()))
                 .append(fromString(eq(a, list('0', '1')) ? " cent" : " cents"));
  };

  public static List<Character> write(final List<Character> cs) {
    final F<List<Character>, List<Character>> dropNonDigit = cs2 -> cs2.filter(Characters.isDigit);
    final P2<List<Character>, List<Character>> x =
        cs.dropWhile(charEqual.eq('0')).breakk(charEqual.eq('.')).map1(dropNonDigit).map1(dollars).map2(dropNonDigit)
            .map2(List.<Character>take().f(2)).map2(cents);
    return x._1().append(fromString(" and ")).append(x._2());
  }

  public static void main(final String[] args) {
    if (args.length == 0)
      tests();
    else
      for (final String a : args)
        System.out.println(asString(write(fromString(a))));
  }

  @SuppressWarnings("unchecked")
  public static void tests() {
    // show
    for (final P2<String, String> t : list(
        p("1", "one"),
        p("10", "ten"),
        p("15", "fifteen"),
        p("40", "forty"),
        p("45", "forty-five"))) {
      assert eq(show(fromString(t._1())), fromString(t._2()));
    }

    // write
    for (final P2<String, String> t : list(
        p("0", "zero dollars and zero cents"),
        p("1", "one dollar and zero cents"),
        p("1.", "one dollar and zero cents"),
        p("0.", "zero dollars and zero cents"),
        p("0.0", "zero dollars and zero cents"),
        p("1.0", "one dollar and zero cents"),
        p("a1a", "one dollar and zero cents"),
        p("a1a.a0.7b", "one dollar and seven cents"),
        p("100", "one hundred dollars and zero cents"),
        p("100.45", "one hundred dollars and forty-five cents"),
        p("100.07", "one hundred dollars and seven cents"),
        p("9abc9def9ghi.jkl9mno", "nine hundred and ninety-nine dollars and ninety cents"),
        p("12345.67", "twelve thousand three hundred and forty-five dollars and sixty-seven cents"),
        p("456789123456789012345678901234567890123456789012345678901234567890.12",
          "four hundred and fifty-six vigintillion seven hundred and eighty-nine novemdecillion one hundred and twenty-three octodecillion four hundred and fifty-six septendecillion seven hundred and eighty-nine sexdecillion twelve quindecillion three hundred and forty-five quattuordecillion six hundred and seventy-eight tredecillion nine hundred and one duodecillion two hundred and thirty-four undecillion five hundred and sixty-seven decillion eight hundred and ninety nonillion one hundred and twenty-three octillion four hundred and fifty-six septillion seven hundred and eighty-nine sextillion twelve quintillion three hundred and forty-five quadrillion six hundred and seventy-eight trillion nine hundred and one billion two hundred and thirty-four million five hundred and sixty-seven thousand eight hundred and ninety dollars and twelve cents"))) {
      assert eq(write(fromString(t._1())), fromString(t._2()));
    }
  }
}