package fj.demo.test;

import static fj.Function.curry;
import fj.test.Gen;
import static fj.test.Arbitrary.arbByte;
import static fj.test.Arbitrary.arbCharacter;
import static fj.test.Bool.bool;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.property;

/*
Given the equals and hashCode implementation of MyClass, then check that if two
instances (m1 and m2) are equal, then the two hash codes are also equal.

m1.equals(m2).implies(m1.hashCode() == m2.hashCode())

Note this requires a custom Arbitrary to generate MyClass instances that are
more likely to be equal, since otherwise, you'd be waiting a long time for the
generator to produce values where this premise holds true. This is achieved by
using a restrictive arbitrary for bytes and Strings (the components of MyClass).
These restrictive arbitrary implementations produces a (very small) subset of
the possible byte and String values to assist in providing a true premise (that
m1 equals m2).
*/
public final class EqualsHashCode {
  public static final class MyClass {
    private final byte b;
    private final String s;

    MyClass(final byte b, final String s) {
      this.b = b;
      this.s = s;
    }

    public byte b() { return b; }
    public String s() { return s; }

    public boolean equals(final Object o) {
      return o != null &&
          o.getClass() == MyClass.class &&
          b == ((MyClass)o).b &&
          s.equals(((MyClass)o).s);
    }

    public int hashCode() {
      final int p = 419;
      int result = 239;
      result = p * result + b;
      result = p * result + s.hashCode();
      return result;
    }
  }

  public static void main(final String[] args) {
    // Restrictive arbitrary for Byte, produces from three possible values.
    final Gen<Byte> arbByteR = arbByte.map(b -> (byte)(b % 3));

    // Restrictive arbitrary for String, produces from twelve (2 * 3 * 2) possible values.
    final Gen<String> arbStringR = arbCharacter.bind(arbCharacter, arbCharacter, curry((c1, c2, c3) -> new String(new char[]{(char)(c1 % 2 + 'a'), (char)(c2 % 3 + 'a'), (char)(c3 % 2 + 'a')})));

    // Arbitrary for MyClass that uses the restrictive arbitraries above.
    // We are using the monad pattern (bind) to make this a trivial exercise. 
    final Gen<MyClass> arbMyClass = arbByteR.bind(arbStringR, curry(MyClass::new));

    // Finally the property.
    // if m1 equals m2, then this implies that m1's hashCode is equal to m2's hashCode.
    final Property p = property(arbMyClass, arbMyClass, (m1, m2) -> bool(m1.equals(m2)).implies(m1.hashCode() == m2.hashCode()));
    // at least 100 from 10,000 should satisfy the premise (m1.equals(m2))  
    summary.println(p.check(100, 10000, 0, 100)); // OK, passed 100 tests (4776 discarded).
  }
}
