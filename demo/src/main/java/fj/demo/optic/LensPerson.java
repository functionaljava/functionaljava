package fj.demo.optic;

import fj.Equal;
import fj.data.optic.Lens;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by MarkPerry on 23/06/2015.
 */
public class LensPerson {

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

    static Lens<Person, String> personNameLens = Lens.lens(p -> p.name, s -> p -> new Person(s, p.address));
    static Lens<Person, Address> personAddressLens = Lens.lens(p -> p.address, a -> p -> new Person(p.name, a));
    static Lens<Address, Integer> addressNumberLens = Lens.lens(a -> a.number, n -> a -> new Address(n, a.street));
    static Lens<Address, String> addressStreetLens = Lens.lens(a -> a.street, s -> a -> new Address(a.number, s));
    static Lens<Person, Integer> personNumberLens = personAddressLens.composeLens(addressNumberLens);
    static Lens<Person, String> personStreetLens = personAddressLens.composeLens(addressStreetLens);

    static Equal<Address> addressEqual = Equal.equal(a1 -> a2 -> a1.number == a2.number && a1.street.equals(a2.street));
    static Equal<Person> personEqual = Equal.equal(p1 -> p2 -> p1.name.equals(p2.name) && addressEqual.eq(p1.address, p2.address));

    static final String oldName = "Joe";
    static final int oldNumber = 10;
    static final String oldStreet = "Main St";
    static final Address oldAddress = new Address(oldNumber, oldStreet);
    static final Person oldPerson = new Person(oldName, oldAddress);

    @Test
    public final void get() {
        assertTrue(personNameLens.get(oldPerson).equals(oldName));
        assertTrue(personNumberLens.get(oldPerson) == oldNumber);
        assertTrue(personStreetLens.get(oldPerson) == oldStreet);
    }

    @Test
    public final void setName() {
        String newName = "Bill";
        Person p = personNameLens.set(newName).f(oldPerson);
        assertTrue(p.name.equals(newName));
        assertTrue(addressEqual.eq(p.address, oldPerson.address));
    }

    @Test
    public final void setNumber() {
        int newNumber = 20;
        Person p = personNumberLens.set(newNumber).f(oldPerson);
        assertTrue(p.name.equals(oldName));
        assertTrue(p.address.number == newNumber);
        assertTrue(p.address.street.equals(oldStreet));
    }

    @Test
    public final void setStreet() {
        String newStreet = "First St";
        Person p = personStreetLens.set(newStreet).f(oldPerson);
        assertTrue(p.name.equals(oldName));
        assertTrue(p.address.number == oldPerson.address.number);
        assertTrue(p.address.street.equals(newStreet));
    }

}
