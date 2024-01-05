package it.fulminazzo.fulmicollection.utils;

import it.fulminazzo.fulmicollection.objects.PrintableTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.NotSerializableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class SerializeUtilsTest {
    private String testString;
    private String testBase64String;

    @BeforeEach
    void setUp() {
        testString = "Hello world!";
        testBase64String = "rO0ABXQADEhlbGxvIHdvcmxkIQ==";
    }

    @Test
    void testSerializeToBase64() {
        assertEquals(testBase64String, SerializeUtils.serializeToBase64(testString));
    }

    @Test
    void testSerializeToBase64Unserializable() {
        assertThrowsExactly(NotSerializableException.class, () -> {
            try {
                SerializeUtils.serializeToBase64(new PrintableTest.Person("Alex", 10, null));
            } catch (RuntimeException e) {
                throw e.getCause();
            }
        });
    }

    @Test
    void testDeserializeFromBase64() {
        assertEquals(testString, SerializeUtils.deserializeFromBase64(testBase64String));
    }

}