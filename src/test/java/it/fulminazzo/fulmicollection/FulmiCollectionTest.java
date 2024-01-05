package it.fulminazzo.fulmicollection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FulmiCollectionTest {
    private final PrintStream standardOutput = System.out;
    private ByteArrayOutputStream tempStandardOutput;

    @BeforeEach
    void setUp() {
        tempStandardOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(tempStandardOutput));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOutput);
        try {
            tempStandardOutput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testMain() {
        FulmiCollection.main(new String[0]);
        assertEquals("Welcome to FulmiCollection!\n" +
                "\n" +
                "This is a library created by Fulminazzo containing various useful utility methods and classes.\n" +
                "You should import this library in your main project using Maven or by importing this jar file.\n",
                tempStandardOutput.toString());
    }
}