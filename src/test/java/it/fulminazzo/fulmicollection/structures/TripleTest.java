package it.fulminazzo.fulmicollection.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TripleTest {

    @Test
    void testToString() {
        String first = "Hello";
        String second = "World";
        String third = "Friend?";
        String toString = new Triple<>(first, second, third).toString();
        assertTrue(toString.contains(first), "toString() should contain first");
        assertTrue(toString.contains(second), "toString() should contain second");
        assertTrue(toString.contains(third), "toString() should contain third");
    }

    @Test
    void testIsEmpty() {
        Triple<String, String, Integer> tuple = new Triple<>();
        assertTrue(tuple.isEmpty(), "Triple should be empty when initialized empty");
        tuple.set("Hello", "world", 10);
        assertFalse(tuple.isEmpty(), "Triple should not be empty after set");
    }

    @Test
    void testFirstMethods() {
        String expected = "Hello";
        Triple<String, String, Integer> tuple = new Triple<>();
        assertFalse(tuple.hasFirst(), "Triple should not have first when initialized empty");
        assertFalse(tuple.containsFirst(expected), String.format("Triple should not contain first '%s'", expected));
        tuple.setFirst(expected);
        assertTrue(tuple.hasFirst(), "Triple should have first after set");
        assertTrue(tuple.containsFirst(expected), String.format("Triple should contain first '%s'", expected));
    }

    @Test
    void testSecondMethods() {
        String expected = "World";
        Triple<String, String, Integer> tuple = new Triple<>();
        assertFalse(tuple.hasSecond(), "Triple should not have second when initialized empty");
        assertFalse(tuple.containsSecond(expected), String.format("Triple should not contain second '%s'", expected));
        tuple.setSecond(expected);
        assertTrue(tuple.hasSecond(), "Triple should have second after set");
        assertTrue(tuple.containsSecond(expected), String.format("Triple should contain second '%s'", expected));
    }

    @Test
    void testThirdMethods() {
        int expected = 10;
        Triple<String, String, Integer> tuple = new Triple<>();
        assertFalse(tuple.hasThird(), "Triple should not have third when initialized empty");
        assertFalse(tuple.containsThird(expected), String.format("Triple should not contain third '%s'", expected));
        tuple.setThird(expected);
        assertTrue(tuple.hasThird(), "Triple should have third after set");
        assertTrue(tuple.containsThird(expected), String.format("Triple should contain third '%s'", expected));
    }

    @Test
    void testEquality() {
        Triple<String, String, Integer> t1 = new Triple<>("Hello", "world", 1);
        Triple<String, String, Integer> t2 = new Triple<>("Hello", "world", 1);
        assertEquals(t1, t2);
    }

    @Test
    void testInequality() {
        Triple<String, String, Integer> t1 = new Triple<>("Hello", "world", 1);
        Triple<String, String, Integer> t2 = new Triple<>("Hello", "world", 2);
        Triple<String, String, Integer> t3 = new Triple<>("Hello", "world!", 1);
        assertNotEquals(t1, t2);
        assertNotEquals(t1, t3);
    }

    @Test
    void testCopy() {
        Triple<String, String, Integer> t1 = new Triple<>("Hello", "world", 1);
        Triple<String, String, Integer> t2 = t1.copy();
        assertEquals(t1, t2);
    }
}