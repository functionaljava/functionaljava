import fj.F;
import fj.data.Array;
import static fj.data.Array.array;
import static fj.data.List.fromString;
import static fj.function.Characters.isLowerCase;

public final class Array_exists {
  public static void main(final String[] args) {
    final Array<String> a = array("Hello", "There", "what", "DAY", "iS", "iT");
    final boolean b = a.exists(new F<String, Boolean>() {
      public Boolean f(final String s) {
        return fromString(s).forall(isLowerCase);
      }
    });
    System.out.println(b); // true ("what" provides the only example; try removing it)
  }
}
