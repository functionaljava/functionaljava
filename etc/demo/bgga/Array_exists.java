import fj.data.Array;
import static fj.data.Array.array;
import static fj.data.List.fromString;
import static java.lang.Character.isLowerCase;

public final class Array_exists {
  public static void main(final String[] args) {
    final Array<String> a = array("Hello", "There", "what", "DAY", "iS", "iT");
    final boolean b = a.exists({String s => fromString(s).forall({char c => isLowerCase(c)})});
    System.out.println(b); // true ("what" provides the only example; try removing it)
  }
}
