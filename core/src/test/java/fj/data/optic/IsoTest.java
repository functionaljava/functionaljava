package fj.data.optic;

import fj.P;
import fj.P2;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IsoTest {
    @Test
    public void testIso() {
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Iso<Address, P2<Integer, String>> addressIso = Iso.iso(p -> P.p(p.number, p.street),
                p -> new Address(p._1(), p._2()));
        final Address a = addressIso.reverseGet(addressIso.get(oldAddress));
        assertThat(a.number, is(oldAddress.number));
        assertThat(a.street, is(oldAddress.street));
    }

    static final class Person {
        String name;
        Address address;

        Person(String name, Address address) {
            this.name = name;
            this.address = address;
        }
    }

    static final class Address {
        int number;
        String street;

        public Address(int number, String street) {
            this.number = number;
            this.street = street;
        }
    }

}
