package it.fulminazzo.fulmicollection.utils;

import org.junit.jupiter.api.Test;

import java.lang.management.MemoryType;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilsTest {

    @Test
    void testMemoryTypeValue() {
        assertEquals(MemoryType.HEAP, EnumUtils.valueOf(MemoryType.class, "HEAP"));
    }

    @Test
    void testNonMemoryTypeValue() {
        assertNull(EnumUtils.valueOf(MemoryType.class, "NOT_VALID_VALUE"));
    }

    @Test
    void testValuesNames() {
        assertEquals(Arrays.asList("Heap", "Non_Heap"), EnumUtils.returnValuesAsNames(MemoryType.class));
    }
}