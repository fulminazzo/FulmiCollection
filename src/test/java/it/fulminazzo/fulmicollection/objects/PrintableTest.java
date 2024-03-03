package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrintableTest {

    @Test
    void printJson() {
        assertEquals("{\"name\": \"Leonel\", \"age\": 10, \"partner\": {\"name\": \"Alex\", \"age\": 11, \"partner\": null}}",
                Printable.convertToJson(new Person("Leonel", 10, new Person("Alex", 11, null))));
    }

    @Test
    void printNull() {
        assertNull(Printable.printObject(null, null));
    }

    @Test
    void printWithNullHeadStart() {
        assertEquals("Person {\n  name: Leonel\n  age: 10\n  partner: null\n}",
                Printable.printObject(new Person("Leonel", 10, null), null));
    }

    @Test
    void printPerson() {
        assertEquals("Person {\n  name: Leonel\n  age: 10\n  partner: null\n}",
                new Person("Leonel", 10, null).toString());
    }

    @Test
    void printPersonWithPartner() {
        assertEquals("Person {\n  name: Leonel\n  age: 10\n  " +
                        "partner: Person {\n    name: Alex\n    age: 11\n    partner: null\n  }\n}",
                new Person("Leonel", 10, new Person("Alex", 11, null)).toString());
    }

    public static class Person extends Printable {
        final String name;
        final int age;
        final Person partner;

        public Person(String name, int age, Person partner) {
            this.name = name;
            this.age = age;
            this.partner = partner;
        }
    }
}