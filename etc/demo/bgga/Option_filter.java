import fj.data.Option;
import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.Show.intShow;
import static fj.Show.optionShow;

public final class Option_filter {
  public static void main(final String[] args) {
    final Option<Integer> o1 = some(7);
    final Option<Integer> o2 = none();
    final Option<Integer> o3 = some(8);
    final Option<Integer> p1 = o1.filter({int i => i % 2 == 0});
    final Option<Integer> p2 = o2.filter({int i => i % 2 == 0});
    final Option<Integer> p3 = o3.filter({int i => i % 2 == 0});
    optionShow(intShow).println(p1); // None
    optionShow(intShow).println(p2); // None
    optionShow(intShow).println(p3); // Some(8)
  }
}
