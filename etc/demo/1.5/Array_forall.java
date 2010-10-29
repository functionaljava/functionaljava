import fj.F;
import fj.data.Array;
import static fj.data.Array.array;
import static fj.data.List.fromString;
import static fj.function.Characters.isLowerCase;

public final class Array_forall {
  public static void main(final String[] args) {
    final Array<String> a = array("hello", "There", "what", "day", "is", "it");
    final boolean b = a.forall(new F<String, Boolean>() {
      public Boolean f(final String s) {
        return fromString(s).forall(isLowerCase);
      }
    });
    System.out.println(b); // false ("There" is a counter-example; try removing it)
  }
}
