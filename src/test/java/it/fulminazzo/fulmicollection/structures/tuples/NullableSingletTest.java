package it.fulminazzo.fulmicollection.structures.tuples;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullableSingletTest {

    @Test
    void testIsEmpty() {
        assertFalse(new NullableSinglet<>(null).isEmpty());
    }

    @Test
    void testIsPresent() {
        assertTrue(new NullableSinglet<>("Hello").isPresent());
    }

    @Test
    void testMap() {
        assertEquals(1, new NullableSinglet<>("1")
                .map(i -> new NullableSinglet<>(Integer.valueOf(i)))
                .getValue());
    }

    @Test
    void testToString() {
        String string = new NullableSinglet<>(1).toString();
        assertFalse(string.contains("present"), "toString should not contain 'present' but was: " + string);
    }
}