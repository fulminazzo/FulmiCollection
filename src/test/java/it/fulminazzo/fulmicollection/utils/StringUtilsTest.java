package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilsTest {

    @Test
    void testGetFileName() {
        String path = "test/directory/file.yml";
        assertEquals("file.yml", StringUtils.getFileName(path));
    }

    @Test
    void testDecapitalize() {
        String string = "TEST STRING";
        String expected = "TEST_STRING";
        assertEquals(expected, StringUtils.decapitalize(StringUtils.capitalize(string)));
    }

    @ParameterizedTest
    @CsvSource({"test string, Test String",
            "test String, Test String",
            "TEST STRING, Test String",
            "TeST sTriNG, Test String",
            "test_STRING, Test_String",
            "test-string, Test-String",
            "test\tstring, Test\tString",
    })
    void testCapitalizeParameters(String string, String expected) {
        assertEquals(expected, StringUtils.capitalize(string));
    }

    @Test
    void testCapitalizeNewLine() {
        assertEquals("Test\nString", StringUtils.capitalize("tEST\nsTRING"));
    }

    @Test
    void testCapitalizeCarriageReturn() {
        assertEquals("Test\rString", StringUtils.capitalize("tEST\rsTRING"));
    }

    @Test
    void testCapitalizeNull() {
        assertNull(StringUtils.capitalize(null));
    }

    @ParameterizedTest
    @CsvSource({"A, AAAA", "A, AAAAAAAAAAAAAAAAAAAA",})
    void testRepeat(String string, String expected) {
        assertEquals(expected, StringUtils.repeat(string, expected.length()));
    }

    @Test
    void testRepeatNull() {
        assertNull(StringUtils.repeat(null, 10));
    }

    @Test
    void testRepeatEmpty() {
        assertEquals("", StringUtils.repeat("", 10));
    }

    @Test
    void testRepeatNegativeTimes() {
        assertEquals("", StringUtils.repeat("A", -10));
    }

    @Test
    void testRepeatZero() {
        assertEquals("", StringUtils.repeat("A", 0));
    }
}