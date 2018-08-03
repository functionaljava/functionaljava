package fj.data.optic;

import fj.F;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class LensTest {
    @Test
    public void testLensPersonGet() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, String> personNameLens = Lens.lens(p -> p.name, s -> p -> new Person(s, p.address));
        final Lens<Person, Address> personAddressLens = Lens.lens(p -> p.address, a -> p -> new Person(p.name, a));
        final Lens<Address, Integer> addressNumberLens = Lens.lens(a -> a.number, n -> a -> new Address(n, a.street));
        final Lens<Address, String> addressStreetLens = Lens.lens(a -> a.street, s -> a -> new Address(a.number, s));
        final Lens<Person, Integer> personNumberLens = personAddressLens.composeLens(addressNumberLens);
        final Lens<Person, String> personStreetLens = personAddressLens.composeLens(addressStreetLens);
        assertThat(personNameLens.get(oldPerson), is(oldName));
        assertThat(personNumberLens.get(oldPerson), is(oldNumber));
        assertThat(personStreetLens.get(oldPerson), is(oldStreet));
    }

    @Test
    public void testLensPersonSetName() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, String> personNameLens = Lens.lens(p -> p.name, s -> p -> new Person(s, p.address));
        String newName = "Bill";
        Person p = personNameLens.set(newName).f(oldPerson);
        assertThat(p.name, is(newName));
        assertThat(p.address, is(oldPerson.address));
    }

    @Test
    public void testLensPersonSetNumber() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, Address> personAddressLens = Lens.lens(p -> p.address, a -> p -> new Person(p.name, a));
        final Lens<Address, Integer> addressNumberLens = Lens.lens(a -> a.number, n -> a -> new Address(n, a.street));
        final Lens<Person, Integer> personNumberLens = personAddressLens.composeLens(addressNumberLens);
        int newNumber = 20;
        Person p = personNumberLens.set(newNumber).f(oldPerson);
        assertThat(p.name, is(oldName));
        assertThat(p.address.number, is(newNumber));
        assertThat(p.address.street, is(oldStreet));
    }

    @Test
    public void testLensPersonSetStreet() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, Address> personAddressLens = Lens.lens(p -> p.address, a -> p -> new Person(p.name, a));
        final Lens<Address, Integer> addressNumberLens = Lens.lens(a -> a.number, n -> a -> new Address(n, a.street));
        final Lens<Address, String> addressStreetLens = Lens.lens(a -> a.street, s -> a -> new Address(a.number, s));
        final Lens<Person, String> personStreetLens = personAddressLens.composeLens(addressStreetLens);
        String newStreet = "First St";
        Person p = personStreetLens.set(newStreet).f(oldPerson);
        assertThat(p.name, is(oldName));
        assertThat(p.address.number, is(oldPerson.address.number));
        assertThat(p.address.street, is(newStreet));
    }

    @Test
    public void testLensPersonSetter() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, String> personNameLens = Lens.lens(p -> p.name, s -> p -> new Person(s, p.address));
        String newName = "Bill";
        F<Person, Person> setter = personNameLens.asSetter().set(newName);
        Person p = setter.f(oldPerson);
        assertThat(p.name, is(newName));
        assertThat(p.address, is(oldPerson.address));
    }

    @Test
    public void testLensPersonGetter() {
        final String oldName = "Joe";
        final int oldNumber = 10;
        final String oldStreet = "Main St";
        final Address oldAddress = new Address(oldNumber, oldStreet);
        final Person oldPerson = new Person(oldName, oldAddress);
        final Lens<Person, String> personNameLens = Lens.lens(p -> p.name, s -> p -> new Person(s, p.address));
        assertThat(personNameLens.asGetter().get(oldPerson), is(oldName));
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
