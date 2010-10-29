import fj.data.Array;
import static fj.data.Array.array;
import static fj.data.List.fromString;
import static java.lang.Character.isLowerCase;

public final class Array_forall {
  public static void main(final String[] args) {
    final Array<String> a = array("hello", "There", "what", "day", "is", "it");
    final boolean b = a.forall({String s => fromString(s).forall({char c => isLowerCase(c)})});
    System.out.println(b); // false ("There" is a counter-example; try removing it)
  }
}
