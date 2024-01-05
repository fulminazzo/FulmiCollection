package it.fulminazzo.fulmicollection.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassCannotBeNullExceptionTest {

    @Test
    void testClassNameContainedInExceptionMessage() {
        String className = "java/lang/String";
        ClassCannotBeNullException exception = new ClassCannotBeNullException(className);
        assertTrue(exception.getMessage().contains(className));
    }
}