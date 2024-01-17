package it.fulminazzo.fulmicollection.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameCannotBeNullExceptionTest {

    @Test
    void testVariableNameContainedInExceptionMessage() {
        String className = "importantString";
        NameCannotBeNullException exception = new NameCannotBeNullException(className);
        assertTrue(exception.getMessage().contains(className));
    }
}