package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ReflTest {
    private TestClass testClass;
    private Refl<?> refl;

    @BeforeEach
    void setUp() {
        this.testClass = new TestClass("Hello world");
        this.refl = new Refl<>(this.testClass);
    }

    @Test
    void testEquality() {
        assertEquals(this.refl, new Refl<>(this.testClass));
    }

    @Test
    void testInEquality() {
        assertNotEquals(this.refl, new Refl<>(new Object()));
    }

    @Test
    void testToString() {
        assertEquals(this.testClass.toString(), this.refl.toString());
    }

    static class TestClass {
        final String name;

        TestClass(String name) {
            this.name = name;
        }

        public String printField(String greeting) {
            return greeting + name;
        }
    }
}