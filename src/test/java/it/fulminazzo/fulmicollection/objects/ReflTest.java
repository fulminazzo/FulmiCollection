package it.fulminazzo.fulmicollection.objects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ReflTest extends AbstractReflTest {

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

}