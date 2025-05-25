package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FieldEquableTest {

    @Test
    void testIgnoreField() {
        MockClass m1 = new MockClass();
        m1.name = "Alex";
        m1.id = UUID.randomUUID();
        m1.age = 10;
        m1.toIgnore = true;
        MockClass m2 = new MockClass();
        m2.name = m1.name;
        m2.id = m1.id;
        m2.age = m1.age;
        m2.toIgnore = false;
        assertEquals(m1.name, m2.name);
    }

    @Test
    void testEquality() {
        MockClass m1 = new MockClass();
        m1.name = "Alex";
        m1.id = UUID.randomUUID();
        m1.age = 10;
        MockClass m2 = new MockClass();
        m2.name = m1.name;
        m2.id = m1.id;
        m2.age = m1.age;
        assertEquals(m1.hashCode(), m2.hashCode());
        assertEquals(m1, m2);
    }

    @Test
    void testInequality() {
        MockClass m1 = new MockClass();
        m1.name = "Alex";
        m1.id = UUID.randomUUID();
        m1.age = 10;
        MockClass m2 = new MockClass();
        m2.name = "Steve";
        m2.id = m1.id;
        m2.age = m1.age;
        assertNotEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m2);
    }

    @Test
    void testPartialEquality() {
        MockSuperClass m1 = new MockSuperClass();
        m1.name = "Alex";
        m1.id = UUID.randomUUID();
        MockClass m2 = new MockClass();
        m2.name = m1.name;
        m2.id = m1.id;
        assertNotEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m2);
    }

    static class MockSuperClass extends FieldEquable {
        String name;
        UUID id;
    }

    static class MockClass extends MockSuperClass {
        String surname;
        int age;

        @IgnoreField
        boolean toIgnore;
    }
}